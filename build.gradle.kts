/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

// okhttp3 added for publishing to the Sonatype Central Repository:
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.gradle.internal.os.OperatingSystem
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.io.FileNotFoundException
import java.util.*

buildscript {
    dependencies {
        classpath("com.squareup.okhttp3:okhttp:4.12.0")
    }
}

plugins {
    kotlin("multiplatform") apply false
    kotlin("js") apply false
    id("org.openjfx.javafxplugin") version "0.1.0" apply false
}


fun ExtraPropertiesExtension.getOrNull(name: String): Any? = if (has(name)) {
    get(name)
} else {
    null
}


val os: OperatingSystem = OperatingSystem.current()
val letsPlotTaskGroup by extra { "lets-plot" }

allprojects {
    group = "org.jetbrains.lets-plot"
    version = "4.6.3-SNAPSHOT" // see also: python-package/lets_plot/_version.py
//    version = "0.0.0-SNAPSHOT"  // for local publishing only

    // Generate JVM 1.8 bytecode
    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
    }

    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

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
            "$localPropsFileName file not found!\n" +
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
            val execResult = providers.exec {
                commandLine(
                    "${pythonBinPath}/python",
                    "-c",
                    "import platform; print(platform.machine())"
                )
            }
            val getArchOutput = execResult.standardOutput.asText.get()

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
// define the Maven Repository URL. Currently set to a local path for uploading
// artifacts to the Sonatype Central Repository.
val mavenReleasePublishUrl by extra { layout.buildDirectory.dir("maven/artifacts").get().toString() }
// define Maven Snapshot repository URL.
val mavenSnapshotPublishUrl by extra { "https://central.sonatype.com/repository/maven-snapshots/" }

// define Sonatype Central Repository settings:
val sonatypeUsername by extra { extra.getOrNull("sonatype.username") ?: "" }
val sonatypePassword by extra { extra.getOrNull("sonatype.password") ?: "" }

// Publish some sub-projects as Kotlin Multi-project libraries.
val publishLetsPlotCoreModulesToMavenLocalRepository by tasks.registering {
    group = letsPlotTaskGroup
}

val publishLetsPlotCoreModulesToMavenRepository by tasks.registering {
    group = letsPlotTaskGroup
}

// Configure a workaround tasks for publishing to the Sonatype Central Repository,
// as there is currently no official Gradle plugin support.
// Refer to documentation: https://central.sonatype.org/publish/publish-portal-gradle/
val packageMavenArtifacts by tasks.registering(Zip::class) {
    group = letsPlotTaskGroup

    from(mavenReleasePublishUrl)
    archiveFileName.set("${project.name}-artifacts.zip")
    destinationDirectory.set(layout.buildDirectory)
}
val uploadMavenArtifacts by tasks.registering {
    group = letsPlotTaskGroup
    dependsOn(packageMavenArtifacts)

    doLast {
        val uriBase = "https://central.sonatype.com/api/v1/publisher/upload"
        val publishingType = "USER_MANAGED"
        val deploymentName = "${project.name}-$version"
        val uri = "$uriBase?name=$deploymentName&publishingType=$publishingType"

        val userName = sonatypeUsername as String
        val password = sonatypePassword as String
        val base64Auth = Base64.getEncoder().encode("$userName:$password".toByteArray()).toString(Charsets.UTF_8)
        val bundleFile = packageMavenArtifacts.get().archiveFile.get().asFile

        println("Sending request to $uri...")

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(uri)
            .header("Authorization", "Bearer $base64Auth")
            .post(
                MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("bundle", bundleFile.name, bundleFile.asRequestBody())
                    .build()
            )
            .build()

        client.newCall(request).execute().use { response ->
            val statusCode = response.code
            println("Upload status code: $statusCode")
            println("Upload result: ${response.body!!.string()}")
            if (statusCode != 201) {
                error("Upload error to Central repository. Status code $statusCode.")
            }
        }
    }
}


if ((extra.getOrNull("enable_magick_canvas") as? String ?: "true").toBoolean()) {
    val defaultImageMagickLibPath = rootDir.path + "/platf-imagick/deps"
    val imageMagickLibPath = project.findProperty("imagemagick_lib_path") as? String
        ?: System.getenv("IMAGICK_LIB_PATH")
        ?: defaultImageMagickLibPath

    extra.set("imagemagick_lib_path", imageMagickLibPath)
    val initImageMagick by tasks.registering {
        group = letsPlotTaskGroup
        doLast {
            exec {
                this.workingDir = File(rootDir.path + "/platf-imagick")
                commandLine(
                    "./init_imagemagick.sh",
                    imageMagickLibPath
                )
            }
        }
    }
}

// Generating JavaDoc task for each publication task.
// Fixes "Task ':canvas:publishJsPublicationToMavenRepository' uses this output of task ':canvas:signJvmPublication'
// without declaring an explicit or implicit dependency" error.
// Issues:
//  - https://github.com/gradle-nexus/publish-plugin/issues/208
//  - https://github.com/gradle/gradle/issues/26091
//
fun getJarJavaDocsTask(distributeName: String): TaskProvider<Jar> {
    return tasks.register<Jar>("${distributeName}JarJavaDoc") {
        archiveClassifier.set("javadoc")
        from("$rootDir/README.md")
        archiveBaseName.set(distributeName)
    }
}

