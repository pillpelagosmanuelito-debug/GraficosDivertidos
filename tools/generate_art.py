#!/usr/bin/env python3
"""
Generador de recursos visuales locales para Gráficos Divertidos.

Dibuja, con Pillow, todas las ilustraciones planas del "Estudio de
Visualización": el mascota Grafi (asistente geométrico), la portada,
iconos de módulo, insignias y avatares. No se descarga ni referencia
ninguna imagen externa: todo se genera de forma procedural con formas
geométricas, coherente con la identidad "hecho de gráficos" de Grafi.

Salida: app/src/main/res/drawable-nodpi/*.png
"""
import math
import os
from PIL import Image, ImageDraw, ImageFilter, ImageFont

OUT = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "res", "drawable-nodpi")
os.makedirs(OUT, exist_ok=True)

# ---------------------------------------------------------------- paleta ---
INK = (26, 27, 46, 255)          # contorno oscuro (casi negro-índigo)
PAPER = (255, 255, 255, 0)       # fondo transparente
VIOLET = (108, 75, 255, 255)
VIOLET_D = (76, 50, 199, 255)
TEAL = (23, 195, 178, 255)
TEAL_D = (13, 148, 136, 255)
CORAL = (255, 107, 107, 255)
CORAL_D = (216, 68, 68, 255)
AMBER = (255, 182, 39, 255)
AMBER_D = (219, 146, 10, 255)
SKY = (86, 180, 255, 255)
SKY_D = (46, 130, 210, 255)
MINT = (110, 231, 183, 255)
CREAM = (255, 247, 230, 255)
NAVY = (20, 22, 43, 255)
NAVY_2 = (32, 35, 66, 255)

PALETTES = [
    (VIOLET, VIOLET_D), (TEAL, TEAL_D), (CORAL, CORAL_D),
    (AMBER, AMBER_D), (SKY, SKY_D),
]


def canvas(size=512):
    return Image.new("RGBA", (size, size), PAPER)


def save(img, name):
    path = os.path.join(OUT, f"{name}.png")
    img.save(path, "PNG")
    print("  ->", path)


def rounded_rect(d, box, radius, fill, outline=INK, width=6):
    d.rounded_rectangle(box, radius=radius, fill=fill, outline=outline, width=width)


def circle(d, cx, cy, r, fill, outline=INK, width=6):
    d.ellipse([cx - r, cy - r, cx + r, cy + r], fill=fill, outline=outline, width=width)


def soft_shadow(img, offset=(0, 10), blur=14, alpha=70):
    shadow = Image.new("RGBA", img.size, (0, 0, 0, 0))
    alpha_ch = img.split()[3].point(lambda p: alpha if p > 0 else 0)
    shadow.putalpha(alpha_ch)
    sh_layer = Image.new("RGBA", img.size, (0, 0, 0, 0))
    sh_layer.paste((10, 10, 25, alpha), (offset[0], offset[1]), alpha_ch)
    sh_layer = sh_layer.filter(ImageFilter.GaussianBlur(blur))
    out = Image.alpha_composite(sh_layer, img)
    return out


