#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.


class VariableMeta:
    def __init__(self, name, kind):
        if name is None:
            raise ValueError("name can't be none")

        if kind is None:
            raise ValueError("kind can't be none")

        self.name = name
        self.kind = kind


def factor(var_name):
    """
    Marks a numeric variable as categorical.

    Parameters
    ----------
    var_name : string
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
    >>> ggplot(df, aes(x='x', y='y')) + geom_point(aes(color=pm.factor('a')), size=9)
    """
    if isinstance(var_name, str):
        return VariableMeta(var_name, 'discrete')
    # aes(x=factor([1, 2, 3])) - pass as is
    return var_name
