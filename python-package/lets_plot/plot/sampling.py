#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec

__all__ = ['sampling_random',
           'sampling_random_stratified',
           'sampling_pick',
           'sampling_systematic',
           'sampling_group_random',
           'sampling_group_systematic',
           'sampling_vertex_vw',
           'sampling_vertex_dp',
           'sampling_polygon_dp',
           'sampling_polygon_vw',
           'sampling_path_dp',
           'sampling_path_vw'
           ]


def sampling_random(n, seed=None):
    """
    Return a subset of randomly selected items.

    Parameters
    ----------
    n : int
        Number of items to return.
    seed : int
        Number used to initialize a pseudo random number generator.

    Returns
    -------
    `FeatureSpec`
        Random sample specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(27)
        mean = np.zeros(2)
        cov = [[.9, -.6],
               [-.6, .9]]
        x, y = np.random.multivariate_normal(mean, cov, 10000).T
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            geom_point(sampling=sampling_random(1000, 35))

    """
    return _sampling('random', n=n, seed=seed)


def sampling_random_stratified(n, seed=None, min_subsample=None):
    """
    Randomly sample from each stratum (subgroup).

    Parameters
    ----------
    n : int
        Number of items to return.
    seed : int
        Number used to initialize a pseudo random number generator.
    min_subsample : int
        Minimal number of items in sub sample.

    Returns
    -------
    `FeatureSpec`
        Stratified random sample specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 10

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(27)
        n = 1000
        x = np.random.normal(0, 1, n)
        y = np.random.normal(0, 1, n)
        cond = np.random.choice(['a', 'b'], n, p=[.9, .1])
        ggplot({'x': x, 'y': y, 'cond': cond}, aes('x', 'y', color='cond')) + \\
            geom_point(sampling=sampling_random_stratified(50, 35, min_subsample=10))

    """
    return _sampling('random_stratified', n=n, seed=seed, min_subsample=min_subsample)


def sampling_pick(n):
    """
    'Pick' sampling.

    Parameters
    ----------
    n : int
        Number of items to return.

    Returns
    -------
    `FeatureSpec`
        Sample specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 7

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        x = np.linspace(-2, 2, 30)
        y = x ** 2
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            geom_line(sampling=sampling_pick(20))

    """
    return _sampling('pick', n=n)


def sampling_systematic(n):
    """
    Return a subset where items are selected at a regular interval.

    Parameters
    ----------
    n : int
        Number of items to return.

    Returns
    -------
    `FeatureSpec`
        Systematic sample specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 9

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        n = 1000
        x = np.arange(n)
        np.random.seed(12)
        y = np.random.normal(0, 1, n)
        ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + \\
            geom_line(sampling=sampling_systematic(50))

    """

    return _sampling('systematic', n=n)


def sampling_group_systematic(n):
    """
    Return a subset where groups are selected at a regular interval.

    Parameters
    ----------
    n : int
        Number of groups to return.

    Returns
    -------
    `FeatureSpec`
        Group systematic sample specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        waves_count = 100
        peak_amplitude = np.linspace(1, 2, waves_count)
        wave_x = np.linspace(-np.pi, np.pi, 30)
        x = np.tile(wave_x, waves_count)
        y = np.array([a * np.sin(wave_x) for a in peak_amplitude]).flatten()
        a = np.repeat(peak_amplitude, wave_x.size)
        ggplot({'x': x, 'y': y, 'a': a}, aes('x', 'y')) + \\
            geom_line(aes(group='a', color='a'), sampling=sampling_group_systematic(10))

    """

    return _sampling('group_systematic', n=n)


def sampling_group_random(n, seed=None):
    """
    Return a subset of randomly selected groups.

    Parameters
    ----------
    n : int
        Number of groups to return.
    seed : int
        Number used to initialize a pseudo random number generator.

    Returns
    -------
    `FeatureSpec`
        Group sample specification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 11

        import numpy as np
        from lets_plot import *
        LetsPlot.setup_html()
        waves_count = 100
        peak_amplitude = np.linspace(1, 2, waves_count)
        wave_x = np.linspace(-np.pi, np.pi, 30)
        x = np.tile(wave_x, waves_count)
        y = np.array([a * np.sin(wave_x) for a in peak_amplitude]).flatten()
        a = np.repeat(peak_amplitude, wave_x.size)
        ggplot({'x': x, 'y': y, 'a': a}, aes('x', 'y')) + \\
            geom_line(aes(group='a', color='a'), sampling=sampling_group_random(10, 35))

    """
    return _sampling('group_random', n=n, seed=seed)


def sampling_vertex_vw(n):
    """
    Simplify a polyline using the Visvalingam-Whyatt algorithm.

    Parameters
    ----------
    n : int
        Number of items to return.

    Returns
    -------
    `FeatureSpec`
        Vertices sample specification.

    Notes
    -----
    Vertex sampling is designed for polygon simplification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 17

        import numpy as np
        from scipy.stats import multivariate_normal
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 300
        x = np.linspace(-1, 1, n)
        y = np.linspace(-1, 1, n)
        X, Y = np.meshgrid(x, y)
        mean = np.zeros(2)
        cov = [[1, .5],
               [.5, 1]]
        rv = multivariate_normal(mean, cov)
        Z = rv.pdf(np.dstack((X, Y)))
        data = {'x': X.flatten(), 'y': Y.flatten(), 'z': Z.flatten()}
        ggplot(data, aes(x='x', y='y', z='z')) + \\
            geom_contour(sampling=sampling_vertex_vw(150))

    """
    return _sampling('vertex_vw', n=n)


def sampling_vertex_dp(n):
    """
    Simplify a polyline using the Douglas-Peucker algorithm.

    Parameters
    ----------
    n : int
        Number of items to return.

    Returns
    -------
    `FeatureSpec`
        Vertices sample specification.

    Notes
    -----
    Vertex sampling is designed for polygon simplification.

    Examples
    --------
    .. jupyter-execute::
        :linenos:
        :emphasize-lines: 17

        import numpy as np
        from scipy.stats import multivariate_normal
        from lets_plot import *
        LetsPlot.setup_html()
        np.random.seed(42)
        n = 300
        x = np.linspace(-1, 1, n)
        y = np.linspace(-1, 1, n)
        X, Y = np.meshgrid(x, y)
        mean = np.zeros(2)
        cov = [[1, .5],
               [.5, 1]]
        rv = multivariate_normal(mean, cov)
        Z = rv.pdf(np.dstack((X, Y)))
        data = {'x': X.flatten(), 'y': Y.flatten(), 'z': Z.flatten()}
        ggplot(data, aes(x='x', y='y', z='z')) + \\
            geom_contour(sampling=sampling_vertex_dp(100))

    """
    return _sampling('vertex_dp', n=n)


def sampling_polygon_dp(n):
    return _sampling('polygon_dp', n=n)


def sampling_polygon_vw(n):
    return _sampling('polygon_vw', n=n)


def sampling_path_dp(n):
    return _sampling('path_dp', n=n)


def sampling_path_vw(n):
    return _sampling('path_vw', n=n)


def _sampling(name, **kwargs):
    return FeatureSpec('sampling', name, **kwargs)