# ---------------------------------------------------------- Grafi (mascota) ---
def draw_grafi(pose="wave"):
    """Grafi: cuerpo hecho de una barra + cabeza circular con 'ojos de dato'
    y una cola/antena en forma de línea de tendencia. Encarna la temática
    de estadística sin ser un personaje de peluche infantil."""
    img = canvas(512)
    d = ImageDraw.Draw(img)
    cx = 256

    # sombra de suelo
    d.ellipse([cx - 110, 430, cx + 110, 470], fill=(20, 20, 40, 60))

    # cuerpo: prisma tipo "barra" redondeada, degradado violeta
    body_top, body_bottom = 210, 420
    rounded_rect(d, [cx - 95, body_top, cx + 95, body_bottom], 46, VIOLET, INK, 8)
    # franja de "escala" en el cuerpo (líneas horizontales tipo eje)
    for i, y in enumerate(range(body_top + 40, body_bottom - 20, 34)):
        d.line([cx - 70, y, cx + 70, y], fill=(255, 255, 255, 90), width=4)

    # cabeza: círculo con "ojos de punto de dato"
    head_r = 92
    head_cy = 150
    circle(d, cx, head_cy, head_r, CREAM, INK, 8)

    # antena = mini línea de tendencia ascendente (identidad de "gráficos de líneas")
    ant_pts = [(cx - 30, head_cy - head_r - 6), (cx - 6, head_cy - head_r - 34),
               (cx + 20, head_cy - head_r - 20), (cx + 46, head_cy - head_r - 52)]
    d.line(ant_pts, fill=TEAL_D, width=7, joint="curve")
    for p in ant_pts:
        circle(d, p[0], p[1], 6, TEAL, INK, 3)

    # ojos: dos puntos de dato grandes sobre un mini eje
    eye_y = head_cy - 6
    for ex in (cx - 34, cx + 34):
        circle(d, ex, eye_y, 15, NAVY, None, 0)
        circle(d, ex - 4, eye_y - 4, 5, (255, 255, 255, 230), None, 0)

    # boca: arco de "sonrisa" tipo sector de pastel
    d.arc([cx - 34, eye_y + 6, cx + 34, eye_y + 60], start=20, end=160, fill=INK, width=7)

    # mejillas (rubor sutil, sin ser bebé)
    circle(d, cx - 62, head_cy + 22, 12, (255, 160, 160, 140), None, 0)
    circle(d, cx + 62, head_cy + 22, 12, (255, 160, 160, 140), None, 0)

    # brazos: segmentos tipo "barra" delgada
    if pose == "wave":
        d.line([cx - 95, 250, cx - 150, 190], fill=CORAL, width=20, joint="curve")
        circle(d, cx - 150, 190, 16, CORAL, INK, 5)
        d.line([cx + 95, 260, cx + 150, 300], fill=AMBER, width=20, joint="curve")
        circle(d, cx + 150, 300, 16, AMBER, INK, 5)
    elif pose == "celebrate":
        d.line([cx - 95, 250, cx - 160, 170], fill=CORAL, width=20, joint="curve")
        circle(d, cx - 160, 170, 16, CORAL, INK, 5)
        d.line([cx + 95, 250, cx + 160, 170], fill=AMBER, width=20, joint="curve")
        circle(d, cx + 160, 170, 16, AMBER, INK, 5)
        # estrellas de celebración
        for sx, sy, r, col in [(140, 120, 14, AMBER), (380, 140, 10, TEAL), (120, 260, 9, CORAL), (392, 260, 12, VIOLET)]:
            draw_star(d, sx, sy, r, col)
    elif pose == "think":
        d.line([cx - 95, 250, cx - 150, 300], fill=CORAL, width=20, joint="curve")
        circle(d, cx - 150, 300, 16, CORAL, INK, 5)
        # lupa en la mano derecha (detective de gráficos)
        d.line([cx + 95, 250, cx + 165, 210], fill=AMBER, width=20, joint="curve")
        circle(d, 208, 170, 34, (255, 255, 255, 60), INK, 8)
        d.line([230, 194, 254, 218], fill=INK, width=10)
    elif pose == "build":
        d.line([cx - 95, 250, cx - 150, 280], fill=CORAL, width=20, joint="curve")
        d.line([cx + 95, 250, cx + 150, 280], fill=AMBER, width=20, joint="curve")
        # bloques de construcción entre las manos
        rounded_rect(d, [cx - 40, 268, cx - 4, 304], 6, TEAL, INK, 5)
        rounded_rect(d, [cx + 4, 250, cx + 40, 286], 6, SKY, INK, 5)

    # piernas cortas
    for lx in (cx - 40, cx + 40):
        rounded_rect(d, [lx - 16, 420, lx + 16, 452], 10, VIOLET_D, INK, 6)

    return soft_shadow(img)


def draw_star(d, cx, cy, r, fill):
    pts = []
    for i in range(10):
        ang = math.pi / 5 * i - math.pi / 2
        rad = r if i % 2 == 0 else r * 0.45
        pts.append((cx + rad * math.cos(ang), cy + rad * math.sin(ang)))
    d.polygon(pts, fill=fill, outline=INK)


