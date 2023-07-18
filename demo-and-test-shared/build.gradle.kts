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
                api(project(":plot-config-portable"))
            }
        }
//        jvmMain {
//            dependencies {
//                implementation kotlin('test')
//                implementation kotlin('test-junit')
//            }
//        }
//        jsMain {
//            dependencies {
//                implementation kotlin('stdlib-js')
//                compileOnly kotlin('test-js')
//            }
//        }
    }
}

