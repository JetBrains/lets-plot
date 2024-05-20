/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()

    val jfxPlatform = extra["jfxPlatformResolved"] as String
    val jfxVersion = extra["jfx_version"] as String

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))
                api(project(":demo-common-util"))
            }
        }
        jvmMain {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                implementation(project(":canvas"))
                implementation(project(":platf-awt"))
                implementation(project(":platf-jfx-swing"))

                compileOnly("org.openjfx:javafx-swing:${jfxVersion}:${jfxPlatform}")
            }
        }
    }
}
