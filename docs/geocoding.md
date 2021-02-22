# Geocoding

- [Overview](#introduction)
- [Examples](#examples)
- [Reference](#reference)
    - [Levels](#levels)
    - [Parents](#parents)
    - [Scope](#scope)
    - [Fetch All](#fetch-all)
    - [US-48](#us-48)
    - [Ambiguity](#ambiguity)
        - [allow_ambiguous()](#allow-ambiguous)
        - [ignore_not_found()](#ignore-not-found)
        - [ignore_all_errors()](#ignore-all-errors)
        - [where()](#where)  
            - [closest_to](#where-closest-to)  
            - [scope](#where-scope)
    - [Working with plot](#working-with-plot)
        - [Plotting a `GeoDataFrame`](#plot-gdf)
        - [Plotting a `Geocoder`](#plot-geocoder)
        - [`map` and `map_join`](#join)
            - [Join with `GeoDataFrame`](#join-gdf)
            - [Join with `Geocoder`](#join-geocoder)
    
    
<a id="introduction"></a>
## Overview

Geocoding is the process of converting names of places into geographic coordinates.

*Lets-Plot* now offers geocoding API covering the following administrative levels:
- country
- state
- county
- city

*Lets-Plot* geocoding API allows a user to execute a single and batch geocoding queries, and handle possible 
names ambiguity.

The core class is `Geocoder`. There is a function's family for constructing the `Geocoder` object - `geocode_cities()`, `geocode_counties()`, `geocode_states()`, `geocode_countries()` and `geocode()`. For example:
```python
from lets_plot.geo_data import *
countries = geocode_countries(['usa', 'canada'])
```
Note that actual geocoding process is not executing here, it starts when any `get_xxx()` function is called. We will use in examples function `get_geocodes()` which returns `DataFrame` with metadata. 

Let us geocode countries:
```python
countries.get_geocodes()
``` 
returns the `DataFrame` object containing internal IDs for Canada and the US:
```
  |id      |request |found name
----------------------------------
0 |297677  |usa     |United States
1 |2856251 |canada  |Canada
```
More complex queries can be created in order to specify how to handle geocoding ambiguities.

For example:
```python
geocode_cities('warwick')  \
    .allow_ambiguous()  \
    .get_geocodes()
```    
This sample returns the `DataFrame` object containing IDs of all cities matching "warwick":
```

    |id       |request |found name
----------------------------------------------------
0   |239553   |warwick |Warwick
1   |352173   |warwick |Warwick
2   |352897   |warwick |Warwick
3   |363189   |warwick |Warwick
4   |368499   |warwick |Warwick
5   |785807   |warwick |Warwick
6   |3679247  |warwick |Warwick
7   |8144841  |warwick |Warwick
8   |15994531 |warwick |Warwick
9   |382429   |warwick |West Warwick
10  |6098747  |warwick |Warwick Township
11  |7042961  |warwick |Warwick Township
12  |18489127 |warwick |Warwick Mountain
13  |15994533 |warwick |Sainte-Élizabeth-de-Warwick
``` 
```python
boston_us = geocode_cities('boston').scope('us')
geocode_cities('warwick') \
    .where('warwick', closest_to=boston_us) \
    .get_geocodes()
```    
This example returns the `DataFrame` object containing the ID of one particular "warwick" closest to Boston (US):
```
  |id     |request |found name
------------------------------
0 |785807 |warwick |Warwick
```
Once the `Geocoder` object is available, it can be passed to any *Lets-Plot* geom 
supporting the `map` parameter. `map` parameter can be used to simply [draw a GeoDataFrame](#plot-gdf) or to [draw a Geocoder](#plot-geocoder). For more complex plots parameter [map_join](#join) can be used to map data to geometries.

If necessary, the `Geocoder` object can be transformed to a geopandas `GeoDataFrame` using one of `get_centroids()`, `get_boundaries()`, or `get_limits()` methods.

All coordinates are in the EPSG:4326 coordinate reference system (CRS). 

Note that an internet connection is required to execute geocoding queries.

<a id="examples"></a>
## Examples
                     
* Various geocoding cases with maps: [geocoding_examples.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/geocoding_examples.ipynb)
                      
* Mapping US Household Income:
<a href="https://www.kaggle.com/alshan/mapping-us-household-income" title="View at Kaggle">
    <img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/docs/examples/images/logo_kaggle.svg" width="20" height="20">
</a>

* Geocoding the US counties: [map_US_household_income.ipynb](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/map_US_household_income.ipynb)

* Visualization of the Titanic's voyage:
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

<a id="reference"></a>
## Reference

<a id="levels"></a>
### Levels
Geocoding supports 4 administrative levels:
- city
- county
- state
- country


Function `geocode()` with `level=None` can try to detect level automatically - it enumerates all levels from country to city and selects best matching level (result without  ambiguity and unknown names). For example:  
```python
geocode(names=['florida', 'tx']).get_geocodes()
```

```
  |id     |request |found name
------------------------------
0 |324101 |florida |Florida
1 |229381 |tx      |Texas
```
Level auto-detection can be useful, but it is slower and not recommended for large data sets.
  

Functions `geocode_cities()`, `geocode_counties()`, `geocode_states()`, `geocode_countries()` or `geocode(level=xxx)` search names only at a given level or return an error.
```python
geocode_states(['florida', 'tx']).get_geocodes()
```


<a id="parents"></a>
### Parents
`Geocoder` class provides functions to define parents with administrative level - `counties()`, `states()`, `countries()`. These functions can handle single or multiple values of type string or `Geocoder`. The number of values must match the number of names in `Geocoder` so that they form a table.  

Parents will be present in the result `DataFrame` to make it possible to join data and geometry via [map_join](#join).

```python
geocode_cities(['warwick', 'worcester'])\
    .counties(['Worth County', 'worcester county'])\
    .states(['georgia', 'massachusetts'])\
    .get_geocodes()
```
```
  |id      | request   |found name |county           |state
--------------------------------------------------------------
0 |239553  | warwick   |Warwick    |Worth County     |georgia
1 |3688419 | worcester |Worcester  |worcester county |massachusetts
```

Parents can contain `None` value for countries with different administrative division:
```python
geocode_cities(['warwick', 'worcester'])\
    .states(['Georgia', None])\
    .countries(['USA', 'United Kingdom'])\
    .get_geocodes()
```
```

  |id      |request   |found name |state   |country
--------------------------------------------------------------
0 |239553  |warwick   |Warwick    |Georgia |USA
1 |3750683 |worcester |Worcester  |None    |United Kingdom
```

Parent can be `Geocoder` object. This allows resolving parent's ambiguity:
```python

s = geocode_states(['vermont', 'georgia']).scope('usa')
geocode_cities(['worcester', 'warwick']).states(s).get_geocodes()
```
```
  |id       |request   |found name |state
-------------------------------------------
0 |17796275 |worcester |Worcester  |vermont
1 |239553   |warwick   |Warwick    |georgia
```

<a id="scope"></a>
###Scope
`scope()` is a special kind of parent. `scope()` can handle a `string` or a single entry `Geocoder` object. `scope()` is not associated with any administrative level, it acts as parent for any other parents and names. `scope()` can not be used with `countries()` parents. Typical use-case: all of names belong to the same parent.

```python
geocode_counties(['Dakota County', 'Nevada County']).states(['NE', 'AR']).scope('USA').get_geocodes()
```
```
   |id      |request       |found name    |state
------------------------------------------------
0  |2850895 |Dakota County |Dakota County |NE
1  |3653651 |Nevada County |Nevada County |AR
```

Parents can be modified between searches:

```python
florida = geocode_states('florida')

display(florida.countries('usa').get_geocodes())
display(florida.countries('uruguay').get_geocodes())
display(florida.countries(None).get_geocodes())
```

```
id     |request |found name |country
------------------------------------
324101 |florida |Florida    |usa

id     |request |found name |country
------------------------------------
3270329|florida |Florida    |uruguay

id     |request |found name
---------------------------
324101 |florida |Florida
```
<a id="fetch-all"></a>
### Fetch all

It is possible to fetch all objects within parent - just do not set the `names` parameter. 

```python
geocode_counties().states('massachusetts').get_geocodes()
```

```
  |id      |request          |found name       |state
-------------------------------------------------------------
0 |2363239 |Hampden County   |Hampden County   |massachusetts
1 |122643  |Berkshire County |Berkshire County |massachusetts
2 |180869  |Essex County     |Essex County     |massachusetts
3 |3677609 |Hampshire County |Hampshire County |massachusetts
4 |3677611 |Worcester County |Worcester County |massachusetts
...
```

<a id="us-48"></a>
### US-48
Geocoding supports a special name `us-48` for [CONUS](https://en.wikipedia.org/wiki/Contiguous_United_States). The `us-48` can be used as name or parent.
```python
geocode_states('us-48').get_geocodes()
```
```
  |id     |request       |found name
---------------------------------------
0 |121519 |Vermont       |Vermont
1 |122631 |Massachusetts |Massachusetts
2 |122641 |New York      |New York
3 |127025 |Maine         |Maine
4 |134427 |New Hampshire |New Hampshire
...
```

<a id="ambiguity"></a>
### Ambiguity
Often geocoding can find multiple objects for a name or do not find anything. in this case error will be generated:
 ```python
geocode_cities(['warwick', 'worcester']).get_geocodes()
```
```
Multiple objects (14) were found for warwick:

- Warwick (United States, Georgia, Worth County)
- Warwick (United States, New York, Orange County)
- Warwick (United Kingdom, England, West Midlands, Warwickshire)
- Warwick (United States, North Dakota, Benson County)
- Warwick (United States, Oklahoma, Lincoln County)
- Warwick (United States, Rhode Island, Kent County)
- Warwick (United States, Massachusetts, Franklin County)
- Warwick (Canada, Ontario, Southwestern Ontario, Lambton County)
- Warwick (Canada, Québec, Centre-du-Québec, Arthabaska)
- West Warwick (United States, Rhode Island, Kent County) Multiple objects (4) were found for worcester:
- Worcester (United States, Massachusetts, Worcester County)
- Worcester (United Kingdom, England, West Midlands, Worcestershire)
- Worcester (United States, Vermont, Washington County)
- Worcester Township (United States, Pennsylvania, Montgomery County)
```

The ambiguity can be resolved in different ways.  

<a id="allow-ambiguous"></a>
#### `allow_ambiguous()`

The best way is to find an object that we search and use its parents. The function converts error result into success result that can be rendered on a map or verified manually in other way.
Can be combined with [ignore_not_found()](#ignore_not_found) to suppress the "not found" error, which has higher priority.

```python
geocode_cities(['warwick', 'worcester']).allow_ambiguous().get_geocodes()
```
```
  |id     |request |found name
------------------------------
0 |239553 |warwick |Warwick
1 |352173 |warwick |Warwick
2 |352897 |warwick |Warwick
3 |363189 |warwick |Warwick
4 |368499 |warwick |Warwick
```

<a id="ignore-not-found"></a>
#### `ignore_not_found()`
Removes unknown names from the result.
```python
geocode_cities(['paris', 'foo']).ignore_not_found().get_geocodes()
```

```
  |id    |request |found name
-----------------------------
0 |14889 |paris	  |Paris
```

<a id="ignore-all-errors"></a>
#### `ignore_all_errors()`
Remove not found names or names with multiple matches.
```python
geocode_cities(['paris', 'worcester', 'foo']).ignore_all_errors().get_geocodes()
```
```
  |id    |request |found name
-----------------------------
0 |14889 |paris	  |Paris
``` 

<a id="where"></a>
#### `where()`
For resolving an ambiguity geocoding provides a function that can configure names individually.  
To configure a name the function `where(...)` should be called with the place name and all used parent names.  Parents cannot be changed via `where()` function call. If name and parents do not match with ones from the `where()` function an error will be generated. It is important for cases like this:
```python
geocode_counties(['Washington', 'Washington']).states(['oregon', 'utah']).get_geocodes()
```
```
  |id      |request    |found name        |state
-------------------------------------------------
0 |3674267 |Washington |Washington County |oregon
1 |3488745 |Washington |Washington County |utah
```
<a id="where-closest-to"></a>
##### closest_to
With parameter `closest_to` geocoding will take only the object closest to given place. Parameter `closest_to` can be a single value `Geocoder`. 
```python
boston = geocode_cities('boston')
geocode_cities('worcester').where('worcester', closest_to=boston).get_geocodes()
```

```
  |id      |request   |found name
---------------------------------
0 |3688419 |worcester |Worcester
```
Or it can be a `shapely.geometry.Point`. 
```python
geocode_cities('worcester').where('worcester', closest_to=shapely.geometry.Point(-71.088, 42.311)).get_geocodes()
```
```
  |id      |request   |found name
---------------------------------
0 |3688419 |worcester |Worcester
```
<a id="where-scope"></a>
##### scope
With parameter `scope` a `shapely.geometry.Polygon` can be used for limiting an area of the search (coordinates should be in WGS84 coordinate system). Note that bbox of the polygon will be used: 
```python
geocode_cities('worcester')\
    .where('worcester', scope=shapely.geometry.box(-71.00, 42.00, -72.00, 43.00))\
    .get_geocodes()
```
```
  |id      |request   |found name
---------------------------------
0 |3688419 |worcester |Worcester
```

Also, `scope` can be a single value `Geocoder` object or a `string`:
```python
massachusetts = geocode_states('massachusetts')
geocode_cities('worcester').where('worcester', scope=massachusetts).get_geocodes()
```

`scope` does not change parents in the result `DataFrame`:
```python
worcester_county=geocode_counties('Worcester County').states('massachusetts').countries('usa')

geocode_cities(['worcester', 'worcester'])\
    .countries(['USA', 'United Kingdom'])\
    .where('worcester', country='USA', scope=worcester_county)\
    .get_geocodes()
```

```
  |id      |request   |found name |country
-------------------------------------------------
0 |3688419 |worcester |Worcester  |USA
1 |3750683 |worcester |Worcester  |United Kingdom
```

<a id="working-with-plot"></a>
### Working with plots
<a id="plot-gdf"></a>
#### Plotting a `GeoDataFrame`
`get_xxx()` functions return GeoDataFrame which can be used as `data` or `map` parameter (see [this](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/geopandas_naturalearth.ipynb) or [this](https://nbviewer.jupyter.org/github/JetBrains/lets-plot/blob/master/docs/examples/jupyter-notebooks/geopandas_kotlin_isl.ipynb)).
```
ggplot() + geom_point(map=geocode_states('us-48').get_centroids())
```

<a id="plot-geocoder"></a>
#### Plotting a `Geocoder`
Drawing geometries with `Geocoder` is a bit easier than using `GeoDataFrame`. Just pass a `Geocoder` to the `map` parameter, and the layer will fetch geometry it supports:
```
ggplot() + geom_point(map=geocode_states('us-48'))
```

The list of geoms and corresponding fetching functions they support:
```
geom_point(), geom_text() - get_centroids() 
geom_map(), geom_polygon() - get_boundaries() 
geom_rect() - get_limits() 
```

<a id="join"></a>
#### `map` and `map_join`
Parameter `map_join` is used to join map coordinates with data. Keys used to join:
- first value in a pair is data_key (column/columns in `data`)
- second value in a pair is a map_key (column/columns in `map`)  

<a id="join-gdf"></a>
##### Join with `GeoDataFrame`
- `map_join='state'`:  
    same as `[['state'], ['state']]`
- `map_join=[['city', 'state']]`:  
    same as `[['city', 'state'], ['city', 'state']]`
- `map_join=[['City_Name', 'State_Name'], ['city', 'state']]`:  
    Explicitly set keys for both data and map.

<a id="join-geocoder"></a>
##### Join with `Geocoder`
`Geocoder` contains metadata so in most cases only data have to be provided - Lets-Plot will generate map keys automatically with columns that were used for geocoding.  


- `map_join='State_Name'`:  
    same as `[['State_Name'], ['state']]`
- `map_join=['City_Name', 'State_Name']`:  
    same as `[['City_Name', 'State_Name'], ['city', 'state']]`
- `map_join=[['City_Name', 'State_Name'], ['city', 'state']]`:  
    Explicitly set keys for both data and map.
    
**NB: Generated keys follow this order - `city`, `county`, `state`, `country`. Parents that were not provided will be omitted. Data columns should follow the same order or result of join operation will be incorrect.** 