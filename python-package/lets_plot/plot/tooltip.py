#
#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from .core import FeatureSpec
from typing import List, Dict

#
# Tooltips
#

__all__ = ['layer_tooltips']


class layer_tooltips(FeatureSpec):
    """
    Define tooltips.

    Parameters
    ----------
    tooltip_lines : list of string - lines to show in the tooltip
        The string describes the content of the tooltip line.
        The description can contain names of aes, variable, constant value and static text.
        Aas names are prefixed with '$' symbol, variable names have prefix "$var@".
        If variable name contains spaces, curly brackets are used.
        The Label is separated with '|' symbol. The '@' symbol as label value means the default label.

    tooltip_formats : map <source name> to <format>
        Specifies the format of displayed variables

    Returns
    -------
        layer tooltips specification

    Examples
    ---------
    >>> ggplot(mpg, aes(x='displ', y='hwy')) \
    >>>   + geom_point(aes(color='cty', shape='drv'), \
    >>>                tooltips=layer_tooltips()
    >>>                             .format({'$color':'.1f'})
    >>>                             .line('$color (miles per gallon)'))
   """

    def __init__(self):
        self.tooltip_formats: Dict = {}
        self.tooltip_lines: List = []
        super().__init__('tooltips',
                         name=None,
                         tooltip_lines=self.tooltip_lines,
                         tooltip_formats=self.tooltip_formats)

    def format(self, value):
        self.tooltip_formats.update(value)
        return self

    def line(self, value):
        self.tooltip_lines.append(value)
        return self