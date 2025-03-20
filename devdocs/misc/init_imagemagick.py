import os
import shutil
import subprocess

# Define paths
PROJECT_ROOT = os.path.abspath(os.getcwd())  # Assume script is launched from project root
PLATF_IMAGICK_DIR = os.path.join(PROJECT_ROOT, "platf-imagick")
IMAGEMAGICK_DIR = os.path.join(PLATF_IMAGICK_DIR, "ImageMagick")
BUILD_DIR = os.path.join(IMAGEMAGICK_DIR, "build")
OUTPUT_DIR = os.path.join(IMAGEMAGICK_DIR, "output")
INSTALL_DIR = os.path.join(BUILD_DIR, "install")
IMAGEMAGICK_REPO = "https://github.com/ImageMagick/ImageMagick.git"
IMAGEMAGICK_HASH = "8209e844cf02b5365918da83b2fc811442813080"  # Replace with correct commit

# Set compilers from Kotlin/Native
KONAN_DIR = os.path.expanduser("/home/ikupriyanov/.konan/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2")  # Adjust as needed
CC = os.path.join(KONAN_DIR, "bin", "x86_64-unknown-linux-gnu-gcc")
CXX = os.path.join(KONAN_DIR, "bin", "x86_64-unknown-linux-gnu-g++")

print(f"Project Root: {PROJECT_ROOT}")
print(f"ImageMagick Target Directory: {IMAGEMAGICK_DIR}")
print(f"Build Directory: {BUILD_DIR}")
print(f"Install Directory: {INSTALL_DIR}")
print(f"Output Directory: {OUTPUT_DIR}")

# Ensure platf-imagick exists
if not os.path.isdir(PLATF_IMAGICK_DIR):
    print(f"❌ Error: {PLATF_IMAGICK_DIR} does not exist. Make sure to create it first!")
    exit(1)

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
os.makedirs(OUTPUT_DIR, exist_ok=True)
os.makedirs(INSTALL_DIR, exist_ok=True)

# Remove unnecessary modules
modules_to_remove = ["coders/animate.c", "filters/animate.c"]
for module in modules_to_remove:
    module_path = os.path.join(IMAGEMAGICK_DIR, module)
    if os.path.exists(module_path):
        print(f"Removing {module_path} to disable animation support...")
        os.remove(module_path)

# Run configure script
print("Configuring ImageMagick...")
configure_cmd = [
    f"CC={CC}", f"CXX={CXX}",
    "CFLAGS=-fPIC", "CXXFLAGS=-fPIC",
    "../configure",
    "--disable-shared",
    "--enable-static",
    "--with-pic",
    f"--prefix={INSTALL_DIR}",
    "--with-fontconfig",
    "--with-freetype",
    "--disable-dependency-tracking",
    "--disable-openmp",
    "--disable-hdri",
    "--with-quantum-depth=8",
    "--without-threads",
    "--without-magick-plus-plus",
    "--without-jpeg",
    "--without-webp",
    "--without-jbig",
    "--without-png",
    "--without-openjp2",
    "--without-bzlib",
    "--without-tiff",
    "--without-zlibs",
    "--without-zstd",
    "--without-lzma",
    "--without-xml",
    "--without-x",  # Disable X11 support
    "--without-x11",
    "--without-modules",  # Disable dynamically loaded modules
]

subprocess.run(" ".join(configure_cmd), shell=True, cwd=BUILD_DIR, check=True)

print("Building ImageMagick...")
subprocess.run(["make", "-j", str(os.cpu_count())], cwd=BUILD_DIR, check=True)

print("Installing ImageMagick...")
subprocess.run(["make", "install"], cwd=BUILD_DIR, check=True)

# Move include and lib directories to output directory
print("Copying include and lib directories to output...")
shutil.copytree(os.path.join(INSTALL_DIR, "include"), os.path.join(OUTPUT_DIR, "include"), dirs_exist_ok=True)
shutil.copytree(os.path.join(INSTALL_DIR, "lib"), os.path.join(OUTPUT_DIR, "lib"), dirs_exist_ok=True)

print(f"✅ ImageMagick built and installed. Include and lib files are in {OUTPUT_DIR}.")
