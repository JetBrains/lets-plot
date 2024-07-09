/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
}

// KT-55751. MPP / Gradle: Consumable configurations must have unique attributes.
// https://youtrack.jetbrains.com/issue/KT-55751/MPP-Gradle-Consumable-configurations-must-have-unique-attributes
//
val dummyAttribute = Attribute.of("dummyAttribute", String::class.java)

kotlin {
    jvm("jvmBatik") {
        attributes.attribute(dummyAttribute, "jvmBatik")
    }
    jvm("jvmJfx") {
        attributes.attribute(dummyAttribute, "jvmJfx")
    }
    jvm("jvmBrowser")
    js {
        browser()
        binaries.executable()
    }

    val batikVersion = extra["batik_version"] as String
    val kotlinLoggingVersion = extra["kotlinLogging_version"] as String
    val kotlinxHtmlVersion = extra["kotlinx_html_version"] as String
    val kotlinxCoroutinesVersion = extra["kotlinx_coroutines_version"] as String
    val ktorVersion = extra["ktor_version"] as String
    val jfxVersion = extra["jfx_version"] as String

    // Fix "The Default Kotlin Hierarchy Template was not applied to 'project'..." warning
    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")

                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))
                implementation(project(":demo-plot-common"))
                implementation(project(":demo-common-util"))
                implementation(project(":demo-and-test-shared"))
            }
        }
        val allJvm by creating {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                implementation(project(":canvas"))
                implementation(project(":livemap"))
                implementation(project(":plot-livemap"))
                implementation(project(":gis"))

                implementation("io.github.microutils:kotlin-logging-jvm:${kotlinLoggingVersion}")
                implementation("io.ktor:ktor-client-cio:${ktorVersion}")
                implementation("org.slf4j:slf4j-simple:${extra["slf4j_version"]}")  // Enable logging to console
            }
        }
        named("jvmBatikMain") {
            dependsOn(allJvm)
            dependencies {
                implementation(project(":demo-common-batik"))
                implementation(project(":platf-awt"))
                implementation(project(":platf-batik"))

                implementation("org.apache.xmlgraphics:batik-codec:${batikVersion}")
            }
        }
        named("jvmJfxMain") {
            dependsOn(allJvm)
            dependencies {
                implementation(project(":canvas"))
                implementation(project(":platf-awt"))
                implementation(project(":platf-jfx-swing"))
                implementation(project(":demo-common-jfx"))

                implementation("org.openjfx:javafx-base:${jfxVersion}")
                implementation("org.openjfx:javafx-graphics:${jfxVersion}")
                implementation("org.openjfx:javafx-swing:${jfxVersion}")
            }
        }
        named("jvmBrowserMain") {
            dependsOn(allJvm)
            dependencies {
                implementation(project(":platf-awt"))

                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:${kotlinxHtmlVersion}")
            }
        }
        jsMain {
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
            dependencies {
                implementation(kotlin("stdlib-js"))

                implementation(project(":platf-w3c"))
            }
        }
    }
}
