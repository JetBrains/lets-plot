# lets-plot changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
 - Python 3.6 support.
 - Windows platform support.
 - factor() function.
 - tooltip for geom_rect.
 - export SVG/HTML to file. 
 - `tooltip` parameter for geoms.
 - `axis_tooltip` parameters for `theme()`.
 - tooltip for `geom_vline` with mapping.
 
 ### Fixed
 - error if data contains NaT value.

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