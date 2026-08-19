#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Genera app/src/main/kotlin/.../data/local/seed/SeedContent.kt a partir de
definiciones de datos declaradas aquí en Python. Mantener el contenido
educativo en un generador (en vez de escrito a mano dentro de un ViewModel o
repetido cientos de veces en Kotlin) es lo que exige la especificación del
proyecto en el punto 24 (datos semilla mantenibles).

Ejecutar: python3 tools/generate_seed_content.py
"""
import os
import json

OUT_PATH = os.path.join(
    os.path.dirname(__file__), "..",
    "app/src/main/kotlin/com/educalab/graficosdivertidos/data/local/seed/SeedContent.kt",
)

# ============================================================ DATASETS ====
# (key, title, category, unit, iconKey, [(label, value), ...], tags)
# tags: "composicion" -> apto para circular/comparador de partes de un todo
#       "serie"       -> apto para líneas (evolución en el tiempo)
#       "conteo"      -> apto para pictograma (cantidades enteras pequeñas)
DATASETS = [
    ("mascotas_clase", "Mascotas de la clase", "Escuela", "estudiantes", "profile",
     [("Perros", 9), ("Gatos", 6), ("Peces", 4), ("Aves", 2), ("Conejos", 3)], ["conteo", "composicion"]),
    ("snacks_favoritos", "Snacks favoritos del recreo", "Alimentación", "votos", "pictograms",
     [("Fruta", 12), ("Galletas", 8), ("Yogur", 5), ("Frutos secos", 3)], ["conteo", "composicion"]),
    ("horas_pantalla", "Horas de pantalla por día", "Hábitos", "horas", "lines",
     [("Lunes", 1.5), ("Martes", 2.0), ("Miércoles", 1.0), ("Jueves", 2.5), ("Viernes", 3.0), ("Sábado", 4.0), ("Domingo", 3.5)], ["serie"]),
    ("libros_leidos", "Libros leídos por mes", "Lectura", "libros", "lines",
     [("Marzo", 2), ("Abril", 3), ("Mayo", 1), ("Junio", 4), ("Julio", 5), ("Agosto", 3)], ["serie"]),
    ("clima_semana", "Temperatura máxima de la semana", "Clima", "°C", "lines",
     [("Lunes", 21), ("Martes", 23), ("Miércoles", 20), ("Jueves", 25), ("Viernes", 27), ("Sábado", 24), ("Domingo", 22)], ["serie"]),
    ("excursiones", "Estudiantes en cada excursión", "Escuela", "estudiantes", "bars",
     [("Museo", 28), ("Granja", 24), ("Planetario", 30), ("Playa", 26)], ["conteo"]),
    ("colores_favoritos", "Color favorito del salón", "Preferencias", "votos", "pie",
     [("Azul", 10), ("Verde", 7), ("Rojo", 6), ("Morado", 5), ("Amarillo", 4)], ["composicion", "conteo"]),
    ("medallas_curso", "Medallas ganadas por curso", "Deportes", "medallas", "bars",
     [("4°A", 6), ("4°B", 9), ("5°A", 7), ("5°B", 11), ("6°A", 8)], ["conteo"]),
    ("animales_zoo", "Animales observados en el zoológico", "Naturaleza", "avistamientos", "pictograms",
     [("Leones", 4), ("Jirafas", 6), ("Monos", 10), ("Elefantes", 3), ("Loros", 8)], ["conteo"]),
    ("instrumentos_banda", "Instrumentos en la banda escolar", "Música", "estudiantes", "bars",
     [("Flauta", 8), ("Tambor", 6), ("Trompeta", 5), ("Xilófono", 4), ("Violín", 3)], ["conteo", "composicion"]),
    ("visitas_biblioteca", "Visitas a la biblioteca por semana", "Lectura", "visitas", "lines",
     [("Semana 1", 40), ("Semana 2", 55), ("Semana 3", 48), ("Semana 4", 62), ("Semana 5", 58)], ["serie"]),
    ("transporte_cole", "Cómo llegan al colegio", "Hábitos", "estudiantes", "pie",
     [("A pie", 14), ("Bicicleta", 6), ("Bus escolar", 18), ("Auto familiar", 10)], ["composicion", "conteo"]),
    ("horas_sueno", "Horas de sueño por noche", "Hábitos", "horas", "lines",
     [("Lunes", 9), ("Martes", 8.5), ("Miércoles", 9.5), ("Jueves", 8), ("Viernes", 10), ("Sábado", 10.5), ("Domingo", 9)], ["serie"]),
    ("frutas_mercado", "Frutas disponibles en el mercado escolar", "Alimentación", "cajas", "pictograms",
     [("Manzanas", 15), ("Plátanos", 20), ("Naranjas", 10), ("Peras", 5)], ["conteo"]),
    ("videojuegos_jugados", "Tipo de videojuego preferido", "Preferencias", "votos", "bars",
     [("Aventura", 11), ("Deportes", 8), ("Rompecabezas", 6), ("Carreras", 9), ("Construcción", 7)], ["conteo"]),
    ("paises_visitados", "Países visitados por la clase (votos)", "Geografía", "estudiantes", "pie",
     [("Perú", 12), ("Chile", 5), ("Argentina", 4), ("México", 3), ("España", 2)], ["composicion", "conteo"]),
    ("huerto_escolar", "Plantas del huerto escolar", "Naturaleza", "plantas", "pictograms",
     [("Tomates", 12), ("Lechugas", 18), ("Zanahorias", 9), ("Fresas", 6)], ["conteo"]),
    ("reciclaje_material", "Reciclaje por material (kg)", "Medio ambiente", "kg", "bars",
     [("Papel", 34), ("Plástico", 21), ("Vidrio", 15), ("Metal", 8)], ["conteo", "composicion"]),
    ("goles_equipo", "Goles marcados por equipo", "Deportes", "goles", "lines",
     [("Partido 1", 1), ("Partido 2", 3), ("Partido 3", 2), ("Partido 4", 4), ("Partido 5", 2), ("Partido 6", 5)], ["serie"]),
    ("minutos_tarea", "Minutos de tarea por materia", "Escuela", "minutos", "bars",
     [("Matemática", 30), ("Comunicación", 25), ("Ciencia", 20), ("Arte", 10), ("Inglés", 15)], ["conteo"]),
    ("ahorro_semanal", "Monedas ahorradas por semana", "Hábitos", "monedas", "lines",
     [("Semana 1", 4), ("Semana 2", 6), ("Semana 3", 5), ("Semana 4", 9), ("Semana 5", 8), ("Semana 6", 12)], ["serie"]),
    ("nubes_observadas", "Tipos de nubes observadas", "Clima", "días", "pictograms",
     [("Cúmulos", 10), ("Estratos", 6), ("Cirros", 8), ("Nimbos", 4)], ["conteo"]),
    ("insectos_patio", "Insectos encontrados en el patio", "Naturaleza", "insectos", "pictograms",
     [("Hormigas", 25), ("Mariposas", 8), ("Escarabajos", 6), ("Abejas", 5)], ["conteo"]),
    ("monedas_coleccion", "Monedas coleccionadas por país", "Colecciones", "monedas", "bars",
     [("Perú", 14), ("Colombia", 9), ("Ecuador", 7), ("Bolivia", 5), ("Chile", 11)], ["conteo"]),
    ("canciones_recreo", "Canciones más pedidas en el recreo", "Música", "votos", "pie",
     [("Pop", 16), ("Cumbia", 10), ("Rock", 6), ("Reggaetón", 8)], ["composicion", "conteo"]),
    ("mascotas_tipo", "Mascotas por tipo en el barrio", "Naturaleza", "mascotas", "bars",
     [("Perros", 22), ("Gatos", 18), ("Hámsters", 6), ("Tortugas", 4), ("Peces", 9)], ["conteo"]),
    ("estrellas_equipo", "Estrellas ganadas por equipo", "Gamificación", "estrellas", "lines",
     [("Reto 1", 2), ("Reto 2", 3), ("Reto 3", 3), ("Reto 4", 5), ("Reto 5", 4), ("Reto 6", 6)], ["serie"]),
    ("planetas_favoritos", "Planeta favorito (votos)", "Ciencia", "votos", "pie",
     [("Marte", 13), ("Saturno", 11), ("Júpiter", 8), ("Tierra", 6), ("Neptuno", 4)], ["composicion", "conteo"]),
    ("historietas_leidas", "Historietas leídas por bimestre", "Lectura", "historietas", "lines",
     [("Bim. 1", 3), ("Bim. 2", 5), ("Bim. 3", 4), ("Bim. 4", 7), ("Bim. 5", 6)], ["serie"]),
    ("frutas_loncheras", "Frutas en las loncheras", "Alimentación", "loncheras", "pictograms",
     [("Manzana", 14), ("Plátano", 11), ("Uvas", 7), ("Fresas", 5)], ["conteo"]),
]

assert len(DATASETS) == 30, f"Se esperaban 30 datasets, hay {len(DATASETS)}"

DATASET_BY_KEY = {d[0]: d for d in DATASETS}


def by_tag(tag):
    return [d[0] for d in DATASETS if tag in d[6]]


# ================================================= CHART DEFINITIONS =======
# Cuatro módulos de tipo fijo: barras, pictogramas, líneas, circular.
CHART_TYPE_MAP = {
    "BARRAS": "BARRAS", "PICTOGRAMA": "PICTOGRAMA", "LINEAS": "LINEAS", "CIRCULAR": "CIRCULAR",
}

# Objetivo: 50 ejercicios repartidos en los 4 módulos de tipo fijo.
# BARRAS y PICTOGRAMAS tienen suficientes datasets propios (18 y 21 posibles);
# LINEAS y CIRCULAR solo cuentan con 9 datasets etiquetados como "serie" o
# "composicion" respectivamente, así que algunos datasets aportan 2 ejercicios
# (con mecánicas distintas) para completar el objetivo sin repetir contenido
# de forma idéntica.
BARRAS_TARGET = 15
PICTOGRAMAS_TARGET = 12
LINEAS_TARGET = 11
CIRCULAR_TARGET = 12

bar_datasets = [d[0] for d in DATASETS if "serie" not in d[6]][:BARRAS_TARGET]
picto_datasets = by_tag("conteo")[:PICTOGRAMAS_TARGET]
line_pool = by_tag("serie")
pie_pool = by_tag("composicion")
line_datasets = [line_pool[i % len(line_pool)] for i in range(LINEAS_TARGET)]
pie_datasets = [pie_pool[i % len(pie_pool)] for i in range(CIRCULAR_TARGET)]

chart_defs = []
_chart_def_keys_seen = set()
exercises = []


def add_def(key, dataset_key, chart_type, title, module_key, axis_max=None):
    if key in _chart_def_keys_seen:
        return
    _chart_def_keys_seen.add(key)
    chart_defs.append({
        "key": key, "datasetKey": dataset_key, "chartType": chart_type,
        "title": title, "moduleKey": module_key, "axisMax": axis_max,
    })


def cat_labels(dataset_key):
    return [p[0] for p in DATASET_BY_KEY[dataset_key][5]]


def cat_values(dataset_key):
    return [p[1] for p in DATASET_BY_KEY[dataset_key][5]]


def add_exercise(defkey, module_key, itype, prompt, correct, options, ok, bad, diff=1):
    exercises.append({
        "chartDefinitionKey": defkey, "moduleKey": module_key, "interactionType": itype,
        "prompt": prompt, "correctAnswer": correct, "options": options,
        "explanationCorrect": ok, "explanationIncorrect": bad, "difficulty": diff,
    })


def argmax(values):
    return max(range(len(values)), key=lambda i: values[i])


def argmin(values):
    return min(range(len(values)), key=lambda i: values[i])


# ---- BARRAS (14 datasets -> 14 exercises, variando mecánica) ----
bar_interactions = ["SELECCION_EN_GRAFICO", "COMPARAR_PUNTOS", "ORDENAR_CATEGORIAS", "ESTIMAR_VALOR", "OPCION_MULTIPLE"]
for i, dkey in enumerate(bar_datasets):
    title = DATASET_BY_KEY[dkey][1]
    defkey = f"def_barras_{dkey}"
    add_def(defkey, dkey, "BARRAS", title, "BARRAS")
    labels = cat_labels(dkey)
    values = cat_values(dkey)
    itype = bar_interactions[i % len(bar_interactions)]
    if itype == "SELECCION_EN_GRAFICO":
        idx = argmax(values)
        add_exercise(defkey, "BARRAS", itype,
                     f"Toca la barra con más {DATASET_BY_KEY[dkey][3]}.",
                     [idx], [],
                     f"¡Correcto! «{labels[idx]}» tiene el valor más alto: {values[idx]:g}.",
                     "Fíjate en cuál barra llega más arriba en el eje vertical.", 1)
    elif itype == "COMPARAR_PUNTOS":
        a, b = 0, 1
        winner = 0 if values[a] >= values[b] else 1
        add_exercise(defkey, "BARRAS", itype,
                     f"¿Cuál tiene más: {labels[a]} o {labels[b]}?",
                     [winner], [labels[a], labels[b]],
                     f"«{labels[winner]}» tiene más ({values[winner]:g} contra {values[1-winner]:g}).",
                     "Compara la altura de ambas barras con cuidado.", 1)
    elif itype == "ORDENAR_CATEGORIAS":
        order = sorted(range(len(values)), key=lambda i: values[i], reverse=True)
        add_exercise(defkey, "BARRAS", itype,
                     "Ordena las categorías de mayor a menor.",
                     order, labels,
                     "¡Bien hecho! Ordenaste las barras de la más alta a la más baja.",
                     "Recuerda: empieza por la barra más alta y termina en la más baja.", 2)
    elif itype == "ESTIMAR_VALOR":
        idx = argmax(values)
        add_exercise(defkey, "BARRAS", itype,
                     f"Estima el valor de «{labels[idx]}» ({DATASET_BY_KEY[dkey][3]}).",
                     [int(round(values[idx] * 100))], [DATASET_BY_KEY[dkey][3]],
                     f"El valor real es {values[idx]:g}. ¡Buena estimación!",
                     f"El valor real era {values[idx]:g}. Observa la altura respecto al eje.", 2)
    else:
        idx = argmin(values)
        wrong_opts = [labels[j] for j in range(len(labels)) if j != idx][:2]
        options = [labels[idx]] + wrong_opts
        add_exercise(defkey, "BARRAS", itype,
                     "¿Cuál categoría tiene el valor más bajo?",
                     [0], options,
                     f"«{labels[idx]}» es la más baja, con {values[idx]:g}.",
                     "Busca la barra más corta del gráfico.", 1)

# ---- PICTOGRAMAS (12 datasets -> 12 exercises) ----
picto_interactions = ["SELECCION_EN_GRAFICO", "ESTIMAR_VALOR", "COMPARAR_PUNTOS", "OPCION_MULTIPLE"]
for i, dkey in enumerate(picto_datasets):
    title = DATASET_BY_KEY[dkey][1]
    defkey = f"def_picto_{dkey}"
    add_def(defkey, dkey, "PICTOGRAMA", title, "PICTOGRAMAS")
    labels = cat_labels(dkey)
    values = cat_values(dkey)
    itype = picto_interactions[i % len(picto_interactions)]
    if itype == "SELECCION_EN_GRAFICO":
        idx = argmax(values)
        add_exercise(defkey, "PICTOGRAMAS", itype,
                     f"Toca la fila de iconos con más {DATASET_BY_KEY[dkey][3]}.",
                     [idx], [],
                     f"«{labels[idx]}» tiene la fila con más iconos ({values[idx]:g}).",
                     "Cuenta los iconos completos de cada fila; la escala está en la leyenda.", 1)
    elif itype == "ESTIMAR_VALOR":
        idx = argmin(values)
        add_exercise(defkey, "PICTOGRAMAS", itype,
                     f"¿Cuánto vale aproximadamente «{labels[idx]}»?",
                     [int(round(values[idx] * 100))], [DATASET_BY_KEY[dkey][3]],
                     f"El valor real es {values[idx]:g}, contando cada icono según la escala.",
                     f"Recuerda multiplicar el número de iconos por el valor de la escala. Era {values[idx]:g}.", 2)
    elif itype == "COMPARAR_PUNTOS":
        a, b = 0, len(labels) - 1
        winner = 0 if values[a] >= values[b] else 1
        add_exercise(defkey, "PICTOGRAMAS", itype,
                     f"¿Cuál tiene más iconos: {labels[a]} o {labels[b]}?",
                     [winner], [labels[a], labels[b]],
                     f"«{labels[winner]}» tiene más filas de iconos.",
                     "Cuenta cuántos iconos completos tiene cada fila.", 1)
    else:
        idx = argmax(values)
        wrong = [labels[j] for j in range(len(labels)) if j != idx][:2]
        options = [labels[idx]] + wrong
        add_exercise(defkey, "PICTOGRAMAS", itype,
                     "¿Qué categoría necesita más iconos para representarse?",
                     [0], options,
                     f"«{labels[idx]}» tiene el valor más alto, por eso usa más iconos.",
                     "La categoría con más iconos es la de mayor valor.", 1)

# ---- LINEAS (12 datasets -> 12 exercises) ----
line_interactions = ["SELECCION_EN_GRAFICO", "COMPARAR_PUNTOS", "ORDENAR_CATEGORIAS", "OPCION_MULTIPLE"]
for i, dkey in enumerate(line_datasets):
    title = DATASET_BY_KEY[dkey][1]
    defkey = f"def_lineas_{dkey}"
    add_def(defkey, dkey, "LINEAS", title, "LINEAS")
    labels = cat_labels(dkey)
    values = cat_values(dkey)
    itype = line_interactions[i % len(line_interactions)]
    if itype == "SELECCION_EN_GRAFICO":
        idx = argmax(values)
        add_exercise(defkey, "LINEAS", itype,
                     "Toca el punto más alto de la línea.",
                     [idx], [],
                     f"El punto más alto es «{labels[idx]}» con {values[idx]:g}.",
                     "Sigue la línea y busca el punto que está más arriba.", 1)
    elif itype == "COMPARAR_PUNTOS":
        a, b = argmax(values), argmin(values)
        add_exercise(defkey, "LINEAS", itype,
                     f"¿En qué momento hubo más: {labels[a]} o {labels[b]}?",
                     [0], [labels[a], labels[b]],
                     f"«{labels[a]}» tuvo el valor más alto de los dos.",
                     "Compara la altura de ambos puntos sobre la línea.", 1)
    elif itype == "ORDENAR_CATEGORIAS":
        first3 = list(range(min(3, len(values))))
        order = sorted(first3, key=lambda i: values[i])
        add_exercise(defkey, "LINEAS", itype,
                     "Ordena estos primeros momentos de menor a mayor valor.",
                     order, [labels[i] for i in first3],
                     "¡Correcto! Ordenaste la tendencia de menor a mayor.",
                     "Observa qué punto está más bajo y cuál más alto en ese tramo.", 2)
    else:
        rising = values[-1] > values[0]
        add_exercise(defkey, "LINEAS", itype,
                     "En general, ¿la tendencia de la línea sube o baja?",
                     [0 if rising else 1], ["Sube", "Baja"],
                     f"La tendencia general {'sube' if rising else 'baja'} de inicio a fin.",
                     "Compara el primer punto con el último para ver la tendencia general.", 1)

# ---- CIRCULAR (12 datasets -> 12 exercises) ----
pie_interactions = ["SELECCION_EN_GRAFICO", "ESTIMAR_VALOR", "COMPARAR_PUNTOS", "OPCION_MULTIPLE"]
for i, dkey in enumerate(pie_datasets):
    title = DATASET_BY_KEY[dkey][1]
    defkey = f"def_circular_{dkey}"
    add_def(defkey, dkey, "CIRCULAR", title, "CIRCULAR")
    labels = cat_labels(dkey)
    values = cat_values(dkey)
    total = sum(values)
    pct = [round(v / total * 100) for v in values]
    itype = pie_interactions[i % len(pie_interactions)]
    if itype == "SELECCION_EN_GRAFICO":
        idx = argmax(values)
        add_exercise(defkey, "CIRCULAR", itype,
                     "Toca el sector más grande del círculo.",
                     [idx], [],
                     f"El sector más grande es «{labels[idx]}» con {pct[idx]}% del total.",
                     "Busca la porción del círculo que ocupa más espacio.", 1)
    elif itype == "ESTIMAR_VALOR":
        idx = argmax(values)
        add_exercise(defkey, "CIRCULAR", itype,
                     f"¿Qué porcentaje aproximado representa «{labels[idx]}»?",
                     [pct[idx]], ["%"],
                     f"«{labels[idx]}» representa cerca del {pct[idx]}% del total.",
                     f"El porcentaje real era cerca de {pct[idx]}%. Compara el sector con el círculo completo.", 2)
    elif itype == "COMPARAR_PUNTOS":
        a, b = 0, 1
        winner = 0 if values[a] >= values[b] else 1
        add_exercise(defkey, "CIRCULAR", itype,
                     f"¿Qué porción es mayor: {labels[a]} o {labels[b]}?",
                     [winner], [labels[a], labels[b]],
                     f"«{labels[winner]}» ocupa una porción más grande del círculo.",
                     "Compara el tamaño de ambos sectores dentro del círculo.", 1)
    else:
        idx = argmin(values)
        wrong = [labels[j] for j in range(len(labels)) if j != idx][:2]
        options = [labels[idx]] + wrong
        add_exercise(defkey, "CIRCULAR", itype,
                     "¿Qué categoría ocupa la porción más pequeña?",
                     [0], options,
                     f"«{labels[idx]}» es la porción más pequeña, con {pct[idx]}%.",
                     "Busca el sector más angosto del círculo.", 1)

assert len(exercises) == 50, f"Se esperaban 50 ejercicios, hay {len(exercises)}"

# ============================================== ERROR CHALLENGES (30) ======
ERROR_TYPES_CYCLE = [
    "EJE_TRUNCADO", "ESCALA_INCONSISTENTE", "DATOS_FALTANTES",
    "TITULO_ENGANOSO", "CATEGORIA_INCORRECTA", "PICTOGRAMA_SIN_ESCALA",
]
error_datasets = [d[0] for d in DATASETS][:30]
error_challenges = []
for i, dkey in enumerate(error_datasets):
    etype = ERROR_TYPES_CYCLE[i % 6]
    labels = cat_labels(dkey)
    title = DATASET_BY_KEY[dkey][1]
    unit = DATASET_BY_KEY[dkey][3]
    chart_type = "PICTOGRAMA" if etype == "PICTOGRAMA_SIN_ESCALA" else ("BARRAS" if i % 2 == 0 else "CIRCULAR")
    entry = {
        "datasetKey": dkey, "chartType": chart_type, "errorType": etype,
        "axisMinOverride": None, "unitPerIconOverride": None, "omittedCategoryLabel": None,
        "difficulty": 1 + (i % 3),
    }
    if etype == "EJE_TRUNCADO":
        entry["chartType"] = "BARRAS"
        entry["displayedTitle"] = title
        entry["axisMinOverride"] = round(min(v for _, v in DATASET_BY_KEY[dkey][5]) * 0.85, 1)
        entry["explanation"] = (
            f"El eje no comienza en 0, así que las diferencias entre {unit} parecen mucho más "
            "grandes de lo que son en realidad. Un eje truncado exagera visualmente los datos."
        )
    elif etype == "ESCALA_INCONSISTENTE":
        entry["chartType"] = "BARRAS"
        entry["displayedTitle"] = title
        entry["explanation"] = (
            "Los espacios entre los números del eje no son iguales entre sí, así que las barras "
            "no se pueden comparar de forma justa: la escala debe avanzar siempre en pasos iguales."
        )
    elif etype == "DATOS_FALTANTES":
        entry["displayedTitle"] = title
        entry["omittedCategoryLabel"] = labels[-1]
        entry["explanation"] = (
            f"Falta la categoría «{labels[-1]}» en el gráfico. Sin ese dato, la comparación entre "
            "categorías queda incompleta y puede llevar a una conclusión equivocada."
        )
    elif etype == "TITULO_ENGANOSO":
        entry["displayedTitle"] = f"¡{title.split(' ')[0]} está fuera de control!"
        entry["explanation"] = (
            "El título usa palabras alarmantes o exageradas que los datos del gráfico no respaldan. "
            "Un buen título describe los datos sin exagerar ni dramatizar."
        )
    elif etype == "CATEGORIA_INCORRECTA":
        entry["displayedTitle"] = title
        entry["explanation"] = (
            "Una de las etiquetas no corresponde al dato que está representando: revisa que cada "
            "barra o sector tenga el nombre correcto antes de confiar en el gráfico."
        )
    else:  # PICTOGRAMA_SIN_ESCALA
        entry["chartType"] = "PICTOGRAMA"
        entry["displayedTitle"] = title
        entry["unitPerIconOverride"] = 0.0
        entry["explanation"] = (
            "El pictograma no indica cuánto vale cada icono (no tiene leyenda de escala), así que "
            "es imposible saber la cantidad real que representa cada fila."
        )
    error_challenges.append(entry)

assert len(error_challenges) == 30

# ================================================= COMPARISONS (20) ========
comparisons = []
comp_specs = [
    ("composicion", "CIRCULAR", "BARRAS",
     "¿Qué gráfico muestra mejor qué parte del total representa cada categoría?", "A",
     "El gráfico circular muestra de un vistazo qué proporción del total ocupa cada parte."),
    ("serie", "LINEAS", "BARRAS",
     "¿Qué gráfico muestra mejor cómo cambia el valor a lo largo del tiempo?", "A",
     "La línea conecta los puntos en el tiempo y deja ver la tendencia con claridad."),
    ("conteo", "BARRAS", "PICTOGRAMA",
     "¿Qué gráfico permite comparar valores exactos con más precisión?", "A",
     "Las barras usan una escala continua, así que se pueden leer valores exactos más fácilmente."),
]
pool_composicion = by_tag("composicion")
pool_serie = by_tag("serie")
pool_conteo = [d for d in by_tag("conteo") if d not in pool_composicion][:8]

idx = 0
for dkey in pool_composicion[:7]:
    spec = comp_specs[0]
    comparisons.append({
        "datasetKey": dkey, "chartTypeA": spec[1], "chartTypeB": spec[2],
        "question": spec[3], "betterSide": spec[4], "explanation": spec[5],
        "difficulty": 1 + (idx % 3),
    })
    idx += 1
for dkey in pool_serie[:7]:
    spec = comp_specs[1]
    comparisons.append({
        "datasetKey": dkey, "chartTypeA": spec[1], "chartTypeB": spec[2],
        "question": spec[3], "betterSide": spec[4], "explanation": spec[5],
        "difficulty": 1 + (idx % 3),
    })
    idx += 1
for dkey in pool_conteo[:6]:
    spec = comp_specs[2]
    comparisons.append({
        "datasetKey": dkey, "chartTypeA": spec[1], "chartTypeB": spec[2],
        "question": spec[3], "betterSide": spec[4], "explanation": spec[5],
        "difficulty": 1 + (idx % 3),
    })
    idx += 1

assert len(comparisons) >= 18, len(comparisons)

# ======================================================= BADGES (10) =======
BADGES = [
    ("badge_primer_grafico", "Primer gráfico", "Completaste tu primer reto de interpretación.", "badge_primer_grafico", "Completar 1 ejercicio"),
    ("badge_maestro_barras", "Maestro de barras", "Dominaste el módulo de gráficos de barras.", "badge_maestro_barras", "Dominar el módulo Barras"),
    ("badge_ojo_de_lince", "Ojo de lince", "Detectaste 10 gráficos con errores.", "badge_ojo_de_lince", "Resolver 10 retos del Detective"),
    ("badge_constructor_experto", "Constructor experto", "Construiste y guardaste 5 gráficos propios.", "badge_constructor_experto", "Guardar 5 gráficos en el Constructor"),
    ("badge_detective_grafico", "Detective gráfico", "Resolviste 20 casos de gráficos engañosos.", "badge_detective_grafico", "Resolver 20 retos del Detective"),
    ("badge_comparador_agudo", "Comparador agudo", "Elegiste correctamente 10 veces la mejor representación.", "badge_comparador_agudo", "Acertar 10 comparaciones"),
    ("badge_racha_5", "Racha de 5", "Lograste 5 aciertos seguidos sin fallar.", "badge_racha_5", "Racha de 5 aciertos"),
    ("badge_explorador_datos", "Explorador de datos", "Completaste 25 ejercicios de interpretación.", "badge_explorador_datos", "Completar 25 ejercicios"),
    ("badge_precision_total", "Precisión total", "Terminaste una sesión completa sin errores.", "badge_precision_total", "Sesión perfecta"),
    ("badge_leyenda_del_estudio", "Leyenda del Estudio", "Dominaste los cuatro módulos principales.", "badge_leyenda_del_estudio", "Dominar los 4 módulos base"),
]
assert len(BADGES) == 10


# ================================================================ WRITE =====
def kstr(s):
    return json.dumps(s, ensure_ascii=False)


def kdouble(v):
    return f"{float(v)}"


lines = []
lines.append("// GENERADO AUTOMÁTICAMENTE por tools/generate_seed_content.py — no editar a mano.")
lines.append("// Ejecutar el script y volver a compilar si se necesita cambiar el contenido semilla.")
lines.append("package com.educalab.graficosdivertidos.data.local.seed")
lines.append("")
lines.append("import com.educalab.graficosdivertidos.domain.model.ChartType")
lines.append("import com.educalab.graficosdivertidos.domain.model.GraphErrorType")
lines.append("import com.educalab.graficosdivertidos.domain.model.InteractionType")
lines.append("import com.educalab.graficosdivertidos.domain.model.ModuleKey")
lines.append("")
lines.append("object SeedContent {")

# datasets
lines.append("    val datasets: List<SeedDataset> = listOf(")
for d in DATASETS:
    key, title, category, unit, icon, points, tags = d
    pts = ", ".join(f"({kstr(l)} to {kdouble(v)})" for l, v in points)
    lines.append(
        f"        SeedDataset({kstr(key)}, {kstr(title)}, {kstr(category)}, {kstr(unit)}, {kstr(icon)}, "
        f"listOf({pts})),"
    )
lines.append("    )")
lines.append("")

# chart definitions
lines.append("    val chartDefinitions: List<SeedChartDefinition> = listOf(")
for cd in chart_defs:
    axis = "null" if cd["axisMax"] is None else kdouble(cd["axisMax"])
    lines.append(
        f"        SeedChartDefinition({kstr(cd['key'])}, {kstr(cd['datasetKey'])}, "
        f"ChartType.{cd['chartType']}, {kstr(cd['title'])}, ModuleKey.{cd['moduleKey']}, axisMax = {axis}),"
    )
lines.append("    )")
lines.append("")

# exercises
lines.append("    val exercises: List<SeedExercise> = listOf(")
for ex in exercises:
    correct = "listOf(" + ", ".join(str(c) for c in ex["correctAnswer"]) + ")"
    options = "listOf(" + ", ".join(kstr(o) for o in ex["options"]) + ")"
    lines.append(
        "        SeedExercise(\n"
        f"            chartDefinitionKey = {kstr(ex['chartDefinitionKey'])},\n"
        f"            moduleKey = ModuleKey.{ex['moduleKey']},\n"
        f"            interactionType = InteractionType.{ex['interactionType']},\n"
        f"            prompt = {kstr(ex['prompt'])},\n"
        f"            correctAnswer = {correct},\n"
        f"            options = {options},\n"
        f"            explanationCorrect = {kstr(ex['explanationCorrect'])},\n"
        f"            explanationIncorrect = {kstr(ex['explanationIncorrect'])},\n"
        f"            difficulty = {ex['difficulty']},\n"
        "        ),"
    )
lines.append("    )")
lines.append("")

# error challenges
lines.append("    val errorChallenges: List<SeedErrorChallenge> = listOf(")
for e in error_challenges:
    axis_min = "null" if e["axisMinOverride"] is None else kdouble(e["axisMinOverride"])
    unit_icon = "null" if e["unitPerIconOverride"] is None else kdouble(e["unitPerIconOverride"])
    omitted = "null" if e["omittedCategoryLabel"] is None else kstr(e["omittedCategoryLabel"])
    lines.append(
        "        SeedErrorChallenge(\n"
        f"            datasetKey = {kstr(e['datasetKey'])},\n"
        f"            chartType = ChartType.{e['chartType']},\n"
        f"            displayedTitle = {kstr(e['displayedTitle'])},\n"
        f"            errorType = GraphErrorType.{e['errorType']},\n"
        f"            axisMinOverride = {axis_min},\n"
        f"            unitPerIconOverride = {unit_icon},\n"
        f"            omittedCategoryLabel = {omitted},\n"
        f"            explanation = {kstr(e['explanation'])},\n"
        f"            difficulty = {e['difficulty']},\n"
        "        ),"
    )
lines.append("    )")
lines.append("")

# comparisons
lines.append("    val comparisons: List<SeedComparison> = listOf(")
for c in comparisons:
    lines.append(
        "        SeedComparison(\n"
        f"            datasetKey = {kstr(c['datasetKey'])},\n"
        f"            chartTypeA = ChartType.{c['chartTypeA']},\n"
        f"            chartTypeB = ChartType.{c['chartTypeB']},\n"
        f"            question = {kstr(c['question'])},\n"
        f"            betterSide = {kstr(c['betterSide'])},\n"
        f"            explanation = {kstr(c['explanation'])},\n"
        f"            difficulty = {c['difficulty']},\n"
        "        ),"
    )
lines.append("    )")
lines.append("")

# badges
lines.append("    val badges: List<SeedBadge> = listOf(")
for b in BADGES:
    lines.append(
        f"        SeedBadge({kstr(b[0])}, {kstr(b[1])}, {kstr(b[2])}, {kstr(b[3])}, {kstr(b[4])}),"
    )
lines.append("    )")

lines.append("}")

with open(OUT_PATH, "w", encoding="utf-8") as f:
    f.write("\n".join(lines) + "\n")

print(f"Escrito {OUT_PATH}")
print(f"  datasets={len(DATASETS)} chartDefs={len(chart_defs)} exercises={len(exercises)} "
      f"errorChallenges={len(error_challenges)} comparisons={len(comparisons)} badges={len(BADGES)}")
