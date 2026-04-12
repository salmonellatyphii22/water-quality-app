package com.example.canteen.ui.theme.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.canteen.R

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Simple and correct
        setContentView(R.layout.activity_history)
    }
}