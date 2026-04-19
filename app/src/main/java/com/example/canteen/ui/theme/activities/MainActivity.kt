package com.example.canteen.ui.theme.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.canteen.R
import com.google.android.material.navigation.NavigationView
import android.widget.TextView
import com.example.canteen.ui.theme.activities.GraphActivity

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuIcon: ImageView
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_with_drawer)

        drawerLayout = findViewById(R.id.drawerLayout)
        menuIcon = findViewById(R.id.menuIcon)
        navigationView = findViewById(R.id.navigationView)

        // ✅ Open drawer
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // ✅ Load Home by default
        loadHomeScreen()

        // 🔥 MENU CLICK HANDLING (CORRECT WAY)
        navigationView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> loadHomeScreen()

                R.id.nav_activity ->
                    startActivity(Intent(this, ActivityPage::class.java))

                R.id.nav_dashboard ->
                    startActivity(Intent(this, DashboardActivity::class.java))

                R.id.nav_messages ->
                    startActivity(Intent(this, MessagesActivity::class.java))

                R.id.nav_settings ->
                    startActivity(Intent(this, SettingsActivity::class.java))

                R.id.nav_themes ->
                    startActivity(Intent(this, ThemesActivity::class.java))

                R.id.nav_tutorials ->
                    startActivity(Intent(this, TutorialsActivity::class.java))

                R.id.nav_integrations ->
                    startActivity(Intent(this, IntegrationsActivity::class.java))

                // 🔥 ADD THIS
                R.id.nav_graph ->
                    startActivity(Intent(this, GraphActivity::class.java))
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    // ✅ LOAD HOME UI
    private var isHomeLoaded = false

    private fun loadHomeScreen() {
        val frame = findViewById<FrameLayout>(R.id.contentArea)

        if (!isHomeLoaded) {
            layoutInflater.inflate(R.layout.home_content, frame, true)
            isHomeLoaded = true

            // ✅ Use TextView instead of View (FIXES YOUR ERROR)
            val who = frame.findViewById<TextView>(R.id.linkWHO)
            val unesco = frame.findViewById<TextView>(R.id.linkUNESCO)
            val unep = frame.findViewById<TextView>(R.id.linkUNEP)
            val waterAid = frame.findViewById<TextView>(R.id.linkWaterAid)

            val openLink: (String) -> Unit = { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }

            who.setOnClickListener {
                openLink("https://www.who.int/news-room/fact-sheets/detail/drinking-water")
            }

            unesco.setOnClickListener {
                openLink("https://www.unesco.org/en/water-security")
            }

            unep.setOnClickListener {
                openLink("https://www.unep.org/explore-topics/water")
            }

            waterAid.setOnClickListener {
                openLink("https://www.wateraid.org")
            }
        }
    }
}