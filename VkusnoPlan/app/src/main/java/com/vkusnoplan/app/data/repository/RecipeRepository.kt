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

        val systemPrompt = """Ты профессиональный шеф-повар и кулинарный автор. 
Отвечай ТОЛЬКО валидным JSON-массивом без markdown, без пояснений, без текста до или после массива."""

        val userPrompt = """Продукты пользователя: ${ingredients.joinToString(", ")}.
Предпочтения: $prefsText. Тип приёма пищи: $mealType.

Предложи 6 рецептов. Для каждого рецепта напиши ПОДРОБНЫЕ шаги приготовления:
- Каждый шаг должен быть конкретным действием с точными деталями
- Указывай точное время (например: "жарьте 3-4 минуты", "варите 8 минут")
- Указывай температуру где нужно (например: "разогрейте сковороду на среднем огне", "духовка 180°C")
- Указывай количества (например: "добавьте 1 ч.л. соли", "влейте 200 мл воды")
- Описывай визуальный результат (например: "до золотистой корочки", "пока лук не станет прозрачным")
- Минимум 5-7 шагов на каждый рецепт
- Каждый шаг — одно конкретное действие, 1-3 предложения

Верни JSON-массив строго в этом формате (без лишнего текста):
[{"id":1,"title":"Название блюда","emoji":"🍳","time":"25 мин","servings":2,"difficulty":"Легко","matchPercent":90,"haveIngredients":["ингредиент1","ингредиент2"],"missingIngredients":["ингредиент3"],"steps":["Нарежьте лук мелкими кубиками примерно 0.5 см. Чем мельче нарезка — тем равномернее приготовится.","Разогрейте сковороду на среднем огне и добавьте 1 ст.л. растительного масла. Масло должно слегка задымиться — это знак готовности.","Выложите лук на сковороду и жарьте 4-5 минут, помешивая каждую минуту, до прозрачности и лёгкой золотистости.","Добавьте к луку нарезанные томаты и убавьте огонь до минимума. Тушите 7-8 минут под крышкой до мягкости.","Разбейте яйца в отдельную миску, добавьте щепотку соли и взбейте вилкой 30 секунд до однородности.","Вылейте яичную смесь на овощи. Готовьте 3-4 минуты не перемешивая, пока белок не схватится, а желток останется слегка жидким.","Посыпьте свежей зеленью, снимите с огня и подавайте прямо в сковороде."],"tip":"Для более нежной текстуры накройте сковороду крышкой на последней минуте — яйца дойдут от пара.","calories":320,"protein":18,"fat":12,"carbs":30}]"""

        val response = AnthropicClient.groqApi.chat(
            GroqRequest(
                model = "llama-3.3-70b-versatile",
                maxTokens = 4000,
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

        val cleaned = raw
            .replace(Regex("```json\\s*"), "")
            .replace(Regex("```\\s*"), "")
            .trim()

        // Находим JSON-массив если вдруг есть лишний текст
        val startIdx = cleaned.indexOf('[')
        val endIdx = cleaned.lastIndexOf(']')
        val jsonOnly = if (startIdx >= 0 && endIdx > startIdx)
            cleaned.substring(startIdx, endIdx + 1)
        else cleaned

        val type = object : TypeToken<List<Recipe>>() {}.type
        gson.fromJson<List<Recipe>>(jsonOnly, type)
            ?: throw Exception("Не удалось разобрать рецепты")
    }
}
