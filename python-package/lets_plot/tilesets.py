#  Copyright (c) 2021. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.


from lets_plot import maptiles_lets_plot as _maptiles_lets_plot  # to not polute scope with maptiles_lets_plot
from lets_plot import maptiles_solid as _maptiles_solid  # to not polute scope with maptiles_solid
from lets_plot import maptiles_zxy as _maptiles_zxy  # to not polute scope with maptiles_zxy

LETS_PLOT_COLOR = _maptiles_lets_plot(theme='color')
"""
Default vector tiles.

Examples
--------
.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.LETS_PLOT_COLOR)

"""

LETS_PLOT_LIGHT = _maptiles_lets_plot(theme='light')
"""
Vector tiles, light theme.

Examples
--------
.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.LETS_PLOT_LIGHT)

"""

LETS_PLOT_DARK = _maptiles_lets_plot(theme='dark')
"""
Vector tiles, dark theme.

Examples
--------
.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.LETS_PLOT_DARK)

"""

LETS_PLOT_BW = _maptiles_lets_plot(theme='bw')
"""
Vector tiles, BW theme.

Examples
--------
.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.LETS_PLOT_BW)

"""

SOLID = _maptiles_solid('#FFFFFF')
"""
Blank tiles.
Show no other graphics but a solid background color.

Examples
--------
.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.SOLID)

"""

OSM = _maptiles_zxy(
    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
    attribution='map data: <a href="https://www.openstreetmap.org/copyright">© OpenStreetMap contributors</a>',
    min_zoom=1, max_zoom=19, subdomains='abc'
)
"""
OpenStreetMap's standard tile layer.

Examples
--------
.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.OSM)

"""

OPEN_TOPO_MAP = _maptiles_zxy(
    url="https://tile.opentopomap.org/{z}/{x}/{y}.png",
    attribution='map data: <a href="https://www.openstreetmap.org/copyright">© OpenStreetMap contributors</a>, <a href="http://viewfinderpanoramas.org/">SRTM</a> | map style: <a href="https://opentopomap.org/">© OpenTopoMap</a> (<a href="https://creativecommons.org/licenses/by-sa/3.0/">CC-BY-SA</a>) ',
    min_zoom=1, max_zoom=16
)
"""
OpenTopoMap's tile layer.

Examples
--------
.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.OPEN_TOPO_MAP)

"""


def _carto_tiles(tileset, cdn):
    def build_carto_tiles_config(hi_res=''):
        if cdn == 'carto':
            base_url = "https://{{s}}.basemaps.cartocdn.com/rastertiles/{tileset}/{{z}}/{{x}}/{{y}}{hi_res}.png"
        elif cdn == 'fastly':
            base_url = "https://cartocdn_{{s}}.global.ssl.fastly.net/{tileset}/{{z}}/{{x}}/{{y}}{hi_res}.png"
        else:
            raise ValueError("Unknown carto cdn: {}. Expected 'carto' or 'fastly'.".format(cdn))

        return _maptiles_zxy(
            base_url.format(tileset=tileset, hi_res=hi_res),
            'map data: <a href="https://www.openstreetmap.org/copyright">© OpenStreetMap contributors</a> <a href="https://carto.com/attributions#basemaps">© CARTO</a>, <a href="https://carto.com/attributions">© CARTO</a>',
            min_zoom=1, max_zoom=20, subdomains='abc'
        )

    return build_carto_tiles_config(), build_carto_tiles_config(hi_res="@2x")


CARTO_POSITRON, CARTO_POSITRON_HIRES = _carto_tiles('light_all', cdn='carto')
"""
CARTO tiles, positron theme.

Examples
--------
.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.CARTO_POSITRON)

|

.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.CARTO_POSITRON_HIRES)

"""

CARTO_POSITRON_NO_LABELS, CARTO_POSITRON_NO_LABELS_HIRES = _carto_tiles('light_nolabels', cdn='carto')
"""
CARTO tiles, positron (no labels) theme.

Examples
--------
.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.CARTO_POSITRON_NO_LABELS)

|

.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.CARTO_POSITRON_NO_LABELS_HIRES)

"""

