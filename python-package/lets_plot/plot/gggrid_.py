#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .subplots import SupPlotsLayoutSpec
from .subplots import SupPlotsSpec

__all__ = ['gggrid']


def gggrid(plots: list, ncol: int = None, *,
           widths: list = None,
           heights: list = None,
           fit: bool = None,
           align: bool = None
           ):
    """
    """

    if not len(plots):
        raise ValueError("Supplots list is empty.")

    if ncol is None:
        ncol = len(plots)
        nrow = 1
    else:
        extended_list = plots + [None] * (ncol - 1)
        nrow = len(extended_list) // ncol
        length = ncol * nrow
        plots = extended_list[0:length]

    layout = SupPlotsLayoutSpec(
        name="grid",
        ncol=ncol,
        nrow=nrow,
        widths=widths,
        heights=heights,
        fit=fit,
        align=align
    )

    return SupPlotsSpec(
        figures=plots,
        layout=layout
    )
