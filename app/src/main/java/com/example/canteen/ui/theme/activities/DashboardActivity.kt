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

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var viewModel: WaterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Initialize ML ViewModel
        viewModel = WaterViewModel(application)

        // ✅ Graph button
        binding.btnGraph.setOnClickListener {
            startActivity(Intent(this, GraphActivity::class.java))
        }

        fetchLiveData()
//        startAutoRefresh()
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

                    // ✅ Raw sensor values
                    val phStr = latest.field1 ?: "--"
                    val tdsStr = latest.field2 ?: "--"
                    val tempStr = latest.field3 ?: "--"
                    val turbidityStr = latest.field4 ?: "--"

                    // ✅ Show raw data
                    binding.tvPH.text = "pH: $phStr"
                    binding.tvTDS.text = "TDS: $tdsStr"
                    binding.tvTemp.text = "Temp: $tempStr"
                    binding.tvTurbidity.text = "Turbidity: $turbidityStr"

                    // ✅ Time
                    val formattedTime = latest.created_at
                        ?.replace("T", " ")
                        ?.replace("Z", "") ?: "--"
                    binding.tvTime.text = "Time: $formattedTime"

                    // ✅ Convert to Float (ML input)
                    val ph = phStr.toFloatOrNull() ?: 0f
                    val temp = tempStr.toFloatOrNull() ?: 0f
                    val conductivity = tdsStr.toFloatOrNull() ?: 0f

                    // 🔥 ML OUTPUT
                    val raw = viewModel.predictRaw(ph, temp, conductivity)

                    // 🔍 Debug
                    Log.d("ML_DEBUG", "RAW OUTPUT = $raw")

                    // ✅ FINAL UI RESULT
                    val resultText = if (raw > 0.5f) {
                        "Water Quality: GOOD ✅\nConfidence: ${(raw * 100).toInt()}%"
                    } else {
                        "Water Quality: BAD ❌\nConfidence: ${((1 - raw) * 100).toInt()}%"
                    }

                    // 🔥 SET RESULT
                    binding.tvAlert.text = resultText

                    // 🎨 Color based on result
                    val color = if (raw > 0.5f)
                        getColor(android.R.color.holo_green_light)
                    else
                        getColor(android.R.color.holo_red_light)

                    binding.tvAlert.setTextColor(color)

                    // ⚠️ Alert for unsafe water
                    if (raw <= 0.5f) {
                        Toast.makeText(
                            this@DashboardActivity,
                            "⚠️ Unsafe Water Detected!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
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
}