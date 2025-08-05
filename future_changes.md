## [4.7.1] - 2025-mm-dd

### Added

- ggsave(): support font synthesis for *italic* and **bold** styles.

### Changed


### Fixed
- geom_livemap: can't draw a path crossing the antimeridian [[#649](https://github.com/JetBrains/lets-plot/issues/649)].
- geom_ribbon(): tooltip appears in the wrong place on flipped ribbon [[#1334](https://github.com/JetBrains/lets-plot/issues/1334)].
- Arrow crossing -180 longitude is split into two arrows [[#1364](https://github.com/JetBrains/lets-plot/issues/1364)].
- Coordinate limits do not work on reversed scales [[#1365](https://github.com/JetBrains/lets-plot/issues/1365)]
- Display order of fill categories not being set correctly in stacked plots? [[#1367](https://github.com/JetBrains/lets-plot/issues/1367)]
- Polars: add handling for `Enum` values [[#1373](https://github.com/JetBrains/lets-plot/issues/1373)]
- Unclear error when using geom_rect with discrete scales [[#1287](https://github.com/JetBrains/lets-plot/issues/1287)]
- xlim() breaks default scale_x_datetime() [[#1348](https://github.com/JetBrains/lets-plot/issues/1348)]
- scale_x_reverse breaks datetime formatting [[#1257](https://github.com/JetBrains/lets-plot/issues/1257)]
- theme(plot_title="blank") doesn't work with gggrid [[#1349](https://github.com/JetBrains/lets-plot/issues/1349)]
