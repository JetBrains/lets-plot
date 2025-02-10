## [4.5.3] - 2025-mm-dd

### Added
- `ggbunch()` function: combining plots with custom layout.

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25a/ggbunch_indonesia.ipynb).
                                                                                                                       
- Support for plot **title, subtitle, saption, margins** and **insets** in `gggrid()` and `ggbunch()`. 

- `geom_hex()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25a/geom_hex.ipynb).

- `spacer` parameter in `image_matrix()`.

### Changed

- [**DEPRECATED**] class `GGBunch` is deprecated. Please use `ggbunch()` function instead.
- Axis breaks: changed default `lower_exp_bound` to -7 and `upper_exp_bound` to 6 (same as in `theme(...)`).

### Fixed
- Tooltip should not cover and hide the geometry that it provides info for [[#1275](https://github.com/JetBrains/lets-plot/issues/1275)].
- General purpose `scale_continuous`: can't use the `expand` parameter [[#1285](https://github.com/JetBrains/lets-plot/issues/1285)].
- Incorrectly rendered Area chart [[#1295](https://github.com/JetBrains/lets-plot/issues/1295)].
- Broken `plot_background` in `gggrid` [[#1124](https://github.com/JetBrains/lets-plot/issues/1124)].
- gggrid: allow title and other labels for the entire figure [[#715](https://github.com/JetBrains/lets-plot/issues/715)].
- GGbunch: overall title [[#321](https://github.com/JetBrains/lets-plot/issues/321)].
- Livemap: parameters nudge_x and nudge_y have no effect on geom_text or geom_label [[#1048](https://github.com/JetBrains/lets-plot/issues/1048)].
