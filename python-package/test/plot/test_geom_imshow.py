#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

import numpy as np
import pytest

from lets_plot.plot.geom_imshow_ import geom_imshow


def _image_spec(width, height, href):
    return dict(
        data_meta={},
        geom='image',
        href=href,
        mapping={},
        show_legend=True,
        xmin=-0.5,
        ymin=-0.5,
        xmax=width - 1 + 0.5,
        ymax=height - 1 + 0.5
    )


def _image_with_color_grey_scale_spec(width, height, href, data_min, data_max):
    layer_spec = _image_spec(width, height, href)
    layer_spec['color_by'] = 'paint_c'
    layer_spec['mapping'] = dict(paint_c=[data_min, data_max])
    scale_spec = dict(
        aesthetic='paint_c',
        start=0,
        end=1,
        name='',
        scale_mapper_kind='color_grey'
    )
    return {
        'feature-list': [
            dict(layer=layer_spec),
            dict(scale=scale_spec),
        ]
    }


def _append_test_params(params_list: list, image_data, expected_spec: dict):
    params_list.append(([image_data], {}, expected_spec))


class Test:
    test_params_list = []

    # -- gray --

    # 2 x 3 array of ints
    expected_gray_2_x_3_href='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAAAAAC4HznGAAAAD0lEQVR4nGNgAIL///8DAAYCAv507macAAAAAElFTkSuQmCC'

    _append_test_params(test_params_list,
                        np.array([
                            [0, 0, 0],
                            [255, 255, 255]
                        ]),
                        _image_with_color_grey_scale_spec(3, 2, expected_gray_2_x_3_href, data_min=0, data_max=255))

    # 2 x 3 array of floats
    _append_test_params(test_params_list,
                        np.array([
                            [0., 0., 0.],
                            [1., 1., 1.]
                        ]),
                        _image_with_color_grey_scale_spec(3, 2, expected_gray_2_x_3_href, data_min=0, data_max=1))

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
        image_data = args_list[0]
        image_data.flags.writeable = False

        spec = geom_imshow(*args_list)
        assert spec.as_dict() == expected
        # print(json.dumps(spec.as_dict(), indent=2))
        # print(json.dumps(expected, indent=2))
