/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("jvm")
    id("org.openjfx.javafxplugin")
}

val batikVersion = project.extra["batik.version"] as String
val commonsIOVersion = project.extra["commons-io.version"] as String
val kotlinLoggingVersion = project.extra["kotlinLogging.version"] as String
val ktorVersion = project.extra["ktor.version"] as String
val jfxVersion = extra["jfx.version"] as String
val kotlinxDatetimeVersion = project.extra["kotlinx.datetime.version"] as String
val jsvgVersion = project.extra["weisj.jsvg.version"] as String

javafx {
    version = jfxVersion
    modules = listOf("javafx.controls", "javafx.swing")
}

dependencies {
    implementation(kotlin("stdlib-common"))
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":commons"))
    implementation(project(":datamodel"))
    implementation(project(":plot-base"))
    implementation(project(":plot-builder"))
    implementation(project(":plot-stem"))
    implementation(project(":plot-raster"))
    implementation(project(":demo-common-plot"))
    implementation(project(":demo-common-jvm-utils"))
    implementation(project(":demo-and-test-shared"))

    implementation(project(":canvas"))
    implementation(project(":livemap"))
    implementation(project(":plot-livemap"))
    implementation(project(":gis"))

    implementation(project(":platf-awt"))
    implementation(project(":platf-batik"))

    implementation("org.apache.xmlgraphics:batik-codec:${batikVersion}")
    implementation("commons-io:commons-io:${commonsIOVersion}")  // commons-io: a newer version than the one in Batik transitive dependency.

    implementation(project(":platf-jfx-swing"))

    implementation("io.ktor:ktor-client-cio:${ktorVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")
    implementation("com.github.weisj:jsvg:${jsvgVersion}")

    implementation("io.github.microutils:kotlin-logging-jvm:${kotlinLoggingVersion}")
    implementation("org.slf4j:slf4j-simple:${project.extra["slf4j.version"]}")  // Enable logging to console
}
