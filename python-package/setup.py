#!/usr/bin/python

import os

from setuptools import Extension, Command
from setuptools import setup, find_packages

this_dir = os.path.dirname(os.path.abspath(__file__))
kotlin_bridge_src = os.path.join(this_dir, 'kotlin-bridge', 'datalore_plot_kotlin_bridge.c')

root_dir = os.path.dirname(this_dir)
kotlin_binaries_macosX64 = os.path.join(root_dir, 'python-extension', 'build', 'bin', 'macosX64', 'debugStatic')


def update_js():
    js_relative_path = ['build', 'classes', 'kotlin', 'js', 'main']
    js_projects = [
        # 'kotlin',
        # 'kotlin-logging',
        # 'datalore-plot-base-portable',
        # 'datalore-plot-base',
        'mapper-core',
        'visualization-base-svg',
        'visualization-base-svg-mapper',
        'visualization-base-canvas',
        'visualization-plot-common-portable',
        'visualization-plot-common',
        'visualization-plot-base-portable',
        'visualization-plot-base',
        'visualization-plot-builder-portable',
        'visualization-plot-builder',
        'visualization-plot-config-portable',
        'visualization-plot-config',
    ]

    def js_module_prefix(project_name):
        return 'datalore-plot-' if project_name in ['base', 'base-portable'] else ''

    from shutil import copy

    for prj in js_projects:
        js_name = js_module_prefix(prj) + prj + '.js'
        js_path = os.path.join(root_dir, prj, *js_relative_path, js_name)

        dst_dir = os.path.join(this_dir, 'datalore', 'package_data')
        if not os.path.isdir(dst_dir):
            os.mkdir(dst_dir)

        copy(js_path, os.path.join(this_dir, 'datalore', 'package_data'))


class UpdateJsCommand(Command):
    description = "Copy js files from the last build"
    user_options = []

    def initialize_options(self):
        pass

    def finalize_options(self):
        pass

    def run(self):
        update_js()


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

      package_data={
          "datalore": [
              "package_data/*",
          ],
      },

      ext_modules=[
          Extension('datalore_plot_kotlin_bridge',
                    include_dirs=[kotlin_binaries_macosX64],
                    libraries=['datalore_plot_python_extension'],
                    library_dirs=[kotlin_binaries_macosX64],
                    depends=['libdatalore_plot_python_extension_api.h'],
                    sources=[kotlin_bridge_src],
                    )
      ],

      cmdclass=dict(
          updatejs=UpdateJsCommand,
      ),
      )
