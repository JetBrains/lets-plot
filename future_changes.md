## [3.1.1] - 2023-03-??

### Added

### Changed

### Fixed
- livemap: memory leak caused by a document event handler.   
- livemap: flickering when zooming with the buttons.   
- livemap: tooltip text doesn't reflect data under the cursor [[#709](https://github.com/JetBrains/lets-plot/issues/709)].
- Quantile should be shown in tooltip if the variable `..quantile..` is mapped to geom aesthetic.
- Bad default formatting for stat variables [[#654](https://github.com/JetBrains/lets-plot/issues/654)].
- The scale name does not apply with `as_discrete()` [[#653](https://github.com/JetBrains/lets-plot/issues/653)]. 
- Batik: geom_imshow() fail with an error: "The attribute "xlink:href" of the element <image> is required"
- Tooltip is not shown when configured for 'const' value [[#610](https://github.com/JetBrains/lets-plot/issues/610)].
- "newline" doesn't work in legend' texts [[#726](https://github.com/JetBrains/lets-plot/issues/726)].