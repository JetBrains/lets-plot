/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    js {
        browser()
    }

    val kotlinLoggingVersion = project.extra["kotlinLogging.version"] as String

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.github.microutils:kotlin-logging:${kotlinLoggingVersion}")

                api(project(":commons"))
                api(project(":canvas"))
                api(project(":datamodel"))
                api(project(":plot-base"))
                api(project(":plot-builder"))
                api(project(":plot-stem"))
            }
        }

        jvmMain {
            dependencies {
                api(project(":platf-awt"))
            }
        }
    }
}

