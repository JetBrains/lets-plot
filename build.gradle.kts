/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import java.util.*
import org.gradle.internal.os.OperatingSystem
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

plugins {
    kotlin("multiplatform") apply false
    kotlin("js") apply false
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}


fun ExtraPropertiesExtension.getOrNull(name: String): Any? = if (has(name)) { get(name) } else { null }


val os: OperatingSystem = OperatingSystem.current()
val letsPlotTaskGroup by extra { "lets-plot" }

allprojects {
    group = "org.jetbrains.lets-plot"
    version = "4.4.0-rc1" // see also: python-package/lets_plot/_version.py

    // Generate JVM 1.8 bytecode
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    fun getJfxPlatform(): String {
        if (os.isWindows()) {
            return "win"
        } else if (os.isLinux()) {
            return "linux"
        } else if (os.isMacOsX()) {
            return "mac"
        } else {
            return "unknown"
        }
    }

    val jfxPlatformResolved by extra { getJfxPlatform() }

    repositories {
        mavenCentral()
    }
}


// Read build settings from commandline parameters (for build_release.py script):
fun readPropertiesFromParameters() {
    val properties = Properties()
    if (project.hasProperty("enable_python_package")) {
        properties["enable_python_package"] = project.property("enable_python_package")
    }
    if (properties.getProperty("enable_python_package").toBoolean()) {
        properties["python.bin_path"] = project.property("python.bin_path")
        properties["python.include_path"] = project.property("python.include_path")
    }
    if (!os.isWindows) {
        properties["architecture"] = project.property("architecture")
    }
    for (property in properties) {
        extra[property.key as String] = property.value
    }
}

// Read build settings from local.properties:
fun readPropertiesFromFile() {
    val properties = Properties()
    val localPropsFileName = "local.properties"

    if (project.file(localPropsFileName).exists()) {
        properties.load(project.file(localPropsFileName).inputStream())
    } else {
        throw FileNotFoundException(
            "${localPropsFileName} file not found!\n" +
                    "Check ${localPropsFileName}_template file for the template."
        )
    }

    if (!os.isWindows) {
        // Only 64bit version can be built for Windows, so the arch parameter is not needed and may not be set.
        assert(properties["architecture"] != null)
    }

    if (properties.getProperty("enable_python_package").toBoolean()) {
        val pythonBinPath = properties["python.bin_path"]
        val pythonIncludePath = properties["python.include_path"]

        assert(pythonBinPath != null)
        assert(pythonIncludePath != null)

        if (!os.isWindows) {
            val getArchOutput = ByteArrayOutputStream()
            exec {
                commandLine(
                    "${pythonBinPath}/python",
                    "-c",
                    "import platform; print(platform.machine())"
                )
                standardOutput = getArchOutput
            }

            val currentPythonArch = getArchOutput.toString().trim()
            if (currentPythonArch != properties["architecture"]) {
                throw IllegalArgumentException(
                    "Project and Python architectures don't match!\n" +
                            " - Value, from your '${localPropsFileName}' file: ${properties["architecture"]}\n" +
                            " - Your Python architecture: ${currentPythonArch}\n" +
                            "Check your '${localPropsFileName}' file."
                )
            }
        }
    }
    for (property in properties) {
        extra[property.key as String] = property.value
    }
}

// For build_release.py settings will be read from commandline parameters.
// In other cases, settings will be read from local.properties.
if (project.hasProperty("build_release")) {
    readPropertiesFromParameters()
} else {
    readPropertiesFromFile()
}


// Maven publication settings:
// define local Maven Repository path:
val localMavenRepository by extra { "$rootDir/.maven-publish-dev-repo" }
// define Sonatype nexus repository manager settings:
val sonatypeUsername = extra.getOrNull("sonatype.username")?: ""
val sonatypePassword = extra.getOrNull("sonatype.password")?: ""
val sonatypeProfileID = extra.getOrNull("sonatype.profileID")?: ""

