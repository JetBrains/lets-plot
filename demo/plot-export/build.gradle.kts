/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm("jvmBrowser")

    val kotlinLoggingVersion = project.extra["kotlinLogging_version"] as String
    val kotlinxHtmlVersion = project.extra["kotlinx_html_version"] as String

    sourceSets{
        commonMain{
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(project(":commons"))
                implementation(project(":plot-image-export"))
                implementation(project(":demo-plot-common"))
                implementation(project(":demo-common-util"))
            }
        }
        named("jvmBrowserMain") {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                compileOnly("io.github.microutils:kotlin-logging-jvm:${kotlinLoggingVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:${kotlinxHtmlVersion}")
                implementation("org.slf4j:slf4j-simple:${project.extra["slf4j_version"]}")  // Enable logging to console
            }
        }
    }
}
