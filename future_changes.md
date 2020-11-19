## [1.5.4] - 2020-11-??

### Added
 - new tooltip anchor options - center and middle.
 - min tooltip width

### Changed
 - [**breaking change**] In functions `format(field, format)` and `line(template)` in 
 tooltip builder, the '$' symbol is no longer used in aesthetic reference. It was replaced by the '^' (hat) symbol 
 (see the udated doc: [Tooltip Customization](https://github.com/JetBrains/lets-plot/blob/master/docs/tooltips.md)).
 - The project upgraded to Kotlin v1.4.10 (was v1.3.72).
 
### Fixed
 - No tooltip on v-line [[#229](https://github.com/JetBrains/lets-plot/issues/229)]
 - Crosshair for corner tooltips. 
