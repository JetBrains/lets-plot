/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

val mockkVersion = project.extra["mockk_version"] as String
val kotlinLoggingVersion = project.extra["kotlinLogging_version"] as String
val kotlinxCoroutinesVersion = project.extra["kotlinx_coroutines_version"] as String
val kotlinxAtomicfuVersion = project.extra["kotlinx_atomicfu_version"] as String
val hamcrestVersion = project.extra["hamcrest_version"] as String
val mockitoVersion = project.extra["mockito_version"] as String
val assertjVersion = project.extra["assertj_version"] as String

kotlin {
    jvm()
    js {
        browser()
    }

    sourceSets {
        commonMain {
            dependencies {
                compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
                
                compileOnly(project(":commons"))
                compileOnly(project(":datamodel"))
                compileOnly(project(":plot-base"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":demo-and-test-shared"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
                implementation("org.jetbrains.kotlinx:atomicfu:$kotlinxAtomicfuVersion")
            }
        }
        jvmMain {
            dependencies {
                compileOnly("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
            }
        }
        jvmTest {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("org.hamcrest:hamcrest-core:$hamcrestVersion")
                implementation("org.hamcrest:hamcrest-library:$hamcrestVersion")
                implementation("org.mockito:mockito-core:$mockitoVersion")
                implementation("org.assertj:assertj-core:$assertjVersion")
            }
        }
        named("jsMain") {
            dependencies {
                compileOnly("io.github.microutils:kotlin-logging-js:$kotlinLoggingVersion")
            }
        }
        named("jsTest")
    }
}
