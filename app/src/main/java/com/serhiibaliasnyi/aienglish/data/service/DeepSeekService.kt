package com.serhiibaliasnyi.aienglish.data.service

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DeepSeekService {
    @POST("v1/chat/completions")
    suspend fun generateText(
        @Header("Authorization") apiKey: String,
        @Body request: DeepSeekRequest
    ): DeepSeekResponse
} 