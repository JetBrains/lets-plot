/*
 * Copyright (c) 2023. JetBrains s.r.o.
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

    val kotlinLoggingVersion = extra["kotlinLogging_version"] as String

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))
                implementation(project(":demo-common-util"))
                implementation(project(":demo-and-test-shared"))
            }
        }
        jvmMain {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                compileOnly("io.github.microutils:kotlin-logging-jvm:${kotlinLoggingVersion}")
//                implementation ("org.slf4j:slf4j-simple:${extra["slf4j_version"]}")  // Enable logging to console
            }
        }
        jsMain {
            dependencies{
                implementation(kotlin("stdlib-js"))
            }
        }
    }
}
