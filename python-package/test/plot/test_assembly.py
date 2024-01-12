#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import lets_plot as gg
from lets_plot.plot.core import DummySpec, FeatureSpecArray
from lets_plot.plot.core import FeatureSpec
from lets_plot.plot.core import LayerSpec
from lets_plot.plot.geom import _geom
from lets_plot.plot.scale import _scale


def test_plot_geom_geom():
    geom1 = _geom('geom1')
    geom2 = _geom('geom2')
    plot = gg.ggplot()

    expect = {
        'kind': 'plot',
        'mapping': {},
        'data_meta': {},
        'layers': [
            geom1.as_dict(),
            geom2.as_dict()
        ],
        'scales': [],
        'metainfo_list': [],
    }

    assert (plot + geom1 + geom2).as_dict() == expect
    assert (plot + (geom1 + geom2)).as_dict() == expect
    assert ((plot + geom1) + geom2).as_dict() == expect


def test_plot_geom_scale():
    geom = _geom('geom')
    scale = _scale('A')
    plot = gg.ggplot()

    expect = {
        'kind': 'plot',
        'mapping': {},
        'data_meta': {},
        'layers': [geom.as_dict()],
        'scales': [scale.as_dict()],
        'metainfo_list': [],
    }

    assert (plot + geom + scale).as_dict() == expect
    assert (plot + scale + geom).as_dict() == expect
    assert (plot + (scale + geom)).as_dict() == expect


def test_plot_ggtitle():
    plot = gg.ggplot()
    ggtitle = gg.labs(title="New plot title")

    expect = {
        'ggtitle': {'text': 'New plot title'},
        'kind': 'plot',
        'data_meta': {},
        'layers': [],
        'mapping': {},
        'scales': [],
        'metainfo_list': [],
    }

    assert (plot + ggtitle).as_dict() == expect


def test_dummy_with_dummy():
    expect = {
        'dummy-feature': True
    }
    assert (DummySpec() + DummySpec()).as_dict() == expect


def test_feature_with_dummy():
    feature = FeatureSpec("some kind", "some name")
    expect = feature.as_dict()
    assert (feature + DummySpec()).as_dict() == expect


def test_plot_with_dummy():
    plot = gg.ggplot()
    expect = plot.as_dict()
    assert (plot + DummySpec()).as_dict() == expect
    assert (DummySpec() + plot).as_dict() == expect


def test_layer_with_dummy():
    layer = LayerSpec()
    expect = layer.as_dict()
    assert (layer + DummySpec()).as_dict() == expect
    assert (DummySpec() + layer).as_dict() == expect


def test_geom_with_dummy():
    geom = _geom('geom')
    expect = geom.as_dict()
    assert (geom + DummySpec()).as_dict() == expect
    assert (DummySpec() + geom).as_dict() == expect


def test_feature_array_with_dummy():
    array = FeatureSpecArray(FeatureSpec("some kind", "some name"))
    expect = array.as_dict()
    assert (array + DummySpec()).as_dict() == expect
    assert (DummySpec() + array).as_dict() == expect


def test_feature_array_is_always_flat():
    expect = FeatureSpecArray(FeatureSpec("some kind", "some name"))
    nested_array = FeatureSpecArray(expect)
    assert nested_array.as_dict() == expect.as_dict()
