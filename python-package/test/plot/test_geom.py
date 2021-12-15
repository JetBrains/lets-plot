#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import pytest

import lets_plot as gg
from lets_plot.plot.geom import _geom
from lets_plot.plot.geom_extras import arrow


class TestWithListAndDictArgs:
    expected = [0, 0]

    # I
    mapping_arg = gg.aes('X')
    expected[0] = dict(
        geom='n',
        data_meta={},
        mapping=mapping_arg.as_dict()
    )

    # II
    expected[1] = dict(
        geom='n',
        mapping={},
        data_meta={},
        arrow={'angle': 0, 'length': 1, 'ends': 'a', 'type': 'b', 'name': 'arrow'}
    )

    @pytest.mark.parametrize('args_list,args_dict,expected', [
        (['n'], dict(mapping=mapping_arg, ), expected[0]),
        (['n'], {'arrow': arrow(0, 1, 'a', 'b')}, expected[1])
    ])
    def test_aes(self, args_list, args_dict, expected):
        spec = _geom(*args_list, **args_dict)
        assert spec.as_dict() == expected
