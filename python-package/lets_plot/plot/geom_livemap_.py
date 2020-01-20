#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from enum import Enum
from typing import Union, Optional, List

from .geom import _geom

try:
    import pandas
except ImportError:
    pandas = None

# from ..geo_data.livemap_helper import _prepare_location
# from ..geo_data.livemap_helper import _prepare_parent
# from ..geo_data.livemap_helper import _prepare_tiles

__all__ = ['geom_livemap']


def geom_livemap(mapping=None, data=None, geom=None, stat=None, show_legend=None, sampling=None, level=None,
                 interactive=None, location=None,
                 zoom=None, within=None, magnifier=None, clustering=None, scaled=None, labels=None, theme=None,
                 projection=None, geodesic=None, tiles=None, **other_args):
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
    stat : string, optional
        The statistical transformation to use on the data for this layer, as a string. Supported transformations:
        "identity" (leaves the data unchanged), "count" (counts number of points with same x-axis coordinate),
        "bin" (counts number of points with x-axis coordinate in the same bin), "smooth" (performs smoothing -
        linear default)
    geom : string, optional
        The mode of the displayed data. There are:
        - 'polygon' for colored areas (default).
        - 'point' for circles of different size and color.
        - 'pie' for pie charts.
        - 'heatmap' for heatmap (color spots).
        - 'bar' for bar charts.
    level : string, optional
        The administrative level of the displayed data.
        There are 'country', 'state', 'county', 'city', None (default).
    within : string, optional
        Data can be filtered by within name, for example 'USA'.
    interactive : True (default) or False, optional
        Enables user interaction with the map.
    magnifier : True or False (default), optional
        Enables a magnifier when you click on overlapping point. Applicable for 'point'.
    location : string or array, optional
        Initial position of the map. If not set, displays the United States.
        There are id | [lon1, lat1, lon2, lat2,..., lonN, latN].
        - id (string, for example 'Texas').
        - lon1, lon2,..., lonN are longitudes in degrees (positive in the Eastern hemisphere).
        - lat1, lat2,..., latN are latitudes in degrees (positive in the Northern hemisphere).
    zoom : integer, optional
        Zoom of the map in the range 1 - 15.
    clustering : True or False (default), optional
        Enables a clustering for overlapping points. Applicable for 'point'.
    scaled : True or False (default), optional
        Enables a scaling for heatmap.
        If True, the specified size is equal to the size at zero zoom.
    labels : True (default) or False, optional
        Enables a drawing labels on map.
    tiles: string or dict, optional
    theme : string, optional
        Theme for the map.
        There are:
        - 'color' for default mode.
        - 'light' for less colored mode.
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
    - map_id : geographical id or string in format 'lon, lat' used to join data with map coordinates.
               You can use function lon_lat('lon', 'lat') to concatenate coordinates.
    - alpha : transparency level of a layer
        Understands numbers between 0 and 1.
    - color (colour) : color of a geometry lines
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - fill  : color of a geometry internals
        Can be continuous or discrete. For continuous value this will be a color gradient between two colors.
    - size : line width for polygon, radius for point, pie chart and color spot (heatmap).
    - x : value order for pie chart and bar chart.
    - y : value specifying the sector size for pie chart and the heigth for bar chart.
    - frame : timestamp for time-varying heatmap.
    - group : how to group points into polygons (grouping tag). Default: all points belong to one polygon.

    Examples
    --------
    >>> from lets_plot import *
    >>> data = {'state': ['Nevada', 'TEXAS', 'FL'], 'val': [2000, 2200, 1800]}
    >>> p = ggplot(data) + geom_livemap(aes(map_id='state', fill='val'), within='USA')
    >>> p += scale_fill_gradient(low='red')
    >>> p += ggtitle('Live Map')
    """
    # if within is not None:
    #     within = _prepare_parent(within)

    if location is not None:
        location = _prepare_location(location)

    if tiles is not None:
        tiles = _prepare_tiles(tiles)

    _display_mode = 'display_mode'

    if _display_mode in other_args.keys():
        other_args.pop(_display_mode)

    return _geom('livemap', mapping, data, stat, None, show_legend, sampling=sampling,
                 display_mode=geom, level=level,
                 within=within, interactive=interactive, location=location, zoom=zoom, magnifier=magnifier,
                 clustering=clustering, scaled=scaled, labels=labels, theme=theme, projection=projection,
                 geodesic=geodesic, tiles=tiles, **other_args)


LOCATION_COORDINATE_COLUMNS = {'lon', 'lat'}
LOCATION_RECTANGLE_COLUMNS = {'lonmin', 'latmin', 'lonmax', 'latmax'}
LOCATION_LIST_ERROR_MESSAGE = "Expected: location = [double lon1, double lat1, ... , double lonN, double latN]"
LOCATION_DATAFRAME_ERROR_MESSAGE = "Expected: location = DataFrame with [{}] or [{}] columns" \
    .format(', '.join(LOCATION_COORDINATE_COLUMNS), ', '.join(LOCATION_RECTANGLE_COLUMNS))


class RegionKind(Enum):
    region_ids = 'region_ids'
    region_name = 'region_name'
    coordinates = 'coordinates'
    data_frame = 'data_frame'


def _prepare_tiles(tiles: Union[str, dict]) -> Optional[dict]:
    if tiles is None:
        return None

    if isinstance(tiles, str):
        return {'raster': tiles}

    if isinstance(tiles, dict):
        return {'vector': tiles}

    else:
        raise ValueError('Wrong tiles type: ' + tiles.__str__())


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
