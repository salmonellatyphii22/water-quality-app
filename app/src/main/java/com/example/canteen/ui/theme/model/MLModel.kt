package com.example.canteen.ui.theme.model

import android.content.Context
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

    // ✅ UPDATED INPUT FORMAT
    fun predict(ph: Float, temp: Float, conductivity: Float): Float {

        // Input must match EXACT training order
        val input = arrayOf(floatArrayOf(ph, temp, conductivity))

        val output = Array(1) { FloatArray(1) }

        interpreter.run(input, output)

        return output[0][0]
    }
}