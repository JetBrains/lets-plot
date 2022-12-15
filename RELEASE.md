## Releasing the project


### Make version

##### 1. Update CHANGELOG.md file.

##### 2. Set release or pre-release version in the properties (remove _"-alpha"_ and _"dev"_): 

 - `version` in `build.gradle` (`X.X.X` or `X.X.X-rcN`)
 - `js_artifact_version` in `build.gradle` (`X.X.X` or `X.X.XrcN`)
 - `__version__` in `python-package/lets_plot/_version.py` (`X.X.X` or `X.X.XrcN`)
 
##### 3. Build and copy JavaScript artifacts to the publish-directory:

 - `./gradlew js-package:copyForPublish`
 - check `js-package/distr` directory. It must contain `lets-plot.js` and `lets-plot.min.js` files.
  
##### 4. Push the version changes and git tag:
         
 - `git add --all && git commit -m "Updated version vX.X.X" && git push` (or `vX.X.XrcN`)
 - `git tag vX.X.X && git push --tags` (or `vX.X.XrcN`)
 
##### 5. Prepare to the next dev cycle: increment versions and add _"-alpha1"_ and _"dev1"_:
         
 - `version` in `build.gradle` (`X.X.X-alphaN`)
 - `js_artifact_version` in `build.gradle` (`X.X.X.devN`)
 - `__version__` in `python-package/lets_plot/_version.py` (`X.X.X.devN`)

##### 6. Push new dev version to GitHub.


### Build the project for publishing

**The next steps need to be reproduced on all supported platforms (`Mac`, `Linux` and `Windows`).**   
**On Windows use `.\gradlew.bat` instead of `./gradlew` to run Gradle script.**

##### 1. Checkout repository in a new directory: 

 `git clone --branch vX.X.X git@github.com:JetBrains/lets-plot lets-plot-release`

##### 2. Put `build_settings.yml` in the project root. See `build_settings.template.yml` for an example.

##### 3. Edit `build_settings.yml`:

 - set both `build_python_extension` and `enable_python_package` options to `yes`
 - edit `bin` and `include` paths in the `Python settings` section: set paths to Python 3.7
 - check and set credentials in the `PyPI settings` and `Sonatype settings` sections

##### 4. Build the project:

run `./gradlew build`

For Linux without graphical environment add parameter to exclude JFX test:

`./gradlew build -x :vis-svg-mapper-jfx:jvmTest`

or tests will stuck in running state.

_As the result you will get artifacts for jvm-package and python-package (python wheel file built with Python 3.7)_

##### 5. Build python wheels with Python 3.8, 3.9, 3.10 and 3.11:

 - edit `bin` and `include` paths in the `Python settings` section: set paths to Python 3.8
 - run `./gradlew python-package-build:build`
 
Reproduce this steps for Python 3.9, 3.10 and 3.11

_Then you'll get python wheel files built with Python 3.7, 3.8, 3.9, 3.10 and 3.11._

##### 6. _(for Linux only)_ Build python wheels for Manylinux platform:

run `./gradlew python-package-build:buildManylinuxWheels`


### Publish artifacts

##### 1. Python wheels (PyPi):

 - for testing (test.pypi.org):
 
 `./gradlew python-package-build:publishTestPythonPackage`

 - for production (pypi.org):
 
 `./gradlew python-package-build:publishProdPythonPackage`
 
##### 2. JVM artifacts (Sonatype Nexus Repository):

 - `./gradlew publishLetsPlotJvmCommonPublicationToMavenRepository`
 
 - `./gradlew publishLetsPlotJvmJfxPublicationToMavenRepository`
 
 - `./gradlew publishLetsPlotJvmBatikPublicationToMavenRepository`
 
 - `./gradlew publishLetsPlotImageExportPublicationToMavenRepository`

 - `./gradlew publishLetsPlotGISPublicationToMavenRepository`

 - `./gradlew publishKotlinApiDependenciesToMavenRepository`

Check all artifacts were uploaded: https://oss.sonatype.org/#stagingRepositories

Close and release repository to the Maven Central:

`./gradlew closeAndReleaseRepository`

This operation can take up to 5 minutes.

 
### Add the GitHub release:
     
 - Open the link: https://github.com/JetBrains/lets-plot/releases/new
 - Fill `Tag version` and `Release title` with the released version "vX.X.X".
 - Fill the description field - copy from the CHANGELOG.md.
 - Add JS artifacts from the `js-package/distr` directory to the binaries box.
 - Select `This is a pre-release` checkbox if you are releasing a pre-release version.
 - Click `Publish release`.
 

### After release

 - remove build directory `lets-plot-release`
