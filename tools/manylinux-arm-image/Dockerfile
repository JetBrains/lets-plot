FROM quay.io/pypa/manylinux2014_aarch64

# Add aarch64 virtualisation library for running this aarch64 image on x64 host:
COPY qemu-aarch64-static /usr/bin/qemu-aarch64-static

# Install libpng with dev libraries:
RUN yum install -y libpng-devel

# Build ImageMagick static libraries:
RUN curl -o /tmp/imagemagick-sources.tar.gz https://imagemagick.org/archive/ImageMagick-7.1.1-43.tar.gz
RUN mkdir /tmp/imagemagick-sources && tar -xzf /tmp/imagemagick-sources.tar.gz -C /tmp/imagemagick-sources --strip-components 1
RUN cd /tmp/imagemagick-sources && CFLAGS="-fPIC" CXXFLAGS="-fPIC" ./configure \
                                       --disable-shared \
                                       --enable-static \
                                       --prefix=/tmp/imagemagick \
                                       --with-pic \
                                       --disable-dependency-tracking \
                                       --disable-openmp \
                                       --with-magick-plus-plus=no \
                                       --with-jpeg=no \
                                       --without-webp \
                                       --without-jbig \
                                       --with-png=yes \
                                       --with-openjp2=no \
                                       --with-bzlib=no \
                                       --without-tiff \
                                       --with-zlib=yes \
                                       --with-zstd=no \
                                       --with-lzma=no \
                                       --with-xml=no

RUN cd /tmp/imagemagick-sources && make -j$(nproc --ignore=1) && make install
ENV LP_IMAGEMAGICK_PATH="/tmp/imagemagick"

# Cleanup:
RUN rm -rf /tmp/imagemagick-sources && rm -f /tmp/imagemagick-sources.tar.gz
