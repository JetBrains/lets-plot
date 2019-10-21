# datalore-plot Python package

## Requirements

* `python >= 3.5` (with dev headers)
* `setuptools` (`pip install setuptools`)


## Configuration

All configuration are in `build_settings.yml` in the project root.

* `build_python_extension` - set to `yes` for building native python extension from `python-extension`
* `enable_python_package` - set to `yes` for working with python package
* `python.include_path` - path to python include path where Python.h located. 
For getting this path you can run `python -c "from sysconfig import get_paths as gp; print(gp()['include'])"`.
* `python.bin_path` - path to path to your python bin path. 
For getting this path you can run `python -c "from sysconfig import get_paths as gp; print(gp()['scripts'])"`.


## Enabling

* set `build_python_extension` and `enable_python_package` in `build_settings.yml` to `yes`


## Building

For building python wheel package you can run `:python-package-build:buildPythonPackage` gradle task


## Local installing

* build package

* run `:python-package-build:localInstallPythonPackage` gradle task (`./gradlew :python-package:localInstallPythonPackage`)