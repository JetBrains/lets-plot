
- [Function as_discrete](#description)
- [Examples](#examples)
- [Example Notebooks](#example-notebooks)



<a id="description"></a>
### Function as_discrete()

The function `as_discrete()` is used to annotate a numeric data series as categorical data with the possibility of its ordering
for the purposes of given visualization.



#### Usage:

```as_discrete(variable, label=None, order_by=None, order=None)```

where

* `variable` (string) - the name of the data variable (which is mapped to the plot aesthetic);
* `label` (string) - the name of the scale - it will be used as the axis label or as the legend title;
* `order_by` (string) - the name of the variable by which the ordering will be performed;
* `order` (int) - the ordering direction - `1` for ascending direction and `-1` for descending (default value).

To enable ordering mode, at least one ordering parameter (`order_by` or `order`) should be specified.
By the default, it will use descending direction and ordering by eigenvalues.
You cannot specify different order settings for the same variable. However, if these settings don't contradict each other, they will be combined.

The `order_by` is a numeric variable, which values are used for reordering. It's also possible to use statistical variables.
The reordering uses the average value. The exception is plots with the `stack` position adjustment, where multiple bars occupying the same `x` position are stacked atop one another: 
in this case, the sum is calculated to get the order of the stack sizes.


<a id="examples"></a>
### Examples
```
p = ggplot(mpg)
p + geom_point(aes('displ', 'hwy', color='cyl'))
```
![](examples/images/as_discrete_1.png)

Let's annotate the 'cyl' variable as discrete using the `as_discrete('cyl')` function.
As a result, the data is divided into groups, a discrete color scale is assigned instead of a continuous one:
```
p + geom_point(aes('displ', 'hwy', color=as_discrete('cyl')))
```
![](examples/images/as_discrete_2.png)

Set the 'cyl' variable in ascending order of its values:
```
p + geom_point(aes('displ', 'hwy', color=as_discrete('cyl', order=1)))
```
![](examples/images/as_discrete_3.png)


Boxplot example:
```
p + geom_boxplot(aes('class', 'hwy'))
```
![](examples/images/as_discrete_4.png)

Order `x` alphabetically
```
p + geom_boxplot(aes(as_discrete('class', order=1), 'hwy'))

```
![](examples/images/as_discrete_5.png)


Order `x` by another variable - in descending order of the median:

```
p + geom_boxplot(aes(as_discrete('class', order_by='..middle..'), 'hwy'))
```
![](examples/images/as_discrete_6.png)

Add `color` associated with the same variable. 
The ordering is also applied to it, which will be visible in the legend:
```
p + geom_boxplot(aes(as_discrete('class', order=1), 'hwy', color='class'))
```
![](examples/images/as_discrete_7.png)

Two different ordering settings are specified for the `class` variable. 
These settings don't contradict each other. This means that they will be combined,
and the variable will be ordered in ascending order `ymax`:
```
p + geom_boxplot(aes(as_discrete('class', order_by='..ymax..'), 'hwy', color=as_discrete('class', order=1)))
```
![](examples/images/as_discrete_8.png)

Example of ordering for two variables:
```
p + geom_bar(aes(x=as_discrete('manufacturer', order=1), fill=as_discrete('class', order=1)), color='black')

```
![](examples/images/as_discrete_9.png)

Reorder `x` by counts to get from highest on the left to lowest on the right:
```
p + geom_bar(aes(x=as_discrete('manufacturer', order_by='..count..'), fill=as_discrete('class', order=1)), color='black')
```
![](examples/images/as_discrete_10.png)

Apply sampling to the plot after reordering:
```
p + geom_bar(aes(x=as_discrete('manufacturer', order_by='..count..'), fill=as_discrete('class', order=1)), color='black', sampling=sampling_pick(4))
```
![](examples/images/as_discrete_11.png)

<a id="example-notebooks"></a>
## Example Notebooks

* [geom_smooth.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot-docs/blob/master/source/examples/cookbook/geom_smooth.ipynb)
* [ordering_examples.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot-docs/blob/master/source/examples/cookbook/ordering_examples.ipynb)
