# lets-plot Python package

## Requirements

* `python >= 3.5` (with dev headers)
* `setuptools` (`pip install setuptools`)


## Configuration

Edit `build_settings.yml` in the project root:

* `build_python_extension` - set to `yes` for building native Python extension from `python-extension`.
* `enable_python_package` - set to `yes` for working with Python package.
* `python.include_path` - path to Python include directory where Python.h located. 
To get this path you can run `python -c "from sysconfig import get_paths as gp; print(gp()['include'])"`.
* `python.bin_path` - path to your Python bin directory. 
To get this path you can run `python -c "from sysconfig import get_paths as gp; print(gp()['scripts'])"`.


## Building

Gradle `build` in the sibling project **python-package-build**

or just run shell command (if the changes are only in the Python code):

`python setup.py update_js bdist_wheel`

## Installing locally

* change dir to `lets-plot/python-package`
* create / activate Python environment (if needed)
* run shell command: 

`pip install --no-index --find-links=dist/ lets-plot --no-deps --force-reinstall`
 
## Test in Jupyter

When the "dev" version is built, `lets-plot` embeds current "dev" js into Jupyter notebook.

If necessary, this default can be temporarily overwritten by editing `dev_xxx` settings in `_global_settings.py`

The "dev" version of js library can be served from `dist` folder of **js-package** project like:

```shell script
# Start local web-server to serve dev js script:
$ cd lets-plot
$ python -m http.server 8080
```
 
## Example code

```python
from lets_plot import *
data = dict(time=['Lunch', 'Lunch', 'Dinner', 'Dinner', 'Dinner'])
p = ggplot(data) + geom_bar(aes(x='time', fill='..count..'))
p += scale_fill_hue()
p
```
