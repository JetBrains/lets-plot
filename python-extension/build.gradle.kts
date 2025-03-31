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

kotlin {
    //applyDefaultHierarchyTemplate()

    val target = if(enablePythonPackage) {
        val target = when {
            os.isMacOsX && arch == "arm64" -> macosArm64()
            os.isMacOsX && arch == "x86_64" -> macosX64()
            os.isLinux && arch == "arm64" -> linuxArm64()
            os.isLinux && arch == "x86_64" -> linuxX64()
            os.isWindows -> mingwX64()
            else -> throw Exception("Unsupported platform! Check project settings.")
        }

        target.binaries {
            staticLib {
                baseName = "lets-plot-${project.name}"
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
        val nativeMain by creating {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
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
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")

        }
    }
}
