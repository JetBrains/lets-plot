#
#  Copyright (c) 2024. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#
_global_theme = None


def _set_global_theme(theme):
    global _global_theme
    _global_theme = theme


def _get_global_theme():
    return _global_theme
