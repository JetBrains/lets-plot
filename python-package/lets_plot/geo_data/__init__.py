try:
    import geopandas
except ImportError:
    raise ValueError("geopandas is required for using the geo_data module") from None

from .core import *
from .map_geometry import *
from .geocoder import *
from .geocodes import *

__all__ = (core.__all__ + map_geometry.__all__)

# print on the package import
print("The geodata is provided by © OpenStreetMap contributors"
      " and is made available here under the Open Database License (ODbL).")