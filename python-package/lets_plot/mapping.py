#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.


class MappingMeta:
    def __init__(self, variable, annotation, **parameters):
        if variable is None:
            raise ValueError("variable can't be none")

        if annotation is None:
            raise ValueError("annotation can't be none")

        self.variable = variable
        self.annotation = annotation
        self.parameters = parameters


def as_discrete(variable, label=None, order_by=None, order=None):
    """
    Marks a numeric variable as categorical.

    Parameters
    ----------
    variable : string
        The name of the variable

    label : string
        The name of the scale - used as the axis label or the legend title

    order_by : string
        The variable name to order by.
    order : int
        The ordering direction. 1 - ascending, -1 - descending.

    Returns
    -------
        variable meta information

    Notes
    -----
    The plot will use a discrete scale for the aesthetic mapping.
    It is similar to the factor() function from R but works differently - there is no data transformation.
    Examples
    ---------
    >>> df = {
    >>>       'x': [0, 5, 10, 15],
    >>>       'y': [0, 5, 10, 15],
    >>>       'a': [1, 2, 3, 2]
    >>> }
    >>> ggplot(df, aes(x='x', y='y')) + geom_point(aes(color=pm.as_discrete('a')), size=9)
    """
    if isinstance(variable, str):
        label = variable if label is None else label
        return MappingMeta(variable, 'as_discrete', label=label, order_by=order_by, order=order)
    # aes(x=as_discrete([1, 2, 3])) - pass as is
    return variable
