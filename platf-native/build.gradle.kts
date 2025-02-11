/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("multiplatform")
}

val kotlinLoggingVersion = project.extra["kotlinLogging_version"] as String
val os: OperatingSystem = OperatingSystem.current()
val arch = rootProject.project.extra["architecture"]

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
            compilerOpts += listOf("-I${rootProject.project.extra["imagemagick_lib_path"]}/include/ImageMagick-7")
        }
    }

    target.binaries.all {
        linkerOpts += listOf("-L${rootProject.project.extra["imagemagick_lib_path"]}/lib/")
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }

        commonMain {
            dependencies {
                implementation(project(":commons"))
                implementation(project(":canvas"))
                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))
                implementation(project(":plot-raster"))
                implementation("com.squareup.okio:okio:3.9.0")
            }
        }

        //nativeTest {
        //    dependencies {
        //        implementation(project(":demo-and-test-shared"))
        //        implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
        //    }
        //}
    }
}