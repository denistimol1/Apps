package com.vkusnoplan.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    // Список выполненных шагов (true = выполнен)
    val doneSteps = remember(recipe.id) {
        mutableStateListOf(*Array(recipe.steps.size) { false })
    }
    val doneCount = doneSteps.count { it }

    Column(modifier = Modifier.fillMaxSize().background(Cream)) {

        // ── Header ──────────────────────────────────────────────────────
        Box(modifier = Modifier.fillMaxWidth().background(Ink).padding(20.dp)) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(recipe.emoji, fontSize = 44.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(recipe.title, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Cream, lineHeight = 25.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("⏱ ${recipe.time}", fontSize = 11.sp, color = Muted)
                        Text("👤 ${recipe.servings} порц.", fontSize = 11.sp, color = Muted)
                        Text("📊 ${recipe.difficulty}", fontSize = 11.sp, color = Muted)
                    }
                    Spacer(Modifier.height(3.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("🔥 ${recipe.calories} ккал", fontSize = 11.sp, color = Muted)
                        Text("💪 ${recipe.protein}г белка", fontSize = 11.sp, color = Muted)
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

        // ── Прогресс ────────────────────────────────────────────────────
        if (recipe.steps.isNotEmpty()) {
            val progress = doneCount.toFloat() / recipe.steps.size
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0A04))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Прогресс приготовления", fontSize = 12.sp, color = Muted)
                    Text(
                        "$doneCount / ${recipe.steps.size} шагов",
                        fontSize = 12.sp,
                        color = if (progress >= 1f) Sage else Sand
                    )
                }
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF3A2E20))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (progress >= 1f) Sage else Sand)
                    )
                }
            }
        }

        // ── Тело ────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Ингредиенты
            Column {
                SectionTitle("🧾", "Ингредиенты")
                recipe.haveIngredients.chunked(2).forEach { row ->
                    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { ing ->
                            Box(Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(HaveGreen).padding(10.dp, 7.dp)) {
                                Text("✓ $ing", fontSize = 13.sp, color = HaveGreenText)
                            }
                        }
                        if (row.size < 2) Spacer(Modifier.weight(1f))
                    }
                }
                if (recipe.missingIngredients.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text("Нужно докупить:", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(bottom = 6.dp))
                    recipe.missingIngredients.chunked(2).forEach { row ->
                        Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { ing ->
                                Box(Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(MissingOrange).padding(10.dp, 7.dp)) {
                                    Text("+ $ing", fontSize = 13.sp, color = MissingOrangeText)
                                }
                            }
                            if (row.size < 2) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            // Шаги — интерактивный чеклист
            if (recipe.steps.isNotEmpty()) {
                Column {
                    SectionTitle("👨‍🍳", "Пошаговое приготовление")

                    recipe.steps.forEachIndexed { i, step ->
                        val isDone = doneSteps.getOrElse(i) { false }
                        val bgColor by animateColorAsState(
                            if (isDone) HaveGreen else WarmWhite, tween(250), label = "bg$i"
                        )
                        val circleColor by animateColorAsState(
                            if (isDone) Moss else Ink, tween(250), label = "circle$i"
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgColor)
                                .border(1.dp, if (isDone) Sage.copy(.4f) else LightSand, RoundedCornerShape(12.dp))
                                .clickable { if (i < doneSteps.size) doneSteps[i] = !doneSteps[i] }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier.size(32.dp).background(circleColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                } else {
                                    Text("${i + 1}", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Шаг ${i + 1}",
                                    fontSize = 11.sp,
                                    color = if (isDone) HaveGreenText else Muted,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    step,
                                    fontSize = 14.sp,
                                    color = if (isDone) HaveGreenText else Ink,
                                    lineHeight = 21.sp
                                )
                            }
                        }
                    }

                    // Готово!
                    if (doneCount == recipe.steps.size) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Moss)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎉 Блюдо готово! Приятного аппетита!", fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Совет шефа
            if (recipe.tip.isNotBlank()) {
                Column {
                    SectionTitle("💡", "Совет шефа")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp))
                            .background(HaveGreen)
                    ) {
                        Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(Sage))
                        Text(recipe.tip, fontSize = 13.5.sp, color = HaveGreenText, lineHeight = 21.sp, modifier = Modifier.padding(12.dp))
                    }
                }
            }

            // КБЖУ
            Column {
                SectionTitle("📊", "Питательная ценность (на порцию)")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
        Text(value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Bark)
        Text(label, fontSize = 11.sp, color = Muted)
    }
}
