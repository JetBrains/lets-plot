## [2.3.0] - 2022-03-??

### Added

- Plot subtitle and caption   [[#417](https://github.com/JetBrains/lets-plot/issues/417)]: 
  `subtitle` parameter in `ggtitle()` and `labs()`, 
  `caption` parameter in `labs()`, 
  `plot_subtitle` and `plot_caption` parameters in `theme()`. 

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/title_subtitle_caption.ipynb).
     
- The 'newline' character (`\n`) now works as `line break` in plot title, subtitle, caption and in legend title.
    
  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/title_subtitle_caption.ipynb).

- New in tooltip customization API:
  - The `title()` option defines a tooltip "title" text which will always appear above the rest of the tooltip content.
  - The 'newline' character (`\n`) now works as `line break` in tooltips.
  - Automatic word wrap: occurs when the length of a text value in tooltip exceeds the 30 characters limit.  
  
  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/tooltip_title.ipynb).

- Parameter `scales` in `facet_grid()/facet_wrap()` [[#451](https://github.com/JetBrains/lets-plot/issues/451), 
[#479](https://github.com/JetBrains/lets-plot/issues/479)].

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/facets_free_scales.ipynb).
             
- New in `geom_livemap()`:
  - The `Reset` button: returns the map widget to its initial zoom/location state.
  - Parameters `data_size_zoomin, const_size_zoomin`: allow to configure how zooming-in of the map widget increases size of geometry objects (circles, lines etc.) on map.
  - Parameter `ontop` that controls z-index of the `geom_livemap` layer.
  - Parameter `show_coord_pick_tools` to show "copy location" and "draw geometry" buttons.

- New geometries:
  - `geom_violin()`

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/geom_violin.ipynb).

  - `geom_dotplot()`

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/geom_dotplot.ipynb).

  - `geom_ydotplot()`

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22a/notebooks/geom_ydotplot.ipynb).
                                              

### Changed

- New tooltip style: rounded corners, bold label, colored marker inside the tooltip.
- Deprecated tooltip customization API:
  function `color()` will be removed in one of the future releases.
- 'Auto shrink': plots automatically shrink when necessary to fit width of the output (notebook) cell [[#488](https://github.com/JetBrains/lets-plot/issues/488)].

### Fixed

- LiveMap, Swing-batik: legend is not visible when overlapping map [[#496](https://github.com/JetBrains/lets-plot/issues/496)].
- CVE-2021-23792 in org.jetbrains.lets-plot:lets-plot-image-export@2.2.1 [[#497](https://github.com/JetBrains/lets-plot/issues/497)].
- Color in tooltip does not correspond to the color of marker on map [[#227](https://github.com/JetBrains/lets-plot/issues/227)].
- tooltip on livemap: hide tooltip when the cursor is over the controls [[#335](https://github.com/JetBrains/lets-plot/issues/335)].
- Automatic detection of DateTime series [[#99](https://github.com/JetBrains/lets-plot-kotlin/issues/99)].
- Fix tooltips for `geom_histogram(stat='density')`.
- The axis tooltip overlaps the general tooltip [[#515](https://github.com/JetBrains/lets-plot/issues/515)].
- The multi-layer tooltip detection strategy will only be used if more than one layer provides tooltips.
- scaleColorManual Divide by Zero with 1 mapping [[#506](https://github.com/JetBrains/lets-plot/issues/506)].
- LinearBreaksHelper$Companion.computeNiceBreaks out of memory error [[#105](https://github.com/JetBrains/lets-plot-kotlin/issues/105)].