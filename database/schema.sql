-- ============================================================================
-- Gráficos Divertidos — esquema de base de datos (SQLite / Room)
-- Generado a partir de las entidades Room en
-- app/src/main/kotlin/.../data/local/entity/Entities.kt
-- ============================================================================

PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS user_profile (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    alias               TEXT    NOT NULL,
    avatarKey           TEXT    NOT NULL,
    createdAt           INTEGER NOT NULL,
    lastOpenedAt        INTEGER NOT NULL,
    soundEnabled        INTEGER NOT NULL DEFAULT 1,
    hapticsEnabled      INTEGER NOT NULL DEFAULT 1,
    onboardingCompleted INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS user_stats (
    userId              INTEGER PRIMARY KEY,
    totalXp             INTEGER NOT NULL DEFAULT 0,
    totalStars          INTEGER NOT NULL DEFAULT 0,
    currentStreak       INTEGER NOT NULL DEFAULT 0,
    bestStreak          INTEGER NOT NULL DEFAULT 0,
    exercisesCompleted  INTEGER NOT NULL DEFAULT 0,
    updatedAt           INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (userId) REFERENCES user_profile(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS dataset (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    title    TEXT    NOT NULL,
    category TEXT    NOT NULL,
    unit     TEXT    NOT NULL,
    iconKey  TEXT    NOT NULL,
    isSeed   INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS data_point (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    datasetId  INTEGER NOT NULL,
    label      TEXT    NOT NULL,
    value      REAL    NOT NULL,
    orderIndex INTEGER NOT NULL,
    FOREIGN KEY (datasetId) REFERENCES dataset(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_data_point_datasetId ON data_point(datasetId);

CREATE TABLE IF NOT EXISTS chart_definition (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    datasetId  INTEGER NOT NULL,
    chartType  TEXT    NOT NULL, -- BARRAS | PICTOGRAMA | LINEAS | CIRCULAR
    title      TEXT    NOT NULL,
    showLabels INTEGER NOT NULL DEFAULT 1,
    showLegend INTEGER NOT NULL DEFAULT 1,
    axisMax    REAL,
    moduleKey  TEXT    NOT NULL,
    FOREIGN KEY (datasetId) REFERENCES dataset(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_chart_definition_datasetId ON chart_definition(datasetId);
CREATE INDEX IF NOT EXISTS index_chart_definition_moduleKey ON chart_definition(moduleKey);

CREATE TABLE IF NOT EXISTS chart_exercise (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    chartDefinitionId   INTEGER NOT NULL,
    moduleKey           TEXT    NOT NULL,
    interactionType     TEXT    NOT NULL, -- SELECCION_EN_GRAFICO | ORDENAR_CATEGORIAS | ESTIMAR_VALOR | COMPARAR_PUNTOS | OPCION_MULTIPLE
    prompt              TEXT    NOT NULL,
    correctAnswer       TEXT    NOT NULL, -- lista de enteros separada por comas
    options             TEXT    NOT NULL, -- lista de textos separada por "§§"
    explanationCorrect  TEXT    NOT NULL,
    explanationIncorrect TEXT   NOT NULL,
    difficulty          INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (chartDefinitionId) REFERENCES chart_definition(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_chart_exercise_chartDefinitionId ON chart_exercise(chartDefinitionId);
CREATE INDEX IF NOT EXISTS index_chart_exercise_moduleKey ON chart_exercise(moduleKey);

CREATE TABLE IF NOT EXISTS chart_attempt (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    exerciseId     INTEGER NOT NULL,
    userId         INTEGER NOT NULL,
    selectedAnswer TEXT    NOT NULL,
    isCorrect      INTEGER NOT NULL,
    firstTry       INTEGER NOT NULL,
    attemptAt      INTEGER NOT NULL,
    xpAwarded      INTEGER NOT NULL,
    FOREIGN KEY (exerciseId) REFERENCES chart_exercise(id) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES user_profile(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_chart_attempt_exerciseId ON chart_attempt(exerciseId);
CREATE INDEX IF NOT EXISTS index_chart_attempt_userId ON chart_attempt(userId);

CREATE TABLE IF NOT EXISTS chart_configuration (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    userId        INTEGER NOT NULL,
    datasetId     INTEGER NOT NULL,
    chartType     TEXT    NOT NULL,
    title         TEXT    NOT NULL,
    categoryOrder TEXT    NOT NULL,
    showLabels    INTEGER NOT NULL,
    showLegend    INTEGER NOT NULL,
    axisMax       REAL,
    createdAt     INTEGER NOT NULL,
    FOREIGN KEY (datasetId) REFERENCES dataset(id) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES user_profile(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_chart_configuration_datasetId ON chart_configuration(datasetId);
CREATE INDEX IF NOT EXISTS index_chart_configuration_userId ON chart_configuration(userId);

CREATE TABLE IF NOT EXISTS graph_error_challenge (
    id                   INTEGER PRIMARY KEY AUTOINCREMENT,
    datasetId            INTEGER NOT NULL,
    chartType            TEXT    NOT NULL,
    displayedTitle       TEXT    NOT NULL,
    errorType            TEXT    NOT NULL, -- EJE_TRUNCADO | ESCALA_INCONSISTENTE | DATOS_FALTANTES | TITULO_ENGANOSO | CATEGORIA_INCORRECTA | PICTOGRAMA_SIN_ESCALA
    axisMinOverride      REAL,
    unitPerIconOverride  REAL,
    omittedCategoryLabel TEXT,
    explanation          TEXT    NOT NULL,
    difficulty           INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (datasetId) REFERENCES dataset(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_graph_error_challenge_datasetId ON graph_error_challenge(datasetId);

CREATE TABLE IF NOT EXISTS graph_error_attempt (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    challengeId       INTEGER NOT NULL,
    userId            INTEGER NOT NULL,
    selectedErrorType TEXT    NOT NULL,
    isCorrect         INTEGER NOT NULL,
    attemptAt         INTEGER NOT NULL,
    xpAwarded         INTEGER NOT NULL,
    FOREIGN KEY (challengeId) REFERENCES graph_error_challenge(id) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES user_profile(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_graph_error_attempt_challengeId ON graph_error_attempt(challengeId);
CREATE INDEX IF NOT EXISTS index_graph_error_attempt_userId ON graph_error_attempt(userId);

CREATE TABLE IF NOT EXISTS comparison_challenge (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    datasetId   INTEGER NOT NULL,
    chartTypeA  TEXT    NOT NULL,
    chartTypeB  TEXT    NOT NULL,
    question    TEXT    NOT NULL,
    betterSide  TEXT    NOT NULL, -- 'A' o 'B'
    explanation TEXT    NOT NULL,
    difficulty  INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (datasetId) REFERENCES dataset(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_comparison_challenge_datasetId ON comparison_challenge(datasetId);

CREATE TABLE IF NOT EXISTS comparison_attempt (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    challengeId  INTEGER NOT NULL,
    userId       INTEGER NOT NULL,
    selectedSide TEXT    NOT NULL,
    isCorrect    INTEGER NOT NULL,
    attemptAt    INTEGER NOT NULL,
    xpAwarded    INTEGER NOT NULL,
    FOREIGN KEY (challengeId) REFERENCES comparison_challenge(id) ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES user_profile(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_comparison_attempt_challengeId ON comparison_attempt(challengeId);
CREATE INDEX IF NOT EXISTS index_comparison_attempt_userId ON comparison_attempt(userId);

CREATE TABLE IF NOT EXISTS progress (
    userId         INTEGER NOT NULL,
    moduleKey      TEXT    NOT NULL, -- BARRAS | PICTOGRAMAS | LINEAS | CIRCULAR | CONSTRUCTOR | COMPARADOR | DETECTIVE | DESAFIOS
    completedCount INTEGER NOT NULL DEFAULT 0,
    totalCount     INTEGER NOT NULL DEFAULT 0,
    correctCount   INTEGER NOT NULL DEFAULT 0,
    attemptsCount  INTEGER NOT NULL DEFAULT 0,
    state          TEXT    NOT NULL DEFAULT 'DISPONIBLE', -- BLOQUEADO | DISPONIBLE | INICIADO | COMPLETADO | DOMINADO
    updatedAt      INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (userId, moduleKey),
    FOREIGN KEY (userId) REFERENCES user_profile(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_progress_userId ON progress(userId);

CREATE TABLE IF NOT EXISTS badge (
    code         TEXT PRIMARY KEY,
    title        TEXT NOT NULL,
    description  TEXT NOT NULL,
    iconKey      TEXT NOT NULL,
    criteriaText TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS user_badge (
    userId     INTEGER NOT NULL,
    badgeCode  TEXT    NOT NULL,
    unlockedAt INTEGER NOT NULL,
    PRIMARY KEY (userId, badgeCode),
    FOREIGN KEY (userId) REFERENCES user_profile(id) ON DELETE CASCADE,
    FOREIGN KEY (badgeCode) REFERENCES badge(code) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_user_badge_userId ON user_badge(userId);
CREATE INDEX IF NOT EXISTS index_user_badge_badgeCode ON user_badge(badgeCode);
