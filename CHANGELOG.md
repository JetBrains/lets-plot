# Lets-Plot Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [4.2.0] - 2023-12-28

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


- High-contrast tileset "BW" for `geom_livemap()`.

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

## [4.1.0] - 2023-11-03

### Added

- Annotations in Barchart

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23e/bar_annotations.ipynb).


- Common theme support in subplots (i.e. `gggrid()`) [[LPK-#197](https://github.com/JetBrains/lets-plot-kotlin/issues/197)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23e/gggrid_theme.ipynb).


- `HCL` and `CIELAB` color space for hue color scale and gradient color scales [[#876](https://github.com/JetBrains/lets-plot/issues/876)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23e/color_space_update.ipynb).


- New scale transformations: `'log2'` and `'symlog'`.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23e/new_scale_transformations.ipynb).


- `plot_margin` parameter in `theme()` [[#856](https://github.com/JetBrains/lets-plot/issues/856)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23e/theme_plot_margin.ipynb).


- Dual orientation for geometries:
  - `geom_errorbar()`
  - `geom_crossbar()`
  - `geom_pointrange()`
  - `geom_linerange()`
  - `geom_ribbon()`

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23e/horizontal_geoms.ipynb).


- Python 3.12 support [[#907](https://github.com/JetBrains/lets-plot/issues/907)].


### Changed

- [BREAKING] `stat_summary()` and `stat_summary_bin` no longer supports computing of additional variables specified in mappings.

### Fixed

- Husl palette equivalent [[#876](https://github.com/JetBrains/lets-plot/issues/876)].
- Tooltips are trimmed and not visible on a very narrow chart [[#837](https://github.com/JetBrains/lets-plot/issues/837)].
- `geom_crossbar`: an error occurs if the mapped values of aesthetics are not populated [[#886](https://github.com/JetBrains/lets-plot/issues/886)].
- Exception label is unresizeble, uncopyable and uncontrollable [[#902](https://github.com/JetBrains/lets-plot/issues/902)].
- Flickering during plot downsizing [[#888](https://github.com/JetBrains/lets-plot/issues/888)].
- Bad default formatting of numeric values in annotations [[#905](https://github.com/JetBrains/lets-plot/issues/905)].
- corr_plot: unexpected whitespace between the "geometry area" and the legend [[#877](https://github.com/JetBrains/lets-plot/issues/877)].
- scale_log: an option to generate only breaks which are integer powers of 10 needed [[#850](https://github.com/JetBrains/lets-plot/issues/850)].
- Trimmed legend when bounds of the rightmost X-axis tick label exceeds the axis length [[#851](https://github.com/JetBrains/lets-plot/issues/851)].
- HTML files exported using ggsave() are missing the encoding specification [[#900](https://github.com/JetBrains/lets-plot/issues/900)].
- `plot_margin` parameter in `theme()` [[#856](https://github.com/JetBrains/lets-plot/issues/856)].
- Subplot themes not inherited by parent [[LPK-#197](https://github.com/JetBrains/lets-plot-kotlin/issues/197)].
- Saving plots in PDF format using ggsave() [[#710](https://github.com/JetBrains/lets-plot/issues/710)].
- `element_blank()` has no effect on plot title/subtitle/caption in `theme()` [[#913](https://github.com/JetBrains/lets-plot/issues/913)].
- Lollipop in legend is disproportionately large [[LPK-216](https://github.com/JetBrains/lets-plot-kotlin/issues/216)].
- geomBar with fill, produces tooltips artefacts [[#895](https://github.com/JetBrains/lets-plot/issues/895)].
- Exception, when trying to build plot with column name containing line breakes [[#894](https://github.com/JetBrains/lets-plot/issues/894)].
- Added "grey" spelling for the gray color (earlier - "gray" only).

## [4.0.1] - 2023-09-13

### Added

- `plot_message` parameter in `theme(...)` [[#863](https://github.com/JetBrains/lets-plot/issues/863)].  
  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23d/theme_plot_message.ipynb).


- Add `geom_count()`/`stat_sum()` [[#821](https://github.com/JetBrains/lets-plot/issues/821)].  
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23d/geom_count.ipynb).

### Changed

- If layer transparency is set via the alpha-channel in the colors RGBA specification and via the `alpha` aesthetic, \
  then the `alpha` aesthetic overrides the alpha-channel in the color. Previousely it was the opposite.


- `geom_pie()` defaults:
  - "stroke" is visible and `stroke_side='both'` (was `stroke_side='outer'`).
  - the "hole" is not created automatically when `stroke_side = 'both'/'inner'` (was created automatically).

- `geom_bar()` now has solid outline color by default (was transparent).

- `geom_tile()`, `geom_bin2d()` now have solid outline color by default (was transparent).
  - however, by default the `size` is 0 (i.e. tiles outline initially is not visible).


### Fixed

- `geom_tile()`, `geom_bin2d()` : the `alpha` aesthetic is applied to the tiles outline.
- `scale_x_datetime()`: error building plot for early dates [[#346](https://github.com/JetBrains/lets-plot/issues/346)].
- `geom_livemap()`: theme/flavor plot background is not shown [[#857](https://github.com/JetBrains/lets-plot/issues/857)].
- `geom_livemap()`: in AWT dragging a map in a facet moves maps in all facets.
- `geom_livemap()`: support rectangle 'linetype' [[#307](https://github.com/JetBrains/lets-plot/issues/307)].
- `theme_void()` + `flavor_xxx()`: no expected plot background [[#858](https://github.com/JetBrains/lets-plot/issues/858)].
- Inconsistent color in legend when using `paint_a/paint_b/paint_c` [[#867](https://github.com/JetBrains/lets-plot/issues/867)].

## [4.0.0] - 2023-08-17

### Added

- Flavor-aware colors: **pen**, **brush** and **paper**
  - By default, all geometries utilize new flavor-aware colors.
  - Theme `geom` parameter allows redefinition of "geom colors":  `theme(geom=element_geom(pen, brush,paper))`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/geom_theme_colors.ipynb).


- `stat_summary()` :
  [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/stat_summary.ipynb).

- `stat_summary_bin()` :
  [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/stat_summary_bin.ipynb).

- `stat_ecdf()` :
  [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/stat_ecdf.ipynb).

- `geom_function()` :
  [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/geom_function.ipynb).

- `theme_void()` [[#830](https://github.com/JetBrains/lets-plot/issues/830)] :
  [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/theme_void.ipynb).

- `"sum"` statistic [[#821](https://github.com/JetBrains/lets-plot/issues/821)]:
  [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/stat_sum.ipynb).

- `"boxplot_outlier"` statistic: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/stat_boxplot_outlier.ipynb).


- Support for variadic line width and/or color in `geom_line()` and `geom_path()` [[#313](https://github.com/JetBrains/lets-plot/issues/313)].

  [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/aes_size_color_variadic_lines.ipynb).


- In tooltip customization API:\
  `disable_splitting()` function [[LPK-189](https://github.com/JetBrains/lets-plot-kotlin/issues/189)].

  [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/tooltips_disable_splitting.ipynb).


- In `geom_pie()`:
  - `stroke` and `color` aesthetics - the width and color of pie sector arcs.
  - `stroke_side` parameter - which arcs to show (inner, outer, both).
  - `spacer_width` and `spacer_color` parameters - lines between sectors.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/geom_pie_stroke_and_spacers.ipynb).

  - `size_unit` parameter : [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/geom_pie_size_unit.ipynb).


### Changed

- The default qualitative color palette is now [Color Brewer "Set1"](https://colorbrewer2.org/#type=qualitative&scheme=Set1&n=9) (was ["Set2"](https://colorbrewer2.org/#type=qualitative&scheme=Set2&n=8))
- Geometries default colors are now flavor-dependent: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23c/geom_theme_colors.ipynb).
- Geometries default size/line-width is now slightly bigger.
- Point size is adjusted to match the width of a line of the same "size".

- [BREAKING] `geom_pie()` no longer supports parameter `stroke_color`.

- [BREAKING] `geom_boxplot()` no longer support parameter `sampling`.

- [BREAKING] `geom_pointrange()`: `size` aesthetic shouldn't affect line width [[#751](https://github.com/JetBrains/lets-plot/issues/751)]:

  `linewidth` aesthetic is used for line width, `size` - for mid-point size only.

- `geom_boxplot()`: `size` and `stroke` parameters now affect outlier shapes.

- [BREAKING, only affects users of Lets-Plot Kotlin API] \
  The entire project has undergone package refactoring.\
  As a result of package names changed, Lets-Plot v4.0.0 is partially incompatible\
  with Lets-Plot Kotlin API versions 4.4.1 and earlier.


### Fixed

- `ggsave()` doesn't save geom_raster() layer to a file [[#778](https://github.com/JetBrains/lets-plot/issues/778)].
- `ggsave()`: saving geomImshow() to SVG produces fuzzy picture [[LPK-188](https://github.com/JetBrains/lets-plot-kotlin/issues/188)].
- `ggsave()`: saving geomImshow() to raster format produces fuzzy picture.
- `geom_crossbar()` aesthetics take `middle` argument instead of `y` [[#804](https://github.com/JetBrains/lets-plot/issues/804)].
- `geom_boxplot()` doesn't apply alpha to outliers [[#754](https://github.com/JetBrains/lets-plot/issues/754)].
- `geom_boxplot()` outliers do not show tooltips.
- `geom_step()` no tooltips.
- `geom_step()`: toggle the behavior of the `direction` parameter when the orientation is changed.
- `geom_livemap()`: memory leak when re-run cells without reloading a page.
- `geom_ribbon()`: not all tooltips are shown on a multi-layer plot [[#847](https://github.com/JetBrains/lets-plot/issues/847)].
- Bug in empty plot: IndexOutOfBoundsException [[#194](https://github.com/JetBrains/lets-plot-kotlin/issues/194)].
- Weird tooltip/legend in case of extremely long value [[#315](https://github.com/JetBrains/lets-plot/issues/315)].
- panning on interactive map should be more responsive [[#336](https://github.com/JetBrains/lets-plot/issues/336)].
- Offline mode doesn't work with manylinux wheels [[#808](https://github.com/JetBrains/lets-plot/issues/808)].

## [3.2.0] - 2023-05-09

### Added

- `geom_lollipop()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23b/geom_lollipop.ipynb).


- Aesthetic `stroke` [[#320](https://github.com/JetBrains/lets-plot/issues/320)]
  and its scales `scale_stroke()`, `scale_stroke_identity()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23b/aes_stroke.ipynb).


- Aesthetic `linewidth` (for `geom_lollipop()`) and its scales `scale_linewidth()`, `scale_linewidth_identity()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23b/geom_lollipop.ipynb).


- The 'newline' character (`\n`) now works as `line break` in legend
  text ([[#726](https://github.com/JetBrains/lets-plot/issues/726)])

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23b/legend_text_multiline.ipynb).


- Horizontal error bars and vertical "dodge" ([[#735](https://github.com/JetBrains/lets-plot/issues/735)]).

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23b/horizontal_error_bars.ipynb).


- Colorbar in `geom_imshow()`. Parameters `show_legend`
  and `color_by` [[#717](https://github.com/JetBrains/lets-plot/issues/717)].

### Changed

- [BREAKING] `geom_dotplot()` and `geom_ydotplot()` no longer support parameter `stat`.

- Position adjustment settings:
  - `width, height` parameters of `geom_jitter()` have priority over the `width, height` parameters
    of `position_jitter()` function;
  - `nudge_x, nudge_y` parameters of `geom_text(), geom_label()` have priority over `x, y` parameters
    of `position_nudge()` function.

- `geom_text(), geom_label()` use `stat='identity'` by default.

- JVM/Batik: improved "paint manager" which now has better performance and fixes issues with artifacts that could appear beyond the plot component.

### Fixed

- Batik: `geom_imshow()` fail with an error: "The attribute "xlink:href" of the element <image> is required"
- Batik: bug with usage of "&" [[#713](https://github.com/JetBrains/lets-plot/issues/713)].
- Categorical ordering, it's not respected for Boxplot and violin
  plot [[#746](https://github.com/JetBrains/lets-plot/issues/746)].
- Groups not sorted similarly when using facets [[#679](https://github.com/JetBrains/lets-plot/issues/679)].
- HTML export: exclude computation messages from the output [[#725](https://github.com/JetBrains/lets-plot/issues/725)].
- Image export not working with `geom_imshow()`
  and `geom_raster()` [[LPK-175](https://github.com/JetBrains/lets-plot-kotlin/issues/175)].
- `geom_segment()` doesn't take into account the alpha [[#748](https://github.com/JetBrains/lets-plot/issues/748)].
- `geom_density2d`: Internal error with None values in data [[#702](https://github.com/JetBrains/lets-plot/issues/702)].
- DateTime metadata is not applied for scales other than
  X/Y [[LPK-174](https://github.com/JetBrains/lets-plot-kotlin/issues/174)].
- Quantile should be shown in tooltip if the variable `..quantile..` is mapped to geom aesthetic.
- Bad default formatting for stat variables [[#654](https://github.com/JetBrains/lets-plot/issues/654)].
- The scale name does not apply with `as_discrete()` [[#653](https://github.com/JetBrains/lets-plot/issues/653)].
- Tooltip is not shown when configured for 'const' value [[#610](https://github.com/JetBrains/lets-plot/issues/610)].
- Fix crash when try to add a constant to a tooltip (e.g.`"^size"`, where `size` aesthetic is specified with a number).
- "Variable not found" error in `ggmarginal` [[#681](https://github.com/JetBrains/lets-plot/issues/681)].
- `facet_grid`: Internal error [[#699](https://github.com/JetBrains/lets-plot/issues/699)].
- Export to SVG fails if breaks are given by integers [[#763](https://github.com/JetBrains/lets-plot/issues/763)].
- Remove hard IPython dependency [[#749](https://github.com/JetBrains/lets-plot/issues/749)].
- Tooltips bug [[LPK-176](https://github.com/JetBrains/lets-plot-kotlin/issues/176)].
- livemap: doesn't work well with gggrid [[#750](https://github.com/JetBrains/lets-plot/issues/750)].
- livemap: memory leak caused by a document event handler.
- livemap: flickering when zooming with the buttons.
- livemap: tooltip text doesn't reflect data under the
  cursor [[#709](https://github.com/JetBrains/lets-plot/issues/709)].

## [3.1.0] - 2023-03-07

### Added

- `gggrid()` function.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/plot_grid.ipynb).


- `joint_plot()`

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/joint_plot.ipynb).


- Export to PNG files in `ggsave()`.

  Note: export to PNG file requires the [CairoSVG](https://pypi.org/project/CairoSVG) library.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/export_to_png.ipynb).


- Axis `position` parameter in position scales `scale_x_*(), scale_y_*()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/axis_position.ipynb).


- `angle` parameter in `element_text()` in `theme()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/axis_text_angle.ipynb).


- Additional "color" aesthetics: `paint_a, paint_b, paint_c`.

  These aesthetics are flexible and can be used as either "color" or "fill" as needed. See [Multiple Color Scales](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/multiple_color_scales.ipynb) demo.

  Also added a set of related "color scale" functions with the "aesthetic" parameter for configuring of additional color scales.

  See [New "Scale" Functions](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/scale_functions.ipynb) demo.


-  Drawing quantile lines and filling quantile areas in `geom_violin()` and `geom_density()`

   See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/quantile_parameters.ipynb).


- `geodesic` parameter for `geom_segment()` and `geom_path()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/param_geodesic.ipynb).


- `density2d` and `density2df` geometry types in `residual_plot()`.


### Changed

- The `MinGW` toolchain is no longer required for installing of `Lets-Plot` on `Windows`.

- [BREAKING] `geom_violin()` no longer supports parameter `draw_quantiles`. Use new `quantile_lines` and `quantiles` parameters as needed.

- [BREAKING] `stack` and `fill` position adjustments now stack objects on top of each other only if these objects belong to different **groups**.
  If necessary, use `mode="all""` in `position_stack()` or `position_fill()` to stack objects regardless of their group.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/position_stack.ipynb).


### Fixed

- Tooltip does not reflect `..quantile..` aesthetic change [[#658](https://github.com/JetBrains/lets-plot/issues/658)].
- `color_by` parameter of the `residual_plot()` should group the data points [[#662](https://github.com/JetBrains/lets-plot/issues/662)].
- 'map_join': variable is lost after "stat" [[#664](https://github.com/JetBrains/lets-plot/issues/664)].
- Error when tooltip has variable mapped to aesthetic used by stat [[#665](https://github.com/JetBrains/lets-plot/issues/665)].
- Groups not sorted similarly when `position='stack'` [[#673](https://github.com/JetBrains/lets-plot/issues/673)].
- Area ridges: fill overlaps geometry borders when colors are repeated [[#674](https://github.com/JetBrains/lets-plot/issues/674)].
- livemap: hide tooltips when user is zooming-in by double-clicks [[#659](https://github.com/JetBrains/lets-plot/issues/659)].
- livemap: wrong position when path goes through the antimeridian [[#682](https://github.com/JetBrains/lets-plot/issues/682)].
- livemap: wrong position if path is on a circle of latitude [[#683](https://github.com/JetBrains/lets-plot/issues/683)].
- livemap: tooltip may show wrong data on density2df [[#684](https://github.com/JetBrains/lets-plot/issues/684)].
- livemap: geom_text vjust="center" is a bit off [[#132](https://github.com/JetBrains/lets-plot/issues/132)].
- livemap: segment that goes through the antimeridian should be straight [[#692](https://github.com/JetBrains/lets-plot/issues/692)].
- livemap: apply alpha to the pie chart and to its tooltip color marker.
- Layout: uneven plot margins for the horizontal axis [[#705](https://github.com/JetBrains/lets-plot/issues/705)].
- Sampling: increase the default N for "pick sampling" and for other types of sampling [[#687](https://github.com/JetBrains/lets-plot/issues/687)].

## [3.0.0] - 2022-12-15

### Added

- Python wheel for Python 3.11.


- `residual_plot()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/residual_plot.ipynb).

- `geom_area_ridges()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/ridgeline_plot.ipynb).

- `geom_pie()`.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/geom_pie.ipynb).


- Annotations for pie chart:

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/annotations_for_pie.ipynb).

- New variables computed by `'count'` and `'count2d'` statistics: `'..sum..'`, `'..prop..'`, `'..proppct..'`.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/stat_count(2d)_vars.ipynb).


- Maps:

  - "Spatial pies" on interactive maps.

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/titanic.ipynb).

  - The `flat` parameter for `geom_path()` and `geom_segment()` (replaces the retired `geodesic` parameter in `geom_livemap`).

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/param_flat.ipynb).


- Static maps:

  - The value "provided" for `use_crs` parameter.

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/projection_provided.ipynb).


- `geom_imshow()`:

  - Improved performance by orders of magnitude.

  - Transparency of `NaN` values in grayscale images [[#631](https://github.com/JetBrains/lets-plot/issues/631)].
    See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/image_nan_values.ipynb).

  - `alpha` parameter [[#630](https://github.com/JetBrains/lets-plot/issues/630)].
    See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/image_alpha_param.ipynb).


- `tails_cutoff` parameter in `geom_violin()`
  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22e/violin_tails_cutoff.ipynb).


### Changed

- [BREAKING] Dropped support for Python 3.6 as it is in the ["end-of-life"](https://devguide.python.org/versions/) of its release cycle.
- [BREAKING] `geom_livemap()` itself no longer draws geometries, so the following options are no longer supported:
  `symbol`, `data`, `mapping`, `map`, `map_join`, `ontop`, `stat`, `position`, `show_legend`, `sampling`, `tooltips`.
  To draw **point** and **pie** markers on map, please, use the `geom_point()` and `geom_pie()` geometry layers.
- Java/Swing platf.: Apache Batik upgraded to v.1.16 [[#624](https://github.com/JetBrains/lets-plot/issues/624)], [[LPK #140](https://github.com/JetBrains/lets-plot-kotlin/issues/140)].
- The default size is increased for the plot title and decreased for the caption.
- Upgraded Kotlin version to 1.7.21 (was 1.7.20).

### Fixed

- Themes: can't change plot background after applying a "flavor" [[#623](https://github.com/JetBrains/lets-plot/issues/623)].
- Layout: uneven left/right, top/bottom plot margins [[#625](https://github.com/JetBrains/lets-plot/issues/625)].
- A plot building error with empty data on various geoms.
- Precision error in gradient [[#634](https://github.com/JetBrains/lets-plot/issues/634)].
- geom_livemap: wrong position when datapoints geodesic line goes close to the N.P. [[#645](https://github.com/JetBrains/lets-plot/issues/645)].

## [2.5.1] - 2022-11-03

### Added

- `geom_text(), geom_label()`:

  -  the 'newline' character (`\n`) now works as `line break`  ([[#605](https://github.com/JetBrains/lets-plot/issues/605)])
  - `lineheight` aesthetic ([[#324](https://github.com/JetBrains/lets-plot/issues/324)])
  - `nudge_x, nudge_y` parameters ([[#324](https://github.com/JetBrains/lets-plot/issues/324)])
  - special text alignments (`vjust` and `hjust`): `"inward"` and `"outward"` ([[#324](https://github.com/JetBrains/lets-plot/issues/324)])

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/geom_text_new_features.ipynb).

- `vjust` parameter in `position_stack()` and `position_fill()` [[#323](https://github.com/JetBrains/lets-plot/issues/323)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/position_stack.ipynb).

- `use_crs` parameter in `geom_map()` and other geoms, working with `GeoDataFrame`

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/map_use_crs.ipynb).

- `geom_imshow()` (former `geom_image()`):

  - `extent` parameter

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/image_extent.ipynb).

  - `vmin, vmax, cmap` parameters

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/image_grayscale.ipynb).

- `image_matrix()`:

  - `vmin, vmax, cmap` parameters

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/image_matrix.ipynb).


### Changed

- `geom_image()` renamed to `geom_imshow()`

  See updated examples: [image 101](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/image_101.ipynb),
  [Fisher's boat](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/image_fisher_boat.ipynb).

- `geom_boxplot()`: default value for parameter `whisker_width` is 0.5.

- Upgraded Kotlin version to 1.7.20 (was 1.6.21).


### Fixed

- element_blank() has no effect in theme legend_title [[#608](https://github.com/JetBrains/lets-plot/issues/608)].
- `geom_livemap()`: add support of geom_label parameters [[#601](https://github.com/JetBrains/lets-plot/issues/601)].
- Tooltip: different formats for same aesthetic Y [[#579](https://github.com/JetBrains/lets-plot/issues/579)].
- Positioning with "constant" x/y doesn't work on axis with log10 transform [[#618](https://github.com/JetBrains/lets-plot/issues/618)].
- Positional "constant" doesn't honor axis limits [[#619](https://github.com/JetBrains/lets-plot/issues/619)].
- Parameter `norm` in `geom_imshow()`.
- Several issues leading to crush in Swing/Batik apps. Related to [[discussions](https://github.com/JetBrains/lets-plot-kotlin/discussions/138)]
- Text labels got trimmed occasionally, when symbols `-`, `/`, `\` or `|` present.
- `geom_livemap()` doesn't load vector tiles inside `iframe` with certain security policies.


## [2.5.0] - 2022-09-29

### Added

- New theme: `theme_bw()` [[#554](https://github.com/JetBrains/lets-plot/issues/554)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/theme_bw.ipynb).

- Color schemes (flavors) applicable to existing themes:
  - `flavor_darcula()`
  - `flavor_solarized_light()`
  - `flavor_solarized_dark()`
  - `flavor_high_contrast_light()`
  - `flavor_high_contrast_dark()`

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/theme_flavors.ipynb).

- Viridis color scales: `scale_color_viridis()`, `scale_fill_viridis()`.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/colors_viridis.ipynb).

- New parameters in `element_text()`  [[#562](https://github.com/JetBrains/lets-plot/issues/562)]:
  - `size, family`
    ([example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/font_size_and_family.ipynb))
  - `hjust, vjust` for plot title, subtitle, caption, legend and axis titles
    ([example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/hjust_vjust.ipynb))
  - `margin` for plot title, subtitle, caption, axis titles and tick labels
    ([example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/text_margins.ipynb))

- The 'newline' character (`\n`) now works as `line break` in axis title.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/text_margins.ipynb).

- Parameter `whisker_width` in `geom_boxplot()` [[#549](https://github.com/JetBrains/lets-plot/issues/549)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/boxplot_whisker_width.ipynb).

- New geometry `geom_label()` [[#557](https://github.com/JetBrains/lets-plot/issues/557)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/geom_label.ipynb).

- Auto-detection of **Databricks** and **NextJournal** environments [[#602](https://github.com/JetBrains/lets-plot/issues/602)].

- Python wheels for manylinux arm64 architecture [[#581](https://github.com/JetBrains/lets-plot/issues/581)].


### Changed

- New tooltip style after applying `coord_flip()`  [[#580](https://github.com/JetBrains/lets-plot/issues/580)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/tooltips_after_coord_flip.ipynb).

- In Lets-Plot for Java/Swing, Apache Batik upgraded to v.1.15 [[#604](https://github.com/JetBrains/lets-plot/issues/604)] 

### Fixed

- Density and area geoms: preserve the z-order when grouping [[#552](https://github.com/JetBrains/lets-plot/issues/552)].
- Allow to import all 'bistro' functions just by '*' [[#551](https://github.com/JetBrains/lets-plot/issues/551)].
- Boxplot, violin, crossbar: position dodge width=0.95 should be used by default [[#553](https://github.com/JetBrains/lets-plot/issues/553)].
- Tooltip is shown not for the nearest object [[#574](https://github.com/JetBrains/lets-plot/issues/574)].
- Tooltip is not displayed for the object on the plots border [[#575](https://github.com/JetBrains/lets-plot/issues/575)].
- The plot caption overlaps with the legend [[#587](https://github.com/JetBrains/lets-plot/issues/587)].
- Unclear size unit of width [[#589](https://github.com/JetBrains/lets-plot/issues/589)].
- Specify size units in docstrings [[#597](https://github.com/JetBrains/lets-plot/issues/597)].
- No tooltips for geom_boxplot with zero height [[#563](https://github.com/JetBrains/lets-plot/issues/563)].
- geom_text: wrong label alignment with `hjust` 0 and 1 [[#592](https://github.com/JetBrains/lets-plot/issues/592)].
- Error when using lets-plot in streamlit [[#595](https://github.com/JetBrains/lets-plot/issues/595)].
- Documentation for the `breaks` parameter in scales [[#507](https://github.com/JetBrains/lets-plot/issues/507)].


## [2.4.0] - 2022-06-20

### Added

- Python 3.10 support [[#505](https://github.com/JetBrains/lets-plot/issues/505)].

- Python 3.9: a Python wheel for macOS arm64 architecture (Apple Silicon).


- `LetsPlot.set_theme()` - configuring a default plot theme.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/default_theme.ipynb).


- Quantile-Quantile (Q-Q) plot:
  - geometries:
    - `geom_qq()`
    - `geom_qq_line()`
    - `geom_qq2()`
    - `geom_qq2_line()`
  - quick Q-Q : the `qq_plot()` function in the `bistro` module.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/qq_plots.ipynb).


- Marginal plots: the `ggmarginal()` function [[#200](https://github.com/JetBrains/lets-plot/issues/200)],
  [[#384](https://github.com/JetBrains/lets-plot/issues/384)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/marginal_layers.ipynb).


- Parameter `orientation` in geoms: `bar, boxplot, density, histogram, freqpoly, smooth, violin`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/y_orientation.ipynb).


- New in *plot theme*:
  - `face` parameter in `element_text()`.

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/set_font_faces.ipynb).

  - `panel_border` parameter in `theme()` [[#542](https://github.com/JetBrains/lets-plot/issues/542)].

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/panel_border.ipynb).

  - Tooltip theme options, new parameters in `theme()`:
    - `tooltip` - tooltip rectangle options;
    - `tooltip_text, tooltip_title_text` - tooltip text options;
    - `axis_tooltip_text, axis_tooltip_text_x, axis_tooltip_text_y` - axis tooltip text options.

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/tooltips_theme.ipynb).



- `scale_color_gradientn()` and `scale_fill_gradientn()` functions [[#504](https://github.com/JetBrains/lets-plot/issues/504)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/scale_%28color_fill%29_gradientn.ipynb).



### Changed

- `geom_livemap` [**breaking change**] : `symbol="point"` now should be specified explicitly
  to enable the "livemap" layer to handle provided
  "point" aesthetics directly. This change doesn't affect "points" layers added via
  the `geom_point()` geometry layer.
- New style of tooltip color marker (two sidebars with a stroke color)
  and symbols in legend (rectangle with a stroke instead of a slash-line).
- New type of general tooltip for `geom_boxplot`: displayed under the cursor.
- Default sampling type for `geom_violin` switched from `systematic` to `pick`.

### Fixed

- `geom_livemap`: support of the `arrow` parameter in `geom_segment` [[#131](https://github.com/JetBrains/lets-plot/issues/131)].
- Differences in tooltip color marker for plots with and without livemap.
- Labels out of plot when axis_text_y='blank' [[#525](https://github.com/JetBrains/lets-plot/issues/525)].
- NPE in corr_plot with null coefficients.
- Outliers are not shown when boxplot' alpha=0.
- Support for polars.DataFrame [[#526](https://github.com/JetBrains/lets-plot/issues/526)].
- JFX rendering issue that causes tooltips to stuck [[#539](https://github.com/JetBrains/lets-plot/issues/539)].
- Support trim parameter in density and ydensity stats [[#62](https://github.com/JetBrains/lets-plot/issues/62)].
- Unexpected point geometries on geom_livemap() [[#547](https://github.com/JetBrains/lets-plot/issues/547)].
- `geom_violin`: add missing parameters `kernel`, `bw`, `adjust`, `n`, `fs_max` to signature and docstring.

## [2.3.0] - 2022-03-21

### Added

- Plot subtitle and caption   [[#417](https://github.com/JetBrains/lets-plot/issues/417)]:
  `subtitle` parameter in `ggtitle()` and `labs()`,
  `caption` parameter in `labs()`,
  `plot_subtitle` and `plot_caption` parameters in `theme()`.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/title_subtitle_caption.ipynb).

- The 'newline' character (`\n`) now works as `line break` in plot title, subtitle, caption and in legend title.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/title_subtitle_caption.ipynb).

- New in tooltip customization API:
  - The `title()` option defines a tooltip "title" text which will always appear above the rest of the tooltip content.
  - The 'newline' character (`\n`) now works as `line break` in tooltips.
  - Automatic word wrap: occurs when the length of a text value in tooltip exceeds the 30 characters limit.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/tooltip_title.ipynb).

- Parameter `scales` in `facet_grid()/facet_wrap()` [[#451](https://github.com/JetBrains/lets-plot/issues/451),
  [#479](https://github.com/JetBrains/lets-plot/issues/479)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/facets_free_scales.ipynb).

- New in `geom_livemap()`:
  - The `Reset` button: returns the map widget to its initial zoom/location state.
  - Parameters `data_size_zoomin, const_size_zoomin`: allow configuring how zooming-in of the map widget increases size of geometry objects (circles, lines etc.) on map.
  - Parameter `ontop` that controls z-index of the `geom_livemap` layer.
  - Parameter `show_coord_pick_tools` to show "copy location" and "draw geometry" buttons.

- New geometries:
  - `geom_violin()`

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/geom_violin.ipynb).

  - `geom_dotplot()`

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/geom_dotplot.ipynb).

  - `geom_ydotplot()`

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/geom_ydotplot.ipynb).


### Changed

- New tooltip style: rounded corners, bold label, colored marker inside the tooltip.
- Deprecated tooltip customization API:
  function `color()` will be removed in one of the future releases.
- 'Auto shrink': plots automatically shrink when necessary to fit width of the output (notebook) cell [[#488](https://github.com/JetBrains/lets-plot/issues/488)].

### Fixed

- LiveMap, Swing-batik: legend is not visible when overlapping map [[#496](https://github.com/JetBrains/lets-plot/issues/496)].
- CVE-2021-23792 in org.jetbrains.lets-plot:lets-plot-image-export@2.2.1 [[#497](https://github.com/JetBrains/lets-plot/issues/497)].
- Color in tooltip does not correspond to the color of marker on map [[#227](https://github.com/JetBrains/lets-plot/issues/227)].
- tooltip on livemap: hide tooltip when the cursor is over the controls [[#335](https://github.com/JetBrains/lets-plot/issues/335)].
- Automatic detection of DateTime series [[#99](https://github.com/JetBrains/lets-plot-kotlin/issues/99)].
- Fix tooltips for `geom_histogram(stat='density')`.
- The axis tooltip overlaps the general tooltip [[#515](https://github.com/JetBrains/lets-plot/issues/515)].
- The multi-layer tooltip detection strategy will only be used if more than one layer provides tooltips.
- scaleColorManual Divide by Zero with 1 mapping [[#506](https://github.com/JetBrains/lets-plot/issues/506)].
- LinearBreaksHelper$Companion.computeNiceBreaks out of memory error [[#105](https://github.com/JetBrains/lets-plot-kotlin/issues/105)].

## [2.2.1] - 2021-12-10

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

## [2.2.0] - 2021-10-29

### Added

- `coord_flip()`.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-21-10/notebooks/coord_flip.ipynb).

- Date-time formatting support:
  - tooltip format() should understand date-time format pattern [[#387](https://github.com/JetBrains/lets-plot/issues/387)];
  - scale_x_datetime should apply date-time formatting to the breaks [[#392](https://github.com/JetBrains/lets-plot/issues/392)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-21-10/notebooks/datetime_formatting.ipynb).

- Pre-configured themes:
  - Standard ggplot2 themes: `theme_grey(), theme_light(), theme_classic(), theme_minimal()`;
  - Other themes: `theme_minimal2()` - the default theme, `theme_none()`.

- Theme modification: more parameters were added to the `theme()` function.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-21-10/notebooks/themes.ipynb).

> Note: fonts size, family and face still can not be configured.

- `corr_plot()` function now also accepts pre-computed correlation coefficients. I.e. the following two expressions are equivalent:
```python
    corr_plot(iris_df).points().labels().build()
    corr_plot(iris_df.corr()).points().labels().build()  # new
```

### Changed

- The size of fonts on plot was slightly increased all across the board.
- The default plot size was increased by 20%, it's now 600x400 px.
- **Deprecated API**: `stat_corr()` and "correlation stat" will be removed in one of the future releases.
  Please use the `corr_plot()` plot builder object instead.

### Fixed

- Ordering facets - the "order" value 0 disables facet ordering [[#454](https://github.com/JetBrains/lets-plot/issues/454)].
- Tooltips for discrete variables: add the dependence of the tooltip on the number of factors.
  The X-axis tooltip is always shown for discrete data.
- map_join doesn't work when join variables with same names without mappings [[#428](https://github.com/JetBrains/lets-plot/issues/428)].
- Zoom without clipping breaks tooltips [[#373](https://github.com/JetBrains/lets-plot/issues/373)].
- Unreadable breaks on axis [[#430](https://github.com/JetBrains/lets-plot/issues/430)].
- Map rendering is broken when browser uses scaling [[#450](https://github.com/JetBrains/lets-plot/issues/450)].
- corr_plot() error for data with zero variation [[#329](https://github.com/JetBrains/lets-plot/issues/329)].
- Misleading error message [[#271](https://github.com/JetBrains/lets-plot/issues/271)].

## [2.1.0] - 2021-08-16

### Added

- Ordering categories. New parameters in the `as_discrete` function:
    * `order_by` (string) - the name of the variable by which the ordering will be performed;
    * `order` (int) - the ordering direction - 1 for ascending direction and -1 for descending (default value).

  See: [as_discrete](https://lets-plot.org/pages/as_discrete.html).

- Basemap tiles configuring:
    - Subdomains parameter `{s}` for XYZ raster tiles.
    - Solid color tiles: `maptiles_solid()`.
    - Builtin configurations for some 3rd party maptile services. The `lets_plot.tilesets` module.

  See: [Configuring basemap tiles](https://lets-plot.org/pages/basemap_tiles.html).

### Changed

- Upgraded Apach Batik version to 1.14 (was 1.12) [[#398](https://github.com/JetBrains/lets-plot/issues/398)].
- Upgraded Kotlin version to 1.5.21 (was 1.4.21)
- Upgraded Gradle version to 7.1.1 (was 6.8.3)

### Fixed

- geom_livemap: properly handle `max_zoom` pamareter in `maptiles_zxy()`.
- Strange looking legend for tiles [[#245](https://github.com/JetBrains/lets-plot/issues/245)].
- Need to skip "bad" values during scale transformation [[#301](https://github.com/JetBrains/lets-plot/issues/301)].
- NPE on negative value in data and scale_xxx(trans='log10') [[#292](https://github.com/JetBrains/lets-plot/issues/292)].
- Legend is broken when using scale_fill_brewer with 'trans' parameter [[#284](https://github.com/JetBrains/lets-plot/issues/284)].
- Scale breaks should be distributed evenly on 'sqrt' scale. [[#407](https://github.com/JetBrains/lets-plot/issues/407)].
- Wrong tooltip formatting when used with log10 scales [[#406](https://github.com/JetBrains/lets-plot/issues/406)].
- Bad axis labels when using both plot and layer data [[#327](https://github.com/JetBrains/lets-plot/issues/327)].
- Plot layout looks wrong [[#403](https://github.com/JetBrains/lets-plot/issues/403)].
- map_join is not working correctly when `map=geocoder` [[#380](https://github.com/JetBrains/lets-plot/issues/380)]
- Tooltip default formatting should not change after adding other variables to tooltip [[#388](https://github.com/JetBrains/lets-plot/issues/388)].
- Tooltip on axis: increase the font size. [[#399](https://github.com/JetBrains/lets-plot/issues/399)].
- Tooltip format for variable is not working [[#401](https://github.com/JetBrains/lets-plot/issues/401)].
- Wrong direction in colorbars (legend) [[#204](https://github.com/JetBrains/lets-plot/issues/204)].
- geom_jitter: show axis tooltips (same as geom_point) [[#412](https://github.com/JetBrains/lets-plot/issues/412)].
- Outlier tooltips: the spout sometime is too long (boxplot) [[#358](https://github.com/JetBrains/lets-plot/issues/358)].
- Faceted plot is broken by geom with "constant" aesthetics [[#391](https://github.com/JetBrains/lets-plot/issues/391)].
- Interactive maps in AWT (PyCharm plugin):
    - Memory leaks in PyCharm caused by `dispose()` method in PlotPanel.
    - While showing map in PyCharm CPU is busy all the time, even when nothing is changing on screen.
    - Click events detection.

## [2.0.4] - 2021-06-09

### Changed

- Upgraded `kotlinx.html` version to 0.7.3 (was 0.7.2)
>  In JVM projects it's no longer necessary to add `https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven` repository
>  to the project configuration.

### Fixed

- Multilayer plots are corrupted [[#385](https://github.com/JetBrains/lets-plot/issues/385)].

## [2.0.3] - 2021-06-02

### Added

- In tooltip customization API:
    - `layer_tooltips(variables)` - the new parameter `variables` defines a list of variable names, which values will be placed line by line in the general tooltip.
   See: [Tooltip Customization](https://lets-plot.org/pages/tooltips.html).

### Changed

- CDN: Lets-Plot JavaScript library is now served via [JSDELIVR](https://www.jsdelivr.com/?docs=gh) (was CDNJS):
> https://cdn.jsdelivr.net/gh/JetBrains/lets-plot@v2.0.3/js-package/distr/lets-plot.min.js

### Fixed
        
- Removed the last dependency on bintray JCenter ([commit](https://github.com/JetBrains/lets-plot/commit/7bcd38e000a4952b83269ef4ebac0b7d826dea6a)). 
- geom_boxplot: should be possible to create boxplot without specifying x-series [[#325](https://github.com/JetBrains/lets-plot/issues/325)]
- geom_hline: graph plotted outside of coordinate plane visible part [[#334](https://github.com/JetBrains/lets-plot/issues/334)]
- Draw geometry only once if layer has no aes mapping specified [[#73](https://github.com/JetBrains/lets-plot/issues/73)]
- map: calif.housing [[#140](https://github.com/JetBrains/lets-plot/issues/140)]
- Can't build plot: "Uncaught SyntaxError: Unexpected string" in a console [[#371](https://github.com/JetBrains/lets-plot/issues/371)]
- All scales should have the 'format' parameter [[#347](https://github.com/JetBrains/lets-plot/issues/347)].
- Poor font rendering in Swing/Batik. Related to:  [[#364](https://github.com/JetBrains/lets-plot/issues/364)]
- Exclude slf4j implementation from lets-plot-common [[#374](https://github.com/JetBrains/lets-plot/issues/374)]

## [2.0.2] - 2021-04-13

### Changed

- Due to shutting down of [Bintray, JCenter](https://jfrog.com/blog/into-the-sunset-bintray-jcenter-gocenter-and-chartcenter/):
  - The Lets-Plot JS library is now delivered via [CDNJS](https://cdnjs.com/libraries/lets-plot).
  - JVM Maven artifacts are now published at [Maven Central](https://search.maven.org/search?q=lets-plot).

## [2.0.1] - 2021-03-17

### Added

- The `alpha` parameter for lines [[#139](https://github.com/JetBrains/lets-plot/issues/139)].
- Tooltips for `geom_segment()` [[#296](https://github.com/JetBrains/lets-plot/issues/296)].
- The `guides()` function [[#52](https://github.com/JetBrains/lets-plot/issues/52)].
- New Java **Swing plot components** to enable embedding Lets-Plot charts into JVM applications.
  - See: `vis-swing-common, vis-swing-batik, vis-swing-jfx` modules. 

### Fixed
                         
- Fix auto-detection of PyCharm env to enable plotting in SciView while using remote interpreter [[348](https://github.com/JetBrains/lets-plot/issues/348)]
- Fix tooltips appearing outside the specified x/y limits.
- Clippath in accordance to the given limits [[#189](https://github.com/JetBrains/lets-plot/issues/189)].
- Treat a data as DataFrame if both data and map are GeoDataFrames [[#343](https://github.com/JetBrains/lets-plot/issues/343)].
- Removed the restriction on tooltips for small polygons [[#298](https://github.com/JetBrains/lets-plot/issues/298)].
- The x/y axis labels are derived from x/y aesthetics only [[#333](https://github.com/JetBrains/lets-plot/issues/333)].
- Merge 'theme' settings [[#147](https://github.com/JetBrains/lets-plot/issues/147)].
- Add axis tooltips for `geom_bin2d`.
- Outlier tooltips for `geom_ribbon()`.
- Fix tooltip crosshair [[#309](https://github.com/JetBrains/lets-plot/issues/309)].

## [2.0.0] - 2021-02-09

### Added

- Python 3.9 support on all platforms.
- `facet_wrap()` function [[#238](https://github.com/JetBrains/lets-plot/issues/238)]
- In facets:
  - Ascending/descending ordering of faceting values.
  - Formatting of faceting values. 
               
  See: [Facets demo](https://nbviewer.jupyter.org/github/JetBrains/lets-plot-docs/blob/master/source/examples/cookbook/facets.ipynb)


- In tooltip customization API: 
   - option `color` overrides the default tooltip color:
       ```python
       geom_xxx(tooltips=layer_tooltips().color('red'))
       ```

  See: [Tooltip Customization](https://lets-plot.org/pages/tooltips.html). 


- Crosshair cursor when tooltip is in fixed position specified by the `anchor` option.
- Scale `format` parameter: formatting tick labels on X/Y axis. Supported types are `number` and `date/time`.
  
  Example:
   ```python
   scale_x_datetime(format="%b %Y")
   scale_x_continuous(format='is {.2f}')
   ```

   Demo: [Formatting demo](https://nbviewer.jupyter.org/github/JetBrains/lets-plot-docs/blob/master/source/examples/cookbook/formatting_axes_etc.ipynb)
       
   See also: [Formatting](https://lets-plot.org/pages/formats.html)

### Changed
                
- [**breaking change**] New Geocoding API!

  Since Lets-Plot v2.0.0 the peviouse Geocoding API is no longer working (hence we bumped the version to 2.0.0). 
  
  The old version of the Geocoding backend remains on-line for a couple of release cycles
  to continue support of prior versions of Lets-Plot.
  
  To learn more about Geocoding API see: [Geocoding](https://lets-plot.org/pages/geocoding.html).
                                    

- The project upgraded to Kotlin v1.4.21 (was v1.4.10).

### Fixed

- Kernel density estimate computation is too slow.
- Serialize NaN and other special values as None (CRITICAL for the "Lets-Plot in SciView" PyCharm plugin) [[#282](https://github.com/JetBrains/lets-plot/issues/282)].
- Error in plot building: 'trans' parameter not compatible with None values in data [[#287](https://github.com/JetBrains/lets-plot/issues/287)].
- LOESS smoothing fails on small (<3) number points.
- Facet grid truncated in jupyter [[#28](https://github.com/JetBrains/lets-plot-kotlin/issues/28)].
- The `reverse` parameter on discrete scale with 'limits' [[#303](https://github.com/JetBrains/lets-plot/issues/289)].
- Geocoder's `allow_ambiguous()` doesn't prevent "ValueError: Multiple objects.." [[#174](https://github.com/JetBrains/lets-plot/issues/174)].
- Fix tooltip for overlapping objects [[#230](https://github.com/JetBrains/lets-plot/issues/230)].
- Fix duplicate values in tooltip [[#280](https://github.com/JetBrains/lets-plot/issues/280)].
- geom_histogram should not try to handle geometries in GeoDataFrame [[#281](https://github.com/JetBrains/lets-plot/issues/281)].
- Error building plot: Layer 'MAP' is not supported on Live Map. [[#285](https://github.com/JetBrains/lets-plot/issues/285)].
- Align title to the left of the plot geom area [[#289](https://github.com/JetBrains/lets-plot/issues/289)].
- Tooltip on `geom_ribbon()`.

## [1.5.6] - 2020-12-23

### Fixed
- “Symbol not found: _NSGenericException” error on macOS [[#276](https://github.com/JetBrains/lets-plot/issues/276)].

## [1.5.5] - 2020-12-18

### Added
- Correlation plot.
   
   See: [Charts](https://lets-plot.org/pages/charts.html).

- 'No Javascript' mode.

  Support for notebook renderers that don't execute Javascript.
  
  See: ['No Javascript' mode](https://lets-plot.org/pages/no_js_and_offline_mode.html#no-javascript-mode)

- In tooltip customization API:
   - options: `center` and `middle` (anchor).
   - option 'minWidth'.
   
   See: [Tooltip Customization](https://lets-plot.org/pages/tooltips.html).
   
- 'na_text' parameter in 'geom_text'

### Changed
- Tooltip customization API:
   - The `anchor` option moved from `theme` to `layer`:
     ```python                                                     
     geom_xxx(tooltips=layer_tooltips().anchor(anchor_value))
     ```
        where `anchor_value`: 
        `['top_right'|'top_center'|'top_left'|'bottom_right'|'bottom_center'|'bottom_left'|'middle_right'|'middle_center'|'middle_left']`.

   See: [Tooltip Customization](https://lets-plot.org/pages/tooltips.html).
 
### Fixed
- Tooltip should appear when the mapped data is continuous [[#241](https://github.com/JetBrains/lets-plot/issues/241)]
- Tooltip 'null' displayed for undefined vals [[#243](https://github.com/JetBrains/lets-plot/issues/243)]
- Y-tooltip should be aligned with a tile center [[#246](https://github.com/JetBrains/lets-plot/issues/246)]
- With `facet_grid` tooltip shows data from last plot on all plots [[#247](https://github.com/JetBrains/lets-plot/issues/247)]

## [1.5.4] - 2020-11-19
### Changed
- [**breaking change**] In functions `format(field, format)` and `line(template)` in 
tooltip builder, the '$' symbol is no longer used in aesthetic reference. It was replaced by the '^' (hat) symbol 
(see the udated doc: [Tooltip Customization](https://lets-plot.org/pages/tooltips.html)).
- The project upgraded to Kotlin v1.4.10 (was v1.3.72).
 
### Fixed
- No tooltip on v-line [[#229](https://github.com/JetBrains/lets-plot/issues/229)]

## [1.5.3] - 2020-11-05
### Added
- Facilities for customization of tooltip contents and its position (see [Tooltip Customization](https://lets-plot.org/pages/tooltips.html)).
- `attribution` and other new parameters in the `maptiles_zxy()` function. 
- `label_format` parameter in the `geom_text()` function.
- `scale_x_discrete_reversed()` / `scale_y_discrete_reversed()`
- `ggsave()` function (only SVG, HTML)

### Fixed
- Mercator's projection computes coordinates incorrectly (`geom_map(), coord_map()`)
- Handling of `xlim,ylim` in `coord_fixed(), coord_map()`
- Colorbars: take in account limits defined in continuous color/fill scales.
- `scale_color_hue` incorrectly interprets chroma range [[#206](https://github.com/JetBrains/lets-plot/issues/206)].
- `scale_color_brewer` shouldn't fail if 'type' is not specified [[#203](https://github.com/JetBrains/lets-plot/issues/203)].
- `scale_fill_discrete` should not replace the default discrete scale (Brewer) with 'HUE' scale [[#172](https://github.com/JetBrains/lets-plot/issues/172)].
 
### Changed
- Lets-plot vector map-tiles are configured by default.
- PACIFIC_BLUE color (#118ED8) replaced DARK_BLUE as a default color/fill value on plots.
- Default discrete color scheme is now ColorBrewer's Set2 (was Dark2)
 
### Deprecated
- Function `gg_image_matrix()`. The new function is: `image_matrix()` in the `lets_plot.bistro.im` module.
   The new function is not any longer displaying matrix by itself but returns a GGBunch object instead. 
   Unlike the depricated `gg_image_matrix()` function, it is not imported by `from lets_plot import *` statement.
    
   Usage: 
   ```
   from lets_plot.bistro.im import image_matrix
   image_matrix()
   ```    

## [1.5.2] - 2020-08-10
### Fixed
- map_titanic.ipynb : UnboundLocalError: local variable 'map_join' referenced before assignment [[#182](https://github.com/JetBrains/lets-plot/issues/182)]
- Add the `map_join` parameter to all geoms which support `map` parameter (py) [[#183](https://github.com/JetBrains/lets-plot/issues/183)]

## [1.5.1] - 2020-08-06
### Added
- scale_x_reverse() / scale_y_reverse()
- Mnemonics for the `resolution` parameter in the `regions.boundaries()` function. 

### Fixed
- Exception when using `coord_map` with `xlim` [[#173](https://github.com/JetBrains/lets-plot/issues/173)]
- Clipped tooltip [[#155](https://github.com/JetBrains/lets-plot/issues/155)]
- Text on axis is clipped after hiding the tick-marks in theme [[#160](https://github.com/JetBrains/lets-plot/issues/160)]
- Tooltip on x axis with no title shouldn't be above the axis line [[#161](https://github.com/JetBrains/lets-plot/issues/161)]
- NPE while creating scale mapper when data series contains only nulls.
- Default Geocoding server url.
 
## [1.5.0] - 2020-07-15
### Added
- geocoding package
 
### Fixed 
- NPE on geom_tile when data contains null-s.
- The order of values in the `limits` parameter on discrete scales is ignored.
- Livemap is not shown in GGBunch.
 
## [1.4.2] - 2020-05-28
### Added
- Plugin for IntelliJ IDEA / PyCharm is available. For more info see the plugin homepage: [Lets-Plot in SciView](https://plugins.jetbrains.com/plugin/14379-lets-plot-in-sciview).  

### Changed
- Plots build time reduced (up to 4X)

## [1.4.1] - 2020-05-20
### Added
- `geom_smooth`: automatic sampling when n>1000 and the method is LOESS.
 
### Fixed 
- 'map_join' fails when the `map` argument is a dictionary [[#130](https://github.com/JetBrains/lets-plot/issues/130)]
 
### Changed
- HTML output was modified allow better integration with PyCharm.
 
### Removed
- The deprecated method 'load_lets_plot_js()'.  

## [1.4.0] - 2020-05-13
### Added
- Support for interactive maps.
- The `as_discrete()` function.
- Polynomial regression of an arbitrary degree (the`lm` smoothing method) in *geom_smooth*.
- `axis_tooltip`, `axis_tooltip_x`, `axis_tooltip_y` parameters in `theme()`
- Kaggle and Datalore notebook demos.

### Fixed
- Severe performance degradation when using discrete scales [[#119](https://github.com/JetBrains/lets-plot/issues/119)].
- Opaque background to better support PyCharm dark theme [[#121](https://github.com/JetBrains/lets-plot/issues/121)].
- Auto-detection of a Kaggle environment.
- Limits on the maximum plot size [[#115](https://github.com/JetBrains/lets-plot/issues/115)].
- Installation instructions for Windows users [[#118 MinGW](https://github.com/JetBrains/lets-plot/issues/118)].

### Changed
- More slick shape for tooltips on the axis.

## [1.3.0] - 2020-03-26
### Added
- Python 3.6 support.
- Windows platform support.
- SVG/HTML export to file.
- Offline mode for Jupyter notebooks.
- Support for cloud-based notebooks like Google Colab and Datalore.
- JVM Maven artefacts released.
 
 ### Fixed
- Tooltip on `geom_rect` and `geom_vline`.
- Error when date-time series contains `NaT` value.
 
 ### Changed
- Optional `load_lets_plot_js()` function is now deprecated.
- Initialisation call `LetsPlot.setup_html()` is now mandatory in Jupyter.

## [1.2.1] - 2020-02-13
### Fixed
- tooltip not showing in geom_polygon

## [1.2.0] - 2020-02-12
### Added
- *geopandas* support in geoms: point, path, polygon, rect, text.
- support for LOESS smoothing method in *geom_smooth*.
- new geometry layers: *geom_crossbar*, *geom_linerange*, *geom_pointrange*, *geom_bin2d*.
- support for coordinate stystem *xlim*, *ylim* parameters (i.e. "clipping").

### Fixed
- Was not working `weight` parameter in stats: bin, count, bin2d, density, density2d
- incorrect tooltip line 'NaN' in geom_boxplot.
- ambiguous tooltip positioning in geoms: tile, bin2d.
- cropped text in tooltip.

## [1.1.0] - 2019-12-17
### Added
- *GGBunch*. Combines several different plots into one graphical object.
- *geom_image()*. Displays an image specified by ndarray with shape (n,m) or (n,m,3) or (n,m,4). 
- *gg_image_matrix()*. A utility helping to combine several images into one graphical object.
- user_guide.ipynb
- ggbunch.ipynb  
- scatter_matrix.ipynb
- image_101.ipynb
- image_fisher_boat.ipynb
- image_matrix.ipynb

### Changed
- Switched to Kotlin 1.3.61

### Fixed
- *scale_datetime()*. Date-time formatting in tooltips.
- Links in README_PYTHON.md

## [1.0.0] - 2019-11-27
### Changed
 - First public release.
