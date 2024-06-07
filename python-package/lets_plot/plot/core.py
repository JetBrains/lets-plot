#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import io
import json
import os
from typing import Union

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
    Generate aesthetic mappings that describe how variables in the data are projected to visual properties
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
        'count' (count number of points with same x-axis coordinate),
        'bin' (count number of points with x-axis coordinate in the same bin),
        'smooth' (perform smoothing - linear default),
        'density' (compute and draw kernel density estimate).
    data : dict or Pandas or Polars `DataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    position : str or `FeatureSpec`
        Position adjustment.
        Either a position adjustment name: 'dodge', 'dodgev', 'jitter', 'nudge', 'jitterdodge', 'fill',
        'stack' or 'identity', or the result of calling a position adjustment function (e.g., `position_dodge()` etc.).
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
        Return the dictionary of all properties of the object in their initial form.

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
        Return the dictionary of all properties of the object with `as_dict()`
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

    Instead, you should construct its objects with functions `ggplot()`,
    `corr_plot(...).points().build()` etc.
    """

    @classmethod
    def duplicate(cls, other):
        dup = PlotSpec(data=None, mapping=None,
                       scales=other.__scales,
                       layers=other.__layers,
                       metainfo_list=other.__metainfo_list,
                       is_livemap=other.__is_livemap,
                       crs_initialized=other.__crs_initialized,
                       crs=other.__crs,
                       )
        dup.props().update(other.props())
        return dup

    def __init__(self, data, mapping, scales, layers, metainfo_list=[], is_livemap=False, crs_initialized=False,
                 crs=None, **kwargs):
        """Initialize self."""
        super().__init__('plot', name=None, data=data, mapping=mapping, **kwargs)
        self.__scales = list(scales)
        self.__layers = list(layers)
        self.__metainfo_list = list(metainfo_list)
        self.__is_livemap = is_livemap
        self.__crs_initialized = crs_initialized
        self.__crs = crs

    def get_plot_shared_data(self):
        """
        Extract the data shared by all layers.

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
        Check if the `PlotSpec` object has at least one layer.

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
        Allow to add different specs to the `PlotSpec` object.

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

                from lets_plot.plot.util import is_geo_data_frame  # local import to break circular reference
                if is_geo_data_frame(other.props().get('data')) \
                        or is_geo_data_frame(other.props().get('map')):
                    if plot.__crs_initialized:
                        if plot.__crs != other.props().get('use_crs'):
                            raise ValueError(
                                'All geoms with map parameter should either use same `use_crs` or not use it at all')
                    else:
                        plot.__crs_initialized = True
                        plot.__crs = other.props().get('use_crs')

                if plot.__is_livemap and plot.__crs is not None:
                    raise ValueError("livemap doesn't support `use_crs`")

                other.before_append(plot.__is_livemap)
                plot.__layers.append(other)
                return plot

            if other.kind == 'scale':
                plot.__scales.append(other)
                return plot

            if other.kind == 'theme':
                new_theme_options = {k: v for k, v in other.props().items() if v is not None}
                if 'name' in new_theme_options:
                    # keep the previously specified flavor
                    if plot.props().get('theme', {}).get('flavor', None) is not None:
                        new_theme_options.update({'flavor': plot.props()['theme']['flavor']})

                    # pre-configured theme overrides existing theme altogether.
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

            if other.kind == 'guides':
                existing_guides_options = plot.props().get('guides', {})
                plot.props()['guides'] = _merge_dicts_recursively(existing_guides_options, other.as_dict())
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
        Draw a plot.

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

    def to_svg(self, path=None) -> str:
        """
        Export the plot in SVG format.

        Parameters
        ----------
        self : `PlotSpec`
            Plot specification to export.
        path : str, file-like object, default=None
            Сan be either a string specifying a file path or a file-like object.
            If a string is provided, the result will be exported to the file at that path.
            If a file-like object is provided, the result will be exported to that object.
            If None is provided, the result will be returned as a string.

        Returns
        -------
        str
            Absolute pathname of created file,
            SVG content as a string or None if a file-like object is provided.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 9

            import numpy as np
            import io
            from lets_plot import *
            from IPython import display
            LetsPlot.setup_html()
            x = np.random.randint(10, size=100)
            p = ggplot({'x': x}, aes(x='x')) + geom_bar()
            file_like = io.BytesIO()
            p.to_svg(file_like)
            display.SVG(file_like.getvalue())
        """
        return _to_svg(self, path)

    def to_html(self, path=None, iframe: bool = None) -> str:
        """
        Export the plot in HTML format.

        Parameters
        ----------
        self : `PlotSpec`
            Plot specification to export.
        path : str, file-like object, default=None
            Сan be either a string specifying a file path or a file-like object.
            If a string is provided, the result will be exported to the file at that path.
            If a file-like object is provided, the result will be exported to that object.
            If None is provided, the result will be returned as a string.
        iframe : bool, default=False
            Whether to wrap HTML page into a iFrame.

        Returns
        -------
        str
            Absolute pathname of created file,
            HTML content as a string or None if a file-like object is provided.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 8

            import numpy as np
            import io
            from lets_plot import *
            LetsPlot.setup_html()
            x = np.random.randint(10, size=100)
            p = ggplot({'x': x}, aes(x='x')) + geom_bar()
            file_like = io.BytesIO()
            p.to_html(file_like)
        """
        return _to_html(self, path, iframe)

    def to_png(self, path, scale: float = None, w=None, h=None, unit=None, dpi=None) -> str:
        """
        Export a plot to a file or to a file-like object in PNG format.

        Parameters
        ----------
        self : `PlotSpec`
            Plot specification to export.
        path : str, file-like object
            Сan be either a string specifying a file path or a file-like object.
            If a string is provided, the result will be exported to the file at that path.
            If a file-like object is provided, the result will be exported to that object.
        scale : float
            Scaling factor for raster output. Default value is 2.0.
        w : float, default=None
            Width of the output image in units.
            Only applicable when exporting to PNG or PDF.
        h : float, default=None
            Height of the output image in units.
            Only applicable when exporting to PNG or PDF.
        unit : {'in', 'cm', 'mm'}, default=None
            Unit of the output image. One of: 'in', 'cm', 'mm'.
            Only applicable when exporting to PNG or PDF.
        dpi : int, default=None
            Resolution in dots per inch.
            Only applicable when exporting to PNG or PDF.

        Returns
        -------
        str
            Absolute pathname of created file or None if a file-like object is provided.

        Notes
        -----
        Export to PNG file uses the CairoSVG library.
        CairoSVG is free and distributed under the LGPL-3.0 license.
        For more details visit: https://cairosvg.org/documentation/

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 9

            import numpy as np
            import io
            from lets_plot import *
            from IPython import display
            LetsPlot.setup_html()
            x = np.random.randint(10, size=100)
            p = ggplot({'x': x}, aes(x='x')) + geom_bar()
            file_like = io.BytesIO()
            p.to_png(file_like)
            display.Image(file_like.getvalue())
        """
        return _export_as_raster(self, path, scale, 'png', w=w, h=h, unit=unit, dpi=dpi)

    def to_pdf(self, path, scale: float = None, w=None, h=None, unit=None, dpi=None) -> str:
        """
        Export a plot to a file or to a file-like object in PDF format.

        Parameters
        ----------
        self : `PlotSpec`
            Plot specification to export.
        path : str, file-like object
            Сan be either a string specifying a file path or a file-like object.
            If a string is provided, the result will be exported to the file at that path.
            If a file-like object is provided, the result will be exported to that object.
        scale : float
            Scaling factor for raster output. Default value is 2.0.
        w : float, default=None
            Width of the output image in units.
            Only applicable when exporting to PNG or PDF.
        h : float, default=None
            Height of the output image in units.
            Only applicable when exporting to PNG or PDF.
        unit : {'in', 'cm', 'mm'}, default=None
            Unit of the output image. One of: 'in', 'cm', 'mm'.
            Only applicable when exporting to PNG or PDF.
        dpi : int, default=None
            Resolution in dots per inch.
            Only applicable when exporting to PNG or PDF.

        Returns
        -------
        str
            Absolute pathname of created file or None if a file-like object is provided.

        Notes
        -----
        Export to PDF file uses the CairoSVG library.
        CairoSVG is free and distributed under the LGPL-3.0 license.
        For more details visit: https://cairosvg.org/documentation/

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 13

            import numpy as np
            import io
            import os
            from lets_plot import *
            from IPython import display
            LetsPlot.setup_html()
            n = 60
            np.random.seed(42)
            x = np.random.choice(list('abcde'), size=n)
            y = np.random.normal(size=n)
            p = ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + geom_jitter()
            file_like = io.BytesIO()
            p.to_pdf(file_like)
        """
        return _export_as_raster(self, path, scale, 'pdf', w=w, h=h, unit=unit, dpi=dpi)


class LayerSpec(FeatureSpec):
    """
    A class of the plot layer object.

    Do not use this class explicitly.

    Instead, you should construct its objects with functions `geom_point()`,
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
        from .util import normalize_map_join, is_geo_data_frame, auto_join_geo_names, geo_data_frame_to_crs, \
            get_geo_data_frame_meta
        from lets_plot.geo_data_internals.utils import is_geocoder

        name = self.props()['geom']
        map_join = self.props().get('map_join')
        map = self.props().get('map')
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
                if name in ['point', 'pie', 'text', 'livemap']:
                    map = map.get_centroids()
                elif name in ['map', 'polygon']:
                    map = map.get_boundaries()
                elif name in ['rect']:
                    map = map.get_limits()
                else:
                    raise ValueError("Geocoding doesn't provide geometries for geom_{}".format(name))

        if is_geo_data_frame(map):
            # map = geo_data_frame_to_crs(map, self.props().get('use_crs'))
            use_crs = self.props().get('use_crs')
            if use_crs != "provided":
                map = geo_data_frame_to_crs(map, use_crs)
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
        self.__elements = []
        self._flatten(list(features), self.__elements)

    def __len__(self):
        return len(self.__elements)

    def __iter__(self):
        return self.__elements.__iter__()

    def __getitem__(self, item):
        return self.__elements[item]

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

    def _flatten(self, features, out):
        for feature in features:
            if isinstance(feature, FeatureSpecArray):
                self._flatten(feature.elements(), out)
            else:
                out.append(feature)


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


def _merge_dicts_recursively(d1, d2):
    merged = d1.copy()
    for key, value in d2.items():
        if isinstance(value, dict) and isinstance(merged.get(key), dict):
            merged[key] = _merge_dicts_recursively(merged[key], value)
        else:
            merged[key] = value
    return merged


def _theme_dicts_merge(x, y):
    """
    Simple values in `y` override values in `x`.
    If values in `y` and `x` both are dictionaries, then they are merged.
    """
    overlapping_keys = x.keys() & y.keys()
    z = {k: {**x[k], **y[k]} for k in overlapping_keys if type(x[k]) is dict and type(y[k]) is dict}
    return {**x, **y, **z}


def _to_svg(spec, path) -> Union[str, None]:
    from .. import _kbridge as kbr

    svg = kbr._generate_svg(spec.as_dict())
    if path is None:
        return svg
    elif isinstance(path, str):
        abspath = _makedirs(path)
        with io.open(abspath, mode="w", encoding="utf-8") as f:
            f.write(svg)
            return abspath
    else:
        path.write(svg.encode())
        return None


def _to_html(spec, path, iframe: bool) -> Union[str, None]:
    if iframe is None:
        iframe = False

    from .. import _kbridge as kbr
    html_page = kbr._generate_static_html_page(spec.as_dict(), iframe)

    if path is None:
        return html_page
    elif isinstance(path, str):
        abspath = _makedirs(path)
        with io.open(abspath, mode="w", encoding="utf-8") as f:
            f.write(html_page)
            return abspath
    else:
        path.write(html_page.encode())
        return None


def _export_as_raster(spec, path, scale: float, export_format: str, w=None, h=None, unit=None, dpi=None) -> Union[
    str, None]:
    try:
        import cairosvg
    except ImportError:
        import sys
        print("\n"
              "To export Lets-Plot figure to a PNG or PDF file please install CairoSVG library"
              "to your Python environment.\n"
              "CairoSVG is free and distributed under the LGPL-3.0 license.\n"
              "For more details visit: https://cairosvg.org/documentation/\n", file=sys.stderr)
        return None

    if export_format.lower() == 'png':
        export_function = cairosvg.svg2png
    elif export_format.lower() == 'pdf':
        export_function = cairosvg.svg2pdf
    else:
        raise ValueError("Unknown export format: {}".format(export_format))

    from .. import _kbridge
    # Use SVG image-rendering style as Cairo doesn't support CSS image-rendering style,
    svg = _kbridge._generate_svg(spec.as_dict(), use_css_pixelated_image_rendering=False)

    if isinstance(path, str):
        abspath = _makedirs(path)
        result = abspath
    else:
        result = None  # file-like object is provided. No path to return.

    if any(it is not None for it in [w, h, unit, dpi]):
        if w is None or h is None or unit is None or dpi is None:
            raise ValueError("w, h, unit and dpi must be specified")

        w, h = _to_inches(w, unit) * dpi, _to_inches(h, unit) * dpi
        export_function(bytestring=svg, write_to=path, dpi=dpi, output_width=w, output_height=h)
    else:
        scale = scale if scale is not None else 2.0
        export_function(bytestring=svg, write_to=path, scale=scale)

    return result


def _makedirs(path: str) -> str:
    """Return absolute path to a file after creating all directories in the path."""
    abspath = os.path.abspath(path)
    dirname = os.path.dirname(abspath)
    if dirname and not os.path.exists(dirname):
        os.makedirs(dirname)
    return abspath


def _to_inches(size, size_unit):
    if size_unit is None:
        raise ValueError("Unit must be specified")

    if size_unit == 'in':
        inches = size
    elif size_unit == 'cm':
        inches = size / 2.54
    elif size_unit == 'mm':
        inches = size / 25.4
    else:
        raise ValueError("Unknown unit: {}. Expected one of: 'in', 'cm', 'mm'".format(size_unit))

    return inches
