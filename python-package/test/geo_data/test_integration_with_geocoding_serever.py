#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import os
from typing import List

import pytest
import shapely
from pandas import DataFrame
from shapely.geometry import Point

import lets_plot.geo_data as geodata
from lets_plot.geo_data import DF_FOUND_NAME, DF_ID, DF_REQUEST, DF_PARENT_COUNTRY, DF_PARENT_STATE, DF_PARENT_COUNTY

ShapelyPoint = shapely.geometry.Point

BOSTON_ID = '4631409'
NYC_ID = '351811'


def run_intergration_tests() -> bool:
    if 'RUN_GEOCODING_INTEGRATION_TEST' in os.environ.keys():
        return os.environ.get('RUN_GEOCODING_INTEGRATION_TEST').lower() == 'true'
    return False


def use_local_server():
    old = os.environ.copy()
    os.environ.update({'GEOSERVER_URL': 'http://localhost:3012', **old})


def assert_found_names(df: DataFrame, names: List[str]):
    assert names == df[DF_FOUND_NAME].tolist()


def assert_row(df: DataFrame, request: str = None, found_name: str = None, index=0, id=None, lon=None, lat=None):
    if request is not None:
        assert df[DF_REQUEST][index] == request

    if found_name is not None:
        assert df[DF_FOUND_NAME][index] == found_name

    if id is not None:
        assert df[DF_ID][index] == id

    if lon is not None:
        actual_lon = ShapelyPoint(df.geometry[index]).x
        assert actual_lon == lon

    if lat is not None:
        actual_lat = ShapelyPoint(df.geometry[index]).y
        assert actual_lat == lat


TURN_OFF_INTERACTION_TEST = not run_intergration_tests()

DO_NOT_DROP = False
NO_ERROR = None
NOT_FOUND = None


@pytest.mark.parametrize('address,drop_not_found,found,error', [
    pytest.param(['NYC, NY', 'Dallas, TX'], DO_NOT_DROP, ['New York City', 'Dallas'], NO_ERROR),
    pytest.param(['NYC, NY', 'foobar, barbaz'], DO_NOT_DROP, NOT_FOUND, 'No objects were found for barbaz.\n'),
])
@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_missing_address(address, drop_not_found, found, error):
    # use_local_server()
    builder = geodata.regions_builder(level='city', request=address, within='usa')
    if drop_not_found:
        builder.drop_not_found()

    if error is not None:
        try:
            builder.build()
        except ValueError as e:
            assert str(e).startswith(error)
    else:
        r = builder.build()
        assert_found_names(r.to_data_frame(), found)


NO_LEVEL = None
NO_REGION = None


@pytest.mark.parametrize('address,level,region,expected_name', [
    pytest.param('moscow, Latah County, Idaho, USA', NO_LEVEL, NO_REGION, 'Moscow'),
    # TODO: CHECK -  pytest.param('richmond, virginia, usa', NO_LEVEL, NO_REGION, 'Richmond City'),
    # TODO: CHECK - pytest.param('richmond, virginia, usa', 'county', NO_REGION, 'Richmond County'),
    pytest.param('NYC, usa', NO_LEVEL, NO_REGION, 'New York City'),
    pytest.param('NYC, NY', NO_LEVEL, 'usa', 'New York City'),
    pytest.param('dallas, TX', NO_LEVEL, NO_REGION, 'Dallas'),
    pytest.param('moscow, russia', NO_LEVEL, NO_REGION, 'Москва'),
])
@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_address_request(address, level, region, expected_name):
    r = geodata.regions(request=address, level=level, within=region)
    assert_row(r.to_data_frame(), found_name=expected_name)


MOSCOW_LON = 37.620393
MOSCOW_LAT = 55.753960


@pytest.mark.parametrize('level,expected_name', [
    pytest.param('city', 'Москва', id='city-Moscow'),
    pytest.param('county', 'Центральный административный округ', id='county-Central administrative district'),
    pytest.param('country', 'Россия', id='Russian Federeation')
])
@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_reverse_moscow(level, expected_name):
    r = geodata.regions_xy(lon=MOSCOW_LON, lat=MOSCOW_LAT, level=level)
    assert_row(r.to_data_frame(), found_name=expected_name)


