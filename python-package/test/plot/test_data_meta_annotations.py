#  Copyright (c) 2022. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from datetime import datetime, timezone, date, time
from datetime import timedelta

import numpy as np
from pandas import DataFrame, Categorical
# Python 3.9+ is required for ZoneInfo.
from zoneinfo import ZoneInfo

from lets_plot import aes, ggplot, geom_point
from lets_plot.mapping import as_discrete

data_dict = {
    'python_datetime': [(datetime(2020, 1, 1))],
    'python_datetime_tz_utc': [(datetime(2020, 1, 1, tzinfo=timezone.utc))],
    'python_datetime_tz_utc-6': [(datetime(2020, 1, 1, tzinfo=timezone(-timedelta(hours=6))))],  # CST = UTC-6
    'python_datetime_tz_chicago': [(datetime(2020, 1, 1, tzinfo=timezone.utc).astimezone(ZoneInfo('America/Chicago')))],
    'python_date': [(date(2020, 1, 1))],
    'python_time': [(time(12, 30, 45))],
    'np_datetime64': [np.datetime64('2020-01-01')],
    'python_float': [0.0],
    'python_int': [0],
    'python_str': ['foo'],
    'python_bool': [True],
    'np_int': np.array([1], dtype=np.int64),
    'np_float': np.array([1.0], dtype=np.float64),
    'unknown': [type({})]
}
expected_series_annotations = [
    {'column': 'python_datetime', 'type': 'datetime'},
    {'column': 'python_datetime_tz_utc', 'type': 'datetime', 'time_zone': 'UTC'},
    {'column': 'python_datetime_tz_utc-6', 'type': 'datetime', 'time_zone': 'UTC-06:00'},
    {'column': 'python_datetime_tz_chicago', 'type': 'datetime', 'time_zone': 'America/Chicago'},
    {'column': 'python_date', 'type': 'date'},
    {'column': 'python_time', 'type': 'time'},
    {'column': 'np_datetime64', 'type': 'datetime'},
    {'column': 'python_float', 'type': 'float'},
    {'column': 'python_int', 'type': 'int'},
    {'column': 'python_str', 'type': 'str'},
    {'column': 'python_bool', 'type': 'bool'},
    {'column': 'np_int', 'type': 'int'},
    {'column': 'np_float', 'type': 'float'},
    # {'column': 'unknown', 'type': 'unknown'}, unknown type should not be added to series_annotations
]


