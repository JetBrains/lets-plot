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
    thin wrapper around geom_blank().

    Parameters
    ----------
    x, y, size, color, fill, alpha, shape : Any, list, tuple or range
        List of name-value pairs specifying the value (or values) that should be included in each scale.
        These parameters extend the corresponding plot dimensions or aesthetic scales.

    Returns
    -------
    FeatureSpec
        A result of the `geom_blank()` call.

    Examples
    --------

    """
    params = locals()

    def standardize(value):
        if value is None:
            return [None]
        elif isinstance(value, (list, tuple, range)):
            return list(value)
        else:
            return [value]

    standardized = {k: standardize(v) for k, v in params.items()}

    max_length = max(len(v) for v in standardized.values())
    raw_data = {k: v + [None] * (max_length - len(v)) for k, v in standardized.items()}

    # remove all undefined but keep x and y even if undefined.
    filtered_data = {k: v for k, v in raw_data.items() if k in ['x', 'y'] or not all(e is None for e in v)}
    return geom_blank(mapping=aes(**filtered_data))
