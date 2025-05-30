#!/bin/bash

#
# Copyright (c) 2025. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#


# SET COMMON BUILD ENVIRONMENT:
LIBEXPAT_GIT_REPO="https://github.com/libexpat/libexpat.git"
LIBEXPAT_GIT_HASH="f9a3eeb3e09fbea04b1c451ffc422ab2f1e45744"
FREETYPE_GIT_REPO="https://gitlab.freedesktop.org/freetype/freetype.git"
FREETYPE_GIT_HASH="42608f77f20749dd6ddc9e0536788eaad70ea4b5"
FONTCONFIG_GIT_REPO="https://gitlab.freedesktop.org/fontconfig/fontconfig.git"
FONTCONFIG_GIT_HASH="c1bdf4f542f56d3b5035ec2806ed7a685f831c1b"
IMAGEMAGICK_GIT_REPO="https://github.com/ImageMagick/ImageMagick.git"
IMAGEMAGICK_GIT_HASH="8209e844cf02b5365918da83b2fc811442813080"
PLATF_IMAGICK_DIR="platf-imagick"


# COMMON SCRIPT FUNCTIONS:
print_message () {
  local message="$1"
  printf "${message}\n\n"
}

print_warning () {
  local warning="$1"
  printf "\n\n"
  printf "\033[1mWARNING:\033[0m ${warning}\n\n"
  printf "\n"
  sleep 2
}

exit_with_error () {
  local error_text="$1"
  >&2 printf "\033[1mERROR:\033[0m \n${error_text}\n\n"
  exit 1
}

check_exec_status () {
  local exit_code="$1"
  if [[ -z "$2" ]]; then
    local action="Command"
  else
    local action="$2"
  fi

  if [[ $exit_code -ne 0 ]]; then
    exit_with_error "${action} failed with error. Check output."
  else
    return 0
  fi
}


# GET AND SET BUILD AND INSTALL PATHS
script_dir=$(dirname "${BASH_SOURCE[0]}")
cd $script_dir || exit_with_error "Could not open ${script_dir}"
export WORKING_DIR=$(pwd)

# Prefix path for libraries installation can be set as a script argument.
if [[ -z "$1" ]]; then
  export INSTALL_PREFIX="${WORKING_DIR}/imagick_deps"
else
  export INSTALL_PREFIX="$1"
  PATH_WARNING=1
fi

export PKG_CONFIG_PATH="${INSTALL_PREFIX}/lib/pkgconfig:${PKG_CONFIG_PATH}"
export SOURCES_DIR="${WORKING_DIR}/build/dependencies/sources"


# SET PLATFORM SPECIFIC PARAMETERS:
getPlatform="$(uname -s)"

