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
import com.vkusnoplan.app.ui.components.IngTag
import com.vkusnoplan.app.ui.components.SectionTitle
import com.vkusnoplan.app.ui.theme.*

@Composable
fun RecipeDetailScreen(recipe: Recipe, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Ink)
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(recipe.emoji, fontSize = 44.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        recipe.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Cream,
                        lineHeight = 26.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        HeaderMeta("⏱", recipe.time)
                        HeaderMeta("👤", "${recipe.servings} порц.")
                        HeaderMeta("📊", recipe.difficulty)
                        HeaderMeta("🔥", "${recipe.calories} ккал")
                    }
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = .15f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Закрыть", tint = Cream)
                }
            }
        }

        // Scrollable body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Ingredients
            SectionSection(title = "🧾", label = "Ингредиенты") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recipe.haveIngredients.forEach { IngTag(it, true) }
                    recipe.missingIngredients.forEach { IngTag(it, false) }
                }
            }

            // Steps
            SectionSection(title = "👨‍🍳", label = "Приготовление") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    recipe.steps.forEachIndexed { i, step ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Ink, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${i + 1}",
                                    fontSize = 12.sp,
                                    color = Cream,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(step, fontSize = 14.sp, color = Ink, lineHeight = 22.sp, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Tip
            if (recipe.tip.isNotBlank()) {
                SectionSection(title = "💡", label = "Совет шефа") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                            .background(HaveGreen)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(IntrinsicSize.Max)
                                .fillMaxHeight()
                                .background(Sage)
                        )
                        Text(
                            recipe.tip,
                            fontSize = 13.5.sp,
                            color = HaveGreenText,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Nutrition
            SectionSection(title = "📊", label = "Питательная ценность (на порцию)") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
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
private fun SectionSection(title: String, label: String, content: @Composable () -> Unit) {
    Column {
        SectionTitle(title, label)
        content()
    }
}

@Composable
private fun HeaderMeta(icon: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(icon, fontSize = 11.sp)
        Text(text, fontSize = 11.sp, color = Muted)
    }
}

@Composable
private fun NutCell(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(WarmWhite)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Bark)
        Text(label, fontSize = 11.sp, color = Muted)
    }
}
