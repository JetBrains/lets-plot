import geopandas as gpd
import pandas as pd
from shapely.geometry import Point

from lets_plot.plot import ggplot, geom_livemap


def test_location_by_pandas():
    df = pd.DataFrame({'lon': [0], 'lat': [1]})
    p = ggplot() + geom_livemap(location=df)

    assert p.as_dict()['layers'][0]['location']['data'].lon.to_list() == [0]
    assert p.as_dict()['layers'][0]['location']['data'].lat.to_list() == [1]

def test_location_by_geopandas():
    gdf = gpd.GeoDataFrame({'lon': [0], 'lat': [1]}, geometry=[Point(2, 3)], crs='EPSG:4326')
    p = ggplot() + geom_livemap(location=gdf)

    assert p.as_dict()['layers'][0]['location']['data'].lon.to_list() == [0]
    assert p.as_dict()['layers'][0]['location']['data'].lat.to_list() == [1]
    assert p.as_dict()['layers'][0]['location']['data'].geometry.to_list() == [Point(2, 3)]
