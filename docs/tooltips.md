# Tooltip Customization

- [Formatting tooltip fields](#formatting)
- [Customizing tooltip lines](#lines)
    - [Labels configuration](#labels-configuration)
- [Tooltip anchor](#tooltip-anchor)
- [Minimum width of a multi-line tooltip](#minwidth)
- [Examples](#examples)
- [Outlier tooltips configuration](#outliers)
    - [Examples](#example-outliers)    
- [Hiding tooltips](#hiding-tooltips)
- [Example Notebooks](#example-notebooks)    
    
------
You can customize the content of tooltips for the layer by using the parameter `tooltips` of `geom` functions.

The following functions set lines, define formatting of the tooltip, its location and width:

```
tooltips=layer_tooltips()
    .format(field, format)
    .line(template)
    .anchor(position)
    .min_width(value)
```


<a id="formatting"></a>
### Formatting tooltip fields: `layer_tooltips().format(field, format)`

Defines the format for displaying the value.
The format will be applied to the mapped value in the default tooltip or to the corresponding value specified in the `line` template.

#### Arguments

- `field` (string): The name of the variable/aesthetics.
The field name begins with `^` for aesthetics. You can specify variable names without a prefix, but the `@` prefix can be also used.
It's possible to set a format for all positional aesthetics: `^X` (all positional x) and `^Y` (all positional y).
For example:
    - `field = '^Y'` - for all positional y;
    - `field = '^y'` - for y aesthetic;
    - `field = 'y'` - for variable with the name "y".
    
- `format` (string): The format to apply to the field.
The format contains a number format (`'1.f'`) or a string template (`'{.1f}'`).
The numeric format for non-numeric value will be ignored. 
The string template contains “replacement fields” surrounded by curly braces `{}`. 
Any code that is not in the braces is considered literal text, and it will be copied unchanged to the result string. 
If you need to include a brace character into the literal text, it can be escaped by doubling: {{ and }}.
For example:
    - `.format('^color', '.1f')` -> `"17.0"`;
    - `.format('cty', '{.2f} (mpg)'))` -> `"17.00 (mpg)"`;
    - `.format('^color', '{{{.2f}}}')` -> `"{17.00}"`;
    - `.format('model', '{} {{text}}')` -> `"mustang {text}"`.

The string template in the `format` parameter will allow changing lines for the default tooltip without `line` specifying.

Variable's and aesthetic's formats are not interchangeable, for example, `var` format will not be applied to `aes` mapped to this variable.

<a id="lines"></a>
### Customizing tooltip lines: `layer_tooltips().line(template)`

Specifies the string template to use in a multi-line tooltip. If you add `line()`, it overrides the default tooltip.

Variables and aesthetics can be accessed via a special syntax:
- `^color` for aesthetic;
- `@year` for variable;
- `@{number of cylinders}` for a variable with spaces or non-word characters in the name;
- `@..count..` for statistics variables.

A '^' symbol can be escaped with a backslash; a brace character in the literal text - by doubling:
- `.line('text')` -> `"text"`;
- `.line('\^text')` -> `"^text"`;
- `.line('{{text}}')` -> `"{text}"`;
- `.line('@model')` -> `"mustang"`;
- `.line('{{@model}}')` -> `"{mustang}"`.

<a id="labels-configuration"></a>
#### Labels configuration
The default tooltip has a label before the value usually containing the name of the mapped variable.
It has its own behaviour similar to a blank label for an axis aesthetics. 
This default label can be set in the template by using a pair of symbols `@|`.
You can override the label by specifying a string value before `|` symbol.

Within the tooltip line, ou can align a label to left. The string formed by a template can be aligned to right.
If you do not specify a label, the string will be centered in the tooltip. For example:

- `line('^color')`: no label, value is centered;
- `line('|^color')`: label is empty, value is right-aligned;
- `line('@|^color')`: default label is used, value is right-aligned;
- `line('my label|^color')`: label is specified, value is right-aligned.

<a id="tooltip-anchor"></a>
### Tooltip anchor: `layer_tooltips().anchor(position)`
Specifies an anchor to place a multi-line tooltip.
The parameter of the `anchor` function specifies the position of the plot to place tooltip:
- 'top_right'
- 'top_center'
- 'top_left' 
- 'bottom_right' 
- 'bottom_center'
- 'bottom_left'
- 'middle_right'
- 'middle_center' 
- 'middle_left'
 
<a id="minwidth"></a>
### Minimum width of a multi-line tooltip: `layer_tooltips().min_width(value)`
Specifies the minimum width of a multi-line tooltip.

<a id="examples"></a>
### Examples

```
ggplot(mpg) + geom_point(aes(x='displ', y='cty', fill='drv', size='hwy'), shape=21, color='black',\
                         tooltips=layer_tooltips()\
                                    .format('cty', '.1f')
                                    .format('hwy', '.1f')
                                    .format('drv', '{}wd')
                                    .line('@manufacturer @model')
                                    .line('cty/hwy|@cty/@hwy')
                                    .line('@|@class')
                                    .line('drive train|@drv')
                                    .line('@|@year')) 
```
![](examples/images/tooltips_1.png)


Change format for the default tooltip:

```
ggplot(mpg) + geom_point(aes(x='displ', y='cty', fill='drv', size='hwy'), shape=21, color='black',\
                           tooltips=layer_tooltips().format('^color', '{.2f} (mpg)'))
```

![](examples/images/tooltips_2.png)


Place a multi-line tooltip at the top center and define its minimum width:

```
ggplot(mpg) + geom_point(aes(x='displ', y='cty', fill='drv', size='hwy'), shape=21, color='black',\
                          tooltips=layer_tooltips()
                                     .format('cty', '.1f')
                                     .format('hwy', '.1f')
                                     .format('drv', '{}wd')
                                     .line('@manufacturer @model')
                                     .line('cty/hwy|@cty/@hwy')
                                     .line('@|@class')
                                     .line('drive train|@drv')
                                     .line('@|@year')
                                     .anchor('top_center')
                                     .min_width(200)) 
```
![](examples/images/tooltips_6.png)

Move the tooltips to the top right corner:
```
 ggplot(iris_df) + theme(legend_position='none')\
 + geom_area(aes(x='sepal_length', color='sepal_width', fill='species'), stat='density',\
     tooltips=layer_tooltips()
                 .anchor('top_right')
                 .line('^fill')
                 .line('length|^x')
                 .line('density|^y'))
```                 
![](examples/images/tooltips_5.png)


<a id="outliers"></a>
## Outlier tooltips configuration

The default an outlier's tooltip contains a string like `'name: value'`: there is no label and no alignment.
It's possible to change formatting of it with the `format` function. The number format (`'1.f'` ) leaves 
the string as is (`'name: value'`) and formats the value. The string template replaces the default string:
`‘{.1f}` - with `'value'`, `‘format text {.1f}’` - with `“format text value”`.

The specified `line` for an outlier will move it to a general multi-line tooltip.
   
<a id="example-outliers"></a>  
### Examples

`p2 = ggplot(mpg, aes('class', 'hwy')) + theme(legend_position='none')` 


Change formatting for outliers:
```
p2 + geom_boxplot(tooltips=layer_tooltips()
                        .format('^Y', '.2f')          # all positionals
                        .format('^ymax', '.3f')       # use number format --> "ymax: value"
                        .format('^middle', '{.3f}')   # use line format --> "value"
                        .format('^ymin', 'ymin is {.3f}'))
```                        
![](examples/images/tooltips_3.png)

                  
Move outliers to a general tooltip:

`p2 + geom_boxplot(tooltips=layer_tooltips().line('lower/upper|^lower, ^upper'))`
![](examples/images/tooltips_4.png)
                 


<a id="hiding-tooltips"></a> 
## Hiding tooltips     
Set `tooltips = "none"` to hide tooltips from the layer.
          
<a id="example-notebooks"></a> 
 ## Example Notebooks
 
* [tooltip_config.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/tooltip_config.ipynb)
* Visualization of Airport Data on Map: <a href="https://www.kaggle.com/alshan/visualization-of-airport-data-on-map" title="View at Kaggle"> 
                                               <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_kaggle.svg" width="20" height="20">
                                        </a>
                                        <br>