# --------------------------------------------------------------- portada ---
def draw_cover():
    """Ilustración de portada: 'skyline' del Estudio hecho de barras, una
    línea de tendencia y un sector circular, con Grafi al frente."""
    img = Image.new("RGBA", (900, 600), NAVY)
    d = ImageDraw.Draw(img)

    # cielo con puntos de dato (estrellas)
    import random
    random.seed(7)
    for _ in range(60):
        x, y = random.randint(0, 900), random.randint(0, 320)
        r = random.choice([2, 2, 3, 4])
        d.ellipse([x - r, y - r, x + r, y + r], fill=(255, 255, 255, random.randint(60, 180)))

    # "skyline" de barras (representa el estudio de estadística)
    bars = [(40, 420, 320), (110, 380, 320), (180, 300, 320), (250, 440, 320),
             (620, 360, 320), (690, 300, 320), (760, 420, 320), (830, 260, 320)]
    palette_cycle = [VIOLET, TEAL, CORAL, AMBER, SKY]
    for i, (x, h, base) in enumerate(bars):
        col = palette_cycle[i % len(palette_cycle)]
        rounded_rect(d, [x, base - h + 200, x + 46, base + 60], 10, col, INK, 5)

    # línea de tendencia atravesando el horizonte
    line_pts = [(30, 340), (160, 300), (300, 350), (450, 260), (600, 310), (760, 230), (880, 270)]
    d.line(line_pts, fill=TEAL, width=8, joint="curve")
    for p in line_pts:
        circle(d, p[0], p[1], 9, CREAM, INK, 4)

    # sector circular flotante (pie) como "luna"
    d.pieslice([720, 40, 860, 180], start=0, end=270, fill=AMBER, outline=INK, width=6)
    d.pieslice([720, 40, 860, 180], start=270, end=360, fill=CORAL, outline=INK, width=6)

    # plataforma / suelo del estudio
    rounded_rect(d, [0, 500, 900, 600], 0, NAVY_2, None, 0)
    d.line([0, 500, 900, 500], fill=(255, 255, 255, 40), width=3)

    img_rgba = img.convert("RGBA")
    grafi = draw_grafi("wave").resize((260, 260))
    img_rgba.alpha_composite(grafi, (320, 300))

    return img_rgba


# ------------------------------------------------------------- fondos ------
def draw_bg_dots():
    img = canvas(512)
    d = ImageDraw.Draw(img)
    for y in range(0, 512, 40):
        for x in range(0, 512, 40):
            d.ellipse([x - 3, y - 3, x + 3, y + 3], fill=(255, 255, 255, 30))
    return img


def draw_bg_grid():
    img = Image.new("RGBA", (512, 512), NAVY)
    d = ImageDraw.Draw(img)
    for y in range(0, 512, 32):
        d.line([0, y, 512, y], fill=(255, 255, 255, 14), width=1)
    for x in range(0, 512, 32):
        d.line([x, 0, x, 512], fill=(255, 255, 255, 14), width=1)
    return img