case $getPlatform in
  Linux*)
    export PLATFORM="Linux"
    # For Linux run build inside Docker container:
    if [[ "$DOCKER_TRUE" != "1" ]]; then
      print_warning "Your system is Linux. We will run build process inside the Docker container."
      user_id=$(id -u)
      group_id=$(id -g)
      docker run -it --rm \
         -e DOCKER_TRUE="1" \
         -e USER_ID=$user_id \
         -e GROUP_ID=$group_id \
         -v "$WORKING_DIR":"/opt/${PLATF_IMAGICK_DIR}" \
         --name imagick_build \
         quay.io/pypa/manylinux2014_x86_64 \
         /bin/bash "/opt/${PLATF_IMAGICK_DIR}/init_imagemagick.sh"

      exit 0
    fi
  ;;

  Darwin*)
    PLATFORM="Mac"
    # Check Homebrew installation:
    if [[ -z "$HOMEBREW_PREFIX" ]]; then
      brew --prefix
      if [[ "$?" -ne 0 ]]; then
        exit_with_error "Could not find Homebrew installation on your Mac"
      else
        brew_prefix=$(brew --prefix)
      fi
    else
      brew_prefix="$HOMEBREW_PREFIX"
    fi

    # Check libtool and add it to env
    if [[ -d "${brew_prefix}/opt/libtool" ]]; then
      libtool_gnubin_path="${brew_prefix}/opt/libtool/libexec/gnubin"
      if [[ -d "$libtool_gnubin_path" ]]; then
        export PATH="${libtool_gnubin_path}:${PATH}"
      else
        exit_with_error "A 'gnubin' directory is expected, but was not found here:\n ${libtool_gnubin_path}\n\n
         Check your 'libtool' installation."
      fi
    else
      exit_with_error "Could not find 'libtool' installation. Please, install 'libtool':\nbrew install libtool"
    fi

    # Check zlib and add it to env
    if [[ -d "${brew_prefix}/opt/zlib" ]]; then
      zlib_pkgconfig_path="${brew_prefix}/opt/zlib/lib/pkgconfig"
      if [[ -d "$zlib_pkgconfig_path" ]]; then
        export PKG_CONFIG_PATH="${zlib_pkgconfig_path}:$PKG_CONFIG_PATH"
      else
        exit_with_error "A 'pkgconfig' directory is expected, but was not found here:\n ${zlib_pkgconfig_path}\n\n
        Check your 'zlib' installation."
      fi
    else
      exit_with_error "Could not find 'zlib' installation. Please, install 'zlib':\nbrew install zlib"
    fi
  ;;

  MINGW*)   PLATFORM="MinGw";;

  # IN CASE OF UNSUPPORTED PLATFORM EXIT WITH ERROR
  *)
    exit_with_error "UNSUPPORTED PLATFORM: ${getPlatform}!"
  ;;

esac


# MAIN SCRIPT FUNCTION FOR BUILDING LIBRARIES:
build_library () {
  local lib_name="$1"
  local sources_dir=$(pwd)
  local available_proc="$(nproc --ignore=1)"
  base_configure_args=(
    "--prefix=${INSTALL_PREFIX}"
    "--enable-static"
    "--disable-shared"
  )

  export CFLAGS="-O2 -fPIC"
  export CXXFLAGS="-O2 -fPIC"

  print_message "Building ${lib_name}..."

  case "$lib_name" in

    libexpat*)
      local git_repo="$LIBEXPAT_GIT_REPO"
      local git_local_name="libexpat"
      local git_hash="$LIBEXPAT_GIT_HASH"
      extra_configure_args=("")
    ;;

    freetype*)
      local git_repo="$FREETYPE_GIT_REPO"
      local git_local_name="freetype"
      local git_hash="$FREETYPE_GIT_HASH"
      extra_configure_args=(
        "--with-zlib=yes"
        "--without-png"
        "--without-harfbuzz"
        "--without-brotli"
        "--without-bzip2"
      )
    ;;

    fontconfig*)
      local git_repo="$FONTCONFIG_GIT_REPO"
      local git_local_name="fontconfig"
      local git_hash="$FONTCONFIG_GIT_HASH"
      extra_configure_args=(
        "--disable-nls"
        "--with-expat=${INSTALL_PREFIX}"
        "--without-bzip2"
        "--disable-docs"
      )
    ;;

    imagemagick*)
      local git_repo="$IMAGEMAGICK_GIT_REPO"
      local git_local_name="imagemagick"
      local git_hash="$IMAGEMAGICK_GIT_HASH"
      export ac_cv_func_getentropy=no
      export LIBS=$(pkg-config --libs --static freetype2 fontconfig)
      extra_configure_args=(
        "--enable-zero-configuration"
        "--with-quantum-depth=16"
        "--with-fontconfig"
        "--with-freetype"
        "--without-modules"
        "--disable-openmp"
        "--without-threads"
        "--disable-opencl"
        "--disable-assert"
        "--enable-hdri"
        "--disable-installed"
        "--without-bzlib"
        "--without-autotrace"
        "--without-djvu"
        "--without-dps"
        "--without-fftw"
        "--without-flif"
        "--without-fpx"
        "--without-gslib"
        "--without-gvc"
        "--without-heic"
        "--without-jbig"
        "--without-jpeg"
        "--without-jxl"
        "--without-dmr"
        "--without-lcms"
        "--without-lqr"
        "--without-lzma"
        "--without-magick-plus-plus"
        "--without-openexr"
        "--without-openjp2"
        "--without-pango"
        "--without-perl"
        "--without-png"
        "--without-raqm"
        "--without-raw"
        "--without-rsvg"
        "--without-tiff"
        "--without-uhdr"
        "--without-webp"
        "--without-wmf"
        "--without-x"
        "--without-xml"
        "--without-zip"
        "--without-zlib"
      )
    ;;

  esac

  git clone "$git_repo" "$git_local_name"
  cd "$git_local_name" || exit_with_error "Could not find ${git_local_name} directory."
  git checkout "$git_hash"

  if [[ "$lib_name" = "libexpat" ]]; then
    cd expat || exit_with_error "Could not find 'expat' directory.\n Check repository: ${LIBEXPAT_GIT_REPO}"
  fi

  if [[ -f "buildconf.sh" ]]; then
    ./buildconf.sh
    check_exec_status "$?" "./buildconf.sh"
  elif [[ -f "autogen.sh" ]]; then
    ./autogen.sh
    check_exec_status "$?" "./autogen.sh"
  fi

  ./configure "${base_configure_args[@]}" "${extra_configure_args[@]}"
  check_exec_status "$?" "./configure"

  make -j"${available_proc}"
  check_exec_status "$?" "make"

  make install
  check_exec_status "$?" "make install"

  print_message "${lib_name} build finished..."
  cd "$sources_dir" || exit_with_error "Could not return to ${sources_dir} directory."
}


