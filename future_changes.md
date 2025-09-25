## [4.7.4] - 2025-mm-dd

### Added

- `ggtb()`: `size_zoomin` and `size_basis` parameters for geometry scaling. [[#1369](https://github.com/JetBrains/lets-plot/issues/1369)]
  
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/ggtb_size_zoomin.ipynb).

- `flavor_standard()` sets the theme's default color scheme [[#1277](https://github.com/JetBrains/lets-plot/issues/1277)]. <br>
  Use to override other flavors or make defaults explicit.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/flavor_standard.ipynb).
  
- `theme_gray()` as an alias for `theme_grey()`.

### Changed

- Geoms with 1-to-1 statistics (`geom_qq()`, `geom_sina()`) now keep bijection with original data for aesthetics.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/stat_data_bijection.ipynb).

### Fixed

- Markdown: links open only inside the iframe
  [[#1397](https://github.com/JetBrains/lets-plot/issues/1397)].
- geom_density2d: NullPointerException when weight aesthetic contains None values [[#1399](https://github.com/JetBrains/lets-plot/issues/1399)].
- Tooltip shows duplicate lines when as_discrete is applied twice to the same var [[#1400](https://github.com/JetBrains/lets-plot/issues/1400)].