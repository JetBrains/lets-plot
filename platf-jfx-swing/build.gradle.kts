import org.gradle.jvm.tasks.Jar

/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

val artifactBaseName = "platf-jfx-swing-jvm"
val artifactGroupId = project.group as String
val artifactVersion = project.version as String
val jfxVersion = project.extra["jfx_version"] as String
val jfxPlatform = project.extra["jfxPlatformResolved"] as String
val mavenLocalPath = rootProject.project.extra["localMavenRepository"]

dependencies {
    compileOnly(project("::platf-awt"))
    compileOnly(project(":commons"))
    compileOnly(project(":datamodel"))
    compileOnly(project(":canvas"))
    compileOnly(project(":plot-stem"))

    compileOnly("org.openjfx:javafx-base:$jfxVersion:$jfxPlatform")
    compileOnly("org.openjfx:javafx-graphics:$jfxVersion:$jfxPlatform")
    compileOnly("org.openjfx:javafx-swing:$jfxVersion:$jfxPlatform")

    compileOnly(project(":platf-awt"))

    testImplementation(project(":demo-and-test-shared"))
    testImplementation(project(":platf-awt"))
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("org.openjfx:javafx-base:$jfxVersion:$jfxPlatform")
    testImplementation("org.openjfx:javafx-graphics:$jfxVersion:$jfxPlatform")
    testImplementation("org.openjfx:javafx-swing:$jfxVersion:$jfxPlatform")
}

tasks {
    jar {
        from(rootProject.file("LICENSE")) {
            into("META-INF")
        }
    }
    val platfJfxSwingJvmSourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }
    val platfJfxSwingJvmJavadocJar by creating(Jar::class) {
        archiveClassifier.set("javadoc")
        from("$rootDir/README.md")
    }
}

publishing {
    publications {
        register("platfJfxSwingJvm", MavenPublication::class) {

            groupId = artifactGroupId
            artifactId = artifactBaseName
            version = artifactVersion

            artifact(tasks["jar"])
            artifact(tasks["platfJfxSwingJvmSourcesJar"])
            artifact(tasks["platfJfxSwingJvmJavadocJar"])

            pom {
                name = "Lets-Plot core artifact"
                description = "A part of the Lets-Plot library."

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
        sign(publishing.publications["platfJfxSwingJvm"])
    }
}
