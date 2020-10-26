#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

import sys

__all__ = ['gg_image_matrix']

from lets_plot.bistro.im import image_matrix


def gg_image_matrix(image_data_array, *, norm: bool = None, scale=1) -> None:
    """
    Depricated, see `image_matrix` in `lets_plot.bistro.im`

    Display images in a grid.
    The grid dimensions are determined by shape of the input 2D ndarray.

    Elements of the input 2D array are images specified by ndarrays with shape (n, m) or (n, m, 3) or (n, m, 4).

    Parameters
    ----------
    image_data_array : 2D numpy.ndarray containing images
        Specifies dimensions of output grid

    norm : bool
        False - disables default scaling of a luminance (grayscale) images to the (0, 255) range.

    scale : scalar, default: 1
        Specifies magnification factor

    Returns
    -------
        None
    """

    sys.stderr.write(
        """gg_image_matrix is deprecated, use: 
                from lets_plot.bistro.im import image_matrix
                image_matrix()
        """)

    ggbunch = image_matrix(image_data_array, norm=norm, scale=scale)
    ggbunch.show()
