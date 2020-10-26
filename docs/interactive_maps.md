# Interactive maps

*Lets-Plot* supports interactive maps via the `geom_livemap()` geom layer which
enables a researcher to visualize geospatial information on a zoomable and paneble map. 

<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/map_path.png" alt="Couldn't load map_path.png" width="436" height="267"><br><br>

When building interactive geospatial visualizations with *Lets-Plot* the visualisation workflow remains the 
same as when building a regular `ggplot2` plot.

However, `geom_livemap()` creates an interactive base-map super-layer and certain limitations do apply 
comparing to a regular `ggplot2` geom-layer:

* `geom_livemap()` must be added as a 1-st layer in plot;
* Maximum one `geom_livemap()` layer is alloed per plot;
* Not any type of *geometry* can be combined with interactive map layer in one plot;
* Internet connection to *map tiles provider* is required.

The following `ggplot2` geometry can be used with interactive maps:

* `geom_point`
* `geom_rect`
* `geom_path`
* `geom_polygon`
* `geom_segment`
* `geom_text`
* `geom_tile`
* `geom_vline`, `geon_hline`
* `geom_bin2d`
* `geom_contour`, `geom_contourf`
* `geom_density2d`, `geom_density2df`

Examples:

* [map_quickstart.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/map_quickstart.ipynb)
* [map_california_housing.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/map-california-housing/map_california_housing.ipynb)
