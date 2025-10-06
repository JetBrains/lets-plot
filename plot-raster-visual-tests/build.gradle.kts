import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("multiplatform")
}

val kotlinLoggingVersion = extra["kotlinLogging.version"] as String
val assertjVersion = extra["assertj.version"] as String

val enablePythonPackage: Boolean = (rootProject.project.extra["enable_python_package"] as String).toBoolean()
val os: OperatingSystem = OperatingSystem.current()
val arch = rootProject.project.extra["architecture"]

// To improve the building time of the Python extension in development mode.
val pythonExtensionDebugBuild = project.findProperty("python_extension_debug_build") == "true"

kotlin {
    jvm()
    if (enablePythonPackage) {
        val target = when {
            os.isMacOsX && arch == "arm64" -> macosArm64()
            os.isMacOsX && arch == "x86_64" -> macosX64()
            os.isLinux && arch == "arm64" -> linuxArm64()
            os.isLinux && arch == "x86_64" -> linuxX64()
            os.isWindows -> mingwX64()
            else -> throw Exception("Unsupported platform! Check project settings.")
        }

        val imageMagickLibPath = rootProject.project.extra["imagemagick_lib_path"].toString()
        target.binaries.forEach {
            it.linkerOpts += listOf(
                "-L${imageMagickLibPath}/lib",
                "-lMagickWand-7.Q16HDRI",
                "-lMagickCore-7.Q16HDRI",
                "-lfontconfig",
                "-lfreetype",
                "-lexpat",
                "-lz"
            )
            if (os.isWindows) {
                it.linkerOpts += listOf(
                    "-lurlmon",
                    "-lgdi32"
                )
            }
        }
    }


    sourceSets {
        commonMain {
            dependencies {
                compileOnly(project(":commons"))
                compileOnly(project(":canvas"))
                compileOnly(project(":datamodel"))
                compileOnly(project(":plot-base"))
                compileOnly(project(":plot-stem"))
                compileOnly(project(":plot-builder"))
                implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
                implementation(project(":demo-and-test-shared"))
                implementation("org.assertj:assertj-core:${assertjVersion}")
                implementation(kotlin("test"))
            }
        }

        named("commonTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

                implementation(project(":canvas"))
                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
                implementation(project(":plot-stem"))
                implementation(project(":plot-builder"))
            }
        }

        named("jvmTest") {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

                implementation(project(":canvas"))
                implementation(project(":commons"))
                implementation(project(":datamodel"))
                implementation(project(":plot-base"))
                implementation(project(":plot-stem"))
                implementation(project(":plot-builder"))
                implementation(project(":plot-image-export"))
            }
        }

        val nativeTest by creating {
            dependencies {
                implementation(project(":demo-and-test-shared"))
                implementation(project(":demo-common-svg"))
                implementation(project(":python-extension"))
            }
        }

//        named("nativeTest") {
//            dependencies {
//                implementation(kotlin("test-junit"))
//                implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
//
//                implementation(project(":canvas"))
//                implementation(project(":commons"))
//                implementation(project(":datamodel"))
//                implementation(project(":plot-base"))
//                implementation(project(":plot-stem"))
//                implementation(project(":plot-builder"))
//                implementation(project(":python-extension"))
//            }
//        }
    }
}
