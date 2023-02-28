## [3.1.0] - 2023-03-??

### Added

- `gggrid()` function.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/plot_grid.ipynb).


- `position` parameter in position scales `scale_x_*(), scale_y_*()`.       

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/axis_position.ipynb).


- `angle` parameter in `element_text()` for `axis_text, axis_text_x, axis_text_y` in a `theme()` (i.e. to axis labels).

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/axis_text_angle.ipynb).  


- `geodesic` parameter for `geom_segment()` and `geom_path()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/param_geodesic.ipynb).


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


- `joint_plot()` - new function in the `bistro` module.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/joint_plot.ipynb).


- PNG support for `ggsave()` [[#596](https://github.com/JetBrains/lets-plot/issues/596)].
  Requires the [CairoSVG](https://pypi.org/project/CairoSVG) library

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/export_to_png.ipynb).


- `color_by` and `fill_by` layer parameters to support more than one mapping for color and fill. 
  New color aesthetics: `paint_a, paint_b, paint_c`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/multiple_color_scales.ipynb).

### Changed

- **Breaking change** in `geom_violin()`: parameter `draw_quantiles` renamed to `quantiles` - and now it works as in the `geom_area_ridges()` geometry.

- `geom_violin()`: added `quantile_lines` parameter - as in the `geom_area_ridges()` geometry. Also, it was added a `..quantile..` statistic variable.

- `geom_density()`: added two new parameters - `quantiles` and `quantile_lines` - as in the `geom_area_ridges()` geometry. Also, it was added a `..quantile..` statistic variable.

- `residual_plot()`: added `'density2d'` and `'density2df'` geometry types, changed some defaults for color parameters. 

- `pandas` library was added to dependencies of the `residual_plot()` function.

- Python packages for `Windows` no longer require `MinGW` tools to run.

- Parameter `flat=True` turns off lines re-projection, keeping the original number of points.

- Parameter `mode` added to position adjustments `'stack'` and `'fill'`. When `mode='groups'` (default) the position adjustment shifts objects only if their groups are distinct.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/position_stack.ipynb).

### Fixed

- Tooltip does not reflect `..quantile..` aesthetic change [[#658](https://github.com/JetBrains/lets-plot/issues/658)].
- `color_by` parameter of the `residual_plot()` now group the data points [[#662](https://github.com/JetBrains/lets-plot/issues/662)].
- 'map_join'-variable is lost after stat is applied [[#664](https://github.com/JetBrains/lets-plot/issues/664)].
- Error when tooltip has variable mapped to aesthetic used by stat [[#665](https://github.com/JetBrains/lets-plot/issues/665)].
- Groups not sorted similarly when `position='stack'` [[#673](https://github.com/JetBrains/lets-plot/issues/673)].
- livemap: hide tooltips when user is zooming-in by double-clicks [[#659](https://github.com/JetBrains/lets-plot/issues/659)].
- livemap: wrong position when path goes through the antimeridian [[#682](https://github.com/JetBrains/lets-plot/issues/682)].
- livemap: wrong position if path is on a circle of latitude [[#683](https://github.com/JetBrains/lets-plot/issues/683)].
- livemap: tooltip may show wrong data on density2df [[#684](https://github.com/JetBrains/lets-plot/issues/684)].
- map: geom_text vjust="center" is a bit off [[#132](https://github.com/JetBrains/lets-plot/issues/132)].
- livemap: segment that goes through the antimeridian should be straight [[#692](https://github.com/JetBrains/lets-plot/issues/692)].
- livemap: apply alpha to the pie chart and to its tooltip color marker.
- Layout: uneven plot margins for the horizontal axis [[#705](https://github.com/JetBrains/lets-plot/issues/705)].
- Sampling: increase the default N for "pick sampling" and for other types of sampling [[#687](https://github.com/JetBrains/lets-plot/issues/687)].