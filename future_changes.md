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
  

### Changed
- [**BREAKING**] The `position_dodgev()` function and the `'dodgev'` value for the `position` parameter are deprecated and will be removed in future releases.
- Updated RGB values for `lightgray` and `green`. To restore the previous colors, use `gray75` and `lime`, respectively. 
- `geom_violin`: tooltips are not shown in the centerline of the violin if `show_half != 0`.

### Fixed

- Can't add layer which uses continuous data to a plot where other layers use discrete input [[#1323](https://github.com/JetBrains/lets-plot/issues/1323)].
- Multiline legend labels are not vertically centered with their keys [[#1331](https://github.com/JetBrains/lets-plot/issues/1331)]   
- Poor alignment in legend between columns [[#1332](https://github.com/JetBrains/lets-plot/issues/1332)]
