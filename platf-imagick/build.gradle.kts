/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("multiplatform")
}

val imageMagickLibPath = rootProject.project.extra["imagemagick_lib_path"].toString()
val imagickDir = File(imageMagickLibPath)

if (!imagickDir.exists() || !imagickDir.isDirectory) {
    logger.warn("ImageMagick source directory not found at: $imagickDir.\nRun the following task to init:\n./gradlew :initImageMagick")
}

val os: OperatingSystem = OperatingSystem.current()
val arch = rootProject.project.extra["architecture"]
val kotlinLoggingVersion = project.extra["kotlinLogging_version"] as String

kotlin {
    val target = when {
        os.isMacOsX && arch == "arm64" -> macosArm64()
        os.isMacOsX && arch == "x86_64" -> macosX64()
        os.isLinux && arch == "arm64" -> linuxArm64()
        os.isLinux && arch == "x86_64" -> linuxX64()
        os.isWindows -> mingwX64()
        else -> throw Exception("Unsupported platform! Check project settings.")
    }

    target.compilations.getByName("main") {
        val imageMagick by cinterops.creating {
            if (os.isWindows) {
                compilerOpts += listOf("-D_LIB")
            }
            compilerOpts += listOf(
                "-I${imageMagickLibPath}/include/ImageMagick-7",
            )
        }
    }

    target.binaries.all {
        linkerOpts += listOf(
            "-L${imageMagickLibPath}/lib",
            "-lMagickWand-7.Q16HDRI",
            "-lMagickCore-7.Q16HDRI",
            "-lfontconfig",
            "-lfreetype",
            "-lexpat",
            "-lz"
        )
        if (os.isWindows) {
            linkerOpts += listOf(
                "-lurlmon",
                "-lgdi32"
            )
        } else {
            linkerOpts += listOf(
                "-Wl,-rpath,${imageMagickLibPath}/lib"
            )
        }
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }

        commonMain {
            dependencies {
                implementation(project(":commons"))
                implementation(project(":canvas"))
                implementation("io.github.microutils:kotlin-logging:${kotlinLoggingVersion}")
            }
        }

        nativeTest {
            dependencies {
                implementation(project(":demo-and-test-shared"))
            }
        }
    }
}