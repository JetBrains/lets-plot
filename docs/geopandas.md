# GeoPandas Support ([GeoPandas](https://geopandas.org) and [Shapely](https://pypi.org/project/Shapely/))
  
GeoPandas GeoDataFrame is a tabular data structure that contains a set of shapes (geometry) per each observation.

GeoDataFrame extends pandas DataFrame and as such, aside from the geometry, can contain other data.

GeoPandas supports the following three basic classes of geometric objects (shapes):

* Points / Multi-Points
* Lines / Multi-Lines
* Polygons / Multi-Polygons

All GeoPandas shapes are "undersood" by Lets-Plot and can be plotted using various geometry layers, depending on the type of the shape.

Use:

* `geom_point, geom_text` with Points / Multi-Points
* `geom_path` with Lines / Multi-Lines
* `geom_polygon, geom_map` with Polygons / Multi-Polygons
* `geom_rect` when used with Polygon shapes will display corresponding bounding boxes


#### Examples

* The world map with *Lets-Plot* and *GeoPandas*: 
[geopandas_naturalearth.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/geopandas_naturalearth.ipynb)

* An **inset map** of Kotlin island: 
[geopandas_kotlin_isl.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/geopandas_kotlin_isl.ipynb)

<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/kotlin_island.png" alt="Couldn't load kotlin_island.png" width="473" height="327"><br><br>

