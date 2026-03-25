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

    applyDefaultHierarchyTemplate()

    val kotlinxDatetimeVersion = project.extra["kotlinx.datetime.version"] as String

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))
                implementation(project(":demo-and-test-shared"))

                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")
            }
        }
    }
}
