#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from .core import FeatureSpec
from .util import as_boolean

#
# Scales
#

__all__ = ['scale_shape',
           'scale_x_discrete', 'scale_y_discrete',
           'scale_x_continuous', 'scale_y_continuous',
           'scale_x_log10', 'scale_y_log10',
           'scale_color_manual', 'scale_fill_manual', 'scale_size_manual',
           'scale_shape_manual', 'scale_linetype_manual', 'scale_alpha_manual',
           'scale_fill_gradient', 'scale_fill_continuous', 'scale_color_gradient', 'scale_color_continuous',
           'scale_fill_gradient2', 'scale_color_gradient2',
           'scale_fill_hue', 'scale_fill_discrete', 'scale_color_hue', 'scale_color_discrete',
           'scale_fill_grey', 'scale_color_grey',
           'scale_fill_brewer', 'scale_color_brewer',
           'scale_x_datetime', 'scale_y_datetime', 'scale_alpha',
           'scale_size', 'scale_size_area'
           ]


def scale_shape(solid=True, name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, guide=None):
    """
    Scale for shapes

    Parameters
    ----------
    solid : boolean
        Are the shapes solid (default) True, or hollow (False)?
    name : string
        The name of the scale - used as the axis label or the legend title
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Scale for shapes. A continuous variable cannot be mapped to shape.
     Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> x = np.random.uniform(-1, 1, size=100)
    >>> y = np.random.normal(size=100)
    >>> dat = pd.DataFrame({'x': x, 'y': 25 * x ** 2 + y})
    >>> dat['class'] = ['0' if dat['x'][i] < 0 else '1' for i in range(100)]
    >>> ggplot(dat, aes(x='x', y='y', shape='class')) + geom_point(size=5) + scale_shape(solid=False)
    """
    solid = as_boolean(solid, default=True)
    return _scale('shape', name, breaks, labels, limits, expand, na_value, guide, None, solid=solid)


#
# Continuous Scales
#

def scale_x_continuous(name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, trans=None):
    """
    Continuous position scales (x)

    Parameters
    ----------
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list of numerics
        A numeric vector of length two providing limits of the scale.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    na_value :
        Missing values will be replaced with this value.
    trans :
        Name of built-in transformation. ('identity', 'log10')
    Returns
    -------
        scale specification
    Notes
    -----
        Continuous position scales.

     Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> N = 10000
    >>> x = np.random.uniform(-4, 4, size=N)
    >>> x = x.astype(int)
    >>> y = np.random.normal(size=N)
    >>> dat = pd.DataFrame({'x': x, 'y': 25 * x ** 2 + y})
    >>> dat['class'] = ['0' if dat['x'][i] < 0 else '1' for i in range(N)]
    >>> breaks = [-3, -2, -1, 0, 1, 2, 3]
    >>> labels = ['-3', '-2', '-1', '0', '1', '2', '3']
    >>> ggplot(dat, aes('x', group='class')) + geom_bar(stat='count') \
    ...     + scale_x_continuous(name='discretised x', breaks=breaks, labels=labels)
    """
    return _scale('x', name, breaks, labels, limits, expand, na_value, None, trans)


def scale_y_continuous(name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, trans=None):
    """
    Continuous position scales (y)

    Parameters
    ----------
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list of numerics
        A numeric vector of length two providing limits of the scale.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    trans :
        Name of built-in transformation. ('identity', 'log10')
    Returns
    -------
        scale specification
    Notes
    -----
        Continuous position scales.

     Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> N = 10000
    >>> x = np.random.uniform(-4, 4, size=N)
    >>> x = x.astype(int)
    >>> y = np.random.normal(size=N)
    >>> dat = pd.DataFrame({'x': x, 'y': 25 * x ** 2 + y})
    >>> dat['class'] = ['0' if dat['x'][i] < 0 else '1' for i in range(N)]
    >>> breaks = [-3, -2, -1, 0, 1, 2, 3]
    >>> labels = ['-3', '-2', '-1', '0', '1', '2', '3']
    >>> y_breaks = [1500, 3000]
    >>> y_labels = ['one', 'two']
    >>> ggplot(dat, aes('x', 'y', group='class')) + geom_bar(stat='count') \
    ...     + scale_y_continuous(breaks=y_breaks, labels=y_labels)
    """
    return _scale('y', name, breaks, labels, limits, expand, na_value, None, trans)


def scale_x_log10(name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None):
    """
    Continuous position scales (x) where trans='log10'

    Parameters
    ----------
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list of numerics
        A numeric vector of length two providing limits of the scale.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    na_value :
        Missing values will be replaced with this value.
    Returns
    -------
        scale specification
    Notes
    -----
        Continuous position scales.
    Examples
    --------
    >>> import pandas as pd
    >>> N = 21
    >>> x = [v for v in range(N)]
    >>> y0 = [pow(10, v / 10.) for v in range(N)]
    >>> y1 = [v * 5 for v in range(N)]
    >>> formula = ['10^(x/10)'] * N + ['5*x'] * N
    >>> data = dict(x=x * 2, y=y0 + y1, formula=formula)
    >>> ### Linear scales (default)
    >>> p = ggplot(data) + geom_point(aes('x', 'y', color='formula', size='formula')) + \
    ...     scale_size_manual(values=[7, 3])
    >>> ### Log10 scale on Y axis
    >>> p + scale_y_log10()
    >>> ### Log10 scale on both axis
    >>> p + scale_y_log10() + scale_x_log10()
    """
    return scale_x_continuous(name, breaks, labels, limits, expand, na_value, 'log10')


