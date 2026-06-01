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


def _append_test_params(params_list: list, image_data, expected_spec: dict, **kwargs):
    params_list.append(([image_data], kwargs, expected_spec))


def _test_params_list():
    if not np or not palettable_module:
        return []
    from palettable.matplotlib import matplotlib as palettable

    test_params_list = []
    expected_gray_2_x_3_href = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAAEUlEQVR4nGNgYGD4D8f/kQAAg5YO8q3M4hsAAAAASUVORK5CYII='
    _append_test_params(
        test_params_list,
        np.array([[0, 0, 0], [255, 255, 255]]),
        _image_spec(expected_gray_2_x_3_href, _image_bbox(width=3, height=2), data_min=0, data_max=255)
    )
    _append_test_params(
        test_params_list,
        np.array([[0., 0., 0.], [1., 1., 1.]]),
        _image_spec(expected_gray_2_x_3_href, _image_bbox(width=3, height=2), data_min=0, data_max=1)
    )
    _append_test_params(
        test_params_list,
        np.array([[150, 75, 0], [200, 150, 75]]),
        _image_spec(
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAAHUlEQVR4nGP43Z74v1mt8T8DA8t/hj9/9/+HCQAAubEO0e5fj4cAAAAASUVORK5CYII=",
            _image_bbox(width=3, height=2),
            data_min=0.0,
            data_max=200.0,
            colors=palettable.get_map("magma_32").hex_colors
        ),
        cmap='magma'
    )
    expected_rgb_1_x_2 = _image_spec(
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAABCAYAAAD0In+KAAAADklEQVR4nGNgYGD4DwIADvoE/EWwHYsAAAAASUVORK5CYII=',
        _image_bbox(width=2, height=1)
    )
    _append_test_params(test_params_list, np.array([[[0, 0, 0], [255, 255, 255]]]), expected_rgb_1_x_2)
    _append_test_params(test_params_list, np.array([[[0., 0., 0.], [1., 1., 1.]]]), expected_rgb_1_x_2)
    expected_rgba_1_2 = _image_spec(
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAABCAYAAAD0In+KAAAAEUlEQVR4nGNgYGBo+P//fwMADAAD/jYVGcgAAAAASUVORK5CYII=',
        _image_bbox(width=2, height=1)
    )
    _append_test_params(test_params_list, np.array([[[0, 0, 0, 128], [255, 255, 255, 128]]]), expected_rgba_1_2)
    _append_test_params(test_params_list, np.array([[[0., 0., 0., .5], [1., 1., 1., .5]]]), expected_rgba_1_2)
    return test_params_list


@pytest.mark.skipif(not np or not png or not palettable_module, reason="Requires numpy, pypng and palettable")
@pytest.mark.parametrize('args_list,args_dict,expected', _test_params_list())
def test_image_spec(args_list, args_dict, expected):
    image_data = args_list[0]
    image_data.flags.writeable = False

    spec = geom_imshow(*args_list, **args_dict)
    assert spec.as_dict() == expected
