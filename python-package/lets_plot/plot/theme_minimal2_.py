#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

__all__ = [
    'theme_minimal2',
]


def theme_minimal2():
    """
    """
    return FeatureSpec('theme', name="minimal2")
