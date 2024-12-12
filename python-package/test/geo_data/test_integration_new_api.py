#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import pytest
from shapely.geometry import Point, box

import lets_plot.geo_data as geodata
from geo_data_test_util import assert_row, assert_error, NO_COLUMN, COLUMN_NAME_CITY
from lets_plot.geo_data import DF_COLUMN_FOUND_NAME, DF_COLUMN_ID, DF_COLUMN_COUNTRY, DF_COLUMN_STATE, DF_COLUMN_COUNTY, \
    DF_COLUMN_CENTROID, DF_COLUMN_POSITION, DF_COLUMN_LIMIT
from test_integration_with_geocoding_serever import TURN_OFF_INTEGRATION_TEST


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_all_columns_order():
    boston = geodata.geocode_cities('boston').counties('suffolk').states('massachusetts').countries('usa')
    assert boston.get_geocodes().columns.tolist() == \
           [DF_COLUMN_ID, COLUMN_NAME_CITY, DF_COLUMN_FOUND_NAME, DF_COLUMN_COUNTY, DF_COLUMN_STATE, DF_COLUMN_COUNTRY,
            DF_COLUMN_CENTROID, DF_COLUMN_POSITION, DF_COLUMN_LIMIT]

    gdf_columns = [COLUMN_NAME_CITY, DF_COLUMN_FOUND_NAME, DF_COLUMN_COUNTY, DF_COLUMN_STATE, DF_COLUMN_COUNTRY, 'geometry']
    assert boston.get_limits().columns.tolist() == gdf_columns
    assert boston.get_centroids().columns.tolist() == gdf_columns
    assert boston.get_boundaries().columns.tolist() == gdf_columns


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_do_not_add_unsued_parents_columns():
    moscow = geodata.geocode_cities('moscow').countries('russia')

    assert moscow.get_geocodes().columns.tolist() == [DF_COLUMN_ID, COLUMN_NAME_CITY, DF_COLUMN_FOUND_NAME, DF_COLUMN_COUNTRY,
                                                      DF_COLUMN_CENTROID, DF_COLUMN_POSITION, DF_COLUMN_LIMIT]

    gdf_columns = [COLUMN_NAME_CITY, DF_COLUMN_FOUND_NAME, DF_COLUMN_COUNTRY, 'geometry']
    assert moscow.get_limits().columns.tolist() == gdf_columns
    assert moscow.get_centroids().columns.tolist() == gdf_columns
    assert moscow.get_boundaries().columns.tolist() == gdf_columns


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_parents_in_regions_object_and_geo_data_frame():
    boston = geodata.geocode_cities('boston').counties('suffolk').states('massachusetts').countries('usa')

    assert_row(boston.get_geocodes(), names='boston', county='suffolk', state='massachusetts', country='usa')
    assert_row(boston.get_limits(), names='boston', county='suffolk', state='massachusetts', country='usa')
    assert_row(boston.get_centroids(), names='boston', county='suffolk', state='massachusetts', country='usa')
    assert_row(boston.get_boundaries(), names='boston', county='suffolk', state='massachusetts', country='usa')

    # antimeridian
    ru = geodata.geocode(level='country', names='russia')
    assert_row(ru.get_geocodes(), country='russia', city=NO_COLUMN, county=NO_COLUMN, state=NO_COLUMN)
    assert_row(ru.get_limits(), country=['russia', 'russia'], city=NO_COLUMN, county=NO_COLUMN, state=NO_COLUMN)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_regions_parents_in_regions_object_and_geo_data_frame():
    # parent request from regions object should be propagated to resulting GeoDataFrame
    massachusetts = geodata.geocode_states('massachusetts')
    boston = geodata.geocode_cities('boston').states(massachusetts)

    assert_row(boston.get_geocodes(), names='boston', state='massachusetts', county=NO_COLUMN, country=NO_COLUMN)
    assert_row(boston.get_centroids(), names='boston', state='massachusetts', county=NO_COLUMN, country=NO_COLUMN)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_list_of_regions_parents_in_regions_object_and_geo_data_frame():
    # parent request from regions object should be propagated to resulting GeoDataFrame
    states = geodata.geocode_states(['massachusetts', 'texas'])
    cities = geodata.geocode_cities(['boston', 'austin']).states(states)

    assert_row(cities.get_geocodes(),
               names=['boston', 'austin'],
               state=['massachusetts', 'texas'],
               county=NO_COLUMN,
               country=NO_COLUMN
               )

    assert_row(cities.get_geocodes(),
               names=['boston', 'austin'],
               state=['massachusetts', 'texas'],
               county=NO_COLUMN,
               country=NO_COLUMN
               )


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_parents_lists():
    states = geodata.geocode_states(['texas', 'nevada']).countries(['usa', 'usa'])

    assert_row(states.get_geocodes(),
               names=['texas', 'nevada'],
               found_name=['Texas', 'Nevada'],
               country=['usa', 'usa']
               )


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_with_drop_not_found():
    states = geodata.geocode_states(['texas', 'trololo', 'nevada']) \
        .countries(['usa', 'usa', 'usa']) \
        .ignore_not_found()

    assert_row(states.get_geocodes(), names=['texas', 'nevada'], found_name=['Texas', 'Nevada'], country=['usa', 'usa'])
    assert_row(states.get_centroids(), names=['texas', 'nevada'], found_name=['Texas', 'Nevada'], country=['usa', 'usa'])
    assert_row(states.get_boundaries(), names=['texas', 'nevada'], found_name=['Texas', 'Nevada'], country=['usa', 'usa'])
    assert_row(states.get_limits(), names=['texas', 'nevada'], found_name=['Texas', 'Nevada'], country=['usa', 'usa'])


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_drop_not_found_with_namesakes():
    states = geodata.geocode_counties(['jefferson', 'trololo', 'jefferson']) \
        .states(['alabama', 'asd', 'arkansas']) \
        .countries(['usa', 'usa', 'usa']) \
        .ignore_not_found()

    assert_row(states.get_geocodes(),
               names=['jefferson', 'jefferson'],
               found_name=['Jefferson County', 'Jefferson County'],
               state=['alabama', 'arkansas'],
               country=['usa', 'usa']
               )


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_simple_scope():
    florida_with_country = geodata.geocode(
        'state',
        names=['florida', 'florida'],
        countries=['Uruguay', 'usa']
    ).get_geocodes()

    assert florida_with_country[DF_COLUMN_ID][0] != florida_with_country[DF_COLUMN_ID][1]

    florida_with_scope = geodata.geocode(
        'state',
        names=['florida'],
        scope='Uruguay'
    ).get_geocodes()

    assert florida_with_country[DF_COLUMN_ID][0] == florida_with_scope[DF_COLUMN_ID][0]


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_where():
    worcester = geodata.geocode_cities('worcester').where('worcester', scope='massachusetts')

    assert_row(worcester.get_geocodes(), names='worcester', found_name='Worcester', id='158851900')


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_where_closest_to_point():
    worcester = geodata.geocode_cities('worcester').where('worcester', closest_to=Point(-71.00, 42.00))

    assert_row(worcester.get_centroids(), lon=-71.8154652712922, lat=42.2678737342358)
    assert_row(worcester.get_geocodes(), names='worcester', found_name='Worcester', id='158851900')


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_where_closest_to_regions():
    boston = geodata.geocode_cities('boston')
    worcester = geodata.geocode_cities('worcester').where('worcester', closest_to=boston)

    assert_row(worcester.get_geocodes(), names='worcester', found_name='Worcester', id='158851900')
    assert_row(worcester.get_centroids(), lon=-71.8154652712922, lat=42.2678737342358)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_where_scope():
    worcester = geodata.geocode_cities('worcester').where('worcester', scope=box(-71.00, 42.00, -72.00, 43.00))

    assert_row(worcester.get_geocodes(), names='worcester', found_name='Worcester', id='158851900')
    assert_row(worcester.get_centroids(), lon=-71.8154652712922, lat=42.2678737342358)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_where_west_warwick():
    warwick = geodata.geocode_cities('west warwick').states('rhode island')

    assert_row(warwick.get_geocodes(), names='west warwick', state='rhode island', found_name='West Warwick', id='158903676')
    assert_row(warwick.get_centroids(), lon=-71.5257788638961, lat=41.6969098895788)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_query_scope_with_different_level_should_work():
    geodata.geocode_cities(['moscow', 'worcester'])\
        .where('moscow', scope='russia')\
        .where('worcester', scope='massachusetts')\
        .get_geocodes()


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_error_with_scopeand_level_detection():
    assert_error(
        "Region is not found: blablabla",
        lambda: geodata.geocode(names='florida', scope='blablabla').get_geocodes()
    )


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_city_with_ambiguous_county_and_scope():
    assert_error(
        "Region is not found: worcester county",
        lambda: geodata.geocode_cities('worcester').counties('worcester county').scope('usa').get_geocodes()
    )


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_level_detection():
    geodata.geocode(names='boston', countries='usa').get_geocodes()


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_where_scope_with_existing_country():
    washington_county=geodata.geocode_counties('Washington county').states('iowa').countries('usa')
    washington = geodata.geocode_cities('washington').countries('United States of America')\
        .where('washington', country='United States of America', scope=washington_county)

    assert_row(washington.get_geocodes(), names='washington', country='United States of America', found_name='Washington')


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_where_scope_with_existing_country_in_df():
    df = {
        'city': ['moscow', 'tashkent', 'washington'],
        'country': ['russia', 'uzbekistan', 'usa']
    }

    washington_county=geodata.geocode_counties('Washington county').states('iowa').countries('usa')
    cities = geodata.geocode_cities(df['city']).countries(df['country'])\
        .where('washington', country='usa', scope=washington_county)

    assert_row(cities.get_geocodes(), index=2, names='washington', country='usa', found_name='Washington')


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_scope_with_level_detection_should_work():
    florida_uruguay = geodata.geocode(names='florida', scope='uruguay').get_geocodes()[DF_COLUMN_ID][0]
    florida_usa = geodata.geocode(names='florida', scope='usa').get_geocodes()[DF_COLUMN_ID][0]
    assert florida_usa != florida_uruguay, 'florida_usa({}) != florida_uruguay({})'.format(florida_usa, florida_uruguay)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_fetch_all_countries():
    countries = geodata.geocode_countries()
    df = countries.get_geocodes()
    assert len(df) == 218


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_fetch_all_counties_by_state():
    geodata.geocode_counties().states('New York').get_geocodes()


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_duplications_in_filter_should_preserve_order():
    states = geodata.geocode_states(['Texas', 'TX', 'Arizona', 'Texas']).get_geocodes()
    assert_row(
        states,
        names=['Texas', 'TX', 'Arizona', 'Texas'],
        found_name=['Texas', 'Texas', 'Arizona', 'Texas']
    )


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_select_all_query_with_empty_result_should_return_empty_dataframe():
    geocoder = geodata.geocode_counties().scope('vatican')

    geocodes = geocoder.get_geocodes()
    assert 0 == len(geocodes)

    centroids = geocoder.get_centroids()
    assert 0 == len(centroids)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_none_parents_at_diff_levels():
    warwick = geodata.geocode_cities('warwick').states('georgia').get_geocodes()
    worcester = geodata.geocode_cities('worcester').countries('uk').get_geocodes()

    cities = geodata.geocode_cities(['warwick', 'worcester'])\
        .states(['Georgia', None])\
        .countries([None, 'United Kingdom'])\
        .get_geocodes()

    assert_row(
        cities,
        names=['warwick', 'worcester'],
        id=[warwick.id[0], worcester.id[0]]
    )


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_where_with_parent():
    washington_county=geodata.geocode_counties('Washington county').states('Vermont').countries('usa')
    geodata.geocode_cities(['worcester', 'worcester']) \
        .countries(['usa', 'Great Britain']) \
        .where('worcester', country='usa', scope=washington_county) \
        .get_geocodes()


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_counties():
    counties = []
    states = []

    for state in geodata.geocode_states("us-48").get_geocodes()['found name']:
        for county in geodata.geocode_counties().states(state).scope('usa').get_geocodes()['found name']:
            states.append(state)
            counties.append(county)

    geocoded_counties = geodata.geocode_counties(counties).states(states).scope('usa').get_boundaries('country')

    assert_row(geocoded_counties, names=counties)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_request_in_ambiguous_df():
    warwick = geodata.geocode_cities('warwick').allow_ambiguous().get_geocodes()

    assert_row(warwick, names='warwick', found_name='Warwick')