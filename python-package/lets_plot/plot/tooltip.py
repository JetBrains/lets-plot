#
#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from .core import FeatureSpec

#
# Tooltips
#

__all__ = ['layer_tooltips', 'tooltip_line']


def tooltip_line(value=None, label=None, format=None):
    """
    Adjust the content of the tooltip's line.

    Parameters
    ----------
    value:
        variable name
    label:
        tooltip label
    format:
        tooltip format

    Returns
    -------
        dictionary described the tooltip line specification
    """

    return dict(value=value, label=label, format=format)


def layer_tooltips(lines=None):
    """
    Define tooltips.

    Parameters
    ----------
    'lines': List of variables to show in the tooltip.
        Each element of the list can contain a variable name or the full specification of a tooltip line
        using the function 'tooltip_line':
            tooltip_line(value = <variable_name>, label = <label_text>, format = <format>)

        lines = None - default tooltips
        lines = [] - no tooltips

    Returns
    -------
        layer tooltips specification

    Examples
    ---------
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> mpg_url = 'https://vincentarelbundock.github.io/Rdatasets/csv/ggplot2/mpg.csv'
    >>> mpg = pd.read_csv(mpg_url)
    >>> p = ggplot(mpg, aes(x='displ', y='hwy')) \)
    >>>   + geom_point(aes(color='cty', shape='drv'), \
    >>>                tooltips=layer_tooltips(lines=[tooltip_line(value='color', label='city miles per gallon:')]))
    """

    return FeatureSpec('tooltips', name=None, lines=lines)
