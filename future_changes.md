## [1.5.5] - 2020-12-??

### Added
 - new tooltip anchor options - center and middle.
 - min width of a multi-line tooltip: `geom_xxx( tooltips=layer_tooltips().min_width(180) )`
 - stat_corr()
 - 'bistro' package, corr_plot()
 - 'no_js' parameter in LetsPlot.setup_html(), 'LETS_PLOT_NO_JS' env var.
 - 'na_text' parameter in 'geom_text'

### Changed

- Setting for tooltip anchor was moved from theme() to layer().
Now it can be set using the `tooltips` parameter:

   `geom_xxx( tooltips=layer_tooltips().anchor(anchor_value) )`, 
   where `anchor_value`: 
   `['top_right'|'top_center'|'top_left'|'bottom_right'|'bottom_center'|'bottom_left'|'middle_right'|'middle_center'|'middle_left']`.
 
### Fixed
 - Crosshair for corner tooltips. 