// Configure native targets for python-extension dependencies.
subprojects {
    val pythonExtensionModules = listOf(
        "commons",
        "canvas",
        "datamodel",
        "plot-base",
        "plot-builder",
        "plot-stem",
        "plot-raster",

        "demo-and-test-shared",
        "demo-common-svg",
        "demo-svg-native",
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
}

// Configure Lets-Plot Core multiplatform modules.
val multiPlatformCoreModulesForPublish = listOf(
    "commons",
    "datamodel",
    "canvas",
    "gis",
    "livemap",
    "plot-base",
    "plot-builder",
    "plot-stem",
    "plot-livemap"
)

subprojects {
    if (name in multiPlatformCoreModulesForPublish) {
        apply(plugin = "org.jetbrains.kotlin.multiplatform")
        // For `jvmSourcesJar` task:
        configure<KotlinMultiplatformExtension> {
            jvm()
        }
    }
}

// Configure Lets-Plot Core JVM modules.
val jvmCoreModulesForPublish = listOf(
    "platf-awt",
    "platf-batik",
    "platf-jfx-swing"
)

subprojects {
    if (name in jvmCoreModulesForPublish) {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        apply(plugin = "maven-publish")

        configure<KotlinJvmProjectExtension> {
            tasks.register<Jar>("${name}-sources") {
                archiveClassifier.set("sources")
                from(sourceSets.getByName("main").kotlin.srcDirs)
            }
        }

        configure<PublishingExtension> {
            publications {
                register(name, MavenPublication::class) {
                    groupId = project.group as String
                    artifactId = name
                    version = project.version as String

                    artifact(tasks["jar"])
                    artifact(tasks["${name}-sources"])
                }
            }
        }
    }
}

// Configure Maven publication for Lets-Plot Core modules.
subprojects {
    if (name in multiPlatformCoreModulesForPublish + jvmCoreModulesForPublish) {
        apply(plugin = "maven-publish")
        apply(plugin = "signing")
        // Do not publish 'native' targets:
        val targetsToPublish = listOf(
            "platf-awt",
            "platf-batik",
            "platf-jfx-swing",
            "jvm",
            "js",
            "kotlinMultiplatform",
            "metadata"
        )

        configure<PublishingExtension> {
            publications {
                withType(MavenPublication::class) {
                    if (name in targetsToPublish) {
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
                maven {
                    if (version.toString().endsWith("-SNAPSHOT")) {
                        url = uri(mavenSnapshotPublishUrl)

                        credentials {
                            username = sonatypeUsername.toString()
                            password = sonatypePassword.toString()
                        }
                    } else {
                        url = uri(mavenReleasePublishUrl)
                    }
                }
            }
        }
        afterEvaluate {
            // Add LICENSE file to the META-INF folder inside published JAR files.
            tasks.filterIsInstance<Jar>()
                .forEach {
                    if (it.name == "jvmJar" || it.name == "jar") { // "jar" for 'org.jetbrains.kotlin.jvm' plugin
                        it.metaInf {
                            from("$rootDir") {
                                include("LICENSE")
                            }
                        }
                    }
                }

            // Configure artifacts signing process for release versions.
            val publicationsToSign = mutableListOf<Publication>()

            for (task in tasks.withType(PublishToMavenRepository::class)) {
                if (task.publication.name in targetsToPublish) {
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

// Fix warnings in all projects.
subprojects {
    fun KotlinCommonCompilerOptions.configCompilerWarnings() {
        freeCompilerArgs.addAll(
            // Suppress expect/actual classes are in Beta warning.
            "-Xexpect-actual-classes",

            // Non-public primary constructor is exposed via the generated 'copy()' method of the 'data' class.
            // Kotlin 2.0 feature.
            //"-Xconsistent-data-class-copy-visibility",

            // Enable all warnings as errors.
            // Disabled because even with the Suppress("unused") the warnings may still happen:
            // (https://github.com/JetBrains/lets-plot/blob/f5af69befdd2fa963672d3b1d9992f3635f64840/plot-base/src/commonMain/kotlin/org/jetbrains/letsPlot/core/interact/mouse/MouseDragInteraction.kt#L70)
            //"-Werror"
        )
    }
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<KotlinMultiplatformExtension> {
            targets.configureEach {
                compilations.configureEach {
                    compileTaskProvider.get().compilerOptions {
                        configCompilerWarnings()
                    }
                }
            }
        }
    }

    // Koltin 2.0
    //plugins.withId("org.jetbrains.kotlin.jvm") {
    //    extensions.configure<KotlinJvmExtension> {
    //        compilerOptions {
    //            configCompilerWarnings()
    //            jvmTarget.set(JvmTarget.JVM_1_8)
    //        }
    //    }
    //}

    plugins.withId("org.jetbrains.kotlin.jvm") {
        extensions.configure<KotlinJvmProjectExtension> {
            compilerOptions {
                configCompilerWarnings()
                jvmTarget.set(JvmTarget.JVM_1_8)
            }
        }
    }
}