# --------------------------------------------------- iconos de módulo ------
def module_icon(kind):
    img = canvas(256)
    d = ImageDraw.Draw(img)
    circle(d, 128, 128, 118, NAVY_2, INK, 6)

    if kind == "bars":
        vals = [70, 110, 90, 140]
        colors = [VIOLET, TEAL, CORAL, AMBER]
        x = 58
        for v, c in zip(vals, colors):
            rounded_rect(d, [x, 190 - v, x + 34, 190], 8, c, INK, 5)
            x += 44
    elif kind == "pictograms":
        for i, c in enumerate([CORAL, CORAL, AMBER]):
            cx = 90 + i * 42
            rounded_rect(d, [cx - 16, 90, cx + 16, 190], 10, c, INK, 5)
            circle(d, cx, 78, 16, c, INK, 5)
    elif kind == "lines":
        pts = [(50, 170), (100, 120), (140, 150), (190, 90), (220, 120)]
        d.line(pts, fill=TEAL, width=10, joint="curve")
        for p in pts:
            circle(d, p[0], p[1], 8, CREAM, INK, 4)
    elif kind == "pie":
        d.pieslice([68, 68, 188, 188], 0, 140, fill=VIOLET, outline=INK, width=6)
        d.pieslice([68, 68, 188, 188], 140, 260, fill=AMBER, outline=INK, width=6)
        d.pieslice([68, 68, 188, 188], 260, 360, fill=TEAL, outline=INK, width=6)
    elif kind == "builder":
        rounded_rect(d, [70, 150, 110, 190], 6, TEAL, INK, 5)
        rounded_rect(d, [115, 110, 155, 190], 6, VIOLET, INK, 5)
        rounded_rect(d, [160, 80, 200, 190], 6, CORAL, INK, 5)
        d.line([60, 60, 110, 40, 160, 55], fill=AMBER, width=8, joint="curve")
    elif kind == "comparator":
        rounded_rect(d, [60, 80, 118, 190], 8, VIOLET, INK, 5)
        rounded_rect(d, [138, 110, 196, 190], 8, TEAL, INK, 5)
        d.line([128, 60, 128, 200], fill=CREAM, width=6)
        draw_star(d, 128, 40, 14, AMBER)
    elif kind == "detective":
        circle(d, 110, 110, 46, (255, 255, 255, 40), INK, 8)
        d.line([144, 144, 190, 190], fill=INK, width=14)
        draw_star(d, 200, 70, 10, AMBER)
    elif kind == "challenges":
        draw_star(d, 128, 100, 60, AMBER)
        circle(d, 128, 100, 60, None, INK, 6)
    elif kind == "gallery":
        for i, (cx, cy, c) in enumerate([(90, 100, AMBER), (166, 100, TEAL), (90, 170, CORAL), (166, 170, VIOLET)]):
            circle(d, cx, cy, 30, c, INK, 5)
    elif kind == "profile":
        circle(d, 128, 95, 44, CREAM, INK, 6)
        d.pieslice([70, 150, 186, 260], 180, 360, fill=CREAM, outline=INK, width=6)

    return img


# ------------------------------------------------------------- insignias ---
BADGE_SPECS = [
    ("badge_primer_grafico", "Primer gráfico", AMBER, "star"),
    ("badge_maestro_barras", "Maestro de barras", VIOLET, "bars"),
    ("badge_ojo_de_lince", "Ojo de lince", TEAL, "eye"),
    ("badge_constructor_experto", "Constructor experto", CORAL, "wrench"),
    ("badge_detective_grafico", "Detective gráfico", SKY, "magnifier"),
    ("badge_comparador_agudo", "Comparador agudo", MINT, "scale"),
    ("badge_racha_5", "Racha de 5", AMBER, "flame"),
    ("badge_explorador_datos", "Explorador de datos", VIOLET, "compass"),
    ("badge_precision_total", "Precisión total", CORAL, "target"),
    ("badge_leyenda_del_estudio", "Leyenda del estudio", TEAL, "crown"),
]


