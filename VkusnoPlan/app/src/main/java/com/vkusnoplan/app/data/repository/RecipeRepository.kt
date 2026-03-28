package com.vkusnoplan.app.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vkusnoplan.app.data.model.Recipe
import com.vkusnoplan.app.network.AnthropicClient
import com.vkusnoplan.app.network.AnthropicMessage
import com.vkusnoplan.app.network.AnthropicRequest

class RecipeRepository {

    private val gson = Gson()

    suspend fun fetchRecipes(
        ingredients: List<String>,
        preferences: Set<String>,
        mealType: String
    ): Result<List<Recipe>> = runCatching {

        val prefsText = if (preferences.isEmpty()) "нет особых ограничений"
                        else preferences.joinToString(", ")

        val system = "Ты кулинарный помощник. Отвечай ТОЛЬКО валидным JSON-массивом без markdown, без пояснений."

        val user = """Продукты: ${ingredients.joinToString(", ")}.
Предпочтения: $prefsText. Тип: $mealType.
Предложи 6 рецептов. Верни JSON-массив:
[{"id":1,"title":"Название","emoji":"🍳","time":"20 мин","servings":2,"difficulty":"Легко","matchPercent":90,"haveIngredients":["ингредиент"],"missingIngredients":[],"steps":["Шаг 1","Шаг 2","Шаг 3"],"tip":"Совет","calories":300,"protein":15,"fat":10,"carbs":30}]"""

        val response = AnthropicClient.api.sendMessage(
            AnthropicRequest(
                system = system,
                messages = listOf(AnthropicMessage(role = "user", content = user))
            )
        )

        if (!response.isSuccessful) {
            val errBody = response.errorBody()?.string() ?: "unknown"
            throw Exception("HTTP ${response.code()}: $errBody")
        }

        val body = response.body() ?: throw Exception("Пустой ответ от сервера")

        val raw = body.content
            .mapNotNull { it.text }
            .joinToString("")
            .replace(Regex("```json|```"), "")
            .trim()

        val type = object : TypeToken<List<Recipe>>() {}.type
        gson.fromJson<List<Recipe>>(raw, type)
            ?: throw Exception("Не удалось разобрать рецепты")
    }
}
