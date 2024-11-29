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

    val batikVersion = project.extra["batik_version"] as String
    val kotlinLoggingVersion = project.extra["kotlinLogging_version"] as String
    val kotlinxHtmlVersion = project.extra["kotlinx_html_version"] as String

    // Fix "The Default Kotlin Hierarchy Template was not applied to 'project'..." warning
    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":demo-common-util"))
            }
        }
        val allJvm by creating {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                compileOnly("io.github.microutils:kotlin-logging-jvm:${kotlinLoggingVersion}")
            }
        }
        named("jvmBatikMain") {
            dependsOn(allJvm)
            dependencies {
                implementation(project(":platf-batik"))
                implementation(project(":demo-common-batik"))

                implementation("org.apache.xmlgraphics:batik-codec:${batikVersion}")
            }
        }
        named("jvmJfxMain") {
            dependsOn(allJvm)
            dependencies {
                implementation(project(":canvas")) // needed for `svg transform` parsing
                implementation(project(":platf-jfx-swing"))
                implementation(project(":demo-common-jfx"))
            }
        }
        named("jvmBrowserMain") {
            dependsOn(allJvm)
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:${kotlinxHtmlVersion}")
            }
        }
        jsMain {
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
            dependencies {
                implementation(kotlin("stdlib-js"))

                implementation(project(":plot-base"))
                implementation(project(":platf-w3c"))
            }
        }
    }
}
