## [4.4.0] - 2024-08-dd

### Added
- Waterfall plot [[#975](https://github.com/JetBrains/lets-plot/issues/975)]:

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24e/waterfall_plot.ipynb).

- `geom_band()` [[#733](https://github.com/JetBrains/lets-plot/issues/733)]:

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24e/geom_band.ipynb).

- Custom legends [[#774](https://github.com/JetBrains/lets-plot/issues/774)]:  
  - `manual_key` parameter in plot layer
  - `layer_key()` function

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24e/manual_legend.ipynb).
        
- In legends:
  - `title` parameter in `guide_legend()` and `guide_colorbar()` functions

    See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24e/legend_title.ipynb).

  - `override_aes` parameter in the `guide_legend()` function [[#807](https://github.com/JetBrains/lets-plot/issues/807)]:

    See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24e/legend_override_aes.ipynb).

- `plot_title_position` and `plot_caption_position` parameters in `theme()` [[#1027](https://github.com/JetBrains/lets-plot/issues/1027)]:

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24e/theme_plot_title_position.ipynb).

- `threshold` parameter in `geom_histogram()` [[#1122](https://github.com/JetBrains/lets-plot/issues/1122)]:  

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24e/geom_histogram_threshold.ipynb).
                                                          
- Color scales using Matplotlib's colormap [[#1110](https://github.com/JetBrains/lets-plot/issues/1110)]:

  Thanks to a contribution by Eric Gayer.

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24e/scale_cmapmpl.ipynb).

### Changed
- [**breaking change**] `guide_legend()`/`guide_colorbar()` require keyword arguments for 'nrow'/'barwidth' and other parameters except 'title'.
- The triangular point shape is now anchored to the data point via the centroid
- `as_discrete()` is added to `lets-plot` wildcard import (i.e. when using `from lets-plot import *`) 
 
### Fixed
- ggsave (.svg) transforms geom_text integer to float [[#626](https://github.com/JetBrains/lets-plot/issues/626)].
- Int DataFrame column names are being converted to float string representation [[#901](https://github.com/JetBrains/lets-plot/issues/901)].
- `linetype` = 0 ('blank') should make lines invisible [[#712](https://github.com/JetBrains/lets-plot/issues/712)].
- `geom_density2d`: support weight aesthetic [[#791](https://github.com/JetBrains/lets-plot/issues/791)].
- Discrete axis labels unnecessarily rotate 90 degrees when applying coord system limits.
- Axis title via `labs()` breaks the date-time scale [[#1113](https://github.com/JetBrains/lets-plot/issues/1113)].
- JavaFX IllegalArgumentException: Unsupported attribute `display` in Pane.

