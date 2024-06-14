#  Copyright (c) 2024. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import lets_plot as gg
from lets_plot import element_blank
from lets_plot import element_rect
from lets_plot.plot.geom import _geom
from pandas import DataFrame, Categorical
from lets_plot.plot.util import as_annotated_data


def get_data_meta(data, mapping=None):
    _, _, data_meta = as_annotated_data(data, mapping)
    return data_meta['data_meta']


def test_ggsave():
    import pandas as pd
    import numpy as np

    df = pd.DataFrame({
        'X': np.random.randint(0, 100, 10),
        'Y': np.random.randint(0, 100, 10)
    })

    p = gg.ggplot(df) + gg.geom_point(gg.aes(x="X", y="Y")) + \
        gg.geom_text(gg.aes(x='X', y='Y', group='X', label='X'))
    #p.show()
    gg.ggsave(p, "1.svg")


def test_data_meta():
    x = ['ch1', 'ch2', 'ch3', 'ch4']
    df = DataFrame({'v': x})
    data_meta = get_data_meta(df)
    expected_factor_levels = [
        {'column': 'v', 'type_kind': "O", 'type_name': "object"}
    ]
    assert expected_factor_levels == data_meta['types_annotations']


def test_data_meta_integer():
    x = [1, 2, 3, 4]
    df = DataFrame({'v': x})
    data_meta = get_data_meta(df)
    expected_factor_levels = [
        {'column': 'v', 'type_kind': "i", 'type_name': "int64"}
    ]
    assert expected_factor_levels == data_meta['types_annotations']


def test_data_meta_integer():
    x = [1.0, 2.0, 3.0, 4.0]
    df = DataFrame({'v': x})
    data_meta = get_data_meta(df)
    expected_factor_levels = [
        {'column': 'v', 'type_kind': "f", 'type_name': "float64"}
    ]
    assert expected_factor_levels == data_meta['types_annotations']
