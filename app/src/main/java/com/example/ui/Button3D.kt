package com.example.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Oyunun ana aksiyon butonu.
 * Eski kalın 3D görünüm korunur ancak daha ince gölge, hafif gradyan,
 * tutarlı kenarlık ve gerçek basılma hareketiyle daha modern görünür.
 */
@Composable
fun Button3D(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(14.dp),
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 15.dp, vertical = 8.dp),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressOffset by animateDpAsState(
        targetValue = if (isPressed && enabled) 2.5.dp else 0.dp,
        label = "button_press"
    )

    val containerColor = if (enabled) colors.containerColor else colors.disabledContainerColor
    val contentColor = if (enabled) colors.contentColor else colors.disabledContentColor
    val bevelColor = if (enabled) containerColor.darken(0.68f) else Color(0xFFB8C2CF)
    val topHighlight = if (enabled) Color.White.copy(alpha = 0.26f) else Color.White.copy(alpha = 0.08f)
    val resolvedBorder = border ?: BorderStroke(1.dp, topHighlight)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Alt gölge/derinlik parent ölçüsünü değiştirmez; buton içeriği doğal boyutunu korur.
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 4.dp)
                .clip(shape)
                .background(bevelColor)
        )

        // Etkileşim yüzeyi parent'ın tamamını kaplar.
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(bottom = 4.dp)
                .offset(y = pressOffset)
                .shadow(
                    elevation = if (isPressed || !enabled) 0.dp else 4.dp,
                    shape = shape,
                    clip = false
                )
                .clip(shape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            containerColor.copy(alpha = if (enabled) 0.94f else 0.70f),
                            containerColor
                        )
                    )
                )
                .border(resolvedBorder, shape)
                .clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
        )

        CompositionLocalProvider(LocalContentColor provides contentColor) {
            ProvideTextStyle(value = MaterialTheme.typography.labelLarge) {
                Row(
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .offset(y = pressOffset)
                        .padding(contentPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }
        }
    }
}

private fun Color.darken(multiplier: Float): Color = Color(
    red = (red * multiplier).coerceIn(0f, 1f),
    green = (green * multiplier).coerceIn(0f, 1f),
    blue = (blue * multiplier).coerceIn(0f, 1f),
    alpha = alpha
)
