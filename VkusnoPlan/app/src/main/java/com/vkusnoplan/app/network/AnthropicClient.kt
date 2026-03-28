package com.vkusnoplan.app.network

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// ── Request / Response DTOs ───────────────────────────────────────────────

data class AnthropicRequest(
    val model: String = "claude-sonnet-4-20250514",
    @SerializedName("max_tokens") val maxTokens: Int = 2000,
    val system: String,
    val messages: List<AnthropicMessage>
)

data class AnthropicMessage(
    val role: String,
    val content: String
)

data class AnthropicResponse(
    val content: List<ContentBlock>
)

data class ContentBlock(
    val type: String,
    val text: String?
)

// ── Retrofit Interface ────────────────────────────────────────────────────

interface AnthropicApi {
    @Headers(
        "Content-Type: application/json",
        "anthropic-version: 2023-06-01"
    )
    @POST("v1/messages")
    suspend fun sendMessage(@Body request: AnthropicRequest): AnthropicResponse
}

// ── Singleton Client ──────────────────────────────────────────────────────

object AnthropicClient {
    private const val BASE_URL = "https://api.anthropic.com/"

    // ⚠️  Replace with your actual API key before building
    const val API_KEY = "YOUR_ANTHROPIC_API_KEY"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-api-key", API_KEY)
                .build()
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    val api: AnthropicApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AnthropicApi::class.java)
}
