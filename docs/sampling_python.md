# Sampling in Datalore Plot

`Sampling` is a special kind of data transformation which is built in to Datalore Plot and is applied after `stat` transformation.

`Sampling` helps when dealing with large datasets when unintentional attempt to plot excessively large number of geometries can lead to UI freeze and even out-of-memory crash.

`Sampling` is also one of the ways of dealing with over-plotting.

### How it works

`Sampling` kicks-in automatically when the data volume exceeds a configured threshold and also 
can be configured for individual geometry layer using `sampling` argument of `geom_xxx()` function. 

Value `"none"` will disable any samplings for given layer.

There are several sampling methods implemented in Datalore Plot. 
The sampling methods can be chained together using `+` operator.

### Sampling methods

**random** - selects data points at randomly chosen indices without replacement.

**systematic** - selects data points at evenly distributed indices. Unlike canonical systematic sampling, it starts at index 0 and chooses the step so that the last selected index was as close as possible to the last index in the data.

**pick** - intended mostly for bar chart and, unlike first two (above) it doesn’t pick indices. Instead it is going through X-values and selects all points which X-value is in the set of first n X-values found in the population.

**group random, group systematic** - similar to point-wise random/systematic sampling (above) but it selects the entire groups instead of individual data-points.

**random stratified** - randomly selects points from each group proportionally to the group size but also ensure that each group will be represented by at least specified minimal number of points.

**vertex** - designed for polygons simplification. Vertex sampling knows how to handle rings. There is a choice of two implementation algorithms: **Douglas-Peucker** and **Visvalingam-Whyatt**. 

When using group sampling, user specifies target number of groups in the sample which does not guarantee that the total number of points in the sample will be reasonably low.

For this case there is always a 'safety' sampling (**random** N=100_000) ready to kick-in if data volume is still too high.


### Examples

TBD: replace temporary URLs ---> permanent

Random sampling on scatter plot: 
[sampling_random.ipynb](https://nbviewer.jupyter.org/github/alshan/jupyter-examples/blob/master/notebooks/sampling_random.ipynb)

Pick sampling on Bars chart: 
[sampling_pick.ipynb](https://nbviewer.jupyter.org/github/alshan/jupyter-examples/blob/master/notebooks/sampling_pick.ipynb)

Systematic and random sampling on line plot: 
[sampling_systematic.ipynb](https://nbviewer.jupyter.org/github/alshan/jupyter-examples/blob/master/notebooks/sampling_systematic.ipynb)

Stratified sampling: 
[sampling_stratified.ipynb](https://nbviewer.jupyter.org/github/alshan/jupyter-examples/blob/master/notebooks/sampling_stratified.ipynb)

Group sampling:
[sampling_groups.ipynb](https://nbviewer.jupyter.org/github/alshan/jupyter-examples/blob/master/notebooks/sampling_groups.ipynb)

Vertex sampling:
[sampling_vertex.ipynb](https://nbviewer.jupyter.org/github/alshan/jupyter-examples/blob/master/notebooks/sampling_vertex.ipynb)


