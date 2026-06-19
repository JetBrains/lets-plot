#
# Copyright (c) 2026. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

import base64
from io import BytesIO

__all__ = ['annotation_raster']


def annotation_raster(raster, xmin=None, xmax=None, ymin=None, ymax=None, interpolate=False):
    """
    Add a raster image annotation layer.

    Parameters
    ----------
    raster : bytes, bytearray or memoryview
        Encoded raster image bytes. Supported formats: PNG, JPEG, GIF and WebP.
    xmin, xmax, ymin, ymax : number
        Image bounds in data coordinates. None values are interpreted as panel bounds.
    interpolate : bool, default=False
        If True, interpolate pixels when scaling the image.

    Returns
    -------
    ``LayerSpec``
        Geom object specification.
    """
    from .geom import _geom

    image_bytes = _to_supported_png_bytes(_as_image_bytes(raster))
    href = 'data:{};base64,{}'.format(
        'image/png',
        str(base64.standard_b64encode(image_bytes), 'utf-8')
    )

    return _geom(
        'annotation_raster',
        href=href,
        xmin=xmin,
        xmax=xmax,
        ymin=ymin,
        ymax=ymax,
        interpolate=interpolate,
        show_legend=False,
        inherit_aes=False,
    )


def _as_image_bytes(raster):
    if isinstance(raster, bytes):
        image_bytes = raster
    elif isinstance(raster, bytearray):
        image_bytes = bytes(raster)
    elif isinstance(raster, memoryview):
        image_bytes = raster.tobytes()
    else:
        raise ValueError("Unsupported raster value: expected bytes, bytearray or memoryview")

    if len(image_bytes) == 0:
        raise ValueError("Raster image data is empty")

    return image_bytes


def _detect_image_mime_type(image_bytes):
    if image_bytes.startswith(b'\x89PNG\r\n\x1a\n'):
        return 'image/png'
    if image_bytes.startswith(b'\xff\xd8\xff'):
        return 'image/jpeg'
    if image_bytes.startswith(b'GIF87a') or image_bytes.startswith(b'GIF89a'):
        return 'image/gif'
    if len(image_bytes) >= 12 and image_bytes[:4] == b'RIFF' and image_bytes[8:12] == b'WEBP':
        return 'image/webp'

    raise ValueError("Unsupported raster image format: expected PNG, JPEG, GIF or WebP")


def _to_supported_png_bytes(image_bytes):
    _detect_image_mime_type(image_bytes)

    try:
        import png
        from PIL import Image

        image = Image.open(BytesIO(image_bytes)).convert('RGBA')
        width, height = image.size
        row_stride = width * 4
        rgba_bytes = image.tobytes()
        rows = [
            rgba_bytes[y * row_stride:(y + 1) * row_stride]
            for y in range(height)
        ]
        out = BytesIO()
        png.Writer(
            width=width,
            height=height,
            greyscale=False,
            alpha=True,
            bitdepth=8,
        ).write(out, rows)
        return out.getvalue()
    except Exception as e:
        raise ValueError("Unsupported raster image format: expected PNG, JPEG, GIF or WebP") from e
