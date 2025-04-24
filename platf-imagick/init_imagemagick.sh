#!/usr/bin/bash

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
  printf "==========================================================\n\n"
  printf "${message}\n\n"
  printf "==========================================================\n\n"
}

print_warning () {
  local warning="$1"
  printf "\n\n"
  printf "\033[1mWARNING:\033[0m ${warning}\n\n"
  printf "\n"
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
current_path=$(pwd)
current_dir=$(basename "$current_path")

if [[ current_dir = "$PLATF_IMAGICK_DIR" ]]; then
  WORKING_DIR=$current_path
else
  if [[ $current_dir = "lets-plot" ]]; then
    cd $PLATF_IMAGICK_DIR || exit_with_error "Could not find '${PLATF_IMAGICK_DIR}' directory."
    WORKING_DIR=$(pwd)
  else
    exit_with_error "Please run this script from lets-plot/${PLATF_IMAGICK_DIR} directory."
  fi
fi

# Prefix path for libraries installation can be set as a script argument.
if [[ -z "$1" ]]; then
  export INSTALL_PREFIX="${WORKING_DIR}/imagick_deps"
else
  export INSTALL_PREFIX="$1"
  PATH_WARNING=1
fi

export PKG_CONFIG_PATH="$INSTALL_PREFIX/lib/pkgconfig:$PKG_CONFIG_PATH"
export SOURCES_DIR="${WORKING_DIR}/build/dependencies/sources"


# SET PLATFORM SPECIFIC PARAMETERS:
getPlatform="$(uname -s)"

case $getPlatform in
  Linux*)   PLATFORM="Linux";;

  Darwin*)
    PLATFORM="Mac"

    if [[ -z "$HOMEBREW_PREFIX" ]]; then
      if [[ $(brew --prefix) -ne 0 ]]; then
        exit_with_error "Could not find Homebrew installation on your Mac"
      else
        brew_prefix=$(brew --prefix)
      fi
    else
      brew_prefix="$HOMEBREW_PREFIX"
    fi

    if [[ -d "${brew_prefix}/opt/libtool" ]]; then
      libtool_gnubin_path="${brew_prefix}/opt/libtool/libexec/gnubin"
      if [[ -d "$libtool_gnubin_path" ]]; then
        export PATH="${libtool_gnubin_path}:${PATH}"
      else
        exit_with_error "A 'gnubin' directory is expected, but was not found. Check your 'libtool' installation."
      fi
    else
      exit_with_error "Could not find 'libtool' installation. Please, install 'libtool':\nbrew install libtool"
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
    "--disable-shared"
    "--enable-static"
  )

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
        "--with-pic"
        "--enable-zero-configuration"
        "--with-quantum-depth=8"
        "--with-fontconfig"
        "--with-freetype"
        "--without-modules"
        "--disable-openmp"
        "--without-threads"
        "--disable-opencl"
        "--disable-assert"
        "--disable-hdri"
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
  if [[ "${confirmation,,}" != "yes" ]]; then
    printf "\n Operation was cancelled due to user request."
    exit 0
  else
    find "$INSTALL_PREFIX" -mindepth 1 -delete
  fi
fi

if [[ -d "$SOURCES_DIR" && -n $(ls -A "$SOURCES_DIR") ]]; then
  print_warning "${SOURCES_DIR} is not empty. Cleaning..."
  find "$SOURCES_DIR" -mindepth 1 -delete
  check_exec_status "$?" "Deleting ${SOURCES_DIR}"
else
  mkdir -p "$SOURCES_DIR"
fi


# BUILD LIBRARIES:
cd "$SOURCES_DIR" || exit_with_error "Could not find ${SOURCES_DIR} directory."
build_library "libexpat"
build_library "freetype"
build_library "fontconfig"
build_library "imagemagick"


# PRINT FINAL MESSAGE:
printf "*******************************************************************\n\n"
printf "  ImageMagick and its dependencies installation completed.\n\n"
if [[ "$PATH_WARNING" -eq 1 ]]; then
  printf "   You have chosen custom installation path. Do not forget to adjust project settings:\n\n"
  printf "   ${INSTALL_PREFIX}/lib\n"
fi
printf "*******************************************************************\n\n"
