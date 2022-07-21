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

- Parameters `size, family` in `element_text()`

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/font_size_and_family.ipynb).


### Changed

### Fixed

- boxplot, violin, crossbar: position dodge width=0.95 should be used by default [[#553](https://github.com/JetBrains/lets-plot/issues/553)].
- Tooltip is shown not for the nearest object [[#574](https://github.com/JetBrains/lets-plot/issues/574)].
- Tooltip is not displayed for the object on the border of the plot [[#575](https://github.com/JetBrains/lets-plot/issues/575)].
