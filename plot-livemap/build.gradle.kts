/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    js() {
        browser {}
    }

    val ktorVersion = project.extra["ktor_version"] as String
    val kotlinLoggingVersion = project.extra["kotlinLogging_version"] as String

    sourceSets {
        commonMain {
            dependencies {
                compileOnly("io.ktor:ktor-client-websockets:$ktorVersion")

                compileOnly(project(":commons"))
                compileOnly(project(":datamodel"))
                compileOnly(project(":canvas"))
                compileOnly(project(":gis"))
                compileOnly(project(":livemap"))
                compileOnly(project(":plot-base"))
                compileOnly(project(":plot-builder"))
                compileOnly(project(":plot-stem"))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.ktor:ktor-client-websockets:$ktorVersion")

                implementation(project(":demo-and-test-shared"))
                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":canvas"))
                implementation(project(":gis"))
                implementation(project(":livemap"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))
            }
        }

        jvmMain {
            dependencies {
                compileOnly("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
            }
        }

        jvmTest {
            dependencies {
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
            }
        }

        named("jsMain") {
            dependencies {
                compileOnly("io.ktor:ktor-client-js:$ktorVersion")
                compileOnly("io.github.microutils:kotlin-logging-js:$kotlinLoggingVersion")
            }
        }

        // Fix for 'Could not find "io.ktor:ktor-client-js"...'build error (Kotlin 1.9.xx versions):
        named("jsTest") {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktorVersion")
            }
        }
    }
}
