## [2.4.1] - 2022-??-??

### Added

- New pre-configured theme: `theme_bw()`. 
    
  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22c/theme_bw.ipynb).
  
* Color schemes (flavors) applicable to existing themes:
  - `flavor_darcula()`;
  - `flavor_solarized_light()`;
  - `flavor_solarized_dark()`;
  - `flavor_high_contrast_light()`;
  - `flavor_high_contrast_dark()`.
  
  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22с/theme_flavors.ipynb).

### Changed

### Fixed

- boxplot, violin, crossbar: position dodge width=0.95 should be used by default [[#553](https://github.com/JetBrains/lets-plot/issues/553)].
