#!/usr/bin/env python3
#  Copyright (c) 2022. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.


"""
This python script runs Lets-Plot release artifacts build.

It must be placed and executed inside the project root directory.

Before script run install PyYAML python package:

    pip install pyyaml

Script requires YAML-formatted settings file with paths to host Python installations.
Settings file must have the next format (EXAMPLE):

    python37:
        bin_path: "/home/username/miniconda3/envs/python37env/bin"
        include_path: "/home/username/miniconda3/envs/python37env/include/python3.7m"
    python38:
        bin_path: "/home/username/miniconda3/envs/python38env/bin"
        include_path: "/home/username/miniconda3/envs/python38env/include/python3.8"
    ...

You can place it anywhere you want, but do not push it to the project repository.

Run script in terminal by its name and do not forget to pass a path to the settings
file as argument (EXAMPLE):

    ./build_release.py path/to/settings_file.yml

"""


import platform
import sys
import subprocess
import yaml # PyYAML package is required. `pip install pyyaml`


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
    if len(sys.argv) != 2:
        print_error_and_exit(f"Wrong number of arguments. {len(sys.argv)}\n"
                             f"Pass the settings filename.")
    try:
        py_settings_file = open(sys.argv[1])
        py_settings = yaml.load(py_settings_file, Loader=yaml.SafeLoader)
    except Exception as exception:
        print_error_and_exit("Cannot read settings file!\n"
                             f"{exception}")
    else:
        return py_settings


def run_command(command):
    # Runs shell-command and handles its exit code.
    print(" ".join(command), "\n ")
    process = subprocess.run(command, stderr=subprocess.STDOUT)
    if process.returncode != 0:
        print_error_and_exit("Build failed. Check output.")


def build_python_packages(build_command, arch=None):
    # Runs Python artifacts build commands. If 'arch' argument was passed, adds it to shell command.
    python_extension_build_command = [gradle_script_name, "python-extension:build"]
    if arch is not None:
        python_extension_build_command += [f"-Pbuild_arch={arch}"]
        command = build_command + [arch]
    else:
        command = build_command
    print_message(f"Building Python Extension...")
    run_command(python_extension_build_command + build_parameters)
    print_message(f"Building Python Package...")
    run_command(command)


# Read Python settings file from script argument.
# Paths to Python binaries and include directories will be got from here:
python_settings = read_settings_file()

# Get current OS platform:
system = platform.system()

# Define basic gradle commands for project build:
if system == "Windows":
    # For Windows Gradle scipt should be called with another path and name:
    gradle_script_name = ".\gradlew.bat"
elif system == "Linux" or system == "Darwin":
    # For Linux and Mac the standard name:
    gradle_script_name = "./gradlew"
else:
    print_error_and_exit(f"Unsupported platform: {system}")

gradle_build_command = [gradle_script_name, "build", "-Pbuild_release=true"]
python_extension_clean_command = [gradle_script_name, "python-extension:clean"]

# Run project build. JS and JVM artifacts will be built only:
print_message("Started main Gradle build...")
run_command(gradle_build_command)

# Run Python artifacts build.
if system == "Linux":
    # Define Linux-specific parameters.
    # Python linux packages will be built inside Docker 'manylinux' containers (PEP 599).
    # Docker containers will be run by the external shell script, depending on target arch.
    python_package_build_command = ["./tools/run_manylinux_docker.sh"]
    # So the only one Python host installation, defined in the settings file, is needed:
    python_paths = list(python_settings.values())[0]
    # And Python package build by Gradle is disabled due the same reason.
    enable_python_package = "false"
    # Enable Python Extension. Native artifacts from here are used for Python packages.
    build_python_extension = "true"
    # Collect all predefined parameters:
    build_parameters = [
        "-Pbuild_release=true",
        "-Ppython_bin_path=%s" % (python_paths["bin_path"]),
        "-Ppython_include_path=%s" % (python_paths["include_path"]),
        f"-Penable_python_package={enable_python_package}",
        f"-Pbuild_python_extension={build_python_extension}"
    ]
    # Run Python 'manylinux' packages build for x64 arch:
    build_python_packages(python_package_build_command, "x86_64")
    # Clean Python Extension artifacts before next Python packages build.
    # Clean is required due to the fact that only one Native target can be built at once.
    print_message("Clean Python extension artifacts before next build...")
    run_command(python_extension_clean_command)
    # Run Python 'manylinux' packages build for arm64 arch:
    build_python_packages(python_package_build_command, "arm64")
elif system == "Darwin" or system == "Windows":
    # For another systems (Windows, Mac), Python package will be built by Gradle task
    # for all Python binaries, defined in the settings file.
    # Enable Python Extension and packages build by Gradle:
    enable_python_package = "true"
    build_python_extension = "true"
    # Define Gradle command for Python packages build:
    python_package_build_command = [gradle_script_name, "python-package-build:build"]
    # Run Python packages build for all Python host installations, defined in the settings file:
    for python_paths in python_settings.values():
        # Collect all predefined parameters:
        build_parameters = [
            "-Pbuild_release=true",
            "-Ppython_bin_path=%s" % (python_paths["bin_path"]),
            "-Ppython_include_path=%s" % (python_paths["include_path"]),
            f"-Penable_python_package={enable_python_package}",
            f"-Pbuild_python_extension={build_python_extension}"
        ]
        # Run Python package build:
        build_python_packages(python_package_build_command + build_parameters)
        # And clean Python Extension artifacts before the next iteration:
        run_command(python_extension_clean_command)

# Print final message and exit:
print_message("Release build finished!")
sys.exit()
