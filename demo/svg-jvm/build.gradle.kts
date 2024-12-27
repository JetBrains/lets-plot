/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("jvm")
    id ("org.openjfx.javafxplugin")
}

val batikVersion = project.extra["batik_version"] as String
val jfxVersion = extra["jfx_version"] as String

javafx {
    version = jfxVersion
    modules = listOf("javafx.controls", "javafx.swing")
}

dependencies {
    implementation(kotlin("stdlib-common"))
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":commons"))
    implementation(project(":datamodel"))
    implementation(project(":canvas")) // needed for `svg transform` parsing

    implementation(project(":demo-common-jvm-utils"))
    implementation(project(":demo-common-svg"))

    // Batik
    implementation(project(":platf-awt"))
    implementation(project(":platf-batik"))
    implementation("org.apache.xmlgraphics:batik-codec:${batikVersion}")

    // JFX
    implementation(project(":platf-jfx-swing"))
}
