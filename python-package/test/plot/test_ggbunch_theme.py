#  Copyright (c) 2023. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

#
#

import lets_plot as gg


def test_ggbunch_theme():
    spec = gg.ggbunch([gg.ggplot()]) + gg.theme_grey()
    assert 'theme' in spec.as_dict()
    assert spec.as_dict()['theme'] == {'name': 'grey'}


def test_ggbunch_flavor():
    spec = gg.ggbunch([gg.ggplot()]) + gg.flavor_darcula()
    assert 'theme' in spec.as_dict()
    assert spec.as_dict()['theme'] == {'flavor': 'darcula'}


def test_ggbunch_theme_flavor():
    spec = gg.ggbunch([gg.ggplot()]) + gg.theme_grey() + gg.flavor_darcula()
    assert 'theme' in spec.as_dict()
    assert spec.as_dict()['theme'] == {'name': 'grey', 'flavor': 'darcula'}


def test_ggbunch_theme_flavor_as_array():
    spec = gg.ggbunch([gg.ggplot()]) + (gg.theme_grey() + gg.flavor_darcula())
    assert 'theme' in spec.as_dict()
    assert spec.as_dict()['theme'] == {'name': 'grey', 'flavor': 'darcula'}


def test_ggbunch_global_theme_override():
    # Set global setting!
    gg.LetsPlot.set_theme(
        gg.theme_grey()
    )

    try:
        spec = gg.ggbunch([gg.ggplot()]) + gg.theme_light()
    finally:
        # Clear global setting
        gg.LetsPlot.set_theme(None)

    assert 'theme' in spec.as_dict()
    assert spec.as_dict()['theme'] == {'name': 'light'}

    # Global theme should be stripped from the figure spec in grid
    fig = spec.as_dict()['figures'][0]
    assert fig['kind'] == 'plot'  # Make sure it's a plot
    assert 'theme' not in fig


def test_ggbunch_global_theme_override_cancelled():
    # Set global setting!
    gg.LetsPlot.set_theme(
        gg.theme_grey()
    )

    try:
        fig = gg.ggplot() + gg.theme_bw()  # this should cancel global theme for this figure
        spec = gg.ggbunch([fig]) + gg.theme_light()
    finally:
        # Clear global setting
        gg.LetsPlot.set_theme(None)

    assert 'theme' in spec.as_dict()
    assert spec.as_dict()['theme'] == {'name': 'light'}

    # The figure no longer uses global theme thus it should be present in the figure spec.
    fig = spec.as_dict()['figures'][0]
    assert fig['kind'] == 'plot'  # Make sure it's a plot
    assert fig['theme'] == {'name': 'bw'}
