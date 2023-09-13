#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

import numpy as np
import pytest

from lets_plot.plot.geom_imshow_ import geom_imshow

# 'extent' which flips image along both: x,y-axis.
_extent = [1 + .5, 0 - .5, 1 + .5, 0 - .5]  # [left, right, bottom, top]


def _image_spec(href):
    return dict(
        data_meta={},
        geom='image',
        href=href,
        mapping={},
        show_legend=True,
        xmin=_extent[1],
        xmax=_extent[0],
        ymin=_extent[3],
        ymax=_extent[2]
    )


def _image_with_color_grey_scale_spec(href, data_min, data_max):
    layer_spec = _image_spec(href)
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


class Test:
    test_params_list = []

    # -- gray --

    # 2 x 3 array of ints
    expected_gray_2_x_3_href = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAAAAAC4HznGAAAAEElEQVR4nGP4f2YmQ5oxAwAQXgL+el5zTgAAAABJRU5ErkJggg=='\

    test_params_list.append((
        np.array([
            [0, 50, 100],
            [150, 200, 250]
        ]),
        _image_with_color_grey_scale_spec(
            expected_gray_2_x_3_href,
            data_min=0,
            data_max=250
        )
    ))

    # 2 x 3 array of floats
    test_params_list.append((
        np.array([
            [0., 50 / 255, 100 / 255],
            [150 / 255, 200 / 255, 250 / 255]
        ]),
        _image_with_color_grey_scale_spec(
            expected_gray_2_x_3_href,
            data_min=0,
            data_max=250 / 255
        )
    ))

    # -- rgb --

    # 1 x 2 x 3 array of ints
    expected_RGB_1_x_2 = _image_spec(
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAIAAAD91JpzAAAAEklEQVR4nGOYNo2BgWEaCAEJABgUAu/euM7fAAAAAElFTkSuQmCC'
    )
    test_params_list.append((
        np.array([
            [[150, 0, 0], [0, 150, 0]],
            [[0, 0, 150], [150, 150, 0]]
        ]),
        expected_RGB_1_x_2
    ))
    # # 1 x 2 x 3 array of floats
    test_params_list.append((
        np.array([
            [[150 / 255, 0., 0.], [0., 150 / 255, 0.]],
            [[0., 0., 150 / 255], [150 / 255, 150 / 255, 0.]]
        ]),
        expected_RGB_1_x_2
    ))

    @pytest.mark.parametrize('image_data,expected', test_params_list)
    def test_image_spec(self, image_data, expected):
        image_data.flags.writeable = False

        spec = geom_imshow(image_data, extent=_extent)
        assert spec.as_dict() == expected
