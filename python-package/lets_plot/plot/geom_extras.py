#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

__all__ = ['arrow']

#
# See R doc: https://www.rdocumentation.org/packages/grid/versions/3.4.1/topics/arrow
#
def arrow(angle=None, length=None, ends=None, type=None):
    """
    Describe arrows to add to a line.

    Parameters
    ----------
    angle : float
        The angle of the arrow head in degrees (smaller numbers produce narrower, pointer arrows). 
        Essentially describes the width of the arrow head.
    length : int
        The length of the arrow head (px).
    ends : {'last', 'first', 'both'}
        Indicating which ends of the line to draw arrow heads.
    type : {'open', 'closed'}
        Indicating whether the arrow head should be a closed triangle.

    Returns
    -------
    ``FeatureSpec``
        Arrow object specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 5, 7, 9, 11
        
        from lets_plot import *
        LetsPlot.setup_html()
        ggplot() + \\
            geom_segment(x=2, y=10, xend=4, yend=9, \\
                        arrow=arrow(type='closed')) + \\
            geom_segment(x=3, y=6, xend=3, yend=9, \\
                        arrow=arrow(type='open')) + \\
            geom_segment(x=4, y=7, xend=5, yend=10, \\
                        arrow=arrow(type='closed', ends='both', length=23)) + \\
            geom_segment(x=5, y=8, xend=7, yend=7, \\
                        arrow=arrow(type='open', ends='first', angle=120, length=23))

    """

    return FeatureSpec('arrow', 'arrow', **locals())
