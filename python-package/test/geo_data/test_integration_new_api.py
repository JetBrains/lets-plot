#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import lets_plot.geo_data as geodata
from lets_plot.geo_data import DF_FOUND_NAME, DF_ID, DF_REQUEST, DF_PARENT_COUNTRY, DF_PARENT_STATE, DF_PARENT_COUNTY
from .geo_data import assert_row, NO_COLUMN


def test_all_columns_order():
    boston = geodata.regions_builder2(level='city', names='boston', counties='suffolk', states='massachusetts',
                                      countries='usa').build()
    assert boston.to_data_frame().columns.tolist() == [DF_ID, DF_REQUEST, DF_FOUND_NAME, DF_PARENT_COUNTY, DF_PARENT_STATE, DF_PARENT_COUNTRY]

    gdf_columns = [DF_REQUEST, DF_FOUND_NAME, DF_PARENT_COUNTY, DF_PARENT_STATE, DF_PARENT_COUNTRY, 'geometry']
    assert boston.limits().columns.tolist() == gdf_columns
    assert boston.centroids().columns.tolist() == gdf_columns
    assert boston.boundaries().columns.tolist() == gdf_columns


def test_do_not_add_unsued_parents_columns():
    moscow = geodata.regions_builder2(level='city', names='moscow', countries='russia').build()

    assert moscow.to_data_frame().columns.tolist() == [DF_ID, DF_REQUEST, DF_FOUND_NAME, DF_PARENT_COUNTRY]

    gdf_columns = [DF_REQUEST, DF_FOUND_NAME, DF_PARENT_COUNTRY, 'geometry']
    assert moscow.limits().columns.tolist() == gdf_columns
    assert moscow.centroids().columns.tolist() == gdf_columns
    assert moscow.boundaries().columns.tolist() == gdf_columns


# @pytest.mark.skipif(TURN_OFF_INTERACTION_TEST, reason='Need proper server ip')
def test_parents_in_regions_object_and_geo_data_frame():
    boston = geodata.regions_builder2(level='city', names='boston', counties='suffolk', states='massachusetts',
                                      countries='usa').build()

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
    massachusetts = geodata.regions_builder2(level='state', names='massachusetts').build()
    boston = geodata.regions_builder2(level='city', names='boston', states=massachusetts).build()

    assert_row(boston.to_data_frame(), request='boston', state='massachusetts', county=NO_COLUMN, country=NO_COLUMN)
    assert_row(boston.centroids(), request='boston', state='massachusetts', county=NO_COLUMN, country=NO_COLUMN)


def test_list_of_regions_parents_in_regions_object_and_geo_data_frame():
    # parent request from regions object should be propagated to resulting GeoDataFrame
    states = geodata.regions_builder2(level='state', names=['massachusetts', 'texas']).build()
    cities = geodata.regions_builder2(level='city', names=['boston', 'austin'], states=states).build()

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
    states = geodata.regions_builder2(level='state', names=['texas', 'nevada'], countries=['usa', 'usa']).build()

    assert_row(states.to_data_frame(),
               request=['texas', 'nevada'],
               found_name=['Texas', 'Nevada'],
               country=['usa', 'usa']
               )


def test_with_drop_not_found():
    states = geodata.regions_builder2(
        level='state',
        names=['texas', 'trololo', 'nevada'],
        countries=['usa', 'usa', 'usa']
    ) \
        .drop_not_found() \
        .build()

    assert_row(states.to_data_frame(), request=['texas', 'nevada'], found_name=['Texas', 'Nevada'], country=['usa', 'usa'])
    assert_row(states.centroids(), request=['texas', 'nevada'], found_name=['Texas', 'Nevada'], country=['usa', 'usa'])
    assert_row(states.boundaries(), request=['texas', 'nevada'], found_name=['Texas', 'Nevada'], country=['usa', 'usa'])
    assert_row(states.limits(), request=['texas', 'nevada'], found_name=['Texas', 'Nevada'], country=['usa', 'usa'])


def test_drop_not_found_with_namesakes():
    states = geodata.regions_builder2(
        level='county',
        names=['jefferson', 'trololo', 'jefferson'],
        states=['alabama', 'asd', 'arkansas'],
        countries=['usa', 'usa', 'usa']
    ) \
        .drop_not_found() \
        .build()

    assert_row(states.to_data_frame(),
               request=['jefferson', 'jefferson'],
               found_name=['Jefferson County', 'Jefferson County'],
               state=['alabama', 'arkansas'],
               country=['usa', 'usa']
               )

def test_simple_scope():
    florida_with_country = geodata.regions_builder2('state', names=['florida', 'florida'], countries=['Uruguay', 'usa'])\
        .build()\
        .to_data_frame()

    assert florida_with_country[DF_ID][0] != florida_with_country[DF_ID][1]

    florida_with_scope = geodata.regions_builder2('state', names=['florida'], scope='Uruguay').build().to_data_frame()

    assert florida_with_country[DF_ID][0] == florida_with_scope[DF_ID][0]


def test_where():
    geodata.regions_builder2('city', names='warwick')\
        .where('warwick', scope='oklahoma').build()
