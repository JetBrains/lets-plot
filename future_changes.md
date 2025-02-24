## [4.5.3] - 2025-mm-dd

### Added
- `ggbunch()` function: combining plots with custom layout.

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25a/ggbunch_indonesia.ipynb).
                                                                                                                       
- Support for plot **title, subtitle, caption, margins** and **insets** in `gggrid()` and `ggbunch()`. 

- `geom_hex()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25a/geom_hex.ipynb).

- `spacer` parameter in `image_matrix()`.
- `hjust` and `vjust` parameters for axis labels.
  
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25a/axis_label_justification.ipynb).

- Parameters `width_unit` and `height_unit` in `geom_errorbar()`, `geom_tile()` and `geom_hex()` [[#1288](https://github.com/JetBrains/lets-plot/issues/1288)]:

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25a/param_width_unit.ipynb).

- Markdown support for plot title, subtitle, caption, and axis labels [[#1256](https://github.com/JetBrains/lets-plot/issues/1256)].

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25a/markdown.ipynb).

### Changed

- [**DEPRECATED**] class `GGBunch` is deprecated. Please use `ggbunch()` function instead.
- Axis breaks: changed default `lower_exp_bound` to -7 and `upper_exp_bound` to 6 (same as in `theme(...)`).
- Axis labels: changed default justification for rotated labels.
- [**BREAKING**] geoms `tile, bin2d, contour, contourf, density2d, density2df` : default coordinate system changed from 'fixed' to 'cartesian'.

### Fixed
- Tooltip should not cover and hide the geometry that it provides info for [[#1275](https://github.com/JetBrains/lets-plot/issues/1275)].
- General purpose `scale_continuous`: can't use the `expand` parameter [[#1285](https://github.com/JetBrains/lets-plot/issues/1285)].
- Incorrectly rendered Area chart [[#1295](https://github.com/JetBrains/lets-plot/issues/1295)].
- Error when using `stat='summary'` if the data contains NaN values [[#1301](https://github.com/JetBrains/lets-plot/issues/1301)].
- Broken `plot_background` in `gggrid` [[#1124](https://github.com/JetBrains/lets-plot/issues/1124)].
- gggrid: allow title and other labels for the entire figure [[#715](https://github.com/JetBrains/lets-plot/issues/715)].
- GGbunch: overall title [[#321](https://github.com/JetBrains/lets-plot/issues/321)].
- Expand discrete axis according to tile size with geom_tile [[#1284](https://github.com/JetBrains/lets-plot/issues/1284)].
- geom_bin2d implodes when the disparity in axes units is large [[#1303](https://github.com/JetBrains/lets-plot/issues/1303)].
- Livemap: parameters nudge_x and nudge_y have no effect on geom_text or geom_label [[#1048](https://github.com/JetBrains/lets-plot/issues/1048)].
- Livemap: Add zooming-in for geom_text()/geom_label() [[#1059](https://github.com/JetBrains/lets-plot/issues/1059)].
