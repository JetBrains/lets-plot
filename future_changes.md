## [4.10.4] - 2026-mm-dd

### Added

- Merged tooltips — new `theme()` parameters controlling how multiple simultaneous general tooltips are displayed:
  - `tooltip_merge` (default `False`) — when `True`, general tooltips from multiple targets are combined into a single merged tooltip.
  - `tooltip_max_count` (default `10`) — maximum number of tooltip targets shown at once; if exceeded, only the closest target is shown. Set to `0` to disable the limit. Ignored when `tooltip_merge=True` — merging takes priority and all targets are combined.

  See: [example notebook](https://raw.githack.com/JetBrains/lets-plot/master/docs/f-26c/tooltip_merge.html).

### Changed

- Upgrade Kotlin to 2.3.20
- `macosX64` target is no longer available (in Kotlin 2.3.20). See: https://kotl.in/native-targets-tiers.

### Fixed
