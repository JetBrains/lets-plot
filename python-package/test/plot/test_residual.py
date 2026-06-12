#
#  Copyright (c) 2024. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import pytest

from lets_plot._type_utils import LazyModule
from lets_plot.bistro import residual as bistro

pd = LazyModule('pandas')
pl = LazyModule('polars')
statsmodels = LazyModule('statsmodels')

DATA = {'x': [0, 0, 1], 'y': [0, 1, 1]}


def _assert_get_stat_data_does_not_fail(data):
    try:
        bistro._get_stat_data(
            data=data,
            x='x',
            y='y',
            group_by=None,
            method=bistro._METHOD_DEF,
            deg=bistro._METHOD_LM_DEG_DEF,
            span=bistro._METHOD_LOESS_SPAN_DEF,
            seed=None,
            max_n=None
        )
    except Exception as e:
        pytest.fail("_get_stat_data({0}, ...) raised an exception:\n{1}".format(type(data).__name__, e))


@pytest.mark.skipif(not statsmodels, reason='requires statsmodels')
def test_dict__get_stat_data():
    _assert_get_stat_data_does_not_fail(dict(DATA))


@pytest.mark.skipif(not statsmodels or not pd, reason='requires statsmodels and pandas')
def test_pandas_dataframe__get_stat_data():
    _assert_get_stat_data_does_not_fail(pd.DataFrame(DATA))


@pytest.mark.skipif(not statsmodels or not pl, reason='requires statsmodels and polars')
def test_polars_dataframe__get_stat_data():
    _assert_get_stat_data_does_not_fail(pl.DataFrame(DATA))
