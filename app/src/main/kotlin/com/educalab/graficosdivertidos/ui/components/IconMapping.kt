package com.educalab.graficosdivertidos.ui.components

import com.educalab.graficosdivertidos.R
import com.educalab.graficosdivertidos.domain.model.ModuleKey

/** Traduce las claves de icono guardadas en la base de datos a recursos @drawable reales. */
fun badgeIconRes(iconKey: String): Int = when (iconKey) {
    "badge_primer_grafico" -> R.drawable.badge_primer_grafico
    "badge_maestro_barras" -> R.drawable.badge_maestro_barras
    "badge_ojo_de_lince" -> R.drawable.badge_ojo_de_lince
    "badge_constructor_experto" -> R.drawable.badge_constructor_experto
    "badge_detective_grafico" -> R.drawable.badge_detective_grafico
    "badge_comparador_agudo" -> R.drawable.badge_comparador_agudo
    "badge_racha_5" -> R.drawable.badge_racha_5
    "badge_explorador_datos" -> R.drawable.badge_explorador_datos
    "badge_precision_total" -> R.drawable.badge_precision_total
    "badge_leyenda_del_estudio" -> R.drawable.badge_leyenda_del_estudio
    else -> R.drawable.badge_primer_grafico
}

fun moduleIconRes(moduleKey: ModuleKey): Int = when (moduleKey) {
    ModuleKey.BARRAS -> R.drawable.icono_modulo_bars
    ModuleKey.PICTOGRAMAS -> R.drawable.icono_modulo_pictograms
    ModuleKey.LINEAS -> R.drawable.icono_modulo_lines
    ModuleKey.CIRCULAR -> R.drawable.icono_modulo_pie
    ModuleKey.CONSTRUCTOR -> R.drawable.icono_modulo_builder
    ModuleKey.COMPARADOR -> R.drawable.icono_modulo_comparator
    ModuleKey.DETECTIVE -> R.drawable.icono_modulo_detective
    ModuleKey.DESAFIOS -> R.drawable.icono_modulo_challenges
}

fun avatarRes(avatarKey: String): Int = when (avatarKey) {
    "avatar_01" -> R.drawable.avatar_01
    "avatar_02" -> R.drawable.avatar_02
    "avatar_03" -> R.drawable.avatar_03
    "avatar_04" -> R.drawable.avatar_04
    "avatar_05" -> R.drawable.avatar_05
    "avatar_06" -> R.drawable.avatar_06
    "avatar_07" -> R.drawable.avatar_07
    "avatar_08" -> R.drawable.avatar_08
    else -> R.drawable.avatar_01
}

val AVAILABLE_AVATARS = listOf("avatar_01", "avatar_02", "avatar_03", "avatar_04", "avatar_05", "avatar_06", "avatar_07", "avatar_08")
