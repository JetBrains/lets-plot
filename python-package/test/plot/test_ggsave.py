#  Copyright (c) 2025. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import io
import tempfile

from PIL import Image

import lets_plot as gg


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


def test_ggsave_svg():
    p = gg.ggplot() + gg.geom_blank()
    temp_dir = tempfile.gettempdir()
    out_path = gg.ggsave(p, filename='test_ggsave.svg', path=temp_dir)

    print("Output path:", out_path)

    with open(out_path, 'rb') as f:
        content = f.read()
        assert content.startswith(b'<svg xmlns="http://www.w3.org/2000/svg"')


def test_ggsave_png():
    p = gg.ggplot() + gg.geom_blank() + gg.ggsize(400, 300)
    temp_dir = tempfile.gettempdir()
    out_path = gg.ggsave(p, filename='test_ggsave_magick.png', path=temp_dir, scale=1)
    print("Output path:", out_path)
    assert_png(out_path, 400, 300)


def test_ggsave_png_scale3():
    p = gg.ggplot() + gg.geom_blank() + gg.ggsize(400, 300)
    temp_dir = tempfile.gettempdir()
    out_path = gg.ggsave(p, filename='test_ggsave_magick.png', path=temp_dir, scale=3)
    print("Output path:", out_path)
    assert_png(out_path, 1200, 900)


def test_filelike_ggsave_png():
    p = gg.ggplot() + gg.geom_blank() + gg.ggsize(400, 300)
    out_buffer = io.BytesIO()
    p.to_png(path=out_buffer, scale = 1)

    assert_png(out_buffer, 400, 300)


def test_ggsave_png_cairo():
    p = gg.ggplot() + gg.geom_blank() + gg.ggsize(400, 300)
    temp_dir = tempfile.gettempdir()
    out_path = gg.ggsave(p, filename='test_ggsave_cairo.png', path=temp_dir, scale=1)
    print("Output path:", out_path)
    assert_png(out_path, 400, 300)


def test_ggsave_pdf():
    p = gg.ggplot() + gg.geom_blank()
    temp_dir = tempfile.gettempdir()
    out_path = gg.ggsave(p, filename='test_ggsave.pdf', path=temp_dir)

    print("Output path:", out_path)

    with open(out_path, 'rb') as f:
        content = f.read()
        assert content.startswith(b'%PDF-1.')


def test_ggsave_pdf_with_dpi():
    p = gg.ggplot() + gg.geom_blank()
    temp_dir = tempfile.gettempdir()
    out_path = gg.ggsave(p, filename='test_ggsave_with_dpi.pdf', path=temp_dir, dpi=300, w=5, h=3, unit='in', scale=1)

    print("Output path:", out_path)

    with open(out_path, 'rb') as f:
        content = f.read()
        assert content.startswith(b'%PDF-1.')


def test_filelike_ggsave_pdf():
    p = gg.ggplot() + gg.geom_blank()
    out_buffer = io.BytesIO()
    p.to_pdf(path=out_buffer)

    assert out_buffer.getvalue().startswith(b'%PDF-1.')  # Check if it starts with PDF header

