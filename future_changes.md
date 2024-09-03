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
- Legend icon background should inherit the fill color of the plot panel (i.e. grey when `theme_grey` is used).
