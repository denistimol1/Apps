package com.vkusnoplan.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vkusnoplan.app.data.model.Recipe
import com.vkusnoplan.app.ui.theme.*

@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WarmWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // Emoji banner
        Box(
            modifier = Modifier.fillMaxWidth().height(100.dp).background(
                Brush.linearGradient(listOf(LightSand, Cream))
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(recipe.emoji, fontSize = 48.sp)
        }

        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(recipe.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Ink, lineHeight = 20.sp)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("⏱ ${recipe.time}", fontSize = 11.sp, color = Muted)
                Text("👤 ${recipe.servings} порц.", fontSize = 11.sp, color = Muted)
                Text("📊 ${recipe.difficulty}", fontSize = 11.sp, color = Muted)
            }

            MatchBar(recipe.matchPercent)

            val (badgeBg, badgeColor, badgeText) = if (recipe.missingIngredients.isEmpty()) {
                Triple(HaveGreen, HaveGreenText, "✓ Всё есть!")
            } else {
                val shown = recipe.missingIngredients.take(2).joinToString(", ")
                val extra = if (recipe.missingIngredients.size > 2) " +${recipe.missingIngredients.size - 2}" else ""
                Triple(MissingOrange, MissingOrangeText, "+ $shown$extra")
            }

            Box(
                modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(badgeBg).padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(badgeText, fontSize = 11.sp, color = badgeColor)
            }
        }
    }
}
