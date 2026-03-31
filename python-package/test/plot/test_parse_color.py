#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

import numpy as np
import pytest

from lets_plot.plot.geom_imshow_ import _parse_color


def test_hex_long_format():
    result = _parse_color('#ff0000')
    assert result.dtype == np.uint8
    assert list(result) == [255, 0, 0]


def test_hex_short_format():
    result = _parse_color('#f00')
    assert result.dtype == np.uint8
    assert list(result) == [255, 0, 0]


def test_hex_with_alpha():
    result = _parse_color('#00ff00', alpha=128)
    assert list(result) == [0, 255, 0, 128]


def test_rgb_format():
    result = _parse_color('rgb(0, 128, 255)')
    assert result.dtype == np.uint8
    assert list(result) == [0, 128, 255]


def test_rgb_with_alpha():
    result = _parse_color('rgb(255, 0, 0)', alpha=64)
    assert list(result) == [255, 0, 0, 64]


def test_rgba_format():
    result = _parse_color('rgba(100, 150, 200, 128)')
    assert result.dtype == np.uint8
    assert list(result) == [100, 150, 200, 128]


def test_rgba_alpha_multiply():
    # rgba alpha (128) * param alpha (128) / 255 = 64
    result = _parse_color('rgba(255, 0, 0, 128)', alpha=128)
    assert list(result) == [255, 0, 0, 64]


def test_whitespace_handling():
    result = _parse_color('  #ff0000  ')
    assert list(result) == [255, 0, 0]


def test_invalid_format():
    with pytest.raises(ValueError):
        _parse_color('invalid')


def test_invalid_rgb_parts():
    with pytest.raises(ValueError):
        _parse_color('rgb(1, 2)')
