package com.vkusnoplan.app.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vkusnoplan.app.data.model.Recipe
import com.vkusnoplan.app.network.AnthropicClient
import com.vkusnoplan.app.network.GroqMessage
import com.vkusnoplan.app.network.GroqRequest

class RecipeRepository {

    private val gson = Gson()

    suspend fun fetchRecipes(
        ingredients: List<String>,
        preferences: Set<String>,
        mealType: String
    ): Result<List<Recipe>> = runCatching {

        val prefsText = if (preferences.isEmpty()) "нет ограничений"
                        else preferences.joinToString(", ")

        val systemPrompt = "Ты кулинарный помощник. Отвечай ТОЛЬКО валидным JSON-массивом без markdown и пояснений."

        val userPrompt = """Продукты: ${ingredients.joinToString(", ")}.
Предпочтения: $prefsText. Тип: $mealType.
Предложи 6 рецептов. Верни JSON-массив без лишнего текста:
[{"id":1,"title":"Название","emoji":"🍳","time":"20 мин","servings":2,"difficulty":"Легко","matchPercent":90,"haveIngredients":["ингредиент"],"missingIngredients":[],"steps":["Шаг 1","Шаг 2","Шаг 3"],"tip":"Совет","calories":300,"protein":15,"fat":10,"carbs":30}]"""

        val response = AnthropicClient.groqApi.chat(
            GroqRequest(
                messages = listOf(
                    GroqMessage(role = "system", content = systemPrompt),
                    GroqMessage(role = "user", content = userPrompt)
                )
            )
        )

        if (!response.isSuccessful) {
            val err = response.errorBody()?.string() ?: "unknown"
            throw Exception("HTTP ${response.code()}: $err")
        }

        val body = response.body() ?: throw Exception("Пустой ответ")
        val raw = body.choices.firstOrNull()?.message?.content
            ?: throw Exception("Нет текста в ответе")

        val cleaned = raw.replace(Regex("```json|```"), "").trim()

        val type = object : TypeToken<List<Recipe>>() {}.type
        gson.fromJson<List<Recipe>>(cleaned, type)
            ?: throw Exception("Не удалось разобрать рецепты")
    }
}
