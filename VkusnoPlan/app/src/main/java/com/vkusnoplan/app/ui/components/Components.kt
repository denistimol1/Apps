package com.vkusnoplan.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.vkusnoplan.app.ui.theme.*

// ── Ingredient Chip ───────────────────────────────────────────────────────

@Composable
fun IngredientChip(label: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .background(TagBg, RoundedCornerShape(20.dp))
            .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, fontSize = 13.sp, color = Bark)
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Удалить",
                tint = Muted,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

// ── Preference Toggle Button ──────────────────────────────────────────────

@Composable
fun PrefButton(emoji: String, label: String, selected: Boolean, onClick: () -> Unit) {
    val bg by animateColorAsState(
        if (selected) Moss else Cream, tween(200), label = "prefBg"
    )
    val textColor by animateColorAsState(
        if (selected) Color.White else Muted, tween(200), label = "prefText"
    )
    val borderColor by animateColorAsState(
        if (selected) Moss else LightSand, tween(200), label = "prefBorder"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(emoji, fontSize = 14.sp)
            Spacer(Modifier.width(4.dp))
            Text(
                label,
                fontSize = 12.sp,
                color = textColor,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

// ── Match Progress Bar ────────────────────────────────────────────────────

@Composable
fun MatchBar(percent: Int) {
    val animatedWidth by animateFloatAsState(
        percent / 100f, tween(800), label = "matchBar"
    )
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(LightSand)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(Sage, Moss)
                        )
                    )
            )
        }
        Text("$percent% ингредиентов есть у вас", fontSize = 11.sp, color = Muted)
    }
}

// ── Section Title ─────────────────────────────────────────────────────────

@Composable
fun SectionTitle(emoji: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        Text(emoji, fontSize = 16.sp)
        Text(
            text,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Bark
        )
    }
}

// ── Ingredient Tag (in recipe detail) ────────────────────────────────────

@Composable
fun IngTag(label: String, have: Boolean) {
    val bg = if (have) HaveGreen else MissingOrange
    val color = if (have) HaveGreenText else MissingOrangeText
    val prefix = if (have) "✓ " else "+ "
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text("$prefix$label", fontSize = 13.sp, color = color)
    }
}
