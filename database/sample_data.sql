-- ============================================================================
-- Gráficos Divertidos — datos de ejemplo (subconjunto representativo)
-- El contenido semilla COMPLETO (30 datasets, 45 definiciones de gráfico,
-- 50 ejercicios, 30 retos del detective, 20 comparaciones, 10 insignias) se
-- genera y se inserta en tiempo de ejecución desde
-- data/local/seed/SeedContent.kt (ver tools/generate_seed_content.py).
-- Este archivo es una muestra ilustrativa del ESQUEMA con datos reales,
-- pensada para lectura humana y para pruebas manuales rápidas con sqlite3.
-- ============================================================================

PRAGMA foreign_keys = ON;

-- ---------------------------------------------------------------- perfil ---
INSERT INTO user_profile (id, alias, avatarKey, createdAt, lastOpenedAt, soundEnabled, hapticsEnabled, onboardingCompleted)
VALUES (1, 'Explorador', 'avatar_01', 1700000000000, 1700000000000, 1, 1, 1);

INSERT INTO user_stats (userId, totalXp, totalStars, currentStreak, bestStreak, exercisesCompleted, updatedAt)
VALUES (1, 42, 4, 2, 5, 6, 1700000500000);

-- -------------------------------------------------------------- datasets ---
INSERT INTO dataset (id, title, category, unit, iconKey, isSeed) VALUES
  (1, 'Mascotas de la clase', 'Escuela', 'estudiantes', 'profile', 1),
  (2, 'Snacks favoritos del recreo', 'Alimentación', 'votos', 'pictograms', 1),
  (3, 'Temperatura máxima de la semana', 'Clima', '°C', 'lines', 1),
  (4, 'Color favorito del salón', 'Preferencias', 'votos', 'pie', 1);

INSERT INTO data_point (datasetId, label, value, orderIndex) VALUES
  (1, 'Perros', 9, 0), (1, 'Gatos', 6, 1), (1, 'Peces', 4, 2), (1, 'Aves', 2, 3), (1, 'Conejos', 3, 4),
  (2, 'Fruta', 12, 0), (2, 'Galletas', 8, 1), (2, 'Yogur', 5, 2), (2, 'Frutos secos', 3, 3),
  (3, 'Lunes', 21, 0), (3, 'Martes', 23, 1), (3, 'Miércoles', 20, 2), (3, 'Jueves', 25, 3),
  (3, 'Viernes', 27, 4), (3, 'Sábado', 24, 5), (3, 'Domingo', 22, 6),
  (4, 'Azul', 10, 0), (4, 'Verde', 7, 1), (4, 'Rojo', 6, 2), (4, 'Morado', 5, 3), (4, 'Amarillo', 4, 4);

-- ------------------------------------------------------ definiciones -------
INSERT INTO chart_definition (id, datasetId, chartType, title, showLabels, showLegend, axisMax, moduleKey) VALUES
  (1, 1, 'BARRAS', 'Mascotas de la clase', 1, 1, NULL, 'BARRAS'),
  (2, 2, 'PICTOGRAMA', 'Snacks favoritos del recreo', 1, 1, NULL, 'PICTOGRAMAS'),
  (3, 3, 'LINEAS', 'Temperatura máxima de la semana', 1, 1, NULL, 'LINEAS'),
  (4, 4, 'CIRCULAR', 'Color favorito del salón', 1, 1, NULL, 'CIRCULAR');

-- ------------------------------------------------------------ ejercicios ---
INSERT INTO chart_exercise
  (id, chartDefinitionId, moduleKey, interactionType, prompt, correctAnswer, options, explanationCorrect, explanationIncorrect, difficulty)
VALUES
  (1, 1, 'BARRAS', 'SELECCION_EN_GRAFICO', 'Toca la barra con más estudiantes.',
   '0', '', '¡Correcto! «Perros» tiene el valor más alto: 9.',
   'Fíjate en cuál barra llega más arriba en el eje vertical.', 1),
  (2, 3, 'LINEAS', 'OPCION_MULTIPLE', 'En general, ¿la tendencia de la línea sube o baja?',
   '0', 'Sube§§Baja', 'La tendencia general sube de inicio a fin.',
   'Compara el primer punto con el último para ver la tendencia general.', 1);

