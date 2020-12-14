#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from enum import Enum
from typing import Union, Optional, List

from .geom import _geom
from .util import is_geo_data_regions, map_join_regions
from .._global_settings import MAPTILES_KIND, MAPTILES_URL, MAPTILES_THEME, MAPTILES_ATTRIBUTION, \
    GEOCODING_PROVIDER_URL, \
    TILES_RASTER_ZXY, TILES_VECTOR_LETS_PLOT, MAPTILES_MIN_ZOOM, MAPTILES_MAX_ZOOM
from .._global_settings import has_global_value, get_global_val

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
                 **other_args):
    """
    Display an interactive map.

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    show_legend: bool
        True - do not show legend for this layer.
    sampling : result of the call to the sampling_xxx() function.
        Value 'none' will disable sampling for this layer.
    tooltips : result of the call to the layer_tooltips() function.
        Specifies appearance, style and content.
    map : GeoDataFrame (supported shapes Point and MultiPoint) or Regions (implicitly invoke centroids())
        Data containing coordinates of points.
    map_join : str, pair, optional
        Pair of names used to join map coordinates with data.
        str is allowed only when used with Regions object - map key 'request' will be automatically added.
        first value in pair - column in data
        second value in pair - column in map
    symbol : string, optional
        The marker used for displaying the data. There are:
        - 'point' for circles of different size and color.
        - 'pie' for pie charts.
        - 'bar' for bar charts.
    location : array, optional
        Initial position of the map. If not set, displays the United States.
        There are [lon1, lat1, lon2, lat2,..., lonN, latN].
        - lon1, lon2,..., lonN are longitudes in degrees (positive in the Eastern hemisphere).
        - lat1, lat2,..., latN are latitudes in degrees (positive in the Northern hemisphere).
    zoom : integer, optional
        Zoom of the map in the range 1 - 15.
    projection : string, optional
        The map projection. There are:
        - 'epsg3857' for Mercator projection (default).
        - 'epsg4326' for Equirectangular projection.
        Note: 'projection' only works with vector map tiles (i.e. Lets-Plot map tiles)
    geodesic : True (default) or False, optional
        Enables geodesic type of all paths and segments
    tiles: string, optional
        Tiles provider, either as a string - URL for a standard raster ZXY tile provider with {z}, {x} and {y} wildcards
        (e.g. 'http://my.tile.com/{z}/{x}/{y}.png') or the result of a call to a maptiles_xxx functions
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3, stroke = 2 or shape = 21. They may also be parameters to
        the paired geom/stat.

    Returns
    -------
        geom object specification

    Note
    -----
    geom_livemap draws map, which can be moved and zoomed.
    geom_livemap understands the following aesthetics mappings:

    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill  : color of a geometry internals
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : radius for point, pie chart.
    - sym_x : value order for pie chart and bar chart.
    - sym_y : value specifying the sector size for pie chart and the heigth for bar chart.

    Examples
    --------
    >>> from lets_plot import *
    >>> p = ggplot() + geom_livemap()
    >>> p += ggtitle('Live Map')
    """
    # if within is not None:
    #     within = _prepare_parent(within)

    if location is not None:
        location = _prepare_location(location)

    tiles = _prepare_tiles(tiles)
    geocoding = _prepare_geocoding()

    _display_mode = 'display_mode'

    if _display_mode in other_args.keys():
        other_args.pop(_display_mode)

    if is_geo_data_regions(map):
        map = map.centroids()
        map_join = map_join_regions(map_join)

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
                 **other_args)


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
OPTIONS_GEOCODING_PROVIDER_URL = 'url'


class RegionKind(Enum):
    region_ids = 'region_ids'
    region_name = 'region_name'
    coordinates = 'coordinates'
    data_frame = 'data_frame'


def _prepare_geocoding():
    if has_global_value(GEOCODING_PROVIDER_URL):
        return {
            OPTIONS_GEOCODING_PROVIDER_URL: get_global_val(GEOCODING_PROVIDER_URL)
        }

    return {}


def _prepare_tiles(tiles: Optional[Union[str, dict]]) -> Optional[dict]:
    if isinstance(tiles, str):
        return {
            OPTIONS_MAPTILES_KIND: TILES_RASTER_ZXY,
            OPTIONS_MAPTILES_URL: tiles
        }

    if isinstance(tiles, dict):
        if tiles.get(MAPTILES_KIND, None) == TILES_RASTER_ZXY:
            return {
                OPTIONS_MAPTILES_KIND: TILES_RASTER_ZXY,
                OPTIONS_MAPTILES_URL: tiles[MAPTILES_URL],
                OPTIONS_MAPTILES_ATTRIBUTION: tiles[MAPTILES_ATTRIBUTION],
                OPTIONS_MAPTILES_MIN_ZOOM: tiles[MAPTILES_MIN_ZOOM],
                OPTIONS_MAPTILES_MAX_ZOOM: tiles[MAPTILES_MAX_ZOOM],
            }
        elif tiles.get(MAPTILES_KIND, None) == TILES_VECTOR_LETS_PLOT:
            return {
                OPTIONS_MAPTILES_KIND: TILES_VECTOR_LETS_PLOT,
                OPTIONS_MAPTILES_URL: tiles[MAPTILES_URL],
                OPTIONS_MAPTILES_THEME: tiles[MAPTILES_THEME],
                OPTIONS_MAPTILES_ATTRIBUTION: tiles[MAPTILES_ATTRIBUTION],
            }
        else:
            raise ValueError("Unsupported 'tiles' kind: " + tiles.get(MAPTILES_KIND, None))

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

    raise ValueError('Tile provider is not set.')


def _prepare_location(location: Union[str, List[float]]) -> Optional[dict]:
    if location is None:
        return None

    value = location
    # if isinstance(location, Regions):
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
