# Lets-Plot Python Package

## Requirements

* `python >= 3.8` (with dev headers)
* `setuptools` (`pip install setuptools`)
* `build` (`pip install build`)


## Configuration

Edit `local.properties` in the project root:

* `architecture` - set to `arm64` or `x86_64` depending on your Python architecture.
* `enable_python_package` - set to `true` for working with Python package.
* `python.include_path` - path to Python include directory where Python.h located. 
To get this path you can run `python -c "from sysconfig import get_paths as gp; print(gp()['include'])"`.
* `python.bin_path` - path to your Python bin directory. 
To get this path you can run `python -c "from sysconfig import get_paths as gp; print(gp()['scripts'])"`.


## Building

There are two ways to build the Python package:

### 1. Full Build with Gradle

This method builds both the Kotlin multiplatform binaries and the Python package:

```bash
./gradlew :python-package-build:build
```

**Note:** The target Python version for this build is determined by the configuration in `local.properties` file (see **Configuration** section above).

### 2. Python Package Only Build

If you've only made changes to Python code, you can rebuild just the Python wheel:

```bash
python -m build -w
```

**Prerequisites for Python build:**
- Must be in the `python-package` directory
- Need an active Python environment with the `build` package installed
- The resulting wheel will target the Python version of your active environment


## Installing locally

* change dir to `lets-plot/python-package`
* create / activate a Python environment (if needed)
* run shell command: 

`pip install --no-index --find-links=dist/ lets-plot --no-deps --force-reinstall`
 
## Test in Jupyter

When the "dev" version is built, `lets-plot` embeds current "dev" js into Jupyter notebook.

If necessary, this default can be temporarily overwritten by editing `dev_xxx` settings in `_global_settings.py`

The "dev" version of JS library can be served from `js-package/build/dist/js/developmentExecutable` folder (in **js-package** project):

```
$ cd lets-plot

# Build developened JS package:
$ ./gradlew js-package:jsBrowserDevelopmentWebpack
  
# Start local web-server to serve dev js script:
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
