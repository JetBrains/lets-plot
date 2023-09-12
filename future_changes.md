## [4.0.1] - 2023-09-dd

### Added

- `plot_message` parameter in `theme(...)` [[#863](https://github.com/JetBrains/lets-plot/issues/863)].  
  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23d/theme_plot_message.ipynb).  
                                     

- Add `geom_count()`/`stat_sum()` [[#821](https://github.com/JetBrains/lets-plot/issues/821)].  
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23d/geom_count.ipynb).  

### Changed
            
- If layer transparency is set via the alpha-channel in the colors RGBA specification and via the `alpha` aesthetic, \
  then the `alpha` aesthetic overrides the alpha-channel in the color. Previousely it was the opposite.  
   
 
- `geom_pie()` defaults:
  - "stroke" is visible and `stroke_side='both'` (was `stroke_side='outer'`).
  - the "hole" is not created automatically when `stroke_side = 'both'/'inner'` (was created automatically). 

- `geom_bar()` now has solid outline color by default (was transparent). 
      
- `geom_tile()`, `geom_bin2d()` now have solid outline color by default (was transparent). 
  - however, by default the `size` is 0 (i.e. tiles outline initially is not visible). 


### Fixed

- `geom_tile()`, `geom_bin2d()` : the `alpha` aesthetic is applied to the tiles outline. 
- `scale_x_datetime()`: error building plot for early dates [[#346](https://github.com/JetBrains/lets-plot/issues/346)].
- `geom_livemap()`: theme/flavor plot background is not shown [[#857](https://github.com/JetBrains/lets-plot/issues/857)].
- `geom_livemap()`: in AWT dragging a map in a facet moves maps in all facets.
- `geom_livemap()`: support rectangle 'linetype' [[#307](https://github.com/JetBrains/lets-plot/issues/307)].
- `theme_void()` + `flavor_xxx()`: no expected plot background [[#858](https://github.com/JetBrains/lets-plot/issues/858)].
- Inconsistent color in legend when using `paint_a/paint_b/paint_c` [[#867](https://github.com/JetBrains/lets-plot/issues/867)].
