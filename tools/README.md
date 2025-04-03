# Tools Directory

This directory contains scripts for building Python release artifacts for Linux.
The builds are performed inside Docker containers based on `manylinux2014` ([PEP 599](https://peps.python.org/pep-0599/)).

Our project requires **ImageMagick**, which is not included in the standard `manylinux2014` images.
To ensure it is available, we build **custom Docker images** for both `x86_64` and `aarch64` architectures,
incorporating ImageMagick.

Kotlin Native does not provide direct support for Linux `aarch64` ([KT-36871](https://youtrack.jetbrains.com/issue/KT-36871)).
Because of this, the `aarch64` build must be executed on an `x86_64` host using **QEMU**.<br>
To enable this, the `qemu-user-static` utility must be added when building the `aarch64` Docker image.


### Additional Resources
- **ImageMagick**: [Official website](https://imagemagick.org), [License information](https://imagemagick.org/script/license.php)
- **Manylinux**: [GitHub repository](https://github.com/pypa/manylinux)


### Directory contains:

* `run_manylinux_docker.sh` - runs Docker 'manylinux' containers (script runs automatically).
* `build_manylinux_wheel.sh` - builds Python wheels inside 'manylinux' containers (script runs automatically).
* **manylinux-arm-image** - files for building a cross-architecture `manylinux` image with ImageMagick:
    - `Dockerfile`
    - `build_image.sh` - builds `manylinux_aarch64` image, which can be run on x64 host.
* **manylinux-x64-image** - files for building an `x86_64` `manylinux` image with ImageMagick:
    - `Dockerfile`
    - `build_image.sh` - builds `manylinux_x64` image.

Normally `run_manylinux_docker.sh` is  launched by `build_release.py` script, but
it also can be run manually from the project root:
```
./tools/run_manylinux_docker.sh <architecture> <cpython_version>
```
where `<arch>` is `x86_64` or `arm64`, and `<cpython_version>` - Python version in `cp3XX` format.<br>
For example, run build for Python 3.11 and arm64:
```
./tools/run_manylinux_docker.sh arm64 cp311
```


## Build Docker manylinux images

### 1. Prerequisites
You should have Docker installed on your Linux build host: [Instructions](https://docs.docker.com/engine/install/)

Install QEMU packages on your system (example is for Ubuntu):
```shell
sudo apt update
sudo apt install qemu binfmt-support qemu-user-static
```

Run `multiarch/qemu-user-static` Docker image for QEMU script registering:
```shell
docker run --rm --privileged multiarch/qemu-user-static --reset -p yes
```

Copy `qemu-aarch64-static` script to the [manylinux-arm-image](manylinux-arm-image) folder:

```shell
cp /usr/bin/qemu-aarch64-static ./manylinux-arm-image/.
```

### 2. Build `manylinux_aarch64` image  for release

Run `manylinux_aarch64` image build by shell script:

```shell
cd manylinux-arm-image
./build_image.sh
```

### 3. Build `manylinux_x64` image  for release

Run `manylinux_x64` image build by shell script:

```shell
cd manylinux-x64-image
./build_image.sh
```

### 4. Finally

Your Linux build host is ready for Lets-Plot release build.
