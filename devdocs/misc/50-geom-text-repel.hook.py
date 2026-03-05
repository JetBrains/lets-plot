#  Copyright (c) 2026. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
"""
IPython startup hook for lets-plot:
patch geom_text_repel() and geom_label_repel() to add default values:
    max_iter=200
    max_time=-1
if they were not provided explicitly.

Safe to load multiple times.
"""

from functools import wraps


def _patch_one_function(module, func_name, *, seed=42, max_iter=200, max_time=-1):
    geom = getattr(module, func_name, None)
    if geom is None:
        print(f"{func_name} not found")
        return False

    patch_flag = f"_jb_{func_name}_hook_patched"

    # Avoid double patching
    if getattr(geom, patch_flag, False):
        print(f"{func_name}: already patched")
        return False

    @wraps(geom)
    def _wrapped(*args, **kwargs):
        kwargs.setdefault("seed", seed)
        kwargs.setdefault("max_iter", max_iter)
        kwargs["max_time"] = max_time
        return geom(*args, **kwargs)

    setattr(_wrapped, patch_flag, True)
    setattr(_wrapped, f"_jb_{func_name}_hook_original", geom)

    setattr(module, func_name, _wrapped)
    print(f"{func_name}: patched")
    return True


def _patch_lets_plot_repels():
    try:
        import lets_plot
    except Exception:
        print("lets_plot not available")
        return

    _patch_one_function(lets_plot, "geom_text_repel", seed=42, max_iter=200, max_time=-1)
    _patch_one_function(lets_plot, "geom_label_repel", seed=42, max_iter=200, max_time=-1)


_patch_lets_plot_repels()