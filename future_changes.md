## [4.0.1] - 2023-mm-dd

### Added

### Changed

- New defaults:
  - `geom_bar()`: `color='paper'`
  - `geom_pie()`:  `color='paper'`, `stroke_side = 'both'`, no automatic hole creation when `stroke_side = 'both'/'inner'`

- The `alpha` aesthetic parameter changes the alpha channel in the color, even if the original value was set via the RGBA specification.


### Fixed

- `geom_livemap()`: theme/flavor plot background is not shown [[#857](https://github.com/JetBrains/lets-plot/issues/857)].
- `geom_livemap()`: in AWT dragging a map in a facet moves maps in all facets.
- `scale_x_datetime()`: error building plot for early dates [[#346](https://github.com/JetBrains/lets-plot/issues/346)].
- `theme_void()` + `flavor_xxx()`: no expected plot background [[#858](https://github.com/JetBrains/lets-plot/issues/858)].
- Inconsistent color in legend when using `paint_a/paint_b/paint_c` [[#867](https://github.com/JetBrains/lets-plot/issues/867)].