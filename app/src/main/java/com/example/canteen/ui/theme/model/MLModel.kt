package com.example.canteen.ui.theme.model

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MLModel(context: Context) {

    private var interpreter: Interpreter

    init {
        val inputStream = context.assets.open("model.tflite")
        val modelBytes = inputStream.readBytes()

        val buffer = ByteBuffer.allocateDirect(modelBytes.size)
        buffer.order(ByteOrder.nativeOrder())
        buffer.put(modelBytes)

        interpreter = Interpreter(buffer)
    }

    fun predict(ph: Float, temp: Float, conductivity: Float): Float {

        try {
            // 🔥 NORMALIZE INPUT (VERY IMPORTANT)
            val normPH = ph / 14f
            val normTemp = temp / 100f
            val normCond = conductivity / 1000f

            // 🔥 USE FLOAT ARRAY (most compatible)
            val input = arrayOf(floatArrayOf(normPH, normTemp, normCond))

            val output = Array(1) { FloatArray(1) }

            interpreter.run(input, output)

            Log.d("ML_DEBUG", "Output = ${output[0][0]}")

            return output[0][0]

        } catch (e: Exception) {
            Log.e("ML_DEBUG", "ML Crash", e)
            return 0f // prevent crash
        }
    }
}