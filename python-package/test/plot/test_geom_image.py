#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from typing import NamedTuple

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


def _append_test_parameters_0(parameter_list: list, image_data, expected_spec: dict):
    parameter_list.append(([image_data], {}, expected_spec))


class TestOutputWithDifferentArrayShapeAndDtype:
    parameter_list = []

    # -- gray --

    # 2 x 3 array of ints
    expected_gray_2_x_3 = _image_spec(
        3, 2,
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAAAAAC4HznGAAAAD0lEQVR4nGNgAIL///8DAAYCAv507macAAAAAElFTkSuQmCC'
    )
    _append_test_parameters_0(parameter_list,
                              np.array([
                                  [0, 0, 0],
                                  [255, 255, 255]
                              ]),
                              expected_gray_2_x_3)

    # 2 x 3 array of floats
    _append_test_parameters_0(parameter_list,
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
    _append_test_parameters_0(parameter_list,
                              np.array([
                                  [[0, 0, 0], [255, 255, 255]]
                              ]),
                              expected_RGB_1_x_2)

    # 1 x 2 x 3 array of floats
    _append_test_parameters_0(parameter_list,
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
    _append_test_parameters_0(parameter_list,
                              np.array([
                                  [[0, 0, 0, 128], [255, 255, 255, 128]]
                              ]),
                              expected_RGBA_1_2)

    # 1 x 2 x 4 array of floats
    _append_test_parameters_0(parameter_list,
                              np.array([
                                  [[0., 0., 0., .5], [1., 1., 1., .5]]
                              ]),
                              expected_RGBA_1_2)

    @pytest.mark.parametrize('args_list,args_dict,expected', parameter_list)
    def test_geom_image(self, args_list, args_dict, expected):
        spec = geom_image(*args_list, **args_dict)
        assert spec.as_dict() == expected
        # print(json.dumps(spec.as_dict(), indent=2))
        # print(json.dumps(expected, indent=2))


def _append_test_parameters_1(parameter_list: list, image_data_0, image_data_1, expected_equal: bool):
    parameter_list.append((image_data_0, image_data_1, expected_equal))


class TestCase(NamedTuple):
    image_data_0: np.ndarray
    image_data_1: np.ndarray
    expected_equal: bool
    descr: str


class TestNormalizationWithDifferentArrayShapeAndDtype:
    @pytest.mark.parametrize('data', [
        # -- gray --
        # 2 x 3 array of ints -> no normalization (ints)
        TestCase(
            image_data_0=np.array([
                [0, 0, 0],
                [255, 255, 255]
            ]),
            image_data_1=np.array([
                [0, 0, 0],
                [100, 100, 100]
            ]),
            expected_equal=False,
            descr='gray: 2 x 3 array of ints -> no normalization (ints)',
        ),
        # 2 x 3 array of floats -> normalization (grayscale + floats)
        TestCase(
            image_data_0=np.array([
                [0., 0, 0],
                [255, 255, 255]
            ]),
            image_data_1=np.array([
                [0., 0, 0],
                [100, 100, 100]
            ]),
            expected_equal=True,
            descr='gray: 2 x 3 array of floats -> normalization (grayscale + floats)',
        ),
        # -- rgb --
        # int (will be brought to range [0,255] by appying mod op)
        TestCase(
            np.array([
                [[0, 0, 0],
                 [100, 100, 500]]
            ]),
            np.array([
                [[0, 0, 0],
                 [100, 100, 500 % 256]]  # int(v) % 256
            ]),
            True,
            descr='rgb / int : no normalization, values will be brought to range [0,255] by appying mod op',
        ),
        # float  vs  int
        TestCase(
            # floats
            np.array([
                [[0., 0, 0],
                 [1, 1, 1.2]]
            ]),
            # ints
            np.array([
                [[0, 0, 0],
                 [255, 255, 50]]  # int(v * 255 + .5) & 0xff
            ]),
            True,
            'rgb: float  vs  int'
        )
    ], ids=lambda d: d.descr)
    def test_geom_image(self, data: TestCase):
        spec_0 = geom_image(image_data=data.image_data_0)
        spec_1 = geom_image(image_data=data.image_data_1)

        if data.expected_equal:
            assert spec_0.as_dict() == spec_1.as_dict()
        else:
            assert spec_0.as_dict() != spec_1.as_dict()
