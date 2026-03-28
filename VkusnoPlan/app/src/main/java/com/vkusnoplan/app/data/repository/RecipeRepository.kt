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

        val system = """Ты — профессиональный кулинарный ИИ-помощник. 
Отвечай ТОЛЬКО валидным JSON без markdown-оформления, без пояснений, без обёртки в ```json```."""

        val user = """У пользователя есть продукты: ${ingredients.joinToString(", ")}.
Диетические предпочтения: $prefsText.
Тип еды: $mealType.

Предложи ровно 6 рецептов. Максимально используй имеющиеся ингредиенты. 
Допускается 1–3 недостающих ингредиента.

Верни JSON-массив из 6 объектов строго в таком формате:
[
  {
    "id": 1,
    "title": "Название рецепта",
    "emoji": "🍳",
    "time": "25 мин",
    "servings": 2,
    "difficulty": "Легко",
    "matchPercent": 90,
    "haveIngredients": ["яйца", "помидоры"],
    "missingIngredients": ["базилик"],
    "steps": ["Шаг 1...", "Шаг 2...", "Шаг 3...", "Шаг 4..."],
    "tip": "Совет шеф-повара",
    "calories": 320,
    "protein": 18,
    "fat": 12,
    "carbs": 30
  }
]"""

        val response = AnthropicClient.api.sendMessage(
            AnthropicRequest(
                system = system,
                messages = listOf(AnthropicMessage(role = "user", content = user))
            )
        )

        val raw = response.content
            .mapNotNull { it.text }
            .joinToString("")
            .replace(Regex("```json|```"), "")
            .trim()

        val type = object : TypeToken<List<Recipe>>() {}.type
        gson.fromJson<List<Recipe>>(raw, type)
    }
}
