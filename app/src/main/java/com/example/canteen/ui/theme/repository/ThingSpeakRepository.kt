package com.example.canteen.ui.theme.repository

import com.example.canteen.ui.theme.api.RetrofitClient
import com.example.canteen.ui.theme.model.Feed

class ThingSpeakRepository {

    suspend fun getSensorData(): List<Feed> {
        val response = RetrofitClient.api.getFeeds(
            channelId = "YOUR_CHANNEL_ID",
            apiKey = "YOUR_READ_API_KEY"
        )
        return response.feeds
    }
}