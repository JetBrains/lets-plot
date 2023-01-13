#!/bin/bash

#
# Copyright (c) 2023. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#


# Builds Docker image based on quay.io/pypa/manylinux2014_aarch64 which can
#-be launched on x64 host. For details check 'tools/README.md'.


set -e


echo "Updating base ARM manylinux image..."
docker pull quay.io/pypa/manylinux2014_aarch64
echo "Building cross-arch image for manylinux aarch64 artifacts..."
docker build --rm -t manylinux_aarch64 .
echo "manylinux_aarch64 Docker image build fnished."