/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("jvm")
    id ("org.openjfx.javafxplugin")
}

val batikVersion = project.extra["batik_version"] as String
val kotlinLoggingVersion = project.extra["kotlinLogging_version"] as String
val kotlinxCoroutinesVersion = project.extra["kotlinx_coroutines_version"] as String
val ktorVersion = project.extra["ktor_version"] as String

javafx {
    version = extra["jfx_version"] as String
    modules = listOf( "javafx.base", "javafx.graphics", "javafx.swing")
}

dependencies {
    implementation(kotlin("stdlib-common"))
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":commons"))
    implementation(project(":datamodel"))
    implementation(project(":plot-base"))
    implementation(project(":plot-builder"))
    implementation(project(":plot-stem"))
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

    implementation(project(":platf-jfx-swing"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("io.github.microutils:kotlin-logging-jvm:${kotlinLoggingVersion}")
    implementation("io.ktor:ktor-client-cio:${ktorVersion}")
    implementation("org.slf4j:slf4j-simple:${project.extra["slf4j_version"]}")  // Enable logging to console

}
