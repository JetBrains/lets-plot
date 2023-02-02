## [3.0.1] - 2023-??-??

### Added

- `position` parameter in position scales `scale_x_*(), scale_y_*()`.       

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/axis_position.ipynb).


- `angle` parameter in `element_text()` for `axis_text, axis_text_x, axis_text_y` in a `theme()` (i.e. to axis labels).

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/element_text_angle.ipynb).

### Changed

- **Breaking change** in `geom_violin()`: parameter `draw_quantiles` renamed to `quantiles` - and now it works as in the `geom_area_ridges()` geometry.

- `geom_violin()`: added `quantile_lines` parameter - as in the `geom_area_ridges()` geometry. Also, it was added a `..quantile..` statistic variable.

- `geom_density()`: added two new parameters - `quantiles` and `quantile_lines` - as in the `geom_area_ridges()` geometry. Also, it was added a `..quantile..` statistic variable.

- `pandas` library was added to dependencies of the `residual_plot()` function.

- Python packages for `Windows` no longer require `MinGW` tools to run.

### Fixed

- `color_by` parameter of the `residual_plot()` now group the data points [[#662](https://github.com/JetBrains/lets-plot/issues/662)].
- 'map_join'-variable is lost after stat is applied [[#664](https://github.com/JetBrains/lets-plot/issues/664)].
- Error when tooltip has variable mapped to aesthetic used by stat [[#665](https://github.com/JetBrains/lets-plot/issues/665)].
- Add png support to `ggsave()` [[#596](https://github.com/JetBrains/lets-plot/issues/596)].
- Groups not sorted similarly when `position='stack'` [[#673](https://github.com/JetBrains/lets-plot/issues/673)].