/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.unsupported

/*

Use UNSUPPORTED() instead of TODO() from Kotlin standard library.
TODO() throws an `Error` and we are generally don't try to catch errors.

 */

@Suppress("FunctionName")
fun UNSUPPORTED(): Nothing = throw UnsupportedOperationException()

@Suppress("FunctionName")
fun UNSUPPORTED(what: String): Nothing = throw UnsupportedOperationException(what)
