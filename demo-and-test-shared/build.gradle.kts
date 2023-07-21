/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    js {
        browser()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("test"))

                api(project(":commons"))
                api(project(":plot-base"))
                api(project(":plot-builder"))
                api(project(":plot-stem"))
            }
        }
    }
}

