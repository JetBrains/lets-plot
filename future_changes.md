## [3.2.1] - 2023-mm-dd

### Added

- Tooltips for `geom_step()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/geom_step_tooltips.ipynb).


- In tooltip customization API:
  `disable_splitting()` function to hide side tooltips.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/tooltips_disable_splitting.ipynb).


### Changed

- [BREAKING] `geom_boxplot()` no longer support parameter `sampling`.


### Fixed

- ggsave: saving geomImshow() to SVG produces fuzzy picture [[LPK-188](https://github.com/JetBrains/lets-plot-kotlin/issues/188)].
- ggsave: saving geomImshow() to raster format produces fuzzy picture.