@pytest.mark.parametrize('geometry_getter', [
    pytest.param(lambda regions_obj: regions_obj.centroids(), id='centroids()'),
    pytest.param(lambda regions_obj: regions_obj.limits(), id='limits()'),
    pytest.param(lambda regions_obj: regions_obj.boundaries(5), id='boundaries(5)'),
    pytest.param(lambda regions_obj: regions_obj.boundaries(), id='boundaries()')
])
@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_name_columns(geometry_getter):
    request = 'boston'
    found_name = 'Boston'

    boston = geodata.regions_city(request)

    assert_row(boston.to_data_frame(), request=request, found_name=found_name)
    assert_row(geometry_getter(boston), request=request, found_name=found_name)


@pytest.mark.parametrize('geometry_getter', [
    pytest.param(lambda regions_obj: regions_obj.centroids(), id='centroids()'),
    pytest.param(lambda regions_obj: regions_obj.limits(), id='limits()'),
    pytest.param(lambda regions_obj: regions_obj.boundaries(5), id='boundaries(5)'),
    pytest.param(lambda regions_obj: regions_obj.boundaries(), id='boundaries()')
])
#@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_empty_request_name_columns(geometry_getter):
    request = 'Missouri'
    found_name = 'Missouri'

    states = geodata.regions_state('us-48')

    assert_row(states.to_data_frame(), request=request, found_name=found_name)
    assert_row(geometry_getter(states), request=request, found_name=found_name)


BOSTON_LON = -71.057083
BOSTON_LAT = 42.361145

NYC_LON = -73.935242
NYC_LAT = 40.730610


@pytest.mark.parametrize('lons, lats', [
    pytest.param(geodata.Series([BOSTON_LON, NYC_LON]), geodata.Series([BOSTON_LAT, NYC_LAT])),
    pytest.param([BOSTON_LON, NYC_LON], [BOSTON_LAT, NYC_LAT])
])
@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_reverse_geocoding_of_list_(lons, lats):
    r = geodata.regions_xy(lons, lats, 'city')
    assert_row(r.to_data_frame(), index=0, request='[-71.057083, 42.361145]', found_name='Boston')
    assert_row(r.to_data_frame(), index=1, request='[-73.935242, 40.730610]', found_name='New York City')


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_reverse_geocoding_of_nyc():
    r = geodata.regions_xy(NYC_LON, NYC_LAT, 'city')

    assert_row(r.to_data_frame(), found_name='New York City')


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_reverse_geocoding_of_nothing():
    try:
        geodata.regions_xy(-30.0, -30.0, 'city')
    except ValueError as e:
        assert str(e).startswith('No objects were found for [-30.000000, -30.000000].\n')
        return

    assert False, 'Should fail with nothing found exceptuion'


SEVASTOPOL_LON = 33.5224
SEVASTOPOL_LAT = 44.58883
SEVASTOPOL_ID = '6061953'


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_only_one_sevastopol():
    sevastopol = geodata.regions_xy(SEVASTOPOL_LON, SEVASTOPOL_LAT, 'city')

    assert_row(sevastopol.to_data_frame(), id=SEVASTOPOL_ID)


