## [4.0.2] - 2023-mm-dd

### Added

- New scale transformations: `'log2'` and `'symlog'`.

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-23e/scale_transformations.ipynb).

### Changed

- [BREAKING] `stat_summary()` and `stat_summary_bin` no longer supports computing of additional variables through the specifying of mappings.

### Fixed

- Tooltips are trimmed and not visible on a very narrow chart [[#837](https://github.com/JetBrains/lets-plot/issues/837)].
- Inability to use the spelling "grey" for the color grey (via "gray" only).