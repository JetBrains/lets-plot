#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import pytest

from lets_plot.plot import util


@pytest.mark.parametrize('val,default,expected', [
    (True, False, True),
    (None, True, True),
    ([1], False, True),
    ('True', False, True),
    ('A', False, True),
    (object(), False, True),
    ('false', False, True),
    (False, True, False),
    (None, False, False),
    ([], True, False),
    ('False', True, False),
    (None, None, None),
    (True, None, True),
])
def test_as_boolean(val, default, expected):
    assert util.as_boolean(val, default=default) == expected


@pytest.mark.parametrize('map_join,expected', [
    ('state', ['state', 'request']), # only data key set - add 'request'
    (['state'], ['state', 'request']), # only data key set - add 'request'
    (None, None), # without map_join should change nothing
    (['state', 'found name'], ['state', 'found name']), # both keys set - do not change
    ([None, None], [None, None]), # not sure what will happen later, but map_join_regions should change nothing
    ([], []), # not sure what will happen later, but map_join_regions should change nothing
])
def test_map_join_regions(map_join, expected):
    assert util.auto_join_geocoded_gdf(map_join) == expected
