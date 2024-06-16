## [4.3.4] - 2024-mm-dd

### Added
- Legend title in `guide_legend()` and `guide_colorbar()`.
  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24e/legend_title.ipynb).

- Parameter `override_aes` in `guide_legend()`.
  See [example notebook](https://nbviewer.org/github/JetBrains/lets-plot/blob/master/docs/f-24e/legend_override_aes.ipynb).
  
### Changed
- [**breaking change**] guide_legend()/guide_colorbar() require keyword arguments for 'nrow'/'barwidth' other parameters except 'title'.
 
### Fixed
