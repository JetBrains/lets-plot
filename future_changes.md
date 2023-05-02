## [3.2.0] - 2023-05-??

### Added

- New geometry `geom_lollipop()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23b/geom_lollipop.ipynb).


- `stroke` aesthetic for `geom_point()`, `geom_jitter()`, `geom_qq()`, `geom_qq2()`, `geom_pointrange()`, `geom_dotplot()`, `geom_ydotplot()` and `outlier_stroke` parameter for `geom_boxplot()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23b/aes_stroke.ipynb).


- New aesthetic `linewidth`. Used only for `geom_lollipop()` at the moment.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23b/geom_lollipop.ipynb).


- The 'newline' character (`\n`) now works as `line break` in legend text ([[#726](https://github.com/JetBrains/lets-plot/issues/726)])

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23b/legend_text_multiline.ipynb).


- Horizontal error bars and vertical "dodge" ([[#735](https://github.com/JetBrains/lets-plot/issues/735)]).
 
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23b/horizontal_error_bars.ipynb).

       
- Colorbar in `geom_imshow()`. Parameters `show_legend` and `color_by` [[#717](https://github.com/JetBrains/lets-plot/issues/717)].


### Changed

- [BREAKING] `geom_dotplot()` and `geom_ydotplot()` no longer supports parameter `stat`. Only default stats make sense for these geometries.


- Position adjustment settings:
    - `width, height` parameters of `geom_jitter()` have priority over the `width, height` parameters of `position_jitter()` function;
    - `geom_text(), geom_label()` use `stat='identity'` by default;
    - `nudge_x, nudge_y` parameters of `geom_text(), geom_label()` have priority over `x, y` parameters of position_nudge() function.


### Fixed

- livemap: memory leak caused by a document event handler.
- livemap: flickering when zooming with the buttons.
- Implement the 'stroke' aesthetic [[#320](https://github.com/JetBrains/lets-plot/issues/320)].
- geom_density2d: Internal error with None values in data [[#702](https://github.com/JetBrains/lets-plot/issues/702)].
- livemap: tooltip text doesn't reflect data under the cursor [[#709](https://github.com/JetBrains/lets-plot/issues/709)].
- Quantile should be shown in tooltip if the variable `..quantile..` is mapped to geom aesthetic.
- Bad default formatting for stat variables [[#654](https://github.com/JetBrains/lets-plot/issues/654)].
- The scale name does not apply with `as_discrete()` [[#653](https://github.com/JetBrains/lets-plot/issues/653)]. 
- Batik: geom_imshow() fail with an error: "The attribute "xlink:href" of the element <image> is required"
- Tooltip is not shown when configured for 'const' value [[#610](https://github.com/JetBrains/lets-plot/issues/610)].
- Fix crash when try to add a constant to a tooltip (e.g.`"^size"`, where `size` aesthetic is specified with a number).
- `geom_segment()` doesn't take into account the alpha [[#748](https://github.com/JetBrains/lets-plot/issues/748)].
- Batik bug with usage of "&" [[#713](https://github.com/JetBrains/lets-plot/issues/713)].
- HTML export: exclude computation messages from the output [[#725](https://github.com/JetBrains/lets-plot/issues/725)].
- "Variable not found" error in ggmarginal [[#681](https://github.com/JetBrains/lets-plot/issues/681)].
- Image export not working with `geom_imshow()` and `geom_raster()` [[LPK-175](https://github.com/JetBrains/lets-plot-kotlin/issues/175)].
- DateTime metadata is not applied for scales other than X/Y [[LPK-174](https://github.com/JetBrains/lets-plot-kotlin/issues/174)].
- Groups not sorted similarly when using facets [[#679](https://github.com/JetBrains/lets-plot-kotlin/issues/679)].
- Categorical ordering, it's not respected for Boxplot and violin plot [[#746](https://github.com/JetBrains/lets-plot-kotlin/issues/746)].
- facet_grid: Internal error [[#699](https://github.com/JetBrains/lets-plot/issues/699)].
- Export to SVG fails if breaks are given by integers [[#763](https://github.com/JetBrains/lets-plot/issues/763)].
- Remove hard IPython dependency [[#749](https://github.com/JetBrains/lets-plot/issues/749)].
- livemap: doesn't work well with gggrid [[#750](https://github.com/JetBrains/lets-plot/issues/750)].
- Tooltips bug [[LPK-176](https://github.com/JetBrains/lets-plot-kotlin/issues/176)].