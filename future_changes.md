## [4.8.3] - 2026-mm-dd

### Added

- Python 3.14 support

- Plot Theme:
  - Support of axis minor ticks via `axis_minor_ticks` and `axis_minor_ticks_length` parameters in `theme()` [[#1379](https://github.com/JetBrains/lets-plot/issues/1379)].

    See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/axis_minor_ticks.html).

- Color Scales:
  - New `palette()` method for color scales: generates a list of hex color codes that can be used with `scale_color_manual()` to maintain consistent colors across multiple plots [[#1444](https://github.com/JetBrains/lets-plot-kotlin/issues/1444)].

    See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/scale_color_palette.html).

- `geom_imshow()`: 
  - Support for custom colormaps [[#780](https://github.com/JetBrains/lets-plot-kotlin/issues/780)].
  - New `cguide` parameter: use to customize the colorbar for greyscale images.

    See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/image_custom_cmap.html).

### Changed

- [**BREAKING**]: Removed JavaFX artifacts.
- [**BREAKING**]: Removed `plot-image-export` module. Use `PlotImageExport` from `platf-awt` module instead.
- Missing values in `geom_area_ridges()` create gaps in geometries instead of being interpolated over.
- [wip] Added warning messages about the removal of invalid points during geometry construction for geom_point(), geom_path(), and geom_line() layers. [[#81](https://github.com/JetBrains/lets-plot-kotlin/issues/81)].
- [**BREAKING**]: ColorBrewer palettes: when the requested number of colors exceeds the palette's maximum size, colors are now interpolated to generate unique colors. \
  Previously, depending on the palette type, this either resulted in duplicate colors or random additional colors.
- Discrete color scales (Brewer, Manual) now produce a 'colorbar' guide when used with continuous data. \
  Previously they produced a 'legend' guide regardless of the data type.

### Fixed

- Drop commons-io dependency [[#1421](https://github.com/JetBrains/lets-plot/issues/1421)].
- Unexpected replacement of double curly brackets with a single curly bracket [[#1433](https://github.com/JetBrains/lets-plot/issues/1433)].
- Upgrade to a newer version of ws [[#1150](https://github.com/JetBrains/lets-plot/issues/1150)].
- geom_imshow: unclear error message when mixing transparencies [[#1088](https://github.com/JetBrains/lets-plot/issues/1088)].
- geom_imshow and scale_y_reverse [[#1210](https://github.com/JetBrains/lets-plot/issues/1210)].
- Nice to be able to get a list of colors from a color scale object [[#1444](https://github.com/JetBrains/lets-plot/issues/1444)].