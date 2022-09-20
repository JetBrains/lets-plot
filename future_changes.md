## [2.4.1] - 2022-??-??

### Added

- New pre-configured theme: `theme_bw()` [[#554](https://github.com/JetBrains/lets-plot/issues/554)]. 
    
  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/theme_bw.ipynb).
  
- Color schemes (flavors) applicable to existing themes:
  - `flavor_darcula()`;
  - `flavor_solarized_light()`;
  - `flavor_solarized_dark()`;
  - `flavor_high_contrast_light()`;
  - `flavor_high_contrast_dark()`.
  
  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/theme_flavors.ipynb).

- Viridis color scales: `scale_fill_viridis()`, `scale_fill_viridis()`.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/colors_viridis.ipynb).

- New parameters  in `element_text()`  [[#562](https://github.com/JetBrains/lets-plot/issues/562)]:
  - `size, family` 
    ([example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/font_size_and_family.ipynb));
  - `hjust, vjust` for plot title, subtitle, caption, legend and axis titles
    ([example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/hjust_vjust.ipynb));
  - `margin` for plot title, subtitle, caption, axis titles and tick labels
    ([example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/text_margins.ipynb)).

- The 'newline' character (`\n`) now works as `line break` in axis title.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/text_margins.ipynb).

- Parameter `whisker_width` in `geom_boxplot()` [[#549](https://github.com/JetBrains/lets-plot/issues/549)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/boxplot_whisker_width.ipynb).

- Add `geom_label` [[#557](https://github.com/JetBrains/lets-plot/issues/557)].
  
  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/geom_label.ipynb).

### Changed

- New tooltip style after applying `coord_flip()`  [[#580 The tooltip not rotate after we apply coord_flip().](https://github.com/JetBrains/lets-plot/issues/580)].
  
  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/tooltips_after_coord_flip.ipynb).


### Fixed

- Allow to import all 'bistro' functions just by '*' [[#551](https://github.com/JetBrains/lets-plot/issues/551)].
- density and area: preserve the z-order when grouping [[#552](https://github.com/JetBrains/lets-plot/issues/552)].
- boxplot, violin, crossbar: position dodge width=0.95 should be used by default [[#553](https://github.com/JetBrains/lets-plot/issues/553)].
- Tooltip is shown not for the nearest object [[#574](https://github.com/JetBrains/lets-plot/issues/574)].
- Tooltip is not displayed for the object on the border of the plot [[#575](https://github.com/JetBrains/lets-plot/issues/575)].
- The plot caption overlaps with the legend [[#587](https://github.com/JetBrains/lets-plot/issues/587)].
- Unclear size unit of width [[#589](https://github.com/JetBrains/lets-plot/issues/589)].
- Specify size units in docstrings [[#597](https://github.com/JetBrains/lets-plot/issues/597)].
- No tooltips for geom_boxplot with zero height [[#563](https://github.com/JetBrains/lets-plot/issues/563)].
- geom_text: wrong label alignment with `hjust` 0 and 1 [[#592](https://github.com/JetBrains/lets-plot/issues/592)].
- Error using lets-plot in streamlit [[#595](https://github.com/JetBrains/lets-plot/issues/595)].
- Tooltip: Aes.Y, Aes.YMIN, Aes.YMAX have different number format [[#579](https://github.com/JetBrains/lets-plot/issues/579)].