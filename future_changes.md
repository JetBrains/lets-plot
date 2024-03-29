## [4.3.1] - 2024-mm-dd

### Added

- Parameter `labwidth` in `facet_wrap()` and `x_labwidth/y_labwidth` in `facet_grid()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24b/facet_multiline_titles.ipynb).


### Changed
      
### Fixed
- Error when build geom_smooth() with se=False [[#1050](https://github.com/JetBrains/lets-plot/issues/1050)].
- livemap: when release the mouse button from outside the map, it gets stuck in panning mode [[#1044](https://github.com/JetBrains/lets-plot/issues/1044)].
- Incorrect 'plot_background' area (with empty space capture) [[#918](https://github.com/JetBrains/lets-plot/issues/918)].
- Support arrow() in geom_spoke() [[#986](https://github.com/JetBrains/lets-plot/issues/986)].
- Support geom_spoke() in geom_livemap() [[#988](https://github.com/JetBrains/lets-plot/issues/988)].
- arrow on curve sometimes looks weird [[#1041](https://github.com/JetBrains/lets-plot/issues/1041)].
- Improve documentation for parameter `position` [[#1047](https://github.com/JetBrains/lets-plot/issues/1047)].
- Livemap: `vjust` implemented incorrectly [[#1051](https://github.com/JetBrains/lets-plot/issues/1051)].
- Add tooltips for `geom_curve()` [[#1053](https://github.com/JetBrains/lets-plot/issues/1053)].
- Incorrect position for bar annotations when specifying `scale_x_reverse()/scale_y_reverse()` [[#1057](https://github.com/JetBrains/lets-plot/issues/1057)].
- Missing outer bar annotations when specifying `scale_x_reverse()/scale_y_reverse()` [[#1058](https://github.com/JetBrains/lets-plot/issues/1058)].
- `geom_density2d`: the doc missing some 'computed' variables [[#1062](https://github.com/JetBrains/lets-plot/issues/1062)].
- Any way to line-wrap facet labels? [[LPK-237](https://github.com/JetBrains/lets-plot-kotlin/issues/237)].
- Missing marginal gridlines.
- DateTime is not recognised in polars.DataFrame [[#961](https://github.com/JetBrains/lets-plot/issues/961)].