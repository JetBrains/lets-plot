# Facetting

### facet_grid()

Lay out panels in a grid. `facet_grid()` forms a matrix of panels defined by row and column faceting variables. It is most useful for exploratory data analysis, when you have two discrete variables, and all combinations of the variables exist in the data.

`facet_grid(x=None, y=None)`

##### Arguments
   
- `x` (string, optional): Columns of the facet grid to be displayed.
- `y` (string, optional): Rows of the facet grid to be displayed.

##### Examples
 
```python
 mean = norm(loc=0, scale=5).rvs(size=3)
 X = multivariate_normal(mean=mean, cov=0.1).rvs(1000)
 dat = pd.melt(pd.DataFrame(X))
 p = ggplot(dat) + geom_histogram()
 p + facet_grid(y='variable')
```

