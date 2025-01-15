#
# Copyright (c) 2025. JetBrains s.r.o.
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

expected_no_regions = {
    'kind': 'subplots',
    'figures': figures_prop,
    'layout': {'name': 'free', 'regions': []}
}
expected_regions = {
    'kind': 'subplots',
    'figures': figures_prop,
    'layout': {'name': 'free', 'regions': [[0.1, 1.1, 0.3, 1.1]]}
}


@pytest.mark.parametrize('kwargs,expected', [
    ({}, expected_no_regions),
    # Should work with tuples and arrays
    ({'regions': [(0.1, 1.1, 0.3, 1.1)]}, expected_regions),
    ({'regions': [[0.1, 1.1, 0.3, 1.1]]}, expected_regions),
])
def test_gggrid(kwargs, expected):
    regions = kwargs.get('regions', [])
    p = gg.ggplot(data={})
    if regions:
        spec = gg.ggbunch([p], *regions)
    else:
        spec = gg.ggbunch([p])

    assert spec.as_dict() == expected
