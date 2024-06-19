#
#  Copyright (c) 2024. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import lets_plot as gg
from lets_plot import geom_point
from lets_plot.plot.guide import guide_legend, guide_colorbar


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


def test_override_aes():
    spec = (gg.ggplot() + gg.guides(color=guide_legend(override_aes=dict(color=['red'], size=10))))

    as_dict = spec.as_dict()['guides']['color']['override_aes']
    assert as_dict['color'][0] == 'red'
    assert as_dict['size'] == 10

