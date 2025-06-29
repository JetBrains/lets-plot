/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.pythonExtension.interop

import kotlinx.cinterop.*
import platform.posix.getcwd
import platform.posix.uname
import platform.posix.utsname

fun getOSName(): String {
    memScoped {
        val utsname = alloc<utsname>()
        uname(utsname.ptr)
        return utsname.sysname.toKString()
    }
}

fun getCurrentDir(): String {
    return memScoped {
        val bufferSize = 4096 * 8
        val buffer = allocArray<ByteVar>(bufferSize)
        if (getcwd(buffer, bufferSize.convert()) != null) {
            buffer.toKString()
        } else {
            "." // Default to current directory on error
        }
    }
}
