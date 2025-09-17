## [4.7.4] - 2025-mm-dd

### Added

- `flavor_standard()` sets the theme's default color scheme [[#1277](https://github.com/JetBrains/lets-plot/issues/1277)]. <br>
  Use to override other flavors or make defaults explicit.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/flavor_standard.ipynb).
  
- `theme_gray()` as an alias for `theme_grey()`.

### Changed

- Geoms with 1-to-1 statistics (`geom_qq()`, `geom_sina()`) now keep bijection with original data for aesthetics.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/stat_data_bijection.ipynb).

### Fixed
