## [4.5.2] - 2024-12-dd

### Added

### Changed

- Number formatting:
  - We have aligned our specifications with D3.js (rather than Python): `g` format type with `~` (e.g. `~g`) now truncates trailing zeros, and without `~` it doesn't.
  - The default number formatter now respects `theme(exponent_format=...)` settings and does not use 's' format type for large numbers.

### Fixed
- Bad precision in the default tooltip format when using coord limits [[#1134](https://github.com/JetBrains/lets-plot/issues/1134)]. 
- Display integer values without fractional part in tooltips [[#1186](https://github.com/JetBrains/lets-plot/issues/1186)].
- Suboptimal tooltip positioning in facets [[#1187](https://github.com/JetBrains/lets-plot/issues/1187)].
- Incorrect Y-axis layout with facets and panel_inset [[#1194](https://github.com/JetBrains/lets-plot/issues/1194)].
- Kandy toPNG reports NullPointerException [[#1228](https://github.com/JetBrains/lets-plot/issues/1228)]
- lets_plot_kotlin_bridge is unable to locate libc++.1.dylib, I am using Mac m2 [[#1234](https://github.com/JetBrains/lets-plot/issues/1234)].
- Wrong formatting when type='g' for small values [[#1238](https://github.com/JetBrains/lets-plot/issues/1238)].
- Formatting when type='g' for large values throws exception [[#1239](https://github.com/JetBrains/lets-plot/issues/1239)].
- Wrong formatting when type='s' with explicit precision [[#1240](https://github.com/JetBrains/lets-plot/issues/1240)].
- Extra trim in formatted number when type='g' [[#1241](https://github.com/JetBrains/lets-plot/issues/1241)].
- Axis breaks are badly formatted if explicitly set [[#1245](https://github.com/JetBrains/lets-plot/issues/1245)].
- Badly formatted zero break for the "~g" format [[#1246](https://github.com/JetBrains/lets-plot/issues/1246)].
- How to adjust the vertical position of geom_text when using position_dodge [[#1248](https://github.com/JetBrains/lets-plot/issues/1248)].
- Incorrect result for format(9.999, ".2f") [[#1251](https://github.com/JetBrains/lets-plot/issues/1251)].
- Tooltips overlapping when not enough vertical space for them [[#1254](https://github.com/JetBrains/lets-plot/issues/1254)].
