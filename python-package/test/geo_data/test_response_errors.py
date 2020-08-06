#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from typing import List

import pytest

from lets_plot.geo_data.gis.response import Namesake, LevelKind, FeatureBuilder, NamesakeParent, AmbiguousFeature, \
    AmbiguousResponse, ErrorResponse
from lets_plot.geo_data.regions import _create_multiple_error_message, _format_error_message
from .geo_data import ERROR_MESSAGE, make_ambiguous_response, make_error_response


def ambiguous(feature: AmbiguousFeature) -> AmbiguousResponse:
    response: AmbiguousResponse = make_ambiguous_response().set_ambiguous_features([feature]).build()
    return response


def error(message: str) -> ErrorResponse:
    response: ErrorResponse = make_error_response().set_message(message).build()
    return response


@pytest.mark.parametrize('response, expected_message', [
    (ambiguous(
        FeatureBuilder()
            .set_query('NY')
            .set_total_namesake_count(2)
            .set_namesake_examples([Namesake('NY', [NamesakeParent('England', LevelKind.country)])])
            .build_ambiguous()),
     'Multiple objects (2) were found for NY:\n- NY (England)\n'
    ),

    (ambiguous(
        FeatureBuilder()
            .set_query('NY')
            .set_total_namesake_count(0)
            .set_namesake_examples([])
            .build_ambiguous()),
     'No objects were found for NY.\n'
    ),

    (error(
        ERROR_MESSAGE),
     'error msg'
    ),
])
def test_args_that_fail(response, expected_message):
    assert expected_message == _format_error_message(response)


def test_create_multiple_error_message():
    query = 'York'
    namesakes: List[Namesake] = \
        [
            Namesake('York', [
                NamesakeParent('Illinois', LevelKind.state),
                NamesakeParent('USA', LevelKind.country)
            ]),
            Namesake('New York', [
                NamesakeParent('New York', LevelKind.county),
                NamesakeParent('New York', LevelKind.state),
                NamesakeParent('USA', LevelKind.country)
            ])
        ]

    total_namesake_count = 2
    text = _create_multiple_error_message(query, namesakes, total_namesake_count)

    expected_lines = []
    for namesake in namesakes:
        expected_line = '\n- ' + namesake.name
        if len(namesake.parents) > 0:
            expected_line += ' (' + ', '.join([o.name for o in namesake.parents]) + ')'
        expected_lines.append(expected_line)

    expected_text = 'Multiple objects (' + str(total_namesake_count) + ') were found for ' + query + ":" \
                    + expected_lines[0] \
                    + expected_lines[1]

    assert text == expected_text


def test_create_multiple_error_message_without_namesakes():
    query = 'York'
    namesakes: List[Namesake] = []
    total_namesake_count = 4
    text = _create_multiple_error_message(query, namesakes, total_namesake_count)

    expected_text = 'Multiple objects (' + str(total_namesake_count) + ') were found for ' + query + "."

    assert text == expected_text
