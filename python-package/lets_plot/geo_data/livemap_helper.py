from enum import Enum
from typing import Union, Optional, List

from pandas import DataFrame

from .geocodes import Geocodes

LOCATION_COORDINATE_COLUMNS = {'lon', 'lat'}
LOCATION_RECTANGLE_COLUMNS = {'lonmin', 'latmin', 'lonmax', 'latmax'}
LOCATION_LIST_ERROR_MESSAGE = "Expected: location = [double lon1, double lat1, ... , double lonN, double latN]"
LOCATION_DATAFRAME_ERROR_MESSAGE = "Expected: location = DataFrame with [{}] or [{}] columns" \
    .format(', '.join(LOCATION_COORDINATE_COLUMNS), ', '.join(LOCATION_RECTANGLE_COLUMNS))


class RegionKind(Enum):
    region_ids = 'region_ids'
    region_name = 'region_name'
    coordinates = 'coordinates'
    data_frame = 'data_frame'


def _prepare_parent(parent: Union[str, Geocodes]) -> Optional[dict]:
    if not parent:
        return None

    if isinstance(parent, Geocodes):
        kind = RegionKind.region_ids
        value = parent.unique_ids()

    elif isinstance(parent, str):
        kind = RegionKind.region_name
        value = parent

    else:
        raise ValueError('Wrong parent type: ' + parent.__str__())

    return {'type': kind.value, 'data': value}


def _prepare_location(location: Union[str, Geocodes, List[float], DataFrame]) -> Optional[dict]:
    if location is None:
        return None

    value = location
    if isinstance(location, Geocodes):
        kind = RegionKind.region_ids
        value = location.unique_ids()

    elif isinstance(location, str):
        kind = RegionKind.region_name

    elif isinstance(location, list):
        if len(location) == 0 or len(location) % 2 != 0:
            raise ValueError(LOCATION_LIST_ERROR_MESSAGE)
        kind = RegionKind.coordinates

    elif isinstance(location, DataFrame):
        if not LOCATION_COORDINATE_COLUMNS.issubset(location.columns) and not LOCATION_RECTANGLE_COLUMNS.issubset(location.columns):
            raise ValueError(LOCATION_DATAFRAME_ERROR_MESSAGE)
        kind = RegionKind.data_frame

    else:
        raise ValueError('Wrong location type: ' + location.__str__())

    return {'type': kind.value, 'data': value}
