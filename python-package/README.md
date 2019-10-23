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


## Building

`:python-package-build:build` gradle task defined in the sibling project `python-package-build`

or just run shell command:

`python setup.py update_js bdist_wheel`

## Local installing

* create / activate python environment (if needed)
* run shell commands: 

`pip uninstall datalore-plot`

`pip install --no-index --find-links=dist/ datalore-plot`
 
## Jupyther

TBD

