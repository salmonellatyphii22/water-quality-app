package com.example.canteen.ui.theme.repository

import com.example.canteen.ui.theme.api.RetrofitClient
import com.example.canteen.ui.theme.model.Feed

class ThingSpeakRepository {

    suspend fun getSensorData(): List<Feed> {
        val response = RetrofitClient.thingSpeakApi.getFeeds(
            channelId = "3342088",
            apiKey = "A7M99B6I0LUM4T6M",
            10
        )
        return response.feeds
    }
}