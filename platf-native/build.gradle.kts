/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

//import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    kotlin("multiplatform")
}

val kotlinLoggingVersion = extra["kotlinLogging_version"] as String

kotlin {
    // Enable the default target hierarchy:
    targetHierarchy.default()

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":commons"))
                implementation(project(":plot-config-portable"))
            }
        }

        commonTest {
            dependencies {
                implementation(project(":demo-and-test-shared"))
            }
        }
    }
}