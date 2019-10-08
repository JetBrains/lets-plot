## Overview

The library `datalore.plot` follows Grammar of graphics principles. To create a plot, map dataset variables to an aesthetics of your liking; adjust the result with scalings for more accurate data representation; and add themes, labels and coordinates on top to improve the data visualization.

- **Layer** is the data visual representation that combines data values, aesthetic mapping, a geom (geometric object), a stat (statistical transformation), and a position adjustment. 
- On top of layers there are **scales**, **themes**, **guides** and **coordinate systems**. These elements affect overall appearance of the plot rather than transforming data directly into the visual form. 

### Create a plot

To create a plot, use the `ggplot()` function and supply default data values and aesthethic mappings specified by `aes()`. Then add necessary layers, scales, coordinatess and facets with ``+=``.

```python
p = ggplot(dataset, aes(x, y));
p += geom_histogram()
```

### [Layers - geoms](geoms.md)
Typically, anything could be visualized through geoms. Edit the position and stats later if necessary.

- `geom_area() geom_ribbon()` | Area plots and ribbons
- `geom_bar()` | Bar chart 
- `geom_boxplot()` | Tukey-styled box-and-whiskers plot
- `geom_contour(), geom_contourf()` | 2d contours of a 3d surface
- `geom_density()` | Smoothed density estimates
- `geom_density2d(), geom_density2df()` | Contours of a 2d density estimate
- `geom_errorbar()` | Error bars
- `geom_freqpoly(), geom_histogram()` | Frequency polygons and histograms
- **`!!geom_image()` | Insert image directly into the plot**
- `geom_jitter()` | Jittered points
- **`!!geom_livemap` | TBC**
- `geom_path(), geom_line(), geom_step()`| Connect data points
- `geom_point()` | Points
- `geom_polygon()` | Polygons
- `geom_raster(), geom_tile()`, `geom_rect()` | Rectangles
- `geom_smooth()` | Smoothed conditional poligons
- `geom_segment()` | Line segments
- `geom_text()` | Add text

### Layer - stats
Stat_functions enable visualisation of statistical transformations of the original data, which is sometimes helps for more clear figure. Select the necessary function to draw percentage, means

### Layers - annotations

- `geom_abline(), geom_vline(), geom_hline()` | Reference lines: horizontal, vertical, and diagonal 

### [Layers - position adjustment](positions.md)
All layers have a position adjustment that resolves overlapping geoms. Override the default by using the position argument to the `geom_` or `stat_` function.

- `position_dodge()` | Dodge overlapping objects side-to-side
- `position_jitter()` | Jitter points to avoid overplotting
- `position_jitterdodge()` | Simultaneously dodge and jitter
- `position_nudge()`| Nudge points a fixed distance

### [Scales](scales.md)

Scales enable tailored adjustment of data values to visual properties and aesthetics. Override default scales to tweak details like the axis labels or legend keys, or to use a completely different translation from data to aesthetic. 

- `scale_alpha()` | Alpha transparency scale
- `scale_color_brewer(), scale_fill_brewer()` | Sequential, diverging and qualitative colour scales from colorbrewer.org
- `scale_color_continuous(), scale_fill_continuous()` | Continuous colour scales
- `scale_color_discrete(), scale_fill_discrete()` | Discrete colour scales
- `scale_color_gradient(), scale_fill_gradient(), scale_color_gradient2(), scale_fill_gradient2()` | Gradient colour scales
- `scale_color_identity(), scale_fill_identity(), scale_alpha_identity(), scale_linetype_identity(), scale_shape_identity(), scale_size_identity()` | Use values without scaling
- `scale_color_manual(), scale_fill_manual(), scale_alpha_manual(), scale_linetype_manual(), scale_shape_manual(), scale_size_manual()` | Create your own discrete scale
- `scale_color_grey(), scale_fill_grey()` | Sequential grey colour scales
- `scale_fill_hue(), scale_color_hue()` | Evenly spaced colours for discrete data
- `scale_shape()` | Scales for shapes, aka glyphs
- `scale_size(), scale_size_area()` | Scales for area
- `scale_x_continuous(), scale_y_continuous(), scale_x_log10(), scale_y_log10()` | Position scales for continuous data (x & y)
- `scale_x_datetime(),scale_y_datetime` | Position scales for date/time data
- `scale_x_discrete(), scale_y_discrete()` | Position scales for discrete data
- `ggtitle(), xlab(), ylab(), labs()` | Modify axis, legend, and plot labels
- `xlim(), ylim(), lims()` | Set scale limits


### [Guides](guide.md)
The guides (the axes and legends) help readers interpret your plots. Guides are mostly controlled via the scale (e.g. with the limits, breaks, and labels arguments), but sometimes you will need additional cover over the guide appearance. Use `guides()` or the guide argument to individual scales along with `guide_colourbar()` or `guide_legend()`.

- `guide_legend()` | Legend guide
- `guide_colorbar()` | Continuous colour bar guide

### [Facetting](facets.md)
Facetting generates small multiples, each displaying a different subset of the data. Facets are an alternative to aesthetics for displaying additional discrete variables.

- `facet_grid()` | Lay out panels in a grid

### [Coordinate systems](coordinates.md)
The coordinate system determines how the x and y aesthetics combine to position elements in the plot. Override the default Cartesian coordinate system `coord_cartesian()` with `coord_map()` or `coord_fixed()`.

- `coord_fixed()` | Cartesian coordinates with fixed "aspect ratio"
- `coord_cartesian()` | Cartesian coordinates
- `coord_map()` | Map projections


### [GGbunch](ggbunch.md)

### [Image matrix](image-matrix.md)
