#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from typing import NamedTuple

import pytest

from lets_plot._type_utils import LazyModule

np = LazyModule("numpy")
png = LazyModule("png")

from lets_plot.plot.geom_imshow_ import geom_imshow


class TestParams(NamedTuple):
    image_data_0: object
    image_data_1: object
    normalize: bool
    expected_equal: bool
    descr: str
    show_legend: bool = True


def _test_params_list():
    if not np:
        return []
    return [
        # -- gray --
        # 2 x 3 array of ints -> normalized (default)
        TestParams(
            image_data_0=np.array([
                [0, 0, 0],
                [255, 255, 255]
            ]),
            image_data_1=np.array([
                [0, 0, 0],
                [100, 100, 100]
            ]),
            normalize=None,
            expected_equal=True,
            descr='gray: 2 x 3 array of ints -> normalized (default)',
            show_legend=False
        ),
        # 2 x 3 array of ints -> no normalization
        TestParams(
            image_data_0=np.array([
                [0, 0, 0],
                [255, 255, 255]
            ]),
            image_data_1=np.array([
                [0, 0, 0],
                [100, 100, 100]
            ]),
            normalize=False,
            expected_equal=False,
            descr='gray: 2 x 3 array of ints -> no normalization',
        ),
        # 2 x 3 array of floats -> normalized (default)
        TestParams(
            image_data_0=np.array([
                [0., 0., 0.],
                [.1, .1, .1]
            ]),
            image_data_1=np.array([
                [0., 0., 0.],
                [100., 100., 100.]
            ]),
            normalize=None,
            expected_equal=True,
            descr='gray: 2 x 3 array of floats -> normalization (default)',
            show_legend=False
        ),
        # -- rgb --
        # int (will be brought to range [0,255] by clipping
        TestParams(
            image_data_0=np.array([
                [[0, 0, 0],
                 [100, 100, 500]]
            ]),
            image_data_1=np.array([
                [[0, 0, 0],
                 [100, 100, 255]]
            ]),
            normalize=None,
            expected_equal=True,
            descr='rgb / int : no normalization, values will be brought to range [0,255] by appying mod op',
        ),
        # float  vs  int
        TestParams(
            # floats
            image_data_0=np.array([
                [[0., 0., 0.],
                 [1., 1., 1.2]] # will be clipped -> 255
            ]),
            # ints
            image_data_1=np.array([
                [[0, 0, 0],
                 [255, 255, 255]]
            ]),
            normalize=None,
            expected_equal=True,
            descr='rgb: float  vs  int'
        )
    ]


@pytest.mark.skipif(not np or not png, reason="Requires numpy and pypng")
@pytest.mark.parametrize('params', _test_params_list(), ids=lambda d: d.descr)
def test_image_spec(params: TestParams):
    params.image_data_0.flags.writeable = False
    params.image_data_1.flags.writeable = False

    spec_0 = geom_imshow(image_data=params.image_data_0, norm=params.normalize, show_legend=params.show_legend)
    spec_1 = geom_imshow(image_data=params.image_data_1, norm=params.normalize, show_legend=params.show_legend)

    if params.expected_equal:
        assert spec_0.as_dict() == spec_1.as_dict()
    else:
        assert spec_0.as_dict() != spec_1.as_dict()
