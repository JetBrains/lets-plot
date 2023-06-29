## [3.2.1] - 2023-mm-dd

### Added

- New layer `stat_summary()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/stat_summary.ipynb).


- Tooltips for `geom_step()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/geom_step_tooltips.ipynb).


- In tooltip customization API:
  `disable_splitting()` function to hide side tooltips [[LPK-189](https://github.com/JetBrains/lets-plot-kotlin/issues/189)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/tooltips_disable_splitting.ipynb).


### Changed

- [BREAKING] `geom_boxplot()` no longer support parameter `sampling`.

- [BREAKING] `geom_pointrange()`: `size` aesthetic shouldn't affect line width [[#751](https://github.com/JetBrains/lets-plot/issues/751)]:

  `linewidth` aesthetic is used for line width, `size` - for mid-point size only.

- Reduce the default `width`/`height` values for `geom_errorbar()`.


### Fixed

- ggsave: saving geomImshow() to SVG produces fuzzy picture [[LPK-188](https://github.com/JetBrains/lets-plot-kotlin/issues/188)].
- ggsave: saving geomImshow() to raster format produces fuzzy picture.
- geom_livemap: memory leak when re-run cells without reloading a page.
- Fix placement of horizontal tooltips: when there is not enough height for all tooltips, the nearest one should be used.
- Weird tooltip/legend in case of extremely long value [[#315](https://github.com/JetBrains/lets-plot/issues/315)].
