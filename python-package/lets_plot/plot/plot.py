#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import numbers

from lets_plot.plot.core import FeatureSpec
from lets_plot.plot.core import PlotSpec
from lets_plot.plot.util import as_annotated_data
from .._global_settings import has_global_value, get_global_val, MAX_WIDTH, MAX_HEIGHT

__all__ = ['ggplot', 'ggsize', 'GGBunch']


def ggplot(data=None, mapping=None):
    """
    Create a new ggplot plot

    Parameters
    ----------
    data : dictionary or pandas DataFrame, optional
        Default dataset to use for the plot. If not specified, must be supplied in each layer added to the plot.
    mapping : dictionary, optional
        Default list of aesthetic mappings to use for the plot. If not specified, must be supplied in each layer
        added to the plot.

    Returns
    -------
        plot specification

    Note
    -----
        ggplot() initializes a ggplot object. It can be used to declare the input data frame for a graphic and to
        specify the set of plot aesthetics intended to be common throughout all subsequent layers unless specifically
        overridden.
        ggplot() is typically used to construct a plot incrementally, using the + operator to add layers to the
        existing ggplot object. This is advantageous in that the code is explicit about which layers are added
        and the order in which they are added. For complex graphics with multiple layers, initialization with
        ggplot() is recommended.
        There are three common ways to invoke ggplot (see examples below):

            - ggplot(dat,aes(x,y)) :
                This method is recommended if all layers use the same data and the same set of aesthetics, although
                this method can also be used to add a layer using data from another data frame.
            - ggplot(dat) :
                This method specifies the default data frame to use for the plot, but no aesthetics are defined up front.
                This is useful when one data frame is used predominantly as layers are added, but the aesthetics may vary
                from one layer to another.
            - ggplot() :
                This method initializes a skeleton ggplot object which is fleshed out as layers are added. This method is
                useful when multiple data frames are used to produce different layers, as is often the case in complex
                graphics.

        ggplot() with no layers defined will produce an error message: "No layers in plot"

    Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> x = np.random.uniform(-1, 1, size=100)
    >>> y = np.random.normal(size=100)
    >>> dat = pd.DataFrame({'x': x, 'y': 25 * x ** 2 + y})
    >>> dat['class'] = ['0' if dat['x'][i] < 0 else '1' for i in range(100)]
    >>> # three ways to invoke ggplot, producing the same output:
    >>> # (1)
    >>> ggplot(dat, aes(x='x', y='y')) + layer()
    >>> # (2)
    >>> ggplot(dat) + layer()
    >>> # (3)
    >>> ggplot() + layer('point', 'identity', dat)

    """
    if isinstance(data, FeatureSpec):
        raise ValueError("Object {!r} is not acceptable as 'data' argument in ggplot()".format(data.kind))

    data, mapping, data_meta = as_annotated_data(data, mapping)

    return PlotSpec(data, mapping, scales=[], layers=[], **data_meta)


# noinspection SpellCheckingInspection
def ggsize(width, height):
    """
    Specifies overall size of plot

    Parameters
    ----------
    width  : number
        Width of plot in px.
    height : number
        Height of plot in px.

    Returns
    -------
        plot size specification

    Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> x = np.arange(100)
    >>> y = np.random.normal(size=100)
    >>> dat = pd.DataFrame({'x':x, 'y':y})
    >>> ggplot(dat) + geom_line(aes('x','y')) + ggsize(600, 120)
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
    Use method add_plot() to add plot to 'bunch'. Each plot can have arbitrary location and size.
    Use show() to draw all plots in bunch.

    Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from lets_plot import *
    >>> x = np.arange(100)
    >>> y = np.random.normal(size=100)
    >>> dat = pd.DataFrame({'x':x, 'y':y})
    >>> g = ggplot(dat, mapping=aes('x', 'y')) + ggsize(150, 150)
    >>> bunch = GGBunch()
    >>> bunch.add_plot(g + geom_point(), 0, 0)
    >>> bunch.add_plot(g + geom_histogram(bins=4), 150, 0)
    >>> bunch.add_plot(g + geom_line(), 0, 150, 200, 100)
    >>> bunch.show()
    """

    def __init__(self):
        super().__init__('ggbunch', None)
        self.items = []

    def add_plot(self, plot_spec: PlotSpec, x, y, width=None, height=None):
        """
        Adds plot to 'bunch'

        Parameters
        ----------
        plot_spec : value of ggplot()
            Plot specification created by ggplot() function
        x : number
            x-coordinate of plot origin in px.
        y : number
            y-coordinate of plot origin in px.
        width : number
            Width of plot in px.
        height :
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
        Special method discovered and invoked by IPython.display.display
        """
        from ..frontend_context._configuration import _as_html
        return _as_html(self.as_dict())

    def show(self):
        """
        Draw all plots currently in this 'bunch'
        """
        from ..frontend_context._configuration import _display_plot
        _display_plot(self)
