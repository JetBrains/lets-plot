## [1.5.6] - 2021-01-??

### Added

 - In tooltip customization API:
    - option 'color'.

 - Custom formats for labels on axis scales using 'format' parameter.

### Changed

 - Tooltip customization API:
    - The `color` option overrides the default tooltip color:
        ```python
        geom_xxx(tooltips=layer_tooltips().color('red'))
        ```
    - Add a crosshair to an object whose general tooltip has a fixed position specified by the `anchor` option.
    
    See: [Tooltip Customization](https://github.com/JetBrains/lets-plot/blob/master/docs/tooltips.md).

 - Format labels on axis scales:
    - The `format` parameter defines the format for labels on the scale.
      Supported types are number and date/time. The syntax resembles Python's:
      
          '.2f' -> '12.45'
          'Num {}' -> 'Num 12.456789'
          'TTL: {.2f}$' -> 'TTL: 12.45$'
          '%B %Y' -> 'August 2019'
      For example:
        ```python
        ... + scale_x_datetime(format="%b %Y")
        ... + scale_x_continuous(format='is {.2f}')
        ```

### Fixed
 
 - Fix tooltip for overlapping objects [[#230](https://github.com/JetBrains/lets-plot/issues/230)].
 - Option to override the default tooltip color [[#231](https://github.com/JetBrains/lets-plot/issues/231)].
 - Fix duplicate values in tooltip [[#280](https://github.com/JetBrains/lets-plot/issues/280)].
 - Kernel density estimate computation is too slow.
 - Serialize NaN and other special values as None (to fix [#282](https://github.com/JetBrains/lets-plot/issues/282))
 - Align title to the left of the plot geom area [[#289](https://github.com/JetBrains/lets-plot/issues/289)].