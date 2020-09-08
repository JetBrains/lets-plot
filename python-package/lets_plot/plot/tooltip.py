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
        The string describes the content of the tooltip line as a template using the aesthetics and DataFrame variables.
        In the string aes names are prefixed with the '$' symbol, variable names have prefix "$var@".
        If variable name contains spaces, curly brackets are used.
        The label is separated with the '|' symbol. The '@' symbol as label value means the default label.

    tooltip_formats : map <source name> to <format>
        Specifies the format of the displayed aes and DataFrame variables.
        The naming rules are the same: '$' - before aes names, "$var@" - before variable names.
        The specified format will be applied to the corresponding value in the 'line' template.
        Note: the relationship between an aes and a variable is not supported,
              the format parameter is applied to a specific name.

    Use "none" to reset tooltips.

    Returns
    -------
        layer tooltips specification

    Examples
    ---------
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> mpg_url = 'https://vincentarelbundock.github.io/Rdatasets/csv/ggplot2/mpg.csv'
    >>> mpg = pd.read_csv(mpg_url)
    >>> ggplot(mpg, aes(x='displ', y='hwy')) \
    >>>   + geom_point(aes(color='cty', shape='drv'), \
    >>>                tooltips=layer_tooltips()
    >>>                         .format({'$color':'.1f'})                             # format for the aes value
    >>>                         .line('$color (miles per gallon)')                    # formatted aes without label
    >>>                         .line('@|$var@class')                                 # variable with the default label
    >>>                         .line('number of cylinders|$var@cyl')                 # variable with the given label
    >>>                         .line('${var@manufacturer} $var@model ($var@year)')   # complex line of 3 variables
    >>>                         .line('--[mpg dataset] --'))                          # static text
   """

    def __init__(self):
        self.tooltip_formats: Dict = {}
        self.tooltip_lines: List = None
        super().__init__('tooltips', name=None)

    def as_dict(self):
        d = super().as_dict()
        d['tooltip_formats'] = self.tooltip_formats
        d['tooltip_lines'] = self.tooltip_lines
        return d

    def format(self, value):
        self.tooltip_formats.update(value)
        return self

    def line(self, value):
        if self.tooltip_lines is None:
            self.tooltip_lines = []
        self.tooltip_lines.append(value)
        return self
