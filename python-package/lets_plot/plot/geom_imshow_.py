#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import base64
import io

from .core import aes
from .geom import _geom
from .scale import scale_gradientn
from .scale import scale_grey
from .util import as_boolean
from .._type_utils import is_ndarray

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

__all__ = ['geom_imshow']


def _hex2rgb(hex_c, alpha):
    hex_s = hex_c.lstrip('#')
    list_rgb = [int(hex_s[i:i + 2], 16) for i in (0, 2, 4)]
    if alpha is not None:
        list_rgb.append(int(alpha + 0.5))
    return list_rgb


def _hex2rgb_arr_uint8(hex_c, alpha=None):
    """
    Create 'palette' for PyPNG PNG writer
    """
    return numpy.array(_hex2rgb(hex_c, alpha), dtype=numpy.uint8)


def _normalize_2D(image_data, norm, vmin, vmax, min_lum):
    """
    Take numpy 2D array of float or int-s and
    return 2D array of ints with the target range [0..255].
    Values outside the target range will be later clipped.
    """
    min_lum = max(0, min_lum)
    max_lum = 255 - min_lum

    vmin = float(vmin if vmin is not None else numpy.nanmin(image_data))
    vmax = float(vmax if vmax is not None else numpy.nanmax(image_data))
    if vmin > vmax:
        raise ValueError("vmin value must be less then vmax value, was: {} > {}".format(vmin, vmax))

    normalize = as_boolean(norm, default=True)

    # Make a copy via `numpy.copy()` or via `arr.astype()`
    #   - prevent modification of the original image
    #   - work around read-only flag in the original image

    if normalize:
        if vmin == vmax:
            image_data = numpy.copy(image_data)
            image_data[True] = 127
        else:
            # float array for scaling
            if image_data.dtype.kind == 'f':
                image_data = numpy.copy(image_data)
            else:
                image_data = image_data.astype(numpy.float32)

            image_data.clip(vmin, vmax, out=image_data)

            ratio = max_lum / (vmax - vmin)
            image_data -= vmin
            image_data *= ratio
            image_data += min_lum
    else:
        # no normalization
        image_data = numpy.copy(image_data)
        image_data.clip(min_lum, max_lum, out=image_data)
        vmin = float(numpy.nanmin(image_data))
        vmax = float(numpy.nanmax(image_data))

    return (image_data, vmin, vmax)


