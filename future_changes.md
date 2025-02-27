## [4.5.3] - 2025-mm-dd

### Added

- Grouping plots:
  - `ggbunch()` function: combining plots with custom layout.

    See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25a/ggbunch_indonesia.ipynb).
                                                                                                                       
  - Support for plot **title, subtitle, caption, margins** and **insets** in `gggrid()` and `ggbunch()`. 
  
- Geometries:
  - `geom_hex()` [[#556](https://github.com/JetBrains/lets-plot/issues/556)].

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25a/geom_hex.ipynb).

  - Parameters `width_unit` and `height_unit` in `geom_errorbar()`, `geom_tile()` and `geom_hex()` [[#1288](https://github.com/JetBrains/lets-plot/issues/1288)]:

    See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25a/param_width_unit.ipynb).

- Texts and labels:
  - `hjust` and `vjust` parameters for axis labels [[#1227](https://github.com/JetBrains/lets-plot/issues/1227)],[[#1230](https://github.com/JetBrains/lets-plot/issues/1230)].
  
    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25a/axis_label_justification.ipynb).

  - multiline support for axis labels [[#948](https://github.com/JetBrains/lets-plot/issues/948)].

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25a/multiline_axis_labels.ipynb).

  - Markdown support for plot **title**, **subtitle**, **caption**, and axis labels [[#1256](https://github.com/JetBrains/lets-plot/issues/1256)].

    See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25a/markdown.ipynb).

- Theme:
  - Parameters `legend_ticks` and `legend_ticks_length` for fine-grained control over colorbar tick marks [[#1262](https://github.com/JetBrains/lets-plot/issues/1262)].

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25a/theme_legend_ticks.ipynb).

- Parameter `spacer` in `image_matrix()` function.

- `transparent`/`blank`/`''` color [[#1281](https://github.com/JetBrains/lets-plot/issues/1281)].

### Changed

- [**DEPRECATED**] class `GGBunch` is deprecated. Please use `ggbunch()` function instead.
- Axis breaks: changed default `lower_exp_bound` to -7 and `upper_exp_bound` to 6 (same as in `theme(...)`).
- Axis labels: changed default justification for rotated labels.
- Axis labels: changed orientation of automatic vertical labels.
- [**BREAKING**] geoms `tile, bin2d, contour, contourf, density2d, density2df` : default coordinate system changed from 'fixed' to 'cartesian'.

### Fixed
- Incorrectly rendered Area chart [[#1295](https://github.com/JetBrains/lets-plot/issues/1295)].
- Tooltip should not cover and hide the geometry that it provides info for [[#1275](https://github.com/JetBrains/lets-plot/issues/1275)].
- General purpose `scale_continuous`: can't use the `expand` parameter [[#1285](https://github.com/JetBrains/lets-plot/issues/1285)].
- Error when using `stat='summary'` if the data contains NaN values [[#1301](https://github.com/JetBrains/lets-plot/issues/1301)].
- Broken `plot_background` in `gggrid` [[#1124](https://github.com/JetBrains/lets-plot/issues/1124)].
- `plot_background` not inheriting from `rect` [[#1278](https://github.com/JetBrains/lets-plot/issues/1278)]
- gggrid: allow title and other labels for the entire figure [[#715](https://github.com/JetBrains/lets-plot/issues/715)].
- GGbunch: overall title [[#321](https://github.com/JetBrains/lets-plot/issues/321)].
- Expand discrete axis according to tile size with geom_tile [[#1284](https://github.com/JetBrains/lets-plot/issues/1284)].
- geom_bin2d implodes when the disparity in axes units is large [[#1303](https://github.com/JetBrains/lets-plot/issues/1303)].
- Livemap: parameters nudge_x and nudge_y have no effect on geom_text or geom_label [[#1048](https://github.com/JetBrains/lets-plot/issues/1048)].
- Livemap: Add zooming-in for geom_text()/geom_label() [[#1059](https://github.com/JetBrains/lets-plot/issues/1059)].
