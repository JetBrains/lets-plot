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
#       docs/testing/testing_imshow_alpha.ipynb


class Test:
    test_params_list = []

    # Grayscale images

    # Basic
    test_params_list.append((
        np.array([
            [50, 150, 200],
            [200, 100, 50]
        ]),
        None,  # cmap
        None,  # norm
        _image_spec(
            'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAAHklEQVR4nGNgYGBoWLVqVcP///8bGEBEaGhoA0gQAKIaC/gpZfxGAAAAAElFTkSuQmCC',
            _image_bbox(width=3, height=2),
            data_min=50,
            data_max=200
        )
    ))

    # No normalization
    test_params_list.append((
        np.array([
            [50, 150, 200],
            [200, 100, 50]
        ]),
        None,  # cmap
        False,  # norm
        _image_spec(
            'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAAHklEQVR4nGMwMjKqnzZtWv2JEyfqGUBESkpKPUgQAJzLC8Wo8cSRAAAAAElFTkSuQmCC',
            _image_bbox(width=3, height=2),
            data_min=50,
            data_max=200,
            start=50 / 255,
            end=200 / 255
        )
    ))

    # With NaN-s
    test_params_list.append((
        np.array([
            [50., np.nan, 200.],
            [np.nan, 100., 50.]
        ]),
        None,  # cmap
        None,  # norm
        _image_spec(
            'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAAG0lEQVR4nGNgYGBoAGKG////g2mG0NBQEKMBAEvjBf2G1vnvAAAAAElFTkSuQmCC',
            _image_bbox(width=3, height=2),
            data_min=50,
            data_max=200
        )
    ))

    # with 'cmap'
    expected_gray_2_x_3_magma = _image_spec(
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAAHklEQVR4nGNgYGBp+JgQ2/Dn7/4GBhBRJN/YABIEAJZ7Czlt95PHAAAAAElFTkSuQmCC',
        _image_bbox(width=3, height=2),
        data_min=50,
        data_max=200,
        colors=palettable.get_map("magma_32").hex_colors
    )

    test_params_list.append((
        np.array([
            [50, 150, 200],
            [200, 100, 50]
        ]),
        "magma",  # cmap
        None,  # norm
        expected_gray_2_x_3_magma
    ))

    # Containing NaN, cmap="magma"
    expected_gray_2_x_3_magma_nan = _image_spec(
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAAG0lEQVR4nGNgYGBpYACCP3/3g2mGIvnGBpAgAEiqBdPTycZVAAAAAElFTkSuQmCC',
        _image_bbox(width=3, height=2),
        data_min=50,
        data_max=200,
        colors=palettable.get_map("magma_32").hex_colors
    )

    test_params_list.append((
        np.array([
            [50., np.nan, 200.],
            [np.nan, 100., 50.]
        ]),
        "magma",  # cmap
        None,  # norm
        expected_gray_2_x_3_magma_nan
    ))

    # Color images

    # RGB
    test_params_list.append((
        np.array([
            [[255, 0, 0], [0, 255, 0], [0, 0, 255]],
            [[0, 255, 0], [0, 0, 255], [255, 0, 0]]
        ]),
        None,  # cmap
        None,  # norm
        _image_spec(
            'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAAGElEQVR4nGP4z8BQz/AfiBn+gzCEARIEAHMECPW+3yLrAAAAAElFTkSuQmCC',
            _image_bbox(width=3, height=2),
        )
    ))

    # RGBA
    test_params_list.append((
        np.array([
            [[1, 0, 0, 1], [0, 1, 0, 1], [0, 0, 1, 1]],
            [[0, 1, 0, 0.3], [0, 0, 1, 0.3], [1, 0, 0, 0.3]]
        ]),
        None,  # cmap
        None,  # norm
        _image_spec(
            'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAAHElEQVR4nGP4z8DQwPAfiBn+gzCDGpBQAwqqAQBuAwft32FBFAAAAABJRU5ErkJggg==',
            _image_bbox(width=3, height=2),
        )
    ))

    @pytest.mark.parametrize('image_data, cmap, norm, expected', test_params_list)
    def test_image_spec(self, image_data, cmap, norm, expected):
        image_data.flags.writeable = False
        # spec = geom_imshow(image_data, extent=_extent)
        spec = geom_imshow(image_data, cmap=cmap, norm=norm, alpha=0.5)
        assert spec.as_dict() == expected
