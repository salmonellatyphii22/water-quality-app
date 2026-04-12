package com.example.canteen.ui.theme.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.canteen.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Connect XML layout
        setContentView(R.layout.activity_settings)
    }
}