def test_as_discrete_regression():
    # no order
    d = {
        'v': ['a', 'b'],
        'bar': ['b', 'a']
    }

    p = ggplot(d, mapping=aes(x=as_discrete('v'))) + geom_point()
    assert p.as_dict()['data_meta']['mapping_annotations'] == [
        {'aes': 'x', 'annotation': 'as_discrete', 'parameters': {'label': 'v'}}
    ]
    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'type': 'str'},
        {'column': 'bar', 'type': 'str'}
    ]

    # with order
    p = ggplot(d, mapping=aes(x=as_discrete('v', order=1))) + geom_point()
    assert p.as_dict()['data_meta']['mapping_annotations'] == [
        {'aes': 'x', 'annotation': 'as_discrete', 'parameters': {'label': 'v', 'order': 1}}
    ]
    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'type': 'str'},
        {'column': 'bar', 'type': 'str'}
    ]

    # with order_by
    p = ggplot(d, mapping=aes(x=as_discrete('v', order_by='bar'))) + geom_point()
    assert p.as_dict()['data_meta']['mapping_annotations'] == [
        {'aes': 'x', 'annotation': 'as_discrete', 'parameters': {'label': 'v', 'order_by': 'bar'}}
    ]
    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'type': 'str'},
        {'column': 'bar', 'type': 'str'}
    ]

    # with levels
    p = ggplot(d, mapping=aes(x=as_discrete('v', levels=['a', 'b']))) + geom_point()
    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'factor_levels': ['a', 'b'], 'type': 'str'},
        {'column': 'bar', 'type': 'str'}
    ]
    assert 'mapping_annotations' not in p.as_dict()['data_meta']

    # with order and levels
    p = ggplot(d, mapping=aes(x=as_discrete('v', order=1, levels=['a', 'b']))) + geom_point()
    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'factor_levels': ['a', 'b'], 'order': 1, 'type': 'str'},
        {'column': 'bar', 'type': 'str'}
    ]
    assert 'mapping_annotations' not in p.as_dict()['data_meta']

    # with order_by and levels
    p = ggplot(d, mapping=aes(x=as_discrete('v', order_by='bar', levels=['a', 'b']))) + geom_point()
    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'factor_levels': ['a', 'b'], 'type': 'str'},
        {'column': 'bar', 'type': 'str'}
    ]
    assert 'mapping_annotations' not in p.as_dict()['data_meta']

    # with order, order_by and levels
    p = ggplot(d, mapping=aes(x=as_discrete('v', order=1, order_by='bar', levels=['a', 'b']))) + geom_point()
    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'factor_levels': ['a', 'b'], 'order': 1, 'type': 'str'},
        {'column': 'bar', 'type': 'str'}
    ]
    assert 'mapping_annotations' not in p.as_dict()['data_meta']

    from datetime import datetime
    a = datetime(2020, 1, 1)
    b = datetime(2020, 1, 2)

    # with datetime
    p = ggplot({'v': [a, b]}, aes(x=as_discrete('v'))) + geom_point()
    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'type': 'datetime'}
    ]
    assert p.as_dict()['data_meta']['mapping_annotations'] == [
        {'aes': 'x', 'annotation': 'as_discrete', 'parameters': {'label': 'v'}}
    ]

    p = ggplot({'v': [a, b]}, aes(x=as_discrete('v', levels=[b, a]))) + geom_point()
    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'factor_levels': [b, a], 'type': 'datetime'}
    ]
    assert 'mapping_annotations' not in p.as_dict()['data_meta']

    p = ggplot({'v': [a, b]}, aes(x=as_discrete('v', levels=[b, a], order=1))) + geom_point()
    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'factor_levels': [b, a], 'order': 1, 'type': 'datetime'},
    ]
    assert 'mapping_annotations' not in p.as_dict()['data_meta']

    p = ggplot({'v': [a, b]}, aes(x=as_discrete('v', levels=[b, a], order_by='v'))) + geom_point()
    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'factor_levels': [b, a], 'type': 'datetime'},
    ]
    assert 'mapping_annotations' not in p.as_dict()['data_meta']

    p = ggplot({'v': [a, b]}, aes(x=as_discrete('v', levels=[b, a], order=1, order_by='v'))) + geom_point()
    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'factor_levels': [b, a], 'order': 1, 'type': 'datetime'},
    ]
    assert 'mapping_annotations' not in p.as_dict()['data_meta']

    p = ggplot({'v': [a, b]}, aes(x=as_discrete('v', order=1))) + geom_point()
    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'type': 'datetime'}
    ]
    assert p.as_dict()['data_meta']['mapping_annotations'] == [
        {'aes': 'x', 'annotation': 'as_discrete', 'parameters': {'label': 'v', 'order': 1}}
    ]


# This test fixates the current behavior of the library:
# If levels are specified, the label should not be added to the mapping annotations.
# Why not fix - if both levels and label are specified, the levels get ignored.
def test_do_not_add_label_if_levels_are_specified():
    d = {
        'X': [1, 0]
    }

    p = ggplot(d, aes(x=as_discrete('X', levels=[0, 1], label="The X"))) + geom_point()

    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'X', 'type': 'int', 'factor_levels': [0, 1]}
    ]
    assert 'mapping_annotations' not in p.as_dict()['data_meta']


def test_values_list_in_aes_doest_not_produce_series_annotations():
    # values list in layer
    p = ggplot() + geom_point(aes(x=[0, 0, 0], y=[1, 2, 3]))
    assert p.as_dict()['data_meta'] == {}

    # values list in ggplot
    p = ggplot(mapping=aes(x=[0, 0, 0], y=[1, 2, 3])) + geom_point()
    assert p.as_dict()['data_meta'] == {}


