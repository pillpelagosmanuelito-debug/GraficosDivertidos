package com.educalab.graficosdivertidos.ui.navigation

import com.educalab.graficosdivertidos.domain.model.ModuleKey

/** Rutas de navegación de la app. */
object Destinations {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val MODULE_EXERCISES = "module/{moduleKey}"
    const val BUILDER = "builder"
    const val COMPARATOR = "comparator"
    const val DETECTIVE = "detective"
    const val CHALLENGES = "challenges"
    const val GALLERY = "gallery"
    const val PROFILE = "profile"

    fun moduleRoute(moduleKey: ModuleKey): String = "module/${moduleKey.name}"
}