CARTO_DARK_MATTER_NO_LABELS, CARTO_DARK_MATTER_NO_LABELS_HIRES = _carto_tiles('dark_nolabels', cdn='carto')
"""
CARTO tiles, dark matter (no labels) theme.

Examples
--------
.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.CARTO_DARK_MATTER_NO_LABELS)

|

.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.CARTO_DARK_MATTER_NO_LABELS_HIRES)

"""

CARTO_VOYAGER, CARTO_VOYAGER_HIRES = _carto_tiles('voyager', cdn='carto')
"""
CARTO tiles, voyager theme.

Examples
--------
.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.CARTO_VOYAGER)

|

.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.CARTO_VOYAGER_HIRES)

"""

CARTO_MIDNIGHT_COMMANDER, CARTO_MIDNIGHT_COMMANDER_HIRES = _carto_tiles('base-midnight', cdn='fastly')
"""
CARTO tiles, midnight commander theme.

Examples
--------
.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.CARTO_MIDNIGHT_COMMANDER)

|

.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.CARTO_MIDNIGHT_COMMANDER_HIRES)

"""

CARTO_ANTIQUE, CARTO_ANTIQUE_HIRES = _carto_tiles('base-antique', cdn='fastly')
"""
CARTO tiles, antique theme.

Examples
--------
.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.CARTO_ANTIQUE)

|

.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.CARTO_ANTIQUE_HIRES)

"""

CARTO_FLAT_BLUE, CARTO_FLAT_BLUE_HIRES = _carto_tiles('base-flatblue', cdn='fastly')
"""
CARTO tiles, flat blue theme.

Examples
--------
.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.CARTO_FLAT_BLUE)

|

.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.CARTO_FLAT_BLUE_HIRES)

"""


def _nasa_tiles(tileset, max_zoom, time=''):
    # https://wiki.earthdata.nasa.gov/display/GIBS/GIBS+API+for+Developers
    return _maptiles_zxy(
        url="https://gibs.earthdata.nasa.gov/wmts/epsg3857/best/{tileset}/default/{time}/GoogleMapsCompatible_Level{max_zoom}/{{z}}/{{y}}/{{x}}.jpg" \
            .format(tileset=tileset, time=time, max_zoom=max_zoom),
        attribution='map data: <a href="https://earthdata.nasa.gov/eosdis/science-system-description/eosdis-components/gibs">© NASA Global Imagery Browse Services (GIBS)</a>',
        min_zoom=1, max_zoom=max_zoom
    )


NASA_CITYLIGHTS_2012 = _nasa_tiles('VIIRS_CityLights_2012', max_zoom=8)
"""
NASA tiles, CityLights 2012 theme.

Examples
--------
.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.NASA_CITYLIGHTS_2012)

"""

NASA_BLUEMARBLE_NEXTGENERATION = _nasa_tiles('BlueMarble_NextGeneration', max_zoom=8)
"""
NASA tiles, BlueMarble NextGeneration theme.

Examples
--------
.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.NASA_BLUEMARBLE_NEXTGENERATION)

"""

NASA_GREYSCALE_SHADED_RELIEF_30M = _nasa_tiles('ASTER_GDEM_Greyscale_Shaded_Relief', max_zoom=12)
"""
NASA tiles, greyscale shaded relief (30m) theme.

Examples
--------
.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.NASA_GREYSCALE_SHADED_RELIEF_30M)

"""

NASA_COLOR_SHADED_RELIEF_30M = _nasa_tiles('ASTER_GDEM_Color_Shaded_Relief', max_zoom=12)
"""
NASA tiles, color shaded relief (30m) theme.

Examples
--------
.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.NASA_COLOR_SHADED_RELIEF_30M)

"""

NASA_TERRA_TRUECOLOR = _nasa_tiles('MODIS_Terra_CorrectedReflectance_TrueColor', max_zoom=9, time='2015-06-07')
"""
NASA tiles, Terra TrueColor theme.

Examples
--------
.. jupyter-execute::
    :linenos:
    :emphasize-lines: 4

    from lets_plot import *
    from lets_plot import tilesets
    LetsPlot.setup_html()
    ggplot() + geom_livemap(tiles=tilesets.NASA_TERRA_TRUECOLOR)

"""