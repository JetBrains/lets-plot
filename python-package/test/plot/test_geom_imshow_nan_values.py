#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

import pytest

from lets_plot._type_utils import LazyModule

np = LazyModule("numpy")
png = LazyModule("png")
palettable_module = LazyModule("palettable")
from lets_plot.plot.geom_imshow_ import geom_imshow
from test_geom_imshow_util import _image_spec, _image_bbox


# See notebook:
#       docs/testing/testing_imshow_nan_values.ipynb

def _test_params_list():
    if not np or not palettable_module:
        return []
    from palettable.matplotlib import matplotlib as palettable

    expected_gray_2_x_3 = _image_spec(
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAAGUlEQVR4nGNgYGD4zwAigABEM4SGhoIY/wFguQf5zEefdAAAAABJRU5ErkJggg==',
        _image_bbox(width=3, height=2),
        data_min=50,
        data_max=200
    )
    expected_gray_2_x_3_magma = _image_spec(
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAAG0lEQVR4nGNgYGD5zwAEf/7uB9MMRfKN/0GCAF2AB88HHaN6AAAAAElFTkSuQmCC',
        _image_bbox(width=3, height=2),
        data_min=50,
        data_max=200,
        colors=palettable.get_map("magma_32").hex_colors
    )
    return [
        (np.array([[50., np.nan, 200.], [np.nan, 100., 50.]]), None, expected_gray_2_x_3),
        (np.array([[50., np.nan, 200.], [np.nan, 100., 50.]]), "magma", expected_gray_2_x_3_magma),
    ]


@pytest.mark.skipif(not np or not png or not palettable_module, reason="Requires numpy, pypng and palettable")
@pytest.mark.parametrize('image_data, cmap, expected', _test_params_list())
def test_image_spec(image_data, cmap, expected):
    image_data.flags.writeable = False
    spec = geom_imshow(image_data, cmap=cmap)
    assert spec.as_dict() == expected
