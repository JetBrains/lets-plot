#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from assertions import assert_geocoded, assert_ambiguous

from lets_plot.geo_data.gis.fluent_dict import FluentDict, FluentList
from lets_plot.geo_data.gis.geometry import GeoPoint, GeoRect, Ring, Polygon
from lets_plot.geo_data.gis.json_response import ResponseParser, ResponseField, ResponseFormatter
from lets_plot.geo_data.gis.response import Status, ErrorResponse, LevelKind, ResponseBuilder, FeatureBuilder, \
    SuccessResponse, \
    Namesake, NamesakeParent, AmbiguousResponse


def test_valid_error_response():
    response_dict = FluentDict() \
        .put(ResponseField.status, Status.error.value) \
        .put(ResponseField.message, 'error')

    error_response = ResponseParser.parse(response_dict._dict)

    assert isinstance(error_response, ErrorResponse)
    assert 'error' == error_response.message


def test_valid_success_response():
    foo = FeatureBuilder() \
        .set_query('foo') \
        .set_id('bar') \
        .set_name('baz') \
        .set_boundary(Polygon([Ring([GeoPoint(1, 2), GeoPoint(3, 4), GeoPoint(5, 6), GeoPoint(7, 8)])])) \
        .set_centroid(GeoPoint(9, 10)) \
        .set_limit(GeoRect(11, 12, 13, 14)) \
        .set_position(GeoRect(15, 16, 17, 18)) \
        .build_geocoded()

    foofoo = FeatureBuilder() \
        .set_query('foofoo') \
        .set_id('barbar') \
        .set_name('bazbaz') \
        .build_geocoded()

    response_dict = ResponseFormatter.format(
        ResponseBuilder() \
            .set_status(Status.success) \
            .set_message('OK') \
            .set_level(LevelKind.city) \
            .set_geocoded_features([foo, foofoo])
            .build()
    )

    response = ResponseParser.parse(response_dict)

    assert isinstance(response, SuccessResponse)
    assert 'OK' == response.message
    assert 2 == len(response.answers)
    assert_geocoded(foo, response.answers[0].features[0])
    assert_geocoded(foofoo, response.answers[1].features[0])


def test_ambiguous_response():
    foo = FeatureBuilder() \
        .set_query('foo') \
        .set_total_namesake_count(150) \
        .set_namesake_examples(FluentList() \
                               .add(Namesake('foo-namesake', FluentList() \
                                             .add(NamesakeParent('foo-ns-parent', LevelKind.city))
                                             .list()))
                               .add(Namesake('foo-namenamesake', FluentList() \
                                             .add(NamesakeParent('foo-nns-parent', LevelKind.state))
                                             .add(NamesakeParent('foo--nns-parent', LevelKind.county))
                                             .list()))
                               .list()) \
        .build_ambiguous()

    foofoo = FeatureBuilder() \
        .set_query("foofoo") \
        .set_total_namesake_count(13) \
        .set_namesake_examples([]) \
        .build_ambiguous()

    response_dict = ResponseFormatter.format(
        ResponseBuilder() \
            .set_status(Status.ambiguous) \
            .set_message('amb') \
            .set_level(LevelKind.city) \
            .set_ambiguous_features([foo, foofoo])
            .build()
    )

    response = ResponseParser.parse(response_dict)

    assert isinstance(response, AmbiguousResponse)
    assert 'amb' == response.message
    assert 2 == len(response.features)
    assert_ambiguous(foo, response.features[0])
    assert_ambiguous(foofoo, response.features[1])
