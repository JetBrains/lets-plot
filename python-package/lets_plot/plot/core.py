#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import json

__all__ = ['aes', 'layer']


def aes(x=None, y=None, **other):
    """
    Define aesthetic mappings

    Parameters
    ----------
    x, y, ... : List of name value pairs giving aesthetics to map to variables.
    The names for x and y aesthetics are typically omitted because they are so common; all other aesthetics must be named.

    Returns
    -------
        aesthetic mapping specification

    Note
    -----
        Generates aesthetic mappings that describe how variables in the data are projected to visual properties
        (aesthetics) of geometries. This function also standardizes aesthetic names by, for example, converting
        colour to color.

        Aesthetic mappings are not to be confused with aesthetic settings; the latter are used to set aesthetics to
        some constant values, e.g. make all points red in the plot. If one wants to make the color of a point
        depend on the value of a variable, he/she should project this variable to the color aesthetic via
        aesthetic mapping.

    Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> x = np.random.uniform(-1, 1, size=100)
    >>> y = np.random.normal(size=100)
    >>> dat = pd.DataFrame({'x': x, 'y': 25 * x ** 2 + y})
    >>> dat['class'] = ['0' if dat['x'][i] < 0 else '1' for i in range(100)]
    >>> ggplot(dat) + geom_point(aes(x='x', y='y', color='y', shape='class', fill='x', size='y')) +
    ... geom_point(shape=21, color='red', fill='green', size=5, stat='smooth')
    """

    return FeatureSpec('mapping', name=None, x=x, y=y, **other)


def layer(geom=None, stat=None, data=None, mapping=None, position=None, **kwargs):
    """
    Create a new layer

    Parameters
    ----------
    geom: string
        The geometric object to use to display the data
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    mapping : dictionary, optional
        Set of aesthetic mappings created by aes. Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    kwargs:
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.

    Returns
    -------
        geom object specification

    Note
    -----
        A layer is a combination of data, stat and geom with a potential position adjustment. Usually layers are created
        using geom_* or stat_* calls but they can be created directly using this function.

    Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> x = np.random.uniform(-1, 1, size=100)
    >>> y = np.random.normal(size=100)
    >>> dat = pd.DataFrame({'x': x, 'y': 25 * x ** 2 + y})
    >>> dat['class'] = ['0' if dat['x'][i] < 0 else '1' for i in range(100)]
    >>> ggplot(dat, aes(x='x', y='y', group='class', color='class')) + layer(geom='point', stat='smooth', position='identity')


    todo: other parameters:
    inherit.aes = TRUE, subset = NULL, show.legend = NA

    """

    return LayerSpec(**locals())


#
#  -----------------------------------
#  Specs
#

def _specs_to_dict(opts_raw):
    opts = {}
    for k, v in opts_raw.items():
        if isinstance(v, FeatureSpec):
            opts[k] = v.as_dict()
        elif isinstance(v, dict):
            opts[k] = _specs_to_dict(v)
        else:
            opts[k] = v

    return opts


class FeatureSpec():
    def __init__(self, kind, name, **kwargs):
        self.kind = kind
        self.__props = {}
        if name is not None:
            self.__props['name'] = name
        self.__props.update(**kwargs)

    def props(self):
        return self.__props

    def as_dict(self):
        return _specs_to_dict(self.props())

    def __str__(self):
        return json.dumps(self.as_dict(), indent=2)

    def __add__(self, other):
        if isinstance(other, DummySpec):
            # nothing
            return self

        """
            self + plot -> fail
            self + other_feature -> [self,other_feature]
        """
        if isinstance(other, PlotSpec):
            # pass and fail
            pass
        if isinstance(other, FeatureSpec):
            arr = FeatureSpecArray(self, other)
            return arr

        raise TypeError('unsupported operand type(s) for +: {} and {}'
                        .format(self.__class__, other.__class__))


