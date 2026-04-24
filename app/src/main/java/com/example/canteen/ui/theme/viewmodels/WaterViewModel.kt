package com.example.canteen.ui.theme.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.canteen.ui.theme.model.MLModel

class WaterViewModel(application: Application) : AndroidViewModel(application) {

    private val model = MLModel(application)

    // 🔥 1. RAW ML OUTPUT (VERY IMPORTANT)
    fun predictRaw(ph: Float, temp: Float, conductivity: Float): Float {
        return model.predict(ph, temp, conductivity)
    }

    // 🔥 2. USER-FRIENDLY OUTPUT
    fun predict(ph: Float, temp: Float, conductivity: Float): String {

        val result = model.predict(ph, temp, conductivity)

        return if (result > 0.5f) {
            "Water Quality: GOOD ✅"
        } else {
            "Water Quality: BAD ❌"
        }
    }
}