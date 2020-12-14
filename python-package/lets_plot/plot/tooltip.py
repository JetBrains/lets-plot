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
    Configure tooltips.

    Parameters
    ----------
    format() defines the format for displaying the value:
            .format(field = 'density', format = '.1f')
            .format(field = '^color', format = 'value is {.1f}')
        This format will be applied to the mapped value in the default tooltip or to the corresponding value
        specified in the 'line' template.
        The field name starts with a '^' prefix for aesthetics, variable names are specified without prefix.
        It's possible to set the format for all positional aesthetics:
            field = "^X" - for all positional x;
            field = "^Y" - for all positional y.
        The format contains a number format ('1.f') or a string template ('{.1f}').
        The numeric format for non-numeric value will be ignored.
        If you need to include a brace character in the literal text, it can be escaped by doubling: {{ and }}, e.g.,
            .format('^color', '{{ {.1f} }}') -> "{ 17.0 }"
            .format('model', '{} {{text}}') -> "mustang {text}"
        The string template in format will allow to change lines for the default tooltip without 'line' specifying.
        Also the template will change the line for outliers.
        Aes and var formats are not interchangeable, i.e. var format will not be applied to aes, mapped to this variable.

    line() - line to show in the tooltip.
        Adds a line template to the tooltip with a label.
        Variables and aesthetics can be accessed via a special syntax:
            - ^color for aes
            - @year for variable
            - @{number of cylinders} for variable with spaces in the name
            - @{square m^2} for variable with spaces and '^' symbol in the name
            - @nameWith^ for the variable with '^' symbol in its name
        A '^' symbol can be escaped with a backslash, a brace character in the literal text - by doubling:
            .line('text') -> "text"
            .line('{{text}}') -> "{text}"
            .line('@model') -> "mustang"
            .line('{{@model}}') -> "{mustang}"
        The specified 'line' for outlier will move it to the general multi-line tooltip.
        The default tooltip has a label before the value, usually containing the name of the mapped variable.
        It has it's own behaviour, like blank label for axis aesthetics.
        This default label can be set in template using a pair of symbols '@|'.
        The label can be overridden by specifying a string value before '|' symbol.
        Within the tooltip line the label is left-aligned, the formed by template string is right-aligned.
        If a label is not specified, the string will be centered in the tooltip. For example:
            - line('^color'): no label, value is centered;
            - line('|^color'): label is empty, value is right-aligned;
            - line('@|^color'): default label is used, value is right-aligned;
            - line('my label|^color'): label is specified, value is right-aligned.

    Set tooltips = "none" to hide tooltips from this layer.

    anchor() - the corner of the plot to move the general tooltip:
        ['top_left' | 'top_center' | 'top_right' |
                      'middle_left' | 'middle_center' | 'middle_right' |
                      'bottom_left' | 'bottom_center' | 'bottom_right']

    min_width() - minimum width of the general multiline tooltip.

    Returns
    -------
        layer tooltips specification

    Examples
    ---------
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> LetsPlot.setup_html()
    >>> mpg_url = 'https://vincentarelbundock.github.io/Rdatasets/csv/ggplot2/mpg.csv'
    >>> mpg = pd.read_csv(mpg_url)
    >>> ggplot(mpg, aes(x='displ', y='hwy')) +
    ... geom_point(aes(color='cty', shape='drv'),
    ...                tooltips=layer_tooltips()
    ...                         .format('hwy', '.1f')              # set the format for the variable value
    ...                         .line('@manufacturer @model')      # "    ford mustang    "
    ...                         .line('cty/hwy|^color/@hwy')       # "cty/hwy    17.0/26.0"
    ...                         .line('@|@class')                  # "class     subcompact"
    ...                         .line('|@year')                    # "                2008"
    ...                         .line('--[mpg dataset] --'))       # " --[mpg dataset] -- "
    """

    def __init__(self):
        self._tooltip_formats: List = []
        self._tooltip_lines: List = None
        self._tooltip_anchor = None
        self._tooltip_min_width = None
        super().__init__('tooltips', name=None)

    def as_dict(self):
        d = super().as_dict()
        d['tooltip_formats'] = self._tooltip_formats
        d['tooltip_lines'] = self._tooltip_lines
        d['tooltip_anchor'] = self._tooltip_anchor
        d['tooltip_min_width'] = self._tooltip_min_width
        return d

    def format(self, field=None, format=None):
        self._tooltip_formats.append({"field": field, "format": format})
        return self

    def line(self, value):
        if self._tooltip_lines is None:
            self._tooltip_lines = []
        self._tooltip_lines.append(value)
        return self

    def anchor(self, value):
        self._tooltip_anchor = value
        return self

    def min_width(self, value):
        self._tooltip_min_width = value
        return self