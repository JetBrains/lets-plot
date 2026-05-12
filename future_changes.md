## [4.10.0] - 2026-05-dd

### Added

- `ggdeck()` — a new function for overlaying multiple independent plots in a single unified view.

  In a typical scenario, one axis is shared by all plots in the deck, enabling dual-axis effects and multivariate comparisons.

  See examples:
  - [dual-axis effect](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26b/ggdeck_dual_axis.html)
  - [multivariate comparison](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26b/ggdeck_plot_overlay.html)


- Datetime: `%f` formatting pattern — support for milliseconds when formatting datetime values, zero-padded to 3 digits [[#1482](https://github.com/JetBrains/lets-plot/issues/1482)].

  See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26b/milliseconds_format.html).


- In legends:
  - `override_aes` in `guide_legend()` can now customize filled 2D legend keys:
    - `size` controls the key border width;
    - `width` and `height` control the relative key size.

    See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26b/legend_key_size.html).


- Indication of removed records [[LPK-81](https://github.com/JetBrains/lets-plot-kotlin/issues/81)], [[#686](https://github.com/JetBrains/lets-plot/issues/686)].

  When records in data are dropped by active sampling or because they contain missing or out-of-bounds values, the user is now informed of the number of dropped records and the reason they were dropped.

  The new `na_rm` parameter in `geom_xxx()` and `stat_xxx()` functions controls the display of such messages:
  - `na_rm=false` (default) — records are removed and messages are shown;
  - `na_rm=true` — records are removed silently.

  See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26b/removed_records_indication.html).


- Alpha (transparency) component:
  - Hex colors accept `#RRGGBBAA` or `#RGBA` notation.
  - Colors accept an opacity suffix in the form `"named color / opacity"`, for example `"steelblue / 0.35"`.

  See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26b/color_alpha.html).

            
- In `geom_imshow()`, new parameters for controlling the colorbar breaks and labels [[#1486](https://github.com/JetBrains/lets-plot/issues/1486)]:
  - `breaks`, `labels`, `lablim`, `format`


- Memo for Kotlin API users:
  - Added WasmJS support [[LPK-296](https://github.com/JetBrains/lets-plot-kotlin/issues/296)], [[LPC-52](https://github.com/JetBrains/lets-plot-compose/issues/52)].

### Changed


### Fixed

- Add 'synchronized tooltips' feature [[#1415](https://github.com/JetBrains/lets-plot/issues/1415)].
- Alpha is not supported in element_text() [[#1462](https://github.com/JetBrains/lets-plot/issues/1462)].
- geom_imshow(): should render transparency for NaNs when all other pixel values are identical [[#1485](https://github.com/JetBrains/lets-plot/issues/1485)].
- scale_alpha: conflict of constant and mapped values of alpha aesthetic [[#706](https://github.com/JetBrains/lets-plot/issues/706)].
