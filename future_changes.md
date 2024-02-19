## [4.2.1] - 2024-mm-dd

### Added
- `panel_grid_ontop, panel_grid_ontop_x, panel_grid_ontop_y` parameters in `theme()`

### Changed

- [BREAKING] Function `geom_image()` is removed. Please use `geom_imshow()` instead.  
         

### Fixed

- geom_errorbar(): wrong positioning of tooltips [[#992](https://github.com/JetBrains/lets-plot/issues/992)].
- geom_path(): tooltip position interpolation [[#855](https://github.com/JetBrains/lets-plot/issues/855)].
- Stacked bar-chart annotation: labels go out of the plot when zooming-in using coord_cartesian(xlim, ylim) [[#981](https://github.com/JetBrains/lets-plot/issues/981)].
- Facets: "free scales" options are ignored by discrete axis [[#955](https://github.com/JetBrains/lets-plot/issues/955)].
- Bar width is too large when x-domain is defined via x-scale limits [[#1013](https://github.com/JetBrains/lets-plot/issues/1013)].
- How to hide only main tooltip? [[LPK-#232](https://github.com/JetBrains/lets-plot-kotlin/issues/232)].
- Make middle strip in `geomCrossbar()` optional [[LPK-#233](https://github.com/JetBrains/lets-plot-kotlin/issues/233)].
- Can't set None for coord limit [[#486](https://github.com/JetBrains/lets-plot/issues/486)].