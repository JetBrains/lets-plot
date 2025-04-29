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
            executable("PolarPlotDemo") {
                entryPoint = "polarPlotMain"
            }
            executable("PerformanceDemo") {
                entryPoint = "performanceMain"
            }
        }
    }

    target.binaries.forEach {
        it.linkerOpts += listOf(
            "-L${rootProject.project.extra["imagemagick_lib_path"]}/lib",
            "-L/usr/lib/x86_64-linux-gnu",
            "-L/opt/homebrew/opt/fontconfig/lib",
            "-L/opt/homebrew/opt/freetype/lib",
            "-lMagickWand-7.Q8",
            "-lMagickCore-7.Q8",
            "-lfontconfig",
            "-lfreetype",
            "-lz"
        )
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

val demos = listOf("0", "10", "100", "200", "400", "800", "1600", "3200")

// Can't use destructuring declaration here
// Caused by: java.lang.Throwable: Rewrite at slice SCRIPT_SCOPE key: class Build_gradle old value:
val platf = when {
    os.isMacOsX && arch == "arm64" -> "macosArm64" to "MacosArm64"
    os.isMacOsX && arch == "x86_64" -> "macosX64" to "MacosX64"
    os.isLinux && arch == "arm64" -> "linuxArm64" to "LinuxArm64"
    os.isLinux && arch == "x86_64" -> "linuxX64" to "LinuxX64"
    os.isWindows -> "mingwX64" to "MingwX64"
    else -> "" to ""
}

val dirName = platf.first
val taskName = platf.second

demos.forEach { demoSize ->
    tasks.register<Exec>("runPerformance${demoSize}Demo") {
        dependsOn("linkPerformanceDemoReleaseExecutable$taskName") // ensure it's built
        group = "run"
        description = "Runs Performance${demoSize}Demo with argument ${demoSize}"
        executable = "${project.buildDir}/bin/$dirName/PerformanceDemoReleaseExecutable/PerformanceDemo.kexe" // Ensure correct path

        doFirst {
            args(demoSize) // Use the args() method to add arguments
        }

        standardOutput = System.out
        errorOutput = System.err
    }

}