def draw_badge(color, glyph):
    img = canvas(256)
    d = ImageDraw.Draw(img)
    dark = tuple(max(0, c - 60) for c in color[:3]) + (255,)
    # cinta
    d.polygon([(96, 170), (96, 230), (128, 205), (160, 230), (160, 170)], fill=dark, outline=INK)
    # medalla
    circle(d, 128, 118, 92, color, INK, 8)
    circle(d, 128, 118, 68, (255, 255, 255, 60), None, 0)

    if glyph == "star":
        draw_star(d, 128, 118, 46, CREAM)
    elif glyph == "bars":
        for i, h in enumerate([30, 46, 38]):
            rounded_rect(d, [96 + i * 24, 150 - h, 96 + i * 24 + 16, 150], 4, CREAM, INK, 3)
    elif glyph == "eye":
        d.ellipse([80, 100, 176, 140], fill=CREAM, outline=INK, width=4)
        circle(d, 128, 120, 14, INK, None, 0)
    elif glyph == "wrench":
        d.line([90, 150, 166, 90], fill=CREAM, width=16)
        circle(d, 90, 150, 18, CREAM, INK, 4)
    elif glyph == "magnifier":
        circle(d, 116, 106, 30, (255, 255, 255, 70), INK, 6)
        d.line([138, 128, 164, 154], fill=CREAM, width=10)
    elif glyph == "scale":
        d.line([128, 80, 128, 150], fill=CREAM, width=8)
        d.line([90, 100, 166, 100], fill=CREAM, width=8)
        circle(d, 90, 120, 14, CREAM, INK, 3)
        circle(d, 166, 120, 14, CREAM, INK, 3)
    elif glyph == "flame":
        d.polygon([(128, 70), (156, 130), (128, 166), (100, 130)], fill=CREAM, outline=INK)
    elif glyph == "compass":
        circle(d, 128, 118, 40, (255, 255, 255, 60), INK, 4)
        d.polygon([(128, 90), (140, 120), (128, 150), (116, 120)], fill=CREAM, outline=INK)
    elif glyph == "target":
        circle(d, 128, 118, 44, CREAM, INK, 4)
        circle(d, 128, 118, 26, color, INK, 4)
        circle(d, 128, 118, 10, CREAM, INK, 3)
    elif glyph == "crown":
        d.polygon([(90, 140), (100, 95), (128, 120), (156, 95), (166, 140)], fill=CREAM, outline=INK)

    return soft_shadow(img, offset=(0, 6), blur=8, alpha=60)


# -------------------------------------------------------------- avatares ---
AVATAR_SPECS = [
    ("avatar_01", VIOLET), ("avatar_02", TEAL), ("avatar_03", CORAL),
    ("avatar_04", AMBER), ("avatar_05", SKY), ("avatar_06", MINT),
    ("avatar_07", VIOLET_D), ("avatar_08", CORAL_D),
]


def draw_avatar(color, seed):
    img = canvas(256)
    d = ImageDraw.Draw(img)
    circle(d, 128, 128, 108, color, INK, 8)
    # cada avatar es una mini-composición de gráfico distinta a modo de "cara"
    variants = seed % 4
    if variants == 0:
        for i, h in enumerate([26, 44, 34, 50]):
            rounded_rect(d, [78 + i * 26, 170 - h, 78 + i * 26 + 18, 170], 5, CREAM, INK, 3)
    elif variants == 1:
        pts = [(70, 150), (110, 110), (140, 140), (186, 90)]
        d.line(pts, fill=CREAM, width=9, joint="curve")
        for p in pts:
            circle(d, p[0], p[1], 7, CREAM, INK, 3)
    elif variants == 2:
        d.pieslice([78, 78, 178, 178], 0, 160, fill=CREAM, outline=INK, width=5)
        d.pieslice([78, 78, 178, 178], 160, 360, fill=(255, 255, 255, 90), outline=INK, width=5)
    else:
        for i in range(3):
            circle(d, 96 + i * 32, 128, 14, CREAM, INK, 3)
    # ojos simples para dar cara amistosa
    circle(d, 100, 100, 8, INK, None, 0)
    circle(d, 156, 100, 8, INK, None, 0)
    return img


def main():
    print("Generando mascota Grafi...")
    save(draw_grafi("wave"), "grafi_saluda")
    save(draw_grafi("celebrate"), "grafi_celebra")
    save(draw_grafi("think"), "grafi_investiga")
    save(draw_grafi("build"), "grafi_construye")

    print("Generando portada...")
    save(draw_cover(), "portada_estudio")

    print("Generando fondos...")
    save(draw_bg_dots(), "fondo_puntos")
    save(draw_bg_grid(), "fondo_cuadricula")

    print("Generando iconos de módulo...")
    for kind in ["bars", "pictograms", "lines", "pie", "builder", "comparator",
                 "detective", "challenges", "gallery", "profile"]:
        save(module_icon(kind), f"icono_modulo_{kind}")

    print("Generando insignias...")
    for key, _title, color, glyph in BADGE_SPECS:
        save(draw_badge(color, glyph), key)

    print("Generando avatares...")
    for i, (key, color) in enumerate(AVATAR_SPECS):
        save(draw_avatar(color, i), key)

    print("Listo.")


if __name__ == "__main__":
    main()
