#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import pytest
import shapely

import lets_plot.geo_data as geodata
from geo_data_test_util import run_intergration_tests, assert_row, assert_error, get_request_column_name, \
    assert_request_and_found_name_are_equal
from lets_plot.geo_data import DF_COLUMN_FOUND_NAME

ShapelyPoint = shapely.geometry.Point

BOSTON_ID = '158809705'
NYC_ID = '61785451'


TURN_OFF_INTEGRATION_TEST = not run_intergration_tests()


MOSCOW_LON = 37.620393
MOSCOW_LAT = 55.753960


@pytest.mark.parametrize('level,expected_name', [
    pytest.param('city', 'Москва', id='city-Moscow'),
    pytest.param('county', 'Центральный административный округ', id='county-Central administrative district'),
    pytest.param('country', 'Россия', id='Russian Federeation')
])
@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_reverse_moscow(level, expected_name):
    r = geodata.reverse_geocode(lon=MOSCOW_LON, lat=MOSCOW_LAT, level=level)
    assert_row(r.get_geocodes(), found_name=expected_name)


@pytest.mark.parametrize('geometry_getter', [
    pytest.param(lambda regions_obj: regions_obj.get_centroids(), id='centroids()'),
    pytest.param(lambda regions_obj: regions_obj.get_limits(), id='limits()'),
    pytest.param(lambda regions_obj: regions_obj.get_boundaries(5), id='boundaries(5)'),
    pytest.param(lambda regions_obj: regions_obj.get_boundaries(), id='boundaries()')
])
@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_name_columns(geometry_getter):
    request = 'boston'
    found_name = 'Boston'

    boston = geodata.geocode_cities(request)

    assert_row(boston.get_geocodes(), names=request, found_name=found_name)
    assert_row(geometry_getter(boston), names=request, found_name=found_name)


@pytest.mark.parametrize('geometry_getter', [
    pytest.param(lambda regions_obj: regions_obj.get_centroids(), id='centroids()'),
    pytest.param(lambda regions_obj: regions_obj.get_limits(), id='limits()'),
    pytest.param(lambda regions_obj: regions_obj.get_boundaries(5), id='boundaries(5)'),
    pytest.param(lambda regions_obj: regions_obj.get_boundaries(), id='boundaries()')
])
@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_empty_request_name_columns(geometry_getter):
    request = 'Maine'
    found_name = 'Maine'

    states = geodata.geocode_states('us-48')

    assert_row(states.get_geocodes(), names=request, found_name=found_name)
    assert_row(geometry_getter(states), names=request, found_name=found_name)


BOSTON_LON = -71.057083
BOSTON_LAT = 42.361145

NYC_LON = -73.935242
NYC_LAT = 40.730610


@pytest.mark.parametrize('lons, lats', [
    pytest.param(geodata.Series([BOSTON_LON, NYC_LON]), geodata.Series([BOSTON_LAT, NYC_LAT])),
    pytest.param([BOSTON_LON, NYC_LON], [BOSTON_LAT, NYC_LAT])
])
@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_reverse_geocoding_of_list_(lons, lats):
    r = geodata.reverse_geocode(lons, lats, 'city')
    assert_row(r.get_geocodes(), index=0, names='[-71.057083, 42.361145]', found_name='Boston')
    assert_row(r.get_geocodes(), index=1, names='[-73.935242, 40.73061]', found_name='New York')


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_reverse_geocoding_of_nyc():
    r = geodata.reverse_geocode(NYC_LON, NYC_LAT, 'city')

    assert_row(r.get_geocodes(), found_name='New York')


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_reverse_geocoding_of_nothing():
    try:
        geodata.reverse_geocode(-30.0, -30.0, 'city').get_geocodes()
    except ValueError as e:
        assert str(e).startswith('No objects were found for [-30.000000, -30.000000].\n')
        return

    assert False, 'Should fail with nothing found exceptuion'


SEVASTOPOL_LON = 33.5224
SEVASTOPOL_LAT = 44.58883
SEVASTOPOL_ID = '3030976'


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_only_one_sevastopol():
    sevastopol = geodata.reverse_geocode(SEVASTOPOL_LON, SEVASTOPOL_LAT, 'city')

    assert_row(sevastopol.get_geocodes(), id=SEVASTOPOL_ID)


