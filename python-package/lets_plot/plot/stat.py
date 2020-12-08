#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
__all__ = ['stat_corr']

from .coord import coord_cartesian, coord_fixed
from .core import FeatureSpec
from .geom import _geom
from .scale import scale_x_discrete, scale_y_discrete
from .scale_identity import scale_size_identity


def stat_corr(mapping=None, data=None, geom=None, position=None, show_legend=None, sampling=None,
              tooltips=None,
              type=None,
              diag=None,
              **other_args):
    """
    Computes correlations between numeric variables in the 'data'
    and draws a correlation matrix.
    By default uses the 'tile' geometry.

    The correlation statistic computes the following variables that can be used in
    the aesthetic mapping:
    - '..x..' : X coordinates
    - '..y..' : Y coordinates
    - '..corr..' : correlation (in range -1..1)
    - '..corr_abs..' : absolute value of correlation (in range 0..1)

    Parameters
    ----------
    mapping : set of aesthetic mappings created by aes() function.
        Aesthetic mappings describe the way that variables in the data are
        mapped to plot "aesthetics".
    data : dictionary or pandas DataFrame, optional
        The data to be displayed in this layer. If None, the default, the data
        is inherited from the plot data as specified in the call to ggplot.
    geom : string, optional
        The name of 'geometry' used to draw correlation matrix.
        For example: 'tile' or 'point' or 'text'.
    position : string, optional
        Position adjustment, either as a string ("identity", "stack", "dodge",...), or the result of a call to a
        position adjustment function.
    show_legend: bool
        True - do not show legend for this layer.
    sampling : result of the call to the sampling_xxx() function.
        Value 'none' will disable sampling for this layer.
    tooltips : result of the call to the layer_tooltips() function.
        Specifies appearance, style and content.
    type : string
        Type of matrix. Possible values - "upper", "lower", "full".
        Default - "full".
    diag : Boolean
        Determines whether to fill the main diagonal with values.
        Default - "True".

    Returns
    -------
        geom object specification
    """

    geom = geom if geom else "tile"
    other_args['label_format'] = other_args.get('label_format', '.2f')

    scale_xy_expand = None
    if geom == 'tile':
        scale_xy_expand = [0, 0.1]  # Smaller 'additive' expand for tiles (normally: 0.6)

    coord = coord_cartesian()
    if geom in ['point', 'text']:
        other_args['size_unit'] = other_args.get('size_unit', 'x')
        fixed_size = True
        if mapping is not None and isinstance(mapping, FeatureSpec):
            if 'size' in mapping.props():
                fixed_size = False

        if fixed_size:
            other_args['size'] = other_args.get('size', .75)

        if geom == 'point' and not fixed_size:
            coord = coord_fixed()

    return (_geom(geom,
                  mapping=mapping,
                  data=data,
                  stat='corr',
                  position=position,
                  show_legend=show_legend,
                  sampling=sampling,
                  tooltips=tooltips,
                  na_value='',
                  type=type,
                  diag=diag,
                  **other_args) +
            scale_size_identity() +
            coord +
            scale_x_discrete(expand=scale_xy_expand) +
            scale_y_discrete(expand=scale_xy_expand))
