# Interactive Maps

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

* Interactive maps: quick start: <a href="https://datalore.jetbrains.com/view/notebook/cwDq8gX5UGidzo65RY85yP" title="View in Datalore">
  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_datalore.svg" width="20" height="20">
  </a>
* Visualization of the Titanic's Voyage: <a href="https://view.datalore.jetbrains.com/notebook/1h4h0HMctRKJLY64PBe63a?force_sso=true" title="View in Datalore"> 
                                             <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_datalore.svg" width="20" height="20">
                                         </a>
                                         <span>&nbsp;&nbsp;</span>
                                         <a href="https://www.kaggle.com/alshan/visualization-of-the-titanic-s-voyage" title="View at Kaggle"> 
                                             <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_kaggle.svg" width="20" height="20">
                                         </a>
                                         <span>&nbsp;&nbsp;</span>
                                         <a href="https://colab.research.google.com/drive/1PerUfSCyStcbnlXnxBj-JVI25-cXB_N5?usp=sharing" title="View at Colab"> 
                                             <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_colab.svg" width="20" height="20">
                                         </a>
* Visualization of Airport Data on Map:  <a href="https://www.kaggle.com/alshan/visualization-of-airport-data-on-map" title="View at Kaggle"> 
                                             <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_kaggle.svg" width="20" height="20">
                                         </a>
* The Gallery of Base-maps:              <a href="https://www.kaggle.com/alshan/the-gallery-of-basemaps" title="View at Kaggle"> 
                                           <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_kaggle.svg" width="20" height="20">
                                         </a>
                                         <span>&nbsp;&nbsp;</span>
                                         <a href="https://colab.research.google.com/drive/1lwOyQx0UMBHFiLtXQZhXQpv5Z3M2XJI4?usp=sharing" title="View at Colab"> 
                                           <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_colab.svg" width="20" height="20">
                                         </a>
* Mapping US Household Income:          <a href="https://www.kaggle.com/alshan/mapping-us-household-income" title="View at Kaggle">
  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_kaggle.svg" width="20" height="20">
  </a>
* Beijing housing prices on a map:      <a href="https://www.kaggle.com/alshan/beijing-housing-prices-on-a-map-with-spatial-join" title="View at Kaggle">
  <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_kaggle.svg" width="20" height="20">
  </a>
* [map_california_housing.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/map-california-housing/map_california_housing.ipynb)
                                         
 