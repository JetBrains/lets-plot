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
    line() - line to show in the tooltip
        Adds line template to the tooltip.
        Variables and aesthetics can be accessed via a special syntax:
            - $color for aes
            - $var@year for variable
            - ${var@number of cylinders} for variable with spaces in the name
            - ${var@income in $} for variable with spaces and dollar sign in the name
            - $var@income_$_per_month for variable with dollar sign in the name
        The specified 'line' for outlier will move it to the general multi-line tooltip.

    format() defines a format for the displayable value:
            .format(field = 'var@density', format = '.1f')
            .format(field = 'color', format = 'value is {.1f}')
        This format will be applied to the mapped value in the default tooltip or to the corresponding value
        specified in the 'line' template.
        The format contains a number format ('1.f') or a string template ('{.1f}').
        The numeric format for non-numeric value will be ignored.
        The string template in format will allow to change lines for the default tooltip without 'line' specifying.
        Also the template will change the line for outliers.
        To include a bracket character in the literal text - it can be escaped by doubling: {{ and }}.
        Aes and var formats are not interchangeable, i.e. var format will not be applied to aes, mapped to this variable.

    "none" to not show tooltips from this layer.

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
    >>>                         .format('color', '.1f')                               # set the format for the aes value
    >>>                         .line('$color (miles per gallon)')                    # "15.0 (miles per gallon)"
    >>>                         .line('number of cylinders: $var@cyl')                # "number of cylinders: 4"
    >>>                         .line('${var@manufacturer} $var@model ($var@year)')   # "ford mustang (1999)"
    >>>                         .line('--[mpg dataset] --'))                          # "--[mpg dataset] --"
   """

    def __init__(self):
        self.tooltip_formats: List = []
        self.tooltip_lines: List = None
        super().__init__('tooltips', name=None)

    def as_dict(self):
        d = super().as_dict()
        d['tooltip_formats'] = self.tooltip_formats
        d['tooltip_lines'] = self.tooltip_lines
        return d

    def format(self, field=None, format=None):
        if field is None or format is None:
            raise ValueError("Invalid 'format' arguments: 'field' and 'format' are expected.")

        self.tooltip_formats.append({"field": field, "format": format})
        return self

    def line(self, value):
        if self.tooltip_lines is None:
            self.tooltip_lines = []
        self.tooltip_lines.append(value)
        return self
