package com.vkusnoplan.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vkusnoplan.app.MainUiState
import com.vkusnoplan.app.data.model.ALL_PREFERENCES
import com.vkusnoplan.app.data.model.MEAL_TYPES
import com.vkusnoplan.app.ui.components.IngredientChip
import com.vkusnoplan.app.ui.components.MatchBar
import com.vkusnoplan.app.ui.components.RecipeCard
import com.vkusnoplan.app.ui.components.SectionTitle
import com.vkusnoplan.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    uiState: MainUiState,
    onAddIngredient: (String) -> Unit,
    onRemoveIngredient: (String) -> Unit,
    onTogglePref: (String) -> Unit,
    onSetMealType: (Int) -> Unit,
    onGenerate: () -> Unit,
    onOpenRecipe: (Int) -> Unit,
) {
    var inputText by remember { mutableStateOf("") }
    val keyboard = LocalSoftwareKeyboardController.current

    fun submit() {
        if (inputText.isNotBlank()) {
            onAddIngredient(inputText)
            inputText = ""
            keyboard?.hide()
        }
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🌿", fontSize = 20.sp)
                        Text("Вкусно", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Cream)
                        Text("План", fontSize = 20.sp, fontStyle = FontStyle.Italic, color = Sand)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Ink)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Ingredients ──
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = WarmWhite),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, LightSand)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        SectionTitle("🧺", "Мои ингредиенты")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                placeholder = { Text("Добавить продукт…", color = Muted, fontSize = 14.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Sage,
                                    unfocusedBorderColor = LightSand,
                                    focusedTextColor = Ink,
                                    unfocusedTextColor = Ink,
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { submit() }),
                            )
                            FilledIconButton(
                                onClick = { submit() },
                                modifier = Modifier.size(52.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Moss)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Добавить", tint = Color.White)
                            }
                        }
                        // Ingredient chips — simple wrap layout
                        if (uiState.ingredients.isNotEmpty()) {
                            ChipWrap(uiState.ingredients) { ing ->
                                IngredientChip(ing) { onRemoveIngredient(ing) }
                            }
                        }
                    }
                }
            }

            // ── Preferences ──
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = WarmWhite),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, LightSand)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SectionTitle("🥗", "Диетические предпочтения")
                        ALL_PREFERENCES.chunked(2).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                row.forEach { pref ->
                                    PrefToggle(
                                        emoji = pref.emoji,
                                        label = pref.label,
                                        selected = pref.id in uiState.selectedPrefs,
                                        onClick = { onTogglePref(pref.id) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (row.size < 2) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // ── Meal Type ──
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = WarmWhite),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, LightSand)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SectionTitle("🍽️", "Тип приёма пищи")
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            MEAL_TYPES.forEachIndexed { i, label ->
                                SegmentedButton(
                                    selected = i == uiState.mealTypeIndex,
                                    onClick = { onSetMealType(i) },
                                    shape = SegmentedButtonDefaults.itemShape(i, MEAL_TYPES.size),
                                    colors = SegmentedButtonDefaults.colors(
                                        activeContainerColor = Terracotta,
                                        activeContentColor = Color.White,
                                        inactiveContainerColor = Cream,
                                        inactiveContentColor = Muted,
                                    ),
                                    label = { Text(label, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            }

            // ── Generate ──
            item {
                Button(
                    onClick = onGenerate,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Terracotta,
                        disabledContainerColor = Muted
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(10.dp))
                        Text("Ищем рецепты…", fontSize = 15.sp)
                    } else {
                        Text("✨  Найти рецепты", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // ── Error ──
            if (uiState.error != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MissingOrange)
                            .border(1.5.dp, Color(0xFFFADDCE), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(uiState.error, fontSize = 14.sp, color = Terracotta)
                    }
                }
            }

            // ── Welcome ──
            if (!uiState.hasSearched && !uiState.isLoading) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🍳", fontSize = 56.sp)
                        Text("Что приготовим сегодня?", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Bark)
                        Text(
                            "Добавьте продукты из холодильника,\nвыберите предпочтения и нажмите «Найти рецепты»",
                            fontSize = 14.sp, color = Muted, lineHeight = 20.sp, textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // ── Recipes header ──
            if (uiState.recipes.isNotEmpty()) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Рецепты для вас", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Ink)
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(TagBg).padding(horizontal = 10.dp, vertical = 2.dp)
                        ) { Text("${uiState.recipes.size}", fontSize = 12.sp, color = Muted) }
                    }
                }
            }

            // ── Recipe Cards ──
            items(uiState.recipes, key = { it.id }) { recipe ->
                RecipeCard(recipe = recipe, onClick = { onOpenRecipe(recipe.id) })
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

// Простой wrap без экспериментального FlowRow
@Composable
private fun ChipWrap(items: List<String>, content: @Composable (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { item -> content(item) }
            }
        }
    }
}

@Composable
fun PrefToggle(emoji: String, label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bg = if (selected) Moss else Cream
    val textColor = if (selected) Color.White else Muted
    val borderColor = if (selected) Moss else LightSand
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 13.sp)
            Spacer(Modifier.width(4.dp))
            Text(label, fontSize = 11.sp, color = textColor, fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal)
        }
    }
}
