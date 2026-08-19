package com.educalab.graficosdivertidos.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.educalab.graficosdivertidos.R
import com.educalab.graficosdivertidos.domain.model.ModuleKey
import com.educalab.graficosdivertidos.domain.model.ModuleState
import com.educalab.graficosdivertidos.domain.model.ModuleSummary
import com.educalab.graficosdivertidos.ui.components.AnimatedProgressBar
import com.educalab.graficosdivertidos.ui.components.ModuleStateIcon
import com.educalab.graficosdivertidos.ui.components.StatPill
import com.educalab.graficosdivertidos.ui.components.avatarRes
import com.educalab.graficosdivertidos.ui.components.moduleIconRes
import com.educalab.graficosdivertidos.ui.components.moduleStateLabel
import com.educalab.graficosdivertidos.ui.theme.Navy
import com.educalab.graficosdivertidos.ui.theme.Navy2

private val moduleDescriptions = mapOf(
    ModuleKey.BARRAS to "Compara alturas y cantidades",
    ModuleKey.PICTOGRAMAS to "Cuenta con iconos y escalas",
    ModuleKey.LINEAS to "Descubre tendencias en el tiempo",
    ModuleKey.CIRCULAR to "Lee partes de un total",
    ModuleKey.CONSTRUCTOR to "Crea tus propios gráficos",
    ModuleKey.COMPARADOR to "Elige la mejor representación",
    ModuleKey.DETECTIVE to "Atrapa gráficos engañosos",
    ModuleKey.DESAFIOS to "Repasa tus retos pendientes",
)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenModule: (ModuleKey) -> Unit,
    onOpenGallery: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Navy)) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
            item { HomeHeader(state, onOpenProfile, onOpenGallery) }
            item {
                Text(
                    "Tu Estudio",
                    style = MaterialTheme.typography.headlineSmall,
                    color = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp),
                )
                Text(
                    "Elige una estación para empezar tu próxima misión.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 2.dp, bottom = 12.dp),
                )
            }
            items(state.modules) { module ->
                StationCard(module = module, onClick = { onOpenModule(module.moduleKey) })
            }
        }
    }
}

@Composable
private fun HomeHeader(state: HomeUiState, onOpenProfile: () -> Unit, onOpenGallery: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(R.drawable.portada_estudio),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().aspectRatio(1.5f),
            contentScale = ContentScale.Crop,
        )
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.5f).background(Navy.copy(alpha = 0.15f)))
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f))
                        .clickable(onClick = onOpenProfile),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(avatarRes(state.profile?.avatarKey ?: "avatar_01")),
                        contentDescription = "Perfil",
                        modifier = Modifier.size(44.dp),
                    )
                }
                Column(modifier = Modifier.padding(start = 10.dp).weight(1f)) {
                    Text(
                        "¡Hola, ${state.profile?.alias ?: "Explorador"}!",
                        style = MaterialTheme.typography.titleLarge,
                        color = androidx.compose.ui.graphics.Color.White,
                    )
                    Text(
                        "Nivel ${state.stats.level}",
                        style = MaterialTheme.typography.labelMedium,
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f),
                    )
                }
                IconButton(onClick = onOpenGallery) {
                    Icon(Icons.Filled.EmojiEvents, contentDescription = "Galería de logros", tint = androidx.compose.ui.graphics.Color(0xFFFFB627))
                }
            }
            Row(modifier = Modifier.padding(top = 10.dp)) {
                StatPill(icon = Icons.Filled.Star, label = "${state.stats.totalXp} XP")
                Box(Modifier.size(8.dp))
                StatPill(icon = Icons.Filled.LocalFireDepartment, label = "Racha ${state.stats.currentStreak}", tint = androidx.compose.ui.graphics.Color(0xFFFF6B6B))
            }
            AnimatedProgressBar(
                progress = state.stats.levelProgress,
                modifier = Modifier.padding(top = 10.dp),
                color = androidx.compose.ui.graphics.Color(0xFFFFB627),
                trackColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.25f),
            )
        }
    }
}

@Composable
private fun StationCard(module: ModuleSummary, onClick: () -> Unit) {
    val locked = module.state == ModuleState.BLOQUEADO
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable(enabled = !locked, onClick = onClick),
        color = Navy2,
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(moduleIconRes(module.moduleKey)),
                    contentDescription = null,
                    modifier = Modifier.size(46.dp),
                )
            }
            Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                Text(module.title, style = MaterialTheme.typography.titleMedium, color = androidx.compose.ui.graphics.Color.White)
                Text(
                    moduleDescriptions[module.moduleKey].orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.65f),
                )
                if (module.totalCount > 0) {
                    AnimatedProgressBar(
                        progress = if (module.totalCount > 0) module.completedCount.toFloat() / module.totalCount else 0f,
                        modifier = Modifier.padding(top = 6.dp),
                        height = 6.dp,
                        trackColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.15f),
                    )
                }
                Text(
                    moduleStateLabel(module.state),
                    style = MaterialTheme.typography.labelSmall,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            ModuleStateIcon(module.state, modifier = Modifier.padding(start = 8.dp))
        }
    }
}
