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


def geom_livemap(*,
                 location=None,
                 zoom=None,
                 projection=None,
                 tiles=None,
                 show_coord_pick_tools=None,
                 data_size_zoomin=None,
                 const_size_zoomin=None,
                 **other_args):
    """
    Display an interactive map.

    Parameters
    ----------
    location : list
        Initial position of the map. If not set, display the United States.
        There are [lon1, lat1, lon2, lat2,..., lonN, latN]:
        lon1, lon2,..., lonN are longitudes in degrees (positive in the Eastern hemisphere);
        lat1, lat2,..., latN are latitudes in degrees (positive in the Northern hemisphere).
    zoom : int
        Zoom of the map in the range 1 - 15.
    projection : str, default='epsg3857'
        The map projection. There are: 'epsg3857' for Mercator projection;
        'epsg4326' for Equirectangular projection. ``projection`` only works
        with vector map tiles (i.e. Lets-Plot map tiles).
    tiles : str
        Tile provider:

        - pass a predefined constant from the ``tilesets`` module (Lets-Plot's vector tiles, e.g. `LETS_PLOT_COLOR <https://lets-plot.org/python/pages/api/lets_plot.tilesets.LETS_PLOT_COLOR.html>`__, or external raster tiles, e.g. `OPEN_TOPO_MAP <https://lets-plot.org/python/pages/api/lets_plot.tilesets.OPEN_TOPO_MAP.html>`__);
        - pass a URL for a standard raster ZXY tile provider with {z}, {x} and {y} wildcards (e.g. 'http://my.tile.com/{z}/{x}/{y}.png') if the required tileset not present in the module;
        - pass the result of a call to a `maptiles_zxy() <https://lets-plot.org/python/pages/api/lets_plot.maptiles_zxy.html>`__ function if further customisation is required (e.g. attribution or zoom).

        More information about tiles can be found here:
        https://lets-plot.org/python/pages/basemap_tiles.html
    show_coord_pick_tools : bool, default=False
        Show buttons "copy location" and "draw geometry".
    data_size_zoomin : int, default=0
        Control how zooming-in of the map widget increases size of geometry objects (circles, lines etc.) on map
        when the size is set by means of mapping between the data and the ``size`` aesthetic.
        0 - size never increases;
        -1 - size will be increasing without limits;
        n - a number of zooming-in steps (counting from the initial state of the map widget)
        when size of objects will be increasing. Farther zooming will no longer affect the size.
    const_size_zoomin : int, default=-1
        Control how zooming-in of the map widget increases size of geometry objects (circles, lines etc.) on map
        when the size is not linked to a data (i.e. constant size).
        0 - size never increases;
        -1 - size will be increasing without limits;
        n - a number of zooming-in steps (counting from the initial state of the map widget)
        when size of objects will be increasing. Farther zooming will no longer affect the size.
    other_args
        Other arguments passed on to the layer.

    Returns
    -------
    ``LayerSpec``
        Geom object specification.

    Notes
    -----
    ``geom_livemap()`` draws a map, which can be dragged and zoomed.

    ----

    By default the livemap area has a non-zero inset. You can get rid of this with the theme: ``theme(plot_inset=0)``.

    ---

    When drawing a path with two points, the shortest route is taken. To create a longer arc, add intermediate points.

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
        :emphasize-lines: 10

        from lets_plot import *
        from lets_plot import tilesets
        LetsPlot.setup_html()
        data = {
            'city': ['New York City', 'Prague'],
            'lon': [-73.7997, 14.418540],
            'lat': [40.6408, 50.073658],
        }
        ggplot(data, aes(x='lon', y='lat')) + \\
            geom_livemap(projection='epsg4326', tiles=tilesets.LETS_PLOT_DARK) + \\
            geom_path(color='white', geodesic=True) + \\
            geom_point(color='white', tooltips=layer_tooltips().line('@city')) + \\
            ggtitle("The shortest path between New York and Prague")

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'x': [-170, 170, -170, 0, 170],
            'y': [10, 10, -10, -10, -10],
            'route': ['A', 'A', 'B', 'B', 'B'],
        }
        ggplot(data) + \\
            geom_livemap(zoom=1, location=[180, 0]) + \\
            geom_path(aes('x', 'y', color='route'), size=1) + \\
            scale_color_manual(values=['red', 'green'],
                               labels={'A': "'x': [-170, 170]",
                                       'B': "'x': [-170, 0, 170]"}) + \\
            ggtitle("A path that crosses the antimeridian")

    """
    if 'symbol' in other_args:
        print("WARN: The parameter 'symbol' is no longer supported. "
              "Use separate geom_point() or geom_pie() geometry layers to display markers on the map.")
        other_args.pop('symbol')

    deprecated_params = set.intersection(
        {'data', 'mapping', 'map', 'map_join', 'ontop', 'stat', 'position', 'show_legend', 'sampling', 'tooltips'},
        other_args
    )
    if len(deprecated_params) > 0:
        print(f"WARN: These parameters are not supported and will be ignored: {str(deprecated_params):s}. "
              "Specify a separate geometry layer to display data on the livemap.")

    for param in deprecated_params:
        other_args.pop(param)

    if location is not None:
        location = _prepare_location(location)

    tiles = _prepare_tiles(tiles)
    geocoding = _prepare_geocoding()

    return _geom('livemap',
                 mapping=None,
                 data=None,
                 stat=None,
                 position=None,
                 show_legend=None,
                 sampling=None,
                 tooltips=None,
                 map=None, map_join=None,
                 location=location,
                 zoom=zoom,
                 projection=projection,
                 tiles=tiles,
                 geocoding=geocoding,
                 show_coord_pick_tools=show_coord_pick_tools,
                 data_size_zoomin=data_size_zoomin,
                 const_size_zoomin=const_size_zoomin,
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
            _warn_deprecated_tiles(tiles)
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
            _warn_deprecated_tiles(None)
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


def _warn_deprecated_tiles(tiles: Union[dict, None]):
    if tiles is None:
        maptiles_url = get_global_val(MAPTILES_URL)
    else:
        maptiles_url = tiles[MAPTILES_URL]

    # Check if the current tiles should be deprecated and print a deprecation message. Otherwise, return.
    return


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
