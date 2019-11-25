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
    pos_args = ['n', mapping_arg, 'd', 's', 'p', 'sl']
    other_args = {'size': 10, 'fill': 'red'}
    expected[0] = dict(
        geom='n',
        mapping=mapping_arg.as_dict(),
        data='d',
        stat='s',
        position='p',
        show_legend='sl',
        **other_args)

    # II
    expected[1] = dict(
        geom='n',
        mapping=None,
        data=None,
        stat=None,
        position=None,
        show_legend=None,
        arrow={'angle': 0, 'length': 1, 'ends': 'a', 'type': 'b', 'name': 'arrow'}
    )

    @pytest.mark.parametrize('args_list,args_dict,expected', [
        (pos_args, other_args, expected[0]),
        (['n'], {'arrow': arrow(0, 1, 'a', 'b')}, expected[1])
    ])
    def test_aes(self, args_list, args_dict, expected):
        spec = _geom(*args_list, **args_dict)
        assert spec.as_dict() == expected
