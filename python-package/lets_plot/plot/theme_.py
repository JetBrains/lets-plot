#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

__all__ = ['theme', 'element_blank']


def theme(*,
          axis_title=None, axis_title_x=None, axis_title_y=None,
          axis_text=None, axis_text_x=None, axis_text_y=None,
          axis_ticks=None, axis_ticks_x=None, axis_ticks_y=None,
          axis_line=None, axis_line_x=None, axis_line_y=None,
          legend_position=None,
          axis_tooltip=None, axis_tooltip_x=None, axis_tooltip_y=None,
          **kwargs):
    """
    Use theme() to modify individual components of a theme,
    allowing you to control the appearance of all non-data components of the plot.

    Parameters
    ----------

    axis_title : result of element_text() or [element_blank() | 'blank'] to draw nothing and assign no space.
        label of axes

    axis_title_x : result of element_text() or [element_blank() | 'blank'] to draw nothing and assign no space.
        x axis label

    axis_title_y : result of element_text() or [element_blank() | 'blank'] to draw nothing and assign no space.
        y axis label

    axis_text : result of element_text() or [element_blank() | 'blank'] to draw nothing and assign no space.
        tick labels along axes

    axis_text_x : result of element_text() or [element_blank() | 'blank'] to draw nothing and assign no space.
        x axis tick labels

    axis_text_y : result of element_text() or [element_blank() | 'blank'] to draw nothing and assign no space.
        y axis tick labels

    axis_ticks : result of element_line() or [element_blank() | 'blank'] to draw nothing and assign no space.
        tick marks along axes

    axis_ticks_x : result of element_line() or [element_blank() | 'blank'] to draw nothing and assign no space.
        x axis tick marks

    axis_ticks_y : result of element_line() or [element_blank() | 'blank'] to draw nothing and assign no space.
        y axis tick marks

    axis_line : result of element_line() or [element_blank() | 'blank'] to draw nothing and assign no space.
        lines along axes

    axis_line_x : result of element_line() or [element_blank() | 'blank'] to draw nothing and assign no space.
        line along x axis

    axis_line_y : result of element_line() or [element_blank() | 'blank'] to draw nothing and assign no space.
        line along y axis

    legend_position : ['none' | 'left' | 'right' | 'bottom' | 'top'] or two-element numeric vector.
        the position of legends

    axis_tooltip : result of element_text() or [element_blank() | 'blank'] to draw nothing and assign no space.
        axes tooltips

    axis_tooltip_x : result of element_text() or [element_blank() | 'blank'] to draw nothing and assign no space.
        x axis tooltips

     axis_tooltip_y : result of element_text() or [element_blank() | 'blank'] to draw nothing and assign no space.
        y axis tooltips

    Returns
    -------
        theme specification


     Examples
    ---------
    >>> import pandas as pd
    >>> from sklearn.datasets import make_blobs
    >>> X,y = make_blobs(n_samples=1000)
    >>> dat = {'x': X.T[0], 'y': X.T[1], 'variable': y}
    >>> dat = pd.DataFrame(dat)
    >>> colors = {0:'red', 1: 'blue', 2: 'green'}
    >>> dat['color'] = [colors[variable] for variable in dat['variable']]
    >>> ggplot(dat, aes(x='x', y='y')) \
    >>>     + geom_point(aes(color='y'))\
    >>>     + scale_color_gradient(guide=guide_colorbar(nbin=10,barheight= 8, barwidth=300))\
    >>>     + theme(legend_position='top')
    """
    return FeatureSpec('theme', name=None,
                       axis_title=axis_title,
                       axis_title_x=axis_title_x,
                       axis_title_y=axis_title_y,
                       axis_text=axis_text,
                       axis_text_x=axis_text_x,
                       axis_text_y=axis_text_y,
                       axis_ticks=axis_ticks,
                       axis_ticks_x=axis_ticks_x,
                       axis_ticks_y=axis_ticks_y,
                       axis_line=axis_line,
                       axis_line_x=axis_line_x,
                       axis_line_y=axis_line_y,
                       legend_position=legend_position,
                       axis_tooltip=axis_tooltip,
                       axis_tooltip_x=axis_tooltip_x,
                       axis_tooltip_y=axis_tooltip_y,
                       **kwargs)


def element_blank():
    return FeatureSpec('theme_element', name='blank')
