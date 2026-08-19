package com.educalab.graficosdivertidos.ui.screens.gallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.educalab.graficosdivertidos.R
import com.educalab.graficosdivertidos.ui.components.AnimatedProgressBar
import com.educalab.graficosdivertidos.ui.components.ModuleStateIcon
import com.educalab.graficosdivertidos.ui.components.badgeIconRes
import com.educalab.graficosdivertidos.ui.components.moduleIconRes
import com.educalab.graficosdivertidos.ui.components.moduleStateLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(viewModel: GalleryViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Galería de logros") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Volver") } },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Card(shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Nivel ${state.stats.level}", style = MaterialTheme.typography.titleLarge)
                        Text("${state.stats.totalXp} XP acumulados · ${state.stats.totalStars} estrellas", style = MaterialTheme.typography.bodyMedium)
                        AnimatedProgressBar(progress = state.stats.levelProgress, modifier = Modifier.padding(top = 8.dp))
                        Text(
                            "Racha actual: ${state.stats.currentStreak} · Mejor racha: ${state.stats.bestStreak}",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
            }
            item { Text("Progreso por módulo", style = MaterialTheme.typography.titleMedium) }
            items(state.modules) { module ->
                Card(shape = RoundedCornerShape(16.dp)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(moduleIconRes(module.moduleKey)),
                            contentDescription = null,
                            modifier = Modifier.size(44.dp),
                        )
                        Column(Modifier.padding(start = 12.dp).weight(1f)) {
                            Text(module.title, style = MaterialTheme.typography.titleSmall)
                            Text(
                                "${module.completedCount}/${module.totalCount} · ${moduleStateLabel(module.state)}",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        ModuleStateIcon(module.state)
                    }
                }
            }
            item { Text("Insignias", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 4.dp)) }
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(130.dp * (state.badges.size / 3 + 1)),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    gridItems(state.badges) { badge ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.alpha(if (badge.unlocked) 1f else 0.35f),
                        ) {
                            Image(
                                painter = painterResource(badgeIconRes(badge.iconKey)),
                                contentDescription = badge.title,
                                modifier = Modifier.size(72.dp),
                                contentScale = ContentScale.Fit,
                            )
                            Text(
                                badge.title,
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}
