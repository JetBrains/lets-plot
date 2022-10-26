#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from enum import Enum
from typing import Union, Optional, List

from lets_plot._global_settings import MAPTILES_KIND, MAPTILES_URL, MAPTILES_THEME, MAPTILES_ATTRIBUTION, \
    GEOCODING_PROVIDER_URL, GEOCODING_ROUTE, \
    TILES_RASTER_ZXY, TILES_VECTOR_LETS_PLOT, MAPTILES_MIN_ZOOM, MAPTILES_MAX_ZOOM, TILES_SOLID, \
    MAPTILES_SOLID_FILL_COLOR, TILES_CHESSBOARD
from lets_plot._global_settings import has_global_value, get_global_val
from .geom import _geom

try:
    import pandas
except ImportError:
    pandas = None

# from ..geo_data.livemap_helper import _prepare_location
# from ..geo_data.livemap_helper import _prepare_parent
# from ..geo_data.livemap_helper import _prepare_tiles

__all__ = ['geom_livemap']


def geom_livemap(mapping=None, *, data=None, show_legend=None, sampling=None, tooltips=None,
                 map=None, map_join=None,
                 symbol=None,
                 location=None,
                 zoom=None,
                 projection=None,
                 geodesic=None,
                 tiles=None,
                 show_coord_pick_tools=None,
                 data_size_zoomin=None,
                 const_size_zoomin=None,
                 ontop=None,
                 **other_args):
    """
    Display an interactive map.

    Parameters
    ----------
    mapping : `FeatureSpec`
        Set of aesthetic mappings created by `aes()` function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dict or `DataFrame` or `polars.DataFrame` or `GeoDataFrame`
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    show_legend : bool, default=True
        False - do not show legend for this layer.
    sampling : `FeatureSpec`
        Result of the call to the `sampling_xxx()` function.
        Value None (or 'none') will disable sampling for this layer.
    tooltips : `layer_tooltips`
        Result of the call to the `layer_tooltips()` function.
        Specifies appearance, style and content.
    map : `GeoDataFrame` or `Geocoder`
        Data containing coordinates of points.
    map_join : str or list
        Keys used to join map coordinates with data.
        First value in pair - column/columns in `data`.
        Second value in pair - column/columns in `map`.
    symbol : str, default=None
        The marker used for displaying the data. There are:
        'point' for circles of different size and color;
        None for map without markers.
    location : list
        Initial position of the map. If not set, displays the United States.
        There are [lon1, lat1, lon2, lat2,..., lonN, latN]:
        lon1, lon2,..., lonN are longitudes in degrees (positive in the Eastern hemisphere);
        lat1, lat2,..., latN are latitudes in degrees (positive in the Northern hemisphere).
    zoom : int
        Zoom of the map in the range 1 - 15.
    projection : str, default='epsg3857'
        The map projection. There are: 'epsg3857' for Mercator projection;
        'epsg4326' for Equirectangular projection. `projection` only works
        with vector map tiles (i.e. Lets-Plot map tiles).
    geodesic : bool, default=True
        Enables geodesic type of all paths and segments.
    tiles : str
        Tiles provider, either as a string - URL for a standard raster ZXY tile provider
        with {z}, {x} and {y} wildcards (e.g. 'http://my.tile.com/{z}/{x}/{y}.png')
        or the result of a call to a `maptiles_xxx()` functions.
    show_coord_pick_tools : bool, defafult=False
        Show buttons "copy location" and "draw geometry"
    data_size_zoomin : int, defafult=0
        Controls how zooming-in of the map widget increases size of geometry objects (circles, lines etc.) on map
        when the size is set by means of mapping between the data and the `size` aesthetic.
        `0` - size never increases;
        `-1` - size will be increasing without limits;
        `n` - a number of zooming-in steps (counting from the initial state of the map widget)
        when size of objects will be increasing. Farther zooming will no longer affect the size.
    const_size_zoomin : int, defafult=-1
        Controls how zooming-in of the map widget increases size of geometry objects (circles, lines etc.) on map
        when the size is not linked to a data (i.e. constant size).
        `0` - size never increases;
        `-1` - size will be increasing without limits;
        `n` - a number of zooming-in steps (counting from the initial state of the map widget)
        when size of objects will be increasing. Farther zooming will no longer affect the size.
    ontop : bool, default=False
        Whether geometry objects created by aesthetics mappings specified in directly in `geom_livemap()`
        appear below (False) or ontop (True) of  objects of other plot layers.
    other_args
        Other arguments passed on to the layer.
        These are often aesthetics settings used to set an aesthetic to a fixed value,
        like color='red', fill='blue', size=3 or shape=21.
        They may also be parameters to the paired geom/stat.

    Returns
    -------
    `LayerSpec`
        Geom object specification.

    Notes
    -----
    `geom_livemap()` draws a map, which can be dragged and zoomed.

    `geom_livemap()` understands the following aesthetics mappings:

    - x : x-axis value, i.e. longitude in this context.
    - y : y-axis value, i.e. latitude in this context.
    - alpha : transparency level of the point. Accepts values between 0 and 1.
    - color (colour) : color of the geometry lines. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill : color of a geometry internals. Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : radius for point, pie chart.
    - sym_x : value order for pie chart and bar chart.
    - sym_y : value specifying the sector size for pie chart and the heigth for bar chart.

    |

    The `data` and `map` parameters of `GeoDataFrame` type support shapes `Point` and `MultiPoint`.

    The `map` parameter of `Geocoder` type implicitly invoke `centroids()` function.

    |

    The conventions for the values of `map_join` parameter are as follows.

    - Joining data and `GeoDataFrame` object

      Data has a column named 'State_name' and `GeoDataFrame` has a matching column named 'state':

      - map_join=['State_Name', 'state']
      - map_join=[['State_Name'], ['state']]

    - Joining data and `Geocoder` object

      Data has a column named 'State_name'. The matching key in `Geocoder` is always 'state' (providing it is a state-level geocoder) and can be omitted:

      - map_join='State_Name'
      - map_join=['State_Name']

    - Joining data by composite key

      Joining by composite key works like in examples above, but instead of using a string for a simple key you need to use an array of strings for a composite key. The names in the composite key must be in the same order as in the US street addresses convention: 'city', 'county', 'state', 'country'. For example, the data has columns 'State_name' and 'County_name'. Joining with a 2-keys county level `Geocoder` object (the `Geocoder` keys 'county' and 'state' are omitted in this case):

      - map_join=['County_name', 'State_Name']

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 3

        from lets_plot import *
        LetsPlot.setup_html()
        ggplot() + geom_livemap()

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9-12

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'city': ['New York City', 'Singapore'],
            'lon': [-73.7997, 104.0012],
            'lat': [40.6408, 1.3256],
        }
        ggplot(data, aes(x='lon', y='lat')) + \\
            geom_livemap(geodesic=False, projection='epsg4326', \\
                         symbol='point', color='white', \\
                         tiles=maptiles_lets_plot(theme='dark'), \\
                         tooltips=layer_tooltips().line('@city')) + \\
            geom_path(color='white') + \\
            ggtitle('SQ23 - the longest scheduled airline flight '
                    'by great circle distance since 2020')

    """
    if location is not None:
        location = _prepare_location(location)

    tiles = _prepare_tiles(tiles)
    geocoding = _prepare_geocoding()

    _display_mode = 'display_mode'

    if _display_mode in other_args.keys():
        other_args.pop(_display_mode)

    if mapping is not None and symbol is None:
        raise ValueError('The `symbol` parameter is required for `geom_livemap()` with mappings.')

    if map is not None and symbol is None:
        raise ValueError('The `symbol` parameter is required for `geom_livemap()` with `map` parameter.')

    return _geom('livemap',
                 mapping=mapping,
                 data=data,
                 stat=None,
                 position=None,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 map=map, map_join=map_join,
                 display_mode=symbol,
                 location=location,
                 zoom=zoom,
                 projection=projection,
                 geodesic=geodesic,
                 tiles=tiles,
                 geocoding=geocoding,
                 show_coord_pick_tools=show_coord_pick_tools,
                 data_size_zoomin=data_size_zoomin,
                 const_size_zoomin=const_size_zoomin,
                 ontop=ontop,
                 **other_args
    )


