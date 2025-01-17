/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import java.io.BufferedReader
import java.io.InputStreamReader

fun executeExternalCommand(command: String): String {
    val processBuilder = ProcessBuilder()
    processBuilder.command("sh", "-c", command)

    return try {
        val process = processBuilder.start()
        val output = StringBuilder()

        val inputStream = process.inputStream
        val reader = BufferedReader(InputStreamReader(inputStream))
        try {
            var line = reader.readLine()
            while (!line.isNullOrBlank()) {
                output.appendLine(line)
                line = reader.readLine()
            }
        } finally {
            reader.close()
        }

        val exitCode = process.waitFor()
        if (exitCode == 0) {
            output.toString().trimEnd()
        } else {
            "Error: Command exited with code $exitCode"
        }
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}