def scale_y_log10(name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None):
    """
    Continuous position scales (y) where trans='log10'

    Parameters
    ----------
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list of numerics
        A numeric vector of length two providing limits of the scale.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    na_value :
        Missing values will be replaced with this value.
    Returns
    -------
        scale specification
    Notes
    -----
        Continuous position scales.
    Examples
    --------
    >>> import pandas as pd
    >>> N = 21
    >>> x = [v for v in range(N)]
    >>> y0 = [pow(10, v / 10.) for v in range(N)]
    >>> y1 = [v * 5 for v in range(N)]
    >>> formula = ['10^(x/10)'] * N + ['5*x'] * N
    >>> data = dict(x=x * 2, y=y0 + y1, formula=formula)
    >>> ### Linear scales (default)
    >>> p = ggplot(data) + geom_point(aes('x', 'y', color='formula', size='formula')) + \
    ...     scale_size_manual(values=[7, 3])
    >>> ### Log10 scale on Y axis
    >>> p + scale_y_log10()
    >>> ### Log10 scale on both axis
    >>> p + scale_y_log10() + scale_x_log10()
    """
    return scale_y_continuous(name, breaks, labels, limits, expand, na_value, 'log10')


#
# Discrete Scales
#

def scale_x_discrete(name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None):
    """
    Discrete position scales (x)

    Parameters
    ----------
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        A vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Discrete position scales.

     Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> N = 10000
    >>> x = np.random.uniform(-4, 4, size=N)
    >>> x = x.astype(int)
    >>> y = np.random.normal(size=N)
    >>> dat = pd.DataFrame({'x': x, 'y': 25 * x ** 2 + y})
    >>> dat['class'] = ['0' if dat['x'][i] < 0 else '1' for i in range(N)]
    >>> breaks = [-3, -2, -1, 0, 1, 2, 3]
    >>> labels = ['-3', '-2', '-1', '0', '1', '2', '3']
    >>> ggplot(dat, aes('x', group='class')) + geom_bar(stat='count') \
    ...     + scale_x_continuous(name='discretised x', breaks=breaks, labels=labels)
    """
    return _scale('x', name, breaks, labels, limits, expand, na_value, None, None, discrete=True)


def scale_y_discrete(name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None):
    """
    Discrete position scales (y)

    Parameters
    ----------
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        A vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Discrete position scales.

     Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> N = 10000
    >>> x = np.random.uniform(-4, 4, size=N)
    >>> x = x.astype(int)
    >>> y = np.random.normal(size=N)
    >>> dat = pd.DataFrame({'x': x, 'y': 25 * x ** 2 + y})
    >>> dat['class'] = ['0' if dat['x'][i] < 0 else '1' for i in range(N)]
    >>> breaks = [-3, -2, -1, 0, 1, 2, 3]
    >>> labels = ['-3', '-2', '-1', '0', '1', '2', '3']
    >>> y_breaks = [1500, 3000]
    >>> y_labels = ['one', 'two']
    >>> ggplot(dat, aes('x', 'y', group='class')) + geom_bar(stat='count') \
    ...     + scale_y_discrete(breaks=y_breaks, labels=y_labels)
    """
    return _scale('y', name, breaks, labels, limits, expand, na_value, None, None, discrete=True)


#
# Manual Scales
#

def scale_color_manual(values, name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None,
                       guide=None):
    """
    Create your own discrete scale for color aesthetic

    Parameters
    ----------
    values : list of strings
        A set of aesthetic values to map data values to. If this is a named vector, then the values will be matched
        based on the names. If unnamed, values will be matched in order (usually alphabetical) with the limits of
        the scale.
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Create your own discrete scale for color aesthetic. Values are strings, encoding colors.

     Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from sklearn import datasets
    >>> iris = datasets.load_iris()
    >>> X = iris.data
    >>> y = iris.target
    >>> # (1) Split data
    >>> from sklearn.cross_validation import train_test_split
    >>> X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3)
    >>> # (2) Scale data
    >>> from sklearn.preprocessing import StandardScaler
    >>> sc = StandardScaler()
    >>> sc.fit(X_train)
    >>> X_train = sc.transform(X_train)
    >>> X_test = sc.transform(X_test)
    >>> # (3) Fit model
    >>> n_components = 2
    >>> from sklearn.decomposition import KernelPCA
    >>> kpca = KernelPCA(n_components=n_components, kernel="rbf", fit_inverse_transform=True, gamma=1.0)
    >>> kpca.fit(X_train)
    >>> X_reduced = kpca.fit_transform(X_train)
    >>> # (4) Kernel PCA reduction: plot
    >>> dat = dict({'PC1': X_reduced[:, 0], 'PC2': X_reduced[:, 1], 'target': y_train})
    >>> ggplot(dat, aes('PC1', 'PC2')) + geom_point(aes(color='target'), size=3) \
    ...     + scale_color_manual(values=['red', 'blue', 'green'])
    """
    return _scale('color', name, breaks, labels, limits, expand, na_value, guide, None, values=values)


