# Configuring basemap tiles for interactive maps

With Lets-Plot it's possible to configure tiles for a whole notebook or indiviaully for each plot.

Setup tiles config for a notebook with the [LestPlot.set(...)](https://lets-plot.org/pages/api/lets_plot.LetsPlot.html?highlight=set#lets_plot.LetsPlot.set) function which accepts a specially-formed `dict`:
```python
from lets_plot import *
from lets_plot import tilesets
LetsPlot.setup_html()
LetsPlot.set(tilesets.LETS_PLOT_DARK)

ggplot() + geom_livemap()
```

Setup tiles config for a plot with the `tiles` parameter of the [geom_livemap(...)](https://lets-plot.org/pages/api/lets_plot.geom_livemap.html) function:
```python
from lets_plot import *
from lets_plot import tilesets
LetsPlot.setup_html()

ggplot() + geom_livemap(tiles=tilesets.LETS_PLOT_DARK)
```

## Lets-Plot tiles
Free vector tiles with themes, provided by Lets-Plot. Available themes: `color`, `dark`, `light`. Default theme is `color`.   
Configuration function: [LetsPlot.maptiles_lets_plot(...)](https://lets-plot.org/pages/api/lets_plot.maptiles_lets_plot.html).  
Parameters:

`url : str`  
Template for a standard raster ZXY tile provider with {z}, {x}, {y} and {s} placeholders,
e.g. ``"https://{s}.tile.com/{z}/{x}/{y}.png"``. Where {z} means zoom, {x} and {y} means
tile coordinate, {s} means subdomains.

`attribution : str`  
An attribution or a copyright notice to display on the map as required by the tile license.
Supports HTML links: ``'<a href="http://www.example.com">Example</a>'``.

`min_zoom : int`  
Minimal zoom limit. Affects map zoom limit.

`max_zoom : int`  
Maximal zoom limit. Affects map zoom limit.

`subdomains : str`  
Each character of this list is interpreted as standalone tile servers, so an interactive map
can request tiles from any of these servers independently for better load balance. If url
contains {s} placeholder and subdomains parameter is not set default string 'abc' will be used.

`other_args`  
Any key-value pairs that can be substituted into the URL template, e.g.  
`maptiles_zxy(url='http://maps.example.com/{z}/{x}/{y}.png?access_key={key}', key='MY_ACCESS_KEY')`.

```python
from lets_plot import *
LetsPlot.setup_html()

ggplot() + geom_livemap(tiles=maptiles_lets_plot(theme='dark'))
```

Lets-Plot also provides non-graphical so-called solid tiles. They don't need Internet connection and allows user to pan and zoom geospatial data. Color can be configured.

Configuration function: [LetsPlot.maptiles_solid(...)](https://lets-plot.org/pages/api/lets_plot.maptiles_solid.html).  
Parameters:

`color : str`  
Color in HEX format.

```python
from lets_plot import *
LetsPlot.setup_html()

ggplot() + geom_livemap(tiles=maptiles_solid(color='#C1C1C1'))
```

## Raster tiles
Interactive map also supports raster tile services that provide tiles via HTTP/HTTPS. Note that usually these tile services require attribution and/or API key.  

Configuration function: [LetsPlot.maptiles_zxy(...)](https://lets-plot.org/pages/api/lets_plot.maptiles_zxy.html)

Parameters:

`url : str`  
Template for a standard raster ZXY tile provider with {z}, {x}, {y} and {s} placeholders,
e.g. ``"https://{s}.tile.com/{z}/{x}/{y}.png"``. Where {z} means zoom, {x} and {y} means
tile coordinate, {s} means subdomains.

`attribution : str`  
An attribution or a copyright notice to display on the map as required by the tile license.
Supports HTML links: ``'<a href="http://www.example.com">Example</a>'``.

`min_zoom : int`  
Minimal zoom limit. Affects map zoom limit.

`max_zoom : int`  
Maximal zoom limit. Affects map zoom limit.

`subdomains : str`  
Each character of this list is interpreted as standalone tile servers, so an interactive map
can request tiles from any of these servers independently for better load balance. If url
contains {s} placeholder and subdomains parameter is not set a default string 'abc' will be used.

`other_args`  
Any key-value pairs that can be substituted into the URL template, e.g.  
`maptiles_zxy(url='http://maps.example.com/{z}/{x}/{y}.png?access_key={key}', key='MY_ACCESS_KEY')`.


```python
from lets_plot import *
LetsPlot.setup_html()

nasa_city_lights = maptiles_zxy(
    url="https://gibs.earthdata.nasa.gov/wmts/epsg3857/best/VIIRS_CityLights_2012/default/GoogleMapsCompatible_Level8/{z}/{y}/{x}.jpg",
    attribution='<a href="https://earthdata.nasa.gov/eosdis/science-system-description/eosdis-components/gibs">© NASA Global Imagery Browse Services (GIBS)</a>',
    max_zoom=8
)

ggplot() + geom_livemap(tiles=nasa_city_lights)
```
## Tilesets
Lest-Plot comes with collection of predefind configurations, including configuration for various free raster tile services. All configrations have proper attribution string, subdomains list and zoom limitations. HIRES suffix in a name means that service provides high resolution tiles for High DPI displays.  

Contains the following groups of tiles:  
- LETS_PLOT_XXX (vector tiles provided by LetsPlot)
- SOLID (solid white color tiles)
- CARTO_XXX (https://carto.com/help/building-maps/basemap-list/)
- STAMEN_XXX (http://maps.stamen.com)
- OSM (https://wiki.openstreetmap.org/wiki/Standard_tile_layer)
- OPEN_TOPO_MAP (https://wiki.openstreetmap.org/wiki/OpenTopoMap)
- NASA (https://wiki.earthdata.nasa.gov/display/GIBS/GIBS+Available+Imagery+Products?src=spaceshortcut)

```python
from lets_plot import *
from lets_plot import tilesets
LetsPlot.setup_html()

ggplot() + geom_livemap(tiles=tilesets.NASA_CITYLIGHTS_2012)
```
# Examples
![basemaps.jpg](basemaps.png)
See more in [gallery](TODO).