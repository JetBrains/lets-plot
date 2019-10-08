# Layers - position adjustment
Layers enable a position adjustment to avoid overlapping geoms. Change the default positioning by using the `position` argument to the `geom` or `stat` function.

### `position_dodge()` 

Dodging adjusts the horizontal geom positioning while keeping the the vertical position. 

`position = position_dodge(width=None)`

##### Arguments
* `width` | Dodging width, when different to the width of the individual elements.

##### Examples

`p = ggplot(dat, aes('number', 'value', fill='subgroup'))
p += geom_bar(stat='identity', position='dodge')
p`

`p = ggplot(dat, aes('number', 'value', fill='subgroup'))
p += geom_bar(stat='identity', position = position_dodge()
p`

### `position_jitter()` 

Jittering adds a little random noise to the plot elements, shifting overlapping individual data points aside. This provides cleaner visualisation for scatterplots with a discrete variable. Shift distance is relative to the data resolution in pixels. 

`position_jitter(width=None, height=None)`

##### Arguments
* `width` | Jittering width 
* `height` | Jittering height

##### Examples
`p = ggplot(iris_df, aes(x='target', y='sepal width (cm)', color='target')) \
         + geom_point();
 p`
 
`p = ggplot(iris_df, aes(x='target', y='sepal width (cm)', color='target')) \
          + geom_point(position='jitter')
p`

### `position_jitterdodge()` 
Dodge and jitter data points simultaneously.

`position_jitterdodge(dodge_width=None, jitter_width=None, jitter_height=None)`

##### Arguments
* `dodge_width` | Dodging width  
* `jitter_width` | Jittering width
* `jitter_height` | Jittering height

##### Examples
`p = ggplot(mpg, aes('class', 'hwy', group='cyl', color='cyl'))
 p += geom_boxplot()
 p += geom_point(position=position_jitterdodge())
 p`

### `position_nudge()`

Enables shifting data points by a small fixed amount of pixels. Built-in to `geom_text()`, it allows shifting labels aside from the labelled points to avoid overlapping labels and data. 

`position_nudge(x=None, y=None)`

##### Arguments
* `x` | Nudging width
* `y` | Nudging height

##### Examples

`ggplot(dat, aes(x, y)) +
   geom_point() +
   geom_text(aes(label = y))`
   
`ggplot(dat, aes(x, y)) +
   geom_point() +
   geom_text(aes(label = y), position = position_nudge(y = 0.1))`
   
   
#   
Another way to adjust position is to set the `position` argument for each geom:
   - `identity` 
   - `jitter` 
   - `dodge` 
   - `stack` 
   - `fill`
