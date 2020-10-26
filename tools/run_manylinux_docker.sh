#!/usr/bin/env bash
#
# SUDO may be required.
#
# Script parameters required:
# - project path (absolute)


set -e


if [[ -z "$1" ]]
then 
  printf "Script parameters required: \n - project path (absolute)\n"
  exit 1
elif [[ -d "$1" ]]
then
  root_path="$1"
else
  printf "Directory from the script parameter doesn't exist.\n"
  exit 1
fi


platform_name="manylinux1_x86_64"
docker_image="quay.io/pypa/${platform_name}"
python_extension_path="${root_path}/python-extension"
python_package_path="${root_path}/python-package"
build_script="build_manylinux_wheel.sh"
sys_user_id=$(id -u)
sys_group_id=$(id -g)


docker pull $docker_image


docker run --rm \
  -e PLAT=$platform_name \
  -e USER_ID=$sys_user_id \
  -e GROUP_ID=$sys_group_id \
  -v $python_package_path:/tmp/python-package \
  -v $python_extension_path:/tmp/python-extension \
  -v ${root_path}/tools/$build_script:/tmp/$build_script \
  -v ${root_path}/LICENSE:/tmp/LICENSE \
  -v ${root_path}/README_PYTHON.md:/tmp/README_PYTHON.md \
  $docker_image \
  /tmp/$build_script
