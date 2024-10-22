## [4.5.0] - 2024-10-dd

### Added

- Python 3.13 support

- `ggtb()`: enable **zoom/pan** interactivity on plot [[#983](https://github.com/JetBrains/lets-plot/issues/983)],[[#1019](https://github.com/JetBrains/lets-plot/issues/1019)]

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24g/interact_pan_zoom.ipynb).

- Interactive **links** in tooltips/labels/texts [[#1091](https://github.com/JetBrains/lets-plot/issues/1091)].

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24g/interactive_links.ipynb).

- Formatting:
  - **LaTeX** support: superscript, subscript ([[#861](https://github.com/JetBrains/lets-plot/issues/861)]) and Greek letters ([[#960](https://github.com/JetBrains/lets-plot/issues/960)]).

    See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/dev/notebooks/latex_support.ipynb).
  
  - Scientific notation: **compact form**. Enable the compact form using the  `exponent_format` parameter in `theme()` [[#1071](https://github.com/JetBrains/lets-plot/issues/1071)].

    See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24g/superscript_exponent.ipynb).

  [**WARNING**] Subscripts and superscripts are not supported in PDF and PNG exports.

- In `theme()`:
  - `legend_margin, legend_spacing, legend_spacing_x, legend_spacing_y, legend_box, legend_box_just, legend_box_spacing` parameters [[#1180](https://github.com/JetBrains/lets-plot/issues/1180)].

    See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24g/theme_legend_margins.ipynb).

  - `legend_key, legend_key_size, legend_key_width, legend_key_height, legend_key_spacing, legend_key_spacing_x, legend_key_spacing_y` parameters  [[#1181](https://github.com/JetBrains/lets-plot/issues/1181)].

    See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24g/theme_legend_key.ipynb).

  - `strip_background_x, strip_background_y, strip_text_x, strip_text_y` parameters [[#1195](https://github.com/JetBrains/lets-plot/issues/1195)].

    See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24g/theme_facet_strip_xy.ipynb).

- Custom `linetype` patterns [[#1198](https://github.com/JetBrains/lets-plot/issues/1198)]:
  - a list specifying the pattern of dashes and gaps used to draw the line: `[dash, gap, dash, gap, ...]`;
  - a list with a specified offset: `[offset, [dash, gap, dash, gap, ...]]`;
  - a string of an even number (up to eight) of hexadecimal digits specifying the lengths in consecutive positions in the string.

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24g/linetype_custom.ipynb).

- Geometries:
  - `geom_blank()` [[#831](https://github.com/JetBrains/lets-plot/issues/831)].

  - `base` parameter in `waterfall_plot()` [[#1159](https://github.com/JetBrains/lets-plot/issues/1159)].

    See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24g/waterfall_plot_base.ipynb).

  - `check_overlap` parameter in `geom_text()` and `geom_label()`.

    See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24g/check_overlap.ipynb).
       
  - `marginal` parameter in `qq_plot()`:

      See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24g/qq_plot_marginal.ipynb).

  - `inherit_aes` parameter in layers [[#1172](https://github.com/JetBrains/lets-plot/issues/1172)].

- `expand_limits()` [[#820](https://github.com/JetBrains/lets-plot/issues/820)].

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24g/expand_limits.ipynb).

- Support for 3-character hex color codes     


### Changed

- Parameters `labwidth` in `facet_wrap()`, `x_labwidth, y_labwidth` in `facet_grid()`: the maximum label length is applied after splitting at `\n` separators, if present.

- Use `theme_light()` as the default theme in `qq_plot()`.

- [**BREAKING**] For API deprecated in v4.0 the deprecation level raised to "Error" (only relevant for Kotlin clients).

- [**BREAKING**] The 'base-midnight', 'base-antique' and 'base-flatblue' tilesets are deprecated (no longer served by CARTO) and will be removed in future releases.

### Fixed

- Better Marimo Support (via [PR-2084](https://github.com/marimo-team/marimo/pull/2084)) [[#1018](https://github.com/JetBrains/lets-plot/issues/1018)].
- Support layering `aes()` multiple times [[#822](https://github.com/JetBrains/lets-plot/issues/822)].
- waterfall_plot: faceting doesn't work without a measure mapping [[#1152](https://github.com/JetBrains/lets-plot/issues/1152)].
- waterfall_plot: tooltips don't work with column names from original dataset [[#1153](https://github.com/JetBrains/lets-plot/issues/1153)].
- Legend icon background is not transparent when `legend_background` is set to "blank" in `theme` [[#1167](https://github.com/JetBrains/lets-plot/issues/1167)].
- Unable to display or save graph when using geom_path [[#1168](https://github.com/JetBrains/lets-plot/issues/1168)].
- Legend icon background should inherit the fill color of the plot panel (i.e. grey when `theme_grey` is used).
- Vertex sampling uses different tolerances for objects within the same plot [[#1174](https://github.com/JetBrains/lets-plot/issues/1174)].
- sampling_vertex_vw doesn't work as expected [[#1175](https://github.com/JetBrains/lets-plot/issues/1175)].
- sampling_vertex_dp may break rings [[#1176](https://github.com/JetBrains/lets-plot/issues/1176)].
- ggmarginal: bottom boxplot is broken [[#1189](https://github.com/JetBrains/lets-plot/issues/1189)].
- Offscreen cells are sometimes not rendered in JupyterLab.
- The legend_justification parameter doesn't work if the legend_position='top' [[#1031](https://github.com/JetBrains/lets-plot/issues/1031)].
- coord_polar: geom_point tooltips should take in account point size [[#1214](https://github.com/JetBrains/lets-plot/issues/1214)].
