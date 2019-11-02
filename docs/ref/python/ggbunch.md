# GGBunch()
 
 Creates a new object - a collection of plots created with the `ggplot` function.
    Use `add_plot()` method to add plot to the `bunch`. Set arbitrary location and size for plots inside the grid.
    To display the final `bunch` object, use `show()` method.


### add_plot()

Add a plot to the bunch. Create the `ggplot()` object, specify plot parameters and add the resulting object to the grid.

`bunch.add_plot(self, plot_spec: PlotSpec, x, y, width=None, height=None)`

##### Arguments
- `plot_spec` (`ggplot()` object): Plot specifications set with `ggplot()` function
- `x` (number): x-coordinate of plot origin in px.
- `y` (number): y-coordinate of plot origin in px.
- `width` (number): Width of plot in px.
- `height` (number): Height of plot in px.

### show()

Display plots within the `bunch` object in a grid. 

`bunch.show()`

### Examples

`survived_by_adge = ggplot(titanic_df) \
          + geom_histogram(aes('age', fill='survived'), position='dodge') \
          + ggsize(800, 250)`
          
`bunch = GGBunch()
 bunch.add_plot(survived_by_adge, 0, 0, 600, 200)
 bunch.add_plot(path_map + theme(legend_position='none'), 0, 200)
 bunch.show()`
 
![](/assets/docs/ggplot/ggbunch_1.png)
 
 `survived_by_port = ggplot(titanic_df) \
          + geom_bar(aes('embarked', fill='survived'), position='dodge') \
          + xlab('') + ggtitle('Port of embarkation') \
          + theme(legend_position='none')
  bunch.add_plot(survived_by_port, 600, 200, 380, 250)
  bunch.show()`
  
![](/assets/docs/ggplot/ggbunch_2.png)
