#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import pytest

import lets_plot as gg

data = {'a': [1, 2], 'b': [3, 4]}
mapping_empty = gg.aes()
mapping_x = gg.aes('X')

result_empty = {'kind': 'plot', 'mapping': {}, 'data_meta': {}, 'layers': [], 'scales': [], 'metainfo_list': []}
result_data = {'data': data, 'kind': 'plot', 'mapping': {}, 'data_meta': {}, 'layers': [], 'scales': [], 'metainfo_list': []}
result_data_mapping_empty = {'data': data, 'kind': 'plot', 'mapping': {}, 'data_meta': {}, 'layers': [], 'scales': [], 'metainfo_list': []}
result_data_mapping_x = {'data': data, 'mapping': {'x': 'X'}, 'data_meta': {}, 'kind': 'plot', 'layers': [], 'scales': [], 'metainfo_list': []}
result_mapping_x = {'mapping': {'x': 'X'}, 'data_meta': {}, 'kind': 'plot', 'layers': [], 'scales': [], 'metainfo_list': []}


@pytest.mark.parametrize('args,expected', [
    ([], result_empty),
    ([data], result_data),
    ([data, mapping_empty], result_data_mapping_empty),
    ([data, mapping_x], result_data_mapping_x),
    ([None, mapping_x], result_mapping_x),
])
def test_ggplot(args, expected):
    spec = gg.ggplot(*args)
    assert spec.as_dict() == expected
