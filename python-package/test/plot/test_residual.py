#
#  Copyright (c) 2024. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from lets_plot.bistro.residual import _METHOD_DEF, _METHOD_LM_DEG_DEF, _METHOD_LOESS_SPAN_DEF
from lets_plot.bistro.residual import _get_stat_data

def test_dataframes__get_stat_data():
    import pandas as pd
    import polars as pl

    data = {'x': [0, 0, 1], 'y': [0, 1, 1]}

    for transform in [dict, pd.DataFrame, pl.DataFrame]:
        try:
            _get_stat_data(transform(data), 'x', 'y', group_by=None, method=_METHOD_DEF, deg=_METHOD_LM_DEG_DEF, span=_METHOD_LOESS_SPAN_DEF, seed=None, max_n=None)
        except Exception as e:
            assert False, "_get_stat_data({0}(data), ...) raised an exception:\n{1}".format(transform.__name__, e)