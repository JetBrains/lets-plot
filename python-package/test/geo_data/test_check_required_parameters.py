#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

from typing import Optional, List

import pytest

from lets_plot.geo_data.gis.request import MapRegion, RegionQuery, MISSING_LEVEL_AND_WITHIN_OR_REQUEST_EXCEPTION_TEXT, \
    MISSING_LEVEL_OR_REQUEST_EXCEPTION_TEXT, GeocodingRequest, \
    AmbiguityResolver
from lets_plot.geo_data.gis.response import LevelKind

REACTION_KIND_ALERT: AmbiguityResolver = AmbiguityResolver.empty()

NAME = 'NY'
LEVEL: LevelKind = LevelKind.city
REGION_STR = 'ignored'
PARENT = MapRegion.with_name(REGION_STR)


@pytest.mark.parametrize('region_queries,level,message', [
    ([], None, MISSING_LEVEL_AND_WITHIN_OR_REQUEST_EXCEPTION_TEXT),
    ([RegionQuery(None, None, REACTION_KIND_ALERT)], None, MISSING_LEVEL_AND_WITHIN_OR_REQUEST_EXCEPTION_TEXT),
    ([RegionQuery(None, PARENT, REACTION_KIND_ALERT)], None, MISSING_LEVEL_OR_REQUEST_EXCEPTION_TEXT),
])
def test_args_that_fail(region_queries: List[RegionQuery],
                        level: Optional[LevelKind],
                        message: str):
    with pytest.raises(ValueError) as exception:
        GeocodingRequest._check_required_parameters(region_queries, level)

    assert message == exception.value.args[0]


@pytest.mark.parametrize('region_queries,level', [
    ([RegionQuery(None, None, REACTION_KIND_ALERT)], LevelKind.country),
    ([RegionQuery('ignored_value', None, REACTION_KIND_ALERT)], None),
    ([RegionQuery(None, PARENT, REACTION_KIND_ALERT)], LevelKind.state),
])
def test_args_that_pass(region_queries: List[RegionQuery],
                        level: Optional[LevelKind]):
    GeocodingRequest._check_required_parameters(region_queries, level)

