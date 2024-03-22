## [4.3.1] - 2024-mm-dd

### Added
- Automatically choose orientation="y" when aes y is discrete [[#558](https://github.com/JetBrains/lets-plot/issues/558)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24b/auto_rotate.ipynb).
  
### Changed
      
### Fixed
- Error when build geom_smooth() with se=False [[#1050](https://github.com/JetBrains/lets-plot/issues/1050)].
- livemap: when release the mouse button from outside the map, it gets stuck in panning mode [[#1044](https://github.com/JetBrains/lets-plot/issues/1044)].
- Incorrect 'plot_background' area (with empty space capture) [[#918](https://github.com/JetBrains/lets-plot/issues/918)].
- Support arrow() in geom_spoke() [[#986](https://github.com/JetBrains/lets-plot/issues/986)].
- Livemap: `vjust` implemented incorrectly [[#1051](https://github.com/JetBrains/lets-plot/issues/1051)].