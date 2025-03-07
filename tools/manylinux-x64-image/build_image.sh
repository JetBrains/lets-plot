#!/bin/bash

#
# Copyright (c) 2023. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

# Builds Docker image based on quay.io/pypa/manylinux2014_x86_64 with bundled ImageMagick library.
# For details check 'tools/README.md'.

set -e

echo "Updating base x64 manylinux image..."
docker pull quay.io/pypa/manylinux2014_x86_64
echo "Building cross-arch image for manylinux x64 artifacts..."
docker build --rm -t manylinux_x64 .
echo "manylinux_x64 Docker image build fnished."
