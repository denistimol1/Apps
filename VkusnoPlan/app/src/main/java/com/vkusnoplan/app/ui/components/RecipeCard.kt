package com.vkusnoplan.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vkusnoplan.app.data.model.Recipe
import com.vkusnoplan.app.ui.theme.*

@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(
        if (pressed) 2.dp else 6.dp, tween(150), label = "cardElev"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation, RoundedCornerShape(16.dp))
            .clickable {
                pressed = true
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WarmWhite),
    ) {
        // Emoji banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(LightSand, Cream)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(recipe.emoji, fontSize = 48.sp)
        }

        // Body
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                recipe.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Ink,
                lineHeight = 20.sp
            )

            // Meta row
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetaChip("⏱", recipe.time)
                MetaChip("👤", "${recipe.servings} порц.")
                MetaChip("📊", recipe.difficulty)
            }

            MatchBar(recipe.matchPercent)

            // Missing / complete badge
            if (recipe.missingIngredients.isEmpty()) {
                BadgeChip("✓ Всё есть!", success = true)
            } else {
                val shown = recipe.missingIngredients.take(2).joinToString(", ")
                val extra = if (recipe.missingIngredients.size > 2)
                    " и ещё ${recipe.missingIngredients.size - 2}" else ""
                BadgeChip("+ $shown$extra", success = false)
            }
        }
    }
}

@Composable
private fun MetaChip(icon: String, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(icon, fontSize = 11.sp)
        Text(label, fontSize = 11.sp, color = Muted)
    }
}

@Composable
private fun BadgeChip(text: String, success: Boolean) {
    val bg    = if (success) HaveGreen    else MissingOrange
    val color = if (success) HaveGreenText else MissingOrangeText
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text, fontSize = 11.sp, color = color)
    }
}
