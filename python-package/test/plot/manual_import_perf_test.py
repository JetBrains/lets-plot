#  Copyright (c) 2026. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import builtins
import time

libs = {''}

_real_import = builtins.__import__
def _tracing_import(name, *args, **kwargs):
    if False:
        libs.add(name)
        if name == 'matplotlib' or name.startswith('matplotlib.') or name == 'numpy' or name.startswith('numpy.') or name == 'pandas' or name.startswith('pandas.') or name == 'geopandas' or name.startswith('geopandas.'):
            print(f'=== Import of {name} ===')
            traceback.print_stack()
    return _real_import(name, *args, **kwargs)

builtins.__import__ = _tracing_import

t0 = time.time()
print(f'lets_plot import took {time.time() - t0:.3f}s')
for lib in sorted(libs):
    print(lib)
