# Lets-Plot changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.5.5] - 2020-12-18

### Added
 - new tooltip anchor options - center and middle.
 - min width of a multi-line tooltip: `geom_xxx( tooltips=layer_tooltips().min_width(180) )`
 - stat_corr()
 - 'bistro' package, corr_plot()
 - 'no_js' parameter in LetsPlot.setup_html(), 'LETS_PLOT_NO_JS' env var.
 - 'na_text' parameter in 'geom_text'

### Changed

- Setting for tooltip anchor was moved from theme() to layer().
Now it can be set using the `tooltips` parameter:

   `geom_xxx( tooltips=layer_tooltips().anchor(anchor_value) )`, 
   where `anchor_value`: 
   `['top_right'|'top_center'|'top_left'|'bottom_right'|'bottom_center'|'bottom_left'|'middle_right'|'middle_center'|'middle_left']`.
 
### Fixed
 - Crosshair for corner tooltips. 
 - Tooltip should appear if the mapped data is continuous [[#241](https://github.com/JetBrains/lets-plot/issues/241)]
 - Tooltip 'null' displayed for undefined vals [[#243](https://github.com/JetBrains/lets-plot/issues/243)]
 - y-tooltip should be aligned with a tile center [[#246](https://github.com/JetBrains/lets-plot/issues/246)]
 - With facet_grid tooltip shows data from last plot on all plots [[#247](https://github.com/JetBrains/lets-plot/issues/247)]

## [1.5.4] - 2020-11-19
### Changed
 - [**breaking change**] In functions `format(field, format)` and `line(template)` in 
 tooltip builder, the '$' symbol is no longer used in aesthetic reference. It was replaced by the '^' (hat) symbol 
 (see the udated doc: [Tooltip Customization](https://github.com/JetBrains/lets-plot/blob/master/docs/tooltips.md)).
 - The project upgraded to Kotlin v1.4.10 (was v1.3.72).
 
### Fixed
 - No tooltip on v-line [[#229](https://github.com/JetBrains/lets-plot/issues/229)]

## [1.5.3] - 2020-11-05
### Added
 - Facilities for customization of tooltip contents and its position (see [Tooltip Customization](https://github.com/JetBrains/lets-plot/blob/master/docs/tooltips.md)).
 - `attribution` and other new parameters in the `maptiles_zxy()` function. 
 - `label_format` parameter in the `geom_text()` function.
 - `scale_x_discrete_reversed()` / `scale_y_discrete_reversed()`
 - `ggsave()` function (only SVG, HTML)

### Fixed
 - Mercator's projection computes coordinates incorrectly (`geom_map(), coord_map()`)
 - Handling of `xlim,ylim` in `coord_fixed(), coord_map()`
 - Colorbars: take in account limits defined in continuous color/fill scales.
 - `scale_color_hue` incorrectly interprets chroma range [[#206](https://github.com/JetBrains/lets-plot/issues/206)].
 - `scale_color_brewer` shouldn't fail if 'type' is not specified [[#203](https://github.com/JetBrains/lets-plot/issues/203)].
 - `scale_fill_discrete` should not replace the default discrete scale (Brewer) with 'HUE' scale [[#172](https://github.com/JetBrains/lets-plot/issues/172)].
 
### Changed
 - Lets-plot vector map-tiles are configured by default.
 - PACIFIC_BLUE color (#118ED8) replaced DARK_BLUE as a default color/fill value on plots.
 - Default discrete color scheme is now ColorBrewer's Set2 (was Dark2)
 
### Deprecated
 - Function `gg_image_matrix()`. The new function is: `image_matrix()` in the `lets_plot.bistro.im` module.
    The new function is not any longer displaying matrix by itself but returns a GGBunch object instead. 
    Unlike the depricated `gg_image_matrix()` function, it is not imported by `from lets_plot import *` statement.
     
    Usage: 
    ```
    from lets_plot.bistro.im import image_matrix
    image_matrix()
    ```    

## [1.5.2] - 2020-08-10
### Fixed
- map_titanic.ipynb : UnboundLocalError: local variable 'map_join' referenced before assignment [[#182](https://github.com/JetBrains/lets-plot/issues/182)]
- Add the `map_join` parameter to all geoms which support `map` parameter (py) [[#183](https://github.com/JetBrains/lets-plot/issues/183)]

## [1.5.1] - 2020-08-06
### Added
 - scale_x_reverse() / scale_y_reverse()
 - Mnemonics for the `resolution` parameter in the `regions.boundaries()` function. 

### Fixed
 - Exception when using `coord_map` with `xlim` [[#173](https://github.com/JetBrains/lets-plot/issues/173)]
 - Clipped tooltip [[#155](https://github.com/JetBrains/lets-plot/issues/155)]
 - Text on axis is clipped after hiding the tick-marks in theme [[#160](https://github.com/JetBrains/lets-plot/issues/160)]
 - Tooltip on x axis with no title shouldn't be above the axis line [[#161](https://github.com/JetBrains/lets-plot/issues/161)]
 - NPE while creating scale mapper when data series contains only nulls.
 - Default Geocoding server url.
 
## [1.5.0] - 2020-07-15
### Added
 - geocoding package
 
### Fixed 
 - NPE on geom_tile when data contains null-s.
 - The order of values in the `limits` parameter on discrete scales is ignored.
 - Livemap is not shown in GGBunch.
 
## [1.4.2] - 2020-05-28
### Added
 - Plugin for IntelliJ IDEA / PyCharm is available. For more info see the plugin homepage: [Lets-Plot in SciView](https://plugins.jetbrains.com/plugin/14379-lets-plot-in-sciview).  

### Changed
 - Plots build time reduced (up to 4X)

## [1.4.1] - 2020-05-20
### Added
 - `geom_smooth`: automatic sampling when n>1000 and the method is LOESS.
 
### Fixed 
 - 'map_join' fails when the `map` argument is a dictionary [[#130](https://github.com/JetBrains/lets-plot/issues/130)]
 
### Changed
 - HTML output was modified allow better integration with PyCharm.
 
### Removed
 - The deprecated method 'load_lets_plot_js()'.  

## [1.4.0] - 2020-05-13
### Added
 - Support for interactive maps.
 - The `as_discrete()` function.
 - Polynomial regression of an arbitrary degree (the`lm` smoothing method) in *geom_smooth*.
 - `axis_tooltip`, `axis_tooltip_x`, `axis_tooltip_y` parameters in `theme()`
 - Kaggle and Datalore notebook demos.

### Fixed
 - Severe performance degradation when using discrete scales [[#119](https://github.com/JetBrains/lets-plot/issues/119)].
 - Opaque background to better support PyCharm dark theme [[#121](https://github.com/JetBrains/lets-plot/issues/121)].
 - Auto-detection of a Kaggle environment.
 - Limits on the maximum plot size [[#115](https://github.com/JetBrains/lets-plot/issues/115)].
 - Installation instructions for Windows users [[#118 MinGW](https://github.com/JetBrains/lets-plot/issues/118)].

### Changed
 - More slick shape for tooltips on the axis.

## [1.3.0] - 2020-03-26
### Added
 - Python 3.6 support.
 - Windows platform support.
 - SVG/HTML export to file.
 - Offline mode for Jupyter notebooks.
 - Support for cloud-based notebooks like Google Colab and Datalore.
 - JVM Maven artefacts released.
 
 ### Fixed
 - Tooltip on `geom_rect` and `geom_vline`.
 - Error when date-time series contains `NaT` value.
 
 ### Changed
 - Optional `load_lets_plot_js()` function is now deprecated.
 - Initialisation call `LetsPlot.setup_html()` is now mandatory in Jupyter.

## [1.2.1] - 2020-02-13
### Fixed
- tooltip not showing in geom_polygon

## [1.2.0] - 2020-02-12
### Added
- *geopandas* support in geoms: point, path, polygon, rect, text.
- support for LOESS smoothing method in *geom_smooth*.
- new geometry layers: *geom_crossbar*, *geom_linerange*, *geom_pointrange*, *geom_bin2d*.
- support for coordinate stystem *xlim*, *ylim* parameters (i.e. "clipping").

### Fixed
- Was not working `weight` parameter in stats: bin, count, bin2d, density, density2d
- incorrect tooltip line 'NaN' in geom_boxplot.
- ambiguous tooltip positioning in geoms: tile, bin2d.
- cropped text in tooltip.

## [1.1.0] - 2019-12-17
### Added
- *GGBunch*. Combines several different plots into one graphical object.
- *geom_image()*. Displays an image specified by ndarray with shape (n,m) or (n,m,3) or (n,m,4). 
- *gg_image_matrix()*. A utility helping to combine several images into one graphical object.
- user_guide.ipynb
- ggbunch.ipynb  
- scatter_matrix.ipynb
- image_101.ipynb
- image_fisher_boat.ipynb
- image_matrix.ipynb

### Changed
- Switched to Kotlin 1.3.61

### Fixed
- *scale_datetime()*. Date-time formatting in tooltips.
- Links in README_PYTHON.md

## [1.0.0] - 2019-11-27
### Changed
 - First public release.
