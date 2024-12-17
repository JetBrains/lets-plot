/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("jvm")
}

// KT-55751. MPP / Gradle: Consumable configurations must have unique attributes.
// https://youtrack.jetbrains.com/issue/KT-55751/MPP-Gradle-Consumable-configurations-must-have-unique-attributes
//
val batikVersion = project.extra["batik_version"] as String
val jfxPlatform = project.extra["jfxPlatformResolved"] as String
val jfxVersion = project.extra["jfx_version"] as String

dependencies {
    implementation(kotlin("stdlib-common"))
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":commons"))
    implementation(project(":datamodel"))
    implementation(project(":canvas")) // needed for `svg transform` parsing

    implementation(project(":demo-common-jvm-util"))
    implementation(project(":demo-common-svg"))

    // Batik
    implementation(project(":platf-awt"))
    implementation(project(":platf-batik"))
    implementation("org.apache.xmlgraphics:batik-codec:${batikVersion}")

    // JFX
    implementation(project(":platf-jfx-swing"))
    implementation("org.openjfx:javafx-base:${jfxVersion}:${jfxPlatform}")
    implementation("org.openjfx:javafx-graphics:${jfxVersion}:${jfxPlatform}")
    implementation("org.openjfx:javafx-swing:${jfxVersion}:${jfxPlatform}")

}
