## [2.5.1] - 2022-??-??

### Added

- `geom_text(), geom_label()`:

  -  the 'newline' character (`\n`) now works as `line break`  ([[#605](https://github.com/JetBrains/lets-plot/issues/605)])
  - `lineheight` aesthetic ([[#324](https://github.com/JetBrains/lets-plot/issues/324)])
  - `nudge_x, nudge_y` parameters ([[#324](https://github.com/JetBrains/lets-plot/issues/324)])
  - special text alignments (`vjust` and `hjust`): `"inward"` and `"outward"` ([[#324](https://github.com/JetBrains/lets-plot/issues/324)])

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/geom_text_new_features.ipynb).

- `extent` parameter in `geom_image()`

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/image_extent.ipynb).

### Changed

### Fixed

- element_blank() has no effect in theme legend_title [[#608](https://github.com/JetBrains/lets-plot/issues/608)].
- livemap: add support of geom_label parameters [[#601](https://github.com/JetBrains/lets-plot/issues/601)].
- Tooltip: different formats for same aesthetic Y [[#579](https://github.com/JetBrains/lets-plot/issues/579)].
- Positioning with "constant" x/y doesn't work on axis with log10 transform [[#618](https://github.com/JetBrains/lets-plot/issues/618)].
- Positional "constant" doesn't honor axis limits [[#619](https://github.com/JetBrains/lets-plot/issues/619)].