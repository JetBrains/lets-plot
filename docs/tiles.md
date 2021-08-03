## Map tiles

Interactive maps require tiles to be pretty. Lets-plot provides its own set of free to use vector tiles. To start using the tiles just wrote:

```ggplot() + geom_livemap()```

Blank map will show up.

### Tilesets
Lest-Plot provides collection of free raster tile services configurations for easier map styling. All tilests have properly set attribution, subdomains and zoom limits. HIRES suffix in tileset means service provides high resolution tiles which is nice for High DPI displays.  

Tilests contains the following groups of tiles:  
- LETS_PLOT_XXX (vector tiles provided by LetsPlot)
- CARTO_XXX (https://carto.com/help/building-maps/basemap-list/)
- STAMEN_XXX (http://maps.stamen.com)
- OSM (https://wiki.openstreetmap.org/wiki/Standard_tile_layer)
- OPEN_TOPO_MAP (https://wiki.openstreetmap.org/wiki/OpenTopoMap)
- NASA (https://wiki.earthdata.nasa.gov/display/GIBS/GIBS+Available+Imagery+Products?src=spaceshortcut)
- SOLID (solid white color tiles)


### Applying tiles configuration
Lets-Plot allows applying tiles configuration for a whole notebook or indiviaully for each plot.

Setup tiles config for a notebook:
```python
from lets_plot import *
from lets_plot import tilesets
LetsPlot.setup_html()
LetsPlot.set(tilesets.LETS_PLOT_DARK)
ggplot() + geom_livemap()
```

Pplot tile configuration:

```python
from lets_plot import *
from lets_plot import tilesets
LetsPlot.setup_html()
ggplot() + geom_livemap(tiles=tilesets.LETS_PLOT_DARK)
```

### Manual map tiles configuration

It's easy to configure your own tiles service.

#### Raster tiles
Raster tile services that provide tiles via HTTP/HTTPS. Note that usually these tile services require attribution and/or API key.  
Configured with function `LetsPlot.maptiles_zxy(...)`.  
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


#### Lets-Plot vector tiles
Free vector tiles with themes support, provided by Lets-Plot.   
Configured with function `Lets-Plot.maptiles_lets_plot(...)`.  
Parameters:   
`theme: str = None`  
List of available themes: `'color', 'light', 'dark'`. Default value is `'color'`.

#### Solid color tiles
There are cases when graphical tiles not needed - like saving paid requests while working on a notebook, when plot should look like a choropleth or when there is no internet acccess. In this case solid color tiles can be used.

Configured with function `LetsPlot.maptiles_solid(...)`. 

Parameters:

`color : str`  
Color in HEX format.


