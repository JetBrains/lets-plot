# lets-plot changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
 - as_discrete() function.
 - (?) `tooltip` parameter for geoms.
 - `theme`: `axis_tooltip`='blank' (also `axis_tooltip_x`, `axis_tooltip_x`)
 - polynomial regression for geom_smooth.
 - tooltip on `geom_hline`.
 - Fix auto-detection of Kaggle environment.

## [1.3.0] - 2020-03-26
### Added
 - Python 3.6 support.
 - Windows platform support.
 - SVG/HTML export to file.
 - Offline mode for Jupyter notebooks.
 - Support for cloud-based notebooks like Google Colab and Datalore.
 - JVM Maven artefacts released.
 
 ### Fixed
 - No tooltip on `geom_rect` and `geom_vline`.
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
