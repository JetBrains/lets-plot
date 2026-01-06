## [4.8.3] - 2026-mm-dd

### Added

- Plot Theme:

- Support of axis minor ticks via `minor_ticks` and `minor_ticks_length` parameters in `theme()` [[#1379](https://github.com/JetBrains/lets-plot/issues/1379)].

  See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/axis_minor_ticks.html).

### Changed
- [**BREAKING**]: Removed JavaFX artifacts.
- [**BREAKING**]: Removed `plot-image-export` module. Use `PlotImageExport` from `platf-awt` module instead.
- Missing values in `geom_area_ridges()` create gaps in geometries instead of being interpolated over.
- [wip] Added warning messages about the removal of invalid points during geometry construction for geom_point(), geom_path(), and geom_line() layers. [[#81](https://github.com/JetBrains/lets-plot-kotlin/issues/81)]

### Fixed
- Drop commons-io dependency [[#1421](https://github.com/JetBrains/lets-plot/issues/1421)].
- Unexpected replacement of double curly brackets with a single curly bracket [[#1433](https://github.com/JetBrains/lets-plot/issues/1433)].
- Upgrade to a newer version of ws [[#1150](https://github.com/JetBrains/lets-plot/issues/1150)].
- geom_imshow: unclear error message when mixing transparencies [[#1088](https://github.com/JetBrains/lets-plot/issues/1088)].
- geom_imshow and scale_y_reverse [[#1210](https://github.com/JetBrains/lets-plot/issues/1210)].
