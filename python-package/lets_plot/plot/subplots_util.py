#  Copyright (c) 2025. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from lets_plot.plot.core import PlotSpec
from ._global_theme import _get_global_theme
from .subplots import SupPlotsSpec


def _strip_theme_if_global(fig):
    # Strip this global theme if defined
    global_theme_options = _get_global_theme()

    # Strip global theme options from plots in grid (see issue: #966).
    if global_theme_options is not None and fig is not None and 'theme' in fig.props() and fig.props()[
        'theme'] == global_theme_options.props():
        if isinstance(fig, PlotSpec):
            fig = PlotSpec.duplicate(fig)
            fig.props().pop('theme')
            return fig
        elif isinstance(fig, SupPlotsSpec):
            fig = SupPlotsSpec.duplicate(fig)
            fig.props().pop('theme')
            return fig
    return fig
