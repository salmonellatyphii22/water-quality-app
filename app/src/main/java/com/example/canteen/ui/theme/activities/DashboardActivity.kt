package com.example.canteen.ui.theme.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.canteen.databinding.ActivityDashboardBinding
import com.example.canteen.ui.theme.api.RetrofitClient
import com.example.canteen.ui.theme.model.Feed
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ FIX 1: Graph button navigation
        binding.btnGraph.setOnClickListener {
            startActivity(Intent(this, GraphActivity::class.java))
        }

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
                    val latest = feeds.last()

                    // ✅ Sensor values
                    val ph = latest.field1 ?: "--"
                    val tds = latest.field2 ?: "--"
                    val temp = latest.field3 ?: "--"
                    val turbidity = latest.field4 ?: "--"

                    binding.tvPH.text = "pH: $ph"
                    binding.tvTDS.text = "TDS: $tds"
                    binding.tvTemp.text = "Temp: $temp"
                    binding.tvTurbidity.text = "Turbidity: $turbidity"

                    // ✅ FIX 2: Timestamp (formatted)
                    val rawTime = latest.created_at
                    val formattedTime = if (rawTime != null) {
                        rawTime.replace("T", " ").replace("Z", "")
                    } else {
                        "--"
                    }
                    binding.tvTime.text = "Time: $formattedTime"

                    // ✅ Water Quality Check
                    val message = checkWaterQuality(latest)
                    binding.tvAlert.text = message

                    if (!message.contains("Safe")) {
                        Toast.makeText(this@DashboardActivity, message, Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@DashboardActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkWaterQuality(feed: Feed): String {

        val ph = feed.field1?.toFloatOrNull() ?: 0f
        val tds = feed.field2?.toFloatOrNull() ?: 0f
        val temp = feed.field3?.toFloatOrNull() ?: 0f
        val turbidity = feed.field4?.toFloatOrNull() ?: 0f

        return when {
            ph < 6 -> "⚠️ Low pH! Water is acidic"
            ph > 8.5 -> "⚠️ High pH! Water is alkaline"

            tds > 500 -> "⚠️ High TDS! Not safe"

            turbidity > 5 -> "⚠️ High Turbidity!"

            temp > 35 -> "⚠️ Temperature too high!"

            else -> "✅ Water is Safe"
        }
    }

    private fun startAutoRefresh() {
        val handler = Handler(Looper.getMainLooper())

        val runnable = object : Runnable {
            override fun run() {
                fetchLiveData()
                handler.postDelayed(this, 15000)
            }
        }

        handler.post(runnable)
    }
}