## [4.3.0] - 2024-03-dd

### Added
- `coord_polar()`

  The polar coordinate system is most commonly used for pie charts, but </br>
  it can also be used for constructing **Spyder or Radar charts** using the `flat` option.
  
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24a/coord_polar.ipynb).

- In the `theme()` function:
  - `panel_inset`  parameter - primarily used for plots with polar coordinates.

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24a/theme_panel_inset.ipynb).
  
  - `panel_border_ontop` parameter - enables the drawing of panel border on top of the plot geoms.
  - `panel_grid_ontop, panel_grid_ontop_x, panel_grid_ontop_y` parameters - enable the drawing of grid lines on top of the plot geoms.

- `geom_curve()`

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24a/geom_curve.ipynb).
  
- [**UNIQUE**] Visualizing graph-like data with `geom_segment()` and `geom_curve()`.
  
  - Aesthetics `size_start, size_end, stroke_start` and `stroke_end` enable better alignment of</br> 
  segments/curves with nodes of the graph by considering the size of the nodes.

  - The `spacer` parameter allows for additional manual fine-tuning.

  See:  
    - [A simple graph example](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24a/graph_edges.ipynb)
    - [An interactive map example](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24a/geom_curve_on_map.ipynb)

- `alpha_stroke` parameter in `geom_label()` to enable the applying of `alpha` to `color` [[#1029](https://github.com/JetBrains/lets-plot/issues/1029)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24a/geom_label_alpha_stroke.ipynb).

- Showing plots in external browser

  The `LetsPlot.setup_show_ext()` directive allows plots to be displayed in an external browser window.


### Changed

- [**BREAKING**] Function `geom_image()` is removed. Please use `geom_imshow()` instead.
- Parameter `axis_ontop` in `theme()` is now `True` by default.
         

### Fixed

- coord_map() should distort tiles size to account for different unit size at different latitudes[[#331](https://github.com/JetBrains/lets-plot/issues/331)].
- geom_errorbar(): wrong positioning of tooltips [[#992](https://github.com/JetBrains/lets-plot/issues/992)].
- geom_path(): tooltip position interpolation [[#855](https://github.com/JetBrains/lets-plot/issues/855)].
- Stacked bar-chart annotation: labels go out of the plot when zooming-in using coord_cartesian(xlim, ylim) [[#981](https://github.com/JetBrains/lets-plot/issues/981)].
- Facets: "free scales" options are ignored by discrete axis [[#955](https://github.com/JetBrains/lets-plot/issues/955)].
- Bar width is too large when x-domain is defined via x-scale limits [[#1013](https://github.com/JetBrains/lets-plot/issues/1013)].
- How to hide only main tooltip? [[LPK-#232](https://github.com/JetBrains/lets-plot-kotlin/issues/232)].
- Make middle strip in `geomCrossbar()` optional [[LPK-233](https://github.com/JetBrains/lets-plot-kotlin/issues/233)].
- Can't set None for coord limit [[#486](https://github.com/JetBrains/lets-plot/issues/486)].
- Scale limits don't work for bars/area [[LPK-219](https://github.com/JetBrains/lets-plot-kotlin/issues/219)], [[#978](https://github.com/JetBrains/lets-plot/issues/978)]. 
- No gridlines when axis_ontop=True [[#1012](https://github.com/JetBrains/lets-plot/issues/1012)].
- bar-plot: do not draw bar border adjacent to the axis [[#845](https://github.com/JetBrains/lets-plot/issues/845)].
- Displaying tooltips on a multilayer plot [[#1030](https://github.com/JetBrains/lets-plot/issues/1030)].
- Make segment geometry better suited for graphs visualization [[#572](https://github.com/JetBrains/lets-plot/issues/572)].