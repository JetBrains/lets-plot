## [4.5.3] - 2025-mm-dd

### Added
- `spacer` parameter in `image_matrix()`

### Changed

- Axis breaks: changed default `lower_exp_bound` to -7 and `upper_exp_bound` to 6 (same as in `theme(...)`).

### Fixed
- Tooltip should not cover and hide the geometry that it provides info for [[#1275](https://github.com/JetBrains/lets-plot/issues/1275)].
- General purpose `scale_continuous`: can't use the `expand` parameter [[#1285](https://github.com/JetBrains/lets-plot/issues/1285)].
- Broken `plot_background` in `gggrid` [[#1124](https://github.com/JetBrains/lets-plot/issues/1124)].
- gggrid: allow title and other labels for the entire figure [[#715](https://github.com/JetBrains/lets-plot/issues/715)].
- GGbunch: overall title [[#321](https://github.com/JetBrains/lets-plot/issues/321)].

