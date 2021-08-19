## [2.1.1] - 2021-??-??

### Added
- Date-time formatting support:
  - tooltip format() should understand date-time format pattern [[#387](https://github.com/JetBrains/lets-plot/issues/387)];
  - scale_x_datetime should apply date-time formatting to the breaks [[#392](https://github.com/JetBrains/lets-plot/issues/392)].
    
  See: [Demo with examples](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks-dev/datetime_formatting.ipynb).


### Changed

### Fixed

- Tooltips for discrete variables: add the dependence of the tooltip on the number of factors.