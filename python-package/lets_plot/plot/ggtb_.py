#
# Copyright (c) 2024. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

__all__ = ['ggtb']


def ggtb():
    return FeatureSpec(kind='ggtoolbar', name=None)