def scale_fill_manual(values, name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, guide=None):
    """
    Create your own discrete scale for fill aesthetic

    Parameters
    ----------
    values : list of strings
        A set of aesthetic values to map data values to. If this is a named vector, then the values will be matched
        based on the names. If unnamed, values will be matched in order (usually alphabetical) with the limits of
        the scale.
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Create your own discrete scale for fill aesthetic. Values are strings, encoding filling colors.

     Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from sklearn import datasets
    >>> iris = datasets.load_iris()
    >>> X = iris.data
    >>> y = iris.target
    >>> # (1) Split data
    >>> from sklearn.cross_validation import train_test_split
    >>> X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3)
    >>> # (2) Scale data
    >>> from sklearn.preprocessing import StandardScaler
    >>> sc = StandardScaler()
    >>> sc.fit(X_train)
    >>> X_train = sc.transform(X_train)
    >>> X_test = sc.transform(X_test)
    >>> # (3) Fit model
    >>> n_components = 2
    >>> from sklearn.decomposition import KernelPCA
    >>> kpca = KernelPCA(n_components=n_components, kernel="rbf", fit_inverse_transform=True, gamma=1.0)
    >>> kpca.fit(X_train)
    >>> X_reduced = kpca.fit_transform(X_train)
    >>> # (4) Kernel PCA reduction: plot
    >>> dat = dict({'PC1': X_reduced[:, 0], 'PC2': X_reduced[:, 1], 'target': y_train})
    >>> ggplot(dat, aes('PC1', 'PC2')) + geom_point(aes(color='target', fill='target'), size=3, shape=21) \
    ...     + scale_color_manual(values=['black', 'black', 'black']) \
    ...     + scale_fill_manual(values=['red', 'blue', 'green'])
    """
    return _scale('fill', name, breaks, labels, limits, expand, na_value, guide, None, values=values)


def scale_size_manual(values, name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, guide=None):
    """
    Create your own discrete scale for size aesthetic

    Parameters
    ----------
    values : list of strings
        A set of aesthetic values to map data values to. If this is a named vector, then the values will be matched
        based on the names. If unnamed, values will be matched in order (usually alphabetical) with the limits of
        the scale.
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Create your own discrete scale for size aesthetic. Values are numbers, defining sizes.

     Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from sklearn import datasets
    >>> iris = datasets.load_iris()
    >>> X = iris.data
    >>> y = iris.target
    >>> # (1) Split data
    >>> from sklearn.cross_validation import train_test_split
    >>> X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3)
    >>> # (2) Scale data
    >>> from sklearn.preprocessing import StandardScaler
    >>> sc = StandardScaler()
    >>> sc.fit(X_train)
    >>> X_train = sc.transform(X_train)
    >>> X_test = sc.transform(X_test)
    >>> # (3) Fit model
    >>> n_components = 2
    >>> from sklearn.decomposition import KernelPCA
    >>> kpca = KernelPCA(n_components=n_components, kernel="rbf", fit_inverse_transform=True, gamma=1.0)
    >>> kpca.fit(X_train)
    >>> X_reduced = kpca.fit_transform(X_train)
    >>> # (4) Kernel PCA reduction: plot
    >>> dat = dict({'PC1': X_reduced[:, 0], 'PC2': X_reduced[:, 1], 'target': y_train})
    >>> ggplot(dat, aes('PC1', 'PC2')) + geom_point(aes(color='target', size='target')) \
    ...     + scale_color_manual(values=['red', 'blue', 'green']) \
    ...     + scale_size_manual(values=[2, 4, 6])
    """
    return _scale('size', name, breaks, labels, limits, expand, na_value, guide, None, values=values)


def scale_shape_manual(values, name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None,
                       guide=None):
    """
    Create your own discrete scale for shape aesthetic

    Parameters
    ----------
    values : list of strings
        A set of aesthetic values to map data values to. If this is a named vector, then the values will be matched
        based on the names. If unnamed, values will be matched in order (usually alphabetical) with the limits of
        the scale.
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Create your own discrete scale for size aesthetic. Values are numbers, encoding shapes.

     Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from sklearn import datasets
    >>> iris = datasets.load_iris()
    >>> X = iris.data
    >>> y = iris.target
    >>> # (1) Split data
    >>> from sklearn.cross_validation import train_test_split
    >>> X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3)
    >>> # (2) Scale data
    >>> from sklearn.preprocessing import StandardScaler
    >>> sc = StandardScaler()
    >>> sc.fit(X_train)
    >>> X_train = sc.transform(X_train)
    >>> X_test = sc.transform(X_test)
    >>> # (3) Fit model
    >>> n_components = 2
    >>> from sklearn.decomposition import KernelPCA
    >>> kpca = KernelPCA(n_components=n_components, kernel="rbf", fit_inverse_transform=True, gamma=1.0)
    >>> kpca.fit(X_train)
    >>> X_reduced = kpca.fit_transform(X_train)
    >>> # (4) Kernel PCA reduction: plot
    >>> dat = dict({'PC1': X_reduced[:, 0], 'PC2': X_reduced[:, 1], 'target': y_train, 'tgt': y_train.astype(str)})
    >>> ggplot(dat, aes('PC1', 'PC2')) + geom_point(aes(color='target', shape='tgt'), size=3) \
    ...     + scale_color_manual(values=['red', 'blue', 'green']) \
    ...     + scale_shape_manual(values=[0, 1, 2])
    """
    return _scale('shape', name, breaks, labels, limits, expand, na_value, guide, None, values=values)