def test_as_annotated_data_dict():
    p = ggplot(data_dict) + geom_point()
    assert p.as_dict()['data_meta']['series_annotations'] == expected_series_annotations + [
        {'column': 'unknown', 'type': "unknown(python:<class 'type'>)"}]


def test_as_annotated_data_dataframe():
    df = DataFrame(data_dict)
    p = ggplot(df) + geom_point()
    assert p.as_dict()['data_meta']['series_annotations'] == expected_series_annotations + [
        {'column': 'unknown', 'type': 'unknown(pandas:mixed)'}]


def test_as_annotated_data_polars_dataframe():
    from polars import DataFrame as plDataFrame

    modified_data_dict = {key: value for key, value in data_dict.items() if key != 'np_datetime64'}

    modified_expected_series_annotations = []
    for item in expected_series_annotations:
        if item['column'] == 'np_datetime64':
            # Skip this item
            continue
        elif item['column'] == 'python_datetime_tz_utc-6':
            # Polars does not seem to support fixed offset time zones.
            modified_expected_series_annotations.append(
                {'column': 'python_datetime_tz_utc-6', 'type': 'datetime', 'time_zone': 'UTC'})
        else:
            modified_expected_series_annotations.append(item.copy())

    # Test

    df = plDataFrame(modified_data_dict)
    p = ggplot(df) + geom_point()

    assert p.as_dict()['data_meta']['series_annotations'] == modified_expected_series_annotations + [
        {'column': 'unknown', 'type': 'unknown(polars:Object)'}]


def test_as_annotated_data_list():
    data = [datetime(2020, 1, 1)]
    p = ggplot(data) + geom_point()
    assert p.as_dict()['data_meta'] == {}


def test_as_annotated_data_string():
    data = {'x': 'foo', 'y': 'bar'}
    p = ggplot(data) + geom_point()

    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'x', 'type': 'str'},
        {'column': 'y', 'type': 'str'}
    ]


def test_dict_with_empty_series():
    data = {'x': [], 'y': []}
    p = ggplot(data) + geom_point()
    assert p.as_dict()['data_meta'] == {}


def test_dict_with_none_series():
    data = {'x': [None], 'y': [None]}
    p = ggplot(data) + geom_point()
    assert p.as_dict()['data_meta'] == {}


def test_dict_with_mixed_int_float_series():
    data = {'x': [1, 1.0, 2, 2.0]}
    p = ggplot(data) + geom_point()
    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'x', 'type': 'float'}
    ]


def test_pd_with_empty_series():
    data = DataFrame({'x': [], 'y': []})
    p = ggplot(data) + geom_point()
    assert p.as_dict()['data_meta'] == {}


def test_pd_with_none_series():
    data = DataFrame({'x': [None], 'y': [None]})
    p = ggplot(data) + geom_point()
    assert p.as_dict()['data_meta'] == {}


def test_pd_with_mixed_int_float_series():
    data = DataFrame({'x': [1, 1.0, 2, 2.0]})
    p = ggplot(data) + geom_point()
    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'x', 'type': 'float'}
    ]


def test_factor_levels():
    data = {
        'v1': ['foo', 'bar'],
        'v2': [1, 2],
    }

    mapping = aes(as_discrete('v1', levels=['foo', 'bar']))
    p = ggplot(data, mapping) + geom_point()

    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v1', 'type': 'str', 'factor_levels': ['foo', 'bar']},
        {'column': 'v2', 'type': 'int'}
    ]


def test_factor_levels_with_ordering():
    data = {
        'v1': ['foo', 'bar'],
        'v2': [1, 2],
    }

    mapping = aes(
        x=as_discrete('v1', order=-1),
        y='v2',
        a=as_discrete('v1', levels=['foo', 'bar']),
        b=as_discrete('v2', levels=[2, 1], label='V2')  # do not lose the label in mapping annotations
    )

    p = ggplot(data, mapping) + geom_point()

    assert 'mapping_annotations' not in p.as_dict()['data_meta']
    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v1', 'type': 'str', 'factor_levels': ['foo', 'bar'], 'order': -1},
        {'column': 'v2', 'type': 'int', 'factor_levels': [2, 1]},
    ]


