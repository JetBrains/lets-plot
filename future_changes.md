## [4.0.1] - 2023-mm-dd

### Added

- Add `stat_sum()` (`geom_count()` in original version) [[#821](https://github.com/JetBrains/lets-plot/issues/821)].  
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-23d/stat_sum.ipynb).


### Changed

- New defaults:
    - `geom_bar()`: `color='paper'`
    - `geom_pie()`:  `color='paper'`, `stroke_side='both'`, no automatic hole creation when `stroke_side = 'both'/'inner'`
    - `geom_tile()`, `geom_bin2d()`: `size=0, color='paper'`; the `alpha` aesthetic parameter is not applied to the stroke.

- The `alpha` aesthetic parameter overrides the alpha channel in the color, even if the original value was set via the RGBA specification.


### Fixed

- `geom_livemap()`: theme/flavor plot background is not shown [[#857](https://github.com/JetBrains/lets-plot/issues/857)].
- `geom_livemap()`: in AWT dragging a map in a facet moves maps in all facets.
- `scale_x_datetime()`: error building plot for early dates [[#346](https://github.com/JetBrains/lets-plot/issues/346)].
- `theme_void()` + `flavor_xxx()`: no expected plot background [[#858](https://github.com/JetBrains/lets-plot/issues/858)].
- Inconsistent color in legend when using `paint_a/paint_b/paint_c` [[#867](https://github.com/JetBrains/lets-plot/issues/867)].