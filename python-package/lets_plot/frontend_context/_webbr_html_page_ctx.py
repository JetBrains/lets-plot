#
# Copyright (c) 2019. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
import io
import webbrowser
import tempfile
from typing import Dict

from ._frontend_ctx import FrontendContext
from .. import _kbridge as kbr


class WebBrHtmlPageContext(FrontendContext):

    def __init__(self, exec: str, new: bool) -> None:
        super().__init__()
        self.exec = exec
        self.new = new

    def show(self, plot_spec: Dict) -> str:
        html_page = kbr._generate_static_html_page(plot_spec, iframe=False)

        path = tempfile.NamedTemporaryFile(mode='w+t', suffix=".html", delete=False)
        try:
            io.open(path.name, 'w+t').write(html_page)
            webbrowser.get(self.exec).open('file://' + path.name, new=1 if self.new else 2)
        finally:
            path.close()
