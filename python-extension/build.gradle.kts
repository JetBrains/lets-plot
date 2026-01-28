/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("multiplatform")
}

val ktorVersion = project.extra["ktor.version"] as String

val enablePythonPackage: Boolean = (rootProject.project.extra["enable_python_package"] as String).toBoolean()
val os: OperatingSystem = OperatingSystem.current()
val arch = rootProject.project.extra["architecture"]

// To improve the building time of the Python extension in development mode.
val pythonExtensionDebugBuild = project.findProperty("python_extension_debug_build") == "true"

kotlin {
    //applyDefaultHierarchyTemplate()

    val target = if (enablePythonPackage) {
        val target = when {
            os.isMacOsX && arch == "arm64" -> macosArm64()
            os.isMacOsX && arch == "x86_64" -> macosX64()
            os.isLinux && arch == "arm64" -> linuxArm64()
            os.isLinux && arch == "x86_64" -> linuxX64()
            os.isWindows -> mingwX64()
            else -> throw Exception("Unsupported platform! Check project settings.")
        }

        target.binaries {
            staticLib(buildTypes = if (pythonExtensionDebugBuild) listOf(DEBUG) else listOf(RELEASE)) {
                baseName = "lets-plot-${project.name}"
                if (pythonExtensionDebugBuild) {
                    // Output to the `releaseStatic` instead of the default `debugStatic` directory
                    // to not pass extra parameter to the setup.py in python-package.
                    val releaseStaticPath = outputDirectory.toPath().resolveSibling("releaseStatic").toFile()
                    releaseStaticPath.mkdirs()
                    outputDirectory = releaseStaticPath
                }
            }
        }

        val imageMagickLibPath = rootProject.project.extra["imagemagick_lib_path"].toString()
        target.binaries.forEach {
            it.linkerOpts += listOf(
                "-L${imageMagickLibPath}/lib",
                "-lMagickWand-7.Q16HDRI",
                "-lMagickCore-7.Q16HDRI",
                "-lfontconfig",
                "-lfreetype",
                "-lexpat",
                "-lz"
            )
            if (os.isWindows) {
                it.linkerOpts += listOf(
                    "-lurlmon",
                    "-lgdi32",
                    "-lws2_32",
                    "-liconv"
                )
            }
        }

        target.compilations.getByName("main") {
            val python by cinterops.creating {
                compilerOpts("-I${rootProject.project.extra["python.include_path"]}")
            }
        }
        target
    } else {
        jvm() // at least one target is required by MPP - dummy jvm target will work just fine
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }

        val nativeMain by creating {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))
                implementation(project(":platf-imagick"))
                implementation(project(":plot-raster"))
                implementation(project(":plot-livemap"))
                implementation(project(":livemap"))
                implementation(project(":gis"))

                implementation("io.ktor:ktor-client-core:${ktorVersion}")

                if (os.isMacOsX) {
                    // The Darwin client supports WSS and is unaffected by the frame fragmentation issue
                    // documented here: https://youtrack.jetbrains.com/issue/KTOR-9267.
                    // This allows for the development and testing of the LiveMap export feature.
                    implementation("io.ktor:ktor-client-darwin:${ktorVersion}")
                } else {
                    // Uses CIO for Linux and Windows
                    implementation("io.ktor:ktor-client-cio:${ktorVersion}")
                }
            }
        }

            val nativeTest by creating {
                dependencies {
                    implementation(project(":demo-and-test-shared"))
                    implementation(project(":demo-common-svg"))
                }
            }
    }
}
