## [4.4.2] - 2024-mm-dd

### Added

- `base` parameter in `waterfall_plot()` [[#1159](https://github.com/JetBrains/lets-plot/issues/1159)]:

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24g/waterfall_plot_base.ipynb).

### Changed
 
### Fixed
- Better Marimo Support (via [PR-2084](https://github.com/marimo-team/marimo/pull/2084)) [[#1018](https://github.com/JetBrains/lets-plot/issues/1018)].
- waterfall_plot: faceting doesn't work without a measure mapping [[#1152](https://github.com/JetBrains/lets-plot/issues/1152)].
- waterfall_plot: tooltips doesn't work with column names from original dataset [[#1153](https://github.com/JetBrains/lets-plot/issues/1153)].
- Legend icon background is not transparent when `legend_background` is set to "blank" in `theme` [[#1167](https://github.com/JetBrains/lets-plot/issues/1167)].
- Unable to display or save graph when using geom_path [[#1168](https://github.com/JetBrains/lets-plot/issues/1168)].
- Legend icon background should inherit the fill color of the plot panel (i.e. grey when `theme_grey` is used).
- Vertex sampling uses different tolerances for objects within the same plot [[#1174](https://github.com/JetBrains/lets-plot/issues/1174)].
- sampling_vertex_vw doesn't work as expected [[#1175](https://github.com/JetBrains/lets-plot/issues/1175)].
- sampling_vertex_dp may break rings [[#1176](https://github.com/JetBrains/lets-plot/issues/1176)].