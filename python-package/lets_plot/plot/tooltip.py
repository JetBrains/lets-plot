#
#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from typing import List

from lets_plot.plot.core import FeatureSpec, _filter_none

#
# Tooltips
#

__all__ = ['layer_tooltips']


class layer_tooltips(FeatureSpec):
    """
    Configure tooltips.

    Notes
    -----
    Set tooltips='none' to hide tooltips from this layer.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 15

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        data = {
            'id': np.arange(n),
            'x': np.random.normal(size=n),
            'y': np.random.normal(size=n),
            'c': np.random.choice(['a', 'b'], size=n),
            'w': np.random.randint(1, 11, size=n)
        }
        ggplot(data, aes('x', 'y')) + \\
            geom_point(aes(color='c', size='w'), \\
                       tooltips=layer_tooltips().line('@c "@id"')
                                                .line('---')
                                                .format('@y', '.2f')
                                                .line('(x, y)|(^x, @y)')
                                                .line('@|@w')) + \\
            scale_size(range=[2, 4])

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 12

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        data = {
            'x': np.random.normal(size=n),
            'y': np.random.normal(size=n),
            'c': np.random.randint(10, size=n)
        }
        ggplot(data, aes('x', 'y')) + \\
            geom_point(aes(color='c'), tooltips='none')

    """

    def __init__(self, variables: List[str] = None):
        """
        Initialize self.

        Parameters
        ----------
        variables : list of str
            Variable names to place in the general tooltip with default formatting.

        """

        self._tooltip_formats: List = []
        self._tooltip_lines: List = None
        self._tooltip_anchor = None
        self._tooltip_min_width = None
        self._tooltip_color = None
        self._tooltip_variables = variables
        self._tooltip_title = None
        super().__init__('tooltips', name=None)

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
            layer_tooltips().format('@x', '.2f')\\
                            .line('@x @y')\\
                            .line('^fill')\\
                            .as_dict()

        """
        d = super().as_dict()
        d['formats'] = self._tooltip_formats
        d['lines'] = self._tooltip_lines
        d['tooltip_anchor'] = self._tooltip_anchor
        d['tooltip_min_width'] = self._tooltip_min_width
        d['tooltip_color'] = self._tooltip_color
        d['variables'] = self._tooltip_variables
        d['title'] = self._tooltip_title
        return _filter_none(d)

    def format(self, field=None, format=None):
        """
        Defines the format for displaying the value.
        This format will be applied to the mapped value in the default tooltip
        or to the corresponding value specified in the 'line' template.

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
        `layer_tooltips`
            Layer tooltips specification.

        Notes
        -----
        It's possible to set the format for all positional aesthetics:

        - field='^X' - for all positional x,
        - field='^Y' - for all positional y.

        |

        The string template in `format` will allow to change lines
        for the default tooltip without `line` specifying.
        Also the template will change the line for outliers.
        Aes and var formats are not interchangeable, i.e. var format
        will not be applied to aes, mapped to this variable.

        |

        For more info see https://lets-plot.org/pages/formats.html.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 15-16, 18, 20, 22

            import numpy as np
            from lets_plot import *
            LetsPlot.setup_html()
            n = 100
            np.random.seed(42)
            data = {
                'a': np.random.normal(size=n),
                'b': np.random.normal(size=n),
                'c': np.random.choice(['X', 'Y'], size=n),
                'd': np.random.uniform(size=n),
                'e': np.random.randint(100, size=n)
            }
            ggplot(data, aes('a', 'b')) + \\
                geom_point(aes(shape='c', size='e', color='d'), show_legend=False, \\
                           tooltips=layer_tooltips().format(field='a', format='.1f')\\
                                                    .format('^y', '.1f')\\
                                                    .line('(@a, ^y)')\\
                                                    .format('c', '{{{}}}')\\
                                                    .line('@|@c')\\
                                                    .format('^color', '≈ {.2f}')\\
                                                    .line('@|^color')\\
                                                    .format('e', '{}%')\\
                                                    .line('e|@e')) + \\
                scale_size(range=[2, 4])

        |

        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 11-13

            import numpy as np
            from lets_plot import *
            LetsPlot.setup_html()
            n = 50
            np.random.seed(42)
            data = {
                'v': np.random.normal(size=n),
                'c': np.random.choice(['a', 'b', 'c'], size=n),
            }
            ggplot(data, aes('c', 'v')) + \\
                geom_boxplot(tooltips=layer_tooltips().format('^Y', '.4f')\\
                                                      .format('^ymin', 'min y: {.2f}')\\
                                                      .format('^ymax', 'max y: {.2f}'))

        """
        self._tooltip_formats.append({"field": field, "format": format})
        return self

    def line(self, value):
        """
        Line to show in the tooltip.
        Adds a line template to the tooltip with a label.

        Parameters
        ----------
        value : str
            Enriched string which becomes one line of the tooltip.

        Returns
        -------
        `layer_tooltips`
            Layer tooltips specification.

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

        The specified 'line' for outlier will move it to the general multi-line tooltip.
        The default tooltip has a label before the value,
        usually containing the name of the mapped variable.
        It has its own behaviour, like blank label for axis aesthetics.
        This default label can be set in template using a pair of symbols '@|'.
        The label can be overridden by specifying a string value before '|' symbol.
        Within the tooltip line the label is left-aligned,
        the string formed by template is right-aligned.
        If a label is not specified, the string will be centered in the tooltip.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 12, 14

            import numpy as np
            from lets_plot import *
            LetsPlot.setup_html()
            n = 100
            np.random.seed(42)
            x = np.linspace(-3, 3, n)
            y = 9 - x ** 2 + np.random.normal(scale=.3, size=n)
            data = {'x': x, '9 - x^2': y}
            ggplot(data) + \\
                geom_point(aes('x', '9 - x^2'), \\
                           tooltips=layer_tooltips().format('x', '.3f')\\
                                                    .line('x = @x')\\
                                                    .format('9 - x^2', '.3f')\\
                                                    .line('9 - x\^2 = @{9 - x^2}'))

        |

        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 17-20

            import numpy as np
            from lets_plot import *
            LetsPlot.setup_html()
            n = 100
            np.random.seed(42)
            data = {
                'x': np.random.normal(size=n),
                'y': np.random.normal(size=n),
                'c': np.random.choice(['X', 'Y'], size=n),
                'p': np.random.uniform(size=n),
                'w': np.random.randint(100, size=n)
            }
            ggplot(data, aes('x', 'y')) + \\
                geom_point(aes(shape='c', size='w', color='p'), show_legend=False, \\
                           tooltips=layer_tooltips().format('x', '.2f')\\
                                                    .format('y', '.2f')\\
                                                    .line('(^x, ^y)')\\
                                                    .line('|^shape')\\
                                                    .line('@|^color')\\
                                                    .line('w|^size')) + \\
                scale_size(range=[2, 4])

        """
        if self._tooltip_lines is None:
            self._tooltip_lines = []
        self._tooltip_lines.append(value)
        return self

    def anchor(self, value):
        """
        Specifies a fixed position for a general tooltip.

        Parameters
        ----------
        value : {'top_left', 'top_center', 'top_right', 'middle_left', 'middle_center', 'middle_right', 'bottom_left', 'bottom_center', 'bottom_right'}
            Type of the tooltip anchoring.

        Returns
        -------
        `layer_tooltips`
            Layer tooltips specification.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 10

            import numpy as np
            from lets_plot import *
            LetsPlot.setup_html()
            n = 100
            np.random.seed(42)
            x = np.random.normal(size=n)
            y = np.random.normal(size=n)
            ggplot({'x': x, 'y': y}, aes('x', 'y')) + \\
                geom_point(tooltips=layer_tooltips().line('(^x, ^y)')\\
                                                    .anchor('top_center'))

        """
        self._tooltip_anchor = value
        return self

    def min_width(self, value):
        """
        Minimum width of the general tooltip.

        Parameters
        ----------
        value : float
            Minimum width value in px.

        Returns
        -------
        `layer_tooltips`
            Layer tooltips specification.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 10

            import numpy as np
            from lets_plot import *
            LetsPlot.setup_html()
            n = 100
            np.random.seed(42)
            x = np.random.normal(size=n)
            y = np.random.normal(size=n)
            ggplot({'x': x, 'y': y}, aes('x', 'y')) + \\
                geom_point(tooltips=layer_tooltips().line('(^x, ^y)')\\
                                                    .min_width(200))

        """
        self._tooltip_min_width = value
        return self

    def color(self, value):
        """
        Function `color(value)` is deprecated.

        """
        print("WARN: The function color() is deprecated and is no longer supported.")

        self._tooltip_color = value
        return self

    def title(self, value):
        """
        Line with title to show in the tooltip.
        Adds a title template to the tooltip.

        Parameters
        ----------
        value : str
            Enriched string which becomes the title of the tooltip.

        Returns
        -------
        `layer_tooltips`
            Layer tooltips specification.

        Notes
        -----
        The specification rules are the same as for the `lines()` function:
        variables and aesthetics can be used in the template.
        The resulting string will be at the beginning of the general tooltip, centered and highlighted in bold.
        A long title can be split into multiple lines using `\\\\n` as a text separator.

        """
        self._tooltip_title = value
        return self
