## [4.8.0] - 2025-11-dd

### Added

- `geom_pointdensity()` [[#1370](https://github.com/JetBrains/lets-plot/issues/1370)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/geom_pointdensity.ipynb).

 - Geoms with 1-to-1 statistics (such as `geom_qq()`, `geom_sina()`) preserve the mapping to original data after statistical transformation.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/stat_data_bijection.ipynb).

- `ggtb()`: `size_zoomin` and `size_basis` parameters for geometry scaling. [[#1369](https://github.com/JetBrains/lets-plot/issues/1369)]
  
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/ggtb_size_zoomin.ipynb).

- `geom_histogram()`: parameter `breaks`. [[#1382](https://github.com/JetBrains/lets-plot/issues/1382)]

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/geom_histogram_param_breaks.ipynb).

- Plot Layout:
  - The legend automatically wraps to prevent overlap - up to 15 rows for vertical legends and 5 columns for horizontal ones [[#1235](https://github.com/JetBrains/lets-plot/issues/1235)].

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/legend_wrap.ipynb).

  - `guides` parameter in `gggrid()`.

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/gggrid_legend_collect.ipynb).

- Plot Theme:
  - `flavor_standard()` sets the theme's default color scheme [[#1277](https://github.com/JetBrains/lets-plot/issues/1277)]. <br>
    Use to override other flavors or make defaults explicit.

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/flavor_standard.ipynb).

  - `theme_gray()` as an alias for `theme_grey()`.

  - `legend_justification` parameter of `theme()` accepts additional string values: `'left'`, `'right'`, `'top'`, and `'bottom'`.

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/legend_justification.ipynb).

  - Support for inward axis ticks.

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/axis_tick_direction.ipynb).

- Markdown:
  -  Support for `target` attribute for links.
  -  Links now open in a new tab by default [[#1397](https://github.com/JetBrains/lets-plot/issues/1397)].


### Changed

- [**BREAKING**] Explicit `group` aesthetic now overrides default grouping behavior instead of combining with it [[#1401](https://github.com/JetBrains/lets-plot/issues/1401)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/group_override_defaults.ipynb).
  > [!IMPORTANT]
  > Previously, setting `group='variable'` would group by both the explicit variable AND any discrete
  > aesthetics (color, shape, etc.). \
  > Now it groups ONLY by the explicit variable, matching `ggplot2` behavior. \
  > Use `group=[var1, var2, ...]` to group by multiple variables explicitly, \
  > and `group=[]` to disable any grouping. 

- `theme`: the `exponent_format` default value changed to `'pow'` - superscript powers of 10 (was e-notation).

- Plot Nothing when encountering NaN [[#818](https://github.com/JetBrains/lets-plot/issues/818)].
  
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/geom_path_with_breaks_at_NaN.ipynb).

- The multi-layer line plot now shows tooltips for each series simultaneously, in the same way that a single-layer plot with color mapped to series does.
- `map_join` now has a higher priority and overrides positional mappings.
- `geom_pie` now groups data by coordinates when no explicit `group` aesthetic is provided.
- Plot with scale_y_log10() is distorting the value and producing the wrong plot. [[#1406](https://github.com/JetBrains/lets-plot/issues/1406)]

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/geom_ribbon_with_NaN.ipynb).
- geom_area: Handling NaN values. geom_area() will now break if y includes NaN values.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25e/geom_area_with_NaN.ipynb).
  

### Fixed

- geom_density2d: NullPointerException when weight aesthetic contains None values [[#1399](https://github.com/JetBrains/lets-plot/issues/1399)].
- Tooltip shows duplicate lines when as_discrete is applied twice to the same var [[#1400](https://github.com/JetBrains/lets-plot/issues/1400)].
- geom_sina: incorrect shape in legend [[#1403](https://github.com/JetBrains/lets-plot/issues/1403)].
- geom_density2d: Incorrect processing of weighted statistics when None value occurs in the x or y column.
- facet_wrap: indescriptive error when the specified facet variable is not present in the dataset [[#1409](https://github.com/JetBrains/lets-plot/issues/1409)].
- Integer numbers in facet strip titles are displayed as float [[#1386](https://github.com/JetBrains/lets-plot/issues/1386)].
- Error when using scale_identity(aesthetic="shape") [[#1212](https://github.com/JetBrains/lets-plot/issues/1212)].
- ggsave: theme option face="italic" doesn't work [[#1391](https://github.com/JetBrains/lets-plot/issues/1391)].
- Fail early if string format is incorrect [[#1410](https://github.com/JetBrains/lets-plot/issues/1410)].