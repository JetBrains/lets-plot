#!/usr/bin/env python3
#  Copyright (c) 2022. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.


"""
This python script runs Lets-Plot release artifacts build.

It must be placed and executed inside the project root directory.

Before script run install PyYAML python package:

    pip install pyyaml

Script requires YAML-formatted settings file with paths to host Python installations
as the first command line argument.

Settings file must have the next format (EXAMPLE):

    python37:
        bin_path: "/home/username/miniconda3/envs/python37env/bin"
        include_path: "/home/username/miniconda3/envs/python37env/include/python3.7m"
    python38:
        bin_path: "/home/username/miniconda3/envs/python38env/bin"
        include_path: "/home/username/miniconda3/envs/python38env/include/python3.8"
    ...

You can place it anywhere you want, but do not push it to the project repository.

The second command line argument must contain a path to the ImageMagick library root
directory.

Run script in terminal by its name and do not forget to pass a path to the settings
file as argument (EXAMPLE):

    ./build_release.py path/to/settings_file.yml /home/letsplotter/ImageMagick-7.1.1

"""


import os
import platform
import sys
import subprocess
import yaml  # PyYAML package is required. `pip install pyyaml`


def print_message(message):
    # Prints info message.
    print("================================\n")
    print(f"{message} \n")
    print("================================\n")


def print_error_and_exit(error_message):
    # Prints error message and exits.
    print("! ERROR !\n", error_message)
    sys.exit()


def read_settings_file():
    # Reads settings file name from commandline arguments.
    try:
        py_settings_file = open(sys.argv[1])
        py_settings = yaml.load(py_settings_file, Loader=yaml.SafeLoader)
    except Exception as exception:
        print_error_and_exit("Cannot read settings file!\n"
                             f"{exception}")
    else:
        return py_settings

def read_imagemagick_path():
    # Reads path to ImageMagick library from commandline arguments.
    try:
        imagemagick_path = sys.argv[2]
    except Exception as exception:
        print_error_and_exit("Cannot read path to ImageMagick library root directory!\n"
                             f"{exception}")
    if os.path.isdir(imagemagick_path):
        return imagemagick_path
    else:
        print_error_and_exit("ImageMagick path doesn't exist or it is not a directory!\n"
                             f"{imagemagick_path}")

def run_command(command):
    # Runs shell-command and handles its exit code.
    print(" ".join(command), "\n ")
    process = subprocess.run(command, stderr=subprocess.STDOUT)
    if process.returncode != 0:
        print_error_and_exit("Build failed. Check output.")

def get_command_output(command):
    # Runs shell-command and returns its output.
    process = subprocess.check_output(command, stderr=None)
    return process.decode().strip()


def build_python_packages(build_command):
    # Runs Python artifacts build commands.
    python_extension_build_command = [gradle_script_name, "python-extension:build"]
    print_message(f"Building Python Extension...")
    run_command(python_extension_build_command + build_parameters)
    print_message(f"Building Python Package...")
    run_command(build_command)


def get_python_architecture(python_bin_path):
    # Handles and returns Python architecture.
    supported_architectures = ["arm64", "x86_64", "AMD64"]
    current_python_architecture = get_command_output([f"{python_bin_path}/python", "-c", "import platform; print(platform.machine())"])
    if current_python_architecture in supported_architectures:
        return current_python_architecture
    else:
        print_error_and_exit(f"Got wrong Python architecture for {python_bin_path}!\n"
                             f"Check your settings file or Python installation.")


# Check command line arguments:
if len(sys.argv) != 3:
    print_error_and_exit(f"Wrong number of arguments. {len(sys.argv)}\n"
                         f"Pass the settings filename and path to ImageMagick.")

# Read Python settings file from script argument.
# Paths to Python binaries and include directories will be got from here:
python_settings = read_settings_file()

# Get current OS platform:
system = platform.system()

# Define basic gradle commands for project build:
if system == "Windows":
    # For Windows Gradle script should be called with another path and name:
    gradle_script_name = ".\\gradlew.bat"
elif system == "Linux" or system == "Darwin":
    # For Linux and Mac the standard name:
    gradle_script_name = "./gradlew"
else:
    print_error_and_exit(f"Unsupported platform: {system}")

python_extension_clean_command = [gradle_script_name, "python-extension:clean"]


# Run Python artifacts build.
if system == "Linux":
    # Define Linux-specific parameters.
    # Python linux packages will be built inside Docker 'manylinux' containers (PEP 599) for
    # all Python binaries, defined in the settings file.
    
    # Docker containers will be run by the external shell script, depending on target architecture.
    python_package_build_command = ["./tools/run_manylinux_docker.sh"]

    # Run JS artifact build first:
    gradle_js_build_command = [gradle_script_name, "js-package:build", "-Parchitecture=x86_64"]
    run_command(gradle_js_build_command)

    # For each of supported architectures run manylinux build for all Python binaries,
    # defined in the settings file.
    for architecture in ["x86_64", "arm64"]:
        for python_paths in python_settings.values():
            # Collect all predefined parameters:
            build_parameters = [
                "-Pbuild_release=true",
                "-Ppython.bin_path=%s" % (python_paths["bin_path"]),
                "-Ppython.include_path=%s" % (python_paths["include_path"]),
                f"-Penable_python_package=true",
                "-Parchitecture=%s" % architecture,
                "-Pimagemagick_lib_path=%s" % read_imagemagick_path()
            ]

            # Get current Python version in format 'cp3XX':
            cpython_version = get_command_output([f"{python_paths["bin_path"]}/python",
                                                  "-c",
                                                  "import sys; print(f'cp{sys.version_info.major}{sys.version_info.minor}')"])

            # Run Python 'manylinux' package build:
            build_python_packages(python_package_build_command + [architecture, cpython_version])

            # And clean Python Extension artifacts before the next iteration:
            run_command(python_extension_clean_command)


elif system == "Darwin" or system == "Windows":
    # For another systems (Windows, Mac), Python package will be built by Gradle task
    # for all Python binaries, defined in the settings file.

    # Define Gradle command for Python packages build:
    python_package_build_command = [gradle_script_name, "python-package-build:build"]

    # Run Python packages build for all Python host installations, defined in the settings file:
    for python_paths in python_settings.values():
        # Collect all predefined parameters:
        build_parameters = [
            "-Pbuild_release=true",
            "-Ppython.bin_path=%s" % (python_paths["bin_path"]),
            "-Ppython.include_path=%s" % (python_paths["include_path"]),
            f"-Penable_python_package=true",
            "-Parchitecture=%s" % (get_python_architecture(python_paths["bin_path"])),
            "-Pimagemagick_lib_path=%s" % read_imagemagick_path()
        ]

        # Run Python package build:
        build_python_packages(python_package_build_command + build_parameters)

        # And clean Python Extension artifacts before the next iteration:
        run_command(python_extension_clean_command)

# Print final message and exit:
print_message("Release build finished!")
sys.exit()
