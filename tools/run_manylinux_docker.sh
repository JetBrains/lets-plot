#!/usr/bin/env bash
#
# SUDO may be required.
#
# Script parameters required:
# - OS architecture ('x86_64' or 'arm64')
# - CPython version ('cp3XX': cp39, cp310)


set -e


if [[ -z "$1" ]]
then
  printf "Set OS architecture parameter."
  exit 1
elif [[ "$1" = "x86_64" ]]
then
  arch="$1"
  platform_name="manylinux2014_x86_64"
  docker_image="manylinux_x64:latest"
elif [[ "$1" = "arm64" ]]
then
  arch="$1"
  platform_name="manylinux2014_aarch64"
  docker_image="manylinux_aarch64:latest"
else
  printf "OS architecture parameter is wrong or not supported."
  exit 1
fi

if [[ -z "$2" ]]
then
  printf "Set Python version parameter."
  exit 1
else
  cpython_version=$2
fi

root_path=$PWD
python_extension_path="${root_path}/python-extension"
python_package_path="${root_path}/python-package"
js_package_path="${root_path}/js-package"
build_script="build_manylinux_wheel.sh"
sys_user_id=$(id -u)
sys_group_id=$(id -g)

echo "-------------------------"
echo "Started manylinux packages build for ${arch} on ${platform_name}..."
echo "-------------------------"
docker run --rm \
  -e ARCH=$arch \
  -e CPYTHON_VERSION=$cpython_version \
  -e PLAT=$platform_name \
  -e USER_ID=$sys_user_id \
  -e GROUP_ID=$sys_group_id \
  -v $python_package_path:/tmp/python-package \
  -v $python_extension_path:/tmp/python-extension \
  -v $js_package_path:/tmp/js-package \
  -v ${root_path}/tools/$build_script:/tmp/$build_script \
  -v ${root_path}/LICENSE:/tmp/LICENSE \
  -v ${root_path}/README.md:/tmp/README.md \
  $docker_image \
  /tmp/$build_script
