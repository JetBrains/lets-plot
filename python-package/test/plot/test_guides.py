#
#  Copyright (c) 2024. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import lets_plot as gg
from lets_plot.plot.guide import guide_legend, guide_colorbar
from lets_plot.plot.core import FeatureSpec
from lets_plot.plot.core import FeatureSpecArray


def test_two_guides():
    spec = (gg.ggplot() + gg.guides(color=guide_legend(nrow=1)) + gg.guides(color=guide_legend(title="Title")))

    as_dict = spec.as_dict()['guides']['color']
    assert as_dict['nrow'] == 1
    assert as_dict['title'] == "Title"


def test_shape_and_color_guides():
    spec = (gg.ggplot() + gg.guides(shape=guide_legend(ncol=2, title="Shape title"))
            + gg.guides(color=guide_colorbar(nbin=8, title="Color title")))

    as_dict = spec.as_dict()['guides']
    assert as_dict['shape']['ncol'] == 2
    assert as_dict['shape']['title'] == "Shape title"
    assert as_dict['color']['nbin'] == 8
    assert as_dict['color']['title'] == "Color title"


# test labs()

def test_labs_empty():
    spec = gg.labs()
    assert spec.as_dict()['feature-list'] == []


def test_plot_title():
    spec = gg.labs(title='New plot title')
    assert isinstance(spec, FeatureSpec)
    assert spec.kind == 'ggtitle'
    assert spec.as_dict()['text'] == 'New plot title'


def test_aes_label():
    spec = gg.labs(x='New X label')
    assert isinstance(spec, FeatureSpec)
    assert spec.kind == 'guides'
    x_dict = spec.as_dict()['x']
    assert x_dict['title'] == 'New X label'
    assert 'name' not in x_dict


def test_plot_title_and_aes_label():
    spec_list = gg.labs(title='New plot title', x='New X label')
    assert isinstance(spec_list, FeatureSpecArray)
    for spec in spec_list.elements():
        assert isinstance(spec, FeatureSpec)
        assert spec.kind in ('guides', 'ggtitle')
        if spec.kind == 'ggtitle':
            assert spec.as_dict()['text'] == 'New plot title'
        else:
            x_dict = spec.as_dict()['x']
            assert x_dict['title'] == 'New X label'
            assert 'name' not in x_dict


def test_guides_and_labs():
    spec = gg.ggplot() + gg.guides(color=guide_legend(nrow=1)) + gg.labs(color='Title')
    as_dict = spec.as_dict()['guides']['color']
    assert as_dict['name'] == 'legend'
    assert as_dict['nrow'] == 1
    assert as_dict['title'] == "Title"


def test_override_aes():
    spec = (gg.ggplot() + gg.guides(color=guide_legend(override_aes=dict(color=['red'], size=10))))

    as_dict = spec.as_dict()['guides']['color']['override_aes']
    assert as_dict['color'][0] == 'red'
    assert as_dict['size'] == 10

