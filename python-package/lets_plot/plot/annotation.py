#  Copyright (c) 2022. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from typing import List

from lets_plot.plot.core import FeatureSpec, _filter_none

#
# Annotations
#

__all__ = ['layer_labels']


class layer_labels(FeatureSpec):
    """
    Configure annotations for geometry layers.

    Annotations are currently supported for bar, pie, and crossbar geometry
    layers. This class provides methods to customize the appearance and
    content of text labels displayed on these geometries.

    Notes
    -----
    By default, annotation text color is automatically selected for optimal
    contrast: white text appears on darker filled geometries, and black text
    appears on lighter filled geometries.

    The text color can be manually specified using:
    ``theme(label_text=element_text(color=...))``

    Alternatively, the ``inherit_color()`` method can be used to override both
    automatic and manual color settings, making the annotation text use the
    geometry's ``color`` aesthetic instead.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 6

        from lets_plot import *
        LetsPlot.setup_html()
        data = {'name': ['a', 'b', 'c', 'd', 'b'], 'value': [40, 90, 10, 50, 20 ]}
        ggplot(data) + geom_pie(aes(slice='value', fill='name'), size=15, hole=0.4, \\
                                stat='identity', tooltips='none', \\
                                labels=layer_labels().line('@value'))

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
        self._useLayerColor = None
        super().__init__('labels', name=None)

    def as_dict(self):
        """
        Return a dictionary of all properties of the object.

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
            layer_labels().format('@{..prop..}', '.0%') \\
                         .line('@name') \\
                         .line('(@{..prop..})') \\
                         .as_dict()

        """
        d = super().as_dict()
        d['formats'] = self._formats
        d['lines'] = self._lines
        d['variables'] = self._variables
        d['annotation_size'] = self._size
        d['use_layer_color'] = self._useLayerColor
        return _filter_none(d)

    def format(self, field=None, format=None):
        """
        Define the format for displaying the value.
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
            it can be escaped by doubling: ``{{`` and ``}}``.

        Returns
        -------
        ``layer_labels``
            Annotations specification.

        Notes
        -----
        For more info see `Formatting <https://lets-plot.org/python/pages/formats.html>`__.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 7

            from lets_plot import *
            LetsPlot.setup_html()
            data = {'name': ['a', 'b', 'c', 'd', 'b'], 'value': [40, 90, 10, 50, 20 ]}
            ggplot(data) + geom_pie(aes(fill=as_discrete('name', order_by='..count..'), weight='value'), \\
                                    size=15, tooltips='none', \\
                                    labels=layer_labels(['..proppct..']) \\
                                                  .format('..proppct..', '{.1f}%'))

        |

        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 7, 9, 11, 13

            from lets_plot import *
            LetsPlot.setup_html()
            data = {'name': ['a', 'b', 'c', 'd', 'b'], 'value': [40, 90, 10, 50, 20 ]}
            ggplot(data) + geom_pie(aes(fill=as_discrete('name', order_by='..count..', order=1), weight='value'), \\
                                    size=15, tooltips='none', \\
                                    labels=layer_labels() \\
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
        Add a line of text to the multiline label annotation.

        This method configures one line of text that will be displayed in a
        multiline label. Multiple calls to this method can be chained to build
        up a complete multiline annotation.

        Parameters
        ----------
        value : str
            The text content for this line of the annotation. Can include
            variable and aesthetic references.

        Returns
        -------
        ``layer_labels``
            Annotations specification.

        Notes
        -----
        Variables and aesthetics can be accessed via special syntax:

        - ^color for aesthetics,
        - @x for variable,
        - @{x + 1} for variable with spaces in the name,
        - @{x^2 + 1} for variable with spaces and '^' symbol in the name,
        - @x^2 for variable with '^' symbol in its name.

        Special characters can be escaped:

        - 'x\\\\^2' -> "x^2" (escape ^ with backslash)
        - '{{x}}' -> "{x}" (escape braces by doubling)

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 8-11

            from lets_plot import *
            LetsPlot.setup_html()
            data = {'name': ['a', 'b', 'c', 'd', 'b'], 'value': [40, 90, 10, 50, 20 ]}
            ggplot(data) + geom_pie(aes(fill='name', weight='value'), size=15, \\
                                    tooltips='none', \\
                                    labels=layer_labels()\\
                                          .format('..prop..', '.1%')\\
                                          .line('"^fill"')\\
                                          .line('@{..count..}')\\
                                          .line('@{..prop..}')\\
                                          .line('(@{..sum..})'))

        """
        if self._lines is None:
            self._lines = []
        self._lines.append(value)
        return self

    def size(self, value):
        """
        Set the text size for the annotation.

        Parameters
        ----------
        value : float
            The text size value for the annotation.

        Returns
        -------
        ``layer_labels``
            Annotations specification.

        Examples
        --------

        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 7

            from lets_plot import *
            LetsPlot.setup_html()
            data = {'name': ['a', 'b', 'c', 'd', 'b'], 'value': [40, 90, 10, 50, 20 ]}
            ggplot(data) + geom_pie(aes(slice='value', fill='name'), size=15, hole=0.4, \\
                                    stat='identity', tooltips='none', \\
                                    labels=layer_labels().line('@value')
                                                             .size(25))

        """

        self._size = value
        return self

    def inherit_color(self):
        """
        Use the layer's color for the annotation text.

        When enabled, the annotation text will inherit the color from the
        layer it's associated with, rather than using a default or
        explicitly set color.

        Returns
        -------
        ``layer_labels``
            Annotations specification.

        Examples
        --------

        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 8

            from lets_plot import *
            LetsPlot.setup_html()
            data = {'name': ['a', 'b', 'c', 'd', 'b'], 'value': [40, 90, 10, 50, 20 ]}
            ggplot(data) + geom_pie(aes(slice='value', color='name'), alpha=0, size=15, hole=0.4, \\
                                    stroke=5, spacer_color='pen', \\
                                    stat='identity', tooltips='none', \\
                                    labels=layer_labels().line('@value')
                                                         .inherit_color())

        """

        self._useLayerColor = True
        return self
