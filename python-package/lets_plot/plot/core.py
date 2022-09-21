#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import json

__all__ = ['aes', 'layer']

from lets_plot._global_settings import get_global_bool, has_global_value, FRAGMENTS_ENABLED


def aes(x=None, y=None, **other):
    """
    Define aesthetic mappings.

    Parameters
    ----------
    x, y, ... :
        List of name value pairs giving aesthetics to map to variables.
        The names for x and y aesthetics are typically omitted because they are so common; all other aesthetics must be named.

    Returns
    -------
    `FeatureSpec`
        Aesthetic mapping specification.

    Notes
    -----
    Generates aesthetic mappings that describe how variables in the data are projected to visual properties
    (aesthetics) of geometries. This function also standardizes aesthetic names by, for example, converting
    colour to color.

    Aesthetic mappings are not to be confused with aesthetic settings; the latter are used to set aesthetics to
    some constant values, e.g. make all points red in the plot. If one wants to make the color of a point
    depend on the value of a variable, he/she should project this variable to the color aesthetic via
    aesthetic mapping.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10-11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        x = np.random.uniform(-1, 1, size=n)
        y = 25 * x ** 2 + np.random.normal(size=n)
        c = np.where(x < 0, '0', '1')
        ggplot({'x': x, 'y': y, 'c': c}) + \\
            geom_point(aes('x', 'y', color='y', shape='c', size='x')) + \\
            geom_smooth(aes(x='x', y='y'), deg=2, size=1)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 3-4

        from lets_plot import *
        LetsPlot.setup_html()
        ggplot() + geom_polygon(aes(x=[0, 1, 2], y=[2, 1, 4]), \\
                                color='black', alpha=.5, size=1)

    """

    return FeatureSpec('mapping', name=None, x=x, y=y, **other)


def layer(geom=None, stat=None, data=None, mapping=None, position=None, **kwargs):
    """
    Create a new layer.

    Parameters
    ----------
    geom : str
        The geometric object to use to display the data.
    stat : str, default='identity'
        The statistical transformation to use on the data for this layer, as a string.
        Supported transformations: 'identity' (leaves the data unchanged),
        'count' (counts number of points with same x-axis coordinate),
        'bin' (counts number of points with x-axis coordinate in the same bin),
        'smooth' (performs smoothing - linear default),
        'density' (computes and draws kernel density estimate).
    data : dict or `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    position : str or `FeatureSpec`
        Position adjustment, either as a string ('identity', 'stack', 'dodge', ...),
        or the result of a call to a position adjustment function.
    kwargs:
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3 or shape = 21. They may also be parameters to the
        paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Notes
    -----
    A layer is a combination of data, stat and geom with a potential position adjustment. Usually layers are created
    using geom_* or stat_* calls but they can be created directly using this function.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 8

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 50
        np.random.seed(42)
        x = np.random.uniform(-1, 1, size=n)
        y = 25 * x ** 2 + np.random.normal(size=n)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + layer(geom='point')

    """
    # todo: other parameters: inherit.aes = TRUE, subset = NULL, show.legend = NA

    return LayerSpec(**locals())


def _filter_none(original: dict) -> dict:
    return {k: v for k, v in original.items() if v is not None}


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

    return _filter_none(opts)


