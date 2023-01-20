## [3.0.1] - 2023-??-??

### Added

- `position` parameter in position scales `scale_x_*(), scale_y_*()`.       

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/axis_position.ipynb).

### Changed

- `pandas` library was added to dependencies of the `residual_plot()` function.
- Python packages for `Windows` no longer require `MinGW` tools to run.

- `geom_density()`: added two new parameters - `quantiles` and `quantile_lines` - as in the `geom_area_ridges()` geometry. Also, it was added a `..quantile..` statistic variable. 

- `geom_violin()`: added `quantile_lines` parameter - as in the `geom_area_ridges()` geometry. Also, it was added a `..quantile..` statistic variable.

### Fixed

- `color_by` parameter of the `residual_plot()` now group the data points [[#662](https://github.com/JetBrains/lets-plot/issues/662)].
