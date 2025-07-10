FROM quay.io/pypa/manylinux2014_aarch64

# Add aarch64 virtualisation library for running this aarch64 image on x64 host:
COPY qemu-aarch64-static /usr/bin/qemu-aarch64-static

# Cleanup:
RUN rm -rf /tmp/*
