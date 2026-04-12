package com.example.canteen.ui.theme.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.canteen.R
import org.eclipse.paho.client.mqttv3.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import android.graphics.Color
import android.Manifest

class DashboardActivity : AppCompatActivity() {

    private lateinit var client: MqttClient
    private val serverURI = "tcp://broker.hivemq.com:1883"
    private var isConnected = false

    // 🔥 GRAPH
    private lateinit var chart: LineChart
    private val entries = ArrayList<Entry>()
    private var index = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvPH = findViewById<TextView>(R.id.tvPH)
        val tvTDS = findViewById<TextView>(R.id.tvTDS)
        val tvTemp = findViewById<TextView>(R.id.tvTemp)
        val tvTurbidity = findViewById<TextView>(R.id.tvTurbidity)
        val tvAlert = findViewById<TextView>(R.id.tvAlert)

        chart = findViewById(R.id.lineChart)

        // 🔥 Notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        connectMQTT(tvPH, tvTDS, tvTemp, tvTurbidity, tvAlert)
    }

    // 🔥 MQTT
    private fun connectMQTT(
        tvPH: TextView,
        tvTDS: TextView,
        tvTemp: TextView,
        tvTurbidity: TextView,
        tvAlert: TextView
    ) {
        Thread {
            while (!isConnected) {
                try {
                    client = MqttClient(serverURI, MqttClient.generateClientId(), null)

                    val options = MqttConnectOptions()
                    options.isCleanSession = true
                    options.isAutomaticReconnect = true

                    client.connect(options)
                    isConnected = true

                    client.subscribe("water/quality") { _, message ->
                        val data = message.toString()

                        runOnUiThread {
                            handleData(data, tvPH, tvTDS, tvTemp, tvTurbidity, tvAlert)
                        }
                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        tvAlert.text = "Reconnecting..."
                    }
                    Thread.sleep(3000)
                }
            }
        }.start()
    }

    // 🔥 HANDLE DATA
    private fun handleData(
        data: String,
        tvPH: TextView,
        tvTDS: TextView,
        tvTemp: TextView,
        tvTurbidity: TextView,
        tvAlert: TextView
    ) {
        val parts = data.split(",")

        if (parts.size == 4) {
            val ph = parts[0]
            val tds = parts[1]
            val temp = parts[2]
            val turbidity = parts[3]

            tvPH.text = "pH: $ph"
            tvTDS.text = "TDS: $tds ppm"
            tvTemp.text = "Temperature: $temp °C"
            tvTurbidity.text = "Turbidity: $turbidity NTU"

            // 📊 GRAPH
            val tdsFloat = tds.toFloatOrNull() ?: 0f
            updateGraph(tdsFloat)

            val isSafe = tds.toIntOrNull()?.let { it < 500 } ?: true

            if (isSafe) {
                tvAlert.text = "Status: SAFE"
                tvAlert.setTextColor(getColor(android.R.color.holo_green_light))
            } else {
                tvAlert.text = "Status: UNSAFE"
                tvAlert.setTextColor(getColor(android.R.color.holo_red_light))

                // 🚨 NOTIFICATION
                showNotification()
            }
        }
    }

    // 📊 GRAPH FUNCTION (OPTIMIZED)
    private fun updateGraph(value: Float) {
        if (entries.size > 50) entries.removeAt(0) // limit points

        entries.add(Entry(index++, value))

        val dataSet = LineDataSet(entries, "TDS")
        dataSet.color = Color.CYAN
        dataSet.valueTextColor = Color.WHITE
        dataSet.setDrawCircles(false)

        val data = LineData(dataSet)
        chart.data = data
        chart.invalidate()
    }

    // 🚨 NOTIFICATION
    private fun showNotification() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "water_alert"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Water Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("⚠ Water Unsafe!")
            .setContentText("TDS level is too high")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .build()

        manager.notify(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (::client.isInitialized && client.isConnected) {
                client.disconnect()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}