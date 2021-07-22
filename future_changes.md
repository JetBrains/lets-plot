## [2.0.5] - 2021-??-??

### Added


### Changed

- Upgraded Apach Batik version to 1.14 (was 1.12) [[#398](https://github.com/JetBrains/lets-plot/issues/398)]. 

### Fixed

- Strange looking legend for tiles [[#245](https://github.com/JetBrains/lets-plot/issues/245)].
- Need to skip "bad" values during scale transformation [[#301](https://github.com/JetBrains/lets-plot/issues/301)].
- NPE on negative value in data and scale_xxx(trans='log10') [[#292](https://github.com/JetBrains/lets-plot/issues/292)].
- Legend is broken when using scale_fill_brewer with 'trans' parameter [[#284](https://github.com/JetBrains/lets-plot/issues/284)].
- scale_y_log10: hidden segments [[#372](https://github.com/JetBrains/lets-plot/issues/372)].
- Scale breaks should be distributed evenly on 'sqrt' scale. [[#407](https://github.com/JetBrains/lets-plot/issues/407)].
- Wrong tooltip formatting when used with log10 scales [[#406](https://github.com/JetBrains/lets-plot/issues/406)].