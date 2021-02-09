## [2.0.0] - 2021-02-??

### Added

- Python 3.9 support on all platforms.
- `facet_wrap()` function [[#238](https://github.com/JetBrains/lets-plot/issues/238)]
- In facets:
  - Ascending/descending ordering of faceting values.
  - Formatting of faceting values. 
               
  See: [Facets demo](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/facets.ipynb)


- In tooltip customization API: 
   - option `color` overrides the default tooltip color:
       ```python
       geom_xxx(tooltips=layer_tooltips().color('red'))
       ```

  See: [Tooltip Customization](https://github.com/JetBrains/lets-plot/blob/master/docs/tooltips.md). 


- Crosshair cursor when tooltip is in fixed position specified by the `anchor` option.
- Scale `format` parameter: formatting tick labels on X/Y axis. Supported types are `number` and `date/time`.
  
  Example:
   ```python
   scale_x_datetime(format="%b %Y")
   scale_x_continuous(format='is {.2f}')
   ```

   Demo: [Formatting demo](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/formatting_axes_etc.ipynb)
       
   See also: [Formatting](https://github.com/JetBrains/lets-plot/blob/master/docs/formats.md)

### Changed
                
- [**breaking change**] New Geocoding API!

  Since Lets-Plot v2.0.0 the peviouse Geocoding API is no longer working (hence we bumped the version to 2.0.0). 
  
  The old version of the Geocoding backend remains on-line for a couple of release cycles
  to continue support of prior versions of Lets-Plot.
  
  To learn more about Geocoding API see: [Geocoding](https://github.com/JetBrains/lets-plot/blob/master/docs/geocoding.md).
                                    

- The project upgraded to Kotlin v1.4.21 (was v1.4.10).

### Fixed

- Kernel density estimate computation is too slow.
- Serialize NaN and other special values as None (CRITICAL for the "Lets-Plot in SciView" PyCharm plugin) [[#282](https://github.com/JetBrains/lets-plot/issues/282)].
- Error in plot building: 'trans' parameter not compatible with None values in data [[#287](https://github.com/JetBrains/lets-plot/issues/287)].
- LOESS smoothing fails on small (<3) number points.
- Facet grid truncated in jupyter [[#28](https://github.com/JetBrains/lets-plot-kotlin/issues/28)].
- The `reverse` parameter on discrete scale with 'limits' [[#303](https://github.com/JetBrains/lets-plot/issues/289)].
- Geocoder's `allow_ambiguous()` doesn't prevent "ValueError: Multiple objects.." [[#174](https://github.com/JetBrains/lets-plot/issues/174)].
- Fix tooltip for overlapping objects [[#230](https://github.com/JetBrains/lets-plot/issues/230)].
- Fix duplicate values in tooltip [[#280](https://github.com/JetBrains/lets-plot/issues/280)].
- geom_histogram should not try to handle geometries in GeoDataFrame [[#281](https://github.com/JetBrains/lets-plot/issues/281)].
- Error building plot: Layer 'MAP' is not supported on Live Map. [[#285](https://github.com/JetBrains/lets-plot/issues/285)].
- Align title to the left of the plot geom area [[#289](https://github.com/JetBrains/lets-plot/issues/289)].
- Tooltip on `geom_ribbon()`.