#  Copyright (c) 2026. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import warnings
from pathlib import Path

import pytest

import lets_plot

_PACKAGE_DIR = Path(lets_plot.__file__).parent


@pytest.mark.parametrize(
    'path',
    sorted(_PACKAGE_DIR.rglob('*.py')),
    ids=lambda path: path.relative_to(_PACKAGE_DIR).as_posix(),
)
def test_module_compiles_without_warnings(path):
    # For example, an invalid escape sequence in a docstring makes Python
    # print a SyntaxWarning when the module is imported for the first time.
    with warnings.catch_warnings():
        warnings.simplefilter('error')
        compile(path.read_bytes(), str(path), 'exec')
