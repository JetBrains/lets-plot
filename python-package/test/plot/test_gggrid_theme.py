#  Copyright (c) 2023. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

#
#

import lets_plot as gg


def test_gggrid_theme():
    spec = gg.gggrid([gg.ggplot()]) + gg.theme_grey()
    assert 'theme' in spec.as_dict()
    assert spec.as_dict()['theme'] == {'name': 'grey'}

def test_gggrid_flavor():
    spec = gg.gggrid([gg.ggplot()]) + gg.flavor_darcula()
    assert 'theme' in spec.as_dict()
    assert spec.as_dict()['theme'] == {'flavor': 'darcula'}
