#  Copyright (c) 2021. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
import pytest

import lets_plot as gg


@pytest.mark.parametrize('args,expected', [
    ('foo', [['foo'], None]),
    (['foo'], [['foo'], None]),
    (['foo', 'bar'], [['foo'], ['bar']]),
    ([['foo', 'bar']], [['foo', 'bar'], None]),
    ([['foo', 'bar'], ['baz', 'qux']], [['foo', 'bar'], ['baz', 'qux']])
])
def test_map_join(args, expected):
    # ggplot is required - it normalizes map_join on before_append
    spec = gg.ggplot() + gg.geom_point(map_join=args, tooltips=gg.layer_tooltips())
    assert spec.as_dict()['layers'][0]['map_join'] == expected
