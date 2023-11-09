# Releasing the project

## Build machine requirements:

 - Windows 10+
 - Linux Ubuntu 20.04+
 - macOS 11.6+ (Apple Silicon processor)
 - JDK11
 - Python 3.7-3.12
 - `mingw-w64-x86_64-gcc` MSYS2 package (only for Windows)

**Important!** Linux requires more special setup: [README.md](tools%2FREADME.md)   

Python libraries are required:
 - `twine`
 - `setuptools`
 - `pyyaml`


## Make version

### 1. Update docs (for release):

For `RC` skip this step.

 - accept a pull request from `docs-x.x.x` branch on GitHub
 - move new release information from `future_changes.md` to `CHANGELOG.md`
 - clean up `future_changes.md` leaving the template.

### 2. Set release or pre-release version in the properties (remove _"-alpha"_ and _"dev"_): 

 - `version` in `build.gradle` (`X.X.X` or `X.X.X-rcN`)
 - `__version__` in `python-package/lets_plot/_version.py` (`X.X.X` or `X.X.XrcN`)
 
### 3. Build and copy JavaScript artifacts to the publish-directory:

 - `./gradlew js-package:copyForPublish`
 - check `js-package/distr` directory. It must contain `lets-plot.min.js` file.
  
### 4. Push the version changes and git tag:
         
 - `git add --all && git commit -m "Updated version vX.X.X" && git push` (or `vX.X.XrcN`)
 - `git tag vX.X.X && git push --tags` (or `vX.X.XrcN`)
 
### 5. Prepare to the next dev cycle: increment versions and add _"-alpha1"_ and _"dev1"_:
         
 - `version` in `build.gradle` (`X.X.X-alphaN`)
 - `__version__` in `python-package/lets_plot/_version.py` (`X.X.X.devN`)

### 6. Push new dev version to GitHub

## Build the project for publishing

**The next steps need to be reproduced on all supported platforms (`Mac`, `Linux` and `Windows`).**

### 1. Checkout repository in a new directory: 

 `git clone --branch vX.X.X git@github.com:JetBrains/lets-plot lets-plot-release`

### 2. Prepare config file with Python paths for release script

File must be in the YAML format and contain paths to bin and include directories for
each Python version: from 3.7 to 3.12.     
For **Mac arm64**: from 3.8 to 3.12.   
For **Linux**, it is enough to point one Python version.

**Example:**

`release_pythons.yml`

```yaml
py38-arm:
  bin_path: /Users/letsplotter/anaconda-arm/envs/py38/bin
  include_path: /Users/letsplotter/anaconda-arm/envs/py37/include/python3.8
py37-x64:
  bin_path: /Users/letsplotter/anaconda-x64/envs/py37/bin
  include_path: /Users/letsplotter/anaconda-x64/envs/py37/include/python3.7m
...
```

### 3. Run release script

For **Linux** check [README.md](tools%2FREADME.md) before build.

From the project root run Python script for release build. Pass a path to the config file
from step 2 as a script parameter.

```shell
./build_release.py ../release_pythons.yml
```

For **Windows** the command must be:

```shell
python .\build_release.py ..\release_pythons.yml
```

### 4. Check Python artifacts

The directory `python-package/dist` must contain Python release wheels:
 - Windows: `x64` wheels for Python versions 3.7-3.12
 - Linux: manylinux `x64` and `aarch64` wheels for Python versions 3.7-3.12
 - Mac: `x64` wheels for Python versions 3.7-3.12 and `arm64` wheel for 3.8-3.12


## Publish artifacts

Put `build_settings.yml` in the project root. See `build_settings.template.yml` for an example.   
Fill `pypi` and `sonatype` sections with credentials.

### 1. Python wheels (PyPi):

 - for testing (test.pypi.org):
 
 `./gradlew python-package-build:publishTestPythonPackage`

 - for production (pypi.org):
 
 `./gradlew python-package-build:publishProdPythonPackage`
 
### 2. JVM artifacts (Sonatype Nexus Repository)

Publish JVM artifacts from one of build machines:

```shell
./gradlew publishLetsPlotJvmCommonPublicationToMavenRepository \
          publishLetsPlotJvmJfxPublicationToMavenRepository \
          publishLetsPlotJvmBatikPublicationToMavenRepository \
          publishLetsPlotImageExportPublicationToMavenRepository \
          publishLetsPlotGISPublicationToMavenRepository \
          publishLetsPlotCoreModulesToMavenRepository
```

Check all artifacts were uploaded: https://oss.sonatype.org/#stagingRepositories

Close and release repository to the Maven Central:

`./gradlew findMavenStagingRepository closeAndReleaseMavenStagingRepository`

This operation can take up to 5 minutes.

 
## Add the GitHub release:
     
 - Open the link: https://github.com/JetBrains/lets-plot/releases/new
 - Fill `Tag version` and `Release title` with the released version "vX.X.X".
 - Fill the description field - copy from the CHANGELOG.md.
 - Add JS artifacts from the `js-package/distr` directory to the binaries box.
 - Select `This is a pre-release` checkbox if you are releasing a pre-release version.
 - Click `Publish release`.
 

## After release

 - remove build directory `lets-plot-release`
