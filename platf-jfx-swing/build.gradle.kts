/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("jvm")
}

val jfxVersion = project.extra["jfx_version"] as String
val jfxPlatform = project.extra["jfxPlatformResolved"] as String

dependencies {
    compileOnly(project("::platf-awt"))
    compileOnly(project(":commons"))
    compileOnly(project(":datamodel"))
    compileOnly(project(":canvas"))
    compileOnly(project(":plot-stem"))

    compileOnly("org.openjfx:javafx-base:$jfxVersion:$jfxPlatform")
    compileOnly("org.openjfx:javafx-graphics:$jfxVersion:$jfxPlatform")
    compileOnly("org.openjfx:javafx-swing:$jfxVersion:$jfxPlatform")

    compileOnly(project(":platf-awt"))

    testImplementation(project(":demo-and-test-shared"))
    testImplementation(project(":platf-awt"))
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("org.openjfx:javafx-base:$jfxVersion:$jfxPlatform")
    testImplementation("org.openjfx:javafx-graphics:$jfxVersion:$jfxPlatform")
    testImplementation("org.openjfx:javafx-swing:$jfxVersion:$jfxPlatform")
}
