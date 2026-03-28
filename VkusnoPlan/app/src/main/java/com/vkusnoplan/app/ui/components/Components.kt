package com.vkusnoplan.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vkusnoplan.app.ui.theme.*

@Composable
fun IngredientChip(label: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier.background(TagBg, RoundedCornerShape(20.dp)).padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, fontSize = 13.sp, color = Bark)
        Box(
            modifier = Modifier.size(20.dp).clip(CircleShape).clickable(onClick = onRemove),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Close, contentDescription = "Удалить", tint = Muted, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
fun MatchBar(percent: Int) {
    val animatedWidth by animateFloatAsState(percent / 100f, tween(800), label = "matchBar")
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(6.dp)).background(LightSand)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Brush.horizontalGradient(listOf(Sage, Moss)))
            )
        }
        Text("$percent% ингредиентов есть у вас", fontSize = 11.sp, color = Muted)
    }
}

@Composable
fun SectionTitle(emoji: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        Text(emoji, fontSize = 16.sp)
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Bark)
    }
}
