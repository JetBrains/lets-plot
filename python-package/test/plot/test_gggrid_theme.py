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


def test_gggrid_theme_flavor():
    spec = gg.gggrid([gg.ggplot()]) + gg.theme_grey() + gg.flavor_darcula()
    assert 'theme' in spec.as_dict()
    assert spec.as_dict()['theme'] == {'name': 'grey', 'flavor': 'darcula'}


def test_gggrid_theme_flavor_as_array():
    spec = gg.gggrid([gg.ggplot()]) + (gg.theme_grey() + gg.flavor_darcula())
    assert 'theme' in spec.as_dict()
    assert spec.as_dict()['theme'] == {'name': 'grey', 'flavor': 'darcula'}


def test_gggrid_global_theme_override():
    # Set global setting!
    gg.LetsPlot.set_theme(
        gg.theme_grey()
    )

    try:
        spec = gg.gggrid([gg.ggplot()]) + gg.theme_light()
    finally:
        # Clear global setting
        gg.LetsPlot.set_theme(None)

    assert 'theme' in spec.as_dict()
    assert spec.as_dict()['theme'] == {'name': 'light'}

    # Global theme should be stripped from the figure spec in grid
    fig = spec.as_dict()['figures'][0]
    assert fig['kind'] == 'plot'  # Make sure it's a plot
    assert 'theme' not in fig


def test_gggrid_global_theme_override_cancelled():
    # Set global setting!
    gg.LetsPlot.set_theme(
        gg.theme_grey()
    )

    try:
        fig = gg.ggplot() + gg.theme_bw()  # this should cancel global theme for this figure
        spec = gg.gggrid([fig]) + gg.theme_light()
    finally:
        # Clear global setting
        gg.LetsPlot.set_theme(None)

    assert 'theme' in spec.as_dict()
    assert spec.as_dict()['theme'] == {'name': 'light'}

    # The figure no longer uses global theme thus it should be present in the figure spec.
    fig = spec.as_dict()['figures'][0]
    assert fig['kind'] == 'plot'  # Make sure it's a plot
    assert fig['theme'] == {'name': 'bw'}


def test_gggrid_subplot_theme_preserved():
    # Subplot has explicit theme_bw
    fig = gg.ggplot() + gg.theme_bw()

    # Grid has theme_grey
    spec = gg.gggrid([fig]) + gg.theme_grey()
    d = spec.as_dict()

    assert "theme" in d
    assert d["theme"] == {"name": "grey"}

    # Subplot should still carry its own theme
    fig_spec = d["figures"][0]
    assert fig_spec["kind"] == "plot"
    assert fig_spec.get("theme") == {"name": "bw"}


def test_gggrid_subplot_flavor_preserved():
    # Subplot has explicit solarized_light flavor
    fig = gg.ggplot() + gg.flavor_solarized_light()

    # Grid has darcula flavor
    spec = gg.gggrid([fig]) + gg.flavor_darcula()
    d = spec.as_dict()

    assert "theme" in d
    assert d["theme"] == {"flavor": "darcula"}

    # Subplot should still carry its own flavor
    fig_spec = d["figures"][0]
    assert fig_spec["kind"] == "plot"
    assert fig_spec.get("theme") == {"flavor": "solarized_light"}


def test_gggrid_subplot_theme_and_flavor_preserved():
    # Subplot: bw + solarized_light
    fig = gg.ggplot() + gg.theme_bw() + gg.flavor_solarized_light()

    # Grid: grey + darcula
    spec = gg.gggrid([fig]) + gg.theme_grey() + gg.flavor_darcula()
    d = spec.as_dict()

    # Grid-level theme/flavor (container)
    assert "theme" in d
    assert d["theme"] == {"name": "grey", "flavor": "darcula"}

    # Subplot-level theme/flavor (explicit on figure)
    fig_spec = d["figures"][0]
    assert fig_spec["kind"] == "plot"
    assert fig_spec.get("theme") == {"name": "bw", "flavor": "solarized_light"}


def test_gggrid_global_theme_subplot_theme_no_grid_theme():
    # Set a global theme
    gg.LetsPlot.set_theme(gg.theme_grey())
    try:
        # Subplot has its own theme_bw
        fig = gg.ggplot() + gg.theme_bw()

        # gggrid does NOT set any theme
        spec = gg.gggrid([fig])
    finally:
        # Clear global setting
        gg.LetsPlot.set_theme(None)

    d = spec.as_dict()
    fig_spec = d['figures'][0]
    assert fig_spec['kind'] == 'plot'  # Make sure it's a plot

    # The explicit theme_bw must NOT be replaced by the global theme
    assert fig_spec.get('theme') == {'name': 'bw'}
