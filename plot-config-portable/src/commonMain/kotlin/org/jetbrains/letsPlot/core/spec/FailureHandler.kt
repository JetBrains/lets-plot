/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec

import org.jetbrains.letsPlot.commons.intern.gcommon.base.Throwables

object FailureHandler {
    fun failureInfo(e: RuntimeException): FailureInfo {
        @Suppress("NAME_SHADOWING")
        val e = Throwables.getRootCause(e)
        return if (!e.message.isNullOrBlank() && (
//                    e is IllegalStateException ||
                    e is IllegalArgumentException)
        ) {
            // Not a bug - likely user configuration error like `No layers in plot`
            FailureInfo(e.message!!, false)
        } else {
            val className = e::class.simpleName ?: "<Anonymous exception>"
            FailureInfo(
                "Internal error: $className : ${e.message ?: "<no message>"}",
                true
            )
        }
    }

    class FailureInfo(val message: String, val isInternalError: Boolean)
}