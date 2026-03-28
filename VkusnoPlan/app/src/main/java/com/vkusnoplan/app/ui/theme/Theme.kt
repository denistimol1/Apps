package com.vkusnoplan.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Cream             = Color(0xFFFAF6EF)
val WarmWhite         = Color(0xFFFFF9F2)
val Ink               = Color(0xFF1A1208)
val Bark              = Color(0xFF5C3D1E)
val Moss              = Color(0xFF3D6B44)
val Sage              = Color(0xFF7BA882)
val Sand              = Color(0xFFC8A97A)
val Terracotta        = Color(0xFFC4613A)
val LightSand         = Color(0xFFEDE0CC)
val TagBg             = Color(0xFFE8DDD0)
val Muted             = Color(0xFF8A7360)
val HaveGreen         = Color(0xFFEAF4EB)
val HaveGreenText     = Color(0xFF3D6B44)
val MissingOrange     = Color(0xFFFEF3EE)
val MissingOrangeText = Color(0xFFC4613A)

private val AppColorScheme = lightColorScheme(
    primary             = Terracotta,
    onPrimary           = Color.White,
    primaryContainer    = Color(0xFFFFDDD2),
    secondary           = Moss,
    onSecondary         = Color.White,
    secondaryContainer  = Color(0xFFD4EDD6),
    tertiary            = Sand,
    background          = Cream,
    surface             = WarmWhite,
    onBackground        = Ink,
    onSurface           = Ink,
    outline             = LightSand,
    outlineVariant      = TagBg,
)

@Composable
fun VkusnoPlanTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        content = content
    )
}
