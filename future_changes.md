## [4.2.0] - 2023-12-dd

### Added

- Support for `Categoricals`:
  - Support for `pandas.Categorical` data type [[#914](https://github.com/JetBrains/lets-plot/issues/914)].
  - The `levels` parameter in `as_discrete()` function [[#931](https://github.com/JetBrains/lets-plot/issues/931)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/factor_levels.ipynb).


- Support for superscript for numbers in scientific notation [[#743](https://github.com/JetBrains/lets-plot/issues/743)].

  > #### Warning!
  >
  > Do NOT(!) use `exponent_format='pow'` if you are planning to export plot to a raster format (PNG,PDF).
  >
  > The `CairoSVG` library (which is under the hood of our `ggsave()` function) does not handre `tspan` element properly end breaks superscript notation when transforming SVG to PNG/PDF.
  >
  > More details: https://github.com/Kozea/CairoSVG/issues/317

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/superscript_exponent.ipynb).


- Support exporting plot to a file-like object. <br> 
  Convenience methods: `to_svg()`, `to_html()`, `to_png()`, `to_pdf()` [[#885](https://github.com/JetBrains/lets-plot/issues/885)], [[#590](https://github.com/JetBrains/lets-plot/issues/590)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/new_export_methods.ipynb).


- Sharing of X,Y-scale limits between subplots in `gggrid()` [[#718](https://github.com/JetBrains/lets-plot/issues/718)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/gggrid_scale_share.ipynb).


- `geom_spoke()` [[#738](https://github.com/JetBrains/lets-plot/issues/738)]. 
 
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/geom_spoke.ipynb).


- `scale_x_log2()`, `scale_y_log2()` [[#922](https://github.com/JetBrains/lets-plot/issues/922)].


- Hight-contrast tileset "BW" for `geom_livemap()`.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/geom_livemap_bw_tiles.ipynb).


- New variables computed by `'count'` and `'count2d'` statistics: `'..sumprop..'`, `'..sumpct..'` [[#936](https://github.com/JetBrains/lets-plot/issues/936)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/new_stat_count_vars.ipynb).


- Support using dictionaries for breaks/labels/values customization in `scale_xxx()` functions [[#169](https://github.com/JetBrains/lets-plot/issues/169)], [[#882](https://github.com/JetBrains/lets-plot/issues/882)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/scale_params_with_dict.ipynb).


- The `lablim` parameter in `scale_xxx()` functions [[#939](https://github.com/JetBrains/lets-plot/issues/939), [#946](https://github.com/JetBrains/lets-plot/issues/946)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/scale_lablim.ipynb).


- `label_text` parameter in `theme()` for annotation text settings [[#930](https://github.com/JetBrains/lets-plot/issues/930)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/theme_label_text.ipynb).


- NumberFormat: new flag `~` to trim trailing zeros.


### Changed
                
- [BREAKING] Function `margin()` is deprecated and will be removed in future releases. <br/>
  Please replace all existing usages, i.e. `theme(plot_margin=margin(..))` and `element_text(margin=margin(..))` <br/> 
  with a list or with just a number:
  - a number or list of one number - the same margin it applied to **all four sides**;
  - a list of two numbers - the first margin applies to the **top and bottom**, the second - to the **left and right**;
  - a list of three numbers -  the first margin applies to the **top**, the second - to the **right and left**,
  the third - to the **bottom**;
  - a list of four numbers - the margins are applied to the **top, right, bottom and left** in that order.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23f/margins.ipynb).
                                               

- Upgraded Apache Batik to version 1.17 [[#887](https://github.com/JetBrains/lets-plot/issues/887)]


### Fixed

- Jitter reproducibility in geom_jitter, position_jitter, position_jitterdodge [[#911](https://github.com/JetBrains/lets-plot/issues/911)].
- Facets: order = 0 doesn't work as expected [[#923](https://github.com/JetBrains/lets-plot/issues/923)].
- geom_livemap: fix missing styles (e.g. road outline on high zooms) [[#926](https://github.com/JetBrains/lets-plot/issues/926)].
- geom_livemap: freeze at zoom 10 [[#892](https://github.com/JetBrains/lets-plot/issues/892)].
- Enormous CPU / Time/ Memory consumption on some data [[#932](https://github.com/JetBrains/lets-plot/issues/932)].
- gggrid: composite plot is not visible if saved with ggsave [[#942](https://github.com/JetBrains/lets-plot/issues/942)].
- gggrid doesn't override global theme [[#966](https://github.com/JetBrains/lets-plot/issues/966)].
- `scale_continuous()` fails with non-color aesthetics [[#953](https://github.com/JetBrains/lets-plot/issues/953)].
- NumberFormat: `g` format doesn't use e-notation for small numbers [[#965](https://github.com/JetBrains/lets-plot/issues/965)].
- Tooltips: graphical artifacts and bad performance in multi-line plot in Batik [[#967](https://github.com/JetBrains/lets-plot/issues/967)].
- Wrong tooltip position on `geom_segment()` with position adjustment [[#963](https://github.com/JetBrains/lets-plot/issues/963)].