## Release the project


### Make version


##### 1. Edit CHANGELOG.md file.

##### 2. Set production version in the properties (remove _"-SNAPSHOT"_ and _"dev"_): 

 - `version` and `js_artifact_version` in `build.gradle` 
 - `__version__` in `python-package/lets_plot/_version.py`
  
##### 3. Push the version changes and git tag:
         
 - `git add --all && git commit -m "Updated version vX.X.X" && git push`
 - `git tag vX.X.X && git push --tags`

##### 4. Prepare to the next dev cycle: increment versions and add _"-SNAPSHOT"_ and _"dev"_:

 - `version` and `js_artifact_version` in `build.gradle` 
 - `__version__` in `python-package/lets_plot/_version.py`

##### 5. Push new dev version to GitHub.


 
### Build the project for publishing

**The next steps need to be reproduced on all supported platforms (`Mac`, `Linux` and `Windows`).**   
**On Windows use `.\gradlew.bat` instead of `./gradlew` to run Gradle script.**

##### 1. Checkout repository in a new directory: 

 `git clone --branch vX.X.X git@github.com:JetBrains/lets-plot lets-plot-release`

##### 2. Put `build_settings.yml` in the project root. See `build_settings.template.yml` for an example.

##### 3. Edit `build_settings.yml`:

 - set both `build_python_extension` and `enable_python_package` options to `yes`
 - edit `bin` and `include` paths in the `Python settings` section: set paths to Python 3.6
 - check and set credentials in the `PyPI settings` and `Bintray settings` sections

##### 4. Build the project:

run `./gradlew build`

_As the result you will get artifacts for js-package and python-package (python wheel file built with Python 3.6)_

##### 5. Build python wheels with Python 3.7 and 3.8:

 - edit `bin` and `include` paths in the `Python settings` section: set paths to Python 3.7
 - run `./gradlew python-package-build:build`
 
Reproduce this steps for Python 3.8
 
_Then you'll get python wheel files built with Python 3.7 and 3.8._


##### 6. _(for Linux only)_ Build python wheels for Manylinux platform:

run `./gradlew python-package-build:buildManylinuxWheels`


### Publish artifacts

##### 1. JavaScript artifacts (Bintray):

 - `./gradlew js-package:js-publish-version:bintrayUpload`

 - `./gradlew js-package:js-publish-latest:bintrayUpload`

##### 2. Python wheels (PyPi):

 - for testing (test.pypi.org):
 
 `./gradlew python-package-build:publishTestPythonPackage`

 - for production (pypi.org):
 
 `./gradlew python-package-build:publishProdPythonPackage`
 
##### 3. JVM artifacts (Bintray/JCenter):

 - `./gradlew :jvm-package:jvm-publish-common:bintrayUpload`
 
 - `./gradlew :jvm-package:jvm-publish-jfx:bintrayUpload`
 
 - `./gradlew :jvm-package:jvm-publish-batik:bintrayUpload`
 
 
### Add the GitHub release:
 
 * Open the link: https://github.com/JetBrains/lets-plot/releases/new
 * Fill `Tag version` and `Release title` with released version `vX.X.X`
 * Fill a description field with the new changes from the CHANGELOG.md
 



### After release

 - remove build directory `lets-plot-release`
