package com.example.canteen.ui.theme.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.canteen.databinding.ActivityHistoryBinding
import com.example.canteen.ui.theme.api.RetrofitClient
import com.example.canteen.ui.theme.adapters.ThingSpeakAdapter
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        fetchHistory()
    }

    private fun fetchHistory() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.thingSpeakApi.getFeeds(
                    channelId = "3342088",
                    apiKey = "A7M99B6I0LUM4T6M",
                    results = 50
                )

                val feeds = response.feeds.reversed()

                if (feeds.isEmpty()) {
                    Toast.makeText(this@HistoryActivity, "No data found", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // ✅ Directly use ThingSpeakAdapter
                binding.recyclerView.adapter = ThingSpeakAdapter(feeds)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@HistoryActivity, "Error loading history", Toast.LENGTH_SHORT).show()
            }
        }
    }
}