def scale_linetype_manual(values, name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None,
                          guide=None):
    """
    Create your own discrete scale for line type aesthetic

    Parameters
    ----------
    values : list of strings
        A set of aesthetic values to map data values to. If this is a named vector, then the values will be matched
        based on the names. If unnamed, values will be matched in order (usually alphabetical) with the limits of
        the scale.
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Create your own discrete scale for line type aesthetic. Values are strings or numbers, encoding linetypes.
        Available codes and names: 0 = "blank", 1 = "solid", 2 = "dashed", 3 = "dotted", 4 = "dotdash",
        5 = "longdash", 6 = "twodash"

     Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from sklearn import datasets
    >>> iris = datasets.load_iris()
    >>> X = iris.data
    >>> y = iris.target
    >>> # (1) Split data
    >>> from sklearn.cross_validation import train_test_split
    >>> X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3)
    >>> # (2) Scale data
    >>> from sklearn.preprocessing import StandardScaler
    >>> sc = StandardScaler()
    >>> sc.fit(X_train)
    >>> X_train = sc.transform(X_train)
    >>> X_test = sc.transform(X_test)
    >>> # (3) Fit model
    >>> n_components = 2
    >>> from sklearn.decomposition import KernelPCA
    >>> kpca = KernelPCA(n_components=n_components, kernel="rbf", fit_inverse_transform=True, gamma=1.0)
    >>> kpca.fit(X_train)
    >>> X_reduced = kpca.fit_transform(X_train)
    >>> # (4) Kernel PCA reduction: plot
    >>> dat = dict({'PC1': X_reduced[:, 0], 'PC2': X_reduced[:, 1], 'target': y_train, 'tgt': y_train.astype(str)})
    >>> ggplot(dat, aes('PC1', 'PC2')) + geom_line(aes(color='target', linetype='tgt'), size=1) \
    ...     + scale_color_manual(values=['red', 'blue', 'green']) \
    ...     + scale_linetype_manual(values=['dotted', 'solid', 'dashed'])
    """
    return _scale('linetype', name, breaks, labels, limits, expand, na_value, guide, None, values=values)


def scale_alpha_manual(values, name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None,
                       guide=None):
    """
    Create your own discrete scale for alpha (transparency) aesthetic

    Parameters
    ----------
    values : list of strings
        A set of aesthetic values to map data values to. If this is a named vector, then the values will be matched
        based on the names. If unnamed, values will be matched in order (usually alphabetical) with the limits of
        the scale.
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Create your own discrete scale for alpha (transparency) aesthetic. Values should be taken from [0,1] interval.

     Examples
    ---------
    >>> import numpy as np
    >>> import pandas as pd
    >>> from sklearn import datasets
    >>> iris = datasets.load_iris()
    >>> X = iris.data
    >>> y = iris.target
    >>> # (1) Split data
    >>> from sklearn.cross_validation import train_test_split
    >>> X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3)
    >>> # (2) Scale data
    >>> from sklearn.preprocessing import StandardScaler
    >>> sc = StandardScaler()
    >>> sc.fit(X_train)
    >>> X_train = sc.transform(X_train)
    >>> X_test = sc.transform(X_test)
    >>> # (3) Fit model
    >>> n_components = 2
    >>> from sklearn.decomposition import KernelPCA
    >>> kpca = KernelPCA(n_components=n_components, kernel="rbf", fit_inverse_transform=True, gamma=1.0)
    >>> kpca.fit(X_train)
    >>> X_reduced = kpca.fit_transform(X_train)
    >>> # (4) Kernel PCA reduction: plot
    >>> dat = dict({'PC1': X_reduced[:, 0], 'PC2': X_reduced[:, 1], 'target': y_train, 'tgt': y_train.astype(str)})
    >>> ggplot(dat, aes('PC1', 'PC2')) + geom_point(aes(color='target', alpha='target'), size=3) \
    ...     + scale_color_manual(values=['red', 'blue', 'green']) \
    ...     + scale_alpha_manual(values=[0.2, 0.5, 0.9])
    """
    return _scale('alpha', name, breaks, labels, limits, expand, na_value, guide, None, values=values)


#
# Gradient (continuous) Color Scales
#

def scale_fill_gradient(low=None, high=None,
                        name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, guide=None,
                        trans=None, ):
    """
    Defines smooth color gradient between two colors for fill aesthetic

    Parameters
    ----------
    low : string
        Color for low end of gradient
    high : string
        Color for high end of gradient
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Defines smooth gradient between two colors (defined by low and high) for filling color.

     Examples
    ---------
    >>> dat = {'x': [v for v in range(-16, 16)]}
    >>> ggplot(dat) + geom_tile(aes('x', fill='x'), width=1.05) \
    ...     + scale_fill_gradient(low='green', high='red')
    """
    return scale_fill_continuous(low, high, name, breaks, labels, limits, expand, na_value, guide, trans)


