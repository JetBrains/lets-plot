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
        python_extension_build_command += [f"-Parchitecture={arch}"]
        command = build_command + [arch]
    else:
        command = build_command
    print_message(f"Building Python Extension...")
    run_command(python_extension_build_command + build_parameters)
    print_message(f"Building Python Package...")
    run_command(command)


def get_python_arch(python_bin_path):
    get_python_arch_command = [f"{python_bin_path}/python", "-c", "import platform; print(platform.machine())"]
    process = subprocess.check_output(get_python_arch_command, stderr=None)
    current_python_arch = process.decode().strip()
    if current_python_arch == "arm64" or current_python_arch == "x86_64":
        return current_python_arch
    else:
        print_error_and_exit(f"Got wrong Python architecture for {python_bin_path}!\n"
                             f"Check your settings file or Python installation.")


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

python_extension_clean_command = [gradle_script_name, "python-extension:clean"]


# Run Python artifacts build.
if system == "Linux":
    # Define Linux-specific parameters.
    # Python linux packages will be built inside Docker 'manylinux' containers (PEP 599).
    # Docker containers will be run by the external shell script, depending on target arch.
    python_package_build_command = ["./tools/run_manylinux_docker.sh"]

    # So the only one Python host installation, defined in the settings file, is needed:
    python_paths = list(python_settings.values())[0]

    # Enable Python package to build Python extension module.
    enable_python_package = "true"

    # Collect all predefined parameters:
    build_parameters = [
        "-Pbuild_release=true",
        "-Ppython.bin_path=%s" % (python_paths["bin_path"]),
        "-Ppython.include_path=%s" % (python_paths["include_path"]),
        f"-Penable_python_package={enable_python_package}"
    ]

    # Run JS artifact build first:
    gradle_js_build_command = [gradle_script_name, "js-package:jsBrowserProductionWebpack", "-Parchitecture=x86_64"]
    run_command(gradle_js_build_command + build_parameters)

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

    # Define Gradle command for Python packages build:
    python_package_build_command = [gradle_script_name, "python-package-build:build"]

    # Run Python packages build for all Python host installations, defined in the settings file:
    for python_paths in python_settings.values():
        # Collect all predefined parameters:
        build_parameters = [
            "-Pbuild_release=true",
            "-Ppython.bin_path=%s" % (python_paths["bin_path"]),
            "-Ppython.include_path=%s" % (python_paths["include_path"]),
            f"-Penable_python_package={enable_python_package}"
        ]
        # Add architecture parameter for Mac:
        if system == "Darwin":
            build_parameters += ["-Parchitecture=%s" % (get_python_arch(python_paths["bin_path"]))]

        # Run Python package build:
        build_python_packages(python_package_build_command + build_parameters)

        # And clean Python Extension artifacts before the next iteration:
        run_command(python_extension_clean_command)

# Print final message and exit:
print_message("Release build finished!")
sys.exit()
