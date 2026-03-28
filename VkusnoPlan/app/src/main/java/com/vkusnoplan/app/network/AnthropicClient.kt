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

// ── Groq API DTOs ─────────────────────────────────────────────────────────

data class GroqRequest(
    val model: String = "llama-3.3-70b-versatile",
    val messages: List<GroqMessage>,
    @SerializedName("max_tokens") val maxTokens: Int = 2000,
    val temperature: Double = 0.7
)

data class GroqMessage(
    val role: String,   // "system" | "user"
    val content: String
)

data class GroqResponse(
    val choices: List<GroqChoice>
)

data class GroqChoice(
    val message: GroqMessage
)

// ── Retrofit Interface ────────────────────────────────────────────────────

interface GroqApi {
    @Headers("Content-Type: application/json")
    @POST("openai/v1/chat/completions")
    suspend fun chat(@Body request: GroqRequest): retrofit2.Response<GroqResponse>
}

// ── Singleton ─────────────────────────────────────────────────────────────

object AnthropicClient {
    private const val BASE_URL = "https://api.groq.com/"
    const val API_KEY = "YOUR_GROQ_API_KEY"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val req = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $API_KEY")
                .build()
            chain.proceed(req)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    val groqApi: GroqApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GroqApi::class.java)
}