-- ------------------------------------------------------------- intentos ----
INSERT INTO chart_attempt (exerciseId, userId, selectedAnswer, isCorrect, firstTry, attemptAt, xpAwarded)
VALUES (1, 1, '0', 1, 1, 1700000100000, 11);

-- --------------------------------------------- retos del detective ---------
INSERT INTO graph_error_challenge
  (id, datasetId, chartType, displayedTitle, errorType, axisMinOverride, unitPerIconOverride, omittedCategoryLabel, explanation, difficulty)
VALUES
  (1, 1, 'BARRAS', 'Mascotas de la clase', 'EJE_TRUNCADO', 1.7, NULL, NULL,
   'El eje no comienza en 0, así que las diferencias entre estudiantes parecen mucho más grandes de lo que son en realidad.', 1),
  (2, 2, 'PICTOGRAMA', 'Snacks favoritos del recreo', 'PICTOGRAMA_SIN_ESCALA', NULL, 0.0, NULL,
   'El pictograma no indica cuánto vale cada icono, así que es imposible saber la cantidad real que representa cada fila.', 2);

-- ------------------------------------------------- retos del comparador ----
INSERT INTO comparison_challenge (id, datasetId, chartTypeA, chartTypeB, question, betterSide, explanation, difficulty)
VALUES
  (1, 4, 'CIRCULAR', 'BARRAS', '¿Qué gráfico muestra mejor qué parte del total representa cada categoría?', 'A',
   'El gráfico circular muestra de un vistazo qué proporción del total ocupa cada parte.', 1);

-- ------------------------------------------------------------ progreso -----
INSERT INTO progress (userId, moduleKey, completedCount, totalCount, correctCount, attemptsCount, state, updatedAt)
VALUES (1, 'BARRAS', 1, 15, 1, 1, 'INICIADO', 1700000100000);

-- ------------------------------------------------------------- insignias ---
INSERT INTO badge (code, title, description, iconKey, criteriaText) VALUES
  ('badge_primer_grafico', 'Primer gráfico', 'Completaste tu primer reto de interpretación.', 'badge_primer_grafico', 'Completar 1 ejercicio'),
  ('badge_maestro_barras', 'Maestro de barras', 'Dominaste el módulo de gráficos de barras.', 'badge_maestro_barras', 'Dominar el módulo Barras'),
  ('badge_ojo_de_lince', 'Ojo de lince', 'Detectaste 10 gráficos con errores.', 'badge_ojo_de_lince', 'Resolver 10 retos del Detective'),
  ('badge_constructor_experto', 'Constructor experto', 'Construiste y guardaste 5 gráficos propios.', 'badge_constructor_experto', 'Guardar 5 gráficos en el Constructor'),
  ('badge_detective_grafico', 'Detective gráfico', 'Resolviste 20 casos de gráficos engañosos.', 'badge_detective_grafico', 'Resolver 20 retos del Detective'),
  ('badge_comparador_agudo', 'Comparador agudo', 'Elegiste correctamente 10 veces la mejor representación.', 'badge_comparador_agudo', 'Acertar 10 comparaciones'),
  ('badge_racha_5', 'Racha de 5', 'Lograste 5 aciertos seguidos sin fallar.', 'badge_racha_5', 'Racha de 5 aciertos'),
  ('badge_explorador_datos', 'Explorador de datos', 'Completaste 25 ejercicios de interpretación.', 'badge_explorador_datos', 'Completar 25 ejercicios'),
  ('badge_precision_total', 'Precisión total', 'Terminaste una sesión completa sin errores.', 'badge_precision_total', 'Sesión perfecta'),
  ('badge_leyenda_del_estudio', 'Leyenda del Estudio', 'Dominaste los cuatro módulos principales.', 'badge_leyenda_del_estudio', 'Dominar los 4 módulos base');

INSERT INTO user_badge (userId, badgeCode, unlockedAt) VALUES (1, 'badge_primer_grafico', 1700000100000);
