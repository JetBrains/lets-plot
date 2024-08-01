#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import numbers

from lets_plot._global_settings import has_global_value, get_global_val, MAX_WIDTH, MAX_HEIGHT
from lets_plot.geo_data_internals.utils import is_geocoder
from lets_plot.plot._global_theme import _get_global_theme
from lets_plot.plot.core import FeatureSpec
from lets_plot.plot.core import PlotSpec
from lets_plot.plot.util import as_annotated_data, key_int2str

__all__ = ['ggplot', 'ggsize', 'GGBunch']


def ggplot(data=None, mapping=None):
    """
    Create a new ggplot plot.

    Parameters
    ----------
    data : dict or Pandas or Polars `DataFrame`
        Default dataset to use for the plot. If not specified,
        must be supplied in each layer added to the plot.
    mapping : `FeatureSpec`
        Default list of aesthetic mappings to use for the plot.
        If not specified, must be supplied in each layer added to the plot.

    Returns
    -------
    `PlotSpec`
        Plot specification.

    Notes
    -----
    `ggplot()` initializes a ggplot object.
    It can be used to declare the input data frame for a graphic and
    to specify the set of plot aesthetics intended to be common
    throughout all subsequent layers unless specifically overridden.

    `ggplot()` is typically used to construct a plot incrementally,
    using the + operator to add layers to the existing ggplot object.
    This is advantageous in that the code is explicit about which layers
    are added and the order in which they are added. For complex graphics
    with multiple layers, initialization with `ggplot()` is recommended.

    There are three common ways to invoke ggplot (see examples below):

    - `ggplot(data, aes(x, y))`: This method is recommended if all layers use the same data and the same set of aesthetics, although this method can also be used to add a layer using data from another data frame.
    - `ggplot(data)`: This method specifies the default data frame to use for the plot, but no aesthetics are defined up front. This is useful when one data frame is used predominantly as layers are added, but the aesthetics may vary from one layer to another.
    - `ggplot()`: This method initializes a skeleton ggplot object which is fleshed out as layers are added. This method is useful when multiple data frames are used to produce different layers, as is often the case in complex graphics.

    `ggplot()` with no layers defined will produce an error message:
    "No layers in plot".

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11, 13, 15

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 100
        x = np.random.uniform(-1, 1, size=n)
        y = np.random.normal(size=n)
        data = {'x': x, 'y': 25 * x ** 2 + y}
        # three ways to invoke ggplot, producing the same output:
        # (1)
        ggplot(data, aes(x='x', y='y')) + geom_point()
        # (2)
        ggplot(data) + geom_point(aes(x='x', y='y'))
        # (3)
        ggplot() + geom_point(aes(x='x', y='y'), data=data)

    """
    if isinstance(data, FeatureSpec):
        raise ValueError("Object {!r} is not acceptable as 'data' argument in ggplot()".format(data.kind))

    if is_geocoder(data):
        data = data.get_geocodes()

    data = key_int2str(data)

    data, mapping, data_meta = as_annotated_data(data, mapping)

    plot_spec = PlotSpec(data, mapping, scales=[], layers=[], **data_meta)

    if _get_global_theme() is not None:
        plot_spec += _get_global_theme()

    return plot_spec


# noinspection SpellCheckingInspection
def ggsize(width, height):
    """
    Specify overall size of plot.

    Parameters
    ----------
    width : int
        Width of plot in px.
    height : int
        Height of plot in px.

    Returns
    -------
    `FeatureSpec`
        Plot size specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        x = np.arange(50)
        y = np.random.normal(size=50)
        data = {'x': x, 'y': y}
        ggplot(data) + geom_line(aes('x', 'y')) + ggsize(400, 150)

    """
    assert isinstance(width, numbers.Number), "'width' must be numeric"
    assert isinstance(height, numbers.Number), "'height' must be numeric"

    max_width = get_global_val(MAX_WIDTH) if has_global_value(MAX_WIDTH) else 10_000
    max_height = get_global_val(MAX_HEIGHT) if has_global_value(MAX_HEIGHT) else 10_000

    assert width <= max_width, "'width' must be less than or equal to " + str(max_width)
    assert height <= max_height, "'height' must be less than or equal to " + str(max_height)

    return FeatureSpec('ggsize', name=None, width=width, height=height)


class GGBunch(FeatureSpec):
    """
    Collection of plots created by ggplot function.
    Use method `add_plot()` to add plot to 'bunch'.
    Each plot can have arbitrary location and size.
    Use `show()` to draw all plots in bunch.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10-14

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 100
        x = np.arange(n)
        y = np.random.normal(size=n)
        w, h = 200, 150
        p = ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + ggsize(w, h)
        bunch = GGBunch()
        bunch.add_plot(p + geom_point(), 0, 0)
        bunch.add_plot(p + geom_histogram(bins=3), w, 0)
        bunch.add_plot(p + geom_line(), 0, h, 2*w, h)
        bunch.show()

    """

    def __init__(self):
        """
        Initialize self.
        """
        super().__init__('ggbunch', None)
        self.items = []

    def add_plot(self, plot_spec: PlotSpec, x, y, width=None, height=None):
        """
        Add plot to 'bunch'.

        Parameters
        ----------
        plot_spec
            Plot specification created by `ggplot()` function.
        x : int
            x-coordinate of plot origin in px.
        y : int
            y-coordinate of plot origin in px.
        width : int
            Width of plot in px.
        height : int
            Height of plot in px.

        """
        if width and not height:
            raise TypeError('height argument is required')
        if height and not width:
            raise TypeError('width argument is required')

        assert isinstance(x, numbers.Number), "'x' must be numeric"
        assert isinstance(y, numbers.Number), "'y' must be numeric"
        if width:
            assert isinstance(width, numbers.Number), "'width' must be numeric"
        if height:
            assert isinstance(height, numbers.Number), "'height' must be numeric"

        self.items.append(dict(feature_spec=plot_spec, x=x, y=y, width=width, height=height))

    def as_dict(self):
        d = super().as_dict()
        d['kind'] = self.kind

        def item_as_dict(item):
            result = dict((k, v) for k, v in item.items() if k != 'feature_spec')
            result['feature_spec'] = item['feature_spec'].as_dict()
            return result

        d['items'] = [item_as_dict(item) for item in self.items]
        return d

    def _repr_html_(self):
        """
        Special method discovered and invoked by IPython.display.display.
        """
        from ..frontend_context._configuration import _as_html
        return _as_html(self.as_dict())

    def show(self):
        """
        Draw all plots currently in this 'bunch'.
        """
        from ..frontend_context._configuration import _display_plot
        _display_plot(self)

