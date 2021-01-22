## [1.5.6] - 2021-01-??

### Added

 - Tooltip customization API: 
    - option `color` overrides the default tooltip color:
        ```python
        geom_xxx(tooltips=layer_tooltips().color('red'))
        ```
 - Add a crosshair to an object whose general tooltip has a fixed position specified by the `anchor` option.
 - Custom formats for labels on axis scales: parameter `format`. Supported types are number and date/time.
    ```python
    ... + scale_x_datetime(format="%b %Y")
    ... + scale_x_continuous(format='is {.2f}')
    ```

### Changed

### Fixed
 
 - Fix tooltip for overlapping objects [[#230](https://github.com/JetBrains/lets-plot/issues/230)].
 - Option to override the default tooltip color [[#231](https://github.com/JetBrains/lets-plot/issues/231)].
 - Fix duplicate values in tooltip [[#280](https://github.com/JetBrains/lets-plot/issues/280)].
 - Kernel density estimate computation is too slow.
 - Serialize NaN and other special values as None (to fix [#282](https://github.com/JetBrains/lets-plot/issues/282))
 - Align title to the left of the plot geom area [[#289](https://github.com/JetBrains/lets-plot/issues/289)].
 - LOESS smoothing fails on small (<3) number points. 
 - Facet grid truncated in jupyter [[#28](https://github.com/JetBrains/lets-plot-kotlin/issues/28)]