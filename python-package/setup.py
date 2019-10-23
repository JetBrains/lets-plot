#!/usr/bin/python

import os
import platform

from setuptools import Command, Extension
from setuptools import setup, find_packages

this_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(this_dir)
kotlin_bridge_src = os.path.join(this_dir, 'kotlin-bridge', 'datalore_plot_kotlin_bridge.c')

# ToDo: option: debug / release
this_system = platform.system()
if this_system == "Linux":
    binaries_build_path = os.path.join(root_dir, 'python-extension', 'build', 'bin', 'linuxX64', 'debugStatic')
elif this_system == "Darwin":
    binaries_build_path = os.path.join(root_dir, 'python-extension', 'build', 'bin', 'macosX64', 'debugStatic')
else:
    raise RuntimeError("Unsupported platform {}".format(this_system))

python_package = "datalore_plot"


def update_js():
    js_relative_path = ['js-package', 'build', 'dist']
    js_libs = [
        'datalore-plot-latest.min',
    ]

    from shutil import copy

    for lib in js_libs:
        js_path = os.path.join(root_dir, *js_relative_path, lib + '.js')

        dst_dir = os.path.join(this_dir, python_package, 'package_data')
        if not os.path.isdir(dst_dir):
            os.mkdir(dst_dir)

        copy(js_path, os.path.join(this_dir, python_package, 'package_data'))


class UpdateJsCommand(Command):
    description = "Copy datalore plot js files from last build"
    user_options = []

    def initialize_options(self):
        pass

    def finalize_options(self):
        pass

    def run(self):
        update_js()


version_locals = {}
with open(os.path.join(this_dir, python_package, '_version.py')) as f:
    exec(f.read(), {}, version_locals)

setup(name='datalore-plot',
      version=version_locals['__version__'],
      maintainer='JetBrains',
      maintainer_email='datalore-plot@jetbrains.com',
      author='JetBrains',
      author_email='datalore-plot@jetbrains.com',
      description='Graphing library for Python',
      long_description='Graphing library for Python',

      packages=find_packages(exclude=('test',)),

      package_data={
          python_package: [
              "package_data/*",
          ],
      },

      ext_modules=[
          Extension('datalore_plot_kotlin_bridge',
                    include_dirs=[binaries_build_path],
                    libraries=['datalore_plot_python_extension', 'stdc++'],
                    library_dirs=[binaries_build_path],
                    depends=['libdatalore_plot_python_extension_api.h'],
                    sources=[kotlin_bridge_src],
                    )
      ],

      cmdclass=dict(
          update_js=UpdateJsCommand,
      ),
      )
