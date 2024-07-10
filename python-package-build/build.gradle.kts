/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.gradle.internal.os.OperatingSystem

plugins {
    id("base")
}

fun ExtraPropertiesExtension.getOrNull(name: String): Any? = if (has(name)) { get(name) } else { null }

val enablePythonPackage: Boolean = (rootProject.extra["enable_python_package"] as String).toBoolean()
val pythonPackagePath = "${rootDir}/python-package"
val pythonPackageBuildDir = "${pythonPackagePath}/build"
val pythonPackageDistDir = "${pythonPackagePath}/dist"

tasks.clean {
    delete(pythonPackageBuildDir)
    delete(pythonPackageDistDir)
    delete("${pythonPackagePath}/lets_plot/package_data")
    delete("${pythonPackagePath}/lets_plot.egg-info")
    delete("${pythonPackagePath}/kotlin-bridge/kotlin_bridge.o")
    delete(fileTree("${pythonPackagePath}/kotlin-bridge/"){
        include("*.def")
    })
}

if (enablePythonPackage) {
    val os: OperatingSystem = OperatingSystem.current()
    val pypiTestUsername = rootProject.extra.getOrNull("pypi.test.username")
    val pypiTestPassword = rootProject.extra.getOrNull("pypi.test.password")

    val pypiProdUsername = rootProject.extra.getOrNull("pypi.prod.username")
    val pypiProdPassword = rootProject.extra.getOrNull("pypi.prod.password")

    val pythonBinPath = rootProject.extra["python.bin_path"]

    val pythonBuildCommand = if (os.isWindows) {
        listOf("${pythonBinPath}/python",
            "setup.py",
            "bdist_wheel",
            "--dist-dir=${pythonPackageDistDir}",
            "build",
            "-c",
            "mingw32")
    } else {
         listOf("${pythonBinPath}/python",
            "-m",
            "build",
            "-w",
            "-o",
            pythonPackageDistDir)
    }

    val pythonTwineCommand = if (os.isWindows) "${pythonBinPath}/Scripts/twine" else "${pythonBinPath}/twine"

    tasks.register<Exec>("buildPythonPackage") {
        group = rootProject.extra["letsPlotTaskGroup"] as String
        description = "Builds lets-plot wheel distribution (python)"
        dependsOn(":js-package:build")
        dependsOn(":python-extension:build")

        workingDir(pythonPackagePath)
        commandLine(pythonBuildCommand)
    }

    tasks.build {
        dependsOn("buildPythonPackage")
    }

    if (pypiTestUsername != null && pypiTestPassword != null) {
        tasks.register<Exec>("publishTestPythonPackage") {
            group = rootProject.extra["letsPlotTaskGroup"] as String
            description = "Publishes lets-plot python package to test.pypi.org"
            workingDir(pythonPackageDistDir)
            commandLine(pythonTwineCommand,
                "upload",
                "--repository-url",
                "https://test.pypi.org/legacy/",
                "-u",
                pypiTestUsername,
                "-p",
                pypiTestPassword,
                "./*"
            )
        }
    }

    if (pypiProdUsername != null && pypiProdPassword != null) {
        tasks.register<Exec>("publishProdPythonPackage") {
            group = rootProject.extra["letsPlotTaskGroup"] as String
            description = "Publishes lets-plot python package to pypi.org"
            workingDir(pythonPackageDistDir)
            commandLine(pythonTwineCommand,
                "upload",
                "-u",
                pypiProdUsername,
                "-p",
                pypiProdPassword,
                "./*"
            )
        }
    }
}
