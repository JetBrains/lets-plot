#
# Copyright (c) 2026. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

import base64

import pytest

from lets_plot.plot.annotation import annotation_raster

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
    )

    assert spec.as_dict() == {
        'data_meta': {},
        'geom': 'annotation_raster',
        'href': 'data:image/png;base64,' + PNG_1X1_BASE64,
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
    expected_href = 'data:image/png;base64,' + PNG_1X1_BASE64

    assert annotation_raster(bytearray(PNG_1X1_BYTES)).as_dict()['href'] == expected_href
    assert annotation_raster(memoryview(PNG_1X1_BYTES)).as_dict()['href'] == expected_href


def test_annotation_raster_rejects_empty_bytes():
    with pytest.raises(ValueError, match="Raster image data is empty"):
        annotation_raster(b'')


def test_annotation_raster_rejects_unknown_image_format():
    with pytest.raises(ValueError, match="Unsupported raster image format"):
        annotation_raster(b'not an image')
