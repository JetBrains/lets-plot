#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

import numpy as np
import pytest

from lets_plot.plot.geom_imshow_ import geom_imshow


def _image_spec(href, width, height):
    return dict(
        data_meta={},
        geom='image',
        href=href,
        mapping={},
        xmin=-0.5,
        ymin=-0.5,
        xmax=width - 1 + 0.5,
        ymax=height - 1 + 0.5
    )


class Test:
    test_params_list = []

    expected_gray_2_x_3 = _image_spec(
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAQAAAA3fa6RAAAAFUlEQVR4nGNg+M/A8B+IGUL/M/wHACJBBVE68QEEAAAAAElFTkSuQmCC',
        width=3,
        height=2
    )

    # 2 x 3 array of floats containing NaN
    test_params_list.append((
        np.array([
            [50., np.nan, 200.],
            [np.nan, 100., 50.]
        ]),
        expected_gray_2_x_3
    ))

    @pytest.mark.parametrize('image_data,expected', test_params_list)
    def test_image_spec(self, image_data, expected):
        # spec = geom_imshow(image_data, extent=_extent)
        spec = geom_imshow(image_data)
        assert spec.as_dict() == expected
