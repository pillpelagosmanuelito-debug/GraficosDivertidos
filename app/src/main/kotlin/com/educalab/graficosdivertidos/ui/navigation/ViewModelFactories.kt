package com.educalab.graficosdivertidos.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.runtime.Composable
import com.educalab.graficosdivertidos.GraficosDivertidosApp
import com.educalab.graficosdivertidos.domain.model.ModuleKey
import com.educalab.graficosdivertidos.ui.screens.builder.BuilderViewModel
import com.educalab.graficosdivertidos.ui.screens.comparator.ComparatorViewModel
import com.educalab.graficosdivertidos.ui.screens.detective.DetectiveViewModel
import com.educalab.graficosdivertidos.ui.screens.gallery.GalleryViewModel
import com.educalab.graficosdivertidos.ui.screens.home.HomeViewModel
import com.educalab.graficosdivertidos.ui.screens.module.ModuleExerciseViewModel
import com.educalab.graficosdivertidos.ui.screens.onboarding.OnboardingViewModel
import com.educalab.graficosdivertidos.ui.screens.profile.ProfileViewModel

/**
 * Fábricas manuales de ViewModel (sin Hilt) usando la DSL
 * `viewModelFactory { initializer { ... } }` de androidx.lifecycle, para
 * poder inyectar los repositorios del contenedor de la Application.
 */

@Composable
fun rememberHomeViewModel(app: GraficosDivertidosApp, userId: Long): HomeViewModel = viewModel(
    factory = viewModelFactory {
        initializer { HomeViewModel(userId, app.profileRepository, app.progressRepository) }
    },
)

@Composable
fun rememberModuleExerciseViewModel(app: GraficosDivertidosApp, userId: Long, moduleKey: ModuleKey?): ModuleExerciseViewModel = viewModel(
    key = "module_${moduleKey?.name ?: "review"}",
    factory = viewModelFactory {
        initializer { ModuleExerciseViewModel(userId, moduleKey, app.contentRepository, app.progressRepository) }
    },
)

@Composable
fun rememberDetectiveViewModel(app: GraficosDivertidosApp, userId: Long): DetectiveViewModel = viewModel(
    factory = viewModelFactory {
        initializer { DetectiveViewModel(userId, app.contentRepository, app.progressRepository) }
    },
)

@Composable
fun rememberComparatorViewModel(app: GraficosDivertidosApp, userId: Long): ComparatorViewModel = viewModel(
    factory = viewModelFactory {
        initializer { ComparatorViewModel(userId, app.contentRepository, app.progressRepository) }
    },
)

@Composable
fun rememberBuilderViewModel(app: GraficosDivertidosApp, userId: Long): BuilderViewModel = viewModel(
    factory = viewModelFactory {
        initializer { BuilderViewModel(userId, app.contentRepository, app.builderRepository, app.progressRepository) }
    },
)

@Composable
fun rememberGalleryViewModel(app: GraficosDivertidosApp, userId: Long): GalleryViewModel = viewModel(
    factory = viewModelFactory {
        initializer { GalleryViewModel(userId, app.profileRepository, app.progressRepository) }
    },
)

@Composable
fun rememberProfileViewModel(app: GraficosDivertidosApp, userId: Long): ProfileViewModel = viewModel(
    factory = viewModelFactory {
        initializer { ProfileViewModel(userId, app.profileRepository) }
    },
)

@Composable
fun rememberOnboardingViewModel(app: GraficosDivertidosApp, userId: Long): OnboardingViewModel = viewModel(
    factory = viewModelFactory {
        initializer { OnboardingViewModel(userId, app.profileRepository) }
    },
)
