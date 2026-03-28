package com.vkusnoplan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vkusnoplan.app.data.model.Recipe
import com.vkusnoplan.app.ui.components.SectionTitle
import com.vkusnoplan.app.ui.theme.*

@Composable
fun RecipeDetailScreen(recipe: Recipe, onClose: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Cream)) {

        // Header
        Box(modifier = Modifier.fillMaxWidth().background(Ink).padding(20.dp)) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(recipe.emoji, fontSize = 44.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(recipe.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Cream, lineHeight = 26.sp)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("⏱ ${recipe.time}", fontSize = 11.sp, color = Muted)
                        Text("👤 ${recipe.servings} порц.", fontSize = 11.sp, color = Muted)
                        Text("📊 ${recipe.difficulty}", fontSize = 11.sp, color = Muted)
                        Text("🔥 ${recipe.calories} ккал", fontSize = 11.sp, color = Muted)
                    }
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(36.dp).background(Color.White.copy(alpha = .15f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Закрыть", tint = Cream)
                }
            }
        }

        // Body
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Ingredients
            Column {
                SectionTitle("🧾", "Ингредиенты")
                // Have
                if (recipe.haveIngredients.isNotEmpty()) {
                    recipe.haveIngredients.chunked(3).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                            row.forEach { ing ->
                                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(HaveGreen).padding(horizontal = 10.dp, vertical = 5.dp)) {
                                    Text("✓ $ing", fontSize = 13.sp, color = HaveGreenText)
                                }
                            }
                        }
                    }
                }
                // Missing
                if (recipe.missingIngredients.isNotEmpty()) {
                    recipe.missingIngredients.chunked(3).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                            row.forEach { ing ->
                                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(MissingOrange).padding(horizontal = 10.dp, vertical = 5.dp)) {
                                    Text("+ $ing", fontSize = 13.sp, color = MissingOrangeText)
                                }
                            }
                        }
                    }
                }
            }

            // Steps
            Column {
                SectionTitle("👨‍🍳", "Приготовление")
                recipe.steps.forEachIndexed { i, step ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(28.dp).background(Ink, CircleShape),
                            contentAlignment = Alignment.Center
                        ) { Text("${i + 1}", fontSize = 12.sp, color = Cream, fontWeight = FontWeight.Medium) }
                        Text(step, fontSize = 14.sp, color = Ink, lineHeight = 22.sp, modifier = Modifier.weight(1f))
                    }
                }
            }

            // Tip
            if (recipe.tip.isNotBlank()) {
                Column {
                    SectionTitle("💡", "Совет шефа")
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)).background(HaveGreen)
                    ) {
                        Box(modifier = Modifier.width(3.dp).fillMaxHeight().background(Sage))
                        Text(recipe.tip, fontSize = 13.sp, color = HaveGreenText, lineHeight = 20.sp, modifier = Modifier.padding(12.dp))
                    }
                }
            }

            // Nutrition
            Column {
                SectionTitle("📊", "Питательная ценность (на порцию)")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NutCell("ккал", "${recipe.calories}", Modifier.weight(1f))
                    NutCell("белки", "${recipe.protein}г", Modifier.weight(1f))
                    NutCell("жиры", "${recipe.fat}г", Modifier.weight(1f))
                    NutCell("углеводы", "${recipe.carbs}г", Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun NutCell(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(10.dp)).background(WarmWhite).padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Bark)
        Text(label, fontSize = 11.sp, color = Muted)
    }
}