nexusPublishing {
    repositories {
        register("maven") {
            username.set(sonatypeUsername as String)
            password.set(sonatypePassword as String)
            stagingProfileId.set(sonatypeProfileID as String)
            nexusUrl.set(uri("https://oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

// Publish some sub-projects as Kotlin Multi-project libraries.
val publishLetsPlotCoreModulesToMavenLocalRepository by tasks.registering {
    group=letsPlotTaskGroup
}

val publishLetsPlotCoreModulesToMavenRepository by tasks.registering {
    group=letsPlotTaskGroup
}

// Generating JavaDoc task for each publication task.
// Fixes "Task ':canvas:publishJsPublicationToMavenRepository' uses this output of task ':canvas:signJvmPublication'
// without declaring an explicit or implicit dependency" error.
// Issues:
//  - https://github.com/gradle-nexus/publish-plugin/issues/208
//  - https://github.com/gradle/gradle/issues/26091
//
fun getJarJavaDocsTask(distributeName:String): TaskProvider<Jar> {
    return tasks.register<Jar>("${distributeName}JarJavaDoc") {
        archiveClassifier.set("javadoc")
        from("$rootDir/README.md")
        archiveBaseName.set(distributeName)
    }
}


subprojects {
    val pythonExtensionModules = listOf(
        "commons",
        "datamodel",
        "plot-base",
        "plot-builder",
        "plot-stem",
        "platf-native",
        "demo-and-test-shared"
    )
    val projectArchitecture = rootProject.extra.getOrNull("architecture")

    if (name in pythonExtensionModules) {
        apply(plugin = "org.jetbrains.kotlin.multiplatform")

        configure<KotlinMultiplatformExtension> {
            if (os.isMacOsX && projectArchitecture == "x86_64") {
                macosX64()
            } else if (os.isMacOsX && projectArchitecture == "arm64") {
                if (project.hasProperty("build_release")) {
                    macosX64()
                    macosArm64()
                } else {
                    macosArm64()
                }
            } else if (os.isLinux) {
                if (project.hasProperty("build_release")) {
                    linuxX64()
                    linuxArm64()
                } else if (projectArchitecture == "x86_64") {
                    linuxX64()
                }
            } else if (os.isWindows) {
                mingwX64()
            } else {
                throw Exception("Unsupported platform! Check project settings.")
            }
        }
    }

    val coreModulesForPublish = listOf(
        "commons",
        "datamodel",
        "canvas",
        "gis",
        "livemap",
        "plot-base",
        "plot-builder",
        "plot-stem",
        "plot-livemap",
        "platf-awt",
        "platf-batik",
        "platf-jfx-swing",
        "deprecated-in-v4"
    )

    if (name in coreModulesForPublish) {
        apply(plugin = "org.jetbrains.kotlin.multiplatform")
        apply(plugin = "maven-publish")
        apply(plugin = "signing")

        // For `jvmSourcesJar` task:
        configure<KotlinMultiplatformExtension> {
            jvm()
        }

        // Do not publish 'native' targets:
        val publicationsToPublish = listOf("jvm", "js", "kotlinMultiplatform", "metadata")

        configure<PublishingExtension> {
            publications {
                withType(MavenPublication::class) {
                    if (name in publicationsToPublish) {
                        // Configure this publication.
                        artifact(getJarJavaDocsTask("${name}-${project.name}"))

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
            }
            repositories {
                mavenLocal {
                    url = uri(localMavenRepository)
                }
            }
        }

        afterEvaluate {
            // Add LICENSE file to the META-INF folder inside published JAR files.
            tasks.named<Jar>("jvmJar") {
                metaInf {
                    from("$rootDir") {
                        include("LICENSE")
                    }
                }
            }

            // Configure artifacts signing process for release versions.
            val publicationsToSign = mutableListOf<Publication>()

            for (task in tasks.withType(PublishToMavenRepository::class)) {
                if (task.publication.name in publicationsToPublish) {
                    val repoName = task.repository.name

                    if (repoName == "MavenLocal") {
                        publishLetsPlotCoreModulesToMavenLocalRepository.configure {
                            dependsOn += task
                        }
                    } else if (repoName == "maven") {
                        publishLetsPlotCoreModulesToMavenRepository.configure {
                            dependsOn += task
                        }
                            publicationsToSign.add(task.publication)
                    } else {
                        throw IllegalStateException("Repository expected: 'MavenLocal' or 'maven' but was: '$repoName'.")
                    }
                }
            }
            // Sign artifacts.
            publicationsToSign.forEach {
                if (!project.version.toString().contains("SNAPSHOT")) {
                    configure<SigningExtension> {
                        sign(it)
                    }
                }
            }
        }
    }
}
