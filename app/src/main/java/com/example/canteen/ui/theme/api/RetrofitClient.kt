package com.example.canteen.ui.theme.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 🔹 ThingSpeak API
    val thingSpeakApi: ThingSpeakApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.thingspeak.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ThingSpeakApi::class.java)
    }
}