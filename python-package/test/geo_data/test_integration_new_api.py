#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
from typing import Callable, Any

from shapely.geometry import Point, box
import lets_plot.geo_data as geodata
from lets_plot.geo_data import DF_FOUND_NAME, DF_ID, DF_REQUEST, DF_PARENT_COUNTRY, DF_PARENT_STATE, DF_PARENT_COUNTY
from .geo_data import assert_row, NO_COLUMN


def test_all_columns_order():
    boston = geodata.city_regions_builder('boston').counties('suffolk').states('massachusetts').countries('usa').build()
    assert boston.to_data_frame().columns.tolist() == [DF_ID, DF_REQUEST, DF_FOUND_NAME, DF_PARENT_COUNTY,
                                                       DF_PARENT_STATE, DF_PARENT_COUNTRY]

    gdf_columns = [DF_REQUEST, DF_FOUND_NAME, DF_PARENT_COUNTY, DF_PARENT_STATE, DF_PARENT_COUNTRY, 'geometry']
    assert boston.limits().columns.tolist() == gdf_columns
    assert boston.centroids().columns.tolist() == gdf_columns
    assert boston.boundaries().columns.tolist() == gdf_columns


def test_do_not_add_unsued_parents_columns():
    moscow = geodata.city_regions_builder('moscow').countries('russia').build()

    assert moscow.to_data_frame().columns.tolist() == [DF_ID, DF_REQUEST, DF_FOUND_NAME, DF_PARENT_COUNTRY]

    gdf_columns = [DF_REQUEST, DF_FOUND_NAME, DF_PARENT_COUNTRY, 'geometry']
    assert moscow.limits().columns.tolist() == gdf_columns
    assert moscow.centroids().columns.tolist() == gdf_columns
    assert moscow.boundaries().columns.tolist() == gdf_columns


# @pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_parents_in_regions_object_and_geo_data_frame():
    boston = geodata.city_regions_builder('boston').counties('suffolk').states('massachusetts').countries('usa').build()

    assert_row(boston.to_data_frame(), request='boston', county='suffolk', state='massachusetts', country='usa')
    assert_row(boston.limits(), request='boston', county='suffolk', state='massachusetts', country='usa')
    assert_row(boston.centroids(), request='boston', county='suffolk', state='massachusetts', country='usa')
    assert_row(boston.boundaries(), request='boston', county='suffolk', state='massachusetts', country='usa')

    # antimeridian
    ru = geodata.regions_builder2(level='country', names='russia').build()
    assert_row(ru.to_data_frame(), request='russia', county=NO_COLUMN, state=NO_COLUMN, country=NO_COLUMN)
    assert_row(ru.limits(), request=['russia', 'russia'], county=NO_COLUMN, state=NO_COLUMN, country=NO_COLUMN)


# @pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_regions_parents_in_regions_object_and_geo_data_frame():
    # parent request from regions object should be propagated to resulting GeoDataFrame
    massachusetts = geodata.state_regions_builder('massachusetts').build()
    boston = geodata.city_regions_builder('boston').states(massachusetts).build()

    assert_row(boston.to_data_frame(), request='boston', state='massachusetts', county=NO_COLUMN, country=NO_COLUMN)
    assert_row(boston.centroids(), request='boston', state='massachusetts', county=NO_COLUMN, country=NO_COLUMN)


def test_list_of_regions_parents_in_regions_object_and_geo_data_frame():
    # parent request from regions object should be propagated to resulting GeoDataFrame
    states = geodata.state_regions_builder(['massachusetts', 'texas']).build()
    cities = geodata.city_regions_builder(['boston', 'austin']).states(states).build()

    assert_row(cities.to_data_frame(),
               request=['boston', 'austin'],
               state=['massachusetts', 'texas'],
               county=NO_COLUMN,
               country=NO_COLUMN
               )

    assert_row(cities.centroids(),
               request=['boston', 'austin'],
               state=['massachusetts', 'texas'],
               county=NO_COLUMN,
               country=NO_COLUMN
               )


def test_parents_lists():
    states = geodata.state_regions_builder(['texas', 'nevada']).countries(['usa', 'usa']).build()

    assert_row(states.to_data_frame(),
               request=['texas', 'nevada'],
               found_name=['Texas', 'Nevada'],
               country=['usa', 'usa']
               )


def test_with_drop_not_found():
    states = geodata.state_regions_builder(['texas', 'trololo', 'nevada']) \
        .countries(['usa', 'usa', 'usa']) \
        .drop_not_found() \
        .build()

    assert_row(states.to_data_frame(), request=['texas', 'nevada'], found_name=['Texas', 'Nevada'],
               country=['usa', 'usa'])
    assert_row(states.centroids(), request=['texas', 'nevada'], found_name=['Texas', 'Nevada'], country=['usa', 'usa'])
    assert_row(states.boundaries(), request=['texas', 'nevada'], found_name=['Texas', 'Nevada'], country=['usa', 'usa'])
    assert_row(states.limits(), request=['texas', 'nevada'], found_name=['Texas', 'Nevada'], country=['usa', 'usa'])


