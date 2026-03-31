#
# Copyright (c) 2026. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

import re

import pytest

import lets_plot as gg


@pytest.mark.parametrize('aesthetic', ['color', 'fill', 'paint_a', 'paint_b', 'paint_c'])
def test_scale_hue_palette_with_different_aesthetics(aesthetic):
    """Test palette generation for scale_hue with different color aesthetics."""
    palette = gg.scale_hue(aesthetic).palette(5)

    assert isinstance(palette, list)
    assert len(palette) == 5

    # Check all items are valid hex color codes
    for color in palette:
        assert isinstance(color, str)
        assert re.match(r'^#[0-9A-Fa-f]{6}$', color), f"Invalid hex color: {color}"


def test_different_palette_sizes():
    """Test palette generation with different sizes."""
    for n in [1, 2, 5, 10, 20]:
        palette = gg.scale_color_hue().palette(n)
        assert len(palette) == n, f"Expected {n} colors, got {len(palette)}"


def test_palette_values_are_different():
    """Test that generated palette contains different colors."""
    palette = gg.scale_color_hue().palette(5)

    # All colors should be unique
    assert len(palette) == len(set(palette)), "Palette should contain unique colors"
