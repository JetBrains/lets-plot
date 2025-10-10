## [4.7.4] - 2025-mm-dd

### Added

- `ggtb()`: `size_zoomin` and `size_basis` parameters for geometry scaling. [[#1369](https://github.com/JetBrains/lets-plot/issues/1369)]
  
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/ggtb_size_zoomin.ipynb).

- `flavor_standard()` sets the theme's default color scheme [[#1277](https://github.com/JetBrains/lets-plot/issues/1277)]. <br>
  Use to override other flavors or make defaults explicit.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/flavor_standard.ipynb).
  
- `theme_gray()` as an alias for `theme_grey()`.

- `legend_justification` parameter of `theme()` accepts additional string values: `'left'`, `'right'`, `'top'`, and `'bottom'`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/legend_justification.ipynb).
- markdown: `target` attribute for links.


### Changed

- [**BREAKING**] Explicit `group` aesthetic now overrides default grouping behavior instead of combining with it [[#1401](https://github.com/JetBrains/lets-plot/issues/1401)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/group_override_defaults.ipynb).
> [!IMPORTANT]
> Previously, setting `group='variable'` would group by both the explicit variable AND any discrete
> aesthetics (color, shape, etc.). \
> Now it groups ONLY by the explicit variable, matching `ggplot2` behavior. \
> Use `group=[var1, var2, ...]` to group by multiple variables explicitly, \
> and `group=[]` to disable any grouping. 

- Geoms with 1-to-1 statistics (`geom_qq()`, `geom_sina()`) now keep bijection with original data for aesthetics.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/stat_data_bijection.ipynb).
- markdown: links now open in a new tab by default [[#1397](https://github.com/JetBrains/lets-plot/issues/1397)].

### Fixed

- geom_density2d: NullPointerException when weight aesthetic contains None values [[#1399](https://github.com/JetBrains/lets-plot/issues/1399)].
- Tooltip shows duplicate lines when as_discrete is applied twice to the same var [[#1400](https://github.com/JetBrains/lets-plot/issues/1400)].