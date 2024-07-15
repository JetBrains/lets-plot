#  Copyright (c) 2024. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from lets_plot.plot.core import PlotSpec

__all__ = ['waterfall_plot']


def waterfall_plot(data, x, y, *,
                   color=None, fill=None, size=None, alpha=None, linetype=None,
                   width=None,
                   show_legend=None, tooltips=None,
                   sorted_value=None, threshold=None, max_values=None,
                   calc_total=None, total_title=None,
                   hline=None, hline_ontop=None,
                   connector=None,
                   label=None, label_format=None) -> PlotSpec:
    return PlotSpec(data=data, mapping=None, scales=[], layers=[], bistro={
        'name': 'waterfall',
        'x': x,
        'y': y,
        'color': color,
        'fill': fill,
        'size': size,
        'alpha': alpha,
        'linetype': linetype,
        'width': width,
        'show_legend': show_legend,
        'tooltips': tooltips,
        'sorted_value': sorted_value,
        'threshold': threshold,
        'max_values': max_values,
        'calc_total': calc_total,
        'total_title': total_title,
        'hline': hline,
        'hline_ontop': hline_ontop,
        'connector': connector,
        'label': label,
        'label_format': label_format,
    })