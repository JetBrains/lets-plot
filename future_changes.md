## [4.7.3] - 2025-mm-dd

### Added

### Changed

- `ggsave()`: Large plot dimensions without units now require explicit unit specification. <br>
  When plot size exceeds 20 without specifying units (e.g., `ggsave(p, 300, 400)`), <br>
  we ask to specify units explicitly: <br>
  `ggsave(p, 300, 400, unit='px')` or `ggsave(p, 3, 4, unit='in')`.

### Fixed

- `ggtb()` support in Swing/Batik frontend.
- Multiline support for axis labels in polar coordinates.
- When the plot size in `ggsave()` is specified in pixels, `dpi` now affects <br> 
  only the physical size, not the pixel dimensions as before.

