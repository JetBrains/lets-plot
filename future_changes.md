## [4.0.0] - 2023-mm-dd

### Added

- New layer `stat_summary()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/stat_summary.ipynb).


- New layer `stat_summary_bin()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/stat_summary_bin.ipynb).


- New layer `geom_function()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/geom_function.ipynb).


- Tooltips for `geom_step()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/geom_step_tooltips.ipynb).


- In tooltip customization API:
  `disable_splitting()` function to hide side tooltips [[LPK-189](https://github.com/JetBrains/lets-plot-kotlin/issues/189)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/tooltips_disable_splitting.ipynb).


- Variadic lines with `size` and `color` mapping in `geom_line()` and `geom_path()`.  
See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/aes_size_color_variadic_lines.ipynb).


- `geom_pie()`:
  - `stroke` and `color` aesthetics - the width and color of the pie sector arcs.
  - `stroke_side` parameter - which arcs should have a stroke (inner, outer, both).
  - `spacer_width` and `spacer_color` parameters - lines between sectors.

  The `stroke_color` parameter is no longer supported.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/geom_pie_stroke_and_spacers.ipynb).


- New named system colors: "pen", "paper", "brush".

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/named_system_colors.ipynb).



### Changed

- [BREAKING] `geom_boxplot()` no longer support parameter `sampling`.

- [BREAKING] `geom_pointrange()`: `size` aesthetic shouldn't affect line width [[#751](https://github.com/JetBrains/lets-plot/issues/751)]:

  `linewidth` aesthetic is used for line width, `size` - for mid-point size only.

- Reduce the default `width`/`height` values for `geom_errorbar()`.

- Change the default `position` value from `'identity'` to `'dodge'` for `geom_errorbar()`, `geom_pointrange()` and `geom_linerange()`.

- `geom_boxplot()`: the `size` and `stroke` parameters started to affect outliers.


### Fixed

- ggsave: saving geomImshow() to SVG produces fuzzy picture [[LPK-188](https://github.com/JetBrains/lets-plot-kotlin/issues/188)].
- ggsave: saving geomImshow() to raster format produces fuzzy picture.
- geom_livemap: memory leak when re-run cells without reloading a page.
- Fix placement of horizontal tooltips: when there is not enough height for all tooltips, the nearest one should be used.
- `geom_path` doesn't support different colors for segments [[#313](https://github.com/JetBrains/lets-plot/issues/313)].
- Weird tooltip/legend in case of extremely long value [[#315](https://github.com/JetBrains/lets-plot/issues/315)].
- Add `stat_summary`, `stat_summary_bin` to ggplot [[#316](https://github.com/JetBrains/lets-plot/issues/316)].
- `geom_boxplot()` doesn't apply alpha to outliers [[#754](https://github.com/JetBrains/lets-plot/issues/754)].
- ggsave() doesn't save geom_raster() layer to a file [[#778](https://github.com/JetBrains/lets-plot/issues/778)].
- geom_crossbar aesthetics take `middle` argument instead of `y` [[#804](https://github.com/JetBrains/lets-plot/issues/804)].
- panning on interactive map should be more responsive [[#336](https://github.com/JetBrains/lets-plot/issues/336)].

