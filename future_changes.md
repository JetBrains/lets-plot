## [4.4.2] - 2024-mm-dd

### Added
                 
- `geom_blank()` [[#831](https://github.com/JetBrains/lets-plot/issues/831)].

- `expand_limits()` [[#820](https://github.com/JetBrains/lets-plot/issues/820)].

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24g/expand_limits.ipynb).

- `base` parameter in `waterfall_plot()` [[#1159](https://github.com/JetBrains/lets-plot/issues/1159)].

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24g/waterfall_plot_base.ipynb).

- `check_overlap` parameter for `geom_text()` and `geom_label()`.

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24g/check_overlap.ipynb).
       
- `marginal` parameter in `qq_plot()`:

    See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24g/qq_plot_marginal.ipynb).

- `inherit_aes` parameter in layers [[#1172](https://github.com/JetBrains/lets-plot/issues/1172)].

- Support for 3-character hex color codes     

### Changed

- Compact format of scientific notation; new values for the `exponent_format` parameter of `theme()` [[#1071](https://github.com/JetBrains/lets-plot/issues/1071)].

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24g/scientific_notation_table.ipynb).

### Fixed
- Better Marimo Support (via [PR-2084](https://github.com/marimo-team/marimo/pull/2084)) [[#1018](https://github.com/JetBrains/lets-plot/issues/1018)].
- Support layering `aes()` multiple times [[#822](https://github.com/JetBrains/lets-plot/issues/822)].
- waterfall_plot: faceting doesn't work without a measure mapping [[#1152](https://github.com/JetBrains/lets-plot/issues/1152)].
- waterfall_plot: tooltips doesn't work with column names from original dataset [[#1153](https://github.com/JetBrains/lets-plot/issues/1153)].
- Legend icon background is not transparent when `legend_background` is set to "blank" in `theme` [[#1167](https://github.com/JetBrains/lets-plot/issues/1167)].
- Unable to display or save graph when using geom_path [[#1168](https://github.com/JetBrains/lets-plot/issues/1168)].
- Legend icon background should inherit the fill color of the plot panel (i.e. grey when `theme_grey` is used).
- Vertex sampling uses different tolerances for objects within the same plot [[#1174](https://github.com/JetBrains/lets-plot/issues/1174)].
- sampling_vertex_vw doesn't work as expected [[#1175](https://github.com/JetBrains/lets-plot/issues/1175)].
- sampling_vertex_dp may break rings [[#1176](https://github.com/JetBrains/lets-plot/issues/1176)].
- ggmarginal: bottom boxplot is broken [[#1189](https://github.com/JetBrains/lets-plot/issues/1189)].