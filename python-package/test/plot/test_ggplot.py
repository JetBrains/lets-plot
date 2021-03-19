#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import pytest

import lets_plot as gg

data = [1, 2]
mapping_empty = gg.aes()
mapping_x = gg.aes('X')

result_empty = {'data': None, 'data_meta': {}, 'mapping': {'x': None, 'y': None}, 'layers': [], 'scales': [], 'kind': 'plot'}
result_data = {'data': data, 'data_meta': {}, 'mapping': {'x': None, 'y': None}, 'layers': [], 'scales': [], 'kind': 'plot'}
result_data_mapping_empty = {'data': data, 'data_meta': {}, 'mapping': mapping_empty.as_dict(), 'layers': [], 'scales': [], 'kind': 'plot'}
result_data_mapping_x = {'data': data, 'data_meta': {}, 'mapping': mapping_x.as_dict(), 'layers': [], 'scales': [], 'kind': 'plot'}
result_mapping_x = {'data': None, 'data_meta': {}, 'mapping': mapping_x.as_dict(), 'layers': [], 'scales': [], 'kind': 'plot'}


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
