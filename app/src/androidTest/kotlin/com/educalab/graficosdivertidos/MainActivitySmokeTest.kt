package com.educalab.graficosdivertidos

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prueba instrumentada de humo: la app debe arrancar, sembrar la base de
 * datos y llegar al onboarding (primer inicio) sin bloquearse ni fallar.
 * Se ejecuta en un dispositivo/emulador real, a diferencia de las 70
 * pruebas unitarias JVM en app/src/test.
 */
@RunWith(AndroidJUnit4::class)
class MainActivitySmokeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun laAppArrancaYMuestraElOnboardingEnElPrimerInicio() {
        // El seeding + creación de perfil son asíncronos; se espera hasta que
        // aparezca el texto de bienvenida del onboarding (o la Home, si el
        // dispositivo de pruebas ya tenía datos de una ejecución previa).
        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule
                .onAllNodesWithTextContaining("Estudio de Visualización")
                .fetchSemanticsNodesSafely() || composeRule
                .onAllNodesWithTextContaining("Tu Estudio")
                .fetchSemanticsNodesSafely()
        }
    }

    private fun androidx.compose.ui.test.SemanticsNodeInteractionCollection.fetchSemanticsNodesSafely(): Boolean {
        return try {
            fetchSemanticsNodes().isNotEmpty()
        } catch (e: Throwable) {
            false
        }
    }

    private fun androidx.compose.ui.test.junit4.ComposeTestRule.onAllNodesWithTextContaining(text: String) =
        onAllNodes(androidx.compose.ui.test.hasText(text, substring = true))
}
