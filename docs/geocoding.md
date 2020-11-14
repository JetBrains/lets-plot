# Geocoding API

Geocoding is the process of converting names of places into geographic coordinates.

*Lets-Plot* now offers geocoding API covering the following administrative levels:
- country
- state
- county
- city

*Lets-Plot* geocoding API allows a user to execute a single and batch geocoding queries, and handle possible 
names ambiguity.

Relatively simple geocoding queries are executed using the `regions_xxx()` functions family. For example:
```python
from lets_plot.geo_data import *
regions_country(['usa', 'canada'])
```
returns the `Regions` object containing internal IDs for Canada and the US:
```
  request       id                found name
0     usa   297677  United States of America
1  canada  2856251                    Canada 
```
More complex geocoding queries can be created with the help of the `regions_builder()` function that
returns the `RegionsBuilder` object and allows chaining its various methods in order to specify 
how to handle geocoding ambiguities.

For example:
```python
regions_builder(request='warwick', level='city')  \
    .allow_ambiguous()  \
    .build()
```    
This sample returns the `Regions` object containing IDs of all cities matching "warwick":
```
    request        id                   found name
0   warwick    785807                      Warwick
1   warwick    363189                      Warwick
2   warwick    352173                      Warwick
3   warwick  15994531                      Warwick
4   warwick    368499                      Warwick
5   warwick    239553                      Warwick
6   warwick    352897                      Warwick
7   warwick   3679247                      Warwick
8   warwick   8144841                      Warwick
9   warwick    382429                 West Warwick
10  warwick   7042961             Warwick Township
11  warwick   6098747             Warwick Township
12  warwick  15994533  Sainte-Élizabeth-de-Warwick
``` 
```python
boston_us = regions(request='boston', within='us')
regions_builder(request='warwick', level='city') \
    .where('warwick', near=boston_us) \
    .build()
```    
This example returns the `Regions` object containing the ID of one particular "warwick" near Boston (US):
```
   request      id found name
0  warwick  785807    Warwick
```
Once the `Regions` object is available, it can be passed to any *Lets-Plot* geom 
supporting the `map` parameter.

If necessary, the `Regions` object can be transformed into a regular pandas `DataFrame` using `to_data_frame()` method
or to a geopandas `GeoDataFrame` using one of `centroids()`, `boundaries()`, or `limits()` methods.

All coordinates are in the EPSG:4326 coordinate reference system (CRS). 

Note what executing geocoding queries requires an internet connection.

Examples:

* Visualization of the Titanic's voyage:
<a href="https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/map_titanic.ipynb"> 
    <img src="https://raw.githubusercontent.com/jupyter/design/master/logos/Badges/nbviewer_badge.png" width="109" height="20" align="left">
</a>
<span>&nbsp;&nbsp;</span>
<a href="https://view.datalore.io/notebook/1h4h0HMctRKJLY64PBe63a" title="View in Datalore"> 
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_datalore.svg" width="20" height="20">
</a>
<span>&nbsp;&nbsp;</span>
<a href="https://www.kaggle.com/alshan/visualization-of-the-titanic-s-voyage" title="View at Kaggle"> 
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_kaggle.svg" width="20" height="20">
</a>
<span>&nbsp;&nbsp;</span>
<a href="https://colab.research.google.com/drive/1PerUfSCyStcbnlXnxBj-JVI25-cXB_N5?usp=sharing" title="View at Colab"> 
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_colab.svg" width="20" height="20">
</a>
<br>
<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/map_titanic.png" alt="Couldn't load map_titanic.png" width="547" height="197">
<br>

* Visualization of Airport Data on Map: <a href="https://www.kaggle.com/alshan/visualization-of-airport-data-on-map" title="View at Kaggle"> 
                                             <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_kaggle.svg" width="20" height="20">
                                        </a>
<br>
<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/map_airports.png" alt="Couldn't load map_airports.png" width="547" height="311">                                         