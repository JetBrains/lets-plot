## [4.9.1] - 2026-mm-dd

### Added
- WasmJS support [[LPK-296](https://github.com/JetBrains/lets-plot-kotlin/issues/296)], [[LPC-52](https://github.com/JetBrains/lets-plot-compose/issues/52)].

- Datetime formatting: `%f` support in `DateTimeFormat` (milliseconds, zero-padded to 3 digits) [[#1482](https://github.com/JetBrains/lets-plot/issues/1482)].

  See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26b/milliseconds_format.html).

- In legends:
  - `override_aes` in `guide_legend()` can now customize filled 2D legend keys:
    - `size` controls the key border width;
    - `width` and `height` control the relative key size.

    See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26b/legend_key_size.html).

- `na_rm` parameter for geoms and statistical functions. When `na_rm=false` (default), corresponding computation messages are shown; when `na_rm=true`, non-finite / dropped rows are removed silently [[LPK-81](https://github.com/JetBrains/lets-plot-kotlin/issues/81)].

  See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26b/removed_points_messages.html).

### Changed

### Fixed
