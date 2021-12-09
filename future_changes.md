## [2.2.1] - 2021-12-??

### Added

- `scale_x_time()` and `scale_y_time()` [[#468](https://github.com/JetBrains/lets-plot/issues/468)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-21-12/notebooks/scale_time.ipynb).
       
- `plot_background, legend_background` parameters in `theme()` [[#485](https://github.com/JetBrains/lets-plot/issues/485)]. 
- `axis_ontop, axis_ontop_x, axis_ontop_y` parameters in `theme()`                                                                

### Fixed

- Coord system limits do not work with x/y scale with transform [[#474](https://github.com/JetBrains/lets-plot/issues/474)].
- Provide 0-23 hour formatting [[#469](https://github.com/JetBrains/lets-plot/issues/469)].
- No tooltip shown when I'm trying to add an empty line [[#382](https://github.com/JetBrains/lets-plot/issues/382)].
- `coord_fixed()` should adjust dimensions of "geom" panel accordingly [[#478](https://github.com/JetBrains/lets-plot/issues/478)].
- The tooltip dependence on number of factors works separately by layers [[#481](https://github.com/JetBrains/lets-plot/issues/481)].
- Tooltip on y-axis looks wrong [[#393](https://github.com/JetBrains/lets-plot/issues/393)].
- Is kotlin-reflect really needed for lets-plot? [[#471](https://github.com/JetBrains/lets-plot/issues/471)].