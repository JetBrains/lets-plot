from pkgutil import extend_path

# To handle the situation when 'datalore' package is shared my modules in different locations.
__path__ = extend_path(__path__, __name__)

from ._version import __version__

# __version__ = __version__   # only to get rid of 'unused import' warning

from .plot import *

__all__ = plot.__all__
