/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.gradle.internal.os.OperatingSystem
import java.io.*
import java.util.*

fun readLocalPropertiesFile(): Properties {
    val localPropsFileName = "local.properties"
    val properties = Properties()

    if (File(localPropsFileName).exists()) {
        properties.load(InputStreamReader(FileInputStream(localPropsFileName)))
    } else {
        throw FileNotFoundException(
            "$localPropsFileName file not found!\n" +
                    "Check ${localPropsFileName}_template file for the template."
        )
    }

    if (properties.getProperty("enable_python_package").toBoolean()) {
        val pythonBinPath = properties["python.bin_path"]
        val os: OperatingSystem = OperatingSystem.current()

        if (!os.isWindows) {
            val command = "${pythonBinPath}/python -c \"import platform; print(platform.machine())\""
            val currentPythonArch = executeExternalCommand(command)

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
    return properties
}
