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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GraphActivity : AppCompatActivity() {

    private lateinit var chart: LineChart
    private val repository = ThingSpeakRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        chart = findViewById(R.id.lineChart)

        // 🔥 Remove "No chart data available"
        chart.setNoDataText("")

        // Optional: make background clean
        chart.setNoDataTextColor(android.graphics.Color.TRANSPARENT)

        fetchData()
    }

    private fun fetchData() {
        lifecycleScope.launch {
            try {
                val feeds = withContext(Dispatchers.IO) {
                    repository.getSensorData()
                }

                showGraph(feeds)
                analyzeData(feeds)

            } catch (e: Exception) {
                Toast.makeText(this@GraphActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showGraph(feeds: List<Feed>) {

        if (feeds.isEmpty()) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show()
            return
        }

        val limitedFeeds = feeds.takeLast(10)

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

        if (phEntries.isEmpty() && tdsEntries.isEmpty() &&
            tempEntries.isEmpty() && turbidityEntries.isEmpty()
        ) {
            Toast.makeText(this, "No valid data", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ BETTER LABELS WITH UNITS
        val phDataSet = LineDataSet(phEntries, "pH (Acidity)")
        val tdsDataSet = LineDataSet(tdsEntries, "TDS (ppm)")
        val tempDataSet = LineDataSet(tempEntries, "Temperature (°C)")
        val turbidityDataSet = LineDataSet(turbidityEntries, "Turbidity (NTU)")

        // ✅ Styling
        listOf(phDataSet, tdsDataSet, tempDataSet, turbidityDataSet).forEach {
            it.lineWidth = 2f
            it.setDrawCircles(false)
            it.setDrawValues(false)
        }

        // ✅ COLORS (important for legend)
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

        // ✅ REMOVE DEFAULT DESCRIPTION
        chart.description.isEnabled = false

        // ✅ ENABLE LEGEND (THIS WAS MISSING)
        chart.legend.isEnabled = true
        chart.legend.textColor = android.graphics.Color.WHITE
        chart.legend.textSize = 12f

        // ✅ AXIS VISIBILITY (VERY IMPORTANT FOR UI)
        chart.xAxis.textColor = android.graphics.Color.WHITE
        chart.axisLeft.textColor = android.graphics.Color.WHITE
        chart.axisRight.isEnabled = false

        chart.invalidate()
    }

    private fun analyzeData(feeds: List<Feed>) {

        val values = feeds.mapNotNull {
            it.field1?.toFloatOrNull()
        }

        if (values.isEmpty()) return

        val avg = values.average()
        val max = values.maxOrNull()
        val min = values.minOrNull()

        findViewById<TextView>(R.id.tvAvg).text = "Avg: %.2f".format(avg)
        findViewById<TextView>(R.id.tvMax).text = "Max: ${max ?: "--"}"
        findViewById<TextView>(R.id.tvMin).text = "Min: ${min ?: "--"}"

        // 🔥 Better threshold logic (pH safe range example)
        if (avg < 6.5 || avg > 8.5) {
            Toast.makeText(this, "⚠️ Unsafe pH level!", Toast.LENGTH_LONG).show()
        }
    }
}