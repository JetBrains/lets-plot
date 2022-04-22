## [2.3.1] - 2022-??-??

### Added

- Global theme configuring with `plot_theme(...)` function.  
See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/notebooks/plot_theme.ipynb).

- `geom_livemap`: support of arrows drawing for segments.

### Changed
- New style of tooltip color marker (two side bars with a stroke color)
  and symbols in legend (rectangle with a stroke instead of a slash-line).

### Fixed

- Differences in tooltip color marker for plots with and without livemap.
- Labels out of plot when axis_text_y='blank' [[#525](https://github.com/JetBrains/lets-plot/issues/525)]
- NPE in corr_plot with null coefficients.