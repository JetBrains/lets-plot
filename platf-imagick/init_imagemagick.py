import os
import shutil
import subprocess

# Define paths
ROOT_DIR = os.path.join(os.path.abspath(os.getcwd()), "ImageMagick")
IMAGEMAGICK_DIR = os.path.join(ROOT_DIR, "src")
BUILD_DIR = os.path.join(ROOT_DIR, "build")
INSTALL_DIR = os.path.join(ROOT_DIR, "install")
IMAGEMAGICK_REPO = "https://github.com/ImageMagick/ImageMagick.git"
IMAGEMAGICK_HASH = "8209e844cf02b5365918da83b2fc811442813080"  # Replace with correct commit

# Set compilers from Kotlin/Native
KONAN_DIR = os.path.expanduser("/home/ikupriyanov/.konan/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2")  # Adjust as needed
#CC = os.path.join(KONAN_DIR, "bin", "x86_64-unknown-linux-gnu-gcc")
#CXX = os.path.join(KONAN_DIR, "bin", "x86_64-unknown-linux-gnu-g++")

print(f"Root: {ROOT_DIR}")
print(f"ImageMagick Src Directory: {IMAGEMAGICK_DIR}")
print(f"Build Directory: {BUILD_DIR}")
print(f"Install Directory: {INSTALL_DIR}")

# Clone ImageMagick inside platf-imagick if not already present
if not os.path.isdir(IMAGEMAGICK_DIR):
    print(f"Cloning ImageMagick into {IMAGEMAGICK_DIR}...")
    subprocess.run(["git", "clone", IMAGEMAGICK_REPO, IMAGEMAGICK_DIR], check=True)
else:
    print(f"Repository already exists in {IMAGEMAGICK_DIR}. Skipping clone.")

# Ensure full commit history (if it was a shallow clone)
subprocess.run(["git", "-C", IMAGEMAGICK_DIR, "rev-parse", "--is-shallow-repository"], capture_output=True, text=True)
if "true" in subprocess.run(["git", "-C", IMAGEMAGICK_DIR, "rev-parse", "--is-shallow-repository"], capture_output=True, text=True).stdout.strip():
    print("Repository is shallow, fetching full history...")
    subprocess.run(["git", "-C", IMAGEMAGICK_DIR, "fetch", "--unshallow"], check=True)

# Fetch the commit if not present
commit_check = subprocess.run(["git", "-C", IMAGEMAGICK_DIR, "cat-file", "-t", IMAGEMAGICK_HASH], capture_output=True, text=True)
if "commit" not in commit_check.stdout.strip():
    print(f"Commit {IMAGEMAGICK_HASH} not found locally, fetching...")
    subprocess.run(["git", "-C", IMAGEMAGICK_DIR, "fetch", "origin", IMAGEMAGICK_HASH], check=True)

# Checkout the specific commit
print(f"Checking out commit: {IMAGEMAGICK_HASH}")
subprocess.run(["git", "-C", IMAGEMAGICK_DIR, "checkout", IMAGEMAGICK_HASH], check=True)

# Create build and output directories
os.makedirs(BUILD_DIR, exist_ok=True)
os.makedirs(INSTALL_DIR, exist_ok=True)

# Clean
print("Cleaning ImageMagick...")
subprocess.run(["make", "clean"], cwd=BUILD_DIR, check=False)

# Prepare your env
env = os.environ.copy()
env["GS"] = "none"  # disable Ghostscript delegate

# Run configure script
print("Configuring ImageMagick...")
#env["CFLAGS"] = "-O2 -fPIC -DNDEBUG"
#env["CXXFLAGS"] = "-O2 -fPIC -DNDEBUG"
env["CFLAGS"] = "-O2 -fPIC -DDEBUG -DMAGICKCORE_ZERO_CONFIGURATION_SUPPORT"
env["CXXFLAGS"] = "-O2 -fPIC -DDEBUG -DMAGICKCORE_ZERO_CONFIGURATION_SUPPORT"
env["ac_cv_func_getentropy"] = "no"

configure_cmd = [
    "../src/configure",
    f"--prefix={INSTALL_DIR}",
    "--with-pic",
    "--enable-static",
    "--disable-shared",
    "--enable-zero-configuration",
    "--with-quantum-depth=8",
    "--with-fontconfig",
    "--with-freetype",
    "--without-apple-font-dir",
    "--without-dejavu-font-dir",
    "--without-gs-font-dir",
    "--without-urw-base35-font-dir",
    "--without-urw-base35-type1-font-dir",
    "--without-windows-font-dir",

    "--without-modules",
    "--disable-silent-rules",
    "--disable-openmp",
    "--without-threads",
    "--disable-opencl",
#    "--disable-assert",
    "--disable-hdri",
    "--disable-installed",

    "--without-bzlib",
    "--without-autotrace",
    "--without-djvu",
    "--without-dps",
    "--without-fftw",
    "--without-flif",
    "--without-fpx",
    "--without-gslib",
    "--without-gvc",
    "--without-heic",
    "--without-jbig",
    "--without-jpeg",
    "--without-jxl",
    "--without-dmr",
    "--without-lcms",
    "--without-lqr",
    "--without-lzma",
    "--without-magick-plus-plus",
    "--without-openexr",
    "--without-openjp2",
    "--without-pango",
    "--without-perl",
    "--without-png",
    "--without-raqm",
    "--without-raw",
    "--without-rsvg",
    "--without-tiff",
    "--without-uhdr",
    "--without-webp",
    "--without-wmf",
    "--without-x",
    "--without-xml",
    "--without-zip",
    "--without-zlib",
    "--without-zstd",
]

subprocess.run(configure_cmd, cwd=BUILD_DIR, env=env, check=True)

print("Building ImageMagick...")
subprocess.run(["make", "-j", str(os.cpu_count())], cwd=BUILD_DIR, check=True)

print("Installing ImageMagick...")
subprocess.run(["make", "install"], cwd=BUILD_DIR, check=True)

print(f"ImageMagick built and installed to {INSTALL_DIR}.")
