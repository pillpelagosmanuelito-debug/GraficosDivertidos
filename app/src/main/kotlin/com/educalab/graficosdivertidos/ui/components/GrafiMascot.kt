package com.educalab.graficosdivertidos.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.educalab.graficosdivertidos.R

enum class GrafiPose { SALUDA, CELEBRA, INVESTIGA, CONSTRUYE }

private fun poseDrawable(pose: GrafiPose): Int = when (pose) {
    GrafiPose.SALUDA -> R.drawable.grafi_saluda
    GrafiPose.CELEBRA -> R.drawable.grafi_celebra
    GrafiPose.INVESTIGA -> R.drawable.grafi_investiga
    GrafiPose.CONSTRUYE -> R.drawable.grafi_construye
}

/** Grafi flotando suavemente (idle animation), sin sobrecargar de movimiento. */
@Composable
fun GrafiFloating(pose: GrafiPose, modifier: Modifier = Modifier, size: androidx.compose.ui.unit.Dp = 96.dp) {
    val transition = rememberInfiniteTransition(label = "grafi-float")
    val offsetY by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing), RepeatMode.Reverse),
        label = "grafi-offset",
    )
    androidx.compose.foundation.Image(
        painter = painterResource(poseDrawable(pose)),
        contentDescription = "Grafi, el asistente geométrico",
        modifier = modifier
            .size(size)
            .graphicsLayer { translationY = offsetY },
        contentScale = ContentScale.Fit,
    )
}

/** Grafi con un globo de diálogo breve. Se usa para presentar retos y celebrar avances. */
@Composable
fun GrafiSpeechBubble(
    pose: GrafiPose,
    message: String,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
) {
    AnimatedVisibility(visible = visible, enter = expandVertically(), exit = shrinkVertically()) {
        Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
            GrafiFloating(pose = pose, size = 64.dp)
            Box(modifier = Modifier.width(10.dp))
            Card(
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp),
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
