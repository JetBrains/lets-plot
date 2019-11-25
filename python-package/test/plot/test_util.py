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
