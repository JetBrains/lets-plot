## [4.2.1] - 2024-mm-dd

### Added
- `coord_polar()` function for polar coordinate system  
**[TODO: ADD NOTEBOOK]**
- `panel_grid_ontop, panel_grid_ontop_x, panel_grid_ontop_y` parameters in `theme()`
- `panel_border_ontop` parameter in `theme()`
- `panel_inset` parameter in `theme()`  
  **[TODO: ADD NOTEBOOK]**

- The `scale_mapper_kind` parameter in `scale_*_continuous()` and `scale_*_discrete()` to specify the scale type 
('identity', 'color_gradient', 'color_gradient2', 'color_gradientn', 'color_hue', 'color_grey', 'color_brewer', 'color_cmap', 'size_area').

- `geom_curve()`

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24a/geom_curve.ipynb).

- Aesthetics to adjust start/end coordinate of segment/curve: `size_start, size_end, stroke_start, stroke_end`.
 
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24a/geom_curve.ipynb).

- `setup_show_ext()` function is for display plots in an external browser.

- `alpha_stroke` parameter in `geom_label()` to enable the applying of `alpha` to `color` [[#1029](https://github.com/JetBrains/lets-plot/issues/1029)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24a/geom_label_alpha_stroke.ipynb).


### Changed

- [BREAKING] Function `geom_image()` is removed. Please use `geom_imshow()` instead.
- Parameter `axis_ontop` in `theme()` is now `True` by default.
         

### Fixed

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