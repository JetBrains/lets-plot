from lets_plot import ggplot, aes, geom_function


def test_geom_function_in_plot():
    # A simple parabola
    p = ggplot() + geom_function(fun=lambda x: x ** 2, xlim=[-2, 2], n=5)

    assert p.as_dict()['layers'][0]['data']['x'] == [-2.0, -1.0, 0.0, 1.0, 2.0]
    assert p.as_dict()['layers'][0]['data']['y'] == [4.0, 1.0, 0.0, 1.0, 4.0]

def test_geom_function_with_pandas():
    import pandas as pd

    # A simple parabola with pandas DataFrame
    df = pd.DataFrame({'x': [-2, -1, 0, 1, 2]})
    p = ggplot() + geom_function(aes(x='x'), data=df, fun=lambda x: x ** 2, xlim=[-2, 2], n=5)

    assert p.as_dict()['layers'][0]['data'].x.tolist() == [-2.0, -1.0, 0.0, 1.0, 2.0]
    assert p.as_dict()['layers'][0]['data'].y.tolist() == [4.0, 1.0, 0.0, 1.0, 4.0]


def test_geom_function_with_polars():
    import polars as pl

    # A simple parabola with polars DataFrame
    df = pl.DataFrame({'x': [-2, -1, 0, 1, 2]})
    p = ggplot() + geom_function(aes(x='x'), data=df, fun=lambda x: x ** 2, xlim=[-2, 2], n=5)

    assert p.as_dict()['layers'][0]['data']['x'].to_list() == [-2.0, -1.0, 0.0, 1.0, 2.0]
    assert p.as_dict()['layers'][0]['data']['y'].to_list() == [4.0, 1.0, 0.0, 1.0, 4.0]
