# Build Report — Gráficos Divertidos v1.0.0

## Stack declarado

| Componente | Versión |
|---|---|
| Kotlin | 2.0.21 |
| AGP | 8.5.2 |
| Gradle Wrapper | 8.7 |
| KSP | 2.0.21-1.0.28 |
| Compose BOM | 2024.09.00 |
| Room | 2.6.1 |
| Navigation Compose | 2.8.0 |
| Coroutines | 1.8.1 |
| JDK objetivo | 17 |
| minSdk / compileSdk / targetSdk | 24 / 34 / 34 |

## Entorno real de generación de este proyecto

Este proyecto se escribió en un contenedor Linux en la nube con las siguientes características verificadas:

- **JDK:** OpenJDK 21.0.10 instalado y funcional (`java -version` ejecutado con éxito).
- **Gradle:** 8.14.3 instalado y funcional (`gradle -v` ejecutado con éxito).
- **Android SDK:** **no instalado.** No existe `ANDROID_HOME`/`ANDROID_SDK_ROOT`, ni `/opt/android-sdk`, ni `~/Android/Sdk`. No hay `sdkmanager` disponible.
- **Acceso de red:** restringido por allowlist a un conjunto reducido de hosts (registro npm, PyPI, algunos más). Se verificó explícitamente con `curl` que `dl.google.com`, `services.gradle.org` y `repo.maven.apache.org` devuelven **403 Forbidden** a través del proxy de este entorno, tanto en acceso directo como a través del proxy HTTP configurado.

Con esta combinación (sin Android SDK **y** sin acceso a los repositorios de Google/Maven), es **estructuralmente imposible** resolver el plugin `com.android.application` u obtener el SDK/AGP/Compose/Room desde este contenedor. No es una limitación de configuración del proyecto, sino del entorno de red en el que se generó.

## Resultado real de la ejecución

Se generó el wrapper de Gradle real con `gradle wrapper --gradle-version 8.7` (verificado funcional: `gradlew`/`gradlew.bat`/`gradle-wrapper.jar` presentes y ejecutables) y se intentó la secuencia solicitada:

```
$ gradle clean --offline
```

Resultado real (log completo también en `build_logs/clean.log` dentro del proyecto de trabajo, no incluido en el ZIP de entrega para no confundirlo con un artefacto de build):

```
FAILURE: Build failed with an exception.

* Where:
Build file '.../build.gradle.kts' line: 2

* What went wrong:
Plugin [id: 'com.android.application', version: '8.5.2', apply: false] was not found in any of the following sources:
- Gradle Core Plugins (plugin is not in 'org.gradle' namespace)
- Included Builds (No included builds contain this plugin)
- Plugin Repositories (could not resolve plugin artifact 'com.android.application:com.android.application.gradle.plugin:8.5.2')
  Searched in the following repositories:
    Google
    MavenRepo
    Gradle Central Plugin Repository

BUILD FAILED in 829ms
```

Como consecuencia, **no se pudo ejecutar** `testDebugUnitTest`, `lintDebug` ni `assembleDebug`: todos dependen de que el plugin de Android se resuelva primero, y eso requiere red hacia `google()`/`mavenCentral()` que este entorno bloquea.

| Paso | Estado |
|---|---|
| `./gradlew clean` | ❌ Falla en la resolución del plugin AGP (ver log arriba) |
| `./gradlew testDebugUnitTest` | **NO EJECUTADO** (no alcanzable sin el paso anterior) |
| `./gradlew lintDebug` | **NO EJECUTADO** |
| `./gradlew assembleDebug` | **NO EJECUTADO** |

## COMPILACIÓN NO VERIFICADA

De acuerdo con la regla de honestidad del proyecto, se declara explícitamente: **COMPILACIÓN NO VERIFICADA**. No se generó ningún APK real. No existe `app/build/outputs/apk/debug/app-debug.apk`. No se calculó ningún SHA-256 porque no hay archivo que hashear — cualquier valor que se hubiera reportado aquí habría sido inventado.

Tests aprobados/fallidos: **no ejecutados, por lo tanto no reportables como aprobados**. Se escribieron 73 pruebas unitarias (30 en `ChartMathEngineTest`, 20 en `GamificationEngineTest`, 7 en `ConvertersTest`, 8 en `DatabaseSeederTest` y 8 en `ProgressRepositoryTest`, estas dos últimas usando Room en memoria sobre Robolectric), pensadas y revisadas para pasar en un entorno estándar con Android Studio/JDK 17/Android SDK, pero su resultado real solo puede confirmarse compilando en una máquina con esas herramientas o mediante el workflow `.github/workflows/build.yml` en GitHub Actions.

## Revisión estática de compilación (sustituto parcial)

Ante la imposibilidad de compilar, se hizo una revisión de código exhaustiva orientada a detectar errores que un compilador Kotlin señalaría (referencias sin resolver, imports faltantes, incoherencias de API, problemas de scope en Compose, inconsistencias en el catálogo de versiones de Gradle, referencias `R.drawable.*` sin recurso correspondiente, anotaciones Room). Se encontraron y corrigieron 10 imports de Compose faltantes (`Modifier.weight/width/height` y un choque de `items` entre `foundation.lazy` y `foundation.lazy.grid`) repartidos en 8 archivos de UI. Tras la corrección, la revisión no encontró más referencias sin resolver, inconsistencias de Room (entidades/DAOs/TypeConverters/claves foráneas) ni discrepancias entre el catálogo de versiones y `app/build.gradle.kts`. Esto reduce el riesgo de fallos de compilación, pero **no reemplaza una compilación real**, que sigue sin poder verificarse en este entorno.

## PDFs de documentación

Generados realmente (no simulados) con `pandoc` + `wkhtmltopdf`, verificados abriendo cada archivo con `pypdf` y confirmando páginas y texto extraíble con acentos correctos:

| Archivo | Páginas |
|---|---|
| `docs/pdf/MEMORIA_DESCRIPTIVA.pdf` | 5 |
| `docs/pdf/MANUAL_USUARIO.pdf` | 3 |
| `docs/pdf/MANUAL_TECNICO.pdf` | 4 |
| `docs/pdf/BASE_DE_DATOS.pdf` | 3 (incluye el diagrama entidad-relación renderizado como imagen) |

## Limitaciones de esta entrega

- **No hay APK.** El ZIP de código fuente permite compilar el APK en un entorno con Android Studio/SDK, o automáticamente vía `.github/workflows/build.yml` al hacer push a GitHub.
- **Los 73 tests no se ejecutaron realmente.** Se escribieron con cuidado (incluyendo pruebas de Room en memoria vía Robolectric para validar transacciones, cascadas de borrado e idempotencia del seeder) pero su aprobación real está pendiente de un entorno con SDK.
- Ninguna cifra de compilación, test o hash fue inventada en este documento.
