package com.example.canteen.ui.theme.activities

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.canteen.R
import com.example.canteen.ui.theme.model.Feed
import com.example.canteen.ui.theme.repository.ThingSpeakRepository
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import kotlinx.coroutines.launch

class GraphActivity : AppCompatActivity() {

    private lateinit var chart: LineChart
    private val repository = ThingSpeakRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        chart = findViewById(R.id.lineChart)

        fetchData()
    }

    private fun fetchData() {
        lifecycleScope.launch {
            try {
                val feeds = repository.getSensorData()
                showGraph(feeds)
                analyzeData(feeds)
            } catch (e: Exception) {
                Toast.makeText(this@GraphActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showGraph(feeds: List<Feed>) {
        val entries = ArrayList<Entry>()

        for (i in feeds.indices) {
            val feed = feeds[i]
            val value = feed.field1?.toFloatOrNull() ?: 0f
            entries.add(Entry(i.toFloat(), value))
        }

        val dataSet = LineDataSet(entries, "Turbidity")
        val lineData = LineData(dataSet)

        chart.data = lineData
        chart.invalidate()
    }

    private fun analyzeData(feeds: List<Feed>) {

        val values = mutableListOf<Float>()

        for (feed in feeds) {
            feed.field1?.toFloatOrNull()?.let {
                values.add(it)
            }
        }

        if (values.isEmpty()) return

        val avg = values.average()
        val max = values.maxOrNull()
        val min = values.minOrNull()

        findViewById<TextView>(R.id.tvAvg).text =
            String.format("%.2f", avg).let { "Avg: $it" }

        findViewById<TextView>(R.id.tvMax).text = "Max: ${max ?: "--"}"
        findViewById<TextView>(R.id.tvMin).text = "Min: ${min ?: "--"}"

        if (avg > 50) {
            Toast.makeText(this, "⚠️ High Turbidity!", Toast.LENGTH_LONG).show()
        }
    }
}