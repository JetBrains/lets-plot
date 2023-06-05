/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 * 
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 * */

@file:Suppress("unused")
package org.jetbrains.letsPlot.util.pngj

internal enum class ErrorBehaviour( // we try hard to read, even garbage, without throwing exceptions
    val c: Int
) {
    STRICT(0),  // default mode: any error aborts reading with exception
    LENIENT1_CRC(1),  // CRC errors only trigger warning (or nothing if not checking)
    LENIENT2_ANCILLARY(3),  // also: content errors in ancillary chunks are ignored
    SUPER_LENIENT(5);
}