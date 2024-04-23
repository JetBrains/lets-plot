## [4.3.2] - 2024-mm-dd

### Added

### Changed
      
### Fixed
- Regression of issue [[#966](https://github.com/JetBrains/lets-plot/issues/966)].
- `to_svg()`, `to_html()`: return the content as string if no "path" is given [[#1067](https://github.com/JetBrains/lets-plot/issues/1067)].
- Polygon with 2 points should be skipped silently [[#1084](https://github.com/JetBrains/lets-plot/issues/1084)].
- Linetype doesn't work for `geom_tile()` [[LPK-#241](https://github.com/JetBrains/lets-plot-kotlin/issues/241)].