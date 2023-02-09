## [3.0.1] - 2023-??-??

### Added

- `position` parameter in position scales `scale_x_*(), scale_y_*()`.       

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/axis_position.ipynb).


- `angle` parameter in `element_text()` for `axis_text, axis_text_x, axis_text_y` in a `theme()` (i.e. to axis labels).

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/element_text_angle.ipynb).  


- `geodesic` parameter for `geom_segment()` and `geom_path()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/geodesic_path.ipynb).


- New scale functions with  parameter `aesthetic` to define aesthetics that this scale works with:
  - `scale_identity(aesthetic, *, ...)`
  - `scale_manual(aesthetic, values, *, ...)`
  - `scale_continuous(aesthetic, *, ...)`
  - `scale_gradient(aesthetic, *, ...)`
  - `scale_gradient2(aesthetic, *, ...)`
  - `scale_gradientn(aesthetic, *, ...)`
  - `scale_hue(aesthetic, *, ...)`
  - `scale_discrete(aesthetic, *, ...)`
  - `scale_grey(aesthetic, *, ...)`
  - `scale_brewer(aesthetic, *, ...)`
  - `scale_viridis(aesthetic, *, ...)`
    
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/scale_functions.ipynb). 

### Changed

- `pandas` library was added to dependencies of the `residual_plot()` function.
- Python packages for `Windows` no longer require `MinGW` tools to run.
- parameter `flat=True` turns off lines re-projection, keeping the original number of points.

### Fixed

- `color_by` parameter of the `residual_plot()` now group the data points [[#662](https://github.com/JetBrains/lets-plot/issues/662)].
- 'map_join'-variable is lost after stat is applied [[#664](https://github.com/JetBrains/lets-plot/issues/664)].
- Error when tooltip has variable mapped to aesthetic used by stat [[#665](https://github.com/JetBrains/lets-plot/issues/665)].
- Add png support to `ggsave()` [[#596](https://github.com/JetBrains/lets-plot/issues/596)].
- Groups not sorted similarly when `position='stack'` [[#673](https://github.com/JetBrains/lets-plot/issues/673)].
- livemap: hide tooltips when user is zooming-in by double-clicks [[#659](https://github.com/JetBrains/lets-plot/issues/659)].
- livemap: wrong position when path goes through the antimeridian [[#682](https://github.com/JetBrains/lets-plot/issues/682)].
- livemap: wrong position if path is on a circle of latitude [[#683](https://github.com/JetBrains/lets-plot/issues/683)].
- livemap: tooltip may show wrong data on density2df [[#684](https://github.com/JetBrains/lets-plot/issues/684)].
