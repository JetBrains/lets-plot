#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.


class MappingMeta:
    def __init__(self, variable, annotation, levels, **parameters):
        if variable is None:
            raise ValueError("variable can't be none")

        if annotation is None:
            raise ValueError("annotation can't be none")

        self.variable = variable
        self.annotation = annotation
        self.levels = levels
        self.parameters = parameters


def as_discrete(variable, label=None, order_by=None, order=None, levels=None):
    """
    The function converts a column to a discrete scale and allows you to specify the order of its values.

    Parameters
    ----------
    variable : str
        The name of the variable.
    label : str
        The name of the scale - used as the axis label or the legend title.
    order_by : str
        The variable name to order by.
    order : int
        The ordering direction. 1 for ascending, -1 for descending.
    levels : list
        The list of values that defines a specific order of categories.

    Returns
    -------
    `MappingMeta` or list
        Variable meta information.

    Notes
    -----
    The plot will use a discrete scale for the aesthetic mapping.
    It is similar to the `factor()` function from R but works differently - there is no data transformation.

    To enable ordering mode, at least one ordering parameter (`order_by` or `order`) should be specified.
    By the default, it will use descending direction and ordering by eigenvalues.
    You cannot specify different order settings for the same variable.
    But if these settings don't contradict each other, they will be combined.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 13

        import numpy as np
        from lets_plot import *
        from lets_plot.mapping import as_discrete
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        data = {
            'x': np.random.normal(size=n),
            'y': np.random.normal(size=n),
            'c': np.random.randint(5, size=n),
        }
        ggplot(data, aes('x', 'y')) + \\
            geom_point(aes(color=as_discrete('c')))

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 12

        import numpy as np
        from lets_plot import *
        from lets_plot.mapping import as_discrete
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        data = {
            'x': np.random.uniform(size=100),
            'c': np.random.choice(list('abcde'), size=100),
        }
        ggplot(data) + \\
            geom_boxplot(aes(as_discrete('c', label='class', order=1), 'x'))

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 13-14

        import numpy as np
        from lets_plot import *
        from lets_plot.mapping import as_discrete
        LetsPlot.setup_html()
        n = 100
        np.random.seed(42)
        data = {
            'x': np.random.normal(size=n),
            'c': np.random.choice(list('abcde'), size=n),
            'i': np.random.randint(3, size=n),
        }
        ggplot(data) + \\
            geom_bar(aes(as_discrete('c', order=1, order_by='..count..'), 'x', \\
                         fill=as_discrete('i', order=1, order_by='..count..')))

    """
    if isinstance(variable, str):
        label = variable if label is None else label
        return MappingMeta(variable, 'as_discrete', levels=levels, label=label, order_by=order_by, order=order)
    # aes(x=as_discrete([1, 2, 3])) - pass as is
    return variable
