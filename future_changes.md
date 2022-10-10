## [2.5.1] - 2022-??-??

### Added

- Residual plot `geom_residuals()`.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/residual_plot.ipynb).


- `geom_text(), geom_label()`:

  -  the 'newline' character (`\n`) now works as `line break`  ([[#605](https://github.com/JetBrains/lets-plot/issues/605)])
  - `lineheight` aesthetic ([[#324](https://github.com/JetBrains/lets-plot/issues/324)])
  - `nudge_x, nudge_y` parameters  ([[#324](https://github.com/JetBrains/lets-plot/issues/324)])

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22d/geom_text_multiline.ipynb).  

### Changed

### Fixed

- element_blank() has no effect in theme legend_title [[#608](https://github.com/JetBrains/lets-plot/issues/608)].