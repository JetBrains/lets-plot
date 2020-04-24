#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
from typing import Dict


class FrontendContext:
    def configure(self, verbose: bool):
        pass

    def as_str(self, plot_spec: Dict) -> str:
        pass

    def show(self, plot_spec: Dict) -> str:
        pass
