# datalore-plot Python package

## Building

* install `python-dev` package (if you use Anaconda it's already installed)

* install python `setuptools` (run `pip install setuptools`)

* set `python_include_path` in `gradle.properties`. 
For getting this path you can run `python -c "from sysconfig import get_paths as gp; print(gp()['include'])"`.
 
* check if `python_bin_path` in `gradle.properties` contents true path to your python bin path (default is `/usr/bin`). 
For getting this path you can run `python -c "from sysconfig import get_paths as gp; print(gp()['scripts'])"`.

* build project with Gradle (run `./gradlew build`)


## Local installing

* build package

* run `:python-package:localInstallPythonPackage` gradle task (`./gradlew :python-package:localInstallPythonPackage`)


## Publishing

* build package

* set `pypi_username` and `pypi_password` in `gradle.properties` with your PyPI credentials

* set `python_repository_url` in `gradle.properties` if you need publish to not standard PyPI repository (i.e. `https://test.pypi.org/legacy/` for testing publishing). 

* run `:python-package:publishPythonPackage` 