## [2.0.3] - 2021-??-??

### Added

- In tooltip customization API:
    - `layer_tooltips(variables)` - the new parameter `variables` defines a list of variable names, which values will be placed line by line in the general tooltip.
   See: [Tooltip Customization](https://github.com/JetBrains/lets-plot/blob/master/docs/tooltips.md).

### Changed

### Fixed
        
- Removed the last dependency on bintray JCenter ([commit](https://github.com/JetBrains/lets-plot/commit/7bcd38e000a4952b83269ef4ebac0b7d826dea6a)). 
- geom_boxplot: should be possible to create boxplot without specifying x-series [[#325](https://github.com/JetBrains/lets-plot/issues/325)]
- geom_hline: graph plotted outside of coordinate plane visible part [[#334](https://github.com/JetBrains/lets-plot/issues/334)]
- Draw geometry only once if layer has no aes mapping specified [[#73](https://github.com/JetBrains/lets-plot/issues/73)]
- map: calif.housing [[#140](https://github.com/JetBrains/lets-plot/issues/140)]
- Can't build plot: "Uncaught SyntaxError: Unexpected string" in a console [[#371](https://github.com/JetBrains/lets-plot/issues/371)]
- All scales should have the 'format' parameter [[#347](https://github.com/JetBrains/lets-plot/issues/347)].
- Poor font rendering in Swing/Batik. Related to:  [[#364](https://github.com/JetBrains/lets-plot/issues/364)]
- Exclude slf4j implementation from lets-plot-common [[#374](https://github.com/JetBrains/lets-plot/issues/374)]