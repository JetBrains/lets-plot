## [4.8.3] - 2026-mm-dd

### Added

- Python 3.14 support

- Python 3.14 free-threading support [[#1454](https://github.com/JetBrains/lets-plot/issues/1454)]

- Plot tags. A tag can be specified via `labs(tag=...)` and styled using theme parameters [[#1407](https://github.com/JetBrains/lets-plot/issues/1407)]
  
    See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/plot_tags.html).

- Plot Theme:
  - Tag customization parameters:
    - `plot_tag` - sets the tag style via `element_text()`
    - `plot_tag_location` - specifies the area used for positioning the tag  
    - `plot_tag_position` - specifies the position of the tag within the selected area
    - `plot_tag_prefix` - text added before the tag value
    - `plot_tag_suffix` - text added after the tag value
   
    See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/plot_tags.html).
 
  - Support of axis minor ticks via `axis_minor_ticks` and `axis_minor_ticks_length` parameters in `theme()` [[#1379](https://github.com/JetBrains/lets-plot/issues/1379)].

    See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/axis_minor_ticks.html).

- Color Scales:
  - New `palette()` method for color scales: generates a list of hex color codes that can be used with `scale_color_manual()` to maintain consistent colors across multiple plots [[#1444](https://github.com/JetBrains/lets-plot/issues/1444)].

    See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/scale_color_palette.html).

  - New `overflow` parameter in `scale_color_brewer()` / `scale_fill_brewer()`: controls how colors are generated when more colors are needed than the palette provides. \
    Options: `'interpolate'` (`'i'`), `'cycle'` (`'c'`), `'generate'` (`'g'`).

    See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/scale_brewer_overflow.html).

- Positional Scales:
  - New `break_width` parameter that specifies a fixed distance between axis breaks.

    See examples:
    - [datetime scale](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/scale_break_width_datetime.html)
    - [time (duration) scale](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/scale_break_width_duration.html)
    - [log10 scale](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/scale_break_width_log10.html)

- `geom_bracket()`, `geom_bracket_dodge()` [[#1114](https://github.com/JetBrains/lets-plot/issues/1114)].

    See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/geom_bracket.html).

- `gggrid()`: interactive pan/zoom now propagates across subplots with shared axes (`sharex`/`sharey`) [[#1413](https://github.com/JetBrains/lets-plot/issues/1413)].

    See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/gggrid_scale_share_zoom.html).

- `geom_imshow()`:
  - Support for custom colormaps [[#780](https://github.com/JetBrains/lets-plot/issues/780)].
  - New `cguide` parameter: use to customize the colorbar for grayscale images.

    See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/image_custom_cmap.html).

- `geom_smooth()`: introduced `smooth_labels()` to provide automated labeling of goodness-of-fit metrics and the regression equation.

    See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26a/smooth_labels.html).


### Changed

- Upgraded Kotlin version to 2.2.20 (was 1.9.25).
- [**BREAKING**]: Removed JavaFX artifacts.
- [**BREAKING**]: Removed `plot-image-export` module. Use `PlotImageExport` from `platf-awt` module instead.
- Missing values in `geom_area_ridges()` create gaps in geometries instead of being interpolated over.
- [**BREAKING**]: ColorBrewer palettes: changed default behavior when the requested number of colors exceeds the palette's maximum size. \
  Now defaults to `'interpolate'` for sequential/diverging palettes and `'generate'` for qualitative palettes. \
  Previously, depending on the palette type, this either resulted in duplicate colors or random additional colors. \
  Use the new `overflow` parameter to explicitly control this behavior.
- Discrete color scales (Brewer, Manual) now produce a `colorbar` guide when used with continuous data. \
  Previously they produced a `legend` guide regardless of the data type.

### Fixed

- Drop commons-io dependency [[#1421](https://github.com/JetBrains/lets-plot/issues/1421)].
- Unexpected replacement of double curly brackets with a single curly bracket [[#1433](https://github.com/JetBrains/lets-plot/issues/1433)].
- Upgrade to a newer version of ws [[#1150](https://github.com/JetBrains/lets-plot/issues/1150)].
- geom_imshow: unclear error message when mixing transparencies [[#1088](https://github.com/JetBrains/lets-plot/issues/1088)].
- geom_imshow and scale_y_reverse [[#1210](https://github.com/JetBrains/lets-plot/issues/1210)].
- Nice to be able to get a list of colors from a color scale object [[#1444](https://github.com/JetBrains/lets-plot/issues/1444)].
- Allow tooltips param to accept list [[#1455](https://github.com/JetBrains/lets-plot/issues/1455)].
- Allow grouped tooltips for plots with multiple univariate geoms [[#1460](https://github.com/JetBrains/lets-plot/issues/1460)].