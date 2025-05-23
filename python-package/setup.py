#!/usr/bin/python

import os
import platform

from setuptools import Extension
from setuptools import setup, find_packages

kn_platform_build_dir = {
    ('Linux', 'x86_64'): 'linuxX64',
    ('Linux', 'aarch64'): 'linuxArm64',
    ('Darwin', 'x86_64'): 'macosX64',
    ('Darwin', 'arm64'): 'macosArm64',
    ('Windows', 'AMD64'): 'mingwX64',
}

this_dir = os.path.dirname(os.path.abspath(__file__))
root_dir = os.path.dirname(this_dir)
this_system = platform.system()

kotlin_bridge_src = os.path.join(this_dir, 'kotlin-bridge', 'lets_plot_kotlin_bridge.c')

binaries_build_path = os.path.join(root_dir, 'python-extension', 'build', 'bin',
                                   kn_platform_build_dir[(platform.system(), platform.machine())], 'releaseStatic')
imagemagick_lib_path = os.environ.get('LP_IMAGEMAGICK_PATH')
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
extra_link = []

if this_system == 'Darwin':
    static_link_libraries_list += ['c++']
    extra_link += [
        '-Wl,-rpath,/usr/lib',
        '-framework', 'Foundation',
        '-lz'
    ]
    if imagemagick_lib_path is not None:
        extra_link += [
            '-Wl,-rpath,@loader_path/../lib',
            f'-L{imagemagick_lib_path}/lib',
            '-lMagickWand-7.Q16HDRI',
            '-lMagickCore-7.Q16HDRI',
            '-lfontconfig',
            '-lfreetype',
            '-lexpat'
        ]

elif this_system == 'Windows':
    static_link_libraries_list += ['stdc++']
    # fix python package build with Kotlin v1.7.20 (and later) on Windows.
    extra_link += [
        '-static-libgcc',
        '-static',
        '-lbcrypt',
        '-lpthread',
        '-lz'
    ]

    if imagemagick_lib_path is not None:
        extra_link += [
            f'-L{imagemagick_lib_path}/lib',
            '-lMagickWand-7.Q16HDRI',
            '-lMagickCore-7.Q16HDRI',
            '-lfontconfig',
            '-lfreetype',
            '-lexpat',
            '-lurlmon',
            '-lgdi32',
            '-lz'
        ]

    # fix for "cannot find -lmsvcr140: No such file or directory" compiler error on Windows.
    import distutils.cygwinccompiler

    distutils.cygwinccompiler.get_msvcr = lambda: []

elif this_system == 'Linux':
    static_link_libraries_list += ['stdc++']
    extra_link += ['-lz']

    if imagemagick_lib_path is not None:
        extra_link += [
            f'-L{imagemagick_lib_path}/lib',
            '-lMagickWand-7.Q16HDRI',
            '-lMagickCore-7.Q16HDRI',
            '-lfontconfig',
            '-lfreetype',
            '-lexpat'
        ]

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
