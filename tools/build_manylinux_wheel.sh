#!/usr/bin/env bash
#
# Must be run inside the manylinux docker container


set -e -x


working_dir="/tmp/python-package/"
dist_dir="dist/"
python_bin_version="cp3[6-8]*"


cd $working_dir

# Compile wheels
for pybin in /opt/python/${python_bin_version}/bin; do
    "${pybin}/pip" wheel ${working_dir} -w ${dist_dir}
done

# Bundle external shared libraries into the wheels
for whl in ${dist_dir}/lets_plot*.whl; do
    auditwheel repair "$whl" --plat ${PLAT} -w ${dist_dir}
done

# Remove source wheels with common platform tag 
shopt -s extglob
rm ${dist_dir}*-linux_*

# Change folder ownership to user (uid: 1000)
chown -R 1000:1000 ${working_dir}${dist_dir}