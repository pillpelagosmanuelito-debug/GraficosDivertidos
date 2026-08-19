package com.educalab.graficosdivertidos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.educalab.graficosdivertidos.ui.LocalUserId
import com.educalab.graficosdivertidos.ui.navigation.GraficosNavGraph
import com.educalab.graficosdivertidos.ui.screens.onboarding.OnboardingScreen
import com.educalab.graficosdivertidos.ui.screens.onboarding.OnboardingViewModel
import com.educalab.graficosdivertidos.ui.theme.GraficosDivertidosTheme
import com.educalab.graficosdivertidos.ui.theme.Navy

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as GraficosDivertidosApp
        setContent {
            GraficosDivertidosTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RootContent(app)
                }
            }
        }
    }
}

@Composable
private fun RootContent(app: GraficosDivertidosApp) {
    var userId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        app.seeder.seedIfNeeded()
        val id = app.profileRepository.ensureProfileExists(
            defaultAlias = "Explorador",
            defaultAvatar = "avatar_01",
            now = System.currentTimeMillis(),
        )
        app.profileRepository.touchLastOpened(id, System.currentTimeMillis())
        userId = id
    }

    val currentUserId = userId
    if (currentUserId == null) {
        LoadingSplash()
        return
    }

    CompositionLocalProvider(LocalUserId provides currentUserId) {
        val profile by app.profileRepository.observeProfile(currentUserId).collectAsState(initial = null)
        when (profile?.onboardingCompleted) {
            null -> LoadingSplash()
            false -> {
                val onboardingViewModel = androidx.lifecycle.viewmodel.compose.viewModel<OnboardingViewModel>(
                    factory = androidx.lifecycle.viewmodel.viewModelFactory {
                        androidx.lifecycle.viewmodel.initializer { OnboardingViewModel(currentUserId, app.profileRepository) }
                    },
                )
                OnboardingScreen(viewModel = onboardingViewModel, onDone = {})
            }
            true -> {
                val navController = rememberNavController()
                GraficosNavGraph(navController = navController, app = app, userId = currentUserId)
            }
        }
    }
}

@Composable
private fun LoadingSplash() {
    Box(modifier = Modifier.fillMaxSize().background(Navy), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White)
    }
}
