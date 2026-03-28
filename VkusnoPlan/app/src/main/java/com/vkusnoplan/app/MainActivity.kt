package com.vkusnoplan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.vkusnoplan.app.ui.screens.MainScreen
import com.vkusnoplan.app.ui.screens.RecipeDetailScreen
import com.vkusnoplan.app.ui.theme.VkusnoPlanTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VkusnoPlanTheme {
                val uiState by viewModel.uiState.collectAsState()

                AnimatedContent(
                    targetState = uiState.selectedRecipe,
                    transitionSpec = {
                        if (targetState != null) {
                            slideInVertically(tween(300)) { it } togetherWith
                                    fadeOut(tween(150))
                        } else {
                            fadeIn(tween(200)) togetherWith
                                    slideOutVertically(tween(300)) { it }
                        }
                    },
                    label = "screen"
                ) { recipe ->
                    if (recipe != null) {
                        RecipeDetailScreen(
                            recipe = recipe,
                            onClose = viewModel::closeRecipe
                        )
                    } else {
                        MainScreen(
                            uiState = uiState,
                            onAddIngredient  = viewModel::addIngredient,
                            onRemoveIngredient = viewModel::removeIngredient,
                            onTogglePref     = viewModel::togglePref,
                            onSetMealType    = viewModel::setMealType,
                            onGenerate       = viewModel::generateRecipes,
                            onOpenRecipe     = { id ->
                                uiState.recipes.find { it.id == id }?.let(viewModel::openRecipe)
                            }
                        )
                    }
                }
            }
        }
    }
}
