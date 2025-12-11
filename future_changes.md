## [4.8.2] - 2026-mm-dd

### Added

- Plot Theme:

-  New `minor_ticks`, `minor_ticks_length` parameters in `theme()` to control minor ticks [[#1379](https://github.com/JetBrains/lets-plot/issues/1379)]. <br>

  See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/axis_minor_ticks.html).

### Changed

- [**DEPRECATED**]: `plot-image-export` module is deprecated and will be removed in future versions. Use `PlotImageExport` from `platf-awt` module instead.
- Missing values in `geom_area_ridges()` create gaps in geometries instead of being interpolated over.
- [wip] Added warning messages about the removal of invalid points during geometry construction for geom_point(), geom_path(), and geom_line() layers. [[#81](https://github.com/JetBrains/lets-plot-kotlin/issues/81)]

### Fixed

- Hyperlinks support for lets-plot-compose.
- Drop commons-io dependency [[#1421](https://github.com/JetBrains/lets-plot/issues/1421)].
- Unexpected replacement of double curly brackets with a single curly bracket [[#1433](https://github.com/JetBrains/lets-plot/issues/1433)].
