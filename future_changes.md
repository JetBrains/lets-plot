## [2.5.1] - 2022-11-??

### Added

- `geom_text(), geom_label()`:

  -  the 'newline' character (`\n`) now works as `line break`  ([[#605](https://github.com/JetBrains/lets-plot/issues/605)])
  - `lineheight` aesthetic ([[#324](https://github.com/JetBrains/lets-plot/issues/324)])
  - `nudge_x, nudge_y` parameters ([[#324](https://github.com/JetBrains/lets-plot/issues/324)])
  - special text alignments (`vjust` and `hjust`): `"inward"` and `"outward"` ([[#324](https://github.com/JetBrains/lets-plot/issues/324)])

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/geom_text_new_features.ipynb).

- `vjust` parameter in `position_stack()` and `position_fill()` [[#323](https://github.com/JetBrains/lets-plot/issues/323)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/position_stack.ipynb).

- `use_crs` parameter in `geom_map()` and other geoms, working with `GeoDataFrame`
 
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/map_use_crs.ipynb).

- `geom_imshow()` (former `geom_image()`):

  - `extent` parameter

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/image_extent.ipynb).

  - `vmin, vmax, cmap` parameters                    

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/image_grayscale.ipynb).

- `image_matrix()`:

  - `vmin, vmax, cmap` parameters

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/image_matrix.ipynb).


### Changed

- `geom_image()` renamed to `geom_imshow()`

  See updated examples: [image 101](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/image_101.ipynb),
  [Fisher's boat](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/image_fisher_boat.ipynb).

### Fixed

- element_blank() has no effect in theme legend_title [[#608](https://github.com/JetBrains/lets-plot/issues/608)].
- livemap: add support of geom_label parameters [[#601](https://github.com/JetBrains/lets-plot/issues/601)].
- Tooltip: different formats for same aesthetic Y [[#579](https://github.com/JetBrains/lets-plot/issues/579)].
- Positioning with "constant" x/y doesn't work on axis with log10 transform [[#618](https://github.com/JetBrains/lets-plot/issues/618)].
- Positional "constant" doesn't honor axis limits [[#619](https://github.com/JetBrains/lets-plot/issues/619)].
- Parameter `norm` in `geom_imshow()`.
- Several issues leading to crush in Swing/Batik apps. Related to [[discussions](https://github.com/JetBrains/lets-plot-kotlin/discussions/138)]
- Default value for parameter `whisker_width` in `geom_boxplot()` is 0.5.