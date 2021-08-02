#  Copyright (c) 2021. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.


from lets_plot import maptiles_lets_plot as _maptiles_lets_plot  # to not polute scope with maptiles_lets_plot
from lets_plot import maptiles_zxy as _maptiles_zxy  # to not polute scope with maptiles_zxy
from lets_plot import maptiles_solid as _maptiles_solid  # to not polute scope with maptiles_zxy

LETS_PLOT_COLOR = _maptiles_lets_plot(theme='color')
LETS_PLOT_LIGHT = _maptiles_lets_plot(theme='light')
LETS_PLOT_DARK = _maptiles_lets_plot(theme='dark')

SOLID = _maptiles_solid('#FFFFFF')

OSM = _maptiles_zxy(
    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
    attribution='<a href="https://www.openstreetmap.org/copyright">© OpenStreetMap contributors</a>',
    min_zoom=1, max_zoom=19, subdomains='abc'
)

OPEN_TOPO_MAP = _maptiles_zxy(
    url="https://tile.opentopomap.org/{z}/{x}/{y}.png",
    attribution='Map data: <a href="https://www.openstreetmap.org/copyright">© OpenStreetMap contributors</a>, <a href="http://viewfinderpanoramas.org/">SRTM</a> | map style: <a href="https://opentopomap.org/">© OpenTopoMap</a> (<a href="https://creativecommons.org/licenses/by-sa/3.0/">CC-BY-SA</a>) ',
    min_zoom=1, max_zoom=16
)


def _stamen_tiles(tileset, data_license, max_zoom, lowres_only=False):
    if data_license == 'osm':
        attribution = 'Map tiles by <a href="http://stamen.com">Stamen Design</a>, under <a href="http://creativecommons.org/licenses/by/3.0">CC BY 3.0</a>. Data by <a href="http://openstreetmap.org">OpenStreetMap</a>, under <a href="http://www.openstreetmap.org/copyright">ODbL</a>.'
    elif data_license == 'cc':
        attribution = 'Map tiles by <a href="http://stamen.com">Stamen Design</a>, under <a href="http://creativecommons.org/licenses/by/3.0">CC BY 3.0</a>. Data by <a href="http://openstreetmap.org">OpenStreetMap</a>, under <a href="http://creativecommons.org/licenses/by-sa/3.0">CC BY SA</a>.'
    else:
        raise ValueError("Unknown data license: {}. Expected 'osm' or 'cc'".format(data_license))

    def build_stamen_tiles_config(hi_res=""):
        return _maptiles_zxy(
            "https://stamen-tiles-{{s}}.a.ssl.fastly.net/{tileset}/{{z}}/{{x}}/{{y}}{hi_res}.png".format(tileset=tileset, hi_res=hi_res),
            attribution,
            min_zoom=1, max_zoom=max_zoom, subdomains='abcd'
        )

    if lowres_only:
        return build_stamen_tiles_config()

    return build_stamen_tiles_config(), build_stamen_tiles_config(hi_res="@2x")


STAMEN_DESIGN_TONER, STAMEN_DESIGN_TONER_HIRES = _stamen_tiles('toner', data_license='osm', max_zoom=20)
STAMEN_DESIGN_TONER_LIGHT, STAMEN_DESIGN_TONER_LIGHT_HIRES = _stamen_tiles('toner-lite', data_license='osm', max_zoom=20)
STAMEN_DESIGN_TONER_LABELS, STAMEN_DESIGN_TONER_LABELS_HIRES = _stamen_tiles('toner-labels', data_license='osm', max_zoom=20)
STAMEN_DESIGN_TERRAIN, STAMEN_DESIGN_TERRAIN_HIRES = _stamen_tiles('terrain', data_license='osm', max_zoom=18)
STAMEN_DESIGN_WATERCOLOR = _stamen_tiles('watercolor', data_license='cc', max_zoom=18, lowres_only=True)


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
            '<a href="https://www.openstreetmap.org/copyright">© OpenStreetMap contributors</a> <a href="https://carto.com/attributions#basemaps">© CARTO</a>, <a href="https://carto.com/attributions">© CARTO</a>',
            min_zoom=1, max_zoom=20, subdomains='abc'
        )

    return build_carto_tiles_config(), build_carto_tiles_config(hi_res="@2x")


CARTO_POSITRON, CARTO_POSITRON_HIRES = _carto_tiles('light_all', cdn='carto')
CARTO_POSITRON_NO_LABELS, CARTO_POSITRON_NO_LABELS_HIRES = _carto_tiles('light_nolabels', cdn='carto')
CARTO_DARK_MATTER_NO_LABELS, CARTO_DARK_MATTER_NO_LABELS_HIRES = _carto_tiles('dark_nolabels', cdn='carto')
CARTO_VOYAGER, CARTO_VOYAGER_HIRES = _carto_tiles('voyager', cdn='carto')
CARTO_MIDNIGHT_COMMANDER, CARTO_MIDNIGHT_COMMANDER_HIRES = _carto_tiles('base-midnight', cdn='fastly')
CARTO_ANTIQUE, CARTO_ANTIQUE_HIRES = _carto_tiles('base-antique', cdn='fastly')
CARTO_FLAT_BLUE, CARTO_FLAT_BLUE_HIRES = _carto_tiles('base-flatblue', cdn='fastly')


def _nasa_tiles(tileset, max_zoom, time=''):
    # https://wiki.earthdata.nasa.gov/display/GIBS/GIBS+API+for+Developers
    return _maptiles_zxy(
        url="https://gibs.earthdata.nasa.gov/wmts/epsg3857/best/{tileset}/default/{time}/GoogleMapsCompatible_Level{max_zoom}/{{z}}/{{y}}/{{x}}.jpg" \
            .format(tileset=tileset, time=time, max_zoom=max_zoom),
        attribution='<a href="https://earthdata.nasa.gov/eosdis/science-system-description/eosdis-components/gibs">© NASA Global Imagery Browse Services (GIBS)</a>',
        min_zoom=1, max_zoom=max_zoom
    )


NASA_CITYLIGHTS_2012 = _nasa_tiles('VIIRS_CityLights_2012', max_zoom=8)
NASA_BLUEMARBLE_NEXTGENERATION = _nasa_tiles('BlueMarble_NextGeneration', max_zoom=8)
NASA_GREYSCALE_SHADED_RELIEF_30M = _nasa_tiles('ASTER_GDEM_Greyscale_Shaded_Relief', max_zoom=12)
NASA_COLOR_SHADED_RELIEF_30M = _nasa_tiles('ASTER_GDEM_Color_Shaded_Relief', max_zoom=12)
NASA_TERRA_TRUECOLOR = _nasa_tiles('MODIS_Terra_CorrectedReflectance_TrueColor', max_zoom=9, time='2015-06-07')
