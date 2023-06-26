#  Copyright (c) 2023. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
from .core import aes
from .geom import _geom

#
# Stats - functions, drawing attention to the statistical transformation rather than the visual appearance.
#
__all__ = ['stat_summary']


def stat_summary(mapping=None, *, data=None, geom='pointrange',
                 position=None, show_legend=None, sampling=None, tooltips=None,
                 orientation=None,
                 fun=None, fun_min=None, fun_max=None, fun_map=None,
                 quantiles=None,
                 color_by=None, fill_by=None,
                 **other_args):
    def fun_to_stat(fun_name):
        prefix = "y" if fun_name in ["min", "max"] else ""
        return "..{0}{1}..".format(prefix, fun_name)

    mapping_dict = mapping.as_dict() if mapping is not None else {}
    fun_mapping_dict = {aes_name: fun_to_stat(fun_name) for aes_name, fun_name in (fun_map or {}).items()}
    inner_mapping_dict = {**fun_mapping_dict, **mapping_dict}
    inner_mapping = aes(**inner_mapping_dict) if len(inner_mapping_dict.keys()) > 0 else None

    return _geom(geom,
                 mapping=inner_mapping,
                 data=data,
                 stat='summary',
                 position=position,
                 show_legend=show_legend,
                 sampling=sampling,
                 tooltips=tooltips,
                 orientation=orientation,
                 fun=fun, fun_min=fun_min, fun_max=fun_max, fun_map=fun_map,
                 quantiles=quantiles,
                 color_by=color_by, fill_by=fill_by,
                 **other_args)
