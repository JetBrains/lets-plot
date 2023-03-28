## [3.1.1] - 2023-03-??

### Added

### Changed

### Fixed
- livemap: memory leak caused by a document event handler.   
- livemap: flickering when zooming with the buttons.   
- livemap: tooltip text doesn't reflect data under the cursor [[#709](https://github.com/JetBrains/lets-plot/issues/709)].
- Quantile should be shown in tooltip if the variable `..quantile..` is mapped to geom aesthetic.
- Bad default formatting for stat variables [[#654](https://github.com/JetBrains/lets-plot/issues/654)].
- The scale name does not apply with `as_discrete()` [[#653](https://github.com/JetBrains/lets-plot/issues/653)]. 