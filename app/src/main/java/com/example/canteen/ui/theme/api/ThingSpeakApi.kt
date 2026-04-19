package com.example.canteen.ui.theme.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.canteen.ui.theme.model.ThingSpeakResponse

interface ThingSpeakApi {

    @GET("channels/{channel_id}/feeds.json")
    suspend fun getFeeds(
        @Path("channel_id") channelId: String,
        @Query("api_key") apiKey: String,
        @Query("results") results: Int = 50
    ): ThingSpeakResponse
}