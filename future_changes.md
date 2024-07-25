## [4.3.4] - 2024-mm-dd

### Added
- Legend title in `guide_legend()` and `guide_colorbar()`.

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24e/legend_title.ipynb).

- Parameter `override_aes` in `guide_legend()`.

  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24e/legend_override_aes.ipynb).

- Manual customization of the legend [[#774](https://github.com/JetBrains/lets-plot/issues/774)]:<br/>
  `manual_key` layer parameter that specifies a legend label or advanced settings using the `layer_key()` function.

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24e/manual_legend.ipynb).

- `plot_title_position` and `plot_caption_position` parameters in `theme()` [[#1027](https://github.com/JetBrains/lets-plot/issues/1027)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24e/theme_plot_title_position.ipynb).



### Changed
- [**breaking change**] `guide_legend()/guide_colorbar()` require keyword arguments for 'nrow'/'barwidth' other parameters except 'title'.
- The triangular point shape is now anchored to the data point via the centroid.
 
### Fixed
- `linetype` = 0 ('blank') should make lines invisible [[#712](https://github.com/JetBrains/lets-plot/issues/712)].
- `geom_density2d`: support weight aesthetic [[#791](https://github.com/JetBrains/lets-plot/issues/791)].
- Int DataFrame column names are being converted to float string representation [[#901](https://github.com/JetBrains/lets-plot/issues/901)].
- Discrete axis labels unnecessary rotate 90 degrees when applying coord system limits.
- Axis title via `labs()` breaks the date-time scale [[#1113](https://github.com/JetBrains/lets-plot/issues/1113)].