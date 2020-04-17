#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.


class MappingMeta:
    def __init__(self, variable, annotation):
        if variable is None:
            raise ValueError("variable can't be none")

        if annotation is None:
            raise ValueError("annotation can't be none")

        self.variable = variable
        self.annotation = annotation


def as_discrete(variable):
    """
    Marks a numeric variable as categorical.

    Parameters
    ----------
    variable : string
        The name of the variable

    Returns
    -------
        variable meta information

    Notes
    -----
    The plot will use a discrete scale for the aesthetic mapping.
    It is similar to the factor() function from R but works differently - there is no data transformation.
    Examples
    ---------
    >>> from lets_plot import *
    >>> import lets_plot.mapping as pm
    >>> load_lets_plot_js()
    >>> df = {
    >>>       'x': [0, 5, 10, 15],
    >>>       'y': [0, 5, 10, 15],
    >>>       'a': [1, 2, 3, 2]
    >>> }
    >>> ggplot(df, aes(x='x', y='y')) + geom_point(aes(color=pm.as_discrete('a')), size=9)
    """
    if isinstance(variable, str):
        return MappingMeta(variable, 'as_discrete')
    # aes(x=as_discrete([1, 2, 3])) - pass as is
    return variable
