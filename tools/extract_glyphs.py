#!/usr/bin/env python3
"""
Extract a small glyph set from DejaVu Sans Regular for the LaTeX vector renderer.
Outputs Kotlin source declaring a Map<Char, VectorGlyph>.

Regenerate with:
  python3 tools/extract_glyphs.py [--font /path/to/font.ttf] > out.kt
The default font is DejaVuSans; it can also be overridden with $LATEX_GLYPH_FONT.

License: DejaVu fonts are based on Bitstream Vera (Bitstream Vera Fonts Copyright);
DejaVu modifications are released to the public domain.
The generated Kotlin file is a derivative work containing only outline data;
no font binary is bundled. Attribution is emitted at the file header.
"""

import argparse
import os

DEFAULT_FONT_PATH = "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"
TARGET_UPM = 1000

# Characters we need (matches the plan).
CHARS_BASIC = (
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    "abcdefghijklmnopqrstuvwxyz"
    "0123456789"
    " +-=.,:;()[]<>/"          # ASCII punctuation/math, hyphen is treated separately below
)
CHARS_MINUS = "−"          # − U+2212 MINUS SIGN
CHARS_OPS   = "±∓×÷·≤≥≠∞"  # ± ∓ × ÷ · ≤ ≥ ≠ ∞
CHARS_GREEK_UP = "ΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΤΥΦΧΨΩ"
CHARS_GREEK_LO = "αβγδεζηθικλμνξοπρστυφχψω"

ALL_CHARS = CHARS_BASIC + CHARS_MINUS + CHARS_OPS + CHARS_GREEK_UP + CHARS_GREEK_LO


def parse_args():
    parser = argparse.ArgumentParser(
        description="Extract LaTeX vector glyphs from a TTF font (emits Kotlin to stdout)."
    )
    parser.add_argument(
        "--font",
        default=os.environ.get("LATEX_GLYPH_FONT", DEFAULT_FONT_PATH),
        help="Path to the TTF font (default: %(default)s; or set $LATEX_GLYPH_FONT).",
    )
    return parser.parse_args()


def round_path(path: str) -> str:
    """Round all numbers in the path to integers (1000-UPM grid)."""
    out = []
    i = 0
    while i < len(path):
        c = path[i]
        if c.isalpha():
            out.append(c)
            i += 1
        elif c in " ,":
            out.append(c)
            i += 1
        else:
            # parse a number
            j = i
            while j < len(path) and (path[j].isdigit() or path[j] in ".-+e"):
                j += 1
            num_str = path[i:j]
            if num_str:
                try:
                    val = float(num_str)
                    out.append(str(int(round(val))))
                except ValueError:
                    out.append(num_str)
            i = j
    return "".join(out)


def tokenize_path(path: str):
    """Yield (command_char, [args]) tuples from an SVG path data string.
    Only handles the absolute commands fontTools' SVGPathPen produces: M, L, H, V, C, Q, Z.
    Implements the implicit-lineTo-after-M rule: subsequent coord pairs after a M are treated as
    L commands (SVG spec).
    """
    i, n = 0, len(path)
    cur_cmd = None
    per_cmd = {"M": 2, "L": 2, "H": 1, "V": 1, "C": 6, "Q": 4, "Z": 0, "T": 2, "S": 4}

    def skip_ws():
        nonlocal i
        while i < n and (path[i] in " ,\t\r\n"):
            i += 1

    def at_number() -> bool:
        if i >= n:
            return False
        ch = path[i]
        return ch.isdigit() or ch in ".+-"

    def read_num() -> float:
        nonlocal i
        skip_ws()
        j = i
        if j < n and path[j] in "+-":
            j += 1
        while j < n and (path[j].isdigit() or path[j] == "."):
            j += 1
        if j < n and path[j] in "eE":
            j += 1
            if j < n and path[j] in "+-":
                j += 1
            while j < n and path[j].isdigit():
                j += 1
        v = float(path[i:j])
        i = j
        return v

    while i < n:
        skip_ws()
        if i >= n:
            break
        ch = path[i]
        if ch.isalpha():
            cur_cmd = ch
            i += 1
            # Zero-arg command (Z): yield immediately.
            if cur_cmd in ("Z", "z"):
                yield (cur_cmd, [])
                continue
            # Read first arg group below.
        # Now parse a coord group for cur_cmd.
        if cur_cmd is None:
            raise ValueError(f"Path begins without a command at position {i}: {path[:40]}")
        nargs = per_cmd.get(cur_cmd, 0)
        args = []
        while len(args) < nargs:
            args.append(read_num())
        yield (cur_cmd, args)
        # Implicit-lineTo rule: after M, subsequent coord pairs are L.
        if cur_cmd == "M":
            cur_cmd = "L"
        elif cur_cmd == "m":
            cur_cmd = "l"