LOCATION_COORDINATE_COLUMNS = {'lon', 'lat'}
LOCATION_RECTANGLE_COLUMNS = {'lonmin', 'latmin', 'lonmax', 'latmax'}
LOCATION_LIST_ERROR_MESSAGE = "Expected: location = [double lon1, double lat1, ... , double lonN, double latN]"
LOCATION_DATAFRAME_ERROR_MESSAGE = "Expected: location = DataFrame with [{}] or [{}] columns" \
    .format(', '.join(LOCATION_COORDINATE_COLUMNS), ', '.join(LOCATION_RECTANGLE_COLUMNS))

OPTIONS_MAPTILES_KIND = 'kind'
OPTIONS_MAPTILES_URL = 'url'
OPTIONS_MAPTILES_THEME = 'theme'
OPTIONS_MAPTILES_ATTRIBUTION = 'attribution'
OPTIONS_MAPTILES_MIN_ZOOM = 'min_zoom'
OPTIONS_MAPTILES_MAX_ZOOM = 'max_zoom'
OPTIONS_MAPTILES_FILL_COLOR = 'fill_color'
OPTIONS_GEOCODING_PROVIDER_URL = 'url'


class RegionKind(Enum):
    region_ids = 'region_ids'
    region_name = 'region_name'
    coordinates = 'coordinates'
    data_frame = 'data_frame'


def _prepare_geocoding():
    if has_global_value(GEOCODING_PROVIDER_URL):
        return {
            OPTIONS_GEOCODING_PROVIDER_URL: get_global_val(GEOCODING_PROVIDER_URL) + GEOCODING_ROUTE
        }

    return {}


