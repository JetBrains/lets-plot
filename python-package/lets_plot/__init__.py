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
           ['LetsPlotSettings', 'LetsPlot'])

from .frontend_context import _configuration as cfg

class LetsPlot:
    @classmethod
    def setup_html(cls, isolated_frame: bool = None, offline: bool = None) -> None:
        """
        Configure HTML frontend context
        :param settings:
        :return:
        """
        if not (isinstance(isolated_frame, bool) or isolated_frame is None):
            raise ValueError("'isolated' argument is not boolean: {}".format(type(isolated_frame)))
        if not (isinstance(offline, bool) or offline is None):
            raise ValueError("'offline' argument is not boolean: {}".format(type(offline)))

        cfg._setup_html_context(isolated_frame, offline)