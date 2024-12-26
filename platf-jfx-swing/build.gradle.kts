/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */
plugins {
    id ("org.openjfx.javafxplugin")
}

javafx {
    version = extra["jfx_version"] as String
    modules = listOf( "javafx.base", "javafx.swing", "javafx.graphics" )
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
}
