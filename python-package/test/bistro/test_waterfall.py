#  Copyright (c) 2025. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import lets_plot as gg
from lets_plot.bistro import waterfall_plot

data = {
    'category': ['A', 'B', 'C'],
    'value': [100, -50, 30]
}


class TestWaterfallPlot:

    def test_waterfall_without_background_layers(self):
        spec = waterfall_plot(data, x='category', y='value')

        assert [] == spec.as_dict()['bistro']['background_layers']


    def test_waterfall_with_single_background_layer(self):
        spec = waterfall_plot(data, x='category', y='value', background_layers=gg.geom_point())

        assert 'point' == spec.as_dict()['bistro']['background_layers'][0]['geom']


    def test_waterfall_with_multiple_background_layers(self):
        spec = waterfall_plot(data, x='category', y='value',
                              background_layers=gg.geom_point() + gg.geom_line())

        assert len(spec.as_dict()['bistro']['background_layers']) == 2
        assert 'point' == spec.as_dict()['bistro']['background_layers'][0]['geom']
        assert 'line' == spec.as_dict()['bistro']['background_layers'][1]['geom']

    def test_waterfall_background_layers_with_regular_list_should_fail(self):
        try:
            waterfall_plot(data, x='category', y='value', background_layers=[gg.geom_point()])
        except TypeError as e:
            assert str(e) == "Invalid 'layer' type: <class 'list'>"
        else:
            assert False, "Expected TypeError was not raised"
