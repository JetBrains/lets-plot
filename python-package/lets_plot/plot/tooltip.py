#
#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from .core import FeatureSpec
from typing import List, Dict

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


# def layer_tooltips(lines=None):
#    return FeatureSpec('tooltips', name=None, lines=lines)


class layer_tooltips(FeatureSpec):
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

    TODO: other parameters:
        - tooltip_formats/tooltip_lines
        - format/line function

    Returns
    -------
        layer tooltips specification

    Examples
    ---------
    Variant 1

    >>> import pandas as pd
    >>> from lets_plot import *
    >>> mpg_url = 'https://vincentarelbundock.github.io/Rdatasets/csv/ggplot2/mpg.csv'
    >>> mpg = pd.read_csv(mpg_url)
    >>> p = ggplot(mpg, aes(x='displ', y='hwy')) \
    >>>   + geom_point(aes(color='cty', shape='drv'), \
    >>>                tooltips=layer_tooltips(lines=[tooltip_line('$color', label = '', format='{.1f} (miles per gallon)')]))

    Variant 2
    ---------
    >>> p = ggplot(mpg, aes(x='displ', y='hwy')) \
    >>>   + geom_point(aes(color='cty', shape='drv'), \
    >>>                tooltip_formats={'$color': '.1f'},\
    >>>                tooltip_lines=['$color (miles per gallon)'])

    Variant 3
    ---------
    >>> p = ggplot(mpg, aes(x='displ', y='hwy')) \
    >>>   + geom_point(aes(color='cty', shape='drv'), \
    >>>                tooltips=layer_tooltips().format({'$color':'.1f'})
    >>>                                         .line('$color (miles per gallon)'))
   """

    def __init__(self, lines=None):
        self.tooltip_formats: Dict = {}
        self.tooltip_lines: List = []
        super().__init__('tooltips',
                         name=None,
                         lines=lines,
                         tooltip_lines=self.tooltip_lines,
                         tooltip_formats=self.tooltip_formats)

    def format(self, format):
        self.tooltip_formats.update(format)
        return self

    def line(self, value):
        self.tooltip_lines.append(value)
        return self