## [4.8.3] - 2026-mm-dd

### Added

### Changed
- [**BREAKING**]: Removed JavaFX artifacts.
- [**BREAKING**]: Removed `plot-image-export` module. Use `PlotImageExport` from `platf-awt` module instead.
- Missing values in `geom_area_ridges()` create gaps in geometries instead of being interpolated over.
- [wip] Added warning messages about the removal of invalid points during geometry construction for geom_point(), geom_path(), and geom_line() layers. [[#81](https://github.com/JetBrains/lets-plot-kotlin/issues/81)]

### Fixed
- Drop commons-io dependency [[#1421](https://github.com/JetBrains/lets-plot/issues/1421)].
- Unexpected replacement of double curly brackets with a single curly bracket [[#1433](https://github.com/JetBrains/lets-plot/issues/1433)].
- Upgrade to a newer version of ws [[#1150](https://github.com/JetBrains/lets-plot/issues/1150)].
