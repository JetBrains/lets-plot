/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */
import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("multiplatform")
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

    target.apply {
        binaries {
            // ✅ Create separate executables for each demo file
            //executable("SimpleDemo") {
            //    entryPoint = "demo.svg.SimpleDemoKt.main"
            //}
            executable("referenceSvgDemoMain") {
                entryPoint = "referenceSvgDemoMain"
            }
            executable("SimpleCanvasDemo") {
                entryPoint = "simpleMagickCanvasDemoMain"
            }
            executable("BarPlotDemo") {
                entryPoint = "barPlotMain"
            }
        }
    }

    target.binaries.all {
        linkerOpts += listOf(
            "-L${rootProject.project.extra["imagemagick_lib_path"]}/lib",
            "-lMagickWand-7.Q8",
            "-lMagickCore-7.Q8",
            "-lfontconfig",
            "-lfreetype",
            "-lexpat",
            "-lz"
        )

        if (target == mingwX64()) {
            linkerOpts += listOf(
                "-lurlmon",
                "-lgdi32"
            )
        }
    }

    // Fix "The Default Kotlin Hierarchy Template was not applied to 'project'..." warning
    applyDefaultHierarchyTemplate()

    sourceSets {
        nativeMain {
            dependencies {
                implementation(project(":platf-native"))
                implementation(project(":platf-imagick"))
            }
        }

        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":canvas"))
                implementation(project(":plot-raster"))

                implementation(project(":demo-and-test-shared"))
                implementation(project(":demo-common-svg"))

                implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
            }
        }
    }
}
