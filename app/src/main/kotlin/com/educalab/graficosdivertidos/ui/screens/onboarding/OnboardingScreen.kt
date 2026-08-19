package com.educalab.graficosdivertidos.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.educalab.graficosdivertidos.ui.components.AVAILABLE_AVATARS
import com.educalab.graficosdivertidos.ui.components.GrafiFloating
import com.educalab.graficosdivertidos.ui.components.GrafiPose
import com.educalab.graficosdivertidos.ui.components.avatarRes
import com.educalab.graficosdivertidos.ui.theme.Navy

@Composable
fun OnboardingScreen(viewModel: OnboardingViewModel, onDone: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.completed) {
        if (state.completed) onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                when (state.page) {
                    0 -> WelcomePage()
                    1 -> PurposePage()
                    2 -> ProgressPage()
                    else -> ProfilePage(state, viewModel)
                }
            }
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(viewModel.pageCount) { index ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (index == state.page) androidx.compose.ui.graphics.Color.White else androidx.compose.ui.graphics.Color.White.copy(alpha = 0.3f)),
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                if (state.page > 0) {
                    androidx.compose.material3.OutlinedButton(onClick = viewModel::previousPage) { Text("Atrás", color = androidx.compose.ui.graphics.Color.White) }
                } else {
                    Box {}
                }
                if (state.page < viewModel.pageCount - 1) {
                    Button(onClick = viewModel::nextPage) { Text("Siguiente") }
                } else {
                    Button(onClick = viewModel::finish, enabled = state.alias.isNotBlank()) { Text("¡Empezar!") }
                }
            }
        }
    }
}

@Composable
private fun WelcomePage() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        GrafiFloating(GrafiPose.SALUDA, size = 160.dp)
        Text(
            "Bienvenido al Estudio de Visualización",
            style = MaterialTheme.typography.headlineSmall,
            color = androidx.compose.ui.graphics.Color.White,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            "Soy Grafi, tu asistente geométrico. Aquí convertimos números aburridos en gráficos que cuentan historias.",
            style = MaterialTheme.typography.bodyLarge,
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
private fun PurposePage() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        GrafiFloating(GrafiPose.CONSTRUYE, size = 140.dp)
        Text(
            "Lee, compara y construye gráficos",
            style = MaterialTheme.typography.headlineSmall,
            color = androidx.compose.ui.graphics.Color.White,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            "Vas a interpretar barras, pictogramas, líneas y círculos, atrapar gráficos engañosos y construir los tuyos propios.",
            style = MaterialTheme.typography.bodyLarge,
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
private fun ProgressPage() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        GrafiFloating(GrafiPose.CELEBRA, size = 140.dp)
        Text(
            "Gana XP, estrellas e insignias",
            style = MaterialTheme.typography.headlineSmall,
            color = androidx.compose.ui.graphics.Color.White,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            "Todo lo que ganas viene de tus aciertos reales. Puedes jugar unos minutos y volver cuando quieras: tu progreso se guarda solo.",
            style = MaterialTheme.typography.bodyLarge,
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(
            "No pedimos tu nombre real, ni correo, ni ubicación: todo queda en este dispositivo.",
            style = MaterialTheme.typography.bodySmall,
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
private fun ProfilePage(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Elige tu alias y tu avatar",
            style = MaterialTheme.typography.headlineSmall,
            color = androidx.compose.ui.graphics.Color.White,
        )
        OutlinedTextField(
            value = state.alias,
            onValueChange = viewModel::setAlias,
            label = { Text("Tu alias (no tu nombre real)") },
            modifier = Modifier.padding(top = 16.dp),
            singleLine = true,
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.padding(top = 20.dp).size(280.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(AVAILABLE_AVATARS) { avatar ->
                val selected = state.selectedAvatar == avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(if (selected) androidx.compose.ui.graphics.Color.White.copy(alpha = 0.25f) else androidx.compose.ui.graphics.Color.Transparent)
                        .clickable { viewModel.selectAvatar(avatar) },
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(avatarRes(avatar)),
                        contentDescription = "Avatar",
                        modifier = Modifier.size(52.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
        }
    }
}
