/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
}

val kotlinxBrowserVersion = project.extra["kotlinx.browser.version"] as String

kotlin {
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        wasmJsMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-browser:$kotlinxBrowserVersion")

                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":canvas"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))
                implementation(project(":plot-raster"))
                implementation(project(":platf-w3c"))
                implementation(project(":wasmjs-package"))
                implementation(project(":demo-common-plot"))
                implementation(project(":demo-and-test-shared"))
            }
        }
    }
}
