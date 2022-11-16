#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import base64
import io

from .geom import _geom
from .util import as_boolean
from .util import is_ndarray

from time import time

try:
    import png
except ImportError:
    png = None

try:
    import numpy
except ImportError:
    numpy = None

try:
    from palettable.matplotlib import matplotlib as palettable
except ImportError:
    palettable = None

__all__ = ['geom_imshow', 'geom_image']


def _hex2rgb(hex_c):
    hex_s = hex_c.lstrip('#')
    return [int(hex_s[i:i + 2], 16) for i in (0, 2, 4)]


def _hex2rgb_np_int8(hex_c):
    return numpy.array(_hex2rgb(hex_c), dtype=numpy.int8)


def _normalize_2D(image_data, norm, vmin, vmax):
    """
    Takes numpy 2D array of float or int-s and
    returns 2D array of ints with the target range [0..255].
    Values outside the target range will be later clipped.
    """
    image_data = image_data.astype(numpy.float32)
    vmin = float(vmin if vmin is not None else image_data.min())
    vmax = float(vmax if vmax is not None else image_data.max())
    if vmin > vmax:
        raise ValueError("vmin value must be less then vmax value, was: {} > {}".format(vmin, vmax))

    image_data = image_data.clip(vmin, vmax)

    normaize = as_boolean(norm, default=True)
    if normaize:
        if vmin == vmax:
            image_data[True] = 127.
        else:
            ratio = 255. / (vmax - vmin)
            image_data = image_data - vmin
            image_data = image_data * ratio + 0.5
    else:
        # no normalization - just round values to the nearest int.
        image_data = image_data + 0.5

    return image_data


def _normalize_RGBa(image_data):
    """
    Takes numpy 3D array of float or int-s:
    - (M, N, 3): an image with RGB values (0-1 float or 0-255 int).
    - (M, N, 4): an image with RGBA values (0-1 float or 0-255 int).

    returns 3D array of ints with the target range [0..255].
    Values outside the target range will be later clipped.
    """
    if image_data.dtype.kind == 'f':
        def scaler(v):
            return int(v * 255 + .5)

        scaler_v = numpy.vectorize(scaler)
        image_data = scaler_v(image_data)

    return image_data


def geom_image(image_data, cmap=None, norm=None, *, vmin=None, vmax=None, extent=None):
    """
    Function `geom_image()` is deprecated.
    Please, use `geom_imshow()` instead.

    """
    print("WARN: The function geom_image() is deprecated and will be removed in future releases.\n"
          "      Please, use geom_imshow() instead.")

    return geom_imshow(image_data,
                       cmap=cmap,
                       norm=norm,
                       vmin=vmin,
                       vmax=vmax,
                       extent=extent
                       )


