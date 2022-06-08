#  Copyright (c) 2022. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from lets_plot.plot.core import PlotSpec

__all__ = ['qqplot']


def qqplot(data=None, sample=None, *, x=None, y=None) -> PlotSpec:
    return PlotSpec(data=data, mapping=None, scales=[], layers=[], bistro={
        'name': 'qqplot',
        'sample': sample,
        'x': x,
        'y': y,
    })
