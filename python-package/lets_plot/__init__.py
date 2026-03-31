#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from pkgutil import extend_path
from typing import Dict, Union

# To handle the situation when the 'lets_plot' package is shared by modules in different locations.
__path__ = extend_path(__path__, __name__)

from ._version import __version__
from ._global_settings import _settings, is_production, get_global_bool
from ._global_settings import NO_JS, OFFLINE

from .plot import *
from .export import *
from .frontend_context import *
from .mapping import *
from .settings_utils import *
from .plot._global_theme import _set_global_theme

__all__ = (plot.__all__ +
           frontend_context.__all__ +
           mapping.__all__ +
           settings_utils.__all__ +
           export.__all__ +
           ['LetsPlot'])

from .frontend_context import _configuration as cfg


class LetsPlot:
    """
    Initialize the library and its options.
    """

    @classmethod
    def setup_html(cls, *,
                   isolated_frame: bool = None,
                   offline: bool = None,
                   no_js: bool = None,
                   show_status: bool = False,
                   **kwargs) -> None:
        """
        Configure Lets-Plot HTML output.
        This method should typically be called before rendering any plots.
        Depending on the usage, Lets-Plot generates different HTML to show plots.
        In most cases Lets-Plot will detect the type of the environment automatically.
        Use this method to adjust or override the autoconfigured output mode.

        Parameters
        ----------
        isolated_frame : bool
            True - generate HTML which can be used in iframe or in a standalone HTML document.
            False - preload Lets-Plot JS library. Notebook cell output will only consist
            of HTML for the plot rendering. Default: auto-detect.
        offline : bool
            True - full Lets-Plot JS bundle will be added to the notebook.
            Use this option if you would like to work with a notebook without the Internet connection.
            False - load Lets-Plot JS library from CDN.
            Default: 'connected' mode in the production environment, 'offline' mode in the dev environment.
        no_js : bool, default=False
            True - do not generate HTML+JS as an output - just static SVG image.
            Note that without JS interactive maps and tooltips don't work!
        show_status : bool, default=False
            Whether to show the Lets-Plot JS library loading status.
            Only applicable when the Lets-Plot JS library is preloaded.
        **kwargs
            Advanced display options for developers testing in new environments
            or debugging rendering behavior. These options control the underlying
            HTML rendering:

            - isolated_webview_panel : bool
                If True, generates HTML for an isolated webview panel with dynamic script loading.
                When enabled, the 'isolated_frame' parameter is ignored.
            - width_mode : str
                Plot width sizing mode: 'fixed', 'min', 'fit', or 'scaled'.
                Requires height_mode to also be specified.
            - height_mode : str
                Plot height sizing mode: 'fixed', 'min', 'fit', or 'scaled'.
                Requires width_mode to also be specified.
            - width : float
                Explicit width value in px (used with certain sizing modes).
            - height : float
                Explicit height value in px (used with certain sizing modes).
            - force_immediate_render : bool
                Controls the timing of plot rendering.
                If True, renders plot immediately.
                If False, waits for the ResizeObserver event to ensure proper DOM layout.
            - responsive : bool
                If True, the plot automatically resizes when the container is resized.
            - height100pct : bool
                If True, sets the plot container div height to 100%.

            Sizing modes:

            - 'fixed': Uses specified width/height or default size (not responsive)
            - 'min': Uses smallest of: default size, specified size, and container size
            - 'fit': Uses container size or specified size if provided
            - 'scaled': Adjusts to preserve the aspect ratio


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

        # Validate dev options
        if kwargs:
            supported_keys = {'width_mode', 'height_mode', 'width', 'height',
                             'responsive', 'force_immediate_render', 'height100pct', 'isolated_webview_panel'}
            unsupported_keys = set(kwargs.keys()) - supported_keys
            if unsupported_keys:
                raise ValueError(
                    "Unsupported parameter(s): {}".format(', '.join(sorted(unsupported_keys)))
                )

            has_width_mode = 'width_mode' in kwargs
            has_height_mode = 'height_mode' in kwargs
            if has_width_mode != has_height_mode:
                raise ValueError(
                    "Both 'width_mode' and 'height_mode' must be specified together. "
                    "Got: width_mode={}, height_mode={}".format(
                        kwargs.get('width_mode', 'not specified'),
                        kwargs.get('height_mode', 'not specified')
                    )
                )

            if has_width_mode:
                valid_modes = ['fixed', 'min', 'fit', 'scaled']
                width_mode = kwargs['width_mode']
                if not isinstance(width_mode, str):
                    raise ValueError("'width_mode' must be a string, got: {}".format(type(width_mode)))
                if width_mode.lower() not in valid_modes:
                    raise ValueError(
                        "'width_mode' must be one of {}, got: '{}'".format(valid_modes, width_mode)
                    )

            if has_height_mode:
                valid_modes = ['fixed', 'min', 'fit', 'scaled']
                height_mode = kwargs['height_mode']
                if not isinstance(height_mode, str):
                    raise ValueError("'height_mode' must be a string, got: {}".format(type(height_mode)))
                if height_mode.lower() not in valid_modes:
                    raise ValueError(
                        "'height_mode' must be one of {}, got: '{}'".format(valid_modes, height_mode)
                    )

            if 'width' in kwargs:
                width = kwargs['width']
                if not isinstance(width, (int, float)):
                    raise ValueError("'width' must be a number, got: {}".format(type(width)))

            if 'height' in kwargs:
                height = kwargs['height']
                if not isinstance(height, (int, float)):
                    raise ValueError("'height' must be a number, got: {}".format(type(height)))

            # Validate boolean options
            for bool_option in ['responsive', 'force_immediate_render', 'height100pct', 'isolated_webview_panel']:
                if bool_option in kwargs and not isinstance(kwargs[bool_option], bool):
                    raise ValueError("'{}' must be a boolean, got: {}".format(
                        bool_option, type(kwargs[bool_option])
                    ))

            # Warn if isolated_webview_panel is True
            if kwargs.get('isolated_webview_panel'):
                print("WARNING: 'isolated_webview_panel=True' - using isolated webview panel context. "
                      "The 'isolated_frame' parameter will be ignored.")

        offline = offline if offline is not None else get_global_bool(OFFLINE)
        no_js = no_js if no_js is not None else get_global_bool(NO_JS)

        cfg._setup_html_context(isolated_frame=isolated_frame,
                                offline=offline,
                                no_js=no_js,
                                show_status=show_status,
                                dev_options=kwargs)

    @classmethod
    def set(cls, settings: Dict):
        """
        Set up library options.
        For more info see `Configuring Globally <https://lets-plot.org/python/pages/basemap_tiles.html#configuring-globally>`__.

        Parameters
        ----------
        settings : dict
            Dictionary of settings.

        Notes
        -----
        List of possible settings:

        - html_isolated_frame : preload Lets-Plot JS library or not (bool). Do not use this parameter explicitly. Instead you should call `LetsPlot.setup_html() <https://lets-plot.org/python/pages/api/lets_plot.LetsPlot.html#lets_plot.LetsPlot.setup_html>`__.
        - offline : to work with notebook without the Internet connection (bool). Do not use this parameter explicitly. Instead you should call `LetsPlot.setup_html() <https://lets-plot.org/python/pages/api/lets_plot.LetsPlot.html#lets_plot.LetsPlot.setup_html>`__.
        - no_js : do not generate HTML+JS as an output (bool). Do not use this parameter explicitly. Instead you should call `LetsPlot.setup_html() <https://lets-plot.org/python/pages/api/lets_plot.LetsPlot.html#lets_plot.LetsPlot.setup_html>`__. Also note that without JS interactive maps and tooltips doesn't work!

        Interactive map settings could also be specified:

        - maptiles_kind : kind of the tiles, could be 'raster_zxy' or 'vector_lets_plot'. Do not use this parameter explicitly. Instead you should construct it with functions `maptiles_zxy() <https://lets-plot.org/python/pages/api/lets_plot.maptiles_zxy.html>`__ and `maptiles_lets_plot() <https://lets-plot.org/python/pages/api/lets_plot.maptiles_lets_plot.html>`__.
        - maptiles_url : address of the tile server (str). Do not use this parameter explicitly. Instead you should construct it with functions `maptiles_zxy() <https://lets-plot.org/python/pages/api/lets_plot.maptiles_zxy.html>`__ and `maptiles_lets_plot() <https://lets-plot.org/python/pages/api/lets_plot.maptiles_lets_plot.html>`__.
        - maptiles_theme : tiles theme, could be 'color', 'light' or 'dark'. Do not use this parameter explicitly. Instead you should construct it with function `maptiles_lets_plot() <https://lets-plot.org/python/pages/api/lets_plot.maptiles_lets_plot.html>`__.
        - maptiles_attribution : an attribution or a copyright notice to display on the map as required by the tile license (str, supports HTML links). Do not use this parameter explicitly. Instead you should construct it with function `maptiles_zxy() <https://lets-plot.org/python/pages/api/lets_plot.maptiles_zxy.html>`__.
        - maptiles_min_zoom : minimal zoom limit (int). Do not use this parameter explicitly. Instead you should construct it with function `maptiles_zxy() <https://lets-plot.org/python/pages/api/lets_plot.maptiles_zxy.html>`__.
        - maptiles_max_zoom : maximal zoom limit (int). Do not use this parameter explicitly. Instead you should construct it with function `maptiles_zxy() <https://lets-plot.org/python/pages/api/lets_plot.maptiles_zxy.html>`__.

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
    def set_theme(cls, theme: Union['core.FeatureSpec', 'core.FeatureSpecArray']):
        """
        Set up global theme.

        Parameters
        ----------
        theme : spec
            Theme spec provided by `theme(...) <https://lets-plot.org/python/pages/api/lets_plot.theme.html>`__, ``theme_xxx()``, ``flavor_xxx()`` functions, or their sum.

        """
        if theme is None:
            _set_global_theme(None)
            return

        if theme.kind != 'theme' and not (theme.kind == 'feature-list' and all(f.kind == 'theme' for f in theme)):
            raise ValueError("Only `theme(...)`, `theme_xxx()`, `flavor_xxx()`, or a sum of them are supported")

        _set_global_theme(theme)

    @classmethod
    def setup_show_ext(cls, *,
                       exec: str = None,
                       new: bool = False) -> None:
        """
        Configure Lets-Plot to show its HTML output in an external browser.

        When the "show externally" is set up, an invocation of ``figire.show()`` will
        - generate HTML output
        - save it to a temporary file
        - open the file in the default web browser or in a web browser specified by the ``exec`` parameter.

        Parameters
        ----------
        exec : str, optional
            Specify an app to open the generated temporary HTML file.
            If not specified, the default browser will be used.
        new : bool, default=False
            If True, the URL is opened in a new window of the web browser.
            If False, the URL is opened in the already opened web browser window.
            The ``new`` parameter is only applicable when the ``exec`` parameter is not specified.
            Please note that the ``new`` parameter is not supported by all web browsers and all OS-s.

        Examples
        --------
        .. code-block::
            :linenos:
            :emphasize-lines: 3

            # Show the plot in the default web browser.
            from lets_plot import *
            LetsPlot.setup_show_ext()
            p = ggplot() + geom_point(x=0, y=0)
            p.show()

        |

        .. code-block::
            :linenos:
            :emphasize-lines: 3

            # Show the plot in the new window of the default web browser if possible.
            from lets_plot import *
            LetsPlot.setup_show_ext(new=True)
            p = ggplot() + geom_point(x=0, y=0)
            p.show()

        |

        .. code-block::
            :linenos:
            :emphasize-lines: 4

            # Show the plot in the Chrome web browser for Windows.
            # This is the default setup path. Replace the file path with your own if it differs.
            from lets_plot import *
            LetsPlot.setup_show_ext(exec='C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe --app=%s')
            p = ggplot() + geom_point(x=0, y=0)
            p.show()

        |

        .. code-block::
            :linenos:
            :emphasize-lines: 3

            # Show the plot in the Safari web browser for macOS.
            from lets_plot import *
            LetsPlot.setup_show_ext(exec='open -a safari %s')
            p = ggplot() + geom_point(x=0, y=0)
            p.show()

        |

        .. code-block::
            :linenos:
            :emphasize-lines: 4

            # Show the plot in the Chrome web browser for macOS in the application mode.
            # This is the default setup path. Replace the path with your own if it differs.
            from lets_plot import *
            LetsPlot.setup_show_ext(exec='/Applications/Google\\ Chrome.app/Contents/MacOS/Google\\ Chrome --app=%s')
            p = ggplot() + geom_point(x=0, y=0)
            p.show()

        |

        .. code-block::
            :linenos:
            :emphasize-lines: 3

            # Show the plot in the Chrome web browser for Linux.
            from lets_plot import *
            LetsPlot.setup_show_ext(exec='google-chrome --app=%s')
            p = ggplot() + geom_point(x=0, y=0)
            p.show()

        """
        cfg._setup_wb_html_context(exec=exec, new=new)
