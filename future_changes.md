## [4.11.0] - 2026-06-dd

### Added

- `theme()`: new `tooltip_merge` and `tooltip_max_count` parameters to combine the general tooltips of multiple targets into a single tooltip.

  See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26c/tooltip_merge.html).

- Text halos improve readability on varied backgrounds. New `halo_width` and `halo_color` parameters are supported in `geom_text()`/`geom_text_repel()` and by labels in `corr_plot()`.

  See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26c/text_halo.html).

### Changed


### Fixed

- Incorrect `hjust`/`vjust`/`angle` justification of vertical-axis tick labels (`theme(axis_text_y=element_text(...))`)
- Rotated vertical-axis tick labels could be wrongly dropped or kept by overlap-based break filtering
- Misaligned or mismatched axis tick labels when some breaks are dropped after layout


