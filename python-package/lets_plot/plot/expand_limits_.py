#  Copyright (c) 2024. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
from .core import aes
from .geom import geom_blank

__all__ = ['expand_limits']

def expand_limits(*, x=None, y=None, size=None, color=None, fill=None, alpha=None, shape=None):
    """
    Expand the plot limits to include additional data values.

    This function extends the plot boundaries to encompass new data points,
    whether a single value or multiple values are provided. It acts as a
    thin wrapper around `geom_blank() <https://lets-plot.org/python/pages/api/lets_plot.geom_blank.html>`__.

    Parameters
    ----------
    x, y, size, color, fill, alpha, shape : Any, list, tuple or range
        List of name-value pairs specifying the value (or values) that should be included in each scale.
        These parameters extend the corresponding plot dimensions or aesthetic scales.

    Returns
    -------
    FeatureSpec
        A result of the `geom_blank() <https://lets-plot.org/python/pages/api/lets_plot.geom_blank.html>`__ call.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'x': [-3, 0, 1],
            'y': [2, 3, -1],
        }

        # Include the value -10 along the x-axis
        ggplot(data, aes('x', 'y')) + geom_point() + \\
            expand_limits(x=-10)

    |

    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10

        from lets_plot import *
        LetsPlot.setup_html()
        data = {
            'x': [-3, 0, 1],
            'y': [2, 3, -1],
        }

        # Expand Limits Along the y-axis
        ggplot(data, aes('x', 'y')) + geom_point() + \\
            expand_limits(y=range(-10, 10))

    """
    params = locals()

    def standardize(value):
        if isinstance(value, (list, tuple, range)):
            return list(value)
        else:
            return [value]

    standardized = {k: standardize(v) for k, v in params.items()}

    # Drop all undefined but keep x and y even if undefined.
    cleaned = {k: v for k, v in standardized.items() if k in ['x', 'y'] or not all(e is None for e in v)}

    max_length = max(len(v) for v in cleaned.values())
    data = {k: v + [None] * (max_length - len(v)) for k, v in cleaned.items()}

    return geom_blank(mapping=aes(**data))
