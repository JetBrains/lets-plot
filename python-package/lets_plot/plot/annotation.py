#  Copyright (c) 2022. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from typing import List

from lets_plot.plot.core import FeatureSpec, _filter_none

#
# Annotations
#

__all__ = ['annotations']


class annotations(FeatureSpec):
    """
    Configure annotations (for pie chart).

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        data = {'name': ['a', 'b', 'c', 'd', 'b'], 'value': [40, 90, 10, 50, 20 ] }
        ggplot(data) + geom_pie(aes(slice='value', fill='name'), size=15, hole=0.4, \\
                                stat='identity', tooltips = 'none', \\
                                annotations=annotations().line('@value'))

    """

    def __init__(self, variables: List[str] = None):
        """
        Initialize self.

        Parameters
        ----------
        variables : list of str
            Variable names to place in the annotation with default formatting.

        """

        self._formats: List = []
        self._lines: List = None
        self._variables = variables
        self._size = None
        super().__init__('annotations', name=None)

    def as_dict(self):
        """
        Returns the dictionary of all properties of the object.

        Returns
        -------
        dict
            Dictionary of properties.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 6

            from lets_plot import *
            LetsPlot.setup_html()
            annotations().format('@{..prop..}', '.0%') \\
                         .line('@name') \\
                         .line('(@{..prop..})') \\
                         .as_dict()

        """
        d = super().as_dict()
        d['formats'] = self._formats
        d['lines'] = self._lines
        d['variables'] = self._variables
        d['annotation_size'] = self._size
        return _filter_none(d)

    def format(self, field=None, format=None):
        """
        Defines the format for displaying the value.
        This format will be applied to the corresponding value specified in the 'line' template.

        Parameters
        ----------
        field : str
            Name of an aesthetic or variable that would be formatted.
            The field name starts with a '^' prefix for aesthetics,
            the variable name starts with a '@' prefix or without any prefix.
        format : str
            Formatting specification. The format contains a number format ('1.f'),
            a string template ('{.1f}') or a date/time format ('%d.%m.%y').
            The numeric format for non-numeric value will be ignored.
            If you need to include a brace character in the literal text,
            it can be escaped by doubling: `{{` and `}}`.

        Returns
        -------
        `annotations`
            Annotations specification.

        Notes
        -----
        For more info see https://lets-plot.org/pages/formats.html.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 8

            from lets_plot import *
            from lets_plot.mapping import *
            LetsPlot.setup_html()
            data = {'name': ['a', 'b', 'c', 'd', 'b'], 'value': [40, 90, 10, 50, 20 ] }
            ggplot(data) + geom_pie(aes(fill=as_discrete('name', order_by='..count..'), weight='value'), \\
                                    size=15, tooltips='none', \\
                                    annotations=annotations(['..proppct..']) \\
                                                    .format('..proppct..', '{.1f}%'))

        |

        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 8, 10, 12, 14

            from lets_plot import *
            from lets_plot.mapping import *
            LetsPlot.setup_html()
            data = {'name': ['a', 'b', 'c', 'd', 'b'], 'value': [40, 90, 10, 50, 20 ] }
            ggplot(data) + geom_pie(aes(fill=as_discrete('name', order_by='..count..', order=1), weight='value'), \\
                                    size=15, tooltips='none', \\
                                    annotations=annotations() \\
                                                    .format('^fill', '{{{}}}') \\
                                                    .line('^fill') \\
                                                    .format('..count..', 'd') \\
                                                    .line('@{..count..}') \\
                                                    .format('..prop..', '.1%') \\
                                                    .line('@{..prop..}') \\
                                                    .format('..sum..', 'of {d}') \\
                                                    .line('@{..sum..}'))

        """
        self._formats.append({"field": field, "format": format})
        return self

    def line(self, value):
        """
        Line to show in the annotation.

        Parameters
        ----------
        value : str
            Enriched string which becomes one line of the annotation.

        Returns
        -------
        `annotations`
            Annotations specification.

        Notes
        -----
        Variables and aesthetics can be accessed via special syntax:

        - ^color for aes,
        - @x for variable,
        - @{x + 1} for variable with spaces in the name,
        - @{x^2 + 1} for variable with spaces and '^' symbol in the name,
        - @x^2 for variable with '^' symbol in its name.

        A '^' symbol can be escaped with a backslash, a brace character
        in the literal text - by doubling:

        - 'x\^2' -> "x^2"
        - '{{x}}' -> "{x}"

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 7-9

            from lets_plot import *
            LetsPlot.setup_html()
            data = {'name': ['a', 'b', 'c', 'd', 'b'], 'value': [40, 90, 10, 50, 20 ] }
            ggplot(data) + geom_pie(aes(fill='name', weight='value'), size=15, \\
                                    tooltips='none', \\
                                    annotations=annotations() \\
                                                    .line('\'^fill\'') \\
                                                    .line('@{..count..}') \\
                                                    .line('@{..prop..}\n(@{..sum..})') \\
                                                    .format('..prop..', '.1%'))

        """
        if self._lines is None:
            self._lines = []
        self._lines.append(value)
        return self

    def size(self, value):
        """
        Text size in the annotation.

        Parameters
        ----------
        value : float
            Text size in the annotation.

        Returns
        -------
        `annotations`
            Annotations specification.

        Examples
        --------

        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 7

            from lets_plot import *
            LetsPlot.setup_html()
            data = {'name': ['a', 'b', 'c', 'd', 'b'], 'value': [40, 90, 10, 50, 20 ] }
            ggplot(data) + geom_pie(aes(slice='value', fill='name'), size=15, hole=0.4, \\
                                    stat='identity', tooltips = 'none', \\
                                    annotations=annotations().line('@value')
                                                             .size(25))

        """

        self._size = value
        return self
