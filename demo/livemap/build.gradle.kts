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
    jvm("jvmJfx") {
        attributes.attribute(dummyAttribute, "jvmJfx")
    }
    jvm("jvmRawJfx") {
        attributes.attribute(dummyAttribute, "jvmRawJfx")
    }
    jvm("jvmRawAwt") {
        attributes.attribute(dummyAttribute, "jvmRawAwt")
    }
    jvm("jvmBrowser") {
        attributes.attribute(dummyAttribute, "jvmBrowser")
    }
    jvm("jvmJfxPlot") {
        attributes.attribute(dummyAttribute, "jvmJfxPlot")
    }
    jvm("jvmBatikPlot")
    js {
        browser()
        binaries.executable()
    }

    val batikVersion = extra["batik_version"] as String
    val kotlinLoggingVersion = extra["kotlinLogging_version"] as String
    val kotlinxHtmlVersion = extra["kotlinx_html_version"] as String
    val ktorVersion = extra["ktor_version"] as String
    val jfxVersion = extra["jfx_version"] as String

    // Fix "The Default Kotlin Hierarchy Template was not applied to 'project'..." warning
    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))
                implementation(project(":canvas"))
                implementation(project(":gis"))
                implementation(project(":livemap"))
                implementation(project(":plot-livemap"))
                implementation(project(":demo-and-test-shared"))
                implementation(project(":demo-common-util"))
            }
        }
        val allJvm by creating {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                compileOnly("io.github.microutils:kotlin-logging-jvm:${kotlinLoggingVersion}")
                implementation("io.ktor:ktor-client-cio:${ktorVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:${kotlinxHtmlVersion}")
                implementation("org.openjfx:javafx-base:${jfxVersion}")
                implementation("org.openjfx:javafx-controls:${jfxVersion}")
                implementation("org.openjfx:javafx-graphics:${jfxVersion}")
                implementation("org.openjfx:javafx-swing:${jfxVersion}")
                implementation("org.slf4j:slf4j-simple:${extra["slf4j_version"]}")  // Enable logging to console
            }
        }
        named("jvmJfxMain") {
            dependsOn(allJvm)
            dependencies {
                implementation(project(":platf-jfx-swing"))
            }
        }
        named("jvmRawJfxMain") {
            dependsOn(allJvm)
            dependencies {
                implementation(project(":platf-jfx-swing"))
            }
        }
        named("jvmRawAwtMain") {
            dependsOn(allJvm)
            dependencies {
                implementation(project(":platf-awt"))
            }
        }
        named("jvmBrowserMain") {
            dependsOn(allJvm)
        }
        named("jvmJfxPlotMain") {
            dependsOn(allJvm)
            dependencies {
                implementation(project(":platf-jfx-swing"))
                implementation(project(":platf-awt"))
                implementation(project(":demo-common-jfx"))
            }
        }
        named("jvmBatikPlotMain") {
            dependsOn(allJvm)
            dependencies {
                implementation(project(":demo-common-batik"))
                implementation(project(":platf-awt"))
                implementation(project(":platf-batik"))

                implementation("org.apache.xmlgraphics:batik-codec:${batikVersion}")
            }
        }
        jsMain {
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(project(":platf-w3c"))

                implementation("io.ktor:ktor-client-js:${ktorVersion}")
                implementation("io.ktor:ktor-client-websockets:${ktorVersion}")
            }
        }
    }
}
