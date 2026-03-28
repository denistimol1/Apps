package com.vkusnoplan.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkusnoplan.app.data.model.MEAL_TYPES
import com.vkusnoplan.app.data.model.Recipe
import com.vkusnoplan.app.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val ingredients: List<String> = emptyList(),
    val selectedPrefs: Set<String> = emptySet(),
    val mealTypeIndex: Int = 0,
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedRecipe: Recipe? = null,
    val hasSearched: Boolean = false,
)

class MainViewModel : ViewModel() {

    private val repository = RecipeRepository()

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun addIngredient(text: String) {
        val parts = text.split(",", ";")
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() }
        val current = _uiState.value.ingredients
        val updated = (current + parts).distinct()
        _uiState.update { it.copy(ingredients = updated, error = null) }
    }

    fun removeIngredient(ing: String) {
        _uiState.update { it.copy(ingredients = it.ingredients - ing) }
    }

    fun togglePref(prefId: String) {
        _uiState.update { state ->
            val prefs = state.selectedPrefs.toMutableSet()
            if (prefs.contains(prefId)) prefs.remove(prefId) else prefs.add(prefId)
            state.copy(selectedPrefs = prefs)
        }
    }

    fun setMealType(index: Int) {
        _uiState.update { it.copy(mealTypeIndex = index) }
    }

    fun generateRecipes() {
        val state = _uiState.value
        if (state.ingredients.isEmpty()) {
            _uiState.update { it.copy(error = "Добавьте хотя бы один ингредиент 🧺") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, recipes = emptyList()) }
            repository.fetchRecipes(
                ingredients = state.ingredients,
                preferences = state.selectedPrefs,
                mealType = MEAL_TYPES[state.mealTypeIndex]
            ).fold(
                onSuccess = { recipes ->
                    _uiState.update { it.copy(recipes = recipes, isLoading = false, hasSearched = true) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Не удалось получить рецепты: ${e.message}"
                        )
                    }
                }
            )
        }
    }

    fun openRecipe(recipe: Recipe) = _uiState.update { it.copy(selectedRecipe = recipe) }
    fun closeRecipe() = _uiState.update { it.copy(selectedRecipe = null) }
    fun clearError() = _uiState.update { it.copy(error = null) }
}
