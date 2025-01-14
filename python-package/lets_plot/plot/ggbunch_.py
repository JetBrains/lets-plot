#  Copyright (c) 2025. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from numbers import Number
from typing import List, Tuple

from ._global_theme import _get_global_theme
from .subplots import SupPlotsLayoutSpec
from .subplots import SupPlotsSpec
from .subplots_util import _strip_theme_if_global

__all__ = ['ggbunch']


# ggbunch(
#     [p1, p2, p3, p4],
#     (1, 1, 2, 2),
#     (1, 1, 2, 2),
#     (1, 1, 2, 2),
#     (1, 1, 2, 2),
# )


def ggbunch(plots: List,
            *regions: Tuple[float, float, float, float]
            ) -> SupPlotsSpec:
    """
    Combine several plots on one figure, organized in a regular grid.

    Parameters
    ----------
    plots : list
        A list of plot specifications. ToDo...

    Returns
    -------
    `SupPlotsSpec`
        The plor bunch specification.


    """

    if not len(plots):
        raise ValueError("Supplots list is empty.")

    # Validate provided regions
    for i, region in enumerate(regions):
        if len(region) != 4:
            raise ValueError(f"Region {i} must have exactly 4 values, got {len(region)}")
        if not all(isinstance(x, Number) for x in region):
            raise ValueError(f"Region {i} contains non-numeric values: {region}")

        # Validate size is positive
        if any(x <= 0 for x in region[2:]):
            raise ValueError(f"Region {i} sizes must be positive: {region}")

    # Convert *regions (tuple of tuples) to list of lists
    regions_list = [list(r) for r in regions]
    layout = SupPlotsLayoutSpec(
        name="free",
        regions=regions_list
    )

    figures = [_strip_theme_if_global(fig) for fig in plots]

    figure_spec = SupPlotsSpec(figures=figures, layout=layout)

    # Apply global theme if defined
    global_theme_options = _get_global_theme()
    if global_theme_options is not None:
        figure_spec += global_theme_options

    return figure_spec
