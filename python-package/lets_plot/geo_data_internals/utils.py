#  Copyright (c) 2021. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
from typing import List, Any

from lets_plot.geo_data_internals.constants import DF_COLUMN_CITY, DF_COLUMN_COUNTY, DF_COLUMN_STATE, DF_COLUMN_COUNTRY


def find_geo_names(obj) -> List[str]:
    if is_geocoder(obj):
        data = obj.get_geocodes()
    else:
        data = obj

    names = []
    if DF_COLUMN_CITY in data:
        names.append(DF_COLUMN_CITY)
    if DF_COLUMN_COUNTY in data:
        names.append(DF_COLUMN_COUNTY)
    if DF_COLUMN_STATE in data:
        names.append(DF_COLUMN_STATE)
    if DF_COLUMN_COUNTRY in data:
        names.append(DF_COLUMN_COUNTRY)

    return names


def is_geocoder(v: Any) -> bool:
    # do not import Geocoder directly to suppress OSM attribution from geo_data package
    if v is None:
        return False

    return any(base.__name__ == 'Geocoder' for base in type(v).mro())

