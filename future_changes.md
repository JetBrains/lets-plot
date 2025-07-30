## [4.7.1] - 2025-mm-dd

### Added

- plot-image-export: added `width`, `height` and `unit` parameters. If specified, they will override plot's pixels size (default or set by `ggsize()`).
- ggsave(): support font synthesis for *italic* and **bold** styles.

### Changed

- plot-image-export: use `dpi` to calculate exported image pixel size.


### Fixed
- Arrow crossing -180 longitude is split into two arrows [[#1364](https://github.com/JetBrains/lets-plot/issues/1364)].
- Coordinate limits do not work on reversed scales [[#1365](https://github.com/JetBrains/lets-plot/issues/1365)]
- Display order of fill categories not being set correctly in stacked plots? [[#1367](https://github.com/JetBrains/lets-plot/issues/1367)]
