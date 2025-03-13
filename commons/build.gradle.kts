/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}


val mockkVersion = project.extra["mockk_version"] as String
val kotlinLoggingVersion = project.extra["kotlinLogging_version"] as String
val kotlinxCoroutinesVersion = project.extra["kotlinx_coroutines_version"] as String
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
            }
        }

        nativeMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
            }
        }

        jvmMain {
            dependencies {
                compileOnly("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
            }
        }

        jvmTest {
            dependencies {
                implementation("org.hamcrest:hamcrest-core:$hamcrestVersion")
                implementation("org.hamcrest:hamcrest-library:$hamcrestVersion")
                implementation("org.mockito:mockito-core:$mockitoVersion")
                implementation("org.assertj:assertj-core:$assertjVersion")
                implementation("io.mockk:mockk:$mockkVersion")
            }
        }

        named("jsMain") {
            dependencies {
                compileOnly("io.github.microutils:kotlin-logging-js:$kotlinLoggingVersion")
            }
        }

        named("jsTest") {
            dependencies {
                implementation(kotlin("test-js"))

                // Fix for 'Could not find "io.github.microutils:kotlin-logging"...' build error (Kotlin 1.9.xx versions):
                implementation("io.github.microutils:kotlin-logging-js:$kotlinLoggingVersion")
            }
        }
    }
}

