## [2.3.1] - 2022-??-??

### Added

- `LetsPlot.set_theme()` - configuring a default plot theme.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/default_theme.ipynb).

- `geom_livemap`: support of the `arrow` parameter in `geom_segment` [[#525](https://github.com/JetBrains/lets-plot/issues/131)].
      
- Parameter `orientation` in geoms: `bar, boxplot, density, histogram, freqpoly, smooth, violin`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/y_orientation.ipynb).

### Changed

- New style of tooltip color marker (two sidebars with a stroke color)
  and symbols in legend (rectangle with a stroke instead of a slash-line).

### Fixed

- Differences in tooltip color marker for plots with and without livemap.
- Labels out of plot when axis_text_y='blank' [[#525](https://github.com/JetBrains/lets-plot/issues/525)].
- NPE in corr_plot with null coefficients.
- Outliers are not shown when boxplot' alpha=0.