class PlotSpec(FeatureSpec):
    @classmethod
    def duplicate(cls, other):
        dup = PlotSpec(data=None, mapping=None, scales=other.__scales, layers=other.__layers)
        dup.props().update(other.props())
        return dup

    def __init__(self, data, mapping, scales, layers, **kwargs):
        super().__init__('plot', name=None, data=data, mapping=mapping, **kwargs)
        self.__scales = list(scales)
        self.__layers = list(layers)

    def get_plot_shared_data(self):
        # used to evaluate 'completion'
        return self.props()['data']

    def has_layers(self) -> bool:
        return True if self.__layers else False

    def __add__(self, other):
        """
            plot + other_plot -> fail
            plot + layer -> plot[layers] += layer
            plot + geom ->  plot[layers] += new layer(geom)
            plot + scale -> plot[scales] += scale
            plot + [feature]  -> plot + each feature in []
        """
        if isinstance(other, PlotSpec):
            # pass and fail
            pass

        if isinstance(other, DummySpec):
            # nothing
            return self

        elif isinstance(other, FeatureSpec):
            plot = PlotSpec.duplicate(self)
            if other.kind == 'layer':
                plot.__layers.append(other)
                return plot

            if other.kind == 'scale':
                plot.__scales.append(other)
                return plot

            if isinstance(other, FeatureSpecArray):
                for spec in other.elements():
                    plot = plot.__add__(spec)
                return plot

            # add feature to properties
            plot.props()[other.kind] = other
            return plot

        return super().__add__(other)

    def as_dict(self):
        d = super().as_dict()
        d['kind'] = self.kind
        d['scales'] = [scale.as_dict() for scale in self.__scales]
        d['layers'] = [layer.as_dict() for layer in self.__layers]
        return d

    def __str__(self):
        result = ['plot']
        result.extend('{}: {}'.format(k, v)
                      for k, v in self.props().items())

        result.append('scales [{}]'.format(len(self.__scales)))
        for scale in self.__scales:
            result.append(str(scale))
            result.append('-' * 34)

        result.append('layers [{}]'.format(len(self.__layers)))
        for layer in self.__layers:
            result.append(str(layer))
            result.append('-' * 34)

        result.append('')  # for trailing \n
        return '\n'.join(result)

    def _repr_html_(self):
        """
        Special method discovered and invoked by IPython.display.display
        """
        from ..frontend_context._configuration import _as_html
        return _as_html(self.as_dict())

    def show(self):
        """
        Draw plot
        """
        from ..frontend_context._configuration import _display_plot
        _display_plot(self)


class LayerSpec(FeatureSpec):
    __own_features = ['geom', 'stat', 'mapping', 'position']

    @classmethod
    def duplicate(cls, other):
        return LayerSpec(**other.props())

    def __init__(self, **kwargs):
        super().__init__('layer', name=None, **kwargs)

    def get_plot_layer_data(self):
        # used to evaluate 'completion'
        return self.props()['data']

    def __add__(self, other):
        if isinstance(other, DummySpec):
            # nothing
            return self

        """
            layer + own_feature -> layer[feature] = feature
            layer + other -> default
        """
        if isinstance(other, FeatureSpec):
            if other.kind in LayerSpec.__own_features:
                l = LayerSpec.duplicate(self)
                l.props()[other.kind] = other
                return l

        return super().__add__(other)


class FeatureSpecArray(FeatureSpec):
    def __init__(self, *features):
        super().__init__('feature-list', name=None)
        self.__elements = list(features)

    def elements(self):
        return self.__elements

    def as_dict(self):
        elements = [{e.kind: e.as_dict()} for e in self.__elements]
        return {'feature-list': elements}

    def __add__(self, other):
        if isinstance(other, DummySpec):
            # nothing
            return self

        """
            array + other_feature -> [my_elements, other]
            array + other_array -> [my_elements, other_elements]
            layer + ? -> fail
        """
        if isinstance(other, FeatureSpec):
            if isinstance(other, FeatureSpecArray):
                return FeatureSpecArray(*self.__elements, *other.__elements)

            elif other.kind != 'plot':
                return FeatureSpecArray(*self.__elements, other)

        return super().__add__(other)


class DummySpec(FeatureSpec):
    def __init__(self):
        super().__init__('dummy', name=None)

    def as_dict(self):
        return {'dummy-feature': True}

    def __add__(self, other):
        return other


def _generate_data(size):
    """ For testing reasons only """
    # return FeatureSpec('dummy', name=None, data='x' * size)
    return PlotSpec(data='x' * size, mapping=None, scales=[], layers=[])
