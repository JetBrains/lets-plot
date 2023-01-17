#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from typing import Any

from lets_plot.plot.geom_imshow_ import geom_imshow
from lets_plot.plot.plot import ggplot, GGBunch
from lets_plot.plot.scale_position import scale_x_continuous, scale_y_continuous
from lets_plot.plot.theme_ import theme
from lets_plot.plot.util import is_ndarray

__all__ = ['image_matrix']


def image_matrix(image_data_array, cmap=None, *, norm=None, vmin=None, vmax=None, scale=1) -> GGBunch:
    """
    Display a set of images in a grid.
    Dimensions of the grid are determined by the shape of the input Numpy 2D array.

    Each element of the input 2D array is an 2D or 3D Numpy array itself
    specifying either a grayscale image (2D array) or a color RGB(A) image (3D array).
    For more information on image arrays please see the documentation of geom_imshow() function.

    Parameters
    ----------
    image_data_array : `ndarray`
        2D `numpy.ndarray` containing images.
    cmap : str, optional
        Name of colormap. For example "viridis", "magma", "plasma", "inferno", or any other colormap
        which is supported by the Palettable package (https://github.com/jiffyclub/palettable)
        This parameter is ignored for RGB(A) images.
    norm : bool, optional, default=True
        True - luminance values in grey-scale image will be scaled to [0-255] range using a linear scaler.
        False - disables scaling of luminance values in grey-scale image.
        This parameter is ignored for RGB(A) images.
    vmin, vmax : number, optional
        Define the data range used for luminance normalization in grey-scale images.
        This parameter is ignored for RGB(A) images or if parameter `norm=False`.
    scale : float, default=1.0
        Specifies the image size magnification factor.

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
            p = ggplot() + geom_imshow(
                image_data=image_data,
                cmap=cmap,
                norm=norm,
                vmin=vmin,
                vmax=vmax
            )
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
    # return h + 10, w + 10
    return h, w
