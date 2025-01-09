/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val ktorVersion = project.extra["ktor_version"] as String

kotlin {
    jvm()
    js {
        browser()
        binaries.executable()
    }

    // Fix "The Default Kotlin Hierarchy Template was not applied to 'project'..." warning
    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("io.ktor:ktor-client-websockets:${ktorVersion}")

                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))
                implementation(project(":canvas"))
                implementation(project(":gis"))
                implementation(project(":livemap"))
                implementation(project(":plot-livemap"))
                implementation(project(":demo-and-test-shared"))
            }
        }

        jsMain {
            dependencies {
                implementation("io.ktor:ktor-client-js:${ktorVersion}")
                implementation("io.ktor:ktor-client-websockets-js:${ktorVersion}")
            }
        }
    }
}
