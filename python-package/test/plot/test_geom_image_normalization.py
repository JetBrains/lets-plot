#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from typing import NamedTuple

import numpy as np
import pytest

from lets_plot.plot.geom_image_ import geom_image


class TestParams(NamedTuple):
    image_data_0: np.ndarray
    image_data_1: np.ndarray
    expected_equal: bool
    descr: str


class Test:
    test_params_list = [
        # -- gray --
        # 2 x 3 array of ints -> no normalization (ints)
        TestParams(
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
        TestParams(
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
        TestParams(
            image_data_0=np.array([
                [[0, 0, 0],
                 [100, 100, 500]]
            ]),
            image_data_1=np.array([
                [[0, 0, 0],
                 [100, 100, 500 % 256]]  # int(v) % 256
            ]),
            expected_equal=True,
            descr='rgb / int : no normalization, values will be brought to range [0,255] by appying mod op',
        ),
        # float  vs  int
        TestParams(
            # floats
            image_data_0=np.array([
                [[0., 0, 0],
                 [1, 1, 1.2]]
            ]),
            # ints
            image_data_1=np.array([
                [[0, 0, 0],
                 [255, 255, 50]]  # int(v * 255 + .5) & 0xff
            ]),
            expected_equal=True,
            descr='rgb: float  vs  int'
        )
    ]

    @pytest.mark.parametrize('params', test_params_list, ids=lambda d: d.descr)
    def test_image_spec(self, params: TestParams):
        spec_0 = geom_image(image_data=params.image_data_0)
        spec_1 = geom_image(image_data=params.image_data_1)

        if params.expected_equal:
            assert spec_0.as_dict() == spec_1.as_dict()
        else:
            assert spec_0.as_dict() != spec_1.as_dict()
