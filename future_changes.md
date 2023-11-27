## [4.1.1] - 2023-mm-dd

### Added

- New variables computed by `'count'` and `'count2d'` statistics: `'..sumprop..'`, `'..sumpct..'`.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/new_stat_count_vars.ipynb).


- Using a dictionary as a value of `'labels'`, `'breaks'` ([[#169](https://github.com/JetBrains/lets-plot/issues/169)])
  and `'values'` ([[#882](https://github.com/JetBrains/lets-plot/issues/882)]) parameters in scaling functions:

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/scale_params_with_dict.ipynb).


### Changed

### Fixed
- Jitter reproducibility in geom_jitter, position_jitter, position_jitterdodge [[#911](https://github.com/JetBrains/lets-plot/issues/911)].
- Facets: order = 0 doesn't work as expected [[#923](https://github.com/JetBrains/lets-plot/issues/923)].
- geom_livemap: fix missing styles (e.g. road outline on high zooms) [[#926](https://github.com/JetBrains/lets-plot/issues/926)].
- geom_livemap: freeze at zoom 10 [[#892](https://github.com/JetBrains/lets-plot/issues/892)].
- Enormous CPU / Time/ Memory consumption on some data [[#932](https://github.com/JetBrains/lets-plot/issues/932)].
- scale_x_log2(), scale_y_log2() as a shortcut for trans='log2' [[#922](https://github.com/JetBrains/lets-plot/issues/922)].
- How to calculate proportion of points with same coordinate [[#936](https://github.com/JetBrains/lets-plot/issues/936)].
- gggrid: composite plot is not visible if saved with ggsave [[#942](https://github.com/JetBrains/lets-plot/issues/942)].
- Make scale's 'breaks' / 'labels' parameters understand dict of breaks as keys and labels as values [[#169](https://github.com/JetBrains/lets-plot/issues/169)].
- scale manual: "values" parameter should accept dictionary as a value [[#882](https://github.com/JetBrains/lets-plot/issues/882)].