/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.gradle.jvm.tasks.Jar

plugins {
    kotlin("multiplatform")
    `maven-publish`
    signing
}

val artifactBaseName = "lets-plot-image-export"
val artifactGroupId = project.group as String
val artifactVersion = project.version as String
val assertjVersion = project.extra["assertj.version"]

val hamcrestVersion = project.extra["hamcrest.version"]
val kotlinLoggingVersion = project.extra["kotlinLogging.version"]
val mavenLocalPath = rootProject.project.extra["localMavenRepository"]
val mockitoVersion = project.extra["mockito.version"]

kotlin {
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-stem"))
            }
        }
        jvmMain {
            dependencies {
                implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")

                implementation(kotlin("stdlib-jdk8"))

                implementation(project(":platf-awt"))
                implementation(project(":canvas"))
                implementation(project(":plot-raster"))
            }
        }
    }
}

val jvmJarPlotImageExport by tasks.named<Jar>("jvmJar") {
    archiveFileName.set("$artifactBaseName-${artifactVersion}.jar")

    // Add the LICENSE file to the META-INF folder inside published JAR files.
    metaInf {
        from("$rootDir") {
            include("LICENSE")
        }
    }
}

val jvmPlotImageExportSourcesJar by tasks.named<Jar>("jvmSourcesJar") {
    archiveFileName.set("$artifactBaseName-$artifactVersion-sources.jar")
    archiveClassifier.set("sources")
}

// Generating a Javadoc task for each publication task.
// Fixes "Task ':plot-image-export:publishJsPublicationToMavenRepository' uses this output of task
// ':plot-image-export:signJvmPublication' without declaring an explicit or implicit dependency" error.
// Issues:
//  - https://github.com/gradle-nexus/publish-plugin/issues/208
//  - https://github.com/gradle/gradle/issues/26091
//
val jvmPlotImageExportJarJavaDoc by tasks.registering(Jar::class) {
    archiveBaseName.set("$artifactBaseName-$artifactVersion-javadoc.jar")
    archiveClassifier.set("javadoc")
    from("$rootDir/README.md")
}

publishing {
    publications {
        register("letsPlotImageExport", MavenPublication::class) {
            groupId = artifactGroupId
            artifactId = artifactBaseName
            version = artifactVersion

            artifact(jvmJarPlotImageExport)
            artifact(jvmPlotImageExportSourcesJar)
            artifact(jvmPlotImageExportJarJavaDoc.get())

            pom {
                name = "Lets-Plot raster image export"
                description = "Exporting a plot to a raster image (PNG,JPG or TIFF)."
                url = "https://github.com/JetBrains/lets-plot"
                licenses {
                    license {
                        name = "MIT"
                        url = "https://raw.githubusercontent.com/JetBrains/lets-plot/master/LICENSE"
                    }
                }
                developers {
                    developer {
                        id = "jetbrains"
                        name = "JetBrains"
                        email = "lets-plot@jetbrains.com"
                    }
                }
                scm {
                    url = "https://github.com/JetBrains/lets-plot"
                }

                // Dependencies
                withXml {
                    val deps = asNode().appendNode("dependencies")

                    var dep = deps.appendNode("dependency")
                    dep.appendNode("groupId", project.group)
                    dep.appendNode("artifactId", "platf-awt")
                    dep.appendNode("version", project.version)

                    dep = deps.appendNode("dependency")
                    dep.appendNode("groupId", project.group)
                    dep.appendNode("artifactId", "canvas")
                    dep.appendNode("version", project.version)

                    dep = deps.appendNode("dependency")
                    dep.appendNode("groupId", project.group)
                    dep.appendNode("artifactId", "plot-raster")
                    dep.appendNode("version", project.version)

                }
            }
        }
    }
    repositories {
        mavenLocal {
            url = uri("$mavenLocalPath")
        }
        maven {
            // For SNAPSHOT publication use separate URL and credentials:
            if (version.toString().endsWith("-SNAPSHOT")) {
                url = uri(rootProject.project.extra["mavenSnapshotPublishUrl"].toString())

                credentials {
                    username = rootProject.project.extra["sonatypeUsername"].toString()
                    password = rootProject.project.extra["sonatypePassword"].toString()
                }
            } else {
                url = uri(rootProject.project.extra["mavenReleasePublishUrl"].toString())
            }
        }
    }
}
signing {
    if (!project.version.toString().contains("SNAPSHOT")) {
        sign(publishing.publications["letsPlotImageExport"])
    }
}
