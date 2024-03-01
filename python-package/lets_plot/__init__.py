#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import json
from pkgutil import extend_path
from typing import Dict

# To handle the situation when the 'lets_plot' package is shared by modules in different locations.
__path__ = extend_path(__path__, __name__)

from ._version import __version__
from ._global_settings import _settings, is_production, get_global_bool, PLOT_THEME
from ._global_settings import NO_JS, OFFLINE

from .plot import *
from .export import *
from .frontend_context import *
from .settings_utils import *

__all__ = (plot.__all__ +
           frontend_context.__all__ +
           settings_utils.__all__ +
           export.__all__ +
           ['LetsPlot'])

from .frontend_context import _configuration as cfg


class LetsPlot:
    """
    Initialize the library and its options.
    """

    @classmethod
    def setup_show_ext(cls, *,
                       exec: str = None,
                       new: bool = False) -> None:
        """
        Configures Lets-Plot HTML output for showing in a browser.

        Parameters
        ----------
        exec : str, optional
            Command to execute to open the plot in a web browser.
            If not specified, the default browser will be used.
        new : bool, default=False
            If True, the URL is opened in a new window of the web browser.
            If False, the URL is opened in the already opened web browser window.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 2

            from lets_plot import *
            LetsPlot.setup_show_ext()
            p = ggplot({'x': [0], 'y': [0]}, aes('x', 'y')) + geom_point()
            p.show()

        |

        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 2

            from lets_plot import *
            LetsPlot.setup_show_ext(exec = 'chrome.exe --app=%s')
            p = ggplot({'x': [0], 'y': [0]}, aes('x', 'y')) + geom_point()
            p.show()

        |

        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 2

            from lets_plot import *
            LetsPlot.setup_show_ext(exec = 'open -a safari %s', new=True)
            p = ggplot({'x': [0], 'y': [0]}, aes('x', 'y')) + geom_point()
            p.show()

        """
        cfg._setup_wb_html_context(exec=exec, new=new)

    @classmethod
    def setup_html(cls, *,
                   isolated_frame: bool = None,
                   offline: bool = None,
                   no_js: bool = None,
                   show_status: bool = False) -> None:
        """
        Configure Lets-Plot HTML output.
        Depending on the usage, LetsPlot generates different HTML to show plots.
        In most cases LetsPlot will detect type of the environment automatically.
        Auto-detection can be overwritten using this method parameters.

        Parameters
        ----------
        isolated_frame : bool
            True - generate HTLM which can be used in iframe or in a standalone HTML document.
            False - pre-load Lets-Plot JS library. Notebook cell output will only consist
            of HTML for the plot rendering. Default: None - auto-detect.
        offline : bool
            True - full Lets-Plot JS bundle will be added to the notebook.
            Use this option if you would like to work with notebook
            without the Internet connection. False - load Lets-Plot JS library from CDN.
            Default (None): 'connected' mode in production environment
            and 'offline' mode in dev environment.
        no_js : bool, default=False
            True - do not generate HTML+JS as an output - just static SVG image.
            Note that without JS interactive maps and tooltips doesn't work!
        show_status : bool, default=False
            Whether to show status of loading of the Lets-Plot JS library.
            Only applicable when the Lets-Plot JS library is preloaded.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 2

            from lets_plot import *
            LetsPlot.setup_html()
            ggplot({'x': [0], 'y': [0]}, aes('x', 'y')) + geom_point()

        |

        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 2-3

            from lets_plot import *
            LetsPlot.setup_html(isolated_frame=False, offline=True, \\
                                no_js=True, show_status=True)
            ggplot({'x': [0], 'y': [0]}, aes('x', 'y')) + geom_point()

        """
        if not (isinstance(isolated_frame, bool) or isolated_frame is None):
            raise ValueError("'isolated' argument is not boolean: {}".format(type(isolated_frame)))
        if not (isinstance(offline, bool) or offline is None):
            raise ValueError("'offline' argument is not boolean: {}".format(type(offline)))
        if not (isinstance(no_js, bool) or no_js is None):
            raise ValueError("'no_js' argument is not boolean: {}".format(type(no_js)))
        if not isinstance(show_status, bool):
            raise ValueError("'show_status' argument is not boolean: {}".format(type(show_status)))

        offline = offline if offline is not None else get_global_bool(OFFLINE)
        no_js = no_js if no_js is not None else get_global_bool(NO_JS)

        cfg._setup_html_context(isolated_frame=isolated_frame,
                                offline=offline,
                                no_js=no_js,
                                show_status=show_status)

    @classmethod
    def set(cls, settings: Dict):
        """
        Set up library options.
        For more info see https://lets-plot.org/python/pages/basemap_tiles.html#configuring-globally.

        Parameters
        ----------
        settings : dict
            Dictionary of settings.

        Notes
        -----
        List of possible settings:

        - html_isolated_frame : preload Lets-Plot JS library or not (bool). Do not use this parameter explicitly. Instead you should call `LetsPlot.setup_html()`.
        - offline : to work with notebook without the Internet connection (bool). Do not use this parameter explicitly. Instead you should call `LetsPlot.setup_html()`.
        - no_js : do not generate HTML+JS as an output (bool). Do not use this parameter explicitly. Instead you should call `LetsPlot.setup_html()`. Also note that without JS interactive maps and tooltips doesn't work!

        Interactive map settings could also be specified:

        - maptiles_kind : kind of the tiles, could be 'raster_zxy' or 'vector_lets_plot'. Do not use this parameter explicitly. Instead you should construct it with functions `maptiles_zxy()` and `maptiles_lets_plot()`.
        - maptiles_url : address of the tile server (str). Do not use this parameter explicitly. Instead you should construct it with functions `maptiles_zxy()` and `maptiles_lets_plot()`.
        - maptiles_theme : tiles theme, could be 'color', 'light' or 'dark'. Do not use this parameter explicitly. Instead you should construct it with function `maptiles_lets_plot()`.
        - maptiles_attribution : an attribution or a copyright notice to display on the map as required by the tile license (str, supports HTML links). Do not use this parameter explicitly. Instead you should construct it with function `maptiles_zxy()`.
        - maptiles_min_zoom : minimal zoom limit (int). Do not use this parameter explicitly. Instead you should construct it with function `maptiles_zxy()`.
        - maptiles_max_zoom : maximal zoom limit (int). Do not use this parameter explicitly. Instead you should construct it with function `maptiles_zxy()`.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 4

            from lets_plot import *
            from lets_plot import tilesets
            LetsPlot.setup_html()
            LetsPlot.set(tilesets.LETS_PLOT_LIGHT)
            ggplot() + geom_livemap()

        |

        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 4

            from lets_plot import *
            from lets_plot import tilesets
            LetsPlot.setup_html()
            LetsPlot.set(tilesets.LETS_PLOT_BW)
            ggplot() + geom_livemap()

        """
        if is_production():
            _settings.update(settings)
        else:
            _settings.update({'dev_' + key: value for key, value in settings.items()})

    @classmethod
    def set_theme(cls, theme: 'plot.FeatureSpec'):
        """
        Set up global theme.

        Parameters
        ----------
        theme : spec
            Theme spec provided by `theme(...)` or `theme_xxx()` functions.

        """
        if theme.kind != 'theme':
            raise ValueError("Wrong option type. Expected `theme` but was `{}`.".format(theme.kind))

        LetsPlot.set({
            PLOT_THEME: json.dumps(theme.as_dict())
        })
