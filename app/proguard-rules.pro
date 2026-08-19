# Reglas ProGuard/R8 para Gráficos Divertidos.
# La app no minifica en debug; estas reglas cubren un futuro build de release.

# Room genera código en tiempo de compilación; se conservan las entidades y DAOs.
-keep class com.educalab.graficosdivertidos.data.local.entity.** { *; }
-keep class com.educalab.graficosdivertidos.data.local.dao.** { *; }

# Modelos de dominio usados por serialización manual del seeder.
-keep class com.educalab.graficosdivertidos.domain.model.** { *; }