def geom_imshow(image_data, cmap=None, *, norm=None, vmin=None, vmax=None, extent=None):
    """
    Displays image specified by ndarray with shape

    - (M, N) - grey-scale image
    - (M, N, 3) - color RGB image
    - (M, N, 4) - color RGB image with alpha channel

    This geom is not as flexible as `geom_raster()` or `geom_tile()`
    but vastly superior in the terms of rendering efficiency.   

    Parameters
    ----------
    image_data : ndarray
        Specifies image type, size and pixel values.
        Supported array shapes are:

        - (M, N): an image with scalar data. The values are mapped to colors (greys by default) using normalization. See parameters `norm`, `cmap`, `vmin`, `vmax`.
        - (M, N, 3): an image with RGB values (0-1 float or 0-255 int).
        - (M, N, 4): an image with RGBA values (0-1 float or 0-255 int).

        The first two dimensions (M, N) define the rows and columns of the image.
        Out-of-range values are clipped.
    cmap : str, optional
        Name of colormap. For example "viridis", "magma", "plasma", "inferno", or any other colormap
        which is supported by the Palettable package (https://github.com/jiffyclub/palettable)
        This parameter is ignored for RGB(A) images.
    norm : bool, default=True
        True - luminance values in grey-scale image will be scaled to [0-255] range using a linear scaler.
        False - disables scaling of luminance values in grey-scale image.
        This parameter is ignored for RGB(A) images.
    vmin, vmax : number, optional
        Define the data range used for luminance normalization in grey-scale images.
        This parameter is ignored for RGB(A) images or if parameter `norm=False`.
    extent : list of 4 numbers: [left, right, bottom, top], optional
        Define image's bounding box in terms of the "data coordinates".

        - `left, right`: coordinates of pixels' outer edge along the x-axis for pixels in the 1-st and the last column.
        - `bottom, top`: coordinates of pixels' outer edge along the y-axis for pixels in the 1-st and the last row.

        The default is: [-0.5, ncol-0.5, -0.5, nrow-0.5]

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Notes
    -----
    This geom doesn't understand any aesthetics.
    It doesn't support color scales either.

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
        ggplot() + geom_imshow(image)

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
        ggplot() + geom_imshow(image, norm=False)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        image = np.random.normal(size=(64, 64))
        ggplot() + geom_imshow(image, vmin=-1, vmax=1)

    """

    start = time()

    if png == None:
        raise ValueError("pypng is not installed")

    if not is_ndarray(image_data):
        raise ValueError("Invalid image_data: ndarray is expected but was {}".format(type(image_data)))

    if image_data.ndim not in (2, 3):
        raise ValueError(
            "Invalid image_data: 2d or 3d array is expected but was {}-dimensional".format(image_data.ndim))

    # Figure out the type of the image
    if image_data.ndim == 2:
        image_data = _normalize_2D(image_data, norm, vmin, vmax)
        height, width = image_data.shape
        image_type = 'gray'
        nchannels = 1
    else:
        image_data = _normalize_RGBa(image_data)
        height, width, nchannels = image_data.shape
        if nchannels == 3:
            image_type = 'rgb'
        elif nchannels == 4:
            image_type = 'rgba'
        else:
            raise ValueError(
                "Invalid image_data: num of channels in color image expected 3 (RGB) or 4 (RGBA) but was {}".format(
                    nchannels))

    norm_end = time()
    print("Normalization: {}".format(norm_end - start))

    # Make sure all values are ints in range 0-255.
    image_date = image_data.clip(0, 255)

    clip_end = time()
    print("Clipping: {}".format(clip_end - norm_end))

    if cmap and image_type == 'gray':
        # colormap via palettable
        if not palettable:
            raise ValueError(
                "Can't process `cmap`: please install 'Palettable' (https://pypi.org/project/palettable/) to your Python environment."
            )
        cmap_256 = palettable.get_map(cmap + "_256")
        cmap_rgb_256 = [_hex2rgb_np_int8(c) for c in cmap_256.hex_colors]

        # def map2rgb(v):
        #     # v is in range [0,255]
        #     i = max(0, min(255, int(v + .5)))
        #     return cmap_rgb_256[i]
        #
        # cmapper = numpy.vectorize(map2rgb, signature='()->(n)')
        # image_data = cmapper(image_data)
        image_data_rgb = numpy.zeros((numpy.shape(image_data)[0], numpy.shape(image_data)[1],3), dtype=numpy.int8)
        it = numpy.nditer(image_data, flags=['multi_index'], op_flags=['readonly'])
        for x in it:
            image_data_rgb[it.multi_index] = cmap_rgb_256[int(x)]

        image_data = image_data_rgb
        image_type = "rgb"  # it's color image now
        nchannels = 3

    cmap_end = time()
    print("cmap: {}".format(cmap_end - clip_end))

    # Image extent with possible axis flipping.
    # The default image bounds include 1/2 unit size expand in all directions.
    ext_x0, ext_x1, ext_y0, ext_y1 = -.5, width - .5, -.5, height - .5
    if (extent):
        try:
            ext_x0, ext_x1, ext_y0, ext_y1 = [float(v) for v in extent]
        except ValueError as e:
            raise ValueError(
                "Invalid `extent`: list of 4 numbers expected: {}".format(e)
            )

    if (ext_x0 > ext_x1):
        # copy after flip to work around this numpy issue: https://github.com/drj11/pypng/issues/91
        image_data = numpy.flip(image_data, axis=1).copy()
        ext_x0, ext_x1 = ext_x1, ext_x0

    if (ext_y0 > ext_y1):
        image_data = numpy.flip(image_data, axis=0)
        ext_y0, ext_y1 = ext_y1, ext_y0

    # Make sure each value is 1 byte and the type is numpy.int8.
    # Otherwise, pypng will produce broken colors.
    if image_data.dtype != numpy.int8:
        # Can't cast directly from np.float32 to np.int8.
        image_data = image_data.astype(numpy.int16).astype(numpy.int8)

    # Reshape to 2d-array:
    # from [[[R, G, B], [R, G, B]], ...] to [[R, G, B, R, G, B],..], or pypng will fail
    image_2d = image_data.reshape(-1, width * nchannels)

    image_2d_end = time()
    print("image_2d: {}".format(image_2d_end - cmap_end))

    png_bytes = io.BytesIO()
    png.Writer(
        width=width,
        height=height,
        greyscale=(image_type == 'gray'),
        alpha=(image_type == 'rgba'),
        bitdepth=8
    ).write(png_bytes, image_2d)

    png_writer_done = time()
    print("png.Writer: {}".format(png_writer_done - image_2d_end))

    href = 'data:image/png;base64,' + str(base64.standard_b64encode(png_bytes.getvalue()), 'utf-8')

    return _geom('image',
                 href=href,
                 xmin=ext_x0,
                 ymin=ext_y0,
                 xmax=ext_x1,
                 ymax=ext_y1
                 )
