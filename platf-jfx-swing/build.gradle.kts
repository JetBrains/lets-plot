/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val jfxVersion = extra["jfx_version"] as String

kotlin {
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                compileOnly(project(":commons"))
                compileOnly(project(":datamodel"))
                compileOnly(project(":canvas"))
                compileOnly(project(":plot-stem"))
            }
        }

        commonTest {
            dependencies {
                implementation(project(":demo-and-test-shared"))
            }
        }

        named("jvmMain") {
            dependencies {
                compileOnly("org.openjfx:javafx-base:$jfxVersion")
                compileOnly("org.openjfx:javafx-graphics:$jfxVersion")
                compileOnly("org.openjfx:javafx-swing:$jfxVersion")

                compileOnly(project(":platf-awt"))
            }
        }

        named("jvmTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
    }
}