#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from pkgutil import extend_path

# To handle the situation when 'datalore' package is shared my modules in different locations.
__path__ = extend_path(__path__, __name__)

from ._version import __version__
from .plot import *
from ._global_settings import LetsPlotSettings
from .frontend_context import *

__all__ = (plot.__all__ +
           frontend_context.__all__ +
           ['LetsPlotSettings'])
