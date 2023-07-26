/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}


kotlin {
    jvm()
    js().browser()

    val kotlinLoggingVersion = extra["kotlinLogging_version"] as String

    sourceSets {
        commonMain {
            dependencies {
                compileOnly(project(":commons"))
                compileOnly(project(":datamodel"))
                compileOnly(project(":plot-base"))
                compileOnly(project(":plot-builder"))
                compileOnly(project(":plot-stem"))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        jvmMain {
            dependencies {
                compileOnly("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
            }
        }

        named("jsMain") {
            dependencies {
                compileOnly("io.github.microutils:kotlin-logging-js:$kotlinLoggingVersion")
            }
        }
    }
}

