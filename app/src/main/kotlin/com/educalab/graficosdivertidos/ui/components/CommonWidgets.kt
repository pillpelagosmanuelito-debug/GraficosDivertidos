package com.educalab.graficosdivertidos.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.educalab.graficosdivertidos.domain.model.ModuleState

/** Barra de progreso animada con marcas de porcentaje, usada en XP y en módulos. */
@Composable
fun AnimatedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    height: androidx.compose.ui.unit.Dp = 12.dp,
) {
    val animated by animateFloatAsState(targetValue = progress.coerceIn(0f, 1f), label = "progress")
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(trackColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animated)
                .clip(RoundedCornerShape(50))
                .background(color),
        )
    }
}

/** Fila de estrellas (0..maxStars) para mostrar precisión/logro de un ejercicio o módulo. */
@Composable
fun StarRow(filled: Int, maxStars: Int = 3, modifier: Modifier = Modifier, starSize: androidx.compose.ui.unit.Dp = 20.dp) {
    Row(modifier = modifier) {
        repeat(maxStars) { index ->
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = if (index < filled) Color(0xFFFFB627) else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(starSize),
            )
        }
    }
}

/** Icono de estado de módulo (bloqueado/disponible/iniciado/completado/dominado): nunca solo color. */
@Composable
fun ModuleStateIcon(state: ModuleState, modifier: Modifier = Modifier) {
    when (state) {
        ModuleState.BLOQUEADO -> Icon(Icons.Filled.Lock, contentDescription = "Bloqueado", modifier = modifier, tint = Color.Gray)
        ModuleState.DISPONIBLE -> Icon(Icons.Filled.PlayArrow, contentDescription = "Disponible", modifier = modifier, tint = MaterialTheme.colorScheme.primary)
        ModuleState.INICIADO -> Icon(Icons.Filled.PlayArrow, contentDescription = "Iniciado", modifier = modifier, tint = Color(0xFFFFB627))
        ModuleState.COMPLETADO -> Icon(Icons.Filled.CheckCircle, contentDescription = "Completado", modifier = modifier, tint = Color(0xFF2FB170))
        ModuleState.DOMINADO -> Icon(Icons.Filled.Star, contentDescription = "Dominado", modifier = modifier, tint = Color(0xFFFFB627))
    }
}

fun moduleStateLabel(state: ModuleState): String = when (state) {
    ModuleState.BLOQUEADO -> "Bloqueado"
    ModuleState.DISPONIBLE -> "Disponible"
    ModuleState.INICIADO -> "En progreso"
    ModuleState.COMPLETADO -> "Completado"
    ModuleState.DOMINADO -> "¡Dominado!"
}

/** Botón grande de opción (opción múltiple / comparador), con estado de selección. */
@Composable
fun ChoiceButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    resultTint: Color? = null,
) {
    val borderColor = resultTint ?: if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = enabled, onClick = onClick),
        color = resultTint?.copy(alpha = 0.12f)
            ?: if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(2.dp, borderColor),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** Banner de retroalimentación educativa (nunca solo "Correcto"/"Incorrecto"). */
@Composable
fun FeedbackBanner(
    isCorrect: Boolean,
    explanation: String,
    xpAwarded: Int,
    modifier: Modifier = Modifier,
) {
    val bg = if (isCorrect) Color(0xFF2FB170) else Color(0xFFE5484D)
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bg.copy(alpha = 0.14f)),
        border = BorderStroke(2.dp, bg),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Lock,
                    contentDescription = if (isCorrect) "Correcto" else "Intenta de nuevo",
                    tint = bg,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isCorrect) "¡Muy bien!" else "Casi lo tienes",
                    style = MaterialTheme.typography.titleMedium,
                    color = bg,
                    fontWeight = FontWeight.Bold,
                )
                if (isCorrect && xpAwarded > 0) {
                    Spacer(Modifier.width(8.dp))
                    Surface(shape = RoundedCornerShape(50), color = bg) {
                        Text(
                            "+$xpAwarded XP",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(explanation, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/** Insignia pequeña de estadística (XP, racha, estrellas) para la cabecera de Home/Perfil. */
@Composable
fun StatPill(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, modifier: Modifier = Modifier, tint: Color = Color(0xFFFFB627)) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}
