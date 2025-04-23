## [4.6.3] - 2025-mm-dd

### Added
- More named colors, including all HTML/CSS colors.
- Support different color naming styles like `dark-gray`, `darkgrey`, `dark_grey`, `DARKGRAY`, etc.
- Grayscale colors from `gray0` (black) to `gray100` (white).

### Changed
- Updated RGB values for `lightgray` and `green`. To restore the previous colors, use `gray75` and `lime`, respectively. 

### Fixed
- Can't add layer which uses continuous data to a plot where other layers use discrete input [[#1323](https://github.com/JetBrains/lets-plot/issues/1323)].
