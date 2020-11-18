## [1.5.4] - 2020-11-??

### Changed
 - [<span style="color:red">**breaking change**</span>] In functions `format(field, format)` and `line(template)` in 
 tooltip builder, the '$' symbol is no longer used in aesthetic reference. It was replaced by the '^' (hat) symbol 
 (see the udated doc: [Tooltip Customization](https://github.com/JetBrains/lets-plot/blob/master/docs/tooltips.md)).
 - The project upgraded to Kotlin v1.4.10 (was v1.3.72).
 
### Fixed
 - Object selection for the tooltip.
 - Crosshair for corner tooltips. 
