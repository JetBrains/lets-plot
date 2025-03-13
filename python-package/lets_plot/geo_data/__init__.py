try:
    import geopandas
except ImportError:
    print("geopandas is required for using the geo_data package")
    raise

from .core import *
from .geocoder import *
from .geocodes import *

__all__ = core.__all__

#  Use geo_data package only for executing geocoding requests.
#  For accessing variuous checks, contants, types etc use package geo_data_internals
#  as it won't cause the OSM attribution to appear.

# print on the package import
print("The geodata is provided by © OpenStreetMap contributors"
      " and is made available here under the Open Database License (ODbL).")