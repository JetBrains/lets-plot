## [4.1.1] - 2023-mm-dd

### Added

- Add `geom_spoke()` [[#738](https://github.com/JetBrains/lets-plot/issues/738)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/geom_spoke.ipynb).


- New variables computed by `'count'` and `'count2d'` statistics: `'..sumprop..'`, `'..sumpct..'`.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/new_stat_count_vars.ipynb).


- Using a dictionary as a value of `'labels'`, `'breaks'` ([[#169](https://github.com/JetBrains/lets-plot/issues/169)])
  and `'values'` ([[#882](https://github.com/JetBrains/lets-plot/issues/882)]) parameters in scaling functions:

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/scale_params_with_dict.ipynb).


- The `lablim` parameter for `scale_xxx()` functions [[#939](https://github.com/JetBrains/lets-plot/issues/939), [#946](https://github.com/JetBrains/lets-plot/issues/946)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/scale_lablim.ipynb).


- The `levels` parameter in `as_discrete` function [[#931](https://github.com/JetBrains/lets-plot/issues/931)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/factor_levels.ipynb).


- `geom_livemap()`: new tiles - `bw`.
 
  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/geom_livemap_bw_tiles.ipynb).


- `label_text` parameter in `theme()` for annotation text settings [[#930](https://github.com/JetBrains/lets-plot/issues/930)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/theme_label_text.ipynb).



- Formatting: add scientific superscript option [[#743](https://github.com/JetBrains/lets-plot/issues/743)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/superscript.ipynb).

### Changed

- The `plot_margin` parameter in `theme()` and the `margin` parameter in `element_text()` accept a number or a list of numbers:
  - a number or list of one number - the same margin it applied to **all four sides**;
  - a list of two numbers - the first margin applies to the **top and bottom**, the second - to the **left and right**;
  - a list of three numbers -  the first margin applies to the **top**, the second - to the **right and left**,
  the third - to the **bottom**;
  - a list of four numbers - the margins are applied to the **top, right, bottom and left** in that order.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/margins.ipynb).



### Fixed
- Jitter reproducibility in geom_jitter, position_jitter, position_jitterdodge [[#911](https://github.com/JetBrains/lets-plot/issues/911)].
- Facets: order = 0 doesn't work as expected [[#923](https://github.com/JetBrains/lets-plot/issues/923)].
- geom_livemap: fix missing styles (e.g. road outline on high zooms) [[#926](https://github.com/JetBrains/lets-plot/issues/926)].
- geom_livemap: freeze at zoom 10 [[#892](https://github.com/JetBrains/lets-plot/issues/892)].
- Enormous CPU / Time/ Memory consumption on some data [[#932](https://github.com/JetBrains/lets-plot/issues/932)].
- scale_x_log2(), scale_y_log2() as a shortcut for trans='log2' [[#922](https://github.com/JetBrains/lets-plot/issues/922)].
- export functions to export to a file-like object [[#885](https://github.com/JetBrains/lets-plot/issues/885)].
- How to calculate proportion of points with same coordinate [[#936](https://github.com/JetBrains/lets-plot/issues/936)].
- gggrid: composite plot is not visible if saved with ggsave [[#942](https://github.com/JetBrains/lets-plot/issues/942)].
- Make scale's 'breaks' / 'labels' parameters understand dict of breaks as keys and labels as values [[#169](https://github.com/JetBrains/lets-plot/issues/169)].
- scale manual: "values" parameter should accept dictionary as a value [[#882](https://github.com/JetBrains/lets-plot/issues/882)].
- as_discrete: add 'levels' parameter [[#931](https://github.com/JetBrains/lets-plot/issues/931)].
- Support for ordered categoricals [[#914](https://github.com/JetBrains/lets-plot/issues/914)].
- `scale_continuous()` fails with non-color aesthetics [[#953](https://github.com/JetBrains/lets-plot/issues/953)].
- NumberFormat: `g` format doesn't use e-notation for small numbers [[#965](https://github.com/JetBrains/lets-plot/issues/965)].
- Tooltips: graphical artifacts and bad performance in multi-line plot in Batik [[#967](https://github.com/JetBrains/lets-plot/issues/967)].