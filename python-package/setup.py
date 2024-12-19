#!/usr/bin/python

import os
import platform

from setuptools import Extension
from setuptools import setup, find_packages

this_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(this_dir)
this_system = platform.system()

kotlin_bridge_src = os.path.join(this_dir, 'kotlin-bridge', 'lets_plot_kotlin_bridge.c')

python_extension_output_dir_name = {
    ('Linux', 'x86_64'): 'linuxX64',
    #('Linux', 'aarch64'): 'linuxArm64',
    #('Darwin', 'x86_64'): 'macX64',
    #('Darwin', 'arm64'): 'macArm64',
    #('Windows', 'AMD64'): 'winX64',
    #('Windows', 'x86_64'): 'winX64',
    #('Windows', 'AMD64'): 'winX64',
    #('Windows', 'x86'): 'winX86',
    #('Windows', 'x86_64'): 'winX64',
    #('Windows', 'AMD64'): 'winX64',
}[platform.system(), platform.machine()]

if python_extension_output_dir_name is None:
    raise ValueError("Unsupported platform: %s %s" % (platform.system(), platform.machine()))

binaries_build_path = os.path.join(root_dir, 'python-extension', 'build', 'bin', python_extension_output_dir_name, 'releaseStatic')
python_package = 'lets_plot'

def update_js():
    js_relative_path = ['js-package', 'build', 'dist', 'js', 'productionExecutable']
    js_libs = [
        'lets-plot',
    ]

    from shutil import copy

    for lib in js_libs:
        js_path = os.path.join(root_dir, *js_relative_path, lib + '.js')

        dst_dir = os.path.join(this_dir, python_package, 'package_data')
        if not os.path.isdir(dst_dir):
            os.mkdir(dst_dir)

        dst_path = os.path.join(dst_dir, lib + '.min.js')
        copy(js_path, dst_path)


version_locals = {}
with open(os.path.join(this_dir, python_package, '_version.py')) as f:
    exec(f.read(), {}, version_locals)

with open(os.path.join(root_dir, 'README.md'), encoding='utf-8') as f:
    long_description = f.read()

static_link_libraries_list = ['lets_plot_python_extension']

if this_system == 'Darwin':
    extra_link = []

elif this_system == 'Windows':
    static_link_libraries_list += ['stdc++']
    # fix python package build with Kotlin v1.7.20 (and later) on Windows.
    extra_link = ['-static-libgcc', '-static', '-lbcrypt', '-lpthread', '-lz']
    # fix for "cannot find -lmsvcr140: No such file or directory" compiler error on Windows.
    import distutils.cygwinccompiler
    distutils.cygwinccompiler.get_msvcr = lambda: []

elif this_system == 'Linux':
    static_link_libraries_list += ['stdc++']
    extra_link = ['-lz']

else:
    raise ValueError("Unsupported platform.")

# Adds JS package to Python wheel:
update_js()

setup(name='lets-plot',
      license="MIT",
      version=version_locals['__version__'],
      maintainer='JetBrains',
      maintainer_email='lets-plot@jetbrains.com',
      author='JetBrains',
      author_email='lets-plot@jetbrains.com',
      project_urls={
          "Github": "https://github.com/JetBrains/lets-plot",
          "Documentation": 'https://lets-plot.org',
      },
      url='https://lets-plot.org',
      description='An open source library for statistical plotting',
      long_description=long_description,
      long_description_content_type='text/markdown',
      keywords=["ggplot", "ggplot2", "geospatial", "geopandas", "geocoding"],
      classifiers=[
          "License :: OSI Approved :: MIT License",
          "Development Status :: 5 - Production/Stable",
          "Programming Language :: Python :: 3.8",
          "Programming Language :: Python :: 3.9",
          "Programming Language :: Python :: 3.10",
          "Programming Language :: Python :: 3.11",
          "Programming Language :: Python :: 3.12",
          "Programming Language :: Python :: 3.13",
          "Framework :: IPython",
          "Framework :: Jupyter",
          "Operating System :: MacOS",
          "Operating System :: POSIX :: Linux",
          "Operating System :: Microsoft :: Windows",
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
                    libraries=static_link_libraries_list,
                    library_dirs=[binaries_build_path],
                    depends=['liblets_plot_python_extension_api.h'],
                    sources=[kotlin_bridge_src],
                    extra_link_args=extra_link
                    )
      ],

      install_requires=[
          'pypng',  # for geom_imshow
          'palettable',  # for geom_imshow
      ],
      )
