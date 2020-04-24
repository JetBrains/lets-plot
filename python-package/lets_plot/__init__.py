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
        Configures Lets-Plot HTML output.
        Depending on the usage LetsPlot generates different HTML to show plots.
        In most cases LetsPlot will detect type of the environment automatically. Auto-detection
        can be overritten using this method parameters.

        Parameters
        ----------
        isolated_frame : bool, optional, default None - auto-detect
            If `True`, generate HTLM which can be used in `iframe` or in a standalone HTML document
            If `False`, pre-load Lets-Plot JS library. Notebook cell output will only consist of HTML for the plot rendering.
            
        offline : bool, optional, default None - evaluated to 'connected' mode in production environment.
            If `True`, full Lets-Plot JS bundle will be added to the notebook. Use this option if you would like
            to work with notebook without the Internet connection.
            If `False`, load Lets-Plot JS library from CDN.
        """
        if not (isinstance(isolated_frame, bool) or isolated_frame is None):
            raise ValueError("'isolated' argument is not boolean: {}".format(type(isolated_frame)))
        if not (isinstance(offline, bool) or offline is None):
            raise ValueError("'offline' argument is not boolean: {}".format(type(offline)))

        cfg._setup_html_context(isolated_frame, offline)

    @classmethod
    def setup_tile_provider(cls, kind: str = None, url: str = None, port=None, theme: str =  None, token: str = None):
        """
        Configures tile provider, used by geom_livemap.

        :param kind: str
            'zxy' - simple raster tile provider.
            'datalore' - vector tiles for datalore users

        :param url: str
            If kind is 'zxy': template  for a standard raster ZXY tile provider with {z}, {x} and {y} wildcards, e.g. 'http://my.tile.com/{z}/{x}/{y}.png'
            If kind is 'datalore': address of the tile server

        :param port: int
            If kind is 'zxy': port is not user
            If kind is 'datalore': port of the tile server

        :param theme: str
            If kind is 'zxy': theme is not used
            If kind is 'datalore': tiles theme

        :param token: str
            If kind is 'zxy': token is not used
            If kind is 'datalore': token is not used
        """
        assert isinstance(kind, (str, type(None))), "'kind' argument is not str: {}".format(type(kind))
        assert isinstance(url, (str, type(None))), "'url' argument is not str: {}".format(type(url))
        assert isinstance(port, (int, type(None))), "'port' argument is not int: {}".format(type(port))
        assert isinstance(theme, (str, type(None))), "'theme' argument is not str: {}".format(type(theme))
        assert isinstance(token, (str, type(None))), "'token' argument is not str: {}".format(type(token))

        LetsPlotSettings.apply(
            {
                _global_settings.TILE_PROVIDER_KIND: kind,
                _global_settings.TILE_PROVIDER_URL: url,
                _global_settings.TILE_PROVIDER_PORT: port,
                _global_settings.TILE_PROVIDER_THEME: theme,
                _global_settings.TILE_PROVIDER_TOKEN: token,

                'dev_' + _global_settings.TILE_PROVIDER_KIND: kind,
                'dev_' + _global_settings.TILE_PROVIDER_URL: url,
                'dev_' + _global_settings.TILE_PROVIDER_PORT: port,
                'dev_' + _global_settings.TILE_PROVIDER_THEME: theme,
                'dev_' + _global_settings.TILE_PROVIDER_TOKEN: token

            }
        )
