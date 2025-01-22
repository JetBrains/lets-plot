/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

//import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    kotlin("multiplatform")
}

val kotlinLoggingVersion = project.extra["kotlinLogging_version"] as String

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }

        commonMain {
            dependencies {
                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))
            }
        }

        commonTest {
            dependencies {
                implementation(project(":demo-and-test-shared"))
                implementation(project(":canvas"))
                implementation(project(":plot-raster"))
                implementation("com.squareup.okio:okio:3.9.0")
                implementation("org.jetbrains.skiko:skiko:0.8.18")
                implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

            }
        }
    }
}