# CHECK INSTALLATION AND SOURCES DIRECTORIES:
if [[ -d "$INSTALL_PREFIX" && -n $(ls -A "$INSTALL_PREFIX") ]]; then
  print_warning "${INSTALL_PREFIX} directory exists and is not empty."
  read -p "If you continue, ${INSTALL_PREFIX} content will be removed.
  Type 'yes' to confirm: " confirmation
  if [[ "$confirmation" != "yes" ]]; then
    printf "\n Operation was cancelled due to user request.\n\n"
    exit 0
  else
    print_message "$INSTALL_PREFIX"
    rm -rf "$INSTALL_PREFIX"
  fi
fi

if [[ -d "$SOURCES_DIR" && -n $(ls -A "$SOURCES_DIR") ]]; then
  print_warning "${SOURCES_DIR} is not empty. Cleaning..."
  rm -rf "$SOURCES_DIR"
  check_exec_status "$?" "Deleting ${SOURCES_DIR}"
  mkdir -p "$SOURCES_DIR"
else
  mkdir -p "$SOURCES_DIR"
fi


# BUILD LIBRARIES:
cd "$SOURCES_DIR" || exit_with_error "Could not find ${SOURCES_DIR} directory."
# INSTALL BUILD DEPENDENCIES IN DOCKER CONTAINER:
if [[ "$PLATFORM" = "Linux" && "$DOCKER_TRUE" = "1" ]]; then
  print_message "Installing build dependencies..."
  yum install -y gperf gettext-devel
  check_exec_status
fi

build_library "libexpat"
build_library "freetype"
build_library "fontconfig"
build_library "imagemagick"

if [[ "$PLATFORM" = "Linux" && "$DOCKER_TRUE" = "1" ]]; then
  print_message "Fix files ownership..."
  chown -R $USER_ID:$GROUP_ID "$WORKING_DIR"
fi

# PRINT FINAL MESSAGE:
printf "*******************************************************************\n\n"
printf "  ImageMagick and its dependencies installation completed.\n\n"
if [[ "$PATH_WARNING" -eq 1 ]]; then
  printf "   You have chosen custom installation path. Do not forget to adjust project settings:\n\n"
  printf "   imagemagick_lib_path: ${INSTALL_PREFIX}/lib\n"
fi
printf "*******************************************************************\n\n"