def scale_fill_continuous(low=None, high=None,
                          name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, guide=None,
                          trans=None):
    """
    Defines smooth color gradient between two colors for fill aesthetic

    Parameters
    ----------
    low : string
        Color for low end of gradient
    high : string
        Color for high end of gradient
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list of numerics
        A numeric vector of length two providing limits of the scale.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Defines smooth gradient between two colors (defined by low and high) for filling color.

     Examples
    ---------
    >>> dat = {'x': [v for v in range(-16, 16)]}
    >>> ggplot(dat) + geom_tile(aes('x', fill='x'), width=1.05) \
    ...     + scale_fill_continuous(low='green', high='red')
    """
    return _scale('fill', name, breaks, labels, limits, expand, na_value, guide, trans, low=low, high=high,
                  scale_mapper_kind='color_gradient')


def scale_color_gradient(low=None, high=None,
                         name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, guide=None,
                         trans=None):
    """
    Defines smooth color gradient between two colors for color aesthetic

    Parameters
    ----------
    low : string
        Color for low end of gradient
    high : string
        Color for high end of gradient
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Defines smooth gradient between two colors (defined by low and high) for color aesthetic.

     Examples
    ---------
    >>> dat = {'x': [v for v in range(-16, 16)]}
    >>> ggplot(dat) + geom_tile(aes('x', fill='x', color='x'), width=1.05, size=2) \
    ...     + scale_color_gradient(low='green', high='red')
    """
    return scale_color_continuous(low, high, name, breaks, labels, limits, expand, na_value, guide, trans)


def scale_color_continuous(low=None, high=None,
                           name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, guide=None,
                           trans=None):
    """
    Defines smooth color gradient between two colors for color aesthetic

    Parameters
    ----------
    low : string
        Color for low end of gradient
    high : string
        Color for high end of gradient
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list of numerics
        A numeric vector of length two providing limits of the scale.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Defines smooth gradient between two colors (defined by low and high) for color aesthetic.

     Examples
    ---------
    >>> dat = {'x': [v for v in range(-16, 16)]}
    >>> ggplot(dat) + geom_tile(aes('x', fill='x', color='x'), width=1.05, size=2) \
    ...     + scale_color_continuous(low='green', high='red')
    """
    return _scale('color', name, breaks, labels, limits, expand, na_value, guide, trans, low=low, high=high,
                  scale_mapper_kind='color_gradient')


def scale_fill_gradient2(low=None, mid=None, high=None, midpoint=0,
                         name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, guide=None,
                         trans=None):
    """
    Defines diverging color gradient for fill aesthetic

    Parameters
    ----------
    low : string
        Color for low end of gradient
    mid : string
        Color for mid point
    high : string
        Color for high end of gradient
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Defines diverging color gradient for filling color. Default mid point is set to white color.

     Examples
    ---------
    >>> dat = {'x': [v for v in range(-16, 16)]}
    >>> ggplot(dat) + geom_tile(aes('x', fill='x'), width=1.05) \
    ...     + scale_fill_gradient2(low='green', high='red')
    """
    return _scale('fill', name, breaks, labels, limits, expand, na_value, guide, trans, low=low, mid=mid, high=high,
                  midpoint=midpoint, scale_mapper_kind='color_gradient2')


def scale_color_gradient2(low=None, mid=None, high=None, midpoint=0,
                          name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, guide=None,
                          trans=None):
    """
    Defines diverging color gradient for color aesthetic

    Parameters
    ----------
    low : string
        Color for low end of gradient
    mid : string
        Color for mid point
    high : string
        Color for high end of gradient
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Defines diverging color gradient for color aesthetic. Default mid point is set to white color.

     Examples
    ---------
    >>> dat = {'x': [v for v in range(-16, 16)]}
    >>> ggplot(dat) + geom_tile(aes('x', fill='x'), width=1.05) \
    ...     + scale_color_gradient2(low='green', high='red')
    """
    return _scale('color', name, breaks, labels, limits, expand, na_value, guide, trans, low=low, mid=mid, high=high,
                  midpoint=midpoint, scale_mapper_kind='color_gradient2')


def scale_fill_hue(h=None, c=None, l=None, h_start=None, direction=None,
                   name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, guide=None,
                   trans=None):
    """
    Qualitative color scale with evenly spaced hues for fill aesthetic

    Parameters
    ----------

    h : list of two numerics
        Range of hues, in [0,360]
    c : numeric
        Chroma (intensity of color), maximum value varies depending on.
    l : numeric
        Luminance (lightness), in [0,100]
    direction : numeric
        Direction to travel around the color wheel, 1 = clockwise (default), -1=counter-clockwise
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Defines qualitative color scale with evenly spaced hues for filling color aesthetic

     Examples
    ---------
    >>> dat = {'x': [v for v in range(-16, 16)]}
    >>> ggplot(dat) + geom_tile(aes('x', fill='x'), width=1.05) \
    ...     + scale_fill_hue(c=50, l=80, h=[0, 50])
    """
    return _scale('fill', name, breaks, labels, limits, expand, na_value, guide, trans, h=h, c=c, l=l, h_start=h_start,
                  direction=direction, scale_mapper_kind='color_hue')


