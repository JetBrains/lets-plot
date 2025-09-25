/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
    `maven-publish`
    signing
}

val kotlinLoggingVersion = extra["kotlinLogging.version"] as String
val assertjVersion = extra["assertj.version"] as String

kotlin {
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                compileOnly(project(":commons"))
                compileOnly(project(":canvas"))
                compileOnly(project(":datamodel"))
                compileOnly(project(":plot-base"))
                compileOnly(project(":plot-stem"))
                compileOnly(project(":plot-builder"))
                implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
            }
        }

        named("jvmTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.assertj:assertj-core:$assertjVersion")
                implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

                implementation(project(":canvas"))
                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
                implementation(project(":plot-stem"))
                implementation(project(":plot-builder"))
            }
        }
    }
}
