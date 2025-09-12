## [4.7.3] - 2025-mm-dd

### Added

- `ggtb()` support in Swing/Batik frontend.                

- Multiline support for axis labels in polar coordinates.

### Changed

- Appearance of the raster size limit message in `geom_raster()`.

- When the plot size in `ggsave()` is given without units and exceeds 20 inches, <br>
  an error is now issued. To avoid this, explicitly specify the desired units.


### Fixed

- When the plot size in `ggsave()` is specified in pixels, `dpi` now affects <br> 
  only the physical size, not the pixel dimensions as before.

