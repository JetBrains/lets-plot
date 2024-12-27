/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("jvm")
    id ("org.openjfx.javafxplugin")
}

val kotlinxHtmlVersion = project.extra["kotlinx_html_version"] as String
val jfxVersion = extra["jfx_version"] as String

javafx {
    version = jfxVersion
    modules = listOf("javafx.controls", "javafx.swing")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("stdlib-common"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:${kotlinxHtmlVersion}")

    implementation(project(":commons"))
    implementation(project(":datamodel"))
    implementation(project(":plot-stem"))
    implementation(project(":plot-builder"))
    implementation(project(":platf-awt"))
    implementation(project(":platf-batik"))
    implementation(project(":platf-jfx-swing"))
}
