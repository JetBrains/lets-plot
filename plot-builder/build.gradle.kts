/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val mockkVersion = extra["mockk_version"] as String
val kotlinLoggingVersion = extra["kotlinLogging_version"] as String
val hamcrestVersion = extra["hamcrest_version"] as String
val mockitoVersion = extra["mockito_version"] as String
val assertjVersion = extra["assertj_version"] as String

kotlin {
    jvm()
    js {
        browser()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
            }
        }
        commonTest {
            dependencies {
//                implementation kotlin('test-common')
//                implementation kotlin('test-annotations-common')
                implementation(kotlin("test"))
                implementation(project(":test-common"))
            }
        }
        jvmMain {
            dependencies {
                implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
            }
        }
        jvmTest {
            dependencies {
//                implementation kotlin('test')
                implementation(kotlin("test-junit"))
                implementation("org.hamcrest:hamcrest-core:$hamcrestVersion")
                implementation("org.hamcrest:hamcrest-library:$hamcrestVersion")
                implementation("org.mockito:mockito-core:$mockitoVersion")
                implementation("org.assertj:assertj-core:$assertjVersion")
            }
        }
        named("jsMain") {
            dependencies {
//                implementation kotlin('stdlib-js')
                implementation("io.github.microutils:kotlin-logging-js:$kotlinLoggingVersion")
            }
        }
        named("jsTest") {
            dependencies {
//                implementation kotlin('test-js')
            }
        }
    }
}
