/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("multiplatform")
}

val imagickDir = rootProject.file("platf-imagick/ImageMagick")

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
                "-I${rootProject.project.extra["imagemagick_lib_path"]}/include/ImageMagick-7",
            )
        }
    }

    target.binaries.forEach {
        it.linkerOpts += listOf(
            "-L${rootProject.project.extra["imagemagick_lib_path"]}/lib",
            "-L/usr/lib/x86_64-linux-gnu/",
            "-L/opt/homebrew/opt/fontconfig/lib",
            "-L/opt/homebrew/opt/freetype/lib",
            "-lfreetype",
            "-lfontconfig",
            "-lMagickWand-7.Q8",
            "-lMagickCore-7.Q8",
            "-lz"
        )
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
                implementation(project(":demo-common-svg"))

                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))
                implementation(project(":plot-raster"))
            }
        }
    }
}