import pytest

from lets_plot._type_utils import LazyModule

pd = LazyModule('pandas')
pl = LazyModule('polars')

from lets_plot import ggplot, aes, geom_function


def test_geom_function_in_plot():
    p = ggplot() + geom_function(fun=lambda x: x ** 2, xlim=[-2, 2], n=5)

    assert p.as_dict()['layers'][0]['data']['x'] == [-2.0, -1.0, 0.0, 1.0, 2.0]
    assert p.as_dict()['layers'][0]['data']['y'] == [4.0, 1.0, 0.0, 1.0, 4.0]

@pytest.mark.skipif(not pd, reason='requires pandas')
def test_geom_function_with_pandas():
    df = pd.DataFrame({'x': [-2, -1, 0, 1, 2]})
    p = ggplot() + geom_function(aes(x='x'), data=df, fun=lambda x: x ** 2, xlim=[-2, 2], n=5)

    assert p.as_dict()['layers'][0]['data'].x.tolist() == [-2.0, -1.0, 0.0, 1.0, 2.0]
    assert p.as_dict()['layers'][0]['data'].y.tolist() == [4.0, 1.0, 0.0, 1.0, 4.0]


@pytest.mark.skipif(not pl, reason='requires polars')
def test_geom_function_with_polars():
    df = pl.DataFrame({'x': [-2, -1, 0, 1, 2]})
    p = ggplot() + geom_function(aes(x='x'), data=df, fun=lambda x: x ** 2, xlim=[-2, 2], n=5)

    assert p.as_dict()['layers'][0]['data']['x'].to_list() == [-2.0, -1.0, 0.0, 1.0, 2.0]
    assert p.as_dict()['layers'][0]['data']['y'].to_list() == [4.0, 1.0, 0.0, 1.0, 4.0]
