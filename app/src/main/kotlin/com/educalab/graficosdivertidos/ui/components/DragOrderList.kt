package com.educalab.graficosdivertidos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp

/**
 * Lista reordenable por arrastre (long-press + drag). Se usa en el reto
 * "ordena las categorías de mayor a menor" y en la selección/orden de
 * categorías del Constructor. No usa ninguna librería externa: el arrastre
 * se calcula a mano con [pointerInput] y el desplazamiento vertical
 * acumulado.
 */
@Composable
fun <T> DragOrderList(
    items: List<T>,
    itemLabel: (T) -> String,
    onReordered: (List<T>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var itemHeightPx by remember { mutableStateOf(0f) }
    var draggingIndex by remember { mutableStateOf(-1) }
    var dragOffset by remember { mutableStateOf(0f) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEachIndexed { index, item ->
            val isDragging = draggingIndex == index
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isDragging) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface,
                tonalElevation = if (isDragging) 6.dp else 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coords -> if (itemHeightPx == 0f) itemHeightPx = coords.size.height.toFloat() }
                    .graphicsLayer { translationY = if (isDragging) dragOffset else 0f }
                    .pointerInput(items) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                draggingIndex = index
                                dragOffset = 0f
                            },
                            onDragEnd = {
                                if (draggingIndex >= 0 && itemHeightPx > 0f) {
                                    val moveBy = (dragOffset / itemHeightPx).toInt()
                                    val targetIndex = (draggingIndex + moveBy).coerceIn(0, items.lastIndex)
                                    if (targetIndex != draggingIndex) {
                                        val mutable = items.toMutableList()
                                        val moved = mutable.removeAt(draggingIndex)
                                        mutable.add(targetIndex, moved)
                                        onReordered(mutable)
                                    }
                                }
                                draggingIndex = -1
                                dragOffset = 0f
                            },
                            onDragCancel = {
                                draggingIndex = -1
                                dragOffset = 0f
                            },
                            onDrag = { change, amount ->
                                change.consume()
                                dragOffset += amount.y
                            },
                        )
                    },
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primary,
                    ) {
                        Text(
                            "${index + 1}",
                            color = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                    Box(modifier = Modifier.padding(start = 10.dp)) {
                        Text(itemLabel(item), style = MaterialTheme.typography.titleSmall)
                    }
                    Box(modifier = Modifier.weight(1f).padding(0.dp)) {}
                    Icon(Icons.Filled.DragHandle, contentDescription = "Arrastrar para reordenar")
                }
            }
        }
    }
}
