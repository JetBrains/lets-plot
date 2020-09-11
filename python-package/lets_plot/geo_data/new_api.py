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
        Data can be filtered by full names at any level (only exact matching).
        For 'state' level:
        -'US-48' returns continental part of United States (48 states) in a compact form.
    countries : [array | string | None]
        Parent countries. Should have same size as names.
    states : [array | string | None]
        Parent states. Should have same size as names.
    counties : [array | string | None]
        Parent counties. Should have same size as names.
    scope : [array | string | Regions | None]
        Data can be filtered by within name.
        If within is array then request and within will be merged positionally (size should be equal).
        If within is Regions then request will be searched in any of these regions.
        'US-48' includes continental part of United States (48 states).

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
                          progress_callback=None,
                          chunk_size=None,
                          allow_ambiguous=False,
                          countries=countries,
                          states=states,
                          counties=counties)
