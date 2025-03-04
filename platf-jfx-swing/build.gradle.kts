/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */
plugins {
    id ("org.openjfx.javafxplugin")
}

val jfxVersion = extra["jfx_version"] as String
val assertjVersion = project.extra["assertj_version"] as String

javafx {
    version = jfxVersion
    modules = listOf("javafx.controls", "javafx.swing")
}

dependencies {
    compileOnly(project("::platf-awt"))
    compileOnly(project(":commons"))
    compileOnly(project(":datamodel"))
    compileOnly(project(":canvas"))
    compileOnly(project(":plot-stem"))
    compileOnly(project(":platf-awt"))
    testImplementation(project(":demo-and-test-shared"))
    testImplementation(project(":platf-awt"))
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("org.assertj:assertj-core:$assertjVersion")
}
