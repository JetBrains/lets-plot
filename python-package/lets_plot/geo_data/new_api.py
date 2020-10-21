#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
from typing import Any, Union, List, Optional

import numpy as np
from pandas import Series

from .gis.geocoding_service import GeocodingService
from .gis.geometry import GeoPoint
from .gis.request import RequestBuilder, RequestKind
from .gis.response import Response, SuccessResponse
from .regions import Regions, _raise_exception, _to_level_kind, _to_scope
from .regions_builder import RegionsBuilder
from .type_assertion import assert_list_type

__all__ = [
    'regions_builder2'
]

def regions_builder2(level=None, names=None, scope=None, countries=None, states=None, counties=None, highlights=False) -> RegionsBuilder:
    """
    Create a RegionBuilder class by level and request. Allows to refine ambiguous request with
    where method. build() method creates Regions object or shows details for ambiguous result.

    regions_builder(level, request, within)

    Parameters
    ----------
    level : ['country' | 'state' | 'county' | 'city' | None]
        The level of administrative division. Default is a 'state'.
    names : [array | string | None]
        Names of objects to be geocoded.
        For 'state' level:
        -'US-48' returns continental part of United States (48 states) in a compact form.
    countries : [array | None]
        Parent countries. Should have same size as names. Can contain strings or Regions objects.
    states : [array | None]
        Parent states. Should have same size as names. Can contain strings or Regions objects.
    counties : [array | None]
        Parent counties. Should have same size as names. Can contain strings or Regions objects.
    scope : [array | string | Regions | None]
        Limits area of geocoding. Applyed to a highest admin level of parents that are set or to names, if no parents given.
        If all parents are set (including countries) then the scope parameter is ignored.
        If scope is an array then geocoding will try to search objects in all scopes.

    Returns
    -------
    RegionsBuilder object :

    Note
    -----
    regions_builder() allows to refine ambiguous request with where() method. Call build() method to create Regions object

    Examples
    ---------
    >>> from lets_plot.geo_data import *
    >>> r = regions_builder(level='city', request=['moscow', 'york']).where('york', regions_state('New York')).build()
    """
    return RegionsBuilder(level, names, scope, highlights,
                          allow_ambiguous=False,
                          countries=countries,
                          states=states,
                          counties=counties,
                          new_api=True)
