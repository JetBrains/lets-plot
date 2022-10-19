#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

import lets_plot as gg


# noinspection SpellCheckingInspection
def test_ggsize():
    spec = gg.ggplot() + gg.ggsize(5, 10)
    assert spec.as_dict() == {'kind': 'plot',
                              'ggsize': {'height': 10, 'width': 5},
                              'mapping': {},
                              'data_meta': {},
                              'layers': [],
                              'scales': [],
                              'metainfo_list': []
                              }
