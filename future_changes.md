## [4.6.1] - 2025-mm-dd

### Added

### Changed

- [**BREAKING**] the `height` and `height_unit` parameters have been deprecated for the `geom_errorbar`.

- the minimum distance between axis labels was reduced to avoid unsuitable layouts.

### Fixed

- `geom_errorbar()`: the plot domain is always stretched to zero, regardless of the data.

- overlapped axis labels when using `hjust/vjust` or multiline text.