def test_drop_not_found_with_namesakes():
    states = geodata.county_regions_builder(['jefferson', 'trololo', 'jefferson']) \
        .states(['alabama', 'asd', 'arkansas']) \
        .countries(['usa', 'usa', 'usa']) \
        .drop_not_found() \
        .build()

    assert_row(states.to_data_frame(),
               request=['jefferson', 'jefferson'],
               found_name=['Jefferson County', 'Jefferson County'],
               state=['alabama', 'arkansas'],
               country=['usa', 'usa']
               )


def test_simple_scope():
    florida_with_country = geodata.regions_builder2('state', names=['florida', 'florida'], countries=['Uruguay', 'usa']) \
        .build() \
        .to_data_frame()

    assert florida_with_country[DF_ID][0] != florida_with_country[DF_ID][1]

    florida_with_scope = geodata.regions_builder2('state', names=['florida'], scope='Uruguay').build().to_data_frame()

    assert florida_with_country[DF_ID][0] == florida_with_scope[DF_ID][0]


def test_where():
    worcester = geodata.city_regions_builder('worcester').where('worcester', scope='massachusetts').build()

    assert_row(worcester.to_data_frame(), request='worcester', found_name='Worcester', id='3688419')


def test_where_near_point():
    worcester = geodata.city_regions_builder('worcester')\
        .where('worcester', near=Point(-71.00, 42.00)).build()

    assert_row(worcester.centroids(), lon=-71.8154652712922, lat=42.2678737342358)
    assert_row(worcester.to_data_frame(), request='worcester', found_name='Worcester', id='3688419')


def test_where_near_regions():
    boston = geodata.city_regions_builder('boston').build()
    worcester = geodata.city_regions_builder('worcester').where('worcester', near=boston).build()

    assert_row(worcester.to_data_frame(), request='worcester', found_name='Worcester', id='3688419')
    assert_row(worcester.centroids(), lon=-71.8154652712922, lat=42.2678737342358)


def test_where_within():
    worcester = geodata.city_regions_builder('worcester')\
        .where('worcester', within=box(-71.00, 42.00, -72.00, 43.00))\
        .build()

    assert_row(worcester.to_data_frame(), request='worcester', found_name='Worcester', id='3688419')
    assert_row(worcester.centroids(), lon=-71.8154652712922, lat=42.2678737342358)


def test_where_west_warwick():
    warwick = geodata.city_regions_builder('west warwick').states('rhode island') \
        .build()

    assert_row(warwick.to_data_frame(), request='west warwick', state='rhode island', found_name='West Warwick', id='382429')
    assert_row(warwick.centroids(), lon=-71.5257788638961, lat=41.6969098895788)


def test_query_scope_with_different_level_should_work():
    geodata.city_regions_builder(['moscow', 'worcester'])\
        .where('moscow', scope='russia')\
        .where('worcester', scope='massachusetts')\
        .build()


def test_error_level_detection_not_available():
    check_validation_error(
        "Level detection is not available with new API. Please, specify the level.",
        lambda: geodata.regions_builder2(names='boston', countries='usa').build()
    )

def test_error_us48_in_request_not_available():
    check_validation_error(
        "us-48 can't be used in requests with new API.",
        lambda: geodata.state_regions_builder('us-48').countries('usa').build()
    )


def test_error_us48_in_parent_not_available():
    check_validation_error(
        "us-48 can't be used in parents with new API.",
        lambda: geodata.state_regions_builder('boston').states('us-48').build()
    )


def test_asderror_us48_in_parent_not_available():
    geodata.regions_builder('city', 'boston').where('boston', within='us-48').build()
    geodata.city_regions_builder('boston').where('boston', scope='us-48').build()


def test_where_scope_with_existing_country():
    washington_county=geodata.county_regions_builder('Washington county').states('iowa').countries('usa').build()
    washington = geodata.city_regions_builder('washington').countries('United States of America')\
        .where('washington', country='United States of America', scope=washington_county)\
        .build()

    assert_row(washington.to_data_frame(), request='washington', country='United States of America', found_name='Washington')


def test_where_scope_with_existing_country_in_df():
    df = {
        'city': ['moscow', 'tashkent', 'washington'],
        'country': ['russia', 'uzbekistan', 'usa']
    }

    washington_county=geodata.county_regions_builder('Washington county').states('iowa').countries('usa').build()
    cities = geodata.city_regions_builder(df['city']).countries(df['country'])\
        .where('washington', country='usa', scope=washington_county)\
        .build()

    assert_row(cities.to_data_frame(), index=2, request='washington', country='usa', found_name='Washington')


def check_validation_error(message: str, action: Callable[[], Any]):
    assert isinstance(message, str)
    try:
        action()
        assert False, 'Validation error expected'
    except Exception as e:
        assert message == str(e)

