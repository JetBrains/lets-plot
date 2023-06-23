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
                 color_by=None, fill_by=None,
                 **other_args):
    mapping_dict = mapping.as_dict() if mapping is not None else {}

    quantile_agg_functions = {"qa": None, "qb": None, "qc": None}

    def get_stat_name(agg_fun):
        if isinstance(agg_fun, str) and agg_fun not in ["q1", "q2", "q3"]:
            prefix = "" if agg_fun not in ["min", "max"] else "y"
            return prefix + agg_fun
        else:
            name = next((q for (q, f) in quantile_agg_functions.items() if f is None or f == agg_fun), None)
            if name is None:
                raise Exception("No more than three different quantiles can be used in fun_map parameter")
            quantile_agg_functions[name] = agg_fun
            return name

    inner_fun_map = {}
    fun_mapping_dict = {}
    for aes_name, fun_name in (fun_map or {}).items():
        stat_name = get_stat_name(fun_name)
        inner_fun_map[stat_name] = fun_name
        fun_mapping_dict[aes_name] = "..{0}..".format(stat_name)
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
                 fun=fun, fun_min=fun_min, fun_max=fun_max, fun_map=inner_fun_map,
                 color_by=color_by, fill_by=fill_by,
                 **other_args)
