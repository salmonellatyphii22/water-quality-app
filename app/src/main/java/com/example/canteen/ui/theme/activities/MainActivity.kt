package com.example.canteen.ui.theme.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.canteen.R

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_with_drawer)

        drawerLayout = findViewById(R.id.drawerLayout)
        menuIcon = findViewById(R.id.menuIcon)

        // ✅ Open drawer
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // 🔥 SET CLICK LISTENERS
        setupClick(R.id.nav_home, HomeActivity::class.java)
        setupClick(R.id.nav_activity, ActivityPage::class.java)
        setupClick(R.id.nav_dashboard, DashboardActivity::class.java)
        setupClick(R.id.nav_messages, MessagesActivity::class.java)
        setupClick(R.id.nav_settings, SettingsActivity::class.java)

        // 🔥 BELOW DIVIDER
        setupClick(R.id.nav_themes, ThemesActivity::class.java)
        setupClick(R.id.nav_tutorials, TutorialsActivity::class.java)
        setupClick(R.id.nav_integrations, IntegrationsActivity::class.java)
    }

    // ✅ REUSABLE FUNCTION (FIXED)
    private fun setupClick(viewId: Int, activity: Class<*>) {
        val view = findViewById<View>(viewId)   // 🔥 FIXED (View instead of LinearLayout)

        view?.setOnClickListener {
            startActivity(Intent(this, activity))
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }
}