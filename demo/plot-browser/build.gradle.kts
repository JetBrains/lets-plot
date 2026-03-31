/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm("demoRunner")
    js {
        browser()
        binaries.executable()
    }

    wasmJs {
        browser()
        binaries.executable()
    }

    val kotlinLoggingVersion = project.extra["kotlinLogging.version"] as String
    val kotlinxHtmlVersion = project.extra["kotlinx.html.version"] as String
    val ktorVersion = project.extra["ktor.version"] as String
    val kotlinxCoroutinesVersion = project.extra["kotlinx.coroutines.version"] as String
    val kotlinxDatetimeVersion = project.extra["kotlinx.datetime.version"] as String

    // Fix "The Default Kotlin Hierarchy Template was not applied to 'project'..." warning
    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))
                implementation(project(":demo-common-plot"))
                implementation(project(":demo-and-test-shared"))
            }
        }
        named("demoRunnerMain") {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                implementation(project(":demo-common-jvm-utils"))

                implementation(project(":canvas"))
                implementation(project(":livemap"))
                implementation(project(":plot-livemap"))
                implementation(project(":gis"))

                implementation("io.github.oshai:kotlin-logging-jvm:${kotlinLoggingVersion}")
                implementation("io.ktor:ktor-client-cio:${ktorVersion}")
                implementation("org.slf4j:slf4j-simple:${project.extra["slf4j.version"]}")  // Enable logging to console
                implementation(project(":platf-awt"))

                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:${kotlinxHtmlVersion}")
            }
        }

        jsMain {
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
            dependencies {
                implementation(kotlin("stdlib-js"))

                implementation(project(":platf-w3c"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")
            }
        }

    }
}

tasks.named("demoRunnerMainClasses") {
    // Check if "dev" property is passed via -Pdev or if DEV env var is set
    val isDev = project.hasProperty("dev") || System.getenv("DEV") != null

    if (isDev) {
        dependsOn(":js-package:jsBrowserDevelopmentWebpack")
        dependsOn(":wasmjs-package:wasmJsBrowserDevelopmentWebpack")
    } else {
        dependsOn(":js-package:jsBrowserProductionWebpack")
        dependsOn(":wasmjs-package:wasmJsBrowserProductionWebpack")
    }
}
