#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

import numpy as np
import pytest

from lets_plot.plot.geom_image_ import geom_image


def _image_spec(width, height, href):
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


def _append_test_params(params_list: list, image_data, expected_spec: dict):
    params_list.append(([image_data], {}, expected_spec))


class Test:
    test_params_list = []

    # -- gray --

    # 2 x 3 array of ints
    expected_gray_2_x_3 = _image_spec(
        3, 2,
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAAAAAC4HznGAAAAD0lEQVR4nGNgAIL///8DAAYCAv507macAAAAAElFTkSuQmCC'
    )
    _append_test_params(test_params_list,
                        np.array([
                            [0, 0, 0],
                            [255, 255, 255]
                        ]),
                        expected_gray_2_x_3)

    # 2 x 3 array of floats
    _append_test_params(test_params_list,
                        np.array([
                            [0., 0., 0.],
                            [1., 1., 1.]
                        ]),
                        expected_gray_2_x_3)

    # -- rgb --

    # 1 x 2 x 3 array of ints
    expected_RGB_1_x_2 = _image_spec(
        2, 1,
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAABCAIAAAB7QOjdAAAAD0lEQVR4nGNgYGD4//8/AAYBAv4CsjmuAAAAAElFTkSuQmCC'
    )
    _append_test_params(test_params_list,
                        np.array([
                            [[0, 0, 0], [255, 255, 255]]
                        ]),
                        expected_RGB_1_x_2)

    # 1 x 2 x 3 array of floats
    _append_test_params(test_params_list,
                        np.array([
                            [[0., 0., 0.], [1., 1., 1.]]
                        ]),
                        expected_RGB_1_x_2)

    # -- rgb + alpha --

    # 1 x 2 x 4 array of ints
    expected_RGBA_1_2 = _image_spec(
        2, 1,
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAABCAYAAAD0In+KAAAAEUlEQVR4nGNgYGBo+P//fwMADAAD/jYVGcgAAAAASUVORK5CYII='
    )
    _append_test_params(test_params_list,
                        np.array([
                            [[0, 0, 0, 128], [255, 255, 255, 128]]
                        ]),
                        expected_RGBA_1_2)

    # 1 x 2 x 4 array of floats
    _append_test_params(test_params_list,
                        np.array([
                            [[0., 0., 0., .5], [1., 1., 1., .5]]
                        ]),
                        expected_RGBA_1_2)

    @pytest.mark.parametrize('args_list,args_dict,expected', test_params_list)
    def test_image_spec(self, args_list, args_dict, expected):
        spec = geom_image(*args_list, **args_dict)
        assert spec.as_dict() == expected
        # print(json.dumps(spec.as_dict(), indent=2))
        # print(json.dumps(expected, indent=2))
