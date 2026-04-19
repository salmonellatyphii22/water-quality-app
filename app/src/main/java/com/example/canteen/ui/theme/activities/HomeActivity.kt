package com.example.canteen.ui.theme.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.canteen.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Main Content Text
        val contentText = findViewById<TextView>(R.id.contentText)

        val content = """
🌊 HydroIQ – Smart Water Quality Monitoring

HydroIQ is an intelligent IoT-based water quality monitoring system designed to provide real-time insights into water conditions. By integrating advanced sensors such as pH, turbidity, TDS, and temperature, the system continuously tracks water quality parameters and delivers accurate data directly to your mobile application. This ensures users can monitor water safety anytime, anywhere, enabling quick decision-making and preventive action.

Water is one of the most essential resources for life, yet it is increasingly threatened by pollution and misuse. HydroIQ aims to bridge the gap between awareness and action by offering a user-friendly platform that visualizes water data in an easy-to-understand format. With features like live analytics, alerts, and historical tracking, users can identify contamination trends and take timely measures to maintain water purity.

The application also focuses on sustainability by promoting responsible water usage. By analyzing consumption patterns and detecting anomalies, HydroIQ helps reduce water wastage and encourages efficient resource management. Whether used in households, industries, or agricultural settings, HydroIQ empowers users to contribute towards a cleaner and more sustainable environment.

With seamless IoT integration and a scalable backend, HydroIQ is built to support future advancements in smart water management. It not only ensures safety but also educates users about the importance of preserving water resources for future generations.
        """.trimIndent()

        contentText.text = content

        // Link Clicks
        findViewById<TextView>(R.id.linkWHO).setOnClickListener {
            openLink("https://www.who.int/news-room/fact-sheets/detail/drinking-water")
        }

        findViewById<TextView>(R.id.linkUNESCO).setOnClickListener {
            openLink("https://www.unesco.org/en/water-security")
        }

        findViewById<TextView>(R.id.linkUNEP).setOnClickListener {
            openLink("https://www.unep.org/explore-topics/water")
        }

        findViewById<TextView>(R.id.linkWaterAid).setOnClickListener {
            openLink("https://www.wateraid.org")
        }
    }

    // Function to open links
    private fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}