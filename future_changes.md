## [1.5.5] - 2020-12-??

### Added
 - Correlation plot.
    
    See: [The 'bistro' Package](https://github.com/JetBrains/lets-plot/blob/master/README_PYTHON.md#the-bistro-package).

 - The 'no javascript' mode.
   Support for notebook renderers that don't execute Javascript.
   
   See: ['No Javascript' mode](https://github.com/JetBrains/lets-plot/blob/master/README_PYTHON.md#no-javascript-mode)
 
 - In tooltip customization API:
    - options: `center` and `middle` (anchor).
    - option 'minWidth'.
    
    See: [Tooltip Customization](https://github.com/JetBrains/lets-plot/blob/master/docs/tooltips.md).
    
 - 'na_text' parameter in 'geom_text'

### Changed
 - Tooltip customization API:
    - The `anchor` option moved from `theme` to `layer`:
      ```python                                                     
      geom_xxx(tooltips=layer_tooltips().anchor(anchor_value))
      ```
         where `anchor_value`: 
         `['top_right'|'top_center'|'top_left'|'bottom_right'|'bottom_center'|'bottom_left'|'middle_right'|'middle_center'|'middle_left']`.

    See: [Tooltip Customization](https://github.com/JetBrains/lets-plot/blob/master/docs/tooltips.md).
 
### Fixed
 - Tooltip should appear when the mapped data is continuous [[#241](https://github.com/JetBrains/lets-plot/issues/241)]
 - Tooltip 'null' displayed for undefined vals [[#243](https://github.com/JetBrains/lets-plot/issues/243)]
 - Y-tooltip should be aligned with a tile center [[#246](https://github.com/JetBrains/lets-plot/issues/246)]
 - With `facet_grid` tooltip shows data from last plot on all plots [[#247](https://github.com/JetBrains/lets-plot/issues/247)]