#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from enum import Enum
from typing import Union, Optional, List

from .geom import _geom
from .._global_settings import has_global_value, get_global_val
from ..settings_utils import MAPTILES_KIND, MAPTILES_URL, MAPTILES_THEME, GEOCODING_PROVIDER_URL, _RASTER_ZXY, _VECTOR_LETS_PLOT, maptiles_zxy

try:
    import pandas
except ImportError:
    pandas = None

# from ..geo_data.livemap_helper import _prepare_location
# from ..geo_data.livemap_helper import _prepare_parent
# from ..geo_data.livemap_helper import _prepare_tiles

__all__ = ['geom_livemap']


def geom_livemap(mapping=None, data=None, symbol=None, show_legend=None, sampling=None,
                 location=None, zoom=None, projection=None, geodesic=None, tiles=None,
                 **other_args):
    """
    Display a live map.
    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
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
    tiles: string, optional
        Tiles provider, either as a string - URL for a standard raster ZXY tile provider with {z}, {x} and {y} wildcards
        (e.g. 'http://my.tile.com/{z}/{x}/{y}.png') or the result of a call to a maptiles_xxx functions
    projection : string, optional
        The map projection. There are:
        - 'epsg3857' for Mercator projection (default).
        - 'epsg4326' for Equirectangular projection.
    geodesic : True (default) or False, optional
        Enables geodesic type of all paths and segments
    other_args :
        Other arguments passed on to layer. These are often aesthetics settings, used to set an aesthetic to a fixed
        value, like color = "red", fill = "blue", size = 3, stroke = 2 or shape = 21. They may also be parameters to
        the paired geom/stat.
    Returns
    -------
        geom object specification
    Notes
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

    return _geom('livemap', mapping, data, stat=None, position=None, show_legend=show_legend, sampling=sampling,
                 display_mode=symbol, location=location, zoom=zoom,
                 projection=projection, geodesic=geodesic, tiles=tiles, geocoding=geocoding,
                 **other_args)


LOCATION_COORDINATE_COLUMNS = {'lon', 'lat'}
LOCATION_RECTANGLE_COLUMNS = {'lonmin', 'latmin', 'lonmax', 'latmax'}
LOCATION_LIST_ERROR_MESSAGE = "Expected: location = [double lon1, double lat1, ... , double lonN, double latN]"
LOCATION_DATAFRAME_ERROR_MESSAGE = "Expected: location = DataFrame with [{}] or [{}] columns" \
    .format(', '.join(LOCATION_COORDINATE_COLUMNS), ', '.join(LOCATION_RECTANGLE_COLUMNS))


OPTIONS_MAPTILES_KIND = 'kind'
OPTIONS_MAPTILES_URL = 'url'
OPTIONS_MAPTILES_THEME = 'theme'

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


def _prepare_tiles(tiles: Union[str, dict]) -> Optional[dict]:
    if isinstance(tiles, str):
        return {
            OPTIONS_MAPTILES_KIND: _RASTER_ZXY,
            OPTIONS_MAPTILES_URL: tiles
        }

    if isinstance(tiles, dict):
        if tiles.get(MAPTILES_KIND, None) == _RASTER_ZXY:
            return {
                OPTIONS_MAPTILES_KIND: _RASTER_ZXY,
                OPTIONS_MAPTILES_URL: tiles.get(MAPTILES_URL, None)
            }
        elif tiles.get(MAPTILES_KIND, None) == _VECTOR_LETS_PLOT:
            return {
                OPTIONS_MAPTILES_KIND: _VECTOR_LETS_PLOT,
                OPTIONS_MAPTILES_URL: tiles.get(MAPTILES_URL, None),
                OPTIONS_MAPTILES_THEME: tiles.get(MAPTILES_THEME, None),
            }
        else:
            raise ValueError("Unsupported 'tiles' kind: " + tiles.get(MAPTILES_KIND, None))

    if tiles is not None:
        raise ValueError("Unsupported 'tiles' parameter type: " + type(tiles))

    if has_global_value(MAPTILES_KIND):
        if get_global_val(MAPTILES_KIND) == _RASTER_ZXY:
            return {
                OPTIONS_MAPTILES_KIND: _RASTER_ZXY,
                OPTIONS_MAPTILES_URL: get_global_val(MAPTILES_URL)
            }

        if get_global_val(MAPTILES_KIND) == _VECTOR_LETS_PLOT:
            return {
                OPTIONS_MAPTILES_KIND: _VECTOR_LETS_PLOT,
                OPTIONS_MAPTILES_URL: get_global_val(MAPTILES_URL) if has_global_value(MAPTILES_URL) else None,
                OPTIONS_MAPTILES_THEME: get_global_val(MAPTILES_THEME) if has_global_value(MAPTILES_THEME) else None
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
