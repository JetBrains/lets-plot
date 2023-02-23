#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .subplots import SupPlotsLayoutSpec
from .subplots import SupPlotsSpec

__all__ = ['gggrid']


def gggrid(figures: list, ncols: int = None, *,
           widths: list = None,
           heights: list = None,
           inner_alignment: bool = None
           ):
    """
    """

    if not len(figures):
        raise ValueError("Supplots list is empty.")

    if ncols is None:
        ncols = len(figures)
        nrows = 1
    else:
        extended_list = figures + [None] * (ncols - 1)
        nrows = len(extended_list) // ncols
        length = ncols * nrows
        figures = extended_list[0:length]

    layout = SupPlotsLayoutSpec(
        name="grid",
        ncols=ncols,
        nrows=nrows,
        widths=widths,
        heights=heights,
        inner_alignment=inner_alignment
    )

    return SupPlotsSpec(
        figures=figures,
        layout=layout
    )
