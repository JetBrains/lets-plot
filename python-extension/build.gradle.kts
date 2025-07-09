/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("multiplatform")
}

val enablePythonPackage: Boolean = (rootProject.project.extra["enable_python_package"] as String).toBoolean()
val os: OperatingSystem = OperatingSystem.current()
val arch = rootProject.project.extra["architecture"]
val imagick = (rootProject.project.extra.properties.getOrDefault("imagemagick_lib_path", "") as String).isNotBlank()


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

        if (imagick) {
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
                        "-lgdi32"
                    )
                }
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
            kotlin.setSrcDirs(
                listOf(if (imagick) "src/nativeImagickMain" else "src/nativeMain")
            )
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))
                implementation(project(":platf-native"))

                if (imagick) {
                    implementation(project(":platf-imagick"))
                    implementation(project(":plot-raster"))
                }
            }

        }

            val nativeTest by creating {
                kotlin.setSrcDirs(
                    listOf("src/nativeTest") + listOfNotNull("src/nativeImagickTest".takeIf { imagick })
                )
                dependencies {
                    implementation(project(":demo-and-test-shared"))
                    implementation(project(":demo-common-svg"))
                }
            }
    }
}
