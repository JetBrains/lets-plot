/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val jfxVersion = extra["jfx_version"] as String
val jfxPlatform = extra["jfx_platform_resolved"] as String

kotlin {
    jvm()
//    js {
//        browser()
//    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":commons"))
                implementation(project(":base-portable"))
            }
        }

        named("jvmMain") {
            dependencies {
                compileOnly("org.openjfx:javafx-base:$jfxVersion:$jfxPlatform")
                compileOnly("org.openjfx:javafx-graphics:$jfxVersion:$jfxPlatform")
            }
        }
    }
}