WARWICK_LON = -71.4332938210472
WARWICK_LAT = 41.715542525053
WARWICK_ID = '158863860'


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_ambiguity_closest_to_boston_by_name():
    r = geodata.geocode(
        level='city',
        names='Warwick'
    ) \
        .where('Warwick', closest_to=geodata.geocode_cities('boston'))

    assert_row(r.get_geocodes(), id=WARWICK_ID, found_name='Warwick')
    assert_row(r.get_centroids(), lon=WARWICK_LON, lat=WARWICK_LAT)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_ambiguity_closest_to_boston_by_coord():
    r = geodata.geocode(
        level='city',
        names='Warwick'
    ) \
        .where('Warwick', closest_to=ShapelyPoint(BOSTON_LON, BOSTON_LAT))

    assert_row(r.get_geocodes(), id=WARWICK_ID, found_name='Warwick')
    assert_row(r.get_centroids(), lon=WARWICK_LON, lat=WARWICK_LAT)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_ambiguity_scope_boston_by_box():
    boston = geodata.geocode_cities('boston').get_centroids().iloc[[0]]
    buffer = 0.6
    boston_centroid = ShapelyPoint(boston.geometry.x, boston.geometry.y)

    r = geodata.geocode(
        level='city',
        names='Warwick'
    ) \
        .where('Warwick',
               scope=shapely.geometry.box(
                   boston_centroid.x - buffer,
                   boston_centroid.y - buffer,
                   boston_centroid.x + buffer,
                   boston_centroid.y + buffer
               ))

    assert_row(r.get_geocodes(), id=WARWICK_ID, found_name='Warwick')
    assert_row(r.get_centroids(), lon=WARWICK_LON, lat=WARWICK_LAT)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_ambiguity_allow_ambiguous():
    r = geodata.geocode_cities(['gotham', 'new york', 'manchester']) \
        .allow_ambiguous() \
        .get_geocodes()

    actual = r[DF_COLUMN_FOUND_NAME].tolist()
    assert 30 == len(actual)  # 1 New York + 27 Manchester


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_ambiguity_drop_not_matched():
    r = geodata.geocode_cities(['gotham', 'new york', 'manchester']) \
        .ignore_all_errors() \
        .get_geocodes()

    actual = r[DF_COLUMN_FOUND_NAME].tolist()
    assert actual == ['New York']


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_ambiguity_drop_not_found():
    try:
        r = geodata.geocode_cities(['gotham', 'new york', 'manchester']) \
            .ignore_not_found() \
            .get_geocodes()
    except ValueError as ex:
        str(ex).startswith('Multiple objects (27) were found for manchester')
        return

    assert False, 'Should throw exception'


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_single_request_level_detection():
    r = geodata.geocode(names=['new york', 'boston']).scope('usa').get_geocodes()

    assert r.id.tolist() == [NYC_ID, BOSTON_ID]


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_where_request_level_detection():
    """
    where('new york', region=geodata.geocode_states('new york')) gives county as first detected level
    where('boston', region=geodata.geocode_countries('usa')) gives city as first detected level
    But 'new york' also matches a city name so common level should be a city
    """
    r = geodata.geocode(names=['new york', 'boston']) \
        .where('new york', scope=geodata.geocode_states('new york')) \
        .where('boston', scope=geodata.geocode_countries('usa')) \
        .get_geocodes()

    assert [NYC_ID, BOSTON_ID] == r.id.tolist()


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_positional_regions():
    df = geodata.geocode_cities(['york', 'york']).states(['New York', 'Illinois']).get_geocodes()

    assert ['New York', 'Little York'] == df['found name'].tolist()


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_duplications():
    r1 = geodata.geocode(names=['Virginia', 'West Virginia'], scope='USA')
    r1.get_centroids()


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_limits_request():
    print(geodata.geocode(names='texas').get_limits())


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_centroids_request():
    print(geodata.geocode(names='texas').get_centroids())


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_polygon_boundaries_request():
    print(geodata.geocode(names='colorado').get_boundaries(14))


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_multipolygon_boundaries_request():
    assert geodata.geocode(names='USA').get_boundaries(1) is not None


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_regions():
    countries_geocoder = geodata.geocode(level='country', names=['Russia', 'USA'])
    countries_geocoder.get_boundaries()
    assert countries_geocoder is not None


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_mapregion():
    usa = geodata.geocode_countries(names='USA')
    print(usa.get_centroids())


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_geocoderegion_as_region():
    usa = geodata.geocode_countries(names=['usa'])
    states_list = ['NY', 'TX', 'NV']
    geodata.geocode_states(names=states_list).scope(usa).get_geocodes()


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_error_response():
    with pytest.raises(ValueError) as exception:
        geodata.geocode_countries(names='blablabla').get_centroids()

    assert 'No objects were found for blablabla.\n' == exception.value.args[0]


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_rows_order():
    city_names = ['Boston', 'Phoenix', 'Tucson', 'Salt Lake City', 'Los Angeles', 'San Francisco']
    city_regions = geodata.geocode_cities(city_names).scope('US')

    # create path preserving the order
    df = city_regions.get_centroids()

    df = df.set_index(get_request_column_name(df))
    df = df.reindex(city_names)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_new_server():
    c = geodata.geocode_countries(names='USA')
    print(c.get_centroids())
    print(c)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_case():
    usa = geodata.geocode_countries(names=['usa'])
    states_48 = geodata.geocode_states(['us-48'])

    states_list = ['NY', 'TX', 'louisiana']
    states = geodata.geocode_states(names=states_list).scope(usa)

    cities_list = ['New york', 'boston', 'la']
    t_cities = geodata.geocode_cities(names=cities_list).scope(usa)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_ambiguous_not_found_with_level():
    with pytest.raises(ValueError) as exception:
        r = geodata.geocode(names=['zimbabwe', 'moscow'], level='country').get_geocodes()
    assert 'No objects were found for moscow.\n' == exception.value.args[0]


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_order():
    bound = geodata.geocode(names=['Russia', 'USA', 'France', 'Japan'])
    assert_row(bound.get_geocodes(), names=['Russia', 'USA', 'France', 'Japan'])


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_resolution():
    r = geodata.geocode(names=['monaco', ], level='country')
    sizes = []
    for res in range(1, 16):
        b = r.get_boundaries(res)
        sizes.append(len(b))

    assert 15 == len(sizes)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_should_copy_found_name_to_request_for_us48():
    df = geodata.geocode_states('us-48').get_geocodes()

    assert len(df) == 49
    assert_request_and_found_name_are_equal(df)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_us48_in_scope():
    df = geodata.geocode_states().scope('us-48').get_geocodes()

    assert 49 == len(df)
    assert_request_and_found_name_are_equal(df)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_us48_in_name_without_level():
    df = geodata.geocode(names='us-48').get_geocodes()

    assert 49 == len(df)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_duplication_with_us48():
    df = geodata.geocode_states(names=['tx', 'us-48', 'tx']).get_geocodes()

    assert 51 == len(df)
    assert_row(df, names='tx', found_name='Texas', index=0)
    assert_row(df, names='Maine', found_name='Maine', index=1)
    assert_row(df, names='tx', found_name='Texas', index=50)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_empty_request_get_geocodes():
    orange_county = geodata.geocode_counties('orange county').scope('north carolina')
    r = geodata.geocode_cities().scope(orange_county)
    df = r.get_geocodes()
    assert_request_and_found_name_are_equal(df)


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_empty_request_centroid():
    orange_county = geodata.geocode_counties('orange county').scope('north carolina')
    r = geodata.geocode_cities().scope(orange_county)
    df = r.get_centroids()
    assert_request_and_found_name_are_equal(df)



@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_highlights():
    r = geodata.geocode(level='city', names='NYC').highlights(True)
    df = r.get_geocodes()
    assert_row(df, found_name='New York')
    assert df['highlights'].tolist() == [['NYC']]


@pytest.mark.skipif(TURN_OFF_INTEGRATION_TEST, reason='Need proper server ip')
def test_not_found_scope():
    assert_error(
        "Region is not found: blablabla",
        lambda: geodata.geocode(names=['texas'], scope='blablabla').get_geocodes()
    )