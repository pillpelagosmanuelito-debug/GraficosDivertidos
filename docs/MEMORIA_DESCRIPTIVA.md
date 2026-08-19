# Memoria descriptiva — Gráficos Divertidos

## 1. Identificación del proyecto

| Campo | Valor |
|---|---|
| Nombre | Gráficos Divertidos (Estudio de Visualización) |
| Paquete | `com.educalab.graficosdivertidos` |
| Versión | 1.0.0 |
| Plataforma | Android nativo (Kotlin + Jetpack Compose) |
| Público objetivo | Niñas y niños de 9 a 13 años |
| Área curricular | Estadística — representación gráfica |
| Conectividad | 100% offline |

## 2. Problema y justificación

La estadística escolar suele enseñarse con ejercicios de opción múltiple sobre gráficos ya hechos, sin que el estudiante llegue a construir uno propio ni a detectar cuándo un gráfico engaña. Gráficos Divertidos aborda esa carencia con cuatro frentes: **leer** gráficos de los cuatro tipos más comunes en la escuela primaria (barras, pictogramas, líneas y circular), **comparar** representaciones para razonar cuál comunica mejor un mismo dato, **detectar** errores de diseño deliberados (eje truncado, escala inconsistente, datos faltantes, título engañoso, categoría incorrecta, pictograma sin escala) y **construir** gráficos propios eligiendo dataset, tipo, título, categorías, etiquetas, escala y leyenda. La justificación pedagógica es que la alfabetización de datos exige tanto comprensión crítica como práctica de construcción, no solo memorización de nombres de gráficos.

## 3. Objetivos

**General:** ofrecer una experiencia Android completa, atractiva y offline que desarrolle alfabetización estadística y pensamiento crítico frente a gráficos, apropiada para niños de 9 a 13 años.

**Específicos:**
- Representar visualmente los cuatro tipos de gráfico más usados en la escuela primaria, dibujados a mano con Compose Canvas.
- Ofrecer mecánicas de interacción variadas (seleccionar sobre el gráfico, ordenar arrastrando, estimar con slider, comparar, opción múltiple), evitando que la opción múltiple domine la experiencia.
- Modelar y perseguir un sistema de progreso, XP e insignias basado en acciones reales guardadas en Room.
- Enseñar explícitamente a reconocer gráficos engañosos mediante un catálogo de 30 casos deliberadamente problemáticos.
- Permitir que el usuario construya y guarde sus propios gráficos con datos reales del contenido semilla.
- Cumplir con privacidad infantil: cero datos personales, cero conexión a internet.

## 4. Público objetivo

Niños y niñas de 9 a 13 años con lectura fluida de textos breves, capacidad de razonamiento comparativo y experiencia previa con interfaces táctiles. Se evitó deliberadamente la estética "de preescolar" (colores pastel excesivos, personajes de peluche, lenguaje condescendiente) en favor de un tono "estudio/laboratorio de datos": moderno, colorido, con desafío gradual y sensación de descubrimiento.

## 5. Alcance y exclusiones

**Incluye:** los 10 módulos descritos en el README, contenido semilla completo (30 datasets, 45 definiciones de gráfico, 50 ejercicios, 30 retos del Detective, 20 retos del Comparador, 10 insignias), persistencia real en Room, sistema de XP/nivel/racha/insignias, onboarding de 4 pantallas, perfil con alias y avatar local, ilustraciones propias (mascota Grafi, portada, iconografía por módulo, insignias, avatares), animaciones de entrada en los cuatro tipos de gráfico, y batería de pruebas unitarias e instrumentadas.

**Excluye explícitamente:** cuentas de usuario, sincronización en la nube, multijugador o rankings online, compras dentro de la app, publicidad, analítica/telemetría, reconocimiento de voz o cámara (no son necesarios para esta temática), y contenido curricular fuera de "representación gráfica de datos" (no cubre, por ejemplo, medidas de tendencia central o probabilidad).

## 6. Requisitos funcionales

- RF01: la app debe generar contenido semilla completo en el primer arranque y no repetirlo en arranques posteriores.
- RF02: el usuario debe poder crear un perfil local (alias + avatar) sin datos personales reales.
- RF03: cada uno de los 4 módulos base debe presentar ejercicios interpretativos con al menos 4 mecánicas de interacción distintas.
- RF04: el módulo Constructor debe permitir elegir dataset, tipo de gráfico, título, categorías (selección y orden), etiquetas, escala y leyenda, con validación antes de guardar.
- RF05: el módulo Detective debe presentar gráficos con uno de 6 tipos de error y pedir identificarlo.
- RF06: el módulo Comparador debe mostrar dos representaciones del mismo dataset y pedir elegir la que comunica mejor, con explicación.
- RF07: el progreso, XP, racha e insignias deben derivarse exclusivamente de intentos registrados en Room (nunca de estados en memoria).
- RF08: el módulo Desafíos debe ofrecer una cola de repaso con ejercicios pendientes o fallados de los módulos base.
- RF09: la Galería debe mostrar progreso por módulo (con icono de estado, no solo color) y las insignias obtenidas/pendientes.

## 7. Requisitos no funcionales

- RNF01: la app debe funcionar sin conexión a internet en el 100% de sus funciones.
- RNF02: no debe declararse el permiso `INTERNET` ni ningún permiso sensible (cámara, micrófono, ubicación, contactos).
- RNF03: el minSdk debe ser 24 (Android 7.0) para maximizar compatibilidad con dispositivos escolares.
- RNF04: las animaciones deben ser breves (400-800 ms) y no bloquear la interacción.
- RNF05: todos los textos deben estar en español natural, con oraciones breves adecuadas al rango de edad.
- RNF06: el contraste y los estados visuales no deben depender únicamente del color (se añade icono/texto).

