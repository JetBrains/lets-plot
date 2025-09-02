#  Copyright (c) 2025. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import io
import tempfile

from PIL import Image

import lets_plot as gg

try:
    import pymupdf
except ImportError:
    pymupdf = None

def assert_png(file_path, w, h):
    if isinstance(file_path, str):
        with open(file_path, 'rb') as f:
            content = f.read()
            assert content.startswith(b'\x89PNG\r\n\x1a\n')
        img = Image.open(file_path)
        assert img.size == (w, h)
    else:
        content = file_path.getvalue()
        assert content.startswith(b'\x89PNG\r\n\x1a\n')
        img = Image.open(io.BytesIO(content))
        assert img.size == (w, h)


def assert_svg(file_path, w=None, h=None, view_box=None):
    with open(file_path, 'rb') as f:
        content = f.read()
        assert content.startswith(b'<svg xmlns="http://www.w3.org/2000/svg"')
        if w is not None:
            assert f'width="{w}"' in content.decode('utf-8')
        if h is not None:
            assert f'height="{h}"' in content.decode('utf-8')
        if view_box is not None:
            assert f'viewBox="{view_box}"' in content.decode('utf-8')


def assert_pdf(file_path, w=None, h=None, unit=None):
    if pymupdf is None:
        with open(file_path, 'rb') as f:
            content = f.read()
            assert content.startswith(b'%PDF-1.')
    else:
        doc = pymupdf.open(file_path)
        page = doc.load_page(0)  # Load the first page
        size = page.rect  # Get the page size
        doc.close()

        page_width = size.width / 72  # Convert from points to inches
        page_height = size.height / 72  # Convert from points to inches

        if all(v is not None for v in (w, h, unit)):
            if unit == 'in':
                expected_w, expected_h = w, h
            elif unit == 'cm':
                expected_w, expected_h = w / 2.54, h / 2.54
            elif unit == 'mm':
                expected_w, expected_h = w / 25.4, h / 25.4
            else:
                raise ValueError(f"Unknown unit: {unit}")

            assert abs(page_width - expected_w) < 0.1, f"Width mismatch: expected {expected_w}, got {page_width}"
            assert abs(page_height - expected_h) < 0.1, f"Height mismatch: expected {expected_h}, got {page_height}"


def temp_file(filename):
    temp_dir = tempfile.gettempdir()
    return f"{temp_dir}/{filename}"

def test_ggsave_svg():
    p = gg.ggplot() + gg.geom_blank()
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave.svg'))

    print("Output path:", out_path)

    assert_svg(out_path)

def test_ggsave_svg_wh_default_unit_is_inch():
    p = gg.ggplot() + gg.geom_blank()
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave_svg_wh.svg'), w=5, h=3)
    print("Output path:", out_path)
    assert_svg(out_path, w="5.0in", h="3.0in", view_box="0 0 480.0 288.0")


def test_ggsave_svg_wh_unit_cm():
    p = gg.ggplot() + gg.geom_blank()
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave_svg_wh.svg'), w=5, h=3, unit='cm')
    print("Output path:", out_path)
    assert_svg(out_path, w="5.0cm", h="3.0cm", view_box="0 0 188.97637795275588 113.38582677165354")


def test_ggsave_png():
    p = gg.ggplot() + gg.geom_blank() + gg.ggsize(400, 300)
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave_png.png'))
    print("Output path:", out_path)
    assert_png(out_path, 800, 600)  # 2x scale by default


def test_ggsave_png_scale3():
    p = gg.ggplot() + gg.geom_blank() + gg.ggsize(400, 300)
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave_png_scale3.png'), scale=3)
    print("Output path:", out_path)
    assert_png(out_path, 1200, 900)


def test_ggsave_png_dpi150():
    p = gg.ggplot() + gg.geom_blank() + gg.ggsize(400, 300)
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave_png_dpi150.png'), dpi=150)
    print("Output path:", out_path)
    assert_png(out_path, 625, 468)  # 400*150/96, 300*150/96


def test_ggsave_png_wh():
    p = gg.ggplot() + gg.geom_blank() + gg.ggsize(400, 300)
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave_png_wh.png'), w=5, h=3)
    print("Output path:", out_path)
    assert_png(out_path, 1500, 900)  #  5*300, 3*300


def test_ggsave_png_wh_inch():
    p = gg.ggplot() + gg.geom_blank() + gg.ggsize(400, 300)
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave_png_wh_inch.png'), w=5, h=3, unit='in')
    print("Output path:", out_path)
    assert_png(out_path, 1500, 900)  #  5*300, 3*300


def test_ggsave_png_wh_cm():
    p = gg.ggplot() + gg.geom_blank() + gg.ggsize(400, 300)
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave_png_wh_cm.png'), w=5, h=3, unit='cm')
    print("Output path:", out_path)
    assert_png(out_path, 590, 356)  #  1.98inch * 300, 1.18inch * 300


