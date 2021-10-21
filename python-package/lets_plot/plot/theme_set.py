#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

__all__ = [
    'theme_grey',
    'theme_classic',
    'theme_minimal',
    'theme_minimal2',
    'theme_none',
]


def theme_grey():
    """
    ToDo
    """
    return FeatureSpec('theme', name="grey")


def theme_classic():
    """
    ToDo
    """
    return FeatureSpec('theme', name="classic")


def theme_minimal():
    """
    ToDo
    """
    return FeatureSpec('theme', name="minimal")


def theme_minimal2():
    """
    ToDo
    """
    return FeatureSpec('theme', name="minimal2")


def theme_none():
    """
    ToDo
    """
    return FeatureSpec('theme', name="none")
