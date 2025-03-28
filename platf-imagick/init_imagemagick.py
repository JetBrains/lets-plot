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
configure_cmd = [
    "ac_cv_func_getentropy=no", # while in local sysroot we have getentropy, but it is not available in the konan sysroot (glibc 2.19)
    "ac_cv_func_gs=no", # while in local sysroot we have getentropy, but it is not available in the konan sysroot (glibc 2.19)
 #   f"CC={CC}", f"CXX={CXX}",
    "CFLAGS=-fPIC", "CXXFLAGS=-fPIC",
    "../src/configure",
    "--enable-zero-configuration",
    "--enable-static",
    "--with-pic",
    "--with-quantum-depth=8",
    "--with-fontconfig",
    "--with-freetype",
    f"--prefix={INSTALL_DIR}",
    "--disable-shared",
    "--disable-openmp",
    "--without-threads",
    "--disable-opencl",
    "--disable-assert",
    "--disable-hdri",
    "--disable-installed",
    "--without-magick-plus-plus",
    "--without-perl",
    "--without-bzlib",
    "--without-djvu",
    "--without-dps",
    "--without-fftw",
    "--without-gslib",
    "--without-gslib_framework",
    "--without-gvc",
    "--without-heic",
    "--without-jbig",
    "--without-jpeg",
    "--without-lcms",
    "--without-lqr",
    "--without-lzma",
    "--without-openexr",
    "--without-pango",
    "--without-perl",
    "--without-png",
    "--without-raw",
    "--without-rsvg",
    "--without-tiff",
    "--without-webp",
    "--without-wmf",
    "--without-x",
    "--without-xml",
    "--without-zlib",
]

subprocess.run(" ".join(configure_cmd), shell=True, env=env, cwd=BUILD_DIR, check=True)

print("Building ImageMagick...")
subprocess.run(["make", "-j", str(os.cpu_count())], cwd=BUILD_DIR, check=True)

print("Installing ImageMagick...")
subprocess.run(["make", "install"], cwd=BUILD_DIR, check=True)

print(f"ImageMagick built and installed to {INSTALL_DIR}.")
