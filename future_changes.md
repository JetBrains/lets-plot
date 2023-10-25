## [4.0.2] - 2023-mm-dd

### Added

- New scale transformations: `'log2'` and `'symlog'`.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23e/new_scale_transformations.ipynb).

- Horizontal orientation by assigning y, xmin, xmax aesthetics of geoms:
  - `geom_errorbar()`;
  - `geom_crossbar()`;
  - `geom_pointrange()`;
  - `geom_linerange()`;
  - `geom_ribbon()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23e/horizontal_geoms.ipynb).

- Annotations for bar plot:

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23e/bar_annotations.ipynb).


### Changed

- [BREAKING] `stat_summary()` and `stat_summary_bin` no longer supports computing of additional variables through the specifying of mappings.

### Fixed

- Tooltips are trimmed and not visible on a very narrow chart [[#837](https://github.com/JetBrains/lets-plot/issues/837)].
- Inability to use the spelling "grey" for the color grey (via "gray" only).
- `geom_crossbar`: an error occurs if the mapped values of aesthetics are not populated [[#886](https://github.com/JetBrains/lets-plot/issues/886)].
- Husl palette equivalent [[#877](https://github.com/JetBrains/lets-plot/issues/876)].
- Exception label is unresizeble, uncopyable and uncontrollable [[#902](https://github.com/JetBrains/lets-plot/issues/902)].
- Flickering during plot downsizing [[#888](https://github.com/JetBrains/lets-plot/issues/888)].
- Bad default formatting of numeric values in annotations [[#905](https://github.com/JetBrains/lets-plot/issues/905)].
- corr_plot: unexpected whitespace between the "geometry area" and the legend [[#877](https://github.com/JetBrains/lets-plot/issues/877)].
- Trimmed legend when bounds of the rightmost X-axis tick label exceeds the axis length [[#851](https://github.com/JetBrains/lets-plot/issues/851)].