## [3.1.1] - 2023-03-??

### Added

- `stroke` aesthetic for `geom_point()`, `geom_jitter()`, `geom_qq()`, `geom_qq2()`, `geom_pointrange()`, `geom_dotplot()`, `geom_ydotplot()` and `outlier_stroke` parameter for `geom_boxplot()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23b/stroke_aes.ipynb).

### Changed

### Fixed

- livemap: memory leak caused by a document event handler.
- livemap: flickering when zooming with the buttons.
- Implement the 'stroke' aesthetic [[#320](https://github.com/JetBrains/lets-plot/issues/320)].
- livemap: tooltip text doesn't reflect data under the cursor [[#709](https://github.com/JetBrains/lets-plot/issues/709)].
- Quantile should be shown in tooltip if the variable `..quantile..` is mapped to geom aesthetic.
- Bad default formatting for stat variables [[#654](https://github.com/JetBrains/lets-plot/issues/654)].