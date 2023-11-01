## [4.1.0] - 2023-11-dd

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


-  `plot_margin` parameter in `theme()` [[#856](https://github.com/JetBrains/lets-plot/issues/856)].
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23e/theme_plot_margin.ipynb).

 - Python 3.12 support [[#907](https://github.com/JetBrains/lets-plot/issues/907)].


- `lab` colorspace support [[#876](https://github.com/JetBrains/lets-plot/issues/876)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23e/lab_colorspace.ipynb).


### Changed

- [BREAKING] `stat_summary()` and `stat_summary_bin` no longer supports computing of additional variables through the specifying of mappings.

### Fixed

- Tooltips are trimmed and not visible on a very narrow chart [[#837](https://github.com/JetBrains/lets-plot/issues/837)].
- Inability to use the spelling "grey" for the color grey (via "gray" only).
- `geom_crossbar`: an error occurs if the mapped values of aesthetics are not populated [[#886](https://github.com/JetBrains/lets-plot/issues/886)].
- Exception label is unresizeble, uncopyable and uncontrollable [[#902](https://github.com/JetBrains/lets-plot/issues/902)].
- Flickering during plot downsizing [[#888](https://github.com/JetBrains/lets-plot/issues/888)].
- Bad default formatting of numeric values in annotations [[#905](https://github.com/JetBrains/lets-plot/issues/905)].
- corr_plot: unexpected whitespace between the "geometry area" and the legend [[#877](https://github.com/JetBrains/lets-plot/issues/877)].
- scale_log: an option to generate only breaks which are integer powers of 10 needed [[#850](https://github.com/JetBrains/lets-plot/issues/850)].
- Trimmed legend when bounds of the rightmost X-axis tick label exceeds the axis length [[#851](https://github.com/JetBrains/lets-plot/issues/851)].
- HTML files exported using ggsave() are missing the encoding specification [[#900](https://github.com/JetBrains/lets-plot/issues/900)].
- Subplot themes not inherited by parent [[LPK-#197](https://github.com/JetBrains/lets-plot-kotlin/issues/197)].
- Saving plots in PDF format using ggsave() [[#710](https://github.com/JetBrains/lets-plot/issues/710)].
- `element_blank()` has no effect on plot title/subtitle/caption in `theme()` [[#913](https://github.com/JetBrains/lets-plot/issues/913)].
- Lollipop in legend is disproportionately large [[LPK-216](https://github.com/JetBrains/lets-plot-kotlin/issues/216)].
- geomBar with fill, produces tooltips artefacts [[#895](https://github.com/JetBrains/lets-plot/issues/895)].
- Exception, when trying to build plot with column name containing line breakes [[#894](https://github.com/JetBrains/lets-plot/issues/894)].
