# datalore-plot Python package

## Enabling

* set `build_python_extension` and `enable_python_package` in `build_settings.yml` to `yes`


## Building

* install `python-dev` package (if you use Anaconda it's already installed)

* install python `setuptools` (run `pip install setuptools`)

* set `python.include_path` in `build_settings.yml`. 
For getting this path you can run `python -c "from sysconfig import get_paths as gp; print(gp()['include'])"`.
 
* check if `python.bin_path` in `build_settings.yml` contents true path to your python bin path (default is `/usr/bin`). 
For getting this path you can run `python -c "from sysconfig import get_paths as gp; print(gp()['scripts'])"`.

* build project with Gradle (run `./gradlew build`)


## Local installing

* build package

* run `:python-package-build:localInstallPythonPackage` gradle task (`./gradlew :python-package:localInstallPythonPackage`)


## Test publishing to test.pypi.org

* build package

* set `pypi.test.username` and `pypi.test.password` in `build_settings.yml` with your PyPI credentials 

* run `:python-package-build:publishTestPythonPackage`


## Publishing to pypi.org

* build package

* set `pypi.prod.username` and `pypi.prod.password` in `build_settings.yml` with your PyPI credentials 

* run `:python-package-build:publishProdPythonPackage` 