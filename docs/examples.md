## Example Notebooks

Try the following tutorials and examples to learn and evaluate various features of the `Lets-Plot` library.

- [Quickstart and User Guide](#quickstart)
- [Geoms and Stats](#geoms_n_stats)
- [Position Adjustment](#pos)
- [Scales](#scales)
- [Facets](#facets)
- [GGBunch](#ggbunch)
- [`as_discrete()` function](#as_discrete)
- [Export to File](#export)
- [Theme](#theme)
- [Data Sampling](#sampling)
- [Tooltip Customization](#tooltip)
- [The 'bistro' Package](#bistro)
- [GeoPandas Support](#geopandas)
- [Interactive Maps](#livemap)
- [Geocoding API](#geocoding)



<a id="quickstart"></a>
#### Quickstart and User Guide

- Quickstart: 
            <a href="https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/quickstart.ipynb"> 
                <img src="https://raw.githubusercontent.com/jupyter/design/master/logos/Badges/nbviewer_badge.png" width="109" height="20" align="left">
            </a>
            <span>&nbsp;&nbsp;</span>
            <a href="https://view.datalore.io/notebook/Zzg9EVS6i16ELQo3arzWsP" title="View in Datalore"> 
                <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_datalore.svg" width="20" height="20">
            </a>
            <span>&nbsp;&nbsp;</span>
            <a href="https://www.kaggle.com/alshan/lets-plot-quickstart" title="View at Kaggle"> 
                <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_kaggle.svg" width="20" height="20">
            </a>
            <span>&nbsp;&nbsp;</span>
            <a href="https://colab.research.google.com/drive/1uYYZcG0g0kP4lJdPkpWB8aBS96ioDii2?usp=sharing" title="View at Colab"> 
                <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_colab.svg" width="20" height="20">
            </a>

- [user_guide.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/guide/user_guide.ipynb)


<a id="geoms_n_stats"></a>
#### Geoms and Stats

`geom_histogram, geom_density, geom_vline, geom_freqpoly, geom_boxplot`:

[distributions.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/distributions.ipynb)

`geom_errorbar, geom_line, geom_point, geom_bar, geom_crossbar, geom_linerange, geom_pointrange`:

[error_bars.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/error_bars.ipynb)

`geom_point, geom_smooth (stat_smooth)`:

[scatter_plot.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/scatter_plot.ipynb)

[geom_smooth.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/geom_smooth.ipynb) 

`geom_density2d, geom_density2df, geom_bin2d, geom_polygon, geom_point` :

[density_2d.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/density_2d.ipynb)

`geom_tile, geom_contour, geom_polygon (Stat.contour), geom_contourf` :

[contours.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/contours.ipynb)

`geom_image, geom_raster`

`geom_image()`: displays an image specified by a ndarray with shape (n,m) or (n,m,3) or (n,m,4).

* [image_101.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/image_101.ipynb)
* [image_fisher_boat.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/image_fisher_boat.ipynb)

See also: `image_matrix` in [The 'bistro' Package](#bistro).

`geom_text`, label format

[label_format.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/label_format.ipynb)

`stat_corr`

See the `corr_plot` example in [The 'bistro' Package](#bistro).
 

<a id="pos"></a>
#### Position Adjustment

* `position_dodge` : [error_bars.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/error_bars.ipynb)
* `position_jitter` : [scatter_plot.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/scatter_plot.ipynb)


<a id="scales"></a>
#### Scales

* `scale_color_manual, scale_fill_manual` :
 [error_bars.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/error_bars.ipynb)
* `scale_x_continuous, scale_shape_manual` :
 [scatter_plot.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/scatter_plot.ipynb)
* `scale_color_gradient` : [density_2d.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/density_2d.ipynb)
* `scale_fill_hue, scale_fill_grey, scale_color_gradient` : [contours.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/contours.ipynb)


<a id="facets"></a>
#### Facets

* `facet_grid`: 
[distributions.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/distributions.ipynb)


<a id="ggbunch"></a>
#### GGBunch

GGBunch allows to show a collection of plots on one figure. Each plot in the collection can have arbitrary location and size. There is no automatic layout inside the bunch.

* [ggbunch.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/ggbunch.ipynb) 
* [geom_smooth.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/geom_smooth.ipynb)
* [scatter_matrix.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/scatter_matrix.ipynb) 


<a id="as_discrete"></a>
#### `as_discrete()` function 

* [geom_smooth.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/geom_smooth.ipynb) 


<a id="export"></a>
#### Export to File

The `ggsave()` function is an easy way to export plot to a file in SVG or HTML formats.

* [export_SVG_HTML.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/export_SVG_HTML.ipynb)


<a id="theme"></a>
#### Theme

Legend layout and axis presentation options : 
 
* [legend_and_axis.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/legend_and_axis.ipynb)


<a id="sampling"></a>
#### Data Sampling 

Sampling is a special technique of data transformation, which helps to deal with large datasets and overplotting.

See: [Sampling in Lets-Plot](https://github.com/JetBrains/lets-plot/blob/master/docs/sampling.md).


<a id="tooltip"></a>
#### Tooltip Customization

* [tooltip_config.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/tooltip_config.ipynb)
* Visualization of Airport Data on Map: <a href="https://www.kaggle.com/alshan/visualization-of-airport-data-on-map" title="View at Kaggle"> 
                                               <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_kaggle.svg" width="20" height="20">
                                        </a>
                                        <br>
                                        
                                        
See also [Tooltip Customization](https://github.com/JetBrains/lets-plot/blob/master/docs/tooltips.md).


<a id="bistro"></a>
### The 'bistro' Package

#### `from lets_plot.bistro.corr`

`corr_plot`: [correlation_plot.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/correlation_plot.ipynb)

#### `from lets_plot.bistro.im`

`image_matrix`: [image_matrix.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/image_matrix.ipynb) 


<a id="geopandas"></a>
#### GeoPandas Support

See [GeoPandas Support](https://github.com/JetBrains/lets-plot/blob/master/docs/geopandas.md). 


<a id="livemap"></a>
#### Interactive Maps 
  
The interactive map allows zooming in and out and panning around geospatial data that can be added to the base-map layer 
using regular ggplot2 'geoms'.

See [Interactive Maps](https://github.com/JetBrains/lets-plot/blob/master/docs/interactive_maps.md). 


<a id="geocoding"></a>
#### Geocoding API

Geocoding is the process of converting names of places into geographic coordinates.

See [Geocoding API](https://github.com/JetBrains/lets-plot/blob/master/docs/geocoding.md). 