def test_ggsave_png_wh_150dpi():
    p = gg.ggplot() + gg.geom_blank() + gg.ggsize(400, 300)
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave_png_wh_150dpi.png'), w=5, h=3, unit='in', dpi=150)
    print("Output path:", out_path)
    assert_png(out_path, 750, 450)  # 5*150, 3*150


def test_ggsave_png_wh_150dpi_scale2():
    p = gg.ggplot() + gg.geom_blank() + gg.ggsize(400, 300)
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave_png_wh_150dpi_scale2.png'), w=5, h=3, unit='in', dpi=150, scale=2)
    print("Output path:", out_path)
    assert_png(out_path, 1500, 900)  # 5*150*2, 3*150*2


def test_ggsave_png_wh_px():
    p = gg.ggplot() + gg.geom_blank() + gg.ggsize(400, 300)
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave_png_wh_px.png'), w=300, h=200, unit='px')
    print("Output path:", out_path)
    assert_png(out_path, 300, 200)  # 300px, 200px, default dpi is None and the scale is 1.0 if user set w and h in px


def test_ggsave_png_wh_px_scale2():
    p = gg.ggplot() + gg.geom_blank() + gg.ggsize(400, 300)
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave_png_wh_px_scale2.png'), w=300, h=200, unit='px', scale=2)
    print("Output path:", out_path)
    assert_png(out_path, 600, 400)  # 300px, 200px, default dpi is None and the scale is 1.0 if user set w and h in px


def test_ggsave_png_wh_px_150dpi():
    p = gg.ggplot() + gg.geom_blank() + gg.ggsize(400, 300)
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave_png_wh_px_150dpi.png'), w=300, h=200, unit='px', dpi=150)
    print("Output path:", out_path)
    assert_png(out_path, 468, 312)  #


def test_filelike_ggsave_png():
    p = gg.ggplot() + gg.geom_blank() + gg.ggsize(400, 300)
    out_buffer = io.BytesIO()
    p.to_png(path=out_buffer, scale = 1)

    assert_png(out_buffer, 400, 300)


def test_ggsave_pdf():
    p = gg.ggplot() + gg.geom_blank()
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave.pdf'))

    print("Output path:", out_path)

    with open(out_path, 'rb') as f:
        content = f.read()
        assert content.startswith(b'%PDF-1.')


def test_ggsave_pdf_5x3_inch():
    p = gg.ggplot() + gg.geom_blank()
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave_5x3_inch.pdf'), w=5, h=3, unit='in', scale=1)

    print("Output path:", out_path)

    assert_pdf(out_path, w=5, h=3, unit='in')


def test_ggsave_pdf_5x3_inch_150dpi():
    p = gg.ggplot() + gg.geom_blank()
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave_5x3_inch_150dpi.pdf'), w=5, h=3, unit='in', dpi=150, scale=1)

    print("Output path:", out_path)

    assert_pdf(out_path, w=5, h=3, unit='in')


def test_ggsave_pdf_5x3_inch_300dpi():
    p = gg.ggplot() + gg.geom_blank()
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave_5x3_inch_300dpi.pdf'), w=5, h=3, dpi=300, unit='in', scale=1)

    print("Output path:", out_path)

    assert_pdf(out_path, w=5, h=3, unit='in')


def test_ggsave_pdf_5x3_inch_150dpi_2Xscale():
    p = gg.ggplot() + gg.geom_blank()
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave_5x3_inch_150dpi_2Xscale.pdf'), w=5, h=3, unit='in', dpi=150, scale=2)

    print("Output path:", out_path)

    # Scale affects size, 5 * 2 = 10 inches, 3 * 2 = 6 inches
    assert_pdf(out_path, w=10, h=6, unit='in')


def test_ggsave_pdf_5x3_cm():
    p = gg.ggplot() + gg.geom_blank()
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave_5x3_cm.pdf'), w=5, h=3, unit='cm', scale=1)

    print("Output path:", out_path)

    assert_pdf(out_path, w=5, h=3, unit='cm')


def test_ggsave_pdf_5x3_cm_150dpi():
    p = gg.ggplot() + gg.geom_blank()
    out_path = gg.ggsave(p, filename=temp_file('test_ggsave_5x3_cm_150dpi.pdf'), w=5, h=3, unit='cm', dpi=150, scale=1)

    print("Output path:", out_path)

    assert_pdf(out_path, w=5, h=3, unit='cm')


def test_filelike_ggsave_pdf():
    p = gg.ggplot() + gg.geom_blank()
    out_buffer = io.BytesIO()
    p.to_pdf(path=out_buffer)

    assert out_buffer.getvalue().startswith(b'%PDF-1.')  # Check if it starts with PDF header

