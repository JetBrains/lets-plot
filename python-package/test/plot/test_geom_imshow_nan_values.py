#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

import numpy as np
import pytest
from palettable.matplotlib import matplotlib as palettable

from lets_plot.plot.geom_imshow_ import geom_imshow
from test_geom_imshow_util import _image_spec, _image_bbox


# See notebook:
#       docs/testing/testing_imshow_nan_values.ipynb

class Test:
    test_params_list = []

    expected_gray_2_x_3 = _image_spec(
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAAGUlEQVR4nGNgYGD4zwAigABEM4SGhoIY/wFguQf5zEefdAAAAABJRU5ErkJggg==',
        _image_bbox(width=3, height=2),
        data_min=50,
        data_max=200
    )

    # 2 x 3 array of floats containing NaN
    test_params_list.append((
        np.array([
            [50., np.nan, 200.],
            [np.nan, 100., 50.]
        ]),
        None,  # cmap
        expected_gray_2_x_3
    ))

    # with 'cmap'
    expected_gray_2_x_3_magma = _image_spec(
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAAG0lEQVR4nGNgYGD5zwAEf/7uB9MMRfKN/0GCAF2AB88HHaN6AAAAAElFTkSuQmCC',
        _image_bbox(width=3, height=2),
        data_min=50,
        data_max=200,
        colors=palettable.get_map("magma_32").hex_colors
    )

    # 2 x 3 array of floats containing NaN, cmap="magma"
    test_params_list.append((
        np.array([
            [50., np.nan, 200.],
            [np.nan, 100., 50.]
        ]),
        "magma",  # cmap
        expected_gray_2_x_3_magma
    ))

    @pytest.mark.parametrize('image_data, cmap, expected', test_params_list)
    def test_image_spec(self, image_data, cmap, expected):
        image_data.flags.writeable = False

        # spec = geom_imshow(image_data, extent=_extent)
        spec = geom_imshow(image_data, cmap=cmap)
        assert spec.as_dict() == expected
