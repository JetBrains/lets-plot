## [2.0.5] - 2021-??-??

### Added

- Subdomains parameter `{s}` for raster tiles services.
- Solid color tiles `maptiles_solid()`.
- Chessboard tiles `maptiles_chessboard()`.
- Module `lets_plot.tilesets` with collection of free tiles providers. 
- as_discrete: add support for ordered parameter [[#136](https://github.com/JetBrains/lets-plot/issues/136)].
  
  New parameters in function `as_discrete`:
  * `order_by` (string) - the name of the variable by which the ordering will be performed;
  * `order` (int) - the ordering direction - 1 for ascending direction and -1 for descending (default value).

  See: [as_discrete](https://github.com/JetBrains/lets-plot/blob/doc-ordering/docs/as_discrete.md).


### Changed

- Upgraded Apach Batik version to 1.14 (was 1.12) [[#398](https://github.com/JetBrains/lets-plot/issues/398)].

### Fixed

- geom_livemap: missing ScreenGeometryComponent exception.
- geom_livemap: repaint only on visual changes to reduce a CPU usage at idle.
- geom_livemap: improved click events detection.
- geom_livemap: properly handle `max_zoom` pamareter in `maptiles_zxy()`.
- Strange looking legend for tiles [[#245](https://github.com/JetBrains/lets-plot/issues/245)].
- Need to skip "bad" values during scale transformation [[#301](https://github.com/JetBrains/lets-plot/issues/301)].
- NPE on negative value in data and scale_xxx(trans='log10') [[#292](https://github.com/JetBrains/lets-plot/issues/292)].
- Legend is broken when using scale_fill_brewer with 'trans' parameter [[#284](https://github.com/JetBrains/lets-plot/issues/284)].
- scale_y_log10: hidden segments [[#372](https://github.com/JetBrains/lets-plot/issues/372)].
- Scale breaks should be distributed evenly on 'sqrt' scale. [[#407](https://github.com/JetBrains/lets-plot/issues/407)].
- Wrong tooltip formatting when used with log10 scales [[#406](https://github.com/JetBrains/lets-plot/issues/406)].
- Bad axis labels when using both plot and layer data [[#327](https://github.com/JetBrains/lets-plot/issues/327)].
- Plot layout looks wrong [[#403](https://github.com/JetBrains/lets-plot/issues/403)].
- map_join is not working correctly when `map=geocoder` [[#380](https://github.com/JetBrains/lets-plot/issues/380)]
- Tooltip default formatting should not change after adding other variables to tooltip [[#388](https://github.com/JetBrains/lets-plot/issues/388)].
- Tooltip on axis: increase the font size. [[#399](https://github.com/JetBrains/lets-plot/issues/399)].
- Tooltip format for variable is not working [[#401](https://github.com/JetBrains/lets-plot/issues/401)].
- Wrong direction in colorbars (legend) [[#204](https://github.com/JetBrains/lets-plot/issues/204)].
- geom_jitter: show axis tooltips (same as geom_point) [[#412](https://github.com/JetBrains/lets-plot/issues/412)].
- Memory leak in IDEA caused by a `final void dispose()` method in PlotPanel.
- Outlier tooltips: the spout sometimes is too long (boxplot) [[#358](https://github.com/JetBrains/lets-plot/issues/358)].