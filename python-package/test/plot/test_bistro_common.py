#
#  Copyright (c) 2023. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import pytest

from lets_plot.plot.core import DummySpec, FeatureSpecArray
from lets_plot.bistro._plot2d_common import _get_bin_params_2d, _get_geom2d_layer, _get_marginal_layers


def test_empty_data__get_bin_params_2d():
    binwidth2d, bins2d = _get_bin_params_2d([], [], None, None)
    assert binwidth2d is None
    assert bins2d is None

    binwidth2d, bins2d = _get_bin_params_2d([], [], 1.0, None)
    assert [1.0, 1.0] == binwidth2d
    assert bins2d is None

    binwidth2d, bins2d = _get_bin_params_2d([], [], [2.0, 3.0], None)
    assert [2.0, 3.0] == binwidth2d
    assert bins2d is None

    binwidth2d, bins2d = _get_bin_params_2d([], [], None, 1)
    assert binwidth2d is None
    assert [1, 1] == bins2d

    binwidth2d, bins2d = _get_bin_params_2d([], [], None, [2, 3])
    assert binwidth2d is None
    assert [2, 3] == bins2d

    binwidth2d, bins2d = _get_bin_params_2d([], [], [2.0, 3.0], [2, 3])
    assert [2.0, 3.0] == binwidth2d
    assert [2, 3] == bins2d


def test_1d_to_2d__get_bin_params_2d():
    xs, ys = [0, 1], [2, 3]

    binwidth2d, bins2d = _get_bin_params_2d(xs, ys, 1.0, None)
    assert [1.0, 1.0] == binwidth2d
    assert bins2d is None

    binwidth2d, bins2d = _get_bin_params_2d(xs, ys, None, 1)
    assert binwidth2d is None
    assert [1, 1] == bins2d


def test_leave_specified_as_is__get_bin_params_2d():
    xs, ys = [0, 1], [2, 3]

    binwidth2d, bins2d = _get_bin_params_2d(xs, ys, [2.0, 3.0], None)
    assert [2.0, 3.0] == binwidth2d
    assert bins2d is None

    binwidth2d, bins2d = _get_bin_params_2d(xs, ys, None, [2, 3])
    assert binwidth2d is None
    assert [2, 3] == bins2d


def test_calculation_is_correct__get_bin_params_2d():
    binwidth2d, bins2d = _get_bin_params_2d([0, -10, 10, 50, -5], [0, 1, 0, -1, -2], None, None)
    assert [2.0, 2.0] == binwidth2d
    assert bins2d is None

    binwidth2d, bins2d = _get_bin_params_2d([100, 100, 99, 100, 100], [0, 1, 0, -1, -2], None, None)
    assert [0.1, 0.1] == binwidth2d
    assert bins2d is None


def _get_geom2d_layer_props(geom_kind=None, binwidth2d=None, bins2d=None, color=None, color_by=None,
                            size=None, alpha=None, show_legend=None):
    return {'geom_kind': geom_kind, 'binwidth2d': binwidth2d, 'bins2d': bins2d, 'color': color,
            'color_by': color_by, 'size': size, 'alpha': alpha, 'show_legend': show_legend}


def test_geom_kind__get_geom2d_layer():
    kind_to_geom_map = {'point': 'point', 'tile': 'bin2d', 'density2d': 'density2d', 'density2df': 'density2df'}
    for geom_kind, geom_name in kind_to_geom_map.items():
        spec_dict = _get_geom2d_layer(**_get_geom2d_layer_props(geom_kind=geom_kind)).as_dict()
        assert geom_name == spec_dict['geom']

    assert _get_geom2d_layer(**_get_geom2d_layer_props(geom_kind='none')) is None

    with pytest.raises(Exception):
        _get_geom2d_layer(**_get_geom2d_layer_props(geom_kind='density'))


def test_tile_params__get_geom2d_layer():
    def get_spec_dict(binwidth2d=None, bins2d=None):
        return _get_geom2d_layer(**_get_geom2d_layer_props(
            geom_kind='tile', binwidth2d=binwidth2d, bins2d=bins2d
        )).as_dict()

    spec_dict = get_spec_dict()
    assert 'binwidth' not in spec_dict
    assert 'bins' not in spec_dict

    spec_dict = get_spec_dict(binwidth2d=[2.0, 3.0])
    assert [2.0, 3.0] == spec_dict['binwidth']
    assert 'bins' not in spec_dict

    spec_dict = get_spec_dict(bins2d=[2, 3])
    assert 'binwidth' not in spec_dict
    assert [2, 3] == spec_dict['bins']


def test_color_params__get_geom2d_layer():
    spec_dict = _get_geom2d_layer(**_get_geom2d_layer_props(geom_kind='point')).as_dict()
    assert 'color' not in spec_dict

    spec_dict = _get_geom2d_layer(**_get_geom2d_layer_props(geom_kind='point', color='red')).as_dict()
    assert 'red' == spec_dict['color']

    spec_dict = _get_geom2d_layer(**_get_geom2d_layer_props(geom_kind='point', color_by='red')).as_dict()
    assert 'color' not in spec_dict

    for geom_kind, aes_name, aes_def in [
        ('tile', 'fill', '..count..'),
        ('density2d', 'color', '..group..'),
        ('density2df', 'fill', '..group..'),
    ]:
        spec_dict = _get_geom2d_layer(**_get_geom2d_layer_props(geom_kind=geom_kind)).as_dict()
        assert 'color' not in spec_dict
        assert 'fill' not in spec_dict
        assert {aes_name: aes_def} == spec_dict['mapping']

        spec_dict = _get_geom2d_layer(**_get_geom2d_layer_props(geom_kind=geom_kind, color='red')).as_dict()
        assert 'red' == spec_dict['color']
        assert 'fill' not in spec_dict
        assert {aes_name: aes_def} == spec_dict['mapping']

        spec_dict = _get_geom2d_layer(**_get_geom2d_layer_props(geom_kind=geom_kind, color_by='color')).as_dict()
        assert 'color' not in spec_dict
        assert 'fill' not in spec_dict
        assert {aes_name: 'color'} == spec_dict['mapping']

        spec_dict = _get_geom2d_layer(
            **_get_geom2d_layer_props(geom_kind=geom_kind, color='red', color_by='color')).as_dict()
        assert 'red' == spec_dict['color']
        assert 'fill' not in spec_dict
        assert {aes_name: 'color'} == spec_dict['mapping']