def geom_imshow(image_data, cmap=None, *,
                norm=None, alpha=None,
                vmin=None, vmax=None,
                extent=None,
                compression=None,
                show_legend=True,
                color_by="paint_c",
                ):
    """
    Display image specified by ndarray with shape.

    - (M, N) - grey-scale image
    - (M, N, 3) - color RGB image
    - (M, N, 4) - color RGB image with alpha channel

    This geom is not as flexible as `geom_raster()` or `geom_tile()`
    but vastly superior in the terms of rendering efficiency.   

    Parameters
    ----------
    image_data : ndarray
        Specify image type, size and pixel values.
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
    alpha: float, optional
        The alpha blending value, between 0 (transparent) and 1 (opaque).    
    vmin, vmax : number, optional
        Define the data range used for luminance normalization in grey-scale images.
        This parameter is ignored for RGB(A) images or if parameter `norm=False`.
    extent : list of 4 numbers: [left, right, bottom, top], optional
        Define image's bounding box in terms of the "data coordinates".

        - `left, right`: coordinates of pixels' outer edge along the x-axis for pixels in the 1-st and the last column.
        - `bottom, top`: coordinates of pixels' outer edge along the y-axis for pixels in the 1-st and the last row.

        The default is: [-0.5, ncol-0.5, -0.5, nrow-0.5]
    compression : int, optional
        The compression level to be used by the ``zlib`` module.
        Values from 0 (no compression) to 9 (highest).
        Value `None` means that the `zlib` module uses
        the default level of compression (which is generally acceptable).
    show_legend : bool, default=True
        Greyscale images only.
        False - do not show legend for this layer.
    color_by : {'fill', 'color', 'paint_a', 'paint_b', 'paint_c'}, default='paint_c'
        Define the color aesthetic used by the legend shown for a greyscale image.

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

    if png is None:
        raise ValueError("pypng is not installed")

    if not is_ndarray(image_data):
        raise ValueError("Invalid image_data: ndarray is expected but was {}".format(type(image_data)))

    if image_data.ndim not in (2, 3):
        raise ValueError(
            "Invalid image_data: 2d or 3d array is expected but was {}-dimensional".format(image_data.ndim))

    if alpha is not None:
        if not (0 <= alpha <= 1):
            raise ValueError(
                "Invalid alpha: expected float in range [0..1] but was {}".format(alpha))

    if compression is not None:
        if not (0 <= compression <= 9):
            raise ValueError(
                "Invalid compression: expected integer in range [0..9] but was {}".format(compression))

    greyscale = (image_data.ndim == 2)
    if greyscale:
        # Greyscale image

        has_nan = numpy.isnan(image_data.max())
        min_lum = 0 if not (has_nan and cmap) else 1  # index 0 reserved for NaN-s

        (image_data, greyscale_data_min, greyscale_data_max) = _normalize_2D(image_data, norm, vmin, vmax, min_lum)
        height, width = image_data.shape
        nchannels = 1

        has_nan = numpy.isnan(image_data.max())
        if has_nan and not cmap:
            # add alpha-channel (LA)
            alpha_ch_scaler = 1 if alpha is None else alpha
            is_nan = numpy.isnan(image_data)
            im_shape = numpy.shape(image_data)
            alpha_ch = numpy.zeros(im_shape, dtype=image_data.dtype)
            alpha_ch[is_nan == False] = 255 * alpha_ch_scaler
            image_data[is_nan] = 0
            image_data = numpy.dstack((image_data, alpha_ch))
            nchannels = 2
        elif has_nan and cmap:
            # replace all NaN-s with 0 (index 0 for transparent color)
            numpy.nan_to_num(image_data, copy=False, nan=0)
        elif not cmap and alpha is not None:
            # add alpha-channel (LA)
            im_shape = numpy.shape(image_data)
            alpha_ch = numpy.full(im_shape, 255 * alpha, dtype=image_data.dtype)
            image_data = numpy.dstack((image_data, alpha_ch))
            nchannels = 2

    else:
        # Color RGB/RGBA image
        # Make a copy:
        #   - prevent modification of the original image
        #   - drop read-only flag
        image_data = numpy.copy(image_data)
        if image_data.dtype.kind == 'f':
            image_data *= 255

        height, width, nchannels = image_data.shape

        if alpha is not None:
            if nchannels == 3:
                # RGB image: add alpha channel (RGBA)
                alpha_ch = numpy.full((height, width, 1), 255 * alpha, dtype=image_data.dtype)
                image_data = numpy.dstack((image_data, alpha_ch))
                nchannels = 4
            elif nchannels == 4:
                # RGBA image: apply alpha scaling
                image_data[:, :, 3] *= alpha

    # Make sure all values are ints in range 0-255.
    image_data.clip(0, 255, out=image_data)

    # Image extent with possible axis flipping.
    # The default image bounds include 1/2 unit size expand in all directions.
    ext_x0, ext_x1, ext_y0, ext_y1 = -.5, width - .5, -.5, height - .5
    if extent:
        try:
            ext_x0, ext_x1, ext_y0, ext_y1 = [float(v) for v in extent]
        except ValueError as e:
            raise ValueError(
                "Invalid `extent`: list of 4 numbers expected: {}".format(e)
            )

    if ext_x0 > ext_x1:
        # copy after flip to work around this numpy issue: https://github.com/drj11/pypng/issues/91
        image_data = numpy.flip(image_data, axis=1).copy()
        ext_x0, ext_x1 = ext_x1, ext_x0

    if ext_y0 > ext_y1:
        image_data = numpy.flip(image_data, axis=0)
        ext_y0, ext_y1 = ext_y1, ext_y0

    # Make sure each value is 1 byte and the type is numpy.int8.
    # Otherwise, pypng will produce broken colors.
    if image_data.dtype.kind == 'f':
        # Can't cast directly from float to np.int8.
        image_data += 0.5
        image_data = image_data.astype(numpy.int16)

    if image_data.dtype != numpy.int8:
        image_data = image_data.astype(numpy.int8)

    # Reshape to 2d-array:
    # from [[[R, G, B], [R, G, B]], ...] to [[R, G, B, R, G, B],..] for RGB(A)
    # or from [[[L, A], [L, A]], ...] to [[L, A, L, A],..] for greyscale–alpha (LA)
    # or pypng will fail
    image_2d = image_data.reshape(-1, width * nchannels)

    # PNG writer
    palette = None
    if cmap and greyscale:
        # colormap via palettable
        if not palettable:
            raise ValueError(
                "Can't process `cmap`: please install 'Palettable' (https://pypi.org/project/palettable/) to your "
                "Python environment. "
            )
        if not has_nan:
            alpha_ch_val = None if alpha is None else 255 * alpha
            cmap_256 = palettable.get_map(cmap + "_256")
            palette = [_hex2rgb_arr_uint8(c, alpha_ch_val) for c in cmap_256.hex_colors]
        else:
            alpha_ch_val = 255 if alpha is None else 255 * alpha
            cmap_255 = palettable.get_map(cmap + "_255")
            # transparent color at index 0
            palette = [numpy.array([0, 0, 0, 0], dtype=numpy.uint8)] + [_hex2rgb_arr_uint8(c, alpha_ch_val) for c in
                                                                        cmap_255.hex_colors]

    png_bytes = io.BytesIO()
    png.Writer(
        width=width,
        height=height,
        greyscale=greyscale and not cmap,
        alpha=(nchannels == 4 or nchannels == 2),  # RGBA or LA
        bitdepth=8,
        palette=palette,
        compression=compression
    ).write(png_bytes, image_2d)

    href = 'data:image/png;base64,' + str(base64.standard_b64encode(png_bytes.getvalue()), 'utf-8')

    # The Legend (colorbar)
    show_legend = as_boolean(show_legend, default=True)
    normalize = as_boolean(norm, default=True)
    legend_title = ""
    color_scale = None
    color_scale_mapping = None
    if greyscale and show_legend:
        # aes(color=[greyscale_data_min, greyscale_data_max])
        color_scale_mapping = aes(**{color_by: [greyscale_data_min, greyscale_data_max]})
        if cmap and normalize:
            cmap_32 = palettable.get_map(cmap + "_32")
            # color_scale = scale_color_gradientn(colors=cmap_32.hex_colors, name=legend_title)
            color_scale = scale_gradientn(aesthetic=color_by, colors=cmap_32.hex_colors, name=legend_title)
        elif cmap and not normalize:
            cmap_256 = palettable.get_map(cmap + "_256")
            start = max(0, round(greyscale_data_min))
            end = min(255, round(greyscale_data_max))
            cmap_hex_colors = cmap_256.hex_colors[start:end]
            if len(cmap_hex_colors) > 32:
                # reduce number of colors to 32
                indices = numpy.linspace(0, len(cmap_hex_colors) - 1, 32, dtype=int)
                cmap_hex_colors = [cmap_hex_colors[i] for i in indices]

            # color_scale = scale_color_gradientn(colors=cmap_hex_colors, name=legend_title)
            color_scale = scale_gradientn(aesthetic=color_by, colors=cmap_hex_colors, name=legend_title)
        else:
            start = 0 if normalize else greyscale_data_min / 255.
            end = 1 if normalize else greyscale_data_max / 255.
            # color_scale = scale_color_grey(start=start, end=end, name=legend_title)
            color_scale = scale_grey(aesthetic=color_by, start=start, end=end, name=legend_title)

    # Image geom layer
    geom_image_layer = _geom(
        'image',
        mapping=color_scale_mapping,
        href=href,
        xmin=ext_x0,
        ymin=ext_y0,
        xmax=ext_x1,
        ymax=ext_y1,
        show_legend=show_legend,
        color_by=color_by if (show_legend and greyscale) else None,
    )

    if (color_scale is not None):
        geom_image_layer = geom_image_layer + color_scale

    return geom_image_layer
