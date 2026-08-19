# Gráficos Divertidos

**Estudio de Visualización** — una app educativa Android nativa (Kotlin + Jetpack Compose) que enseña estadística y representación gráfica a niños y niñas de 9 a 13 años. Grafi, el asistente geométrico, guía al usuario mientras interpreta, compara, detecta errores y construye sus propios gráficos: barras, pictogramas, líneas y circulares.

- **Paquete:** `com.educalab.graficosdivertidos`
- **Versión:** 1.0.0
- **Mínimo Android:** 7.0 (API 24) · **Compilado con:** API 34
- **100% offline**, sin cuentas, sin anuncios, sin datos personales.

## Concepto

La Home es un **Estudio** (no una lista de botones): un mapa de 8 estaciones —Barras, Pictogramas, Líneas, Circular, Constructor, Comparador, Detective y Desafíos— con progreso visual, XP, racha e insignias. Los gráficos se dibujan con Jetpack Compose Canvas y se animan al construirse; las ilustraciones (Grafi, portada, iconos de módulo, insignias, avatares) son recursos locales generados específicamente para esta app (ver `tools/generate_art.py`), sin ninguna dependencia de imágenes externas.

## Módulos

1. **Perfil/Estudio** — alias, avatar (8 opciones) y estadísticas.
2. **Barras** — comparar alturas.
3. **Pictogramas** — contar con iconos y escala.
4. **Líneas** — leer tendencias en el tiempo.
5. **Circular** — leer partes de un total.
6. **Constructor** — wizard de 7 pasos: dataset → tipo → título → categorías → etiquetas → escala → leyenda → vista previa animada, con guardado real en Room.
7. **Comparador** — dos representaciones del mismo dataset; elegir cuál comunica mejor y por qué.
8. **Detective de gráficos engañosos** — identificar el tipo de error entre 6 posibles (eje truncado, escala inconsistente, datos faltantes, título engañoso, categoría incorrecta, pictograma sin escala).
9. **Desafíos** — cola de repaso mixta con los retos pendientes de los módulos base.
10. **Galería de logros** — progreso por módulo, insignias y estadísticas.

Las mecánicas combinan selección directa sobre el gráfico, arrastrar para ordenar, estimar un valor con slider, comparar dos puntos y opción múltiple — menos de la mitad de la experiencia principal es opción múltiple, tal como exige la especificación del proyecto.

## Contenido semilla

- 30 datasets temáticos (mascotas, snacks, clima, deportes, lectura, música, naturaleza…) con datos reales y variados.
- 45 definiciones de gráfico y 50 ejercicios interpretativos.
- 30 gráficos deliberadamente problemáticos para el Detective.
- 20 retos de comparación.
- 10 insignias con criterios de desbloqueo reales (ver `domain/logic/GamificationEngine.kt`).

Todo el contenido se genera con `tools/generate_seed_content.py` hacia `data/local/seed/SeedContent.kt`, y se inserta en Room la primera vez que se abre la app (`DatabaseSeeder`).

## Arquitectura

```
app/src/main/kotlin/com/educalab/graficosdivertidos/
├── data/
│   ├── local/          Room: entidades, DAOs, converters, AppDatabase, seed
│   └── repository/      Repositorios (Profile, Content, Progress, Builder)
├── domain/
│   ├── model/            Modelos de dominio puros (sin Android)
│   └── logic/            ChartMathEngine y GamificationEngine (testeables con JUnit)
└── ui/
    ├── theme/, components/, navigation/
    └── screens/            onboarding, home, module, builder, comparator, detective, gallery, profile
```

MVVM + Repository Pattern, Room + Coroutines/Flow, Navigation Compose, Material 3. Sin Hilt (inyección manual desde `GraficosDivertidosApp`) para mantener el build simple. `ChartMathEngine` (escalado de ejes, normalización a porcentajes, sectores circulares, conteo de pictogramas, coordenadas de líneas) y `GamificationEngine` (niveles, rachas, insignias, estado de módulo) son Kotlin puro, sin dependencias de Android, y están cubiertos por pruebas JUnit normales.

## Compilación local

```bash
./gradlew clean
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
```

El APK debug queda en `app/build/outputs/apk/debug/app-debug.apk`.

> **Nota sobre este entorno de generación:** el proyecto se construyó en un contenedor en la nube sin Android SDK instalado y sin acceso de red a `google()`/`mavenCentral()` (únicamente a un conjunto reducido de registros como npm/pypi). Por lo tanto, aquí **no fue posible ejecutar realmente Gradle**. El código se escribió y se revisó cuidadosamente para compilar en un entorno estándar con Android Studio / JDK 17 / Android SDK, pero la compilación real solo puede verificarse en una máquina con esas herramientas instaladas (o mediante el workflow de GitHub Actions incluido en `.github/workflows/build.yml`). Ver `docs/BUILD_REPORT.md` para el detalle honesto de qué se pudo y no se pudo verificar.

## Documentación

- `docs/MEMORIA_DESCRIPTIVA.md` — memoria completa del proyecto.
- `docs/MANUAL_USUARIO.md` — manual para quien juega/usa la app.
- `docs/MANUAL_TECNICO.md` — manual para quien mantiene el código.
- `docs/BASE_DE_DATOS.md` — modelo de datos y DER.
- `docs/BUILD_REPORT.md` — resultado real (honesto) de la compilación en este entorno.
- `database/schema.sql` y `database/sample_data.sql` — esquema SQL y datos de ejemplo.
- PDFs equivalentes en `docs/pdf/`.

## Privacidad

Sin `INTERNET`, sin Firebase/backend/APIs remotas, sin login, sin anuncios, sin analítica. No se solicita correo, teléfono, dirección ni ubicación. El alias y avatar son locales y ficticios por diseño.