def _get_marginal_layers_props(marginal=None, binwidth2d=None, bins2d=None, color=None, color_by=None,
                               show_legend=None):
    return {'marginal': marginal, 'binwidth2d': binwidth2d, 'bins2d': bins2d,
            'color': color, 'color_by': color_by, 'show_legend': show_legend}


def test_empty_marginal__get_marginal_layers():
    marginals = _get_marginal_layers(**_get_marginal_layers_props(marginal=""))
    # assert 0 == len(marginals)
    assert isinstance(marginals, DummySpec)


def test_marginal_split__get_marginal_layers():
    marginal = " hist:t,dens:tr ,  dens:b:.1,\n\tbox :\n\ttblr  "
    marginals = _get_marginal_layers(**_get_marginal_layers_props(marginal=marginal))
    assert 12 == len(marginals)
    for marginal_layer in marginals:
        spec_dict = marginal_layer.as_dict()
        assert spec_dict['marginal']
        assert spec_dict['margin_side'] in ['t', 'b', 'l', 'r']
        if 'margin_size' in spec_dict:
            assert isinstance(spec_dict['margin_size'], float)


def test_marginal_kind__get_marginal_layers():
    kind_to_geom_map = {'dens': 'area', 'density': 'area',
                        'hist': 'histogram', 'histogram': 'histogram',
                        'box': 'boxplot', 'boxplot': 'boxplot'}
    for geom_kind, geom_name in kind_to_geom_map.items():
        marginal_layers = _get_marginal_layers(**_get_marginal_layers_props(marginal="{0}:r".format(geom_kind)))
        if isinstance(marginal_layers, FeatureSpecArray):
            spec_dict = marginal_layers[0].as_dict()
        else:
            spec_dict = marginal_layers.as_dict()

        assert geom_name == spec_dict['geom']

    with pytest.raises(Exception):
        _get_marginal_layers(**_get_marginal_layers_props(marginal='point'))


def test_marginal_hist_params__get_marginal_layers():
    def get_spec_dicts(binwidth2d=None, bins2d=None):
        return [
            marginal_layer.as_dict()
            for marginal_layer in _get_marginal_layers(
                **_get_marginal_layers_props(marginal="hist:tblr", binwidth2d=binwidth2d, bins2d=bins2d)
            )
        ]

    spec_dicts = get_spec_dicts()
    for spec_dict in spec_dicts:
        assert 'binwidth' not in spec_dict
        assert 'bins' not in spec_dict

    spec_dicts = get_spec_dicts(binwidth2d=[2.0, 3.0])
    assert 2.0 == spec_dicts[0]['binwidth']
    assert 'bins' not in spec_dicts[0]
    assert 2.0 == spec_dicts[1]['binwidth']
    assert 'bins' not in spec_dicts[1]
    assert 3.0 == spec_dicts[2]['binwidth']
    assert 'bins' not in spec_dicts[2]
    assert 3.0 == spec_dicts[3]['binwidth']
    assert 'bins' not in spec_dicts[3]

    spec_dicts = get_spec_dicts(bins2d=[2, 3])
    assert 'binwidth' not in spec_dicts[0]
    assert 2 == spec_dicts[0]['bins']
    assert 'binwidth' not in spec_dicts[1]
    assert 2 == spec_dicts[1]['bins']
    assert 'binwidth' not in spec_dicts[2]
    assert 3 == spec_dicts[2]['bins']
    assert 'binwidth' not in spec_dicts[3]
    assert 3 == spec_dicts[3]['bins']


def test_marginal_color_params__get_marginal_layers():
    _COLOR_DEF = "pen"

    def get_spec_dict(geom_kind, color=None, color_by=None):
        marginal_layers = _get_marginal_layers(**_get_marginal_layers_props(
            marginal="{0}:r".format(geom_kind),
            color=color, color_by=color_by))

        if isinstance(marginal_layers, FeatureSpecArray):
            spec_dict = marginal_layers[0].as_dict()
        else:
            spec_dict = marginal_layers.as_dict()
        
        return spec_dict

    for geom_kind in ['dens', 'hist', 'box']:
        spec_dict = get_spec_dict(geom_kind)
        assert _COLOR_DEF == spec_dict['color']
        assert _COLOR_DEF == spec_dict['fill']

        spec_dict = get_spec_dict(geom_kind, color='red')
        assert 'red' == spec_dict['color']
        assert 'red' == spec_dict['fill']

        spec_dict = get_spec_dict(geom_kind, color_by='color')
        assert 'color' not in spec_dict
        assert 'fill' not in spec_dict

        spec_dict = get_spec_dict(geom_kind, color='red', color_by='color')
        assert 'color' not in spec_dict
        assert 'fill' not in spec_dict
