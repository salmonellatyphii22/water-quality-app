package com.example.canteen.ui.theme.activities

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.canteen.R
import com.google.android.material.navigation.NavigationView

class AlertActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Directly load alert UI
        setContentView(R.layout.activity_alert)

        val tdsStatus = findViewById<TextView>(R.id.tdsStatus)
        val tds = 80

        if (tds < 100) {
            tdsStatus.text = "⚠️ TDS is LOW!"
        } else {
            tdsStatus.text = "✅ TDS is Normal"
        }
    }
}