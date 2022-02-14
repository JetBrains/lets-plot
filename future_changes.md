## [2.2.2] - 2021-12-??

### Added

- geom_livemap: button for position reset.

- Plot subtitle: `subtitle` parameter in `ggtitle()` and `labs()`, `plot_subtitle` parameter in `theme()` [[#417](https://github.com/JetBrains/lets-plot/issues/417)]. 
  
    See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22-02/notebooks/title_and_subtitle.ipynb).

- Multi-line title: a title and subtitle can be split into multiple lines using `\n` as a text separator.
    
  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22-02/notebooks/title_and_subtitle.ipynb).

- geom_livemap: objects scale on zoom.
- In tooltip customization API:
  - The `title()` option defines the line, which will be at the beginning of the general tooltip, centered and highlighted in bold.
  - Word wrap:
      - use `\n` as a text separator to split the original string into multiple lines;
      - the value is limited by the number of characters in it.
  - See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-22-02/notebooks/tooltip_title.ipynb.ipynb).
    

### Changed

- geom_livemap: new parameter `show_coord_pick_tools` to show "copy location" and "draw geometry" buttons

- New tooltip style: rounded corners, bold label, colored marker inside the tooltip.

- Deprecated tooltip customization API:
  function `color()` will be removed in one of the future releases.

### Fixed

- LiveMap, Swing-batik: legend is not visible when overlapping map [[#496](https://github.com/JetBrains/lets-plot/issues/496)].
- CVE-2021-23792 in org.jetbrains.lets-plot:lets-plot-image-export@2.2.1 [[#497](https://github.com/JetBrains/lets-plot/issues/497)].
- Color in tooltip does not correspond to the color of marker on map [[#227](https://github.com/JetBrains/lets-plot/issues/227)].
- tooltip on livemap: hide tooltip when the cursor is over the controls [[#335](https://github.com/JetBrains/lets-plot/issues/335)].
- Automatic detection of DateTime series [[#99](https://github.com/JetBrains/lets-plot-kotlin/issues/99)].
- Fix tooltips for `geom_histogram(stat='density')`.