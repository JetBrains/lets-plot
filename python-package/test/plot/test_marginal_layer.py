#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import pytest

import lets_plot as gg


# Layers with 'x-statistic', left margin
def expected_x_stat_l(geom):
    return dict(
        geom=geom,
        margin_side='l',
        marginal=True,
        orientation='y'
    )


# Layers with 'x-statistic', top margin
def expected_x_stat_t(geom):
    return dict(
        geom=geom,
        margin_side='t',
        marginal=True,
    )


# Layers with 'y-statistic', left margin
def expected_y_stat_l(geom):
    return dict(
        geom=geom,
        margin_side='l',
        marginal=True,
        x=0
    )


# Layers with 'y-statistic', top margin
def expected_y_stat_t(geom):
    return dict(
        geom=geom,
        margin_side='t',
        marginal=True,
        orientation='y',
        y=0
    )


def _to_feature_list(*spec_dicts):
    feature_list = []
    for spec_dict in spec_dicts:
        feature_list.append(
            dict(layer=spec_dict)
        )
    return {
        'feature-list': feature_list
    }


def _clean_input(input):
    def _clean_layer(layer):
        # Remove some keys
        layer.pop('stat', None)
        layer.pop('data_meta', None)
        layer.pop('mapping', None)
        layer.pop('show_legend', None)

    layers = input.get('feature-list', None)
    if layers:
        for l in layers:
            _clean_layer(l['layer'])
    else:
        _clean_layer(input)


@pytest.mark.parametrize('marginal_layer_spec, expected', [
    (
        gg.ggmarginal("l", layer=gg.geom_histogram()),
        expected_x_stat_l("histogram")
    ),
    (
        gg.ggmarginal("t", layer=gg.geom_histogram()),
        expected_x_stat_t("histogram")
    ),
    (
        gg.ggmarginal("tl", layer=gg.geom_point(stat='bin')),
        _to_feature_list(expected_x_stat_t("point"), expected_x_stat_l("point"))
    ),
    (
        gg.ggmarginal("tl", layer=gg.geom_histogram()),
        _to_feature_list(expected_x_stat_t("histogram"), expected_x_stat_l("histogram"))
    ),
    (
        gg.ggmarginal("tl", layer=gg.geom_density()),
        _to_feature_list(expected_x_stat_t("density"), expected_x_stat_l("density"))
    ),
    (
        gg.ggmarginal("tl", layer=gg.geom_freqpoly()),
        _to_feature_list(expected_x_stat_t("freqpoly"), expected_x_stat_l("freqpoly"))
    ),

    # y-stat
    (
        gg.ggmarginal("tl", layer=gg.geom_point(stat="ydensity")),
        _to_feature_list(expected_y_stat_t("point"), expected_y_stat_l("point"))
    ),
    (
        gg.ggmarginal("tl", layer=gg.geom_boxplot()),
        _to_feature_list(
            expected_y_stat_t("boxplot"), expected_y_stat_l("boxplot"),
            expected_y_stat_t("point"), expected_y_stat_l("point")
        )
    ),
    (
        gg.ggmarginal("tl", layer=gg.geom_violin()),
        _to_feature_list(expected_y_stat_t("violin"), expected_y_stat_l("violin"))
    ),

    # Don't change 'orientation' if specified.
    (
        gg.ggmarginal("l", layer=gg.geom_histogram(orientation='x')),
        {**expected_x_stat_l("histogram"), 'orientation': 'x'}
    )
])
def test_marginal_layer(marginal_layer_spec, expected):
    input = marginal_layer_spec.as_dict()
    _clean_input(input)

    assert input == expected


@pytest.mark.parametrize('marginal_layer_spec, expected_mapping', [
    (gg.ggmarginal("r", layer=gg.geom_histogram()), {'x': '..density..'}),
    (gg.ggmarginal("r", layer=gg.geom_point(stat='bin')), {'x': '..density..'}),
    (gg.ggmarginal("b", layer=gg.geom_histogram()), {'y': '..density..'}),
    (gg.ggmarginal("b", layer=gg.geom_point(stat='bin')), {'y': '..density..'}),

    # preserve other mappings if specified.
    (gg.ggmarginal("r", layer=gg.geom_histogram(gg.aes(color='c'))), {'x': '..density..', 'color': 'c'}),
    # don't override mapping if specified.
    (gg.ggmarginal("r", layer=gg.geom_histogram(gg.aes(x='abc', color='c'))), {'x': 'abc', 'color': 'c'}),
])
def test_histogram_mapping(marginal_layer_spec, expected_mapping):
    input = marginal_layer_spec.as_dict()
    layer_mapping = input.get('mapping', {})

    assert layer_mapping == expected_mapping
