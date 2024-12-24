/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("jvm")
}

val kotlinxHtmlVersion = project.extra["kotlinx_html_version"] as String
val jfxPlatform = project.extra["jfxPlatformResolved"] as String
val jfxVersion = project.extra["jfx_version"] as String

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

    implementation("org.openjfx:javafx-base:${jfxVersion}:${jfxPlatform}")
    implementation("org.openjfx:javafx-controls:${jfxVersion}:${jfxPlatform}")
    implementation("org.openjfx:javafx-graphics:${jfxVersion}:${jfxPlatform}")
    implementation("org.openjfx:javafx-swing:${jfxVersion}:${jfxPlatform}")

}
