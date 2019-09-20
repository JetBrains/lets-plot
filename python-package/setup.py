#!/usr/bin/python

import os

from setuptools import Extension
from setuptools import setup, find_packages

this_dir = os.path.dirname(os.path.abspath(__file__))
kotlin_bridge_src = os.path.join(this_dir, 'kotlin-bridge', 'datalore_plot_kotlin_bridge.c')

# BUILD_PATH_MACOS_X64 = "../build/bin/macosX64/releaseStatic"
# BUILD_PATH_MACOS_X64 = os.path.dirname(os.path.dirname(this_dir)) + "python-extension/build/bin/macosX64/debugStatic"

root_dir = os.path.dirname(this_dir)
kotlin_binaries_macosX64 = os.path.join(root_dir, 'python-extension', 'build', 'bin', 'macosX64', 'debugStatic')

setup(name='datalore-plot',
      version='1.0.0',
      maintainer='JetBrains',
      maintainer_email='info@jetbrains.com',
      author='JetBrains',
      author_email='info@jetbrains.com',
      description='Graphing library for Python',
      long_description='Graphing library for Python',

      # package_dir={'': 'src'},
      # packages=[
      #     "datalore",
      #     "datalore.plot",
      # ],
      packages=find_packages(exclude=('test',)),

      ext_modules=[
          Extension('datalore_plot_kotlin_bridge',
                    include_dirs=[kotlin_binaries_macosX64],
                    libraries=['datalore_plot_python_extension'],
                    library_dirs=[kotlin_binaries_macosX64],
                    depends=['libdatalore_plot_python_extension_api.h'],
                    sources=[kotlin_bridge_src],
                    )
      ],
      )
