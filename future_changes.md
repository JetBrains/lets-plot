## [2.0.1] - 2021-??-??

### Added

- The `alpha` parameter for lines [[#139](https://github.com/JetBrains/lets-plot/issues/139)].
- Tooltips for `geom_segment()` [[#296](https://github.com/JetBrains/lets-plot/issues/296)].
- The `guides()` function [[#52](https://github.com/JetBrains/lets-plot/issues/52)].
- New Java **Swing plot components** to enable embedding Lets-Plot charts into JVM applications.
  - See: `vis-swing-common, vis-swing-batik, vis-swing-jfx` modules. 

### Changed
                
### Fixed
                         
- Fix auto-detection of PyCharm env to enable plotting in SciView while using remote interpreter [[348](https://github.com/JetBrains/lets-plot/issues/348)]
- Fix tooltips appearing outside the specified x/y limits.
- Clippath in accordance to the given limits [[#189](https://github.com/JetBrains/lets-plot/issues/189)].
- Treat a data as DataFrame if both data and map are GeoDataFrames [[#343](https://github.com/JetBrains/lets-plot/issues/343)].
- Removed the restriction on tooltips for small polygons [[#298](https://github.com/JetBrains/lets-plot/issues/298)].
- The x/y axis labels are derived from x/y aesthetics only [[#333](https://github.com/JetBrains/lets-plot/issues/333)].
- Merge 'theme' settings [[#147](https://github.com/JetBrains/lets-plot/issues/147)].
- Add axis tooltips for `geom_bin2d`.
- Outlier tooltips for `geom_ribbon()`.
- Fix tooltip crosshair [[#309](https://github.com/JetBrains/lets-plot/issues/309)].