def test_with_mapping_annotations():
    data = {
        'v1': ['foo', 'bar'],
        'v2': [1, 2],
    }

    mapping = aes(
        x=as_discrete('v1', order_by='v2'),
        y=as_discrete('v2', order_by='v1'),
        a=as_discrete('v1', order=-1),
        b=as_discrete('v2', label='V2', levels=[2, 1])
    )
    p = ggplot(data, mapping) + geom_point()

    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v1', 'type': 'str'},
        {'column': 'v2', 'type': 'int', 'factor_levels': [2, 1]},
    ]
    assert p.as_dict()['data_meta']['mapping_annotations'] == [
        {'aes': 'x', 'annotation': 'as_discrete', 'parameters': {'label': 'v1', 'order_by': 'v2'}},
        {'aes': 'a', 'annotation': 'as_discrete', 'parameters': {'label': 'v1', 'order': -1}},
    ]


def test_pd_categorical_variable():
    df = DataFrame({
        'v': Categorical(['ch4', 'ch5', 'ch1', 'ch2'], categories=['ch5', 'ch4', 'ch2', 'ch1'], ordered=True)
    })

    p = ggplot(df) + geom_point()

    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'type': 'str', 'factor_levels': ['ch5', 'ch4', 'ch2', 'ch1']},
    ]


def test_pd_int_categorical_variable():
    df = DataFrame({
        'v': Categorical([4, 5, 2, 1], categories=[5, 4, 2, 1], ordered=True)
    })

    p = ggplot(df) + geom_point()

    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'type': 'int', 'factor_levels': [5, 4, 2, 1]},
    ]


def test_pd_int_categorical_variable_from_ggplot_dataframe():
    df = DataFrame({
        'v': Categorical([4, 5, 2, 1], categories=[5, 4, 2, 1], ordered=True)
    })

    p = ggplot(df) + geom_point()

    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'type': 'int', 'factor_levels': [5, 4, 2, 1]},
    ]


def test_pd_categorical_variable_with_order_from_mapping():
    df = DataFrame({
        'v': Categorical(values=['ch4', 'ch5', 'ch1', 'ch2'], categories=['ch5', 'ch4', 'ch2', 'ch1'], ordered=True)
    })

    p = ggplot(df, aes(x=as_discrete('v', order=-1))) + geom_point()

    assert p.as_dict()['data_meta']['series_annotations'] == [
        {'column': 'v', 'type': 'str', 'factor_levels': ['ch5', 'ch4', 'ch2', 'ch1'], 'order': -1},
    ]


def test_as_discrete():
    d = {
        'x': [1, 2, 3, 4, 5],
        'y': [0, 0, 0, 0, 0],
        'c': [1, 2, 3, 1, 2]
    }
    p = ggplot(d) + geom_point(aes(x='x', y='y', color=as_discrete('c')))

    # no meta for 'c' in the layer -> no series_annotations
    assert 'series_annotations' not in p.as_dict()['layers'][0]['data_meta']

    assert p.as_dict()['layers'][0]['data_meta']['mapping_annotations'] == [
        {'aes': 'color', 'annotation': 'as_discrete', 'parameters': {'label': 'c'}}
    ]


def test_as_discrete_with_levels():
    d = {
        'x': [1, 2, 3, 4, 5],
        'y': [0, 0, 0, 0, 0],
        'c': [1, 2, 3, 1, 2]
    }
    p = ggplot(d) + geom_point(aes(x='x', y='y', color=as_discrete('c', levels=[3, 2, 1])))

    # order options are in series_annotations
    assert p.as_dict()['layers'][0]['data_meta']['series_annotations'] == [
        {'column': 'c', 'factor_levels': [3, 2, 1]}
    ]

    assert 'mapping_annotations' not in p.as_dict()['layers'][0]['data_meta']
