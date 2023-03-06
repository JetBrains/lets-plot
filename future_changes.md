## [3.1.0] - 2023-03-??

### Added

- `gggrid()` function.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/plot_grid.ipynb).


- `joint_plot()`

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/joint_plot.ipynb).


- Export to PNG files in `ggsave()`.

  Note: export to PNG file requires the [CairoSVG](https://pypi.org/project/CairoSVG) library.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/export_to_png.ipynb).


- Axis `position` parameter in position scales `scale_x_*(), scale_y_*()`.       

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/axis_position.ipynb).


- `angle` parameter in `element_text()` in `theme()`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/axis_text_angle.ipynb).  
                

- Additional "color" aesthetics: `paint_a, paint_b, paint_c`.
    
  These aesthetics are flexible and can be used as either "color" or "fill" as needed. See [Multiple Color Scales](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/multiple_color_scales.ipynb) demo.
                         
  Also added a set of related "color scale" functions with the "aesthetic" parameter for configuring of additional color scales.

  See [New "Scale" Functions](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/scale_functions.ipynb) demo.

        
-  Drawing quantile lines and filling quantile areas in `geom_violin()` and `geom_density()` 

   See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/quantile_parameters.ipynb).


- `geodesic` parameter for `geom_segment()` and `geom_path()`.

   See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/param_geodesic.ipynb).


- `density2d` and `density2df` geometry types in `residual_plot()`.


### Changed

- The `MinGW` toolchain is no longer required for installing of `Lets-Plot` on `Windows`.

- [BREAKING] `geom_violin()` no longer supports parameter `draw_quantiles`. Use new `quantile_lines` and `quantiles` parameters as needed.

- [BREAKING] `stack` and `fill` position adjustments now stack objects on top of each other only if these objects belong to different **groups**.
  If necessary, use `mode="all""` in `position_stack()` or `position_fill()` to stack objects regardless of their group.  

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23a/position_stack.ipynb).


### Fixed

- Tooltip does not reflect `..quantile..` aesthetic change [[#658](https://github.com/JetBrains/lets-plot/issues/658)].
- `color_by` parameter of the `residual_plot()` should group the data points [[#662](https://github.com/JetBrains/lets-plot/issues/662)].
- 'map_join': variable is lost after "stat" [[#664](https://github.com/JetBrains/lets-plot/issues/664)].
- Error when tooltip has variable mapped to aesthetic used by stat [[#665](https://github.com/JetBrains/lets-plot/issues/665)].
- Groups not sorted similarly when `position='stack'` [[#673](https://github.com/JetBrains/lets-plot/issues/673)].
- Area ridges: fill overlaps geometry borders when colors are repeated [[#674](https://github.com/JetBrains/lets-plot/issues/674)].
- livemap: hide tooltips when user is zooming-in by double-clicks [[#659](https://github.com/JetBrains/lets-plot/issues/659)].
- livemap: wrong position when path goes through the antimeridian [[#682](https://github.com/JetBrains/lets-plot/issues/682)].
- livemap: wrong position if path is on a circle of latitude [[#683](https://github.com/JetBrains/lets-plot/issues/683)].
- livemap: tooltip may show wrong data on density2df [[#684](https://github.com/JetBrains/lets-plot/issues/684)].
- livemap: geom_text vjust="center" is a bit off [[#132](https://github.com/JetBrains/lets-plot/issues/132)].
- livemap: segment that goes through the antimeridian should be straight [[#692](https://github.com/JetBrains/lets-plot/issues/692)].
- livemap: apply alpha to the pie chart and to its tooltip color marker.
- Layout: uneven plot margins for the horizontal axis [[#705](https://github.com/JetBrains/lets-plot/issues/705)].
- Sampling: increase the default N for "pick sampling" and for other types of sampling [[#687](https://github.com/JetBrains/lets-plot/issues/687)].
