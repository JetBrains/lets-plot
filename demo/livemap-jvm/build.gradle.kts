/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("jvm")
}

val batikVersion = project.extra["batik_version"] as String
val kotlinLoggingVersion = project.extra["kotlinLogging_version"] as String
val kotlinxHtmlVersion = project.extra["kotlinx_html_version"] as String
val ktorVersion = project.extra["ktor_version"] as String
val jfxPlatform = project.extra["jfxPlatformResolved"] as String
val jfxVersion = project.extra["jfx_version"] as String

dependencies {
    implementation(kotlin("stdlib-common"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-client-cio:${ktorVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:${kotlinxHtmlVersion}")

    implementation(project(":commons"))
    implementation(project(":datamodel"))
    implementation(project(":plot-base"))
    implementation(project(":plot-builder"))
    implementation(project(":plot-stem"))
    implementation(project(":canvas"))
    implementation(project(":gis"))
    implementation(project(":livemap"))
    implementation(project(":plot-livemap"))
    implementation(project(":platf-jfx-swing"))
    implementation(project(":platf-batik"))
    implementation(project(":platf-awt"))

    implementation("org.apache.xmlgraphics:batik-codec:${batikVersion}")
    implementation("org.openjfx:javafx-base:${jfxVersion}:${jfxPlatform}")
    implementation("org.openjfx:javafx-controls:${jfxVersion}:${jfxPlatform}")
    implementation("org.openjfx:javafx-graphics:${jfxVersion}:${jfxPlatform}")
    implementation("org.openjfx:javafx-swing:${jfxVersion}:${jfxPlatform}")
    implementation("org.slf4j:slf4j-simple:${project.extra["slf4j_version"]}")  // Enable logging to console
    compileOnly("io.github.microutils:kotlin-logging-jvm:${kotlinLoggingVersion}")


    implementation(project(":demo-and-test-shared"))
    implementation(project(":demo-common-jvm-utils"))
    implementation(project(":demo-common-livemap"))
}
