#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import base64
import io

from .geom import _geom
from .util import as_boolean
from .util import is_ndarray

try:
    import png
except ImportError:
    png = None

try:
    import numpy
except ImportError:
    numpy = None

__all__ = ['geom_image']


def _scaler_0_1_byte(v):
    # map float range [0,1] to int range [0, 255]
    return int(v * 255 + .5) & 0xff


def _scaler_to_byte(v, offset, ratio):
    # map range [vmin, vmax] to int range [0, 255]
    return int((v + offset) * ratio + .5) & 0xff


def _scaler_0_255_byte(v):
    # map range [0, 255] to int range [0, 255]
    return int(v) % 256


def geom_image(image_data, norm=None, vmin=None, vmax=None):
    """
    Displays image specified by ndarray with shape (n, m) or (n, m, 3) or (n, m, 4).
    This geom is not as flexible as `geom_raster()` or `geom_tile()`
    but vastly superior in the terms of rendering efficiency.   

    Parameters
    ----------
    image_data : `ndarray`
        Specifies image type, size and pixel values in `numpy.ndarray`.
    norm : bool, default=True
        False - disables default scaling of a 2-D float (luminance) input to the (0, 1) range.
    vmin : float, default=None
        Uses normalized luminance data. Only applied to gray-scale images encoded as float array.
    vmax : float, default=None
        Uses normalized luminance data. Only applied to gray-scale images encoded as float array.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Notes
    -----
    This geom doesn't understand any aesthetics.
    It doesn't support color scales either.

    The following images will be rendered depending on the input array:

    - n x m       - gray-scale,
    - n x m x 3   - RGB,
    - n x m x 4   - RGBA.

    The type of values in array can be int, uint or float of any size.
    The value for each component of integer arrays should be in the range [0, 255].
    The value for each component of float arrays should be in the range [0, 1]
    for RGB or RGBA images.

    If gray-scale is encoded as float array then the values will be normalized.
    If arguments `vmin`/`vmax` are specified, they will be used in normalization.
    Otherwise, min/max value will be computed from the image data.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        image = np.random.randint(256, size=(64, 64, 4))
        ggplot() + geom_image(image)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 64
        image = 256 * np.linspace(np.linspace(0, .5, n), \\
                                  np.linspace(.5, .5, n), n)
        ggplot() + geom_image(image, norm=False)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        image = np.random.normal(size=(64, 64))
        ggplot() + geom_image(image, vmin=-1, vmax=1)

    """

    if png == None:
        raise Exception("pypng is not installed")

    if not is_ndarray(image_data):
        raise Exception("Invalid image_data: ndarray is expected but was {}".format(type(image_data)))

    if image_data.ndim not in (2, 3):
        raise Exception("Invalid image_data: 2d or 3d array is expected but was {}-dimensional".format(image_data.ndim))

    vmin = float(vmin) if vmin else None
    vmax = float(vmax) if vmax else None
    if vmin and vmax and vmin >= vmax:
        raise Exception("vmin value must be less then vmax value, was: {} >= {}".format(vmin, vmax))

    # Figure out the type of the image
    if image_data.ndim == 2:
        height, width = image_data.shape
        image_type = 'gray'
        nchannels = 1
    else:
        height, width, nchannels = image_data.shape
        if nchannels == 3:
            image_type = 'rgb'
        elif nchannels == 4:
            image_type = 'rgba'
        else:
            raise Exception(
                "Invalid image_data: num of channels in color image expected 3 (RGB) or 4 (RGBA) but was {}".format(
                    nchannels))

    # Choose scaler function (sometimes - normalization)
    if image_data.dtype.kind == 'f':
        if image_type == 'gray':
            normaize = as_boolean(norm, default=True)

            if normaize:
                # normalize values (gray-scale, floats)
                lower = vmin if vmin else image_data.min()
                upper = vmax if vmax else image_data.max()
                if lower == upper:
                    # 'normalize' to grey
                    def scaler(v):
                        return 127
                else:
                    offset = -lower
                    ratio = 255. / (upper - lower)

                    def scaler(v):
                        return _scaler_to_byte(v, offset, ratio)

            else:
                # do not normalize
                scaler = _scaler_0_255_byte

        else:
            # do not normalize values (colors)
            scaler = _scaler_0_1_byte
    elif image_data.dtype.kind in ('i', 'u'):
        # do not normalize values (ints)
        scaler = _scaler_0_255_byte
    else:
        raise Exception(
            "Invalid image_data: floating point or integer dtype is expected but was '{}'".format(image_data.dtype))

    # set output type to int8 - pypng produces broken colors with other types
    scale = numpy.vectorize(scaler, otypes=[numpy.int8])
    # from [[[R, G, B], [R, G, B]], ...] to [[R, G, B, R, G, B],..], or pypng will fail
    image_2d = scale(image_data).reshape(-1, width * nchannels)

    png_bytes = io.BytesIO()
    png.Writer(
        width=width,
        height=height,
        greyscale=(image_type == 'gray'),
        alpha=(image_type == 'rgba'),
        bitdepth=8
    ).write(png_bytes, image_2d)

    href = 'data:image/png;base64,' + str(base64.standard_b64encode(png_bytes.getvalue()), 'utf-8')

    # image bounds (including 1/2 pixel expand in all directions)
    xmin = -0.5
    ymin = -0.5
    xmax = width - 0.5
    ymax = height - 0.5

    bbox = dict(xmin=xmin, ymin=ymin, xmax=xmax, ymax=ymax)
    return _geom('image', **bbox, href=href)
