import base64

from .core import aes
from .geom import _geom
from .util import as_boolean
from .util import is_ndarray

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
    This geom in not as flexible as geom_raster or geom_tile but vastly superior in the terms of
    rendering efficiency.

    This geom doesn't understands any aesthetics.
    It doesn't support color scales either.

    The following imgaes will be rendered depending on shape of input array:
    N x M       - gray-scale
    N x M x 3   - RGB
    N x M x 4   - RGBA

    Type of values in array can be int, uint or float of any size.
    The value for each component of integer arrays should be in the range [0,255]
    The value for each component of float arrays should be in the range [0,1] for RGB or RGBA images.

    If gray-scale is encoded as float array than values will be normalized. If arguments vmin/vmax are specified
    then they will be used in normalization. Otherwise min/max value will be computed from the image data.     

    Parameters
    ----------
    image_data : numpy.ndarray with shape (n, m) or (n, m, 3) or (n, m, 4)
        Specifies image type, size and pixel values.

    norm : bool
        False - disables default scaling of a 2-D float (luminance) input to the (0, 1) range.

    vmin, vmax : scalar, optional, default: None
        Used normalize luminance data. Only applied to gray-scale images encoded as float array.

    Returns
    -------
        geom object specification

    Examples
    --------
    >>> import numpy as np
    >>> from datalore.plot import *
    >>> image = np.random.choice([0.0, 1.0], [10, 100, 3])
    >>> ggplot() + geom_image(image)
    """
    if not is_ndarray(image_data):
        raise Exception("Invalid image_data: ndarray is expacted but was {}".format(type(image_data)))

    if image_data.ndim not in (2, 3):
        raise Exception("Invalid image_data: 2d or 3d array is expacted but was {}-dimentional".format(image_data.ndim))

    vmin = float(vmin) if vmin else None
    vmax = float(vmax) if vmax else None
    if vmin and vmax and vmin >= vmax:
        raise Exception("vmin value must be less then vmax value, was: {} >= {}".format(vmin, vmax))

    # Figure out the type of the image
    if image_data.ndim == 2:
        height, width = image_data.shape
        image_type = 'gray'
    else:
        height, width, nchannels = image_data.shape
        if nchannels == 3:
            image_type = 'rgb'
        elif nchannels == 4:
            image_type = 'rgba'
        else:
            raise Exception("Invalid image_data: num of channels in color image expected 3 (RGB) or 4 (RGBA) but was {}".format(nchannels))

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
        raise Exception("Invalid image_data: floating point or integer dtype is expacted but was '{}'".format(image_data.dtype))

    flat_bytes = bytearray()
    for v in image_data.ravel():
        flat_bytes.append(scaler(v))

    # Transform nd array to flat array containing ARGB values packed in one integer (32)
    image_bytes = base64.standard_b64encode(flat_bytes)
    image_bytes = image_bytes.decode('utf-8')
    image_spec = dict(
        width=width,
        height=height,
        type=image_type,
        bytes=image_bytes
    )

    # image bounds (including 1/2 pixel expand in all directions)
    xmin = [-0.5]
    ymin = [-0.5]
    xmax = [width - 1 + 0.5]
    ymax = [height - 1 + 0.5]
    mapping = aes(xmin=xmin, ymin=ymin, xmax=xmax, ymax=ymax)

    return _geom('image', mapping=mapping, image_spec=image_spec)
