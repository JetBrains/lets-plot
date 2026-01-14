/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
    `maven-publish`
}


val mockkVersion = project.extra["mockk.version"] as String
val kotlinLoggingVersion = project.extra["kotlinLogging.version"] as String
val kotlinxCoroutinesVersion = project.extra["kotlinx.coroutines.version"] as String
val kotlinxDatetimeVersion = project.extra["kotlinx.datetime.version"] as String

kotlin {
    jvm()
    macosArm64()

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

        jvmMain {
            dependencies {
                compileOnly("junit:junit:4.13.2")
                compileOnly("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
                api("org.jetbrains.kotlin:kotlin-test")
            }
        }
    }
    sourceSets.commonMain.dependencies {
        implementation(kotlin("test"))
    }
}

