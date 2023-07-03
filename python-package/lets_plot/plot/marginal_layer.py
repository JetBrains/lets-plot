#  Copyright (c) 2022. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from .core import FeatureSpec, LayerSpec, DummySpec, FeatureSpecArray

__all__ = ["ggmarginal"]


def ggmarginal(sides: str, *, size=None, layer: LayerSpec) -> FeatureSpec:
    """
    Convert a given geometry layer to a marginal layer.
    You can add one or more marginal layers to a plot to create a marginal plot.

    Parameters
    ----------
    sides : str
        A string specifying which sides of the plot the marginal layer will appear on.
        It should be set to a string containing any of "trbl", for top, right, bottom, and left.
    size : number or list of numbers, default=0.1
        Size of marginal geometry (width or height, depending on the margin side) as a fraction of the entire
        plotting area of the plot.
        The value should be in range [0.01..0.95].
    layer : `LayerSpec`
        A marginal geometry layer.
        The result of calling of the `geom_xxx()` / `stat_xxx()` function.
        Marginal plot works best with `density`,`histogram`,`boxplot`,`violin` and `freqpoly` geometry layers.

    Returns
    -------
    `FeatureSpec`
        An object specifying a marginal geometry layer or a list of marginal geometry layers.

    Notes
    -----
    A marginal plot is a scatterplot (sometimes a 2D density plot or other bivariate plot) that has histograms,
    boxplots, or other distribution visualization layers in the margins of the x- and y-axes.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 24

        import numpy as np
        from lets_plot import *
        from lets_plot.mapping import as_discrete
        LetsPlot.setup_html()
        LetsPlot.set_theme(theme_light())

        np.random.seed(0)

        cov0=[[1, -.8],
             [-.8, 1]]
        cov1=[[ 10, .1],
               [.1, .1]]

        x0, y0 = np.random.multivariate_normal(mean=[-2,0], cov=cov0, size=200).T
        x1, y1 = np.random.multivariate_normal(mean=[0,1], cov=cov1, size=200).T

        data = dict(
            x = np.concatenate((x0,x1)),
            y = np.concatenate((y0,y1)),
            c = ["A"]*200 + ["B"]*200
        )

        p = ggplot(data, aes("x", "y", color="c", fill="c")) + geom_point()
        p + ggmarginal("tr", layer=geom_density(alpha=0.3, show_legend=False))

    """

    if not isinstance(sides, str):
        raise TypeError("'sides' must be a string.")
    if not 0 < len(sides) <= 4:
        raise ValueError("'sides' must be a string containing 1 to 4 chars: 'l','r','t','b'.")

    # Recursive call when layer is sum of geoms
    if isinstance(layer, FeatureSpecArray):
        result = DummySpec()
        for sublayer in layer.elements():
            result += ggmarginal(sides, size=size, layer=sublayer)
        return result

    if not isinstance(layer, LayerSpec):
        raise TypeError("Invalid 'layer' type: {}".format(type(layer)))

    result = DummySpec()

    for i in range(len(sides)):
        side = sides[i]
        margin_size = _to_size(size, i)
        marginal_layer = _to_marginal(side, margin_size, layer)
        result = result + marginal_layer

    return result


def _to_size(size, side_index: int) -> float:
    if size is None:
        return None

    if isinstance(size, float):
        return size

    if not (isinstance(size, list) or isinstance(size, tuple)):
        raise TypeError("Invalid 'size' type: {}. Expected: float, list or tuple.".format(type(size)))

    try:
        return size[side_index]
    except IndexError:
        return None


def _to_marginal(side: str, size, layer: LayerSpec) -> LayerSpec:
    if side not in ['l', 'r', 't', 'b']:
        raise ValueError("Invalid 'side' value: {}. Valid values: 'l','r','t','b'.".format(side))

    if size is not None:
        if not 0.01 <= size <= 0.95:
            raise ValueError("Invalid 'size' value: {}. Should be in range [0.01..0.95].".format(size))

    layer_copy = LayerSpec.duplicate(layer)
    marginal_options = dict(
        marginal=True,
        margin_side=side,
        margin_size=size
    )

    layer_props = layer_copy.props()
    layer_props.update(marginal_options)

    layer_kind = None
    stat = layer_props.get('stat')
    if stat is not None:
        if stat == 'bin':
            layer_kind = 'histogram'
        elif stat == 'ydensity':
            layer_kind = 'violin'
        elif stat in ('density', 'boxplot', 'boxplot_outlier'):
            layer_kind = stat
    else:
        geom = layer_props.get('geom')
        if geom in ('histogram', 'boxplot', 'violin', 'density', 'freqpoly'):
            layer_kind = geom

    auto_settings = {}

    # choose a proper orientation
    if (side in ('l', 'r') and layer_kind in ('histogram', 'density', 'freqpoly')):
        auto_settings['orientation'] = 'y'

    if layer_kind in ('boxplot', 'boxplot_outlier', 'violin'):
        if side in ('l', 'r'):
            auto_settings['x'] = 0
        elif side in ('t', 'b'):
            auto_settings['y'] = 0
            auto_settings['orientation'] = 'y'

    # Update layer's options with auto-generated and try not to override user-defined options.
    filtered = {k: v for k, v in layer_props.items() if v is not None}
    layer_props.update(
        {**auto_settings, **filtered}
    )

    # For 'histogram' set mapping of x or y to '..density..' for compatibility with 'density' geom.
    if layer_kind == 'histogram':
        if side in ('l', 'r'):
            added_mapping = {'x': '..density..'}
        elif side in ('t', 'b'):
            added_mapping = {'y': '..density..'}

        aes_feature_spec = layer_props.get('mapping')
        mappings = aes_feature_spec.props() if isinstance(aes_feature_spec, FeatureSpec) else {}
        filtered_mappings = {k: v for k, v in mappings.items() if v is not None}
        updated_mappings = {**added_mapping, **filtered_mappings}
        updated_aes_feature_spec = FeatureSpec('mapping', name=None, **updated_mappings)
        layer_props['mapping'] = updated_aes_feature_spec

    return layer_copy
