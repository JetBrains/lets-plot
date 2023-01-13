## Tools directory

This directory contains scripts for release build of Python artifacts for Linux platform.
We use `manylinux2014` ([PEP599](https://peps.python.org/pep-0599/)) for Python Linux distribution:
https://github.com/pypa/manylinux.

Python packages are built inside Docker containers. For x64 architecture `quay.io/pypa/manylinux2014_x86_64`
is used.

Due to the lack of Linux aarch64 support from Kotlin Native (https://youtrack.jetbrains.com/issue/KT-36871),
the Docker image for aarch64 packages build (`quay.io/pypa/manylinux2014_aarch64`) must be run on a x64 host.
To do this, you need to build the arm64 image manually with the addition of the `qemu-user-static`
util (https://github.com/multiarch/qemu-user-static).


### 1. Directory contains:

* `run_manylinux_docker.sh` - runs Docker 'manylinux' containers (script runs automatically).
* `build_manylinux_wheel.sh` - builds Python wheels inside 'manylinux' containers (script runs automatically).
* **manylinux-arm-image** - contains files, needed to build cross-arch manylinux image:
    - `Dockerfile`
    - `build_image.sh` - builds `manylinux_aarch64` image, which can be run on x64 host.

Normally `run_manylinux_docker.sh` is  launched by `build_release.py` script, but
it also can be run manually:
```
./run_manylinux_docker.sh <arch>
```
where `<arch>` is `x86_64` or `arm64`


### 2. Prerequisites
You should have Docker installed on your Linux build host: https://docs.docker.com/engine/install/

Install QEMU packages on your system (for Ubuntu):
```shell
sudo apt update
sudo apt install qemu binfmt-support qemu-user-static
```

Run `multiarch/qemu-user-static` Docker image for QEMU script registering:
```shell
docker run --rm --privileged multiarch/qemu-user-static --reset -p yes
```

Copy `qemu-aarch64-static` script to [manylinux-arm-image](manylinux-arm-image) folder:

```shell
cp /usr/bin/qemu-aarch64-static ./manylinux-arm-image/.
```

### 3. Build `manylinux_aarch64` image  for release

Run `manylinux_aarch64` image build by shell script:

```shell
cd manylinux-arm-image
./build_image.sh
```

### Finally

Your Linux build host is ready for Lets-Plot release build.
