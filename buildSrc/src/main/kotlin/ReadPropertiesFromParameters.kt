/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.gradle.api.Project
import org.gradle.internal.os.OperatingSystem
import java.util.*

fun readPropertiesFromParameters(project: Project): Properties {
    val properties = Properties()
    val os: OperatingSystem = OperatingSystem.current()
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
    return properties
}
