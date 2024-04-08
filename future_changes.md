## [4.3.1] - 2024-04-dd

### Added

- Parameter `dpi` in `ggsave()`, `to_png()` and `to_pdf()` functions [[#839](https://github.com/JetBrains/lets-plot/issues/839)].

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24b/parameter_dpi.ipynb).

- Parameter `labwidth` in `facet_wrap()` and `x_labwidth/y_labwidth` in `facet_grid()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24b/facet_multiline_titles.ipynb).

- Parameter `linetype` in `element_line()` and `element_rect()`  in `theme()` [[LPK-235](https://github.com/JetBrains/lets-plot-kotlin/issues/235)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24b/theme_linetype.ipynb).

- Parameter `arrow` in `geom_spoke()` [[#986](https://github.com/JetBrains/lets-plot/issues/986)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24b/geom_spoke_arrow.ipynb).

- Parameter `size_unit` in `geom_point()`, `geom_text()` and `geom_label()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24b/param_size_unit.ipynb).

### Changed
      
### Fixed
- Incorrect 'plot_background' area (with empty space capture) [[#918](https://github.com/JetBrains/lets-plot/issues/918)].
- geom_density2df: uneven borders [[#941](https://github.com/JetBrains/lets-plot/issues/941)].
- DateTime is not recognised in polars.DataFrame [[#961](https://github.com/JetBrains/lets-plot/issues/961)].
- Support geom_spoke() in geom_livemap() [[#988](https://github.com/JetBrains/lets-plot/issues/988)].
- NASA BlueMarble tiles have become blank [[#1000](https://github.com/JetBrains/lets-plot/issues/1000)].
- Line segments in geom_density2df() do not get interpolated when used with coord_polar() [[#1037](https://github.com/JetBrains/lets-plot/issues/1037)].
- arrow on segment: reduce arrow size for short segments [[#1040](https://github.com/JetBrains/lets-plot/issues/1040)].
- arrow on curve sometimes looks weird [[#1041](https://github.com/JetBrains/lets-plot/issues/1041)].
- livemap: when release the mouse button from outside the map, it gets stuck in panning mode [[#1044](https://github.com/JetBrains/lets-plot/issues/1044)].
- Improve documentation for parameter `position` [[#1047](https://github.com/JetBrains/lets-plot/issues/1047)].
- Error when build geom_smooth() with se=False [[#1050](https://github.com/JetBrains/lets-plot/issues/1050)].
- Livemap: `vjust` implemented incorrectly [[#1051](https://github.com/JetBrains/lets-plot/issues/1051)].
- Add tooltips for `geom_curve()` [[#1053](https://github.com/JetBrains/lets-plot/issues/1053)].
- Incorrect position for bar annotations when specifying `scale_x_reverse()/scale_y_reverse()` [[#1057](https://github.com/JetBrains/lets-plot/issues/1057)].
- Missing outer bar annotations when specifying `scale_x_reverse()/scale_y_reverse()` [[#1058](https://github.com/JetBrains/lets-plot/issues/1058)].
- `geom_density2d`: the doc missing some 'computed' variables [[#1062](https://github.com/JetBrains/lets-plot/issues/1062)].
- Weird and problematic behavior : lets-plot does not respect x and y. Sizing problem ?[[#1068](https://github.com/JetBrains/lets-plot/issues/1068)].
- Add `linetype` parameter in `elementLine()` and `elementRect()` [[LPK-235](https://github.com/JetBrains/lets-plot-kotlin/issues/235)].
- Any way to line-wrap facet labels? [[LPK-237](https://github.com/JetBrains/lets-plot-kotlin/issues/237)].
- Missing marginal gridlines.
- ggmarginal(): broken coloring [[#760](https://github.com/JetBrains/lets-plot/issues/760)].
 