def scale_color_hue(h=None, c=None, l=None, h_start=None, direction=None,
                    name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, guide=None,
                    trans=None):
    """
    Qualitative color scale with evenly spaced hues for color aesthetic

    Parameters
    ----------

    h : list of two numerics
        Range of hues, in [0,360]
    c : numeric
        Chroma (intensity of color), maximum value varies depending on.
    l : numeric
        Luminance (lightness), in [0,100]
    direction : numeric
        Direction to travel around the color wheel, 1 = clockwise (default), -1=counter-clockwise
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Defines qualitative color scale with evenly spaced hues for color aesthetic

     Examples
    ---------
    >>> dat = {'x': [v for v in range(-16, 16)]}
    >>> ggplot(dat) + geom_tile(aes('x', fill='x', color='x'), width=1.05, size=2) \
    ...     + scale_color_hue(c=20, l=90)
    """
    return _scale('color', name, breaks, labels, limits, expand, na_value, guide, trans, h=h, c=c, l=l, h_start=h_start,
                  direction=direction,
                  scale_mapper_kind='color_hue')


def scale_fill_discrete(h=None, c=None, l=None, h_start=None, direction=None,
                        name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, guide=None):
    """
    Qualitative color scale with evenly spaced hues for fill aesthetic

    Parameters
    ----------

    h : list of two numerics
        Range of hues, in [0,360]
    c : numeric
        Chroma (intensity of color), maximum value varies depending on.
    l : numeric
        Luminance (lightness), in [0,100]
    direction : numeric
        Direction to travel around the color wheel, 1 = clockwise (default), -1=counter-clockwise
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        A vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Defines qualitative color scale with evenly spaced hues for filling color aesthetic

     Examples
    ---------
    >>> dat = {'x': [v for v in range(-16, 16)]}
    >>> ggplot(dat) + geom_tile(aes('x', fill='x'), width=1.05) \
    ...     + scale_fill_discrete(c=50, l=80, h=[0, 50])
    """
    # same as scale_fill_hue but always 'discrete'
    return _scale('fill', name, breaks, labels, limits, expand, na_value, guide, None, h=h, c=c, l=l, h_start=h_start,
                  direction=direction,
                  scale_mapper_kind='color_hue',
                  discrete=True)


def scale_color_discrete(h=None, c=None, l=None, h_start=None, direction=None,
                         name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, guide=None):
    """
    Qualitative color scale with evenly spaced hues for color aesthetic

    Parameters
    ----------

    h : list of two numerics
        Range of hues, in [0,360]
    c : numeric
        Chroma (intensity of color), maximum value varies depending on.
    l : numeric
        Luminance (lightness), in [0,100]
    direction : numeric
        Direction to travel around the color wheel, 1 = clockwise (default), -1=counter-clockwise
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        A vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Defines qualitative color scale with evenly spaced hues for color aesthetic

     Examples
    ---------
    >>> dat = {'x': [v for v in range(-16, 16)]}
    >>> ggplot(dat) + geom_tile(aes('x', fill='x', color='x'), width=1.05, size=2) \
    ...     + scale_color_discrete(c=20, l=90)
    """
    # same as scale_color_hue but always 'discrete'
    return _scale('color', name, breaks, labels, limits, expand, na_value, guide, None, h=h, c=c, l=l, h_start=h_start,
                  direction=direction,
                  scale_mapper_kind='color_hue',
                  discrete=True)


def scale_fill_grey(start=None, end=None, direction=None,
                    name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, guide=None,
                    trans=None):
    """
    Sequential grey color scale for fill aesthetic

    Parameters
    ----------

    start : numeric
        Gray value at low end of palette in range (0,100)
    end : numeric
        Gray value at high end of palette in range (0,100)
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Defines sequential grey color scale for filling color aesthetic

     Examples
    ---------
    >>> dat = {'x': [v for v in range(-16, 16)]}
    >>> ggplot(dat) + geom_tile(aes('x', fill='x'), width=1.05) \
    ...     + scale_fill_grey(start=50, end=10)
    """
    return _scale('fill', name, breaks, labels, limits, expand, na_value, guide, trans, start=start, end=end,
                  direction=direction,
                  scale_mapper_kind='color_grey')


def scale_color_grey(start=None, end=None, direction=None,
                     name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, guide=None,
                     trans=None):
    """
    Sequential grey color scale for color aesthetic

    Parameters
    ----------

    start : numeric
        Gray value at low end of palette in range (0,100)
    end : numeric
        Gray value at high end of palette in range (0,100)
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Defines sequential grey color scale for color aesthetic

     Examples
    ---------
    >>> dat = {'x': [v for v in range(-16, 16)]}
    >>> ggplot(dat) + geom_tile(aes('x', fill='x', color='x'), width=1.05, size=2) \
    ...     + scale_color_grey(start=50, end=10)
    """
    return _scale('color', name, breaks, labels, limits, expand, na_value, guide, trans, start=start, end=end,
                  direction=direction,
                  scale_mapper_kind='color_grey')


