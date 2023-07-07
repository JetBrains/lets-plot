#!/usr/bin/env bash
#
# SUDO may be required.
#
# Script parameters required:
# - OS architecture ('x86_64' or 'arm64')


set -e


if [[ -z "$1" ]]
then
  printf "Set OS architecture parameter."
elif [[ "$1" = "x86_64" ]]
then
  arch="$1"
  platform_name="manylinux2014_x86_64"
  docker_image="quay.io/pypa/${platform_name}"
  docker pull $docker_image
elif [[ "$1" = "arm64" ]]
then
  arch="$1"
  platform_name="manylinux2014_aarch64"
  docker_image="manylinux_aarch64:latest"
else
  printf "OS architecture parameter is wrong or not supported."
  exit 1
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
