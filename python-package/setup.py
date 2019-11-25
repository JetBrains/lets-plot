#!/usr/bin/python

import os
import platform

from setuptools import Command, Extension
from setuptools import setup, find_packages

this_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(this_dir)
kotlin_bridge_src = os.path.join(this_dir, 'kotlin-bridge', 'lets_plot_kotlin_bridge.c')

this_system = platform.system()
binaries_build_path = os.path.join(root_dir, 'python-extension', 'build', 'bin', 'native', 'debugStatic')

python_package = "lets_plot"


def update_js():
    js_relative_path = ['js-package', 'build', 'dist']
    js_libs = [
        'lets-plot-latest.min',
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

with open(os.path.join(root_dir, 'README_PYTHON.md'), encoding='utf-8') as f:
    long_description = f.read()

setup(name='lets-plot',
      license="MIT",
      version=version_locals['__version__'],
      maintainer='JetBrains',
      maintainer_email='lets-plot@jetbrains.com',
      author='JetBrains',
      author_email='lets-plot@jetbrains.com',
      project_urls={"Github": "https://github.com/JetBrains/lets-plot"},
      description='An open source library for statistical plotting',
      long_description=long_description,
      long_description_content_type='text/markdown',
      classifiers=[
          "License :: OSI Approved :: MIT License",
          "Development Status :: 4 - Beta",
          "Programming Language :: Python :: 3.7",
          "Programming Language :: Python :: 3.8",
          "Framework :: Jupyter",
          "Operating System :: MacOS",
          "Operating System :: POSIX :: Linux",
          "Programming Language :: Python :: Implementation :: CPython",
          "Topic :: Scientific/Engineering :: Visualization",
          "Intended Audience :: Science/Research",
          "Intended Audience :: Developers",
      ],

      packages=find_packages(exclude=('test',)),

      package_data={
          python_package: [
              "package_data/*",
          ],
      },

      ext_modules=[
          Extension('lets_plot_kotlin_bridge',
                    include_dirs=[binaries_build_path],
                    libraries=['lets_plot_python_extension', 'stdc++'],
                    library_dirs=[binaries_build_path],
                    depends=['liblets_plot_python_extension_api.h'],
                    sources=[kotlin_bridge_src],
                    )
      ],

      cmdclass=dict(
          update_js=UpdateJsCommand,
      ),
      )
