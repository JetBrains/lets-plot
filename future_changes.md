## [3.0.0] - 2022-12-??

### Added

- Python wheel for Python 3.11.

- `geom_imshow()`:

    - Improved performance by orders of magnitude.

    - Transparency of `NaN` values in grayscale images [[#631](https://github.com/JetBrains/lets-plot/issues/631)]. 
See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/image_nan_values.ipynb).

    - `alpha` parameter [[#630](https://github.com/JetBrains/lets-plot/issues/630)]. 
See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/image_alpha_param.ipynb).

- `geom_violin()`:

  - `tails_cutoff` parameter.
See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/violin_tails_cutoff.ipynb).

- New 'bistro' plot - `residual_plot()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/residual_plot.ipynb). 

- New geometry `geom_area_ridges()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/ridgeline_plot.ipynb).

- New geometry `geom_pie()`.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/geom_pie.ipynb).

- Annotations for pie chart:

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/annotations_for_pie.ipynb).

- New variables computed by `'count'` and `'count2d'` statistics: `'..sum..'`, `'..prop..'`, `'..proppct..'`.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/stat_count(2d)_vars.ipynb).
        
- Static maps:

  - The value "provided" for `use_crs` parameter. 

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/projection_provided.ipynb).

- The `flat` parameter for `geom_path()` and `geom_segment()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/flat_param.ipynb).

### Changed

- [BREAKING] Dropped support for Python 3.6 as it is in the ["end-of-life"](https://devguide.python.org/versions/) of its release cycle. 
- [BREAKING] `geom_livemap()` itself no longer draws geometries, so the following options are not expected:
  `symbol`, `data`, `mapping`, `map`, `map_join`, `ontop`, `stat`, `position`, `show_legend`, `sampling`, `tooltips`.
  To draw points and pies, please, use the `geom_point()` and `geom_pie()` functions.
- Java/Swing platf.: Apache Batik upgraded to v.1.16 [[#624](https://github.com/JetBrains/lets-plot/issues/624)], [[LPK #140](https://github.com/JetBrains/lets-plot-kotlin/issues/140)].
- The default size is increased for the plot title and decreased for the caption.
- Upgraded Kotlin version to 1.7.21 (was 1.7.20).

### Fixed

- Themes: can't change plot background after applying a "flavor" [[#623](https://github.com/JetBrains/lets-plot/issues/623)].
- Layout: uneven left/right, top/bottom plot margins [[#625](https://github.com/JetBrains/lets-plot/issues/625)].
- A plot building error with empty data on various geoms.
- Precision error in gradient [[#634](https://github.com/JetBrains/lets-plot/issues/634)].
- geom_livemap: wrong position when datapoints geodesic line goes close to the N.P. [[#645](https://github.com/JetBrains/lets-plot/issues/645)].