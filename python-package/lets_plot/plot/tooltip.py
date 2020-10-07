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
        Adds a line template to the tooltip with a label.
        Variables and aesthetics can be accessed via a special syntax:
            - $color for aes
            - $var@year for variable
            - ${var@number of cylinders} for variable with spaces in the name
            - ${var@income in $} for variable with spaces and dollar sign in the name
            - $var@income_$_per_month for variable with dollar sign in the name
        The specified 'line' for outlier will move it to the general multi-line tooltip.
        The default tooltip has a label before the value, usually containing the name of the mapped variable.
        It has it's own behaviour, like blank label for axis aesthetics.
        This default label can be set in template using a pair of symbols '@|'.
        The label can be overridden by specifying a string value before '|' symbol.
        Within the tooltip line the label is left-aligned, the formed by template string is right-aligned.
        If a label is not specified, the string will be centered in the tooltip.

    format() defines a format for the displayable value:
            .format(field = 'var@density', format = '.1f')
            .format(field = 'color', format = 'value is {.1f}')
        This format will be applied to the mapped value in the default tooltip or to the corresponding value
        specified in the 'line' template.
        The format contains a number format ('1.f') or a string template ('{.1f}').
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
    >>> ggplot(mpg, aes(x='displ', y='hwy')) +
    ... geom_point(aes(color='cty', shape='drv'),
    ...                tooltips=layer_tooltips()
    ...                         .format('var@hwy', '.1f')                # set the format for the variable value
    ...                         .line('$var@manufacturer $var@model')    # "    ford mustang    "
    ...                         .line('cty/hwy|$color/$var@hwy')         # "cty/hwy    17.0/26.0"
    ...                         .line('@|$var@class')                    # "class     subcompact"
    ...                         .line('|$var@year')                      # "                2008"
    ...                         .line('--[mpg dataset] --'))             # " --[mpg dataset] -- "
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
