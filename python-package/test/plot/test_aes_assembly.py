#
# Copyright (c) 2024. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import pytest

import lets_plot as gg


class TestAesMappingInExpression:
    result_empty = {}
    result_xy = {'x': 'xVar', 'y': 'yVar'}

    @pytest.mark.parametrize('actual,expected', [
        # Add
        (gg.ggplot() + gg.aes(x='xVar'),
         gg.ggplot(mapping=gg.aes(x='xVar'))),

        (gg.ggplot(mapping=gg.aes(y='yVar')) + gg.aes(x='xVar'),
         gg.ggplot(mapping=gg.aes(x='xVar', y='yVar'))),

        (gg.ggplot() + gg.aes(x='xVar') + gg.aes(y='yVar'),
         gg.ggplot(mapping=gg.aes(x='xVar', y='yVar'))),

        # Override/add
        (gg.ggplot(mapping=gg.aes(x='xBad', y='yVar')) + gg.aes(x='xVar'),
         gg.ggplot(mapping=gg.aes(x='xVar', y='yVar'))),

        (gg.ggplot(mapping=gg.aes(x='xBad', y='yBad', color='cBad')) + gg.aes(x='xVar') + gg.aes(y='yVar') +
         gg.aes(color='cVar'),
         gg.ggplot(mapping=gg.aes(x='xVar', y='yVar', color='cVar'))),

        # Misc
        (gg.ggplot(mapping=gg.aes(color='cBad')) + gg.geom_point() +
         gg.aes(color='cBetter') + gg.aes(color='cWorse') + gg.labs(x="X") +
         gg.aes(color='cVar'),
         gg.ggplot(mapping=gg.aes(color='cVar')) + gg.geom_point() + gg.labs(x="X")),
    ])
    def test_aes(self, actual, expected):
        assert actual.as_dict() == expected.as_dict()