def scale_fill_brewer(type=None, palette=None, direction=None,
                      name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, guide=None,
                      trans=None):
    """
    Sequential, diverging and qualitative color scales from colorbrewer.org for fill aesthetic. Color schemes provided
    are particularly suited to display discrete values (levels of factors) on a map.

    Parameters
    ----------

    type : string
        One of seq (sequential), div (diverging) or qual (qualitative) types of scales.
    palette : string or number
        If a string, will use that named palette. If a number, will index into the list of palettes of appropriate type.
    direction : numeric
        Sets the order of colors in the scale. If 1, the default, colors are as output by brewer.pal.
        If -1, the order of colors is reversed.
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Defines sequential, diverging and qualitative color scales from colorbrewer.org for filling color aesthetic.
        ColorBrewer provides sequential, diverging and qualitative color schemes which are particularly suited and
        tested to display discrete values (levels of a factor) on a map. ggplot2 can use those colors in discrete
        scales. It also allows to smoothly interpolate 6 colors from any palette to a continuous scale
        (6 colors per palette gives nice gradients; more results in more saturated colors which do not look as good).
        However, the original color schemes (particularly the qualitative ones) were not intended for this and the
        perceptual result is left to the appreciation of the user. See colorbrewer2.org for more information.

    Palettes
    --------
    - Diverging :
        BrBG, PiYG, PRGn, PuOr, RdBu, RdGy, RdYlBu, RdYlGn, Spectral
    - Qualitative :
        Accent, Dark2, Paired, Pastel1, Pastel2, Set1, Set2, Set3
    - Sequential :
        Blues, BuGn, BuPu, GnBu, Greens, Greys, Oranges, OrRd, PuBu,
        PuBuGn, PuRd, Purples, RdPu, Reds, YlGn, YlGnBu, YlOrBr, YlOrRd
     Examples
    ---------
    >>> dat = {'x': [v for v in range(-16, 16)]}
    >>> ggplot(dat) + geom_tile(aes('x', fill='x'), width=1.05) \
    ...     + scale_fill_brewer(type='seq', palette='Oranges')
    """
    return _scale('fill', name, breaks, labels, limits, expand, na_value, guide, trans, type=type, palette=palette,
                  direction=direction,
                  scale_mapper_kind='color_brewer')


def scale_color_brewer(type=None, palette=None, direction=None,
                       name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, guide=None,
                       trans=None):
    """
    Sequential, diverging and qualitative color scales from colorbrewer.org for color aesthetic. Color schemes provided
    are particularly suited to display discrete values (levels of factors) on a map.

    Parameters
    ----------

    type : string
        One of seq (sequential), div (diverging) or qual (qualitative) types of scales.
    palette : string or number
        If a string, will use that named palette. If a number, will index into the list of palettes of appropriate type.
    direction : numeric
        Sets the order of colors in the scale. If 1, the default, colors are as output by brewer.pal.
        If -1, the order of colors is reversed.
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        Continuous scale: a numeric vector of length two providing limits of the scale.
        Discrete scale: a vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    Returns
    -------
        scale specification
    Notes
    -----
        Defines sequential, diverging and qualitative color scales from colorbrewer.org for color aesthetic.
        ColorBrewer provides sequential, diverging and qualitative color schemes which are particularly suited and
        tested to display discrete values (levels of a factor) on a map. ggplot2 can use those colors in discrete
        scales. It also allows to smoothly interpolate 6 colors from any palette to a continuous scale
        (6 colors per palette gives nice gradients; more results in more saturated colors which do not look as good).
        However, the original color schemes (particularly the qualitative ones) were not intended for this and the
        perceptual result is left to the appreciation of the user. See colorbrewer2.org for more information.

    Palettes
    --------
    - Diverging :
        BrBG, PiYG, PRGn, PuOr, RdBu, RdGy, RdYlBu, RdYlGn, Spectral
    - Qualitative :
        Accent, Dark2, Paired, Pastel1, Pastel2, Set1, Set2, Set3
    - Sequential :
        Blues, BuGn, BuPu, GnBu, Greens, Greys, Oranges, OrRd, PuBu,
        PuBuGn, PuRd, Purples, RdPu, Reds, YlGn, YlGnBu, YlOrBr, YlOrRd
     Examples
    ---------
    >>> dat = {'x': [v for v in range(-16, 16)]}
    >>> ggplot(dat) + geom_tile(aes('x', fill='x', color='x'), width=1.05, size=2) \
    ...     + scale_color_brewer(type='seq', palette='Oranges')
    """
    return _scale('color', name, breaks, labels, limits, expand, na_value, guide, trans, type=type, palette=palette,
                  direction=direction,
                  scale_mapper_kind='color_brewer')


#
# Date-time
#

def scale_x_datetime(name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None):
    """
    Continuous position scale (x)

    Parameters
    ----------
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list of numerics
        A numeric vector of length two providing limits of the scale.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    na_value :
        Missing values will be replaced with this value.
    Returns
    -------
        scale specification
    Notes
    -----
        Continuous position scales.

     Examples
    ---------
    >>> import pandas as pd
    >>> from datetime import datetime
    >>> economics_url = 'https://vincentarelbundock.github.io/Rdatasets/csv/ggplot2/economics.csv'
    >>> economics = pd.read_csv(economics_url)
    >>> economics['date'] = pd.to_datetime(economics['date'])
    >>> start = datetime(2000, 1, 1)
    >>> economics = economics.loc[economics['date'] >= start]
    >>> ggplot(economics, aes('date', 'unemploy')) \
    ...     + geom_step() \
    ...     + scale_x_datetime()
    """
    return _scale('x', name, breaks, labels, limits, expand, na_value, None, None, datetime=True)


