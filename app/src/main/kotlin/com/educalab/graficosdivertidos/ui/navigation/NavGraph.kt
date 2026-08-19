package com.educalab.graficosdivertidos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.educalab.graficosdivertidos.GraficosDivertidosApp
import com.educalab.graficosdivertidos.domain.model.ModuleKey
import com.educalab.graficosdivertidos.ui.screens.builder.BuilderScreen
import com.educalab.graficosdivertidos.ui.screens.comparator.ComparatorScreen
import com.educalab.graficosdivertidos.ui.screens.detective.DetectiveScreen
import com.educalab.graficosdivertidos.ui.screens.gallery.GalleryScreen
import com.educalab.graficosdivertidos.ui.screens.home.HomeScreen
import com.educalab.graficosdivertidos.ui.screens.module.ModuleExerciseScreen
import com.educalab.graficosdivertidos.ui.screens.module.moduleTitle
import com.educalab.graficosdivertidos.ui.screens.profile.ProfileScreen

@Composable
fun GraficosNavGraph(
    navController: NavHostController,
    app: GraficosDivertidosApp,
    userId: Long,
) {
    NavHost(navController = navController, startDestination = Destinations.HOME) {
        composable(Destinations.HOME) {
            HomeScreen(
                viewModel = rememberHomeViewModel(app, userId),
                onOpenModule = { moduleKey ->
                    val route = when (moduleKey) {
                        ModuleKey.CONSTRUCTOR -> Destinations.BUILDER
                        ModuleKey.COMPARADOR -> Destinations.COMPARATOR
                        ModuleKey.DETECTIVE -> Destinations.DETECTIVE
                        ModuleKey.DESAFIOS -> Destinations.CHALLENGES
                        else -> Destinations.moduleRoute(moduleKey)
                    }
                    navController.navigate(route)
                },
                onOpenGallery = { navController.navigate(Destinations.GALLERY) },
                onOpenProfile = { navController.navigate(Destinations.PROFILE) },
            )
        }
        composable(
            route = Destinations.MODULE_EXERCISES,
            arguments = listOf(navArgument("moduleKey") { }),
        ) { backStackEntry ->
            val moduleKeyArg = backStackEntry.arguments?.getString("moduleKey")
            val moduleKey = ModuleKey.entries.firstOrNull { it.name == moduleKeyArg } ?: ModuleKey.BARRAS
            ModuleExerciseScreen(
                title = moduleTitle(moduleKey),
                viewModel = rememberModuleExerciseViewModel(app, userId, moduleKey),
                onBack = { navController.popBackStack() },
                onFinished = { navController.popBackStack() },
            )
        }
        composable(Destinations.CHALLENGES) {
            ModuleExerciseScreen(
                title = "Desafíos: repaso",
                viewModel = rememberModuleExerciseViewModel(app, userId, null),
                onBack = { navController.popBackStack() },
                onFinished = { navController.popBackStack() },
            )
        }
        composable(Destinations.BUILDER) {
            BuilderScreen(
                viewModel = rememberBuilderViewModel(app, userId),
                onBack = { navController.popBackStack() },
            )
        }
        composable(Destinations.COMPARATOR) {
            ComparatorScreen(
                viewModel = rememberComparatorViewModel(app, userId),
                onBack = { navController.popBackStack() },
                onFinished = { navController.popBackStack() },
            )
        }
        composable(Destinations.DETECTIVE) {
            DetectiveScreen(
                viewModel = rememberDetectiveViewModel(app, userId),
                onBack = { navController.popBackStack() },
                onFinished = { navController.popBackStack() },
            )
        }
        composable(Destinations.GALLERY) {
            GalleryScreen(
                viewModel = rememberGalleryViewModel(app, userId),
                onBack = { navController.popBackStack() },
            )
        }
        composable(Destinations.PROFILE) {
            ProfileScreen(
                viewModel = rememberProfileViewModel(app, userId),
                onBack = { navController.popBackStack() },
            )
        }
    }
}
