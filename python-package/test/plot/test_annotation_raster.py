#
# Copyright (c) 2026. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

import base64
import zlib
from io import BytesIO

import pytest
from PIL import Image

from lets_plot.plot.annotation_raster_ import annotation_raster

PNG_1X1_BASE64 = 'iVBORw0KGgoAAAANSUhEUgAAAAIAAAABCAYAAAD0In+KAAAADklEQVR4nGNgYGD4DwIADvoE/EWwHYsAAAAASUVORK5CYII='
PNG_1X1_BYTES = base64.b64decode(PNG_1X1_BASE64)


def test_annotation_raster_spec():
    spec = annotation_raster(
        PNG_1X1_BYTES,
        xmin=1,
        xmax=2,
        ymin=3,
        ymax=4,
        interpolate=True,
    ).as_dict()

    href = spec.pop('href')
    assert href.startswith('data:image/png;base64,')
    assert _is_rgba_png_without_filter(base64.b64decode(href.removeprefix('data:image/png;base64,')))

    assert spec == {
        'data_meta': {},
        'geom': 'annotation_raster',
        'mapping': {},
        'xmin': 1,
        'xmax': 2,
        'ymin': 3,
        'ymax': 4,
        'interpolate': True,
        'show_legend': False,
        'inherit_aes': False,
    }


def test_annotation_raster_default_bounds_are_omitted():
    spec = annotation_raster(PNG_1X1_BYTES).as_dict()

    assert 'xmin' not in spec
    assert 'xmax' not in spec
    assert 'ymin' not in spec
    assert 'ymax' not in spec


def test_annotation_raster_accepts_bytes_like_values():
    href_from_bytearray = annotation_raster(bytearray(PNG_1X1_BYTES)).as_dict()['href']
    href_from_memoryview = annotation_raster(memoryview(PNG_1X1_BYTES)).as_dict()['href']

    assert _is_rgba_png_without_filter(base64.b64decode(href_from_bytearray.removeprefix('data:image/png;base64,')))
    assert _is_rgba_png_without_filter(base64.b64decode(href_from_memoryview.removeprefix('data:image/png;base64,')))


def test_annotation_raster_converts_rgb_png_to_rgba_png():
    image = Image.new('RGB', (2, 1))
    image.putpixel((0, 0), (255, 0, 0))
    image.putpixel((1, 0), (0, 255, 0))

    rgb_png = BytesIO()
    image.save(rgb_png, format='PNG')

    href = annotation_raster(rgb_png.getvalue()).as_dict()['href']
    png_bytes = base64.b64decode(href.removeprefix('data:image/png;base64,'))

    assert _is_rgba_png_without_filter(png_bytes)


def test_annotation_raster_converts_jpeg_to_rgba_png():
    image = Image.new('RGB', (2, 1))
    image.putpixel((0, 0), (255, 0, 0))
    image.putpixel((1, 0), (0, 255, 0))

    jpeg = BytesIO()
    image.save(jpeg, format='JPEG')

    href = annotation_raster(jpeg.getvalue()).as_dict()['href']
    png_bytes = base64.b64decode(href.removeprefix('data:image/png;base64,'))

    assert _is_rgba_png_without_filter(png_bytes)


def _is_rgba_png_without_filter(png_bytes):
    return (
        png_bytes.startswith(b'\x89PNG\r\n\x1a\n') and
        png_bytes[24] == 8 and
        png_bytes[25] == 6 and
        _png_first_idat_scanline_filter(png_bytes) == 0
    )


def _png_first_idat_scanline_filter(png_bytes):
    offset = 8
    idat = bytearray()
    while offset < len(png_bytes):
        chunk_length = int.from_bytes(png_bytes[offset:offset + 4], 'big')
        chunk_type = png_bytes[offset + 4:offset + 8]
        chunk_data = png_bytes[offset + 8:offset + 8 + chunk_length]
        if chunk_type == b'IDAT':
            idat.extend(chunk_data)
        if chunk_type == b'IEND':
            break
        offset += 12 + chunk_length

    return zlib.decompress(bytes(idat))[0]


def test_annotation_raster_rejects_empty_bytes():
    with pytest.raises(ValueError, match="Raster image data is empty"):
        annotation_raster(b'')


def test_annotation_raster_rejects_unknown_image_format():
    with pytest.raises(ValueError, match="Unsupported raster image format"):
        annotation_raster(b'not an image')
