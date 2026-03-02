/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.logging

interface Logger {
    fun error(e: Throwable, message: () -> String)
    fun info(message: () -> String)
}

val Any.identityString: String get() = "${this::class.simpleName}@${hashCode().toUInt().toString(16)}"