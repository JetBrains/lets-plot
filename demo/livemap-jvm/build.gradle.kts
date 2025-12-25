/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("jvm")
}

val batikVersion = project.extra["batik.version"] as String
//val commonsIOVersion = project.extra["commons-io.version"] as String
val kotlinLoggingVersion = project.extra["kotlinLogging.version"] as String
val kotlinxHtmlVersion = project.extra["kotlinx.html.version"] as String
val ktorVersion = project.extra["ktor.version"] as String


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
    implementation(project(":platf-batik"))
    implementation(project(":platf-awt"))

    implementation("org.apache.xmlgraphics:batik-codec:${batikVersion}")
//    implementation("commons-io:commons-io:${commonsIOVersion}")  // commons-io: a newer version than the one in Batik transitive dependency.
    implementation("org.slf4j:slf4j-simple:${project.extra["slf4j.version"]}")  // Enable logging to console
    compileOnly("io.github.microutils:kotlin-logging-jvm:${kotlinLoggingVersion}")

    implementation(project(":demo-and-test-shared"))
    implementation(project(":demo-common-jvm-utils"))
    implementation(project(":demo-common-livemap"))
}
