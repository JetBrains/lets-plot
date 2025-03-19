
git clone git@github.com:ImageMagick/ImageMagick.git
cd ImageMagick
make clean
CFLAGS="-fPIC" CXXFLAGS="-fPIC" ./configure \
    --disable-shared \
    --enable-static \
    --prefix=/your/path/to/library \
    --with-pic \
    --with-fontconfig \
    --disable-dependency-tracking \
    --disable-openmp \
    --disable-hdri \
    --with-quantum-depth=8 \
    --without-freetype \
    --without-threads \
    --without-magick-plus-plus \
    --without-jpeg \
    --without-webp \
    --without-jbig \
    --without-png \
    --without-openjp2 \
    --without-bzlib \
    --without-tiff \
    --without-zlibs \
    --without-zstd \
    --without-lzma \
    --without-xml