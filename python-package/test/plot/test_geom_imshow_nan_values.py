#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

import numpy as np
import pytest

from lets_plot.plot.geom_imshow_ import geom_imshow

# See notebook:
#       docs/testing/testing_imshow_nan_values.ipynb

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
        None,  # cmap
        expected_gray_2_x_3
    ))

    # with 'cmap'
    expected_gray_2_x_3_magma = _image_spec(
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAMAAACqqpYoAAADAFBMVEUAAAAAAAQBAAUBAQYBAQgCAQkCAgsCAg0DAw8DAxIEBBQFBBYGBRgGBRoHBhwIBx4JByAKCCILCSQMCSYNCikOCysQCy0RDC8SDTETDTQUDjYVDjgWDzsYDz0ZED8aEEIcEEQdEUceEUkgEUshEU4iEVAkElMlElUnElgpEVoqEVwsEV8tEWEvEWMxEWUzEGc0EGk2EGs4EGw5D247D3A9D3E/D3JAD3RCD3VED3ZFEHdHEHhJEHhKEHlMEXpOEXtPEntREnxSE3xUE31WFH1XFX5ZFX5aFn5cFn9dF39fGH9gGIBiGYBkGoBlGoBnG4BoHIFqHIFrHYFtHYFuHoFwH4FyH4FzIIF1IYF2IYF4IoF5IoJ7I4J8I4J+JIKAJYKBJYGDJoGEJoGGJ4GIJ4GJKIGLKYGMKYGOKoGQKoGRK4GTK4CULICWLICYLYCZLYCbLn+cLn+eL3+gL3+hMH6jMH6lMX6mMX2oMn2qM32rM3ytNHyuNHuwNXuyNXuzNnq3N3m4N3m6OHi8OXi9OXe/OnfAOnbCO3XEPHXFPHTHPXPIPnPKPnLMP3HNQHHPQHDQQW/SQm/TQ27VRG3WRWzYRWzZRmvbR2rcSGneSWjfSmjgTGfiTWbjTmXkT2TlUGTnUmPoU2LpVGLqVmHrV2DsWGDtWl/uW17vXV7wX17xYF3yYl3yZFzzZVz0Z1z0aVz1a1z2bFz2blz3cFz3clz4dFz4dlz5eF35eV35e136fV76f176gV/7g1/7hWD7h2H8iWH8imL8jGP8jmT8kGX9kmb9lGf9lmj9mGn9mmr9m2v+nWz+n23+oW7+o2/+pXH+p3L+qXP+qnT+rHb+rnf+sHj+snr+tHv+tnz+t37+uX/+u4H+vYL+v4T+wYX+wof+xIj+xor+yIz+yo3+zI/+zZD+z5L+0ZT+05X+1Zf+15n+2Jr92pz93J793qD94KH94qP946X95af956n96ar966z87K787rD88LL88rT89Lb89rj897n8+bv8+738/b/pkH4eAAABAHRSTlMA////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////Cpf0PAAAABBJREFUeJxjYGT4z8AQxggABbcBWIp93mUAAAAASUVORK5CYII=',
        width=3,
        height=2
    )

    # 2 x 3 array of floats containing NaN, cmap="magma"
    test_params_list.append((
        np.array([
            [50., np.nan, 200.],
            [np.nan, 100., 50.]
        ]),
        "magma",  # cmap
        expected_gray_2_x_3_magma
    ))

    @pytest.mark.parametrize('image_data, cmap, expected', test_params_list)
    def test_image_spec(self, image_data, cmap, expected):
        image_data.flags.writeable = False

        # spec = geom_imshow(image_data, extent=_extent)
        spec = geom_imshow(image_data, cmap=cmap)
        assert spec.as_dict() == expected
