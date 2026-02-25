#  Copyright (c) 2022. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from typing import List

from lets_plot.plot.core import FeatureSpec, _filter_none

#
# Annotations
#

__all__ = ['layer_labels', 'smooth_labels']


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


class smooth_labels(layer_labels):
    """
    Configure annotations for `geom_smooth() <https://lets-plot.org/python/pages/api/lets_plot.geom_smooth.html>`__ layers.

    This class extends `layer_labels() <https://lets-plot.org/python/pages/api/lets_plot.layer_labels.html>`__
    and provides additional options for displaying statistics produced by the ``smooth`` stat, such as
    :math:`R^2`, adjusted :math:`R^2`, and the fitted model equation.

    It allows placing a multi-line annotation near the smooth curve and
    mixing custom text, computed variables (e.g. ``..r2..``), and a generated
    equation block.

    Default behavior
    ----------------
    If created without any additional configuration the annotation displays
    a single line with :math:`R^2`.

    Notes
    -----
    - Supported smooth-stat variables and markers that can be used in
      ``line()`` templates:

      - ``..r2..`` — :math:`R^2`.
      - ``..adjr2..`` — adjusted :math:`R^2`.
      - ``~eq`` — equation block marker. When a line equals ``'~eq'``,
        an equation for the fitted model is rendered (can be configured
        with ``eq()``).
    - ``smooth_labels`` **inherits** all features of `layer_labels() <https://lets-plot.org/python/pages/api/lets_plot.layer_labels.html>`__.
      Methods such as ``format()``, ``line()``, and ``size()``
      work exactly the same.
    - The only difference is ``inherit_color()``: it is applied
      **automatically** during initialization, so annotation text inherits
      the layer's color by default. Calling ``inherit_color()`` manually
      is not required.


    Examples
    --------

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        x = np.linspace(-2, 2, n)
        y = x**2 + np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes('x', 'y')) + geom_point() + \\
            geom_smooth(deg=2, labels=smooth_labels())

    """

    def __init__(self, variables: List[str] = None):
        super().__init__(variables)

        self._kind = "smooth_stat_summary_annotation"
        self._eq = {}
        self._label_x = None
        self._label_y = None

        self.inherit_color()

    def eq(self, lhs=None, rhs=None, format=None, threshold=None) -> "smooth_labels":
        """
        Configure the equation block for the smooth annotation.

        The equation block is typically inserted into the label using a line
        containing the special marker ``'~eq'``. When present, the backend
        will render a fitted model equation (and may also include related
        statistics depending on configuration).

        Parameters
        ----------
        lhs : str, optional
            Left-hand side label for the equation (default ``'y'``).
        rhs : str, optional
            Right-hand side variable representation (default ``'x'``).
        format : str or list of str, optional
            Formatting specification(s) for displaying numeric coefficients in the equation.
            Each item can be either a number format (e.g. ``'.1f'``).
            If a single format is provided, it is applied to all coefficients.
        threshold : float, optional
            A threshold value that can be used to control presentation of small
            coefficients (e.g. hide or simplify terms below the threshold).

        Returns
        -------
        ``smooth_labels``
            Annotations specification.

        Notes
        -----
        For more info see `Formatting <https://lets-plot.org/python/pages/formats.html>`__.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 9

            import numpy as np
            from lets_plot import *
            LetsPlot.setup_html()
            x = np.linspace(-2, 2, 50)
            y = x**2 + np.random.normal(size=len(x))
            ggplot({'x': x, 'y': y}, aes('x', 'y')) + geom_point() + \\
                geom_smooth(deg=2, labels=smooth_labels()
                    .line('~eq')
                    .eq(lhs='f(x)', rhs='x', format='.2f', threshold=0.1))

        """

        if format is not None:
            if isinstance(format, list):
                self._eq["format"] = format
            else:
                self._eq["format"] = [format]

        if rhs is not None:
            self._eq["rhs"] = rhs
        if lhs is not None:
            self._eq["lhs"] = lhs

        if threshold is not None:
            self._eq["threshold"] = threshold

        return self

    def label_x(self, position=None) -> "smooth_labels":
        """
        Set horizontal positioning of the smooth annotation.

        Parameters
        ----------
        position : str or float or list, optional
            Horizontal position for the annotation label.

            - String values set an anchor position: ``'left'``, ``'center'``, ``'right'``.
            - A numeric value sets an exact x-coordinate in plot data units.
            - A list can be used to control the position of each group separately.
              Each list item can be either a string anchor or a numeric coordinate.

        Notes
        -----
        By default, the annotation is placed in the top-left corner.
        When multiple groups are present, annotations are arranged in a vertical
        stack by default; passing a list to ``label_x()`` and/or ``label_y()`` allows
        positioning each group's annotation independently.

        Returns
        -------
        ``smooth_labels``
            Annotations specification.

        Examples
            --------
            .. jupyter-execute::
                :linenos:
                :emphasize-lines: 4

                from lets_plot import *
                LetsPlot.setup_html()
                ggplot({'x': [0, 1, 2], 'y': [0, 1, 4]}, aes('x', 'y')) + geom_point() + \\
                    geom_smooth(deg=2, labels=smooth_labels().line('~eq').label_x('center'))

        """

        self._label_x = position
        return self

    def label_y(self, position=None) -> "smooth_labels":
        """
        Set vertical positioning of the smooth annotation.

        Parameters
        ----------
        position : str or float or list, optional
            Vertical position for the annotation label.

            - String values set an anchor position: ``'top'``, ``'center'``, ``'bottom'``.
            - A numeric value sets an exact y-coordinate in plot data units.
            - A list can be used to control the position of each group separately.
              Each list item can be either a string anchor or a numeric coordinate.

        Notes
        -----
        By default, the annotation is placed in the top-left corner.
        When multiple groups are present, annotations are arranged in a vertical
        stack by default; passing a list to ``label_x()`` and/or ``label_y()`` allows
        positioning each group's annotation independently.

        Returns
        -------
        ``smooth_labels``
            Annotations specification.

        Examples
            --------
            .. jupyter-execute::
                :linenos:
                :emphasize-lines: 4

                from lets_plot import *
                LetsPlot.setup_html()
                ggplot({'x': [0, 1, 2], 'y': [0, 1, 4]}, aes('x', 'y')) + geom_point() + \\
                    geom_smooth(deg=2, labels=smooth_labels().line('~eq').label_y('center'))

        """

        self._label_y = position
        return self

    def as_dict(self):
        """
        Return a dictionary of all properties of the object.

        In addition to the fields provided by ``layer_labels.as_dict``,
        this method includes:
        - ``kind='smooth_stat_summary_annotation'``
        - ``options`` (may contain ``label_x``, ``label_y``, and ``eq``)

        Returns
        -------
        dict
            Dictionary of properties.

        """

        d = super().as_dict()
        opts = {
            'label_x': self._label_x,
            'label_y': self._label_y
        }

        if self._eq:
            opts['eq'] = self._eq

        d['kind'] = self._kind
        d["options"] = _filter_none(opts)

        return _filter_none(d)