## 8. Casos de uso principales

1. **Primer inicio:** el usuario abre la app, ve el onboarding (4 pantallas), elige alias y avatar, y llega a la Home/Estudio.
2. **Resolver un reto de barras:** el usuario entra a "Barras", ve un gráfico animado, interactúa (toca la barra correcta, por ejemplo), recibe retroalimentación educativa y XP, y avanza al siguiente reto.
3. **Construir un gráfico propio:** el usuario recorre el wizard del Constructor y guarda un gráfico circular sobre "Color favorito del salón".
4. **Atrapar un gráfico engañoso:** el usuario ve un gráfico de barras con el eje truncado y debe identificar el tipo de error entre 6 opciones.
5. **Comparar representaciones:** el usuario ve el mismo dataset como circular y como barras, y elige cuál comunica mejor la proporción de cada categoría.
6. **Repasar:** el usuario entra a "Desafíos" y practica únicamente lo que aún no domina.
7. **Revisar logros:** el usuario abre la Galería y ve su nivel, racha, insignias obtenidas y progreso por módulo.

## 9. Módulos y pantallas

Ver README.md §Módulos. En total sí se cuentan 8 módulos de contenido más pantallas de apoyo (onboarding, perfil, galería), dentro del rango de referencia de 8-12 pantallas principales indicado en la especificación maestra.

## 10. Flujo de navegación

`Onboarding → Home (Estudio)` y desde Home hacia cualquiera de las 8 estaciones; cada módulo regresa a Home al terminar la sesión o al pulsar "Volver". Constructor, Comparador, Detective y Desafíos se desbloquean en cuanto el usuario completa al menos una actividad en cualquiera de los 4 módulos base (no están disponibles con la app recién instalada, para dar sensación de progresión, pero tampoco exigen dominar los 4 módulos).

## 11. Arquitectura

MVVM + Repository Pattern sobre Jetpack Compose, con Room como única fuente de verdad. Ver `docs/MANUAL_TECNICO.md` para el detalle de capas, y `docs/BASE_DE_DATOS.md` para el modelo de datos.

## 12. Reglas de negocio relevantes

- Un ejercicio se considera "completado" únicamente cuando el usuario lo resuelve correctamente al menos una vez (los reintentos fallidos no lo marcan como completado, pero sí quedan en el historial).
- El XP de un acierto en el primer intento es mayor que el de un acierto tras reintentar; una respuesta incorrecta igualmente otorga 1 XP de participación (nunca 0 ni penalización negativa).
- Un módulo se considera "Dominado" solo si el usuario completó el 100% de su contenido con al menos 85% de aciertos.
- Las insignias se evalúan tras cada intento registrado, comparando un snapshot de estadísticas reales contra reglas puras en `GamificationEngine`; nunca se conceden manualmente ni por tiempo transcurrido.
- Los módulos avanzados (Constructor, Comparador, Detective, Desafíos) se desbloquean tras completar al menos una actividad de cualquier módulo base.

## 13. Experiencia de usuario (UX) para 9-13 años

Sesiones de 5 a 20 minutos, progreso guardado automáticamente (Room persiste tras cada intento), dificultad creciente dentro de cada módulo, retroalimentación siempre acompañada de una explicación educativa breve (nunca un simple "Correcto/Incorrecto"), y ausencia total de mecánicas de presión (no hay vidas con espera, ni rankings online, ni compras).

## 14. Privacidad y seguridad infantil

No se recopila correo, teléfono, dirección, ubicación ni contactos. No hay cámara, micrófono ni sensores en uso (la temática no los requiere). No hay `INTERNET`, backend, login ni analítica. Todo el almacenamiento es local vía Room/SQLite, privado a la instalación de la app.

## 15. Pruebas

73 pruebas unitarias JVM/Robolectric cubren `ChartMathEngine`, `GamificationEngine`, los convertidores de Room y la transacción completa de registro de intentos (incluyendo desbloqueo de insignias, cascadas de borrado e idempotencia del seeder), más 1 prueba instrumentada de humo (`MainActivitySmokeTest`) que verifica el arranque real de la app. Ver `docs/BUILD_REPORT.md` para el estado real de ejecución en este entorno.

## 16. Limitaciones conocidas

- El entorno de generación de este proyecto no tuvo Android SDK ni acceso de red a los repositorios de Gradle/Google/Maven, por lo que la compilación real (`assembleDebug`) no pudo verificarse aquí; ver `docs/BUILD_REPORT.md`.
- El drag-and-drop de reordenar categorías usa una implementación propia basada en `pointerInput` (sin librería externa); es funcional pero más simple que un motor de reordenamiento con animación de intercambio en tiempo real.
- El módulo Desafíos construye su cola de repaso solo a partir de los 4 módulos base (no incluye retos pendientes del Detective/Comparador), una simplificación documentada para mantener la sesión de repaso corta y enfocada.

## 17. Mejoras futuras

- Añadir más tipos de gráfico (áreas, dispersión) como módulos adicionales.
- Exportar/importar el progreso entre dispositivos vía archivo local (sin nube).
- Añadir modo "aula" opcional con progreso agregado anónimo para un docente, manteniendo el offline-first.
- Ampliar el catálogo de datasets semilla con contenido regional configurable.

## 18. Conclusiones

Gráficos Divertidos cumple el objetivo de ser una aplicación educativa completa —no un prototipo— sobre representación gráfica de datos: combina identidad visual propia, mecánicas de interacción variadas, progresión real basada en datos persistidos y una cobertura de pruebas sustancial, dentro de un diseño técnico offline-first apropiado para uso escolar con menores.
