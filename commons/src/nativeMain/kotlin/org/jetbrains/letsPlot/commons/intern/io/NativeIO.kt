/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.io

import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray

object NativeIO {
    fun getCurrentDir(): String {
        val relativeCurrentDir = Path(".")
        val absoluteCurrentDir = SystemFileSystem.resolve(relativeCurrentDir)
        return absoluteCurrentDir.toString()
    }

    fun writeToFile(path: String, data: ByteArray) {
        val filePath = Path(path)
        val sink = SystemFileSystem.sink(filePath).buffered()
        sink.use { it.write(data) }
    }

    fun readFromFile(path: String): ByteArray {
        val filePath = Path(path)
        val source = SystemFileSystem.source(filePath).buffered()
        return source.use(Source::readByteArray)
    }

    fun dirExists(path: String): Boolean {
        return SystemFileSystem.exists(Path(path))
    }

    fun mkdirs(path: String) {
        SystemFileSystem.createDirectories(Path(path))
    }
}