WARWICK_LON = -71.4332743004962
WARWICK_LAT = 41.7155512422323
WARWICK_ID = '785807'


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_ambiguity_near_boston_by_name():
    r = geodata.regions_builder(
        level='city',
        request='Warwick'
    ) \
        .where('Warwick', near=geodata.regions_city('boston')) \
        .build()

    assert_row(r.to_data_frame(), id=WARWICK_ID, found_name='Warwick')
    assert_row(r.centroids(), lon=WARWICK_LON, lat=WARWICK_LAT)


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_ambiguity_near_boston_by_coord():
    r = geodata.regions_builder(
        level='city',
        request='Warwick'
    ) \
        .where('Warwick', near=ShapelyPoint(BOSTON_LON, BOSTON_LAT)) \
        .build()

    assert_row(r.to_data_frame(), id=WARWICK_ID, found_name='Warwick')
    assert_row(r.centroids(), lon=WARWICK_LON, lat=WARWICK_LAT)


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_ambiguity_near_boston_by_box():
    boston = geodata.regions_city('boston').centroids().iloc[[0]]
    buffer = 0.6
    boston_centroid = ShapelyPoint(boston.geometry.x, boston.geometry.y)

    r = geodata.regions_builder(
        level='city',
        request='Warwick'
    ) \
        .where('Warwick',
               within=shapely.geometry.box(
                   boston_centroid.x - buffer,
                   boston_centroid.y - buffer,
                   boston_centroid.x + buffer,
                   boston_centroid.y + buffer
               )) \
        .build()

    assert_row(r.to_data_frame(), id=WARWICK_ID, found_name='Warwick')
    assert_row(r.centroids(), lon=WARWICK_LON, lat=WARWICK_LAT)


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_ambiguity_allow_ambiguous():
    r = geodata.regions_builder(level='city', request=['gotham', 'new york city', 'manchester']) \
        .allow_ambiguous() \
        .build()

    actual = r.to_data_frame()[DF_FOUND_NAME].tolist()
    assert 28 == len(actual)  # 1 New York City + 27 Manchester


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_ambiguity_drop_not_matched():
    r = geodata.regions_builder(level='city', request=['gotham', 'new york city', 'manchester']) \
        .drop_not_matched() \
        .build()

    actual = r.to_data_frame()[DF_FOUND_NAME].tolist()
    assert ['New York City'] == actual


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_ambiguity_drop_not_found():
    try:
        r = geodata.regions_builder(level='city', request=['gotham', 'new york city', 'manchester']) \
            .drop_not_found() \
            .build()
    except ValueError as ex:
        str(ex).startswith('Multiple objects (27) were found for manchester')
        return

    assert False, 'Should throw exception'


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_single_request_level_detection():
    r = geodata.regions_builder(request=['new york city', 'boston']) \
        .build()

    assert [NYC_ID, BOSTON_ID] == r.to_data_frame().id.tolist()


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_where_request_level_detection():
    """
    where('new york', region=geodata.regions_state('new york')) gives county as first detected level
    where('boston', region=geodata.regions_country('usa')) gives city as first detected level
    But 'new york' also matches a city name so common level should be a city
    """
    r = geodata.regions_builder(request=['new york', 'boston']) \
        .where('new york', within=geodata.regions_state('new york')) \
        .where('boston', within=geodata.regions_country('usa')) \
        .build()

    assert [NYC_ID, BOSTON_ID] == r.to_data_frame().id.tolist()


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_havana_new_york():
    try:
        r = geodata.regions_builder(request=['havana', 'new york city']) \
            .where(request='havana', within=geodata.regions_country('cuba')) \
            .where(request='new york city', within=geodata.regions_state('new york')) \
            .build()
    except ValueError as ex:
        assert 'No objects were found for new york city.\n' == str(ex)
        return

    assert False, 'Should throw exception'


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_positional_regions():
    df = geodata.regions_city(
        request=['york', 'york'],
        within=[
            geodata.regions_state(['New York']),
            geodata.regions_state(['Illinois']),
        ]
    ).to_data_frame()

    assert ['New York City', 'Little York'] == df['found name'].tolist()


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_region_us48():
    df = geodata.regions_state(within='us-48').to_data_frame()
    assert 49 == len(df['request'].tolist())
    for state in df.request:
        assert len(state) > 0


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_filter_us48():
    df = geodata.regions_state(request='us-48').to_data_frame()
    assert 49 == len(df['request'].tolist())
    for state in df.request:
        assert len(state) > 0


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_duplications():
    r1 = geodata.regions(request=['Virginia', 'West Virginia'], within='USA')
    r1.centroids()


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_limits_request():
    print(geodata.regions(request='texas').limits())


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_centroids_request():
    print(geodata.regions(request='texas').centroids())


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_polygon_boundaries_request():
    print(geodata.regions(request='colorado').boundaries(14))


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_multipolygon_boundaries_request():
    assert geodata.regions(request='USA').boundaries(1) is not None


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_regions():
    map_regions = geodata.regions(level='country', request=['Russia', 'USA'])
    map_regions.boundaries()
    assert map_regions is not None


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_mapregion():
    usa: geodata.Regions = geodata.regions_country(request='USA')
    print(usa.centroids())


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_geocoderegion_as_region():
    usa = geodata.regions_country(request=['usa'])
    states_list = ['NY', 'TX', 'NV']
    geodata.regions_state(request=states_list, within=usa)


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_error_response():
    with pytest.raises(ValueError) as exception:
        geodata.regions_country(request='blablabla').centroids()

    assert 'No objects were found for blablabla.\n' == exception.value.args[0]


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_rows_order():
    city_names = ['Boston', 'Phoenix', 'Tucson', 'Salt Lake City', 'Los Angeles', 'San Francisco']
    city_regions = geodata.regions_city(city_names, within='US')

    # create path preserving the order
    df = city_regions.centroids()

    df = df.set_index('request')
    df = df.reindex(city_names)


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_new_server():
    c = geodata.regions_country(request='USA')
    print(c.centroids())
    print(c)


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_case():
    usa = geodata.regions_country(request=['usa'])
    states_48 = geodata.regions_state(['us-48'])

    states_list = ['NY', 'TX', 'louisiana']
    states = geodata.regions_state(request=states_list, within=usa)

    cities_list = ['New york', 'boston', 'la']
    t_cities = geodata.regions_city(request=cities_list, within=usa)


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_ambiguous_not_found_with_level():
    with pytest.raises(ValueError) as exception:
        r = geodata.regions(request=['zimbabwe', 'moscow'], level='country')
    assert 'No objects were found for moscow.\n' == exception.value.args[0]


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_order():
    bound = geodata.regions(request=['Russia', 'USA', 'France', 'Japan'])
    df = bound.to_data_frame()
    assert ['Russia', 'USA', 'France', 'Japan'] == df['request'].tolist()


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_resolution():
    r = geodata.regions(request=['monaco', ], level='country')
    sizes = []
    for res in range(1, 16):
        b = r.boundaries(res)
        sizes.append(len(b['request']))

    assert 15 == len(sizes)


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_duplications_in_filter_should_preserve_order():
    df = geodata.regions(request=['Texas', 'TX', 'Arizona', 'Texas'], level='state').to_data_frame()
    assert ['Texas', 'TX', 'Arizona', 'Texas'] == df['request'].tolist()


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_duplication_with_us48():
    df = geodata.regions_state(request=['tx', 'us-48', 'tx']).to_data_frame()

    assert 51 == len(df['request'])
    assert_row(df, 'tx', 'Texas', 0)
    assert_row(df, 'Missouri', 'Missouri', 1)
    assert_row(df, 'tx', 'Texas', 50)


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_empty_request_to_data_frame():
    r = geodata.regions_city(within='orange county')
    df = r.to_data_frame()
    assert set(['Chapel Hill', 'Town of Carrboro', 'Carrboro', 'Hillsborough', 'Town of Carrboro', 'City of Durham']) == \
           set(df['request'].tolist())


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_empty_request_centroid():
    r = geodata.regions_city(within='orange county')
    df = r.centroids()
    assert set(['Chapel Hill', 'Town of Carrboro', 'Carrboro', 'Hillsborough', 'Town of Carrboro', 'City of Durham']) == \
           set(df['request'].tolist())


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_highlights():
    r = geodata.regions_builder(level='city', request='NY', highlights=True).build()
    df = r.to_data_frame()
    assert ['Peel'] == df['found name'].tolist()
    assert [['Purt ny h-Inshey']] == df['highlights'].tolist()


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_countries():
    assert 221 == len(geodata.regions_country().centroids().request)


