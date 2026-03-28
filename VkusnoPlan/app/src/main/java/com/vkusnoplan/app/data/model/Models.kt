package com.vkusnoplan.app.data.model

// ── UI / Domain Models ────────────────────────────────────────────────────

data class Recipe(
    val id: Int,
    val title: String,
    val emoji: String,
    val time: String,
    val servings: Int,
    val difficulty: String,
    val matchPercent: Int,
    val haveIngredients: List<String>,
    val missingIngredients: List<String>,
    val steps: List<String>,
    val tip: String,
    val calories: Int,
    val protein: Int,
    val fat: Int,
    val carbs: Int
)

data class DietPreference(
    val id: String,
    val label: String,
    val emoji: String
)

val ALL_PREFERENCES = listOf(
    DietPreference("vegetarian",  "Вегетарианство", "🌱"),
    DietPreference("vegan",       "Веганство",       "🐾"),
    DietPreference("gluten-free", "Без глютена",     "🌾"),
    DietPreference("dairy-free",  "Без молочки",     "🥛"),
    DietPreference("keto",        "Кето",            "🥑"),
    DietPreference("low-calorie", "Низкокалорийное", "⚡"),
)

val MEAL_TYPES = listOf(
    "Любой", "Завтрак", "Обед", "Ужин", "Перекус", "Десерт"
)
