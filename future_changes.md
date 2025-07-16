## [4.7.0] - 2025-07-dd

### Added

- Time Series Plotting [[#278](https://github.com/JetBrains/lets-plot-kotlin/issues/278)],
  [[discussion](https://github.com/JetBrains/lets-plot-kotlin/discussions/92#discussioncomment-12976040)],
  [[#678](https://github.com/JetBrains/lets-plot/issues/678)],
  [[LPK-129](https://github.com/JetBrains/lets-plot-kotlin/issues/129)]:
  - Support for Python `time` and `date` objects.
  - Support for timezone-aware `datetime` objects and Pandas/Polars `Series`.
  
  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25b/time_date_datetime.ipynb).

- Geometries:

    - `geom_sina()` [[#1298](https://github.com/JetBrains/lets-plot/issues/1298)].

      See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25b/geom_sina.ipynb).
  
    - `geom_text_repel()` and `geom_label_repel()` [[#1092](https://github.com/JetBrains/lets-plot/issues/1092)].

      See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25b/ggrepel.ipynb).

- Layer Labels (Annotations):
  - Support in `geom_crossbar()`

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25b/geom_crossbar_annotation.ipynb).

  - Support in `waterfall_plot()` via `relative_labels` and `absolute_labels` parameters.

    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25b/waterfall_plot_annotations.ipynb).

  - New `inherit_layer()` option in annotations configuration (see example notebooks above)

- `waterfall_plot()` - support for combining waterfall bars with other geometry layers [[#1344](https://github.com/JetBrains/lets-plot/issues/1344)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25b/waterfall_plot_layers.ipynb).
           
- Plot Layout:

  - New `axis_text_spacing`, `axis_text_spacing_x`, and `axis_text_spacing_y` parameters in `theme()` to control spacing between axis ticks and labels.
  - See new [plot layout diagram](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25b/plot_layout_scheme.ipynb) notebook showing various layout options and their effects on plot appearance. 

- More variants to specify a color by name:

  - all HTML/CSS colors;
  - various naming styles, e.g., `dark-gray`, `darkgrey`, `dark_grey`, `DARKGRAY`, etc.;
  - grayscale colors from `gray0` (black) to `gray100` (white);
  
  See [the complete list of named colors](https://lets-plot.org/python/pages/named_colors.html).


### Changed

- Native support for PNG and PDF exports [[#1268](https://github.com/JetBrains/lets-plot/issues/1268)]:

  Exporting to PNG and PDF formats now uses the `ImageMagick` library bundled with Lets-Plot Python wheels and available out-of-the-box. <br>
  This replaces the previous dependency on the `CairoSVG` library and comes with improved support for LaTeX labels rasterization. <br>
  Related changes:
  - `ggsave`: the `w` and `h` parameters override plot size, allowing to specify the output image size independently of the plot size.
  - `ggsave`: the `dpi` default value changed to 300.
  - `ggsave`: the `unit` default value changed to `in` (inches).


- Continuous data on discrete scales:

  Continuous data when used with discrete positional scales is no longer transformed to discrete data. <br>
  Instead, it remains continuous, allowing for precise positioning of continuous elements relative to discrete ones. <br>
  This resolves issues where combining discrete and continuous data in the same plot was difficult or impossible: [[#1279](https://github.com/JetBrains/lets-plot/issues/1279)].

  See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25b/numeric_data_on_discrete_scale.ipynb).

> [!TIP]
> New way of handling continuous data on discrete scales could potentially break existing plots.
> If you want to restore a broken plot to its original form, you can use the [`as_discrete()`](https://lets-plot.org/python/pages/api/lets_plot.mapping.as_discrete.html) function to annotate continuous data as discrete.

- [**BREAKING**] Dropped support for Python 3.8 as it is in the ["end-of-life"](https://devguide.python.org/versions/#unsupported-versions) of its release cycle.
- [**BREAKING**] `geom_boxplot()`: when y-oriented, it now uses aesthetics `xlower`/`xmiddle`/`xupper` instead of  `lower`/`middle`/`upper` [[#1319](https://github.com/JetBrains/lets-plot/issues/1319)].
- [**BREAKING**] `waterfall_plot()`: special `flow_type` value for `label=element_text(color=...)` replaced with `inherit`. See `label` in the [documentation](https://lets-plot.org/python/pages/api/lets_plot.bistro.waterfall.waterfall_plot.html).
- [**DEPRECATED**] The `position_dodgev()` function and the `'dodgev'` value for the `position` parameter are deprecated and will be removed in future releases.
- Plot layout: reduced margins and spacing for title, caption, axes, and legend.
- Updated RGB values for `lightgray` and `green`. To restore the previous colors, use `gray75` and `lime`, respectively. 
- `waterfall_plot()`: the appearance of the legend has been improved.
- `geom_violin()`: tooltips are not shown in the centerline of the violin if `show_half != 0`.
- `geom_crossbar()`: the midline is not shown in the legend when `fatten` is set to 0, or when there is no mapping for it.
- `geom_pointrange()`: the midpoint will not be drawn if the y aesthetic is set to `None`.
- `geom_band()`: the `alpha` aesthetic only affects the inner part of the geometry, as in `geom_rect()`.
- `geom_band()`: show tooltip over the whole band, not just at the edges.

### Fixed

- AWT: plot prevents wheel events from bubbling up to the parent component.
- Added tooltip for `geom_hline` and `geom_vline` on `geom_livemap` [[#1056](https://github.com/JetBrains/lets-plot/issues/1056)].
- `geom_boxplot`: unable to draw a y-oriented plot with `stat='identity'` [[#1319](https://github.com/JetBrains/lets-plot/issues/1319)].
- Can't add layer which uses continuous data to a plot where other layers use discrete input [[#1323](https://github.com/JetBrains/lets-plot/issues/1323)].
- Multiline legend labels were not vertically centered with their keys [[#1331](https://github.com/JetBrains/lets-plot/issues/1331)].   
- Poor alignment in legend between columns [[#1332](https://github.com/JetBrains/lets-plot/issues/1332)].
- Ordered data was re-ordered by `geom_boxplot` [[#1342](https://github.com/JetBrains/lets-plot/issues/1342)].
- `geom_rect`: fixed data conversion for `geom_livemap` [[#1347](https://github.com/JetBrains/lets-plot/issues/1347)].
- `ggsave`: incorrect output when exporting markdown demo to PNG [[#1362](https://github.com/JetBrains/lets-plot/issues/1362)].
- `as_discrete()` does not work with aes addition [[#1363](https://github.com/JetBrains/lets-plot/issues/1363)].
- Sec: CVE-2024-47554 (commons-io) [[#1231](https://github.com/JetBrains/lets-plot/issues/1231)]
