/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("jvm")
}

val kotlinLoggingVersion = project.extra["kotlinLogging.version"] as String
val kotlinxHtmlVersion = project.extra["kotlinx.html.version"] as String

dependencies {
    implementation(kotlin("stdlib-common"))
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":commons"))
    implementation(project(":plot-stem"))
    implementation(project(":platf-awt"))
    implementation(project(":demo-common-plot"))
    implementation(project(":demo-common-jvm-utils"))

    compileOnly("io.github.microutils:kotlin-logging-jvm:${kotlinLoggingVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:${kotlinxHtmlVersion}")
    implementation("org.slf4j:slf4j-simple:${project.extra["slf4j.version"]}")  // Enable logging to console
}
