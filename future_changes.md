## [4.6.3] - 2025-mm-dd

### Added

- More named colors, including all HTML/CSS colors.
- Support different color naming styles like `dark-gray`, `darkgrey`, `dark_grey`, `DARKGRAY`, etc.
- Grayscale colors from `gray0` (black) to `gray100` (white).
- Geometries:

    - `geom_sina()` [[#1298](https://github.com/JetBrains/lets-plot/issues/1298)].

      See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25b/geom_sina.ipynb).
  
    - `geom_text_repel()` and `geom_label_repel()` for avoiding text overlaps in plots [[#1092](https://github.com/JetBrains/lets-plot/issues/1092)].  
      See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/dev/notebooks/geom_text_repel.ipynb).

- Combining Discrete and Continuous Layers [[#1279](https://github.com/JetBrains/lets-plot/issues/1279)].  
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25b/numeric_data_on_discrete_scale.ipynb).
- `waterfall_plot` - extra layers support [[#1344](https://github.com/JetBrains/lets-plot/issues/1344)].  
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25b/waterfall_plot_layers.ipynb).
    

### Changed

- [**BREAKING**] The `position_dodgev()` function and the `'dodgev'` value for the `position` parameter are deprecated and will be removed in future releases.
- [**BREAKING**] The y-oriented boxplot now use the aesthetics `xlower`/`xmiddle`/`xupper` instead of  `lower`/`middle`/`upper`.
- Updated RGB values for `lightgray` and `green`. To restore the previous colors, use `gray75` and `lime`, respectively. 
- `geom_violin`: tooltips are not shown in the centerline of the violin if `show_half != 0`.
- `geom_crossbar`: the midline is not shown in the legend when `fatten` is set to 0, or when there is no mapping for it.
- `waterfall_plot`: the appearance of the legend has been improved.
- `geom_pointrange`: the midpoint will not be drawn if the y aesthetic is set to `None`.

### Fixed

- `geom_boxplot`: unable to draw a y-oriented plot with `stat='identity'` [[#1319](https://github.com/JetBrains/lets-plot/issues/1319)]
- Can't add layer which uses continuous data to a plot where other layers use discrete input [[#1323](https://github.com/JetBrains/lets-plot/issues/1323)].
- Multiline legend labels are not vertically centered with their keys [[#1331](https://github.com/JetBrains/lets-plot/issues/1331)]   
- Poor alignment in legend between columns [[#1332](https://github.com/JetBrains/lets-plot/issues/1332)]
