/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val kotlinxCoroutinesVersion = extra["kotlinx_coroutines_version"] as String
val kotlinLoggingVersion = extra["kotlinLogging_version"] as String
val kotlinxHtmlVersion = extra["kotlinx_html_version"] as String


repositories {
    mavenCentral()
}

kotlin {
    jvm("demoRunner")
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

                implementation(project(":commons"))
                implementation(project(":plot-base"))
                implementation(project(":datamodel"))
                implementation(project(":demo-common-svg"))
            }
        }
        named("demoRunnerMain") {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(project(":demo-common-jvm-utils"))
                compileOnly("io.github.microutils:kotlin-logging-jvm:${kotlinLoggingVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:${kotlinxHtmlVersion}")
            }
        }
        jsMain {
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(project(":platf-w3c"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
            }
        }
    }
}
