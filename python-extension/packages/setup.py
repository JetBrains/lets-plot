#!/usr/bin/python

import os
from setuptools import Extension
from setuptools import setup

HERE = os.path.dirname(os.path.abspath(__file__))
# KOTLIN_BRIDGE_SRC = os.path.dirname(HERE) + "/kotlin-bridge/datalore_plot_kotlin_bridge.c"
KOTLIN_BRIDGE_SRC = HERE + "/kotlin-bridge/datalore_plot_kotlin_bridge.c"

# BUILD_PATH_MACOS_X64 = "../build/bin/macosX64/releaseStatic"
BUILD_PATH_MACOS_X64 = "../build/bin/macosX64/debugStatic"


setup(name='datalore-plot',
      version='1.0.0',
      maintainer = 'JetBrains',
      maintainer_email = 'info@jetbrains.com',
      author = 'JetBrains',
      author_email = 'info@jetbrains.com',
      description = 'Graphing library for Python',
      long_description = 'Graphing library for Python',

      package_dir={'':'python-runtime'},   # tell distutils packages are under src
      packages=[
            "datalore",
            "datalore.plot",
      ],

      # data_files=[('', [BUILD_PATH_MACOS_X64 + '/libdatalore_plot_python_extension.dylib'])],

      # data_files=[("/Library/Python/2.7/site-packages/", ['libserver.dylib'])],

      ext_modules=[
          Extension('datalore_plot_kotlin_bridge',
                    include_dirs = [BUILD_PATH_MACOS_X64],
                    libraries = ['datalore_plot_python_extension'],
                    library_dirs = [BUILD_PATH_MACOS_X64],
                    depends = ['libdatalore_plot_python_extension_api.h'],
                    sources = [KOTLIN_BRIDGE_SRC],
                    )
      ],
)