def quad_to_cubic(path: str) -> str:
    """Convert Q (quadratic Bezier) commands to C (cubic Bezier) commands.
    Q control1=(qx, qy) endpoint=(x, y) from current point (cx, cy):
        C control1 = ((cx + 2*qx) / 3, (cy + 2*qy) / 3)
        C control2 = ((2*qx + x) / 3, (2*qy + y) / 3)
        endpoint = (x, y)
    Tracks current point through the path. Absolute commands only.
    """
    cx, cy = 0.0, 0.0
    start_x, start_y = 0.0, 0.0  # for Z
    out_parts = []
    for cmd, args in tokenize_path(path):
        if cmd == "M":
            cx, cy = args[0], args[1]
            start_x, start_y = cx, cy
            out_parts.append(f"M{int(round(cx))} {int(round(cy))}")
        elif cmd == "L":
            cx, cy = args[0], args[1]
            out_parts.append(f"L{int(round(cx))} {int(round(cy))}")
        elif cmd == "H":
            cx = args[0]
            out_parts.append(f"H{int(round(cx))}")
        elif cmd == "V":
            cy = args[0]
            out_parts.append(f"V{int(round(cy))}")
        elif cmd == "C":
            x1, y1, x2, y2, x, y = args
            out_parts.append(
                f"C{int(round(x1))} {int(round(y1))} {int(round(x2))} {int(round(y2))} {int(round(x))} {int(round(y))}"
            )
            cx, cy = x, y
        elif cmd == "Q":
            qx, qy, x, y = args
            c1x = (cx + 2 * qx) / 3
            c1y = (cy + 2 * qy) / 3
            c2x = (2 * qx + x) / 3
            c2y = (2 * qy + y) / 3
            out_parts.append(
                f"C{int(round(c1x))} {int(round(c1y))} {int(round(c2x))} {int(round(c2y))} {int(round(x))} {int(round(y))}"
            )
            cx, cy = x, y
        elif cmd == "Z":
            out_parts.append("Z")
            cx, cy = start_x, start_y
    return "".join(out_parts)


def extract_glyph(font, glyph_set, char, scale):
    """Return (advance_em_units_at_1000UPM, path_str_at_1000UPM)."""
    from fontTools.misc.transform import Transform
    from fontTools.pens.svgPathPen import SVGPathPen
    from fontTools.pens.transformPen import TransformPen

    cmap = font.getBestCmap()
    cp = ord(char)
    if cp not in cmap:
        return None
    glyph_name = cmap[cp]
    advance_orig, _lsb = font["hmtx"][glyph_name]
    advance = int(round(advance_orig * scale))

    pen = SVGPathPen(glyph_set)
    # Transform: scale to 1000 UPM, flip y (font y-up -> SVG y-down).
    # baseline stays at y=0, ascenders become negative y, descenders become positive y.
    tpen = TransformPen(pen, Transform(scale, 0.0, 0.0, -scale, 0.0, 0.0))
    glyph = glyph_set[glyph_name]
    glyph.draw(tpen)
    path = pen.getCommands()
    # Convert quadratic Bezier curves (Q) to cubic (C). The raster path parser in
    # plot-raster/.../SvgPathParser.kt does not support Q.
    if path:
        path = round_path(path)
        path = quad_to_cubic(path)
    return advance, path


def main(font_path=DEFAULT_FONT_PATH):
    from fontTools.ttLib import TTFont

    font = TTFont(font_path)
    upm = font["head"].unitsPerEm
    scale = TARGET_UPM / upm
    glyph_set = font.getGlyphSet()

    entries = []
    for ch in ALL_CHARS:
        result = extract_glyph(font, glyph_set, ch, scale)
        if result is None:
            print(f"WARN: missing glyph for U+{ord(ch):04X} ({ch!r})")
            continue
        advance, path = result
        entries.append((ch, advance, path))

    # Emit Kotlin
    print("// Auto-generated by tools/extract_glyphs.py — DO NOT EDIT BY HAND.")
    print("// Glyph outlines and advance widths are derived from DejaVu Sans Regular.")
    print("// DejaVu is based on Bitstream Vera (Bitstream Vera Fonts Copyright (c) 2003 by")
    print("// Bitstream, Inc.); DejaVu modifications are released to the public domain.")
    print("// See: https://dejavu-fonts.github.io/License.html")
    print("// No font binary is bundled. Only outline data and advance widths are derived.")
    print("// Coordinates are in a 1000-UPM grid with SVG y-down (baseline at y=0;")
    print("// ascenders are negative y, descenders are positive y).")
    print(f"// UPM source: {upm}; normalized to {TARGET_UPM}; entries: {len(entries)}.")
    print()
    print("internal fun buildLatexGlyphTable(): Map<Char, VectorGlyph> = mapOf(")
    for ch, advance, path in entries:
        key = char_literal(ch)
        # path may be empty for space-like glyphs
        if path:
            print(f'    {key} to VectorGlyph(advanceEm = {advance}.0 / 1000.0, pathData = "{path}"),')
        else:
            print(f'    {key} to VectorGlyph(advanceEm = {advance}.0 / 1000.0, pathData = null),')
    print(")")


def char_literal(ch: str) -> str:
    cp = ord(ch)
    if ch == "'":
        return r"'\''"
    if ch == "\\":
        return r"'\\'"
    if 32 <= cp < 127:
        return f"'{ch}'"
    return f"'\\u{cp:04X}'"


if __name__ == "__main__":
    main(parse_args().font)
