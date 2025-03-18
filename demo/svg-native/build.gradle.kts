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
            executable("ClippingDemo") {
                entryPoint = "clippingDemoMain"
            }
            executable("SimpleDemo") {
                entryPoint = "simpleDemoMain"
            }
        }
    }

    target.binaries.forEach {
        it.linkerOpts += listOf(
            "-L${rootProject.project.extra["imagemagick_lib_path"]}/lib",
            "-lMagickWand-7.Q16HDRI",
            "-lMagickCore-7.Q16HDRI",
            "-lpng",
            "-lz"
        )
    }

    // Fix "The Default Kotlin Hierarchy Template was not applied to 'project'..." warning
    applyDefaultHierarchyTemplate()

    sourceSets {
        nativeMain {
            dependencies {
                implementation(project(":platf-native"))
            }
        }

        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":canvas"))

                implementation(project(":demo-and-test-shared"))
                implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
            }
        }
    }
}
