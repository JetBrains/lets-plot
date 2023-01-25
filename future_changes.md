## [3.0.1] - 2023-??-??

### Added

- `position` parameter in position scales `scale_x_*(), scale_y_*()`.       

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/axis_position.ipynb).

### Changed

- `pandas` library was added to dependencies of the `residual_plot()` function.
- Python packages for `Windows` no longer require `MinGW` tools to run.

### Fixed

- `color_by` parameter of the `residual_plot()` now group the data points [[#662](https://github.com/JetBrains/lets-plot/issues/662)].
- 'map_join'-variable is lost after stat is applied [[#664](https://github.com/JetBrains/lets-plot/issues/664)].
- Error when tooltip has variable mapped to aesthetic used by stat [[#665](https://github.com/JetBrains/lets-plot/issues/665)].
- Add png support to `ggsave()` [[#596](https://github.com/JetBrains/lets-plot/issues/596)].