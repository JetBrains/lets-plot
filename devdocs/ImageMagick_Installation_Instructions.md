## ImageMagick installation instructions

This document covers steps to prepare a build system for working with ImageMagick support enabled.
During the preparation, you will build and install ImageMagick and its dependencies from source using a bash script.

## 1. Prerequisites

Various systems require additional environment setup to support ImageMagick installation.
<br>

### macOS

#### 1. Install [Homebrew](https://brew.sh)

#### 2. Install required libraries:
```shell
brew install automake libtool gettext pkgconf zlib
```

### Linux

#### 1. Install [Docker](https://docs.docker.com/engine/install/)

Set up Docker to run as a non-root user:  
https://docs.docker.com/engine/install/linux-postinstall/

If you choose not to do this, you will need to run the initialization script with `sudo`.  
**Note:** Be sure to fix the ownership of the affected directories afterward.

#### 2. Install required libraries (example for APT-based systems):
```shell
sudo apt update && sudo apt install -y automake libtool gperf gettext autopoint pkg-config
```

### Windows

#### 1. Install [MSYS2](https://www.msys2.org)

#### 2. Add MSYS2 directories to the `PATH` environment variable (example):
```
C:\msys64\mingw64\bin  
C:\msys64\usr\bin
```

#### 3. Run `mingw64.exe` and install libraries:
```shell
pacman -S git gperf mingw-w64-x86_64-gcc mingw-w64-x86_64-autotools mingw-w64-x86_64-python
```

<br>

## 2. Run ImageMagick installation

All commands shown below should be executed from the project root.

Linux and macOS users can simply run the Gradle task:

```shell
./gradlew :initImageMagick
```

Windows users should run `mingw64.exe` and then execute the script directly:

```shell
./platf-imagick/init_imagemagick.sh
```

As a result, the script will download dependency sources into the `platf-imagick/build` directory,  
then build and install them to the default directory `platf-imagick/deps` (which is included in `.gitignore`).

To install ImageMagick and its dependencies to a custom directory, run the script directly and pass an absolute path:

```shell
./platf-imagick/init_imagemagick.sh /home/letsplotter/applications/ImageMagick
```

Linux users who have not set up Docker for non-root access should run the script with `sudo`:

```shell
sudo ./platf-imagick/init_imagemagick.sh
```

**Important:** Affected directories will have root ownership. Be sure to fix it afterward:

```shell
sudo chown -R $USER:$USER platf-imagick/build
sudo chown -R $USER:$USER platf-imagick/deps  # Or absolute path to your custom directory
```
<br>

## 3. Finally

Now your host is ready to build Lets-Plot with ImageMagick support enabled.
