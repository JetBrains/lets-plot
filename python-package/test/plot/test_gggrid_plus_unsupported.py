#  Copyright (c) 2023. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

#
#

import pytest

import lets_plot as gg
from lets_plot.plot.core import FeatureSpec


def test_gggrid_plus_unsupported():
    with pytest.raises(TypeError):
        spec = gg.gggrid([gg.ggplot()]) + FeatureSpec(kind='unsupported test feature', name=None)


def test_unsupported_plus_gggrid():
    with pytest.raises(TypeError):
        spec = FeatureSpec(kind='unsupported test feature', name=None) + gg.gggrid([gg.ggplot()])
