## Release the project.


### Make version.


##### 1. Clean project dir.

##### 2. Edit CHANGELOG.md file.
  
##### 3. Change project version in file (remove `dev`) and push it:  

`python-package/datalore_plot/_version.py`

##### 4. Push git tag.

##### 5. Change version and add `dev` to it in file:  

`python-package/datalore_plot/_version.py`
 
##### 6. Push new dev version to GitHub.


 
### Build the project for publishing

##### 1. Checkout repository in a new directory: 


 `git clone --branch vX.X.X git@github.com:JetBrains/datalore-plot datalore-plot-release`


##### 2. Edit `build_settings.yml`:

 - set `js_artifact_version` to the actual vesrion
 - set both `build_python_extension` and `enable_python_package` options in `yes`
 - edit `bin` and `include` paths in the `Python settings` section: set paths to the Python 3.7
 - check and set credentials in the `PyPI settings` and `Bintray settings` sections

##### 3. Build the project:

run `./gradlew build`

_As the result you will get artifacts for the js-package and python-package (python wheel file, built with Python 3.7)_

##### 4. Build python wheels with Python 3.8:

 - edit `bin` and `include` paths in the `Python settings` section: set paths to the Python 3.8
 - run `./gradlew python-package-build:build`
 
_This step will add python wheel file, built with Python 3.8._


##### 5. _(for Linux users only at present)_ Build python wheels for Manylinux platform :

run `./gradlew python-package-build:buildManylinuxWheels`


### Publish artifacts:

##### 1. JavaScript artifacts (Bintray):

run `./gradlew :js-package:bintrayUpload`

##### 2. Python wheels (PyPi):

 - for testing (test.pypi.org):
 
 `./gradlew python-package-build:publishTestPythonPackage`

 - for production (pypi.org):
 
 `./gradlew python-package-build:publishProdPythonPackage`


### After release:

 - test published artifacts
 - remove build directory `datalore-plot-release`
