#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

# noinspection PyUnresolvedReferences
from typing import Dict

from lets_plot.plot.core import DummySpec
from lets_plot.plot.core import FeatureSpec
from lets_plot.plot.core import FeatureSpecArray
from lets_plot.plot.core import _specs_to_dict
from lets_plot.plot.core import _theme_dicts_merge
from lets_plot.plot.core import _to_svg, _to_html, _export_as_raster

__all__ = ['SupPlotsSpec']


class SupPlotsLayoutSpec:
    """
    Plots layout specification used in constructing subplots figure.
    """

    def __init__(self, name: str, **kwargs):
        """Initialize self."""
        self.__props = {}
        self.__props.update(**kwargs)
        self.__props['name'] = name

    def as_dict(self) -> Dict:
        return _specs_to_dict(self.__props)


class SupPlotsSpec(FeatureSpec):
    """
    Subplots figure specification.

    See: `gggrid()`
    """

    @classmethod
    def duplicate(cls, other):
        dup = SupPlotsSpec(
            figures=other.__figures,
            layout=other.__layout
        )
        dup.props().update(other.props())
        return dup

    def __init__(self, figures: list, layout: SupPlotsLayoutSpec):
        """Initialize self."""
        super().__init__('subplots', None)
        self.__figures = list(figures)
        self.__layout = layout

    def __add__(self, other):
        """
        """

        if isinstance(other, DummySpec):
            # nothing
            return self

        elif isinstance(other, FeatureSpecArray):
            supplots = SupPlotsSpec.duplicate(self)
            for spec in other.elements():
                supplots = supplots.__add__(spec)
            return supplots

        elif isinstance(other, FeatureSpec) and other.kind in ["ggsize", "theme"]:

            supplots = SupPlotsSpec.duplicate(self)
            # ToDo: duplication!
            if other.kind == 'theme':
                new_theme_options = {k: v for k, v in other.props().items() if v is not None}
                if 'name' in new_theme_options:
                    # keep the previously specified flavor
                    if supplots.props().get('theme', {}).get('flavor', None) is not None:
                        new_theme_options.update({'flavor': supplots.props()['theme']['flavor']})

                    # pre-configured theme overrides existing theme altogether.
                    supplots.props()['theme'] = new_theme_options
                else:
                    # merge themes
                    old_theme_options = supplots.props().get('theme', {})
                    supplots.props()['theme'] = _theme_dicts_merge(old_theme_options, new_theme_options)

                return supplots

            # add feature to properties
            supplots.props()[other.kind] = other
            return supplots

        return super().__add__(other)

    def as_dict(self):
        d = super().as_dict()
        d['kind'] = self.kind
        d['layout'] = self.__layout.as_dict()
        d['figures'] = [figure.as_dict() if figure is not None else None for figure in self.__figures]

        return d

    def _repr_html_(self):
        """
        Special method discovered and invoked by IPython.display.display.
        """
        from ..frontend_context._configuration import _as_html
        return _as_html(self.as_dict())

    def show(self):
        """
        Draw all plots currently in this 'bunch'.
        """
        from ..frontend_context._configuration import _display_plot
        _display_plot(self)

    def to_svg(self, path) -> str:
        """
        Export all plots currently in this 'bunch' to a file or file-like object in SVG format.

        Parameters
        ----------
        self : `SupPlotsSpec`
           Subplots specification to export.
        path : str, file-like object
            Сan be either a string specifying a file path or a file-like object.
            If a string is provided, the result will be exported to the file at that path.
            If a file-like object is provided, the result will be exported to that object.

        Returns
        -------
        str
            Absolute pathname of created file or None if file-like object is provided.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 13

            import numpy as np
            import io
            import os
            from lets_plot import *
            from IPython import display
            LetsPlot.setup_html()
            n = 60
            np.random.seed(42)
            x = np.random.choice(list('abcde'), size=n)
            y = np.random.normal(size=n)
            p = ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + geom_jitter()
            file_like = io.BytesIO()
            p.to_svg(file_like)
            display.SVG(file_like.getvalue())
        """
        return _to_svg(self, path)

    def to_html(self, path, iframe: bool = None) -> str:
        """
        Export all plots currently in this 'bunch' to a file or file-like object in HTML format.

        Parameters
        ----------
        self : `SupPlotsSpec`
            Subplots specification to export.
        path : str, file-like object
            Сan be either a string specifying a file path or a file-like object.
            If a string is provided, the result will be exported to the file at that path.
            If a file-like object is provided, the result will be exported to that object.
        iframe : bool, default=False
            Whether to wrap HTML page into a iFrame.

        Returns
        -------
        str
            Absolute pathname of created file or None if file-like object is provided.

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 12

            import numpy as np
            import io
            import os
            from lets_plot import *
            LetsPlot.setup_html()
            n = 60
            np.random.seed(42)
            x = np.random.choice(list('abcde'), size=n)
            y = np.random.normal(size=n)
            p = ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + geom_jitter()
            file_like = io.BytesIO()
            p.to_html(file_like)
        """
        return _to_html(self, path, iframe)

    def to_png(self, path, scale=None, w=None, h=None, unit=None, dpi=None) -> str:
        """
        Export all plots currently in this 'bunch' to a file or file-like object in PNG format.

        Parameters
        ----------
        self : `SupPlotsSpec`
            Subplots specification to export.
        path : str, file-like object
            Сan be either a string specifying a file path or a file-like object.
            If a string is provided, the result will be exported to the file at that path.
            If a file-like object is provided, the result will be exported to that object.
        scale : float
            Scaling factor for raster output. Default value is 2.0.
        w : float, default=None
            Only applicable when exporting to PNG or PDF.
        h : float, default=None
            Height of the output image in units.
            Only applicable when exporting to PNG or PDF.
        unit : {'in', 'cm', 'mm'}, default=None
            Unit of the output image. One of: 'in', 'cm', 'mm'.
            Only applicable when exporting to PNG or PDF.
        dpi : int, default=None
            Resolution in dots per inch.
            Only applicable when exporting to PNG or PDF.

        Returns
        -------
        str
            Absolute pathname of created file or None if file-like object is provided.

        Notes
        -----
        Export to PNG file uses the CairoSVG library.
        CairoSVG is free and distributed under the LGPL-3.0 license.
        For more details visit: https://cairosvg.org/documentation/

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 13

            import numpy as np
            import io
            import os
            from lets_plot import *
            from IPython import display
            LetsPlot.setup_html()
            n = 60
            np.random.seed(42)
            x = np.random.choice(list('abcde'), size=n)
            y = np.random.normal(size=n)
            p = ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + geom_jitter()
            file_like = io.BytesIO()
            p.to_png(file_like)
            display.Image(file_like.getvalue())
        """
        return _export_as_raster(self, path, scale, 'png', w=w, h=h, unit=unit, dpi=dpi)

    def to_pdf(self, path, scale=None, w=None, h=None, unit=None, dpi=None) -> str:
        """
        Export all plots currently in this 'bunch' to a file or file-like object in PDF format.

        Parameters
        ----------
        self : `SupPlotsSpec`
            Subplots specification to export.
        path : str, file-like object
            Сan be either a string specifying a file path or a file-like object.
            If a string is provided, the result will be exported to the file at that path.
            If a file-like object is provided, the result will be exported to that object.
        scale : float
            Scaling factor for raster output. Default value is 2.0.
        w : float, default=None
            Width of the output image in units.
            Only applicable when exporting to PNG or PDF.
        h : float, default=None
            Height of the output image in units.
            Only applicable when exporting to PNG or PDF.
        unit : {'in', 'cm', 'mm'}, default=None
            Unit of the output image. One of: 'in', 'cm', 'mm'.
            Only applicable when exporting to PNG or PDF.
        dpi : int, default=None
            Resolution in dots per inch.
            Only applicable when exporting to PNG or PDF.

        Returns
        -------
        str
            Absolute pathname of created file or None if file-like object is provided.

        Notes
        -----
        Export to PDF file uses the CairoSVG library.
        CairoSVG is free and distributed under the LGPL-3.0 license.
        For more details visit: https://cairosvg.org/documentation/

        Examples
        --------
        .. jupyter-execute::
            :linenos:
            :emphasize-lines: 12

            import numpy as np
            import io
            import os
            from lets_plot import *
            LetsPlot.setup_html()
            n = 60
            np.random.seed(42)
            x = np.random.choice(list('abcde'), size=n)
            y = np.random.normal(size=n)
            p = ggplot({'x': x, 'y': y}, aes(x='x', y='y')) + geom_jitter()
            file_like = io.BytesIO()
            p.to_pdf(file_like)
        """
        return _export_as_raster(self, path, scale, 'pdf', w=w, h=h, unit=unit, dpi=dpi)
