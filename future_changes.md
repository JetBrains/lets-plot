## [4.0.0] - 2023-mm-dd

### Added

- Flavor-dependent colors: **pen**, **brush** and **paper**
  - By default, all geometries utilize new flavor-dependent colors.
  - Theme `geom` parameter allowing redefinition of "geom colors":  `theme(geom=element_geom(pen, brush,paper))`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/geom_theme_colors.ipynb).


- `stat_summary()` : 
  [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/stat_summary.ipynb).

- `stat_summary_bin()` :
  [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/stat_summary_bin.ipynb).

- `stat_ecdf()` :
  [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/stat_ecdf.ipynb). 

- `geom_function()` :
  [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/geom_function.ipynb).

- `theme_void()` [[#830](https://github.com/JetBrains/lets-plot/issues/830)] :
  [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/theme_void.ipynb).

- `"sum"` statistic [[#821](https://github.com/JetBrains/lets-plot/issues/821)]:
  [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/stat_sum.ipynb).
                                  
- `"boxplot_outlier"` statistic: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/stat_boxplot_outlier.ipynb).
          
                     
- Support for variadic line width and/or color in `geom_line()` and `geom_path()` [[#313](https://github.com/JetBrains/lets-plot/issues/313)].

  [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/aes_size_color_variadic_lines.ipynb). 
      

- In tooltip customization API:\
  `disable_splitting()` function [[LPK-189](https://github.com/JetBrains/lets-plot-kotlin/issues/189)].

  [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/tooltips_disable_splitting.ipynb).


- In `geom_pie()`:
  - `stroke` and `color` aesthetics - the width and color of pie sector arcs.
  - `stroke_side` parameter - which arcs to show (inner, outer, both).
  - `spacer_width` and `spacer_color` parameters - lines between sectors.
  
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/geom_pie_stroke_and_spacers.ipynb).

  - `size_unit` parameter : [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/geom_pie_size_unit.ipynb).


### Changed

- The default qualitative color palette is now [Color Brewer "Set1"](https://colorbrewer2.org/#type=qualitative&scheme=Set1&n=9) (was ["Set2"](https://colorbrewer2.org/#type=qualitative&scheme=Set2&n=8))
- Geometries default colors are now flavor-dependent: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/geom_theme_colors.ipynb).
- Geometries default size/line-width is now slightly bigger.
- Point size is adjusted to match the width of a line of the same "size".

- [BREAKING] `geom_pie()` no longer supports parameter `stroke_color`.

- [BREAKING] `geom_boxplot()` no longer support parameter `sampling`.

- [BREAKING] `geom_pointrange()`: `size` aesthetic shouldn't affect line width [[#751](https://github.com/JetBrains/lets-plot/issues/751)]:

  `linewidth` aesthetic is used for line width, `size` - for mid-point size only.

- `geom_boxplot()`: `size` and `stroke` parameters now affect outlier shapes.

- [BREAKING, only affects users of Lets-Plot Kotlin API] \
  The entire project has undergone package refactoring.\
  As a result of package names changed, this Lets-Plot v4.0.0 is only partially compatible \
  with the current Lets-Plot Kotlin API v4.4.1.


### Fixed

- `ggsave()` doesn't save geom_raster() layer to a file [[#778](https://github.com/JetBrains/lets-plot/issues/778)].
- `ggsave()`: saving geomImshow() to SVG produces fuzzy picture [[LPK-188](https://github.com/JetBrains/lets-plot-kotlin/issues/188)].
- `ggsave()`: saving geomImshow() to raster format produces fuzzy picture.
- `geom_crossbar()` aesthetics take `middle` argument instead of `y` [[#804](https://github.com/JetBrains/lets-plot/issues/804)].
- `geom_boxplot()` doesn't apply alpha to outliers [[#754](https://github.com/JetBrains/lets-plot/issues/754)].
- `geom_boxplot()` outliers do not show tooltips.
- `geom_step()` no tooltips.
- `geom_step()`: toggle the behavior of the `direction` parameter when the orientation is changed.
- `geom_livemap()`: memory leak when re-run cells without reloading a page.
- `geom_ribbon()`: not all tooltips are shown on a multi-layer plot [[#847](https://github.com/JetBrains/lets-plot/issues/847)].
- Bug in empty plot: IndexOutOfBoundsException [[#194](https://github.com/JetBrains/lets-plot-kotlin/issues/194)].
- Weird tooltip/legend in case of extremely long value [[#315](https://github.com/JetBrains/lets-plot/issues/315)].
- panning on interactive map should be more responsive [[#336](https://github.com/JetBrains/lets-plot/issues/336)].
- Offline mode doesn't work with manylinux wheels [[#808](https://github.com/JetBrains/lets-plot/issues/808)].
