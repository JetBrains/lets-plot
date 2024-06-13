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
val assertjVersion = extra["assertj_version"]
val batikGroupId = "org.apache.xmlgraphics"
val batikArtifacts = listOf("batik-transcoder", "batik-codec")
val batikVersion = extra["batik_version"]
val hamcrestVersion = extra["hamcrest_version"]
val kotlinLoggingVersion = extra["kotlinLogging_version"]
val mavenLocalPath = rootProject.extra["localMavenRepository"]
val mockitoVersion = extra["mockito_version"]
val tiffioGroupId = "com.twelvemonkeys.imageio"
val tiffioArtifact = "imageio-tiff"
val tiffioVersion = extra["twelvemonkeys_imageio_version"]

kotlin {
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation( kotlin("stdlib-common"))

                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-stem"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        jvmMain {
            dependencies {
                implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")

                implementation(kotlin("stdlib-jdk8"))

                implementation(project(":platf-awt"))

                batikArtifacts.forEach {
                    api("$batikGroupId:$it:$batikVersion")
                }

                // TIFF support
                implementation("$tiffioGroupId:$tiffioArtifact:$tiffioVersion")
            }
        }
        jvmTest {
            dependencies {
                implementation("org.assertj:assertj-core:$assertjVersion")
                implementation("org.hamcrest:hamcrest-core:$hamcrestVersion")
                implementation("org.hamcrest:hamcrest-library:$hamcrestVersion")
                implementation("org.mockito:mockito-core:$mockitoVersion")

                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))

                implementation(project(":demo-and-test-shared"))
            }
        }
    }
}

val jvmJarPlotImageExport by tasks.named<Jar>("jvmJar") {
    archiveFileName.set("$artifactBaseName-${artifactVersion}.jar")

    // Add LICENSE file to the META-INF folder inside published JAR files.
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

// Generating JavaDoc task for each publication task.
// Fixes "Task ':plot-image-export:publishJsPublicationToMavenRepository' uses this output of task
// ':plot-image-export:signJvmPublication' without declaring an explicit or implicit dependency" error.
// Issues:
//  - https://github.com/gradle-nexus/publish-plugin/issues/208
//  - https://github.com/gradle/gradle/issues/26091
//
val jvmPlotImageExportJarJavaDoc by tasks.registering (Jar::class) {
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
                    batikArtifacts.forEach {
                        val dep = deps.appendNode("dependency")
                        dep.appendNode("groupId", batikGroupId)
                        dep.appendNode("artifactId", it)
                        dep.appendNode("version", batikVersion)
                    }

                    var dep = deps.appendNode("dependency")
                    dep.appendNode("groupId", tiffioGroupId)
                    dep.appendNode("artifactId", tiffioArtifact)
                    dep.appendNode("version", tiffioVersion)

                    dep = deps.appendNode("dependency")
                    dep.appendNode("groupId", project.group)
                    dep.appendNode("artifactId", "platf-awt")
                    dep.appendNode("version", project.version)

                }
            }
        }
    }
    repositories {
        mavenLocal {
            url = uri("$mavenLocalPath")
        }
    }
}
signing {
    if (!project.version.toString().contains("SNAPSHOT")) {
        sign(publishing.publications["letsPlotImageExport"])
    }
}