def scale_y_datetime(name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None):
    """
    Continuous position scale (y)

    Parameters
    ----------
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list of numerics
        A numeric vector of length two providing limits of the scale.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    na_value :
        Missing values will be replaced with this value.
    Returns
    -------
        scale specification
    Notes
    -----
        Continuous position scales.

     Examples
    ---------
        To-do
    """
    return _scale('y', name, breaks, labels, limits, expand, na_value, None, None, datetime=True)


#
# Range Scale (alpha and size)
#

def scale_alpha(range=None, name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None,
                guide=None, trans=None):
    """
    Scales for alpha

    Parameters
    ----------
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        A vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    na_value :
        Missing values will be replaced with this value.
    range : list of numerics of length 2
        The range of the mapped aesthetics result.
    Returns
    -------
        scale specification
    Notes
    -----

     Examples
    ---------
    >>> import numpy as np
    >>> data = {}
    >>> np.random.seed(43)
    >>> data['x'] = np.append(np.random.normal(0, 1, 1000), np.random.normal(3, 1, 500))
    >>> data['y'] = np.append(np.random.normal(0, 1, 1000), np.random.normal(3, 1, 500))
    >>> q = ggplot(data, aes('x', 'y'))
    >>> q + geom_point(aes(alpha='..density..'), stat='density2d', contour=False, n=30) \
    ...     + scale_alpha(range=[0.5, 1])
    """
    return _scale('alpha', name, breaks, labels, limits, expand, na_value, guide, trans, range=range)


def scale_size(range=None, name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None,
               guide=None, trans=None):
    """
    Scales for size

    Parameters
    ----------
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        A vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    na_value :
        Missing values will be replaced with this value.
    range : list of numerics of length 2
        The range of the mapped aesthetics result.
    Returns
    -------
        scale specification
    Notes
    -----

     Examples
    ---------
    >>> import numpy as np
    >>> data = {}
    >>> np.random.seed(43)
    >>> data['x'] = np.append(np.random.normal(0, 1, 1000), np.random.normal(3, 1, 500))
    >>> data['y'] = np.append(np.random.normal(0, 1, 1000), np.random.normal(3, 1, 500))
    >>> q = ggplot(data, aes('x', 'y'))
    >>> q + geom_point(aes(alpha='..density..'), stat='density2d', contour=False, n=30) \
    ...     + scale_size(range=[1, 6])
    """
    return _scale('size', name, breaks, labels, limits, expand, na_value, guide, trans, range=range)


def scale_size_area(max_size=None, name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None,
                    guide=None, trans=None):
    """
    Continuous scales for size that maps 0 to 0

    Parameters
    ----------
    name : string
        The name of the scale - used as the axis label or the legend title. If None, the default, the name of the scale
        is taken from the first mapping used for that aesthetic.
    breaks : list of numerics
        A numeric vector of positions (of ticks)
    labels : list of strings
        A vector of labels (on ticks)
    limits : list
        A vector specifying the data range for the scale. and the default order of their display in guides.
    expand :
        A numeric vector of length two giving multiplicative and additive expansion constants.
    na_value :
        Missing values will be replaced with this value.
    max_size : numeric
        The max size that is mapped to.
    Returns
    -------
        scale specification
    Notes
    -----
        This method maps 0 data to 0 size. Useful in some stats such as count.

     Examples
    ---------
    >>> import numpy as np
    >>> data = {}
    >>> np.random.seed(43)
    >>> data['x'] = np.append(np.random.normal(0, 1, 1000), np.random.normal(3, 1, 500))
    >>> data['y'] = np.append(np.random.normal(0, 1, 1000), np.random.normal(3, 1, 500))
    >>> q = ggplot(data, aes('x', 'y'))
    >>> q + geom_point(aes(alpha='..density..'), stat='density2d', contour=False, n=30) \
    ...     + scale_size_area(max_size=10)
    """
    return _scale('size', name, breaks, labels, limits, expand, na_value, guide, trans, max_size=max_size,
                  scale_mapper_kind='size_area')


def _scale(aesthetic, name=None, breaks=None, labels=None, limits=None, expand=None, na_value=None, guide=None,
           trans=None, **other):
    """
    Create a scale (discrete or continuous)

    :param aesthetic
        The name of the aesthetic that this scale works with
    :param name
        The name of the scale - used as the axis label or the legend title
    :param breaks
        A numeric vector of positions (of ticks)
    :param labels
        A vector of labels (on ticks)
    :param limits
        A numeric vector of length two providing limits of the scale.
    :param expand
        A numeric vector of length two giving multiplicative and additive expansion constants.
    :param na_value
        Value to use for missing values
    :param guide
        Type of legend. Use 'colorbar' for continuous color bar, or 'legend' for discrete values.
    :param trans
        Type of transformation applied to raw data
    :return:
    """

    # flatten the 'other' sub-dictionary
    args = locals().copy()
    args.pop('other')
    return FeatureSpec('scale', **args, **other)
