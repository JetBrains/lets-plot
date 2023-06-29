#  Copyright (c) 2023. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
from .geom import _geom

#
# Stats - functions, drawing attention to the statistical transformation rather than the visual appearance.
#
__all__ = ['stat_summary']


def stat_summary(mapping=None, *, data=None, geom='pointrange',
                 position=None, show_legend=None, sampling=None, tooltips=None,
                 orientation=None,
                 fun=None, fun_min=None, fun_max=None,
                 quantiles=None,
                 color_by=None, fill_by=None,
                 **other_args):
    return _geom(geom,
                 mapping=mapping,
                 data=data,
                 stat='summary',
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 orientation=orientation,
                 fun=fun, fun_min=fun_min, fun_max=fun_max,
                 quantiles=quantiles,
                 color_by=color_by, fill_by=fill_by,
                 **other_args)
