/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("multiplatform")
    `maven-publish`
}


val mockkVersion = project.extra["mockk.version"] as String
val kotlinLoggingVersion = project.extra["kotlinLogging.version"] as String
val kotlinxCoroutinesVersion = project.extra["kotlinx.coroutines.version"] as String
val kotlinxDatetimeVersion = project.extra["kotlinx.datetime.version"] as String

val os: OperatingSystem = OperatingSystem.current()
val arch = rootProject.project.extra["architecture"]

kotlin {
    jvm()

    when {
        os.isMacOsX && arch == "arm64" -> macosArm64()
        os.isMacOsX && arch == "x86_64" -> macosX64()
        os.isLinux && arch == "arm64" -> linuxArm64()
        os.isLinux && arch == "x86_64" -> linuxX64()
        os.isWindows -> mingwX64()
        else -> throw Exception("Unsupported platform! Check project settings.")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":commons"))
                api(project(":canvas"))
                api(project(":datamodel"))
                api(project(":plot-base"))
                api(project(":plot-builder"))
                api(project(":plot-stem"))
                api(project(":plot-raster"))

                compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
                compileOnly("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")

                api("org.jetbrains.kotlin:kotlin-test")
            }
        }
    }
}

