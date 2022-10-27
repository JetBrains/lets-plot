#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from typing import Any

from lets_plot.plot.geom_imshow_ import geom_imshow
from lets_plot.plot.plot import ggplot, GGBunch
from lets_plot.plot.scale import scale_x_continuous, scale_y_continuous
from lets_plot.plot.theme_ import theme
from lets_plot.plot.util import is_ndarray

__all__ = ['image_matrix']


def image_matrix(image_data_array, *, norm: bool = None, scale=1) -> GGBunch:
    """
    Display images in a grid.
    The grid dimensions are determined by shape of the input 2D ndarray.

    Elements of the input 2D array are images specified by ndarrays with shape
    (n, m) or (n, m, 3) or (n, m, 4).

    Parameters
    ----------
    image_data_array : `ndarray`
        2D `numpy.ndarray` containing images. Specifies dimensions of output grid.
    norm : bool, default=True
        False value disables default scaling of a luminance (grayscale) images to the (0, 255) range.
    scale : float, default=1.0
        Specifies magnification factor.

    Returns
    -------
    `GGBunch`
        Plot bunch object.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        from lets_plot.bistro.im import *
        LetsPlot.setup_html()
        np.random.seed(42)
        image = np.random.randint(256, size=(64, 64, 3))
        matrix = np.empty((2, 3), dtype=object)
        matrix.fill(image)
        image_matrix(matrix)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 12

        import numpy as np
        from lets_plot import *
        from lets_plot.bistro.im import *
        LetsPlot.setup_html()
        rows, cols = 3, 3
        matrix = np.empty((rows, cols), dtype=object)
        for r in range(rows):
            for c in range(cols):
                w, h = 32 + 16 * c, 32 + 16 * r
                matrix[r][c] = 256 * np.linspace(np.linspace(0, .5, w), \\
                                                 np.linspace(.5, .5, w), h)
        image_matrix(matrix, norm=False, scale=1.5)

    """

    if not is_ndarray(image_data_array):
        raise Exception("Invalid image_data_array: 2d ndarray is expacted but was {}".format(type(image_data_array)))

    if image_data_array.ndim != 2:
        raise Exception("Invalid image_data_array: 2-dimentional ndarray is expacted but was {}-dimentional".format(
            image_data_array.ndim))

    rows, cols = image_data_array.shape
    if cols * rows <= 0:
        return

    w_max = 0
    h_max = 0
    for row in range(rows):
        for col in range(cols):
            image_data = image_data_array[row][col]
            if image_data is None:
                continue

            _assert_image_data(image_data)
            h, w = image_data.shape[0:2]
            h, w = _expand_h_w(h, w, scale)
            w_max = max(w_max, w)
            h_max = max(h_max, h)

    # no gaps between image and plot edges
    options = scale_x_continuous(expand=[0, 0])
    options += scale_y_continuous(expand=[0, 0])

    # show no axis
    options += theme(axis_line='blank', axis_title='blank', axis_ticks='blank', axis_text='blank')

    ggbunch = GGBunch()

    for row in range(rows):
        for col in range(cols):
            image_data = image_data_array[row][col]
            if image_data is None:
                continue

            h, w = image_data.shape[0:2]
            h, w = _expand_h_w(h, w, scale)
            p = ggplot() + geom_imshow(image_data=image_data, norm=norm)
            p += options
            ggbunch.add_plot(p, col * w_max, row * h_max, w, h)

    return ggbunch


def _assert_image_data(image_data: Any) -> None:
    try:
        import numpy as np
        if not isinstance(image_data, np.ndarray):
            raise Exception("Invalid image_data: ndarray is expacted but was {}".format(type(image_data)))

        if image_data.ndim not in (2, 3):
            raise Exception(
                "Invalid image_data: 2d or 3d array is expacted but was {}-dimentional".format(image_data.ndim))
    except ImportError:
        pass


def _expand_h_w(h, w, scale):
    if scale:
        h *= scale
        w *= scale

    # Mininum plot geom area size: 50 x 50
    h = 50 if h < 50 else h
    w = 50 if w < 50 else w

    # Currently plot has not customizable 10px padding on the right and the bottom
    return h + 10, w + 10
