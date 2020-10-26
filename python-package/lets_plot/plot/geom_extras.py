#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

__all__ = ['arrow', 'lon_lat']


#
# See R doc: https://www.rdocumentation.org/packages/grid/versions/3.4.1/topics/arrow
#
def arrow(angle=None, length=None, ends=None, type=None):
    """
    Describe arrows to add to a line.

    Parameters
    ----------
    angle : numeric
        The angle of the arrow head in degrees (smaller numbers produce narrower, pointier arrows). 
        Essentially describes the width of the arrow head.
    length : numeric
        The length of the arrow head (px).
    ends : ['last' \ 'first' | 'both']
        Indicating which ends of the line to draw arrow heads.
    type : ['open' | 'closed']
        Indicating whether the arrow head should be a closed triangle.
        
    Returns
    -------
        arrow object specification
        
    Examples
    ---------
    >>> from lets_plot import *
    >>> ggplot() + geom_segment(aes(x=[3], y=[6], xend=[4], yend=[10]), arrow=arrow(type='closed'))
    """
    return FeatureSpec('arrow', 'arrow', **locals())


def lon_lat(lon, lat):
    return FeatureSpec('deferred_procedure', 'lon_lat', lon=lon, lat=lat)
