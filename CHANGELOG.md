# Lets-Plot changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.2.1] - 2021-12-10

### Added

- `scale_x_time()` and `scale_y_time()` [[#468](https://github.com/JetBrains/lets-plot/issues/468)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-21-12/notebooks/scale_time.ipynb).

- `plot_background, legend_background` parameters in `theme()` [[#485](https://github.com/JetBrains/lets-plot/issues/485)].
- `axis_ontop, axis_ontop_x, axis_ontop_y` parameters in `theme()`

### Fixed

- Coord system limits do not work with x/y scale with transform [[#474](https://github.com/JetBrains/lets-plot/issues/474)].
- Provide 0-23 hour formatting [[#469](https://github.com/JetBrains/lets-plot/issues/469)].
- No tooltip shown when I'm trying to add an empty line [[#382](https://github.com/JetBrains/lets-plot/issues/382)].
- `coord_fixed()` should adjust dimensions of "geom" panel accordingly [[#478](https://github.com/JetBrains/lets-plot/issues/478)].
- The tooltip dependence on number of factors works separately by layers [[#481](https://github.com/JetBrains/lets-plot/issues/481)].
- Tooltip on y-axis looks wrong [[#393](https://github.com/JetBrains/lets-plot/issues/393)].
- Is kotlin-reflect really needed for lets-plot? [[#471](https://github.com/JetBrains/lets-plot/issues/471)].

## [2.2.0] - 2021-10-29

### Added

- `coord_flip()`.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-21-10/notebooks/coord_flip.ipynb).

- Date-time formatting support:
  - tooltip format() should understand date-time format pattern [[#387](https://github.com/JetBrains/lets-plot/issues/387)];
  - scale_x_datetime should apply date-time formatting to the breaks [[#392](https://github.com/JetBrains/lets-plot/issues/392)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-21-10/notebooks/datetime_formatting.ipynb).

- Pre-configured themes:
  - Standard ggplot2 themes: `theme_grey(), theme_light(), theme_classic(), theme_minimal()`;
  - Other themes: `theme_minimal2()` - the default theme, `theme_none()`.

- Theme modification: more parameters were added to the `theme()` function.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-21-10/notebooks/themes.ipynb).

> Note: fonts size, family and face still can not be configured.

- `corr_plot()` function now also accepts pre-computed correlation coefficients. I.e. the following two expressions are equivalent:
```python
    corr_plot(iris_df).points().labels().build()
    corr_plot(iris_df.corr()).points().labels().build()  # new
```

### Changed

- The size of fonts on plot was slightly increased all across the board.
- The default plot size was increased by 20%, it's now 600x400 px.
- **Deprecated API**: `stat_corr()` and "correlation stat" will be removed in one of the future releases.
  Please use the `corr_plot()` plot builder object instead.

### Fixed

- Ordering facets - the "order" value 0 disables facet ordering [[#454](https://github.com/JetBrains/lets-plot/issues/454)].
- Tooltips for discrete variables: add the dependence of the tooltip on the number of factors.
  The X-axis tooltip is always shown for discrete data.
- map_join doesn't work when join variables with same names without mappings [[#428](https://github.com/JetBrains/lets-plot/issues/428)].
- Zoom without clipping breaks tooltips [[#373](https://github.com/JetBrains/lets-plot/issues/373)].
- Unreadable breaks on axis [[#430](https://github.com/JetBrains/lets-plot/issues/430)].
- Map rendering is broken when browser uses scaling [[#450](https://github.com/JetBrains/lets-plot/issues/450)].
- corr_plot() error for data with zero variation [[#329](https://github.com/JetBrains/lets-plot/issues/329)].
- Misleading error message [[#271](https://github.com/JetBrains/lets-plot/issues/271)].

## [2.1.0] - 2021-08-16

### Added

- Ordering categories. New parameters in the `as_discrete` function:
    * `order_by` (string) - the name of the variable by which the ordering will be performed;
    * `order` (int) - the ordering direction - 1 for ascending direction and -1 for descending (default value).

  See: [as_discrete](https://lets-plot.org/pages/as_discrete.html).

- Basemap tiles configuring:
    - Subdomains parameter `{s}` for XYZ raster tiles.
    - Solid color tiles: `maptiles_solid()`.
    - Builtin configurations for some 3rd party maptile services. The `lets_plot.tilesets` module.

  See: [Configuring basemap tiles](https://lets-plot.org/pages/basemap_tiles.html).

### Changed

- Upgraded Apach Batik version to 1.14 (was 1.12) [[#398](https://github.com/JetBrains/lets-plot/issues/398)].
- Upgraded Kotlin version to 1.5.21 (was 1.4.21)
- Upgraded Gradle version to 7.1.1 (was 6.8.3)

### Fixed

- geom_livemap: properly handle `max_zoom` pamareter in `maptiles_zxy()`.
- Strange looking legend for tiles [[#245](https://github.com/JetBrains/lets-plot/issues/245)].
- Need to skip "bad" values during scale transformation [[#301](https://github.com/JetBrains/lets-plot/issues/301)].
- NPE on negative value in data and scale_xxx(trans='log10') [[#292](https://github.com/JetBrains/lets-plot/issues/292)].
- Legend is broken when using scale_fill_brewer with 'trans' parameter [[#284](https://github.com/JetBrains/lets-plot/issues/284)].
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
- Outlier tooltips: the spout sometime is too long (boxplot) [[#358](https://github.com/JetBrains/lets-plot/issues/358)].
- Faceted plot is broken by geom with "constant" aesthetics [[#391](https://github.com/JetBrains/lets-plot/issues/391)].
- Interactive maps in AWT (PyCharm plugin):
    - Memory leaks in PyCharm caused by `dispose()` method in PlotPanel.
    - While showing map in PyCharm CPU is busy all the time, even when nothing is changing on screen.
    - Click events detection.

## [2.0.4] - 2021-06-09

### Changed

- Upgraded `kotlinx.html` version to 0.7.3 (was 0.7.2)
>  In JVM projects it's no longer necessary to add `https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven` repository
>  to the project configuration.

### Fixed

- Multilayer plots are corrupted [[#385](https://github.com/JetBrains/lets-plot/issues/385)].

## [2.0.3] - 2021-06-02

### Added

- In tooltip customization API:
    - `layer_tooltips(variables)` - the new parameter `variables` defines a list of variable names, which values will be placed line by line in the general tooltip.
   See: [Tooltip Customization](https://lets-plot.org/pages/tooltips.html).

### Changed

- CDN: Lets-Plot JavaScript library is now served via [JSDELIVR](https://www.jsdelivr.com/?docs=gh) (was CDNJS):
> https://cdn.jsdelivr.net/gh/JetBrains/lets-plot@v2.0.3/js-package/distr/lets-plot.min.js

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

## [2.0.2] - 2021-04-13

### Changed

- Due to shutting down of [Bintray, JCenter](https://jfrog.com/blog/into-the-sunset-bintray-jcenter-gocenter-and-chartcenter/):
  - The Lets-Plot JS library is now delivered via [CDNJS](https://cdnjs.com/libraries/lets-plot).
  - JVM Maven artifacts are now published at [Maven Central](https://search.maven.org/search?q=lets-plot).

## [2.0.1] - 2021-03-17

### Added

- The `alpha` parameter for lines [[#139](https://github.com/JetBrains/lets-plot/issues/139)].
- Tooltips for `geom_segment()` [[#296](https://github.com/JetBrains/lets-plot/issues/296)].
- The `guides()` function [[#52](https://github.com/JetBrains/lets-plot/issues/52)].
- New Java **Swing plot components** to enable embedding Lets-Plot charts into JVM applications.
  - See: `vis-swing-common, vis-swing-batik, vis-swing-jfx` modules. 

### Fixed
                         
- Fix auto-detection of PyCharm env to enable plotting in SciView while using remote interpreter [[348](https://github.com/JetBrains/lets-plot/issues/348)]
- Fix tooltips appearing outside the specified x/y limits.
- Clippath in accordance to the given limits [[#189](https://github.com/JetBrains/lets-plot/issues/189)].
- Treat a data as DataFrame if both data and map are GeoDataFrames [[#343](https://github.com/JetBrains/lets-plot/issues/343)].
- Removed the restriction on tooltips for small polygons [[#298](https://github.com/JetBrains/lets-plot/issues/298)].
- The x/y axis labels are derived from x/y aesthetics only [[#333](https://github.com/JetBrains/lets-plot/issues/333)].
- Merge 'theme' settings [[#147](https://github.com/JetBrains/lets-plot/issues/147)].
- Add axis tooltips for `geom_bin2d`.
- Outlier tooltips for `geom_ribbon()`.
- Fix tooltip crosshair [[#309](https://github.com/JetBrains/lets-plot/issues/309)].

## [2.0.0] - 2021-02-09

### Added

- Python 3.9 support on all platforms.
- `facet_wrap()` function [[#238](https://github.com/JetBrains/lets-plot/issues/238)]
- In facets:
  - Ascending/descending ordering of faceting values.
  - Formatting of faceting values. 
               
  See: [Facets demo](https://nbviewer.jupyter.org/github/JetBrains/lets-plot-docs/blob/master/source/examples/cookbook/facets.ipynb)


- In tooltip customization API: 
   - option `color` overrides the default tooltip color:
       ```python
       geom_xxx(tooltips=layer_tooltips().color('red'))
       ```

  See: [Tooltip Customization](https://lets-plot.org/pages/tooltips.html). 


- Crosshair cursor when tooltip is in fixed position specified by the `anchor` option.
- Scale `format` parameter: formatting tick labels on X/Y axis. Supported types are `number` and `date/time`.
  
  Example:
   ```python
   scale_x_datetime(format="%b %Y")
   scale_x_continuous(format='is {.2f}')
   ```

   Demo: [Formatting demo](https://nbviewer.jupyter.org/github/JetBrains/lets-plot-docs/blob/master/source/examples/cookbook/formatting_axes_etc.ipynb)
       
   See also: [Formatting](https://lets-plot.org/pages/formats.html)

### Changed
                
- [**breaking change**] New Geocoding API!

  Since Lets-Plot v2.0.0 the peviouse Geocoding API is no longer working (hence we bumped the version to 2.0.0). 
  
  The old version of the Geocoding backend remains on-line for a couple of release cycles
  to continue support of prior versions of Lets-Plot.
  
  To learn more about Geocoding API see: [Geocoding](https://lets-plot.org/pages/geocoding.html).
                                    

- The project upgraded to Kotlin v1.4.21 (was v1.4.10).

### Fixed

- Kernel density estimate computation is too slow.
- Serialize NaN and other special values as None (CRITICAL for the "Lets-Plot in SciView" PyCharm plugin) [[#282](https://github.com/JetBrains/lets-plot/issues/282)].
- Error in plot building: 'trans' parameter not compatible with None values in data [[#287](https://github.com/JetBrains/lets-plot/issues/287)].
- LOESS smoothing fails on small (<3) number points.
- Facet grid truncated in jupyter [[#28](https://github.com/JetBrains/lets-plot-kotlin/issues/28)].
- The `reverse` parameter on discrete scale with 'limits' [[#303](https://github.com/JetBrains/lets-plot/issues/289)].
- Geocoder's `allow_ambiguous()` doesn't prevent "ValueError: Multiple objects.." [[#174](https://github.com/JetBrains/lets-plot/issues/174)].
- Fix tooltip for overlapping objects [[#230](https://github.com/JetBrains/lets-plot/issues/230)].
- Fix duplicate values in tooltip [[#280](https://github.com/JetBrains/lets-plot/issues/280)].
- geom_histogram should not try to handle geometries in GeoDataFrame [[#281](https://github.com/JetBrains/lets-plot/issues/281)].
- Error building plot: Layer 'MAP' is not supported on Live Map. [[#285](https://github.com/JetBrains/lets-plot/issues/285)].
- Align title to the left of the plot geom area [[#289](https://github.com/JetBrains/lets-plot/issues/289)].
- Tooltip on `geom_ribbon()`.

## [1.5.6] - 2020-12-23

### Fixed
- “Symbol not found: _NSGenericException” error on macOS [[#276](https://github.com/JetBrains/lets-plot/issues/276)].

## [1.5.5] - 2020-12-18

### Added
- Correlation plot.
   
   See: [Charts](https://lets-plot.org/pages/charts.html).

- 'No Javascript' mode.

  Support for notebook renderers that don't execute Javascript.
  
  See: ['No Javascript' mode](https://lets-plot.org/pages/no_js_and_offline_mode.html#no-javascript-mode)

- In tooltip customization API:
   - options: `center` and `middle` (anchor).
   - option 'minWidth'.
   
   See: [Tooltip Customization](https://lets-plot.org/pages/tooltips.html).
   
- 'na_text' parameter in 'geom_text'

### Changed
- Tooltip customization API:
   - The `anchor` option moved from `theme` to `layer`:
     ```python                                                     
     geom_xxx(tooltips=layer_tooltips().anchor(anchor_value))
     ```
        where `anchor_value`: 
        `['top_right'|'top_center'|'top_left'|'bottom_right'|'bottom_center'|'bottom_left'|'middle_right'|'middle_center'|'middle_left']`.

   See: [Tooltip Customization](https://lets-plot.org/pages/tooltips.html).
 
### Fixed
- Tooltip should appear when the mapped data is continuous [[#241](https://github.com/JetBrains/lets-plot/issues/241)]
- Tooltip 'null' displayed for undefined vals [[#243](https://github.com/JetBrains/lets-plot/issues/243)]
- Y-tooltip should be aligned with a tile center [[#246](https://github.com/JetBrains/lets-plot/issues/246)]
- With `facet_grid` tooltip shows data from last plot on all plots [[#247](https://github.com/JetBrains/lets-plot/issues/247)]

## [1.5.4] - 2020-11-19
### Changed
- [**breaking change**] In functions `format(field, format)` and `line(template)` in 
tooltip builder, the '$' symbol is no longer used in aesthetic reference. It was replaced by the '^' (hat) symbol 
(see the udated doc: [Tooltip Customization](https://lets-plot.org/pages/tooltips.html)).
- The project upgraded to Kotlin v1.4.10 (was v1.3.72).
 
### Fixed
- No tooltip on v-line [[#229](https://github.com/JetBrains/lets-plot/issues/229)]

## [1.5.3] - 2020-11-05
### Added
- Facilities for customization of tooltip contents and its position (see [Tooltip Customization](https://lets-plot.org/pages/tooltips.html)).
- `attribution` and other new parameters in the `maptiles_zxy()` function. 
- `label_format` parameter in the `geom_text()` function.
- `scale_x_discrete_reversed()` / `scale_y_discrete_reversed()`
- `ggsave()` function (only SVG, HTML)

### Fixed
- Mercator's projection computes coordinates incorrectly (`geom_map(), coord_map()`)
- Handling of `xlim,ylim` in `coord_fixed(), coord_map()`
- Colorbars: take in account limits defined in continuous color/fill scales.
- `scale_color_hue` incorrectly interprets chroma range [[#206](https://github.com/JetBrains/lets-plot/issues/206)].
- `scale_color_brewer` shouldn't fail if 'type' is not specified [[#203](https://github.com/JetBrains/lets-plot/issues/203)].
- `scale_fill_discrete` should not replace the default discrete scale (Brewer) with 'HUE' scale [[#172](https://github.com/JetBrains/lets-plot/issues/172)].
 
### Changed
- Lets-plot vector map-tiles are configured by default.
- PACIFIC_BLUE color (#118ED8) replaced DARK_BLUE as a default color/fill value on plots.
- Default discrete color scheme is now ColorBrewer's Set2 (was Dark2)
 
### Deprecated
- Function `gg_image_matrix()`. The new function is: `image_matrix()` in the `lets_plot.bistro.im` module.
   The new function is not any longer displaying matrix by itself but returns a GGBunch object instead. 
   Unlike the depricated `gg_image_matrix()` function, it is not imported by `from lets_plot import *` statement.
    
   Usage: 
   ```
   from lets_plot.bistro.im import image_matrix
   image_matrix()
   ```    

## [1.5.2] - 2020-08-10
### Fixed
- map_titanic.ipynb : UnboundLocalError: local variable 'map_join' referenced before assignment [[#182](https://github.com/JetBrains/lets-plot/issues/182)]
- Add the `map_join` parameter to all geoms which support `map` parameter (py) [[#183](https://github.com/JetBrains/lets-plot/issues/183)]

## [1.5.1] - 2020-08-06
### Added
- scale_x_reverse() / scale_y_reverse()
- Mnemonics for the `resolution` parameter in the `regions.boundaries()` function. 

### Fixed
- Exception when using `coord_map` with `xlim` [[#173](https://github.com/JetBrains/lets-plot/issues/173)]
- Clipped tooltip [[#155](https://github.com/JetBrains/lets-plot/issues/155)]
- Text on axis is clipped after hiding the tick-marks in theme [[#160](https://github.com/JetBrains/lets-plot/issues/160)]
- Tooltip on x axis with no title shouldn't be above the axis line [[#161](https://github.com/JetBrains/lets-plot/issues/161)]
- NPE while creating scale mapper when data series contains only nulls.
- Default Geocoding server url.
 
## [1.5.0] - 2020-07-15
### Added
- geocoding package
 
### Fixed 
- NPE on geom_tile when data contains null-s.
- The order of values in the `limits` parameter on discrete scales is ignored.
- Livemap is not shown in GGBunch.
 
## [1.4.2] - 2020-05-28
### Added
- Plugin for IntelliJ IDEA / PyCharm is available. For more info see the plugin homepage: [Lets-Plot in SciView](https://plugins.jetbrains.com/plugin/14379-lets-plot-in-sciview).  

### Changed
- Plots build time reduced (up to 4X)

## [1.4.1] - 2020-05-20
### Added
- `geom_smooth`: automatic sampling when n>1000 and the method is LOESS.
 
### Fixed 
- 'map_join' fails when the `map` argument is a dictionary [[#130](https://github.com/JetBrains/lets-plot/issues/130)]
 
### Changed
- HTML output was modified allow better integration with PyCharm.
 
### Removed
- The deprecated method 'load_lets_plot_js()'.  

## [1.4.0] - 2020-05-13
### Added
- Support for interactive maps.
- The `as_discrete()` function.
- Polynomial regression of an arbitrary degree (the`lm` smoothing method) in *geom_smooth*.
- `axis_tooltip`, `axis_tooltip_x`, `axis_tooltip_y` parameters in `theme()`
- Kaggle and Datalore notebook demos.

### Fixed
- Severe performance degradation when using discrete scales [[#119](https://github.com/JetBrains/lets-plot/issues/119)].
- Opaque background to better support PyCharm dark theme [[#121](https://github.com/JetBrains/lets-plot/issues/121)].
- Auto-detection of a Kaggle environment.
- Limits on the maximum plot size [[#115](https://github.com/JetBrains/lets-plot/issues/115)].
- Installation instructions for Windows users [[#118 MinGW](https://github.com/JetBrains/lets-plot/issues/118)].

### Changed
- More slick shape for tooltips on the axis.

## [1.3.0] - 2020-03-26
### Added
- Python 3.6 support.
- Windows platform support.
- SVG/HTML export to file.
- Offline mode for Jupyter notebooks.
- Support for cloud-based notebooks like Google Colab and Datalore.
- JVM Maven artefacts released.
 
 ### Fixed
- Tooltip on `geom_rect` and `geom_vline`.
- Error when date-time series contains `NaT` value.
 
 ### Changed
- Optional `load_lets_plot_js()` function is now deprecated.
- Initialisation call `LetsPlot.setup_html()` is now mandatory in Jupyter.

## [1.2.1] - 2020-02-13
### Fixed
- tooltip not showing in geom_polygon

## [1.2.0] - 2020-02-12
### Added
- *geopandas* support in geoms: point, path, polygon, rect, text.
- support for LOESS smoothing method in *geom_smooth*.
- new geometry layers: *geom_crossbar*, *geom_linerange*, *geom_pointrange*, *geom_bin2d*.
- support for coordinate stystem *xlim*, *ylim* parameters (i.e. "clipping").

### Fixed
- Was not working `weight` parameter in stats: bin, count, bin2d, density, density2d
- incorrect tooltip line 'NaN' in geom_boxplot.
- ambiguous tooltip positioning in geoms: tile, bin2d.
- cropped text in tooltip.

## [1.1.0] - 2019-12-17
### Added
- *GGBunch*. Combines several different plots into one graphical object.
- *geom_image()*. Displays an image specified by ndarray with shape (n,m) or (n,m,3) or (n,m,4). 
- *gg_image_matrix()*. A utility helping to combine several images into one graphical object.
- user_guide.ipynb
- ggbunch.ipynb  
- scatter_matrix.ipynb
- image_101.ipynb
- image_fisher_boat.ipynb
- image_matrix.ipynb

### Changed
- Switched to Kotlin 1.3.61

### Fixed
- *scale_datetime()*. Date-time formatting in tooltips.
- Links in README_PYTHON.md

## [1.0.0] - 2019-11-27
### Changed
 - First public release.
