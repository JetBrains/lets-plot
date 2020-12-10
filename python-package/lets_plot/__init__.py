#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from pkgutil import extend_path
from typing import Dict

# To handle the situation when the 'lets_plot' package is shared by modules in different locations.
__path__ = extend_path(__path__, __name__)

from ._global_settings import _settings, is_production, get_global_bool
from ._global_settings import NO_JS, OFFLINE

from .plot import *
from .export import *
from .frontend_context import *
from .settings_utils import *
from .bistro import *

__all__ = (plot.__all__ +
           bistro.__all__ +
           frontend_context.__all__ +
           settings_utils.__all__ +
           export.__all__ +
           ['LetsPlot'])

from .frontend_context import _configuration as cfg


class LetsPlot:
    @classmethod
    def setup_html(cls, *,
                   isolated_frame: bool = None,
                   offline: bool = None,
                   no_js: bool = None,
                   show_status: bool = False) -> None:
        """
        Configures Lets-Plot HTML output.
        Depending on the usage LetsPlot generates different HTML to show plots.
        In most cases LetsPlot will detect type of the environment automatically. Auto-detection
        can be overritten using this method parameters.

        Parameters
        ----------
        isolated_frame : bool
            True - generate HTLM which can be used in `iframe` or in a standalone HTML document
            False - pre-load Lets-Plot JS library. Notebook cell output will only consist of HTML for the plot rendering.
            Default: None - auto-detect.
        offline : bool
            True - full Lets-Plot JS bundle will be added to the notebook. Use this option if you would like
            to work with notebook without the Internet connection.
            False - load Lets-Plot JS library from CDN.
            Default (None): 'connected' mode in production environment and 'offline' mode in dev environment.
        no_js : bool
            True - do not generate HTML+JS as an output - just static SVG image.
            Default: False.
        show_status : bool, optional, default False
            Whether to show status of loading of the Lets-Plot JS library.
            Only applicable when the Lets-Plot JS library is preloaded.
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
        if is_production():
            _settings.update(settings)
        else:
            _settings.update({'dev_' + key: value for key, value in settings.items()})
