#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import pytest

import lets_plot as gg

figures_prop = [{'data': {},
                 'data_meta': {},
                 'kind': 'plot',
                 'layers': [],
                 'mapping': {},
                 'metainfo_list': [],
                 'scales': []}
                ]

expected_no_args = {
    'kind': 'subplots',
    'figures': figures_prop,
    'layout': {'name': 'grid', 'ncol': 1, 'nrow': 1}
}
expected_ncols3 = {
    'kind': 'subplots',
    'figures': figures_prop + [None, None],
    'layout': {'name': 'grid', 'ncol': 3, 'nrow': 1}
}
expected_widths_heights = {
    'kind': 'subplots',
    'figures': figures_prop,
    'layout': {'name': 'grid', 'ncol': 1, 'nrow': 1, 'widths': [1, 2, 3], 'heights': [4, 5, 6]}
}
expected_fit_align = {
    'kind': 'subplots',
    'figures': figures_prop,
    'layout': {'name': 'grid', 'ncol': 1, 'nrow': 1, 'fit': False, 'align': True}
}
expected_hv_space = {
    'kind': 'subplots',
    'figures': figures_prop,
    'layout': {'name': 'grid', 'ncol': 1, 'nrow': 1, 'hspace': 10, 'vspace': 20}
}

expected_share_xy_all_none = {
    'kind': 'subplots',
    'figures': figures_prop,
    'layout': {'name': 'grid', 'ncol': 1, 'nrow': 1, 'sharex': 'all', 'sharey': 'none'}
}


@pytest.mark.parametrize('kwargs,expected', [
    ({}, expected_no_args),
    ({'ncol': 3}, expected_ncols3),
    ({'widths': [1, 2, 3], 'heights': [4, 5, 6]}, expected_widths_heights),
    ({'fit': False, 'align': True}, expected_fit_align),
    ({'hspace': 10, 'vspace': 20}, expected_hv_space),
    ({'sharex': True, 'sharey': False}, expected_share_xy_all_none),
    ({'sharex': 'all', 'sharey': 'none'}, expected_share_xy_all_none),
])
def test_gggrid(kwargs, expected):
    p = gg.ggplot(data={})
    spec = gg.gggrid([p], **kwargs)
    assert spec.as_dict() == expected