class FeatureSpec():
    """
    A base class of the plot objects.

    Do not use this class explicitly.

    Instead you should construct its objects with functions `ggplot()`, `geom_point()`,
    `position_dodge()`, `scale_x_continuous()` etc.
    """

    def __init__(self, kind, name, **kwargs):
        """Initialize self."""
        self.kind = kind
        self.__props = {}
        if name is not None:
            self.__props['name'] = name
        self.__props.update(**kwargs)

    def props(self):
        """
        Returns the dictionary of all properties of the object in their initial form.

        Returns
        -------
        dict
            Dictionary of properties.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 4

            from lets_plot import *
            LetsPlot.setup_html()
            p = ggplot({'x': [0], 'y': [0]}) + geom_point(aes('x', 'y'))
            p.props()

        """
        return self.__props

    def as_dict(self):
        """
        Returns the dictionary of all properties of the object with `as_dict()`
        applied recursively to all subproperties of `FeatureSpec` type.

        Returns
        -------
        dict
            Dictionary of properties.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 4

            from lets_plot import *
            LetsPlot.setup_html()
            p = ggplot({'x': [0], 'y': [0]}) + geom_point(aes('x', 'y'))
            p.as_dict()
        """
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
    """
    A class of the initial plot object.

    Do not use this class explicitly.

    Instead you should construct its objects with functions `ggplot()`,
    `corr_plot(...).points().build()` etc.
    """

    @classmethod
    def duplicate(cls, other):
        dup = PlotSpec(data=None, mapping=None,
                       scales=other.__scales,
                       layers=other.__layers,
                       metainfo_list=other.__metainfo_list,
                       is_livemap=other.__is_livemap)
        dup.props().update(other.props())
        return dup

    def __init__(self, data, mapping, scales, layers, metainfo_list=[], is_livemap=False, **kwargs):
        """Initialize self."""
        super().__init__('plot', name=None, data=data, mapping=mapping, **kwargs)
        self.__scales = list(scales)
        self.__layers = list(layers)
        self.__metainfo_list = list(metainfo_list)
        self.__is_livemap = is_livemap

    def get_plot_shared_data(self):
        """
        Extracts the data shared by all layers.

        Returns
        -------
        dict or `DataFrame`
            Object data.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 5

            from lets_plot import *
            LetsPlot.setup_html()
            p = ggplot({'x': [0], 'y': [0]}, aes('x', 'y'))
            p += geom_point(data={'x': [1], 'y': [1]})
            p.get_plot_shared_data()

        """
        # used to evaluate 'completion'
        return self.props()['data']

    def has_layers(self) -> bool:
        """
        Checks if the `PlotSpec` object has at least one layer.

        Returns
        -------
        bool
            True if object has layers.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 4, 6

            from lets_plot import *
            LetsPlot.setup_html()
            p = ggplot()
            print(p.has_layers())
            p += geom_point(x=0, y=0)
            print(p.has_layers())

        """
        return True if self.__layers else False

    def __add__(self, other):
        """
        Allows to add different specs to the `PlotSpec` object.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 7

            from lets_plot import *
            LetsPlot.setup_html()
            p = ggplot({'x': [0, 1, 2], 'y': [0, 1, 2]}, aes('x', 'y'))
            l = layer('point', mapping=aes(color='x'))
            s = scale_color_discrete()
            t = theme(axis_title='blank')
            p + l + s + t

        """
        """
            plot + other_plot -> fail
            plot + layer -> plot[layers] += layer
            plot + geom ->  plot[layers] += new layer(geom)
            plot + scale -> plot[scales] += scale
            plot + metainfo -> plot[metainfo_list] += metainfo
            plot + [feature]  -> plot + each feature in []
            plot + theme + theme -> merged theme
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
                if other.props()['geom'] == 'livemap':
                    plot.__is_livemap = True

                other.before_append(plot.__is_livemap)
                plot.__layers.append(other)
                return plot

            if other.kind == 'scale':
                plot.__scales.append(other)
                return plot

            if other.kind == 'theme':
                new_theme_options = {k: v for k, v in other.props().items() if v is not None}
                if 'name' in new_theme_options:
                    # pre-configured theme overrides existing theme all together.
                    plot.props()['theme'] = new_theme_options
                else:
                    # merge themes
                    old_theme_options = plot.props().get('theme', {})
                    plot.props()['theme'] = _theme_dicts_merge(old_theme_options, new_theme_options)

                return plot

            if other.kind == 'metainfo':
                plot.__metainfo_list.append(other)
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
        d['metainfo_list'] = [metainfo.as_dict() for metainfo in self.__metainfo_list]
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

        result.append('metainfo_list [{}]'.format(len(self.__metainfo_list)))
        for metainfo in self.__metainfo_list:
            result.append(str(metainfo))
            result.append('-' * 34)

        result.append('')  # for trailing \n
        return '\n'.join(result)

    def _repr_html_(self):
        # Special method discovered and invoked by IPython.display.display.
        from ..frontend_context._configuration import _as_html
        return _as_html(self.as_dict())

    def show(self):
        """
        Draws a plot.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 4

            from lets_plot import *
            LetsPlot.setup_html()
            p = ggplot() + geom_point(x=0, y=0)
            p.show()

        """
        from ..frontend_context._configuration import _display_plot
        _display_plot(self)


class LayerSpec(FeatureSpec):
    """
    A class of the plot layer object.

    Do not use this class explicitly.

    Instead you should construct its objects with functions `geom_point()`,
    `geom_contour()`, `geom_boxplot()`, `geom_text()` etc.
    """

    __own_features = ['geom', 'stat', 'mapping', 'position']

    @classmethod
    def duplicate(cls, other):
        # A shallow copy!
        return LayerSpec(**other.props())

    def __init__(self, **kwargs):
        super().__init__('layer', name=None, **kwargs)

    def before_append(self, is_livemap):
        from .util import normalize_map_join, is_geo_data_frame, auto_join_geo_names, geo_data_frame_to_wgs84, \
            get_geo_data_frame_meta
        from lets_plot.geo_data_internals.utils import is_geocoder

        name = self.props().get('geom', None)
        map_join = self.props().get('map_join', None)
        map = self.props().get('map', None)
        map_data_meta = None

        if map_join is None and map is None:
            return

        map_join = normalize_map_join(map_join)

        if is_geocoder(map):
            if is_livemap and get_global_bool(FRAGMENTS_ENABLED) if has_global_value(FRAGMENTS_ENABLED) else False:
                map = map.get_geocodes()
                map_join = auto_join_geo_names(map_join, map)
                map_data_meta = {'georeference': {}}
            else:
                # Fetch proper GeoDataFrame. Further processing is the same as if map was a GDF.
                if name in ['point', 'text', 'livemap']:
                    map = map.get_centroids()
                elif name in ['map', 'polygon']:
                    map = map.get_boundaries()
                elif name in ['rect']:
                    map = map.get_limits()
                else:
                    raise ValueError("Geocoding doesn't provide geometries for geom_{}".format(name))

        if is_geo_data_frame(map):
            map = geo_data_frame_to_wgs84(map)
            map_join = auto_join_geo_names(map_join, map)
            map_data_meta = get_geo_data_frame_meta(map)

        if map_join is not None:
            self.props()['map_join'] = map_join

        if map is not None:
            self.props()['map'] = map

        if map_data_meta is not None:
            self.props()['map_data_meta'] = map_data_meta

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


def _theme_dicts_merge(x, y):
    """
    Simple values in `y` override values in `x`.
    If values in `y` and `x` both are dictionaries, then they are merged.
    """
    overlapping_keys = x.keys() & y.keys()
    z = {k: {**x[k], **y[k]} for k in overlapping_keys if type(x[k]) is dict and type(y[k]) is dict}
    return {**x, **y, **z}
