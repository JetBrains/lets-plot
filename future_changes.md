## [2.1.1] - 2021-??-??

### Added
- `coord_flip()`, see [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-10-21/notebooks/coord_flip.ipynb). 
- Date-time formatting support:
  - tooltip format() should understand date-time format pattern [[#387](https://github.com/JetBrains/lets-plot/issues/387)];
  - scale_x_datetime should apply date-time formatting to the breaks [[#392](https://github.com/JetBrains/lets-plot/issues/392)].
    
  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-10-21/notebooks/datetime_formatting.ipynb).

- Pre-configured themes:
  - Standard ggplot2 themes: `theme_grey(), theme_light(), theme_classic(), theme_minimal()`;
  - Other themes: `theme_minimal2()` - the default theme, `theme_none()`.

- Theme modification: more parameters were added the `theme()` function. 
> Note: fonts size, family and face can not yet be configured. 

  See: [example notebook](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/f-10-21/notebooks/themes.ipynb).


### Changed

- The size of fonts on plot was slightly increased all across the board.
- The default plot size was increased by 20%, it's now 600x400 px. 

### Fixed
  
- Ordering facets - the "order" value 0 disables facet ordering [[#454](https://github.com/JetBrains/lets-plot/issues/454)].
- Tooltips for discrete variables: add the dependence of the tooltip on the number of factors.
  The X-axis tooltip is always shown for discrete data.
- map_join doesn't work when join variables with same names without mappings [[#428](https://github.com/JetBrains/lets-plot/issues/428)].
- Zoom without clipping breaks tooltips [[#373](https://github.com/JetBrains/lets-plot/issues/373)].
- Unreadable breaks on axis [[#430](https://github.com/JetBrains/lets-plot/issues/430)].
- Map rendering is broken when browser uses scaling [[#450](https://github.com/JetBrains/lets-plot/issues/450)].