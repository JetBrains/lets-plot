/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm("demoRunner")
    js {
        browser()
        binaries.executable()
    }

    val kotlinLoggingVersion = project.extra["kotlinLogging_version"] as String
    val kotlinxHtmlVersion = project.extra["kotlinx_html_version"] as String
    val ktorVersion = project.extra["ktor_version"] as String

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
                implementation(project(":canvas"))
                implementation(project(":gis"))
                implementation(project(":livemap"))
                implementation(project(":plot-livemap"))
                implementation(project(":demo-and-test-shared"))
                implementation(project(":demo-common-livemap"))
            }
        }
        named("demoRunnerMain") {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                implementation(project(":demo-common-jvm-utils"))

                compileOnly("io.github.microutils:kotlin-logging-jvm:${kotlinLoggingVersion}")
                implementation("io.ktor:ktor-client-cio:${ktorVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:${kotlinxHtmlVersion}")
                implementation("org.slf4j:slf4j-simple:${project.extra["slf4j_version"]}")  // Enable logging to console
            }
        }
        jsMain {
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(project(":platf-w3c"))

                implementation("io.ktor:ktor-client-js:${ktorVersion}")
                implementation("io.ktor:ktor-client-websockets:${ktorVersion}")
            }
        }
    }
}
