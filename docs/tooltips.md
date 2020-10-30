# Tooltip configuration

- [Formatting tooltip fields](#formatting)
- [Customizing tooltip lines](#lines)
    - [Labels configuration](#labels-configuration)
- [Examples](#examples)
- [Outlier tooltips configuration](#outliers)
    - [Examples](#example-outliers)    
- [Hiding tooltips](#hiding-tooltips)
- [Corner tooltips](#corner-tooltips)
    - [Example](#example-corners)
    
------
It is possible to customize the content of tooltips for the layer. The parameter `tooltips` of `geom` functions is responsible for this.

The following functions are used to set lines and define formatting in the tooltip:

`tooltips=layer_tooltips().format(field, format).line(template)`


<a id="formatting"></a>
### Formatting tooltip fields: `layer_tooltips().format(field, format)`

Defines the format for displaying the value.
The format will be applied to the mapped value in the default tooltip or to the corresponding value specified in the `line` template.

#### Arguments

- `field` (string): The name of variable/aesthetic.
The field name begins with `$` for aesthetics. Variable names are specified without prefix, but the `@` prefix can be also used.
It's possible to set the format for all positional aesthetics: `$X` (all positional x) and `$Y` (all positional y).
For example:
    - `field = '$Y'` - for all positional y;
    - `field = '$y'` - for y aesthetic;
    - `field = 'y'` - for variable with name "y".
    
- `format` (string): The format to apply to the field.
The format contains a number format (`'1.f'`) or a string template (`'{.1f}'`).
The numeric format for non-numeric value will be ignored. 
The string template contain “replacement fields” surrounded by curly braces `{}`. 
Anything that is not contained in braces is considered literal text, which is copied unchanged to the result string. 
If you need to include a brace character in the literal text, it can be escaped by doubling: {{ and }}.
For example:
    - `.format('$color', '.1f')` -> `"17.0"`;
    - `.format('cty', '{.2f} (mpg)'))` -> `"17.00 (mpg)"`;
    - `.format('$color', '{{{.2f}}}')` -> `"{17.00}"`;
    - `.format('model', '{} {{text}}')` -> `"mustang {text}"`.

The string template in format will allow to change lines for the default tooltip without `line` specifying.

Variable's and aesthetic's formats are not interchangeable, i.e. var format will not be applied to aes, mapped to this variable.

<a id="lines"></a>
### Customizing tooltip lines: `layer_tooltips().line(template)`

Specifies the string template to use in the multi-line tooltip. The presence of `line()` overrides the default tooltip.

Variables and aesthetics can be accessed via a special syntax:
- `$color` for aesthetic;
- `@year` for variable;
- `@{number of cylinders}` for variable with spaces or non-word characters in the name;
- `@..count..` for statistics variables.

A dollar sign can be escaped with a backslash, a brace character in the literal text - by doubling:
- `.line('text')` -> `"text"`;
- `.line('\$text')` -> `"$text"`;
- `.line('{{text}}')` -> `"{text}"`;
- `.line('@model')` -> `"mustang"`;
- `.line('{{@model}}')` -> `"{mustang}"`.

<a id="labels-configuration"></a>
#### Labels configuration
The default tooltip has a label before the value, usually containing the name of the mapped variable.
It has it's own behaviour, like blank label for axis aesthetics. 
This default label can be set in template using a pair of symbols `@|`.
The label is overridden by specifying a string value before `|` symbol.

Within the tooltip line the label is aligned to the left, the string formed by template is aligned to the right.
If a label is not specified, the string will be centered in the tooltip. For example:


- `line('$color')`: no label, value is centered;
- `line('|$color')`: label is empty, value is right-aligned;
- `line('@|$color')`: default label is used, value is right-aligned;
- `line('my label|$color')`: label is specified, value is right-aligned.

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
![](ref/assets/tooltips_1.png)


Change format for the default tooltip:

```
ggplot(mpg) + geom_point(aes(x='displ', y='cty', fill='drv', size='hwy'), shape=21, color='black',\
                           tooltips=layer_tooltips().format('$color', '{.2f} (mpg)'))
```

![](ref/assets/tooltips_2.png)




<a id="outliers"></a>
## Outlier tooltips configuration

The default outlier's tooltip contains a string like `'name: value'`: there is no label and no alignment.
It's possible to change formatting of it with `format` function. The number format (`'1.f'` ) leaves 
the string as is (`'name: value'`) and formats the value. The string template replaces the default string:
`‘{.1f}` - with `'value'`, `‘format text {.1f}’` - with `“format text value”`.

The specified `line` for outlier will move it to the general multi-line tooltip.
   
<a id="example-outliers"></a>  
### Examples

`p2 = ggplot(mpg, aes('class', 'hwy')) + theme(legend_position='none')` 


Change formatting for outliers:
```
p2 + geom_boxplot(tooltips=layer_tooltips()
                        .format('$Y', '.2f')          # all positionals
                        .format('$ymax', '.3f')       # use number format --> "ymax: value"
                        .format('$middle', '{.3f}')   # use line format --> "value"
                        .format('$ymin', 'ymin is {.3f}'))
```                        
![](ref/assets/tooltips_3.png)

                  
Move outliers to the general tooltip:

`p2 + geom_boxplot(tooltips=layer_tooltips().line('lower/upper|$lower, $upper'))`
![](ref/assets/tooltips_4.png)
                 


<a id="hiding-tooltips"></a> 
## Hiding tooltips     
Set `tooltips = "none"` to hide tooltips from the layer.
          
<a id="corner-tooltips"></a>
## Corner tooltips
The multi-line tooltip can be placed in the corner. 
The parameter `tooltip_anchor` of `theme` specifies the corner of the plot to place tooltip:
- 'top_right' 
- 'top_left' 
- 'bottom_right' 
- 'bottom_left'

<a id="example-corners"></a> 
### Example
```
 ggplot(iris_df) + theme(legend_position='none', tooltip_anchor='top_right')\
 + geom_area(aes(x='sepal_length', color='sepal_width', fill='species'), stat='density',\
     tooltips=layer_tooltips()
                 .line('$fill')
                 .line('length|$x')
                 .line('density|$y'))
```                 
 ![](ref/assets/tooltips_5.png)
