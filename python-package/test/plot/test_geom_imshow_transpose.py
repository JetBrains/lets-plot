#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

import pytest

from lets_plot._type_utils import LazyModule

np = LazyModule("numpy")
png = LazyModule("png")

from lets_plot.plot.geom_imshow_ import geom_imshow
from test_geom_imshow_util import _image_spec

# 'extent' which flips image along both: x,y-axis.
_extent = [1 + .5, 0 - .5, 1 + .5, 0 - .5]  # [left, right, bottom, top]


def _test_params_list():
    if not np:
        return []
    bbox = dict(xmin=_extent[1], xmax=_extent[0], ymin=_extent[3], ymax=_extent[2])
    expected_gray_2_x_3_href = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAAIUlEQVR4nA3BAQEAAAjCMDrRiU50otN1E28bbVESbCOJA+piDvI2Ub+LAAAAAElFTkSuQmCC'
    expected_rgb_1_x_2 = _image_spec(
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAYAAABytg0kAAAAGElEQVR4nGOYNo3hPwPDNBBm+D+NgeE/AD4uBusHg9tPAAAAAElFTkSuQmCC',
        bbox
    )
    return [
        (
            np.array([[0, 50, 100], [150, 200, 250]]),
            _image_spec(expected_gray_2_x_3_href, bbox, data_min=0, data_max=250)
        ),
        (
            np.array([[0., 50 / 255, 100 / 255], [150 / 255, 200 / 255, 250 / 255]]),
            _image_spec(expected_gray_2_x_3_href, bbox, data_min=0, data_max=250 / 255)
        ),
        (
            np.array([[[150, 0, 0], [0, 150, 0]], [[0, 0, 150], [150, 150, 0]]]),
            expected_rgb_1_x_2
        ),
        (
            np.array([[[150 / 255, 0., 0.], [0., 150 / 255, 0.]], [[0., 0., 150 / 255], [150 / 255, 150 / 255, 0.]]]),
            expected_rgb_1_x_2
        ),
    ]


@pytest.mark.skipif(not np or not png, reason="Requires numpy and pypng")
@pytest.mark.parametrize('image_data,expected', _test_params_list())
def test_image_spec(image_data, expected):
    image_data.flags.writeable = False

    spec = geom_imshow(image_data, extent=_extent)
    assert spec.as_dict() == expected
