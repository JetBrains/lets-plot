#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

from typing import Dict, Tuple

from ._frontend_ctx import FrontendContext
from ._mime_types import LETS_PLOT_JSON
from .._type_utils import standardize_dict


class IntellijPythonJsonContext(FrontendContext):

    def configure(self, verbose: bool):
        pass

    def show(self, plot_spec: Dict) -> str:
        plot_spec_std = standardize_dict(plot_spec)
        data_object = DisplayDataObject(plot_spec_std)

        # See intellij.python.helpers module in IDEA
        from datalore.display import display
        display(data_object)


class DisplayDataObject():

    def __init__(self, plot_spec: Dict) -> None:
        super().__init__()
        self.data_object = (LETS_PLOT_JSON, plot_spec)

    def _repr_display_(self) -> Tuple[str, Dict]:
        """
        Special method discovered and invoked by datalore.display.display()
        See: IDEA/community/python/helpers/pycharm_display/datalore/display/display_.py
        """
        return self.data_object
