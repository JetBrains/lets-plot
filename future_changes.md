## [2.5.2] - 2022-??-??

### Added

- `geom_imshow()`:

    - Improved performance by orders of magnitude
    - Transparency of `NaN` values in grayscale images [[#631](https://github.com/JetBrains/lets-plot/issues/631)]. 
See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/image_nan_values.ipynb).

    - `alpha` parameter [[#630](https://github.com/JetBrains/lets-plot/issues/630)]. 
See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/image_alpha_param.ipynb).

- New variables computed by `'count'` and `'count2d'` statistics: `'..sum..'`, `'..prop..'`, `'..proppct..'`.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/stat_count(2d)_vars.ipynb).


### Changed
        
- Java/Swing platf.: Apache Batik upgraded to v.1.16 [[#624](https://github.com/JetBrains/lets-plot/issues/624)], [[LPK #140](https://github.com/JetBrains/lets-plot-kotlin/issues/140)].

### Fixed

- Themes: can't change plot background after applying a "flavor" [[#623](https://github.com/JetBrains/lets-plot/issues/623)].
- Layout: uneven left/right, top/bottom plot margins [[#625](https://github.com/JetBrains/lets-plot/issues/625)].
- A plot building error with empty data on various geoms.
- Precision error in gradient [[#634](https://github.com/JetBrains/lets-plot/issues/634)].