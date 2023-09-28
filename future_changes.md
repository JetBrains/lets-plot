## [4.0.2] - 2023-mm-dd

### Added

- Horizontal orientation by assigning y, xmin, xmax aesthetics of geoms:
  - `geom_errorbar()`;
  - `geom_crossbar()`;
  - `geom_pointrange()`;
  - `geom_linerange()`;
  - `geom_ribbon()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23e/horizontal_geoms.ipynb).
  
### Changed

- [BREAKING] `stat_summary()` and `stat_summary_bin` no longer supports computing of additional variables through the specifying of mappings.

### Fixed

- Tooltips are trimmed and not visible on a very narrow chart [[#837](https://github.com/JetBrains/lets-plot/issues/837)].
- Inability to use the spelling "grey" for the color grey (via "gray" only).
- `geom_crossbar`: an error occurs if the mapped values of aesthetics are not populated [[#886](https://github.com/JetBrains/lets-plot/issues/886)].