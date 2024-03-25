## [4.3.1] - 2024-mm-dd

### Added
- Parameter `min_tail_length` for `arrow()` function [[#1040](https://github.com/JetBrains/lets-plot/issues/1040)].

### Changed
      
### Fixed
- Error when build geom_smooth() with se=False [[#1050](https://github.com/JetBrains/lets-plot/issues/1050)].
- livemap: when release the mouse button from outside the map, it gets stuck in panning mode [[#1044](https://github.com/JetBrains/lets-plot/issues/1044)].
- Incorrect 'plot_background' area (with empty space capture) [[#918](https://github.com/JetBrains/lets-plot/issues/918)].
- Support arrow() in geom_spoke() [[#986](https://github.com/JetBrains/lets-plot/issues/986)].
- Support geom_spoke() in geom_livemap() [[#988](https://github.com/JetBrains/lets-plot/issues/988)].
- arrow on curve sometimes looks weird [[#1041](https://github.com/JetBrains/lets-plot/issues/1041)].
- Livemap: `vjust` implemented incorrectly [[#1051](https://github.com/JetBrains/lets-plot/issues/1051)].
- Missing marginal gridlines.