## [4.5.2] - 2024-11-dd

### Added

### Changed
- The `g` format type no longer uses truncation by default. To enable it, use `~` (e.g., `~g`).
  
### Fixed

- Kandy toPNG reports NullPointerException [[#1228](https://github.com/JetBrains/lets-plot/issues/1228)]
- Wrong formatting when type='g' for small values [[#1238](https://github.com/JetBrains/lets-plot/issues/1238)].
- Formatting when type='g' for large values throws exception [[#1239](https://github.com/JetBrains/lets-plot/issues/1239)].
- Wrong formatting when type='s' with explicit precision [[#1240](https://github.com/JetBrains/lets-plot/issues/1240)].
- Extra trim in formatted number when type='g' [[#1241](https://github.com/JetBrains/lets-plot/issues/1241)].
- Axis breaks are badly formatted if explicitly set [[#1245](https://github.com/JetBrains/lets-plot/issues/1245)].