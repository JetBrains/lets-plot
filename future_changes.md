## [4.7.2] - 2025-mm-dd

### Added
- Plot Layout:

    - New `strip_spacing`, `strip_spacing_x`, and `strip_spacing_y` parameters in `theme()` to control spacing between the facet strip (title bar) and the plot panel.
    - New `panel_spacing`, `panel_spacing_x`, and `panel_spacing_y` parameters in `theme()` to control spacing between plot panels in faceted plots, [[#1380](https://github.com/JetBrains/lets-plot/issues/1380)].
    
    See: [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-25d/facet_spacings.ipynb).

- Image resolution is saved in the metadata of PNG files created with `ggsave()`.

### Changed

### Fixed

- ggsave(): memory leak when using `geom_raster()`.
- Incorrect physical image size when exporting PDF with `ggsave()` without specifying `dpi`.
