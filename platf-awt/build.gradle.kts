/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

val jsvgVersion = project.extra["weisj.jsvg.version"] as String
val assertjVersion = extra["assertj.version"] as String
val kotlinxCoroutinesVersion = project.extra["kotlinx.coroutines.version"] as String
val ktorVersion = project.extra["ktor.version"] as String

dependencies {
    compileOnly(project(":commons"))
    compileOnly(project(":datamodel"))
    compileOnly(project(":plot-livemap"))
    compileOnly(project(":canvas"))
    compileOnly(project(":plot-base"))
    compileOnly(project(":plot-builder"))
    compileOnly(project(":plot-stem"))
    compileOnly(project(":plot-raster"))

    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:${kotlinxCoroutinesVersion}")
    compileOnly("com.github.weisj:jsvg:${jsvgVersion}")

    testImplementation(project(":demo-and-test-shared"))
    testImplementation(project(":demo-common-svg"))
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":canvas"))
    testImplementation(project(":plot-raster"))
    testImplementation(project(":plot-livemap"))
    testImplementation(project(":livemap"))
    testImplementation(project(":gis"))
    testImplementation(project(":visual-testing"))

    testImplementation("io.ktor:ktor-client-cio:${ktorVersion}")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${kotlinxCoroutinesVersion}")
    testImplementation("org.assertj:assertj-core:${assertjVersion}")
}