def _prepare_tiles(tiles: Optional[Union[str, dict]]) -> Optional[dict]:
    if isinstance(tiles, str):
        return {
            OPTIONS_MAPTILES_KIND: TILES_RASTER_ZXY,
            OPTIONS_MAPTILES_URL: tiles
        }

    if isinstance(tiles, dict):
        if tiles.get(MAPTILES_KIND) == TILES_RASTER_ZXY:
            return {
                OPTIONS_MAPTILES_KIND: TILES_RASTER_ZXY,
                OPTIONS_MAPTILES_URL: tiles[MAPTILES_URL],
                OPTIONS_MAPTILES_ATTRIBUTION: tiles[MAPTILES_ATTRIBUTION],
                OPTIONS_MAPTILES_MIN_ZOOM: tiles[MAPTILES_MIN_ZOOM],
                OPTIONS_MAPTILES_MAX_ZOOM: tiles[MAPTILES_MAX_ZOOM],
            }
        elif tiles.get(MAPTILES_KIND) == TILES_VECTOR_LETS_PLOT:
            return {
                OPTIONS_MAPTILES_KIND: TILES_VECTOR_LETS_PLOT,
                OPTIONS_MAPTILES_URL: tiles[MAPTILES_URL],
                OPTIONS_MAPTILES_THEME: tiles[MAPTILES_THEME],
                OPTIONS_MAPTILES_ATTRIBUTION: tiles[MAPTILES_ATTRIBUTION],
            }
        elif tiles.get(MAPTILES_KIND) == TILES_SOLID:
            return {
                OPTIONS_MAPTILES_KIND: TILES_SOLID,
                OPTIONS_MAPTILES_FILL_COLOR: tiles[MAPTILES_SOLID_FILL_COLOR]
            }
        elif tiles.get(MAPTILES_KIND) == TILES_CHESSBOARD:
            return {
                OPTIONS_MAPTILES_KIND: TILES_CHESSBOARD
            }
        else:
            raise ValueError("Unsupported 'tiles' kind: " + tiles.get(MAPTILES_KIND))

    if tiles is not None:
        raise ValueError("Unsupported 'tiles' parameter type: " + type(tiles))

    # tiles are not set for this livemap - try to get global tiles config
    if has_global_value(MAPTILES_KIND):
        if not has_global_value(MAPTILES_URL):
            raise ValueError('URL for tiles service is not set')

        if get_global_val(MAPTILES_KIND) == TILES_RASTER_ZXY:
            return {
                OPTIONS_MAPTILES_KIND: TILES_RASTER_ZXY,
                OPTIONS_MAPTILES_URL: get_global_val(MAPTILES_URL),
                OPTIONS_MAPTILES_ATTRIBUTION: get_global_val(MAPTILES_ATTRIBUTION) if has_global_value(
                    MAPTILES_ATTRIBUTION) else None,
                OPTIONS_MAPTILES_MIN_ZOOM: get_global_val(MAPTILES_MIN_ZOOM) if has_global_value(
                    MAPTILES_MIN_ZOOM) else None,
                OPTIONS_MAPTILES_MAX_ZOOM: get_global_val(MAPTILES_MAX_ZOOM) if has_global_value(
                    MAPTILES_MAX_ZOOM) else None,
            }

        if get_global_val(MAPTILES_KIND) == TILES_VECTOR_LETS_PLOT:
            return {
                OPTIONS_MAPTILES_KIND: TILES_VECTOR_LETS_PLOT,
                OPTIONS_MAPTILES_URL: get_global_val(MAPTILES_URL),
                OPTIONS_MAPTILES_THEME: get_global_val(MAPTILES_THEME) if has_global_value(MAPTILES_THEME) else None,
                OPTIONS_MAPTILES_ATTRIBUTION: get_global_val(MAPTILES_ATTRIBUTION) if has_global_value(
                    MAPTILES_ATTRIBUTION) else None,
            }

        if get_global_val(MAPTILES_KIND) == TILES_SOLID:
            return {
                OPTIONS_MAPTILES_KIND: TILES_SOLID,
                OPTIONS_MAPTILES_FILL_COLOR: get_global_val(MAPTILES_SOLID_FILL_COLOR),
            }

    raise ValueError('Tile provider is not set.')


def _prepare_location(location: Union[str, List[float]]) -> Optional[dict]:
    if location is None:
        return None

    value = location
    # if isinstance(location, Geocoder):
    #     kind = RegionKind.region_ids
    #     value = location.unique_ids()

    if isinstance(location, str):
        kind = RegionKind.region_name

    elif isinstance(location, list):
        if len(location) == 0 or len(location) % 2 != 0:
            raise ValueError(LOCATION_LIST_ERROR_MESSAGE)
        kind = RegionKind.coordinates

    elif pandas and isinstance(location, pandas.DataFrame):
        if not LOCATION_COORDINATE_COLUMNS.issubset(location.columns) and not LOCATION_RECTANGLE_COLUMNS.issubset(
                location.columns):
            raise ValueError(LOCATION_DATAFRAME_ERROR_MESSAGE)
        kind = RegionKind.data_frame

    else:
        raise ValueError('Wrong location type: ' + location.__str__())

    return {'type': kind.value, 'data': value}
