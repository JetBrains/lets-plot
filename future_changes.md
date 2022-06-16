## [2.4.0] - 2022-06-??

### Added
               
- Python 3.10 support.

- `LetsPlot.set_theme()` - configuring a default plot theme.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/default_theme.ipynb).

- `geom_livemap`: support of the `arrow` parameter in `geom_segment` [[#131](https://github.com/JetBrains/lets-plot/issues/131)].
      
- Parameter `orientation` in geoms: `bar, boxplot, density, histogram, freqpoly, smooth, violin`.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/y_orientation.ipynb).

- New parameters in `theme()`:
  - `tooltip` - tooltip rectangle options;
  - `tooltip_text, tooltip_title_text` - tooltip text options;
  - `axis_tooltip_text, axis_tooltip_text_x, axis_tooltip_text_y` - axis tooltip text options.
  
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/tooltips_theme.ipynb).

- `face` parameter in `element_text()`.
    
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/set_font_faces.ipynb).

- `panel_border` parameter in `theme()` [[#542](https://github.com/JetBrains/lets-plot/issues/542)].
  
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/panel_border.ipynb).
  
- `scale_color_gradientn()` and `scale_fill_gradientn()` functions [[#504](https://github.com/JetBrains/lets-plot/issues/504)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/scale_%28color_fill%29_gradientn.ipynb).
         
- Marginal plots: the `ggmarginal()` function [[#200](https://github.com/JetBrains/lets-plot/issues/200)],
[[#384](https://github.com/JetBrains/lets-plot/issues/384)]. 

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/marginal_layers.ipynb).

- New functions for constructing Q-Q plots:
  - Geometries: 
    - `geom_qq()`
    - `geom_qq_line()`
    - `geom_qq2()`
    - `geom_qq2_line()`
  - Function in 'bistro' module:
    - `qq_plot()`

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22b/qq_plots.ipynb).

### Changed

- New style of tooltip color marker (two sidebars with a stroke color)
  and symbols in legend (rectangle with a stroke instead of a slash-line).
- New type of general tooltip for `geom_boxplot`: displayed under the cursor.
- Default sampling type for `geom_violin` switched from systematic to pick.

### Fixed

- Differences in tooltip color marker for plots with and without livemap.
- Labels out of plot when axis_text_y='blank' [[#525](https://github.com/JetBrains/lets-plot/issues/525)].
- NPE in corr_plot with null coefficients.
- Outliers are not shown when boxplot' alpha=0.
- Support for polars.DataFrame [[#526](https://github.com/JetBrains/lets-plot/issues/526)].
- JFX rendering issue that causes tooltips to stuck [[#539](https://github.com/JetBrains/lets-plot/issues/539)].
- Support trim parameter in density and ydensity stats [[#62](https://github.com/JetBrains/lets-plot/issues/62)].
- Unexpected point geometries on geom_livemap() [[#547](https://github.com/JetBrains/lets-plot/issues/547)].
- `geom_violin`: add missing parameters `kernel`, `bw`, `adjust`, `n`, `fs_max` to signature and docstring.