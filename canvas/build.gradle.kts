@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val kotlinLoggingVersion = project.extra["kotlinLogging.version"] as String
val assertjVersion = project.extra["assertj.version"] as String

kotlin {
    js {
        browser {}
    }
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain {
            dependencies {
                compileOnly(project(":commons"))

                compileOnly("io.github.oshai:kotlin-logging:$kotlinLoggingVersion")
            }
        }

        jvmTest {
            dependencies {
                implementation(project(":commons"))
                implementation(kotlin("test"))
                implementation("org.assertj:assertj-core:$assertjVersion")
            }
        }

    }
}