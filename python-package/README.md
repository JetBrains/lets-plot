# lets-plot Python package

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

Gradle `build` in the sibling project **python-package-build**

or just run shell command (if the changes are only in the python code):

`python setup.py update_js bdist_wheel`

## Installing locally

* create / activate python environment (if needed)
* run shell commands: 

`pip uninstall lets-plot`

`pip install --no-index --find-links=dist/ lets-plot`
 
## Test in Jupyther

When "dev" version, `lets-plot` embeds current "dev" js into Jupyter notebook.

If necessary, this default can be temporarily overwritten by editing `dev_xxx` settings in `_global_settings.py`

The "dev" version of js library can be served from `dist` folder of **js-package** project like:

```shell script
# Start local web-server to serve dev js script:
$ cd lets-plot/js-package/build/dist
$ python -m http.server 8080
```
 
## Example code

```python
from datalore_plot import *
data = dict(time=['Lunch', 'Lunch', 'Dinner', 'Dinner', 'Dinner'])
p = ggplot(data) + geom_bar(aes(x='time', fill='..count..'))
p += scale_fill_hue()
p
```
