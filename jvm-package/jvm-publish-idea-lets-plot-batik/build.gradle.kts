/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("multiplatform")
//    `maven-publish`
//    signing
    id("com.github.johnrengelman.shadow") version "8.1.1" // For creating the fat JAR with relocated classes
}

kotlin {
    jvm()
}

val artifactBaseName = "idea-lets-plot-batik"
val artifactGroupId = project.group as String
val artifactVersion = project.version as String
val batikVersion = project.extra["batik_version"] as String
val ktorVersion = project.extra["ktor_version"] as String
val kotlinLoggingVersion = project.extra["kotlinLogging_version"] as String
val mavenLocalPath = rootProject.project.extra["localMavenRepository"]

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-stem"))

                implementation(project(":platf-awt"))
                implementation(project(":platf-batik"))
                implementation("org.apache.xmlgraphics:batik-codec:$batikVersion")

                implementation(project(":canvas"))
                implementation(project(":gis"))
                implementation(project(":livemap"))
                implementation(project(":plot-livemap"))

                implementation("io.ktor:ktor-client-websockets-jvm:$ktorVersion")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
            }
        }
    }
}

// Create fat JAR with shadowed (relocated) classes
val shadowJar = tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    from(kotlin.jvm().compilations.getByName("main").output)
    configurations = listOf(project.configurations.getByName("jvmRuntimeClasspath"))

    archiveBaseName.set(artifactBaseName)
    archiveVersion.set(artifactVersion)
    archiveClassifier.set("")

    // Exclude packages
    exclude("org/slf4j/**")
    exclude("org/intellij/**")
    exclude("org/jetbrains/annotations/**")
    exclude("kotlinx/coroutines/**")
    exclude("kotlin/**")
    exclude("javax/xml/**")
    exclude("_COROUTINE/**")
    exclude("DebugProbesKt.bin")

    // Relocate all classes to idea243 package
    relocate("org.jetbrains.letsPlot", "idea243.org.jetbrains.letsPlot")
    relocate("io.ktor", "idea243.io.ktor")

    relocate("org.apache.batik", "idea243.org.apache.batik")
    relocate("org.xml.sax", "idea243.org.xml.sax")
    relocate("org.w3c.dom", "idea243.org.w3c.dom")
    relocate("org.w3c.css", "idea243.org.w3c.css")
    relocate("org.apache.xmlgraphics", "idea243.org.apache.xmlgraphics")
    relocate("org.apache.xmlcommons", "idea243.org.apache.xmlcommons")
    relocate("org.apache.commons", "idea243.org.apache.commons")

    // Add LICENSE file to the META-INF folder inside published JAR files
    metaInf {
        from("$rootDir") {
            include("LICENSE")
        }
    }

    // Merge service files
    mergeServiceFiles()
}

// Configure the JVM jar task to use the shadow jar output
val jvmJar by tasks.named<Jar>("jvmJar") {
    enabled = false
}

// Register the shadowJar as an artifact
artifacts {
    add("archives", shadowJar)
}

//publishing {
//    publications {
//        register("ideaLetsPlotBatik", MavenPublication::class) {
//            groupId = artifactGroupId
//            artifactId = artifactBaseName
//            version = artifactVersion
//
//            artifact(shadowJar.get())
//
//            pom {
//                name = "IDEA Lets-Plot Batik"
//                description = "Combined Lets-Plot package with relocated classes for IDEA integration"
//                url = "https://github.com/JetBrains/lets-plot"
//
//                licenses {
//                    license {
//                        name = "MIT"
//                        url = "https://raw.githubusercontent.com/JetBrains/lets-plot/master/LICENSE"
//                    }
//                }
//
//                developers {
//                    developer {
//                        id = "jetbrains"
//                        name = "JetBrains"
//                        email = "lets-plot@jetbrains.com"
//                    }
//                }
//
//                scm {
//                    url = "https://github.com/JetBrains/lets-plot"
//                }
//
//                // This is a self-contained artifact, so we don't need to declare dependencies
//            }
//        }
//    }
//
//    repositories {
//        mavenLocal {
//            url = uri("$mavenLocalPath")
//        }
//    }
//}
//
//signing {
//    if (!project.version.toString().contains("SNAPSHOT")) {
//        sign(publishing.publications["ideaLetsPlotBatik"])
//    }
//}