@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_incorrect_group_processing():
    c = geodata.regions_country().centroids()
    c = list(c.request[141:142]) + list(c.request[143:144]) + list(c.request[136:137]) + list(c.request[114:134])
    print(c)
    c = geodata.regions_country(c).centroids()
    r = geodata.regions_country(c['request'])
    boundaries: DataFrame = r.boundaries(resolution=10)

    assert 'group' not in boundaries.keys()


#@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_parents_in_regions_object_and_geo_data_frame():
    tx = geodata.regions_builder2(level='city', names='boston', counties='suffolk', states='massachusetts', countries='usa').build()

    tx_df = tx.to_data_frame()

    # Test columns order
    assert tx_df.columns.tolist() == [DF_ID, DF_REQUEST, DF_FOUND_NAME, DF_PARENT_COUNTY, DF_PARENT_STATE, DF_PARENT_COUNTRY]

    assert tx_df[DF_REQUEST][0] == 'boston'
    assert tx_df[DF_PARENT_COUNTY][0] == 'suffolk'
    assert tx_df[DF_PARENT_STATE][0] == 'massachusetts'
    assert tx_df[DF_PARENT_COUNTRY][0] == 'usa'

    tx_gdf = tx.limits()
    assert tx_gdf.columns.tolist() == [DF_REQUEST, DF_FOUND_NAME, DF_PARENT_COUNTY, DF_PARENT_STATE, DF_PARENT_COUNTRY, 'geometry']
    assert tx_gdf[DF_REQUEST][0] == 'boston'
    assert tx_gdf[DF_PARENT_COUNTY][0] == 'suffolk'
    assert tx_gdf[DF_PARENT_STATE][0] == 'massachusetts'
    assert tx_gdf[DF_PARENT_COUNTRY][0] == 'usa'

    tx_gdf = tx.centroids()
    assert tx_gdf.columns.tolist() == [DF_REQUEST, DF_FOUND_NAME, DF_PARENT_COUNTY, DF_PARENT_STATE, DF_PARENT_COUNTRY, 'geometry']
    assert tx_gdf[DF_REQUEST][0] == 'boston'
    assert tx_gdf[DF_PARENT_COUNTY][0] == 'suffolk'
    assert tx_gdf[DF_PARENT_STATE][0] == 'massachusetts'
    assert tx_gdf[DF_PARENT_COUNTRY][0] == 'usa'

    tx_gdf = tx.boundaries()
    assert tx_gdf.columns.tolist() == [DF_REQUEST, DF_FOUND_NAME, DF_PARENT_COUNTY, DF_PARENT_STATE, DF_PARENT_COUNTRY, 'geometry']
    assert tx_gdf[DF_REQUEST][0] == 'boston'
    assert tx_gdf[DF_PARENT_COUNTY][0] == 'suffolk'
    assert tx_gdf[DF_PARENT_STATE][0] == 'massachusetts'
    assert tx_gdf[DF_PARENT_COUNTRY][0] == 'usa'

    # antimeridian
    ru = geodata.regions_builder2(level='country', names='russia').build()
    ru_df = ru.to_data_frame()
    assert ru_df.columns.tolist() == [DF_ID, DF_REQUEST, DF_FOUND_NAME]

    ru_gdf = ru.limits()
    assert ru_gdf[DF_REQUEST][0] == 'russia'
    assert ru_gdf[DF_REQUEST][1] == 'russia'
    assert ru_gdf.columns.tolist() == [DF_REQUEST, DF_FOUND_NAME, 'geometry']


#@pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_regions_parents_in_regions_object_and_geo_data_frame():
    ms = geodata.regions_builder2(level='state', names='massachusetts').build()
    boston = geodata.regions_builder2(level='city', names='boston', states=ms).build()

    boston_df = boston.to_data_frame()
    print(boston_df[DF_PARENT_STATE][0])
    assert boston_df[DF_PARENT_STATE][0] == 'massachusetts'