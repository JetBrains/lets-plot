## [4.3.2] - 2024-04-25

### Added

### Changed

- `to_svg()`, `to_html()`: return the content as string if no "path" is given [[#1067](https://github.com/JetBrains/lets-plot/issues/1067)].

### Fixed

- LP occasionally crashes when drawing polygons [[#1084](https://github.com/JetBrains/lets-plot/issues/1084)].
- Regression of issue [[#966](https://github.com/JetBrains/lets-plot/issues/966)].
- Livemap: labels on the map look blurry [[#1045](https://github.com/JetBrains/lets-plot/issues/1045)].
- Linetype doesn't work for `geom_tile()` [[LPK-241](https://github.com/JetBrains/lets-plot-kotlin/issues/241)].