/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
//    js {
//        browser()
//    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-livemap"))
                implementation(project(":canvas"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-config-portable"))
            }
        }

        commonTest {
            dependencies {
                implementation(project(":demo-and-test-shared"))
            }
        }

        jvmTest {
            dependencies {
//                implementation kotlin('test')
                implementation(kotlin("test-junit"))
//                implementation("org.hamcrest:hamcrest-core:$hamcrestVersion")
//                implementation("org.hamcrest:hamcrest-library:$hamcrestVersion")
//                implementation("org.mockito:mockito-core:$mockitoVersion")
//                implementation("org.assertj:assertj-core:$assertjVersion")
//                implementation("io.mockk:mockk:$mockkVersion")
            }
        }

    }
}