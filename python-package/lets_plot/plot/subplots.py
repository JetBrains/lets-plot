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
