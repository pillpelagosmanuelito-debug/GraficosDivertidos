package com.educalab.graficosdivertidos.ui

import androidx.compose.runtime.compositionLocalOf

/** Id del perfil activo (la app maneja un único perfil local por dispositivo). */
val LocalUserId = compositionLocalOf<Long> { error("LocalUserId no ha sido provisto todavía") }
