package com.example.canteen.ui.theme.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.canteen.databinding.ActivityDashboardBinding
import com.example.canteen.ui.theme.api.RetrofitClient
import com.example.canteen.ui.theme.viewmodels.WaterViewModel
import kotlinx.coroutines.launch
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.example.canteen.ui.theme.model.Feed


data class SensorData(
    val ph: Float,
    val tds: Float,
    val temp: Float,
    val turbidity: Float,
    val timestamp: String
)

data class Prediction(
    val drinking: String,
    val washing: String,
    val irrigation: String,
    val unsafe: String
)

class DashboardActivity : AppCompatActivity() {
    private lateinit var chart: LineChart
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var viewModel: WaterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Simple layout (like before)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Chart setup
        chart = binding.lineChart
        chart.setNoDataText("")
        chart.setNoDataTextColor(android.graphics.Color.TRANSPARENT)

        // ✅ ViewModel
        viewModel = WaterViewModel(application)

        // ✅ Load data
        fetchLiveData()
        startAutoRefresh()
    }

    private fun fetchLiveData() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.thingSpeakApi.getFeeds(
                    channelId = "3342088",
                    apiKey = "A7M99B6I0LUM4T6M",
                    results = 10
                )

                val feeds = response.feeds

                if (feeds.isNotEmpty()) {

                    // ✅ Show graph safely
                    showGraph(feeds)

                    val latest = feeds.last()

                    // 🔹 Raw Strings
                    val phStr = latest.field1 ?: "0"
                    val tdsStr = latest.field2 ?: "0"
                    val tempStr = latest.field3 ?: "0"
                    val turbidityStr = latest.field4 ?: "0"

                    // 🔹 Convert to Float
                    val ph = phStr.toFloatOrNull() ?: 0f
                    val tds = tdsStr.toFloatOrNull() ?: 0f
                    val temp = tempStr.toFloatOrNull() ?: 0f
                    val turbidity = turbidityStr.toFloatOrNull() ?: 0f

                    val time = latest.created_at
                        ?.replace("T", " ")
                        ?.replace("Z", "") ?: "--"

                    // 🔹 Create Objects
                    val sensorData = SensorData(ph, tds, temp, turbidity, time)

                    val prediction = generatePrediction(ph, tds, turbidity)

                    // 🔹 Update UI
                    updateUI(sensorData, prediction)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@DashboardActivity,
                    "Error fetching data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateUI(data: SensorData, prediction: Prediction) {

        // 🔹 SENSOR CARDS
        binding.tvPH.text = "${data.ph}"
        binding.tvTDS.text = "${data.tds} ppm"
        binding.tvTemp.text = "${data.temp} °C"
        binding.tvTurbidity.text = "${data.turbidity} NTU"

        // 🔹 TIME
        binding.tvTime.text = "Updated: ${data.timestamp}"

        // 🔹 ML BOXES
        binding.tvDrink.text = prediction.drinking
        binding.tvWash.text = prediction.washing
        binding.tvIrrigation.text = prediction.irrigation
        binding.tvUnsafe.text = prediction.unsafe   // reuse this TextView

        // 🔹 OVERALL STATUS
        val status = if (prediction.drinking == "Safe") {
            "Water Quality: SAFE"
        } else {
            "Water Quality: UNSAFE"
        }

        binding.tvAlert.text = status
    }

    private fun generatePrediction(ph: Float, tds: Float, turbidity: Float): Prediction {

        val drinking = if (ph in 6.5..8.5 && tds < 300 && turbidity < 1)
            "Safe" else "Not Safe"

        val washing = if (ph in 6.0..9.0 && tds < 500 && turbidity < 5)
            "Suitable" else "Not Suitable"

        val irrigation = if (ph in 5.5..9.5 && tds < 2000)
            "Suitable" else "Not Suitable"

        // 🔥 NEW: overall unsafe condition
        val unsafe = if (
            ph !in 5.5..9.5 || tds > 2000 || turbidity > 5
        ) "Unsafe" else "OK"

        return Prediction(drinking, washing, irrigation, unsafe)
    }

    private fun startAutoRefresh() {
        val handler = Handler(Looper.getMainLooper())

        val runnable = object : Runnable {
            override fun run() {
                fetchLiveData()
                handler.postDelayed(this, 15000) // refresh every 15 sec
            }
        }

        handler.post(runnable)
    }

    private fun showGraph(feeds: List<Feed>) {

        if (feeds.isEmpty()) return

        val limitedFeeds = feeds.takeLast(10).filterNotNull()

        val phEntries = ArrayList<Entry>()
        val tdsEntries = ArrayList<Entry>()
        val tempEntries = ArrayList<Entry>()
        val turbidityEntries = ArrayList<Entry>()

        limitedFeeds.forEachIndexed { index, item ->

            item.field1?.toFloatOrNull()?.let {
                phEntries.add(Entry(index.toFloat(), it))
            }

            item.field2?.toFloatOrNull()?.let {
                tdsEntries.add(Entry(index.toFloat(), it))
            }

            item.field3?.toFloatOrNull()?.let {
                tempEntries.add(Entry(index.toFloat(), it))
            }

            item.field4?.toFloatOrNull()?.let {
                turbidityEntries.add(Entry(index.toFloat(), it))
            }
        }

        val phDataSet = LineDataSet(phEntries, "pH")
        val tdsDataSet = LineDataSet(tdsEntries, "TDS")
        val tempDataSet = LineDataSet(tempEntries, "Temp")
        val turbidityDataSet = LineDataSet(turbidityEntries, "Turbidity")

        listOf(phDataSet, tdsDataSet, tempDataSet, turbidityDataSet).forEach {
            it.lineWidth = 2f
            it.setDrawCircles(false)
            it.setDrawValues(false)
        }

        phDataSet.color = android.graphics.Color.BLUE
        tdsDataSet.color = android.graphics.Color.GREEN
        tempDataSet.color = android.graphics.Color.RED
        turbidityDataSet.color = android.graphics.Color.YELLOW

        val lineData = LineData(
            phDataSet,
            tdsDataSet,
            tempDataSet,
            turbidityDataSet
        )

        chart.clear()
        chart.data = lineData
        chart.description.isEnabled = false
        chart.legend.isEnabled = true

        chart.xAxis.textColor = android.graphics.Color.WHITE
        chart.axisLeft.textColor = android.graphics.Color.WHITE
        chart.axisRight.isEnabled = false

        chart.invalidate()
    }

}