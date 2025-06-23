/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.encoding

expect fun deflate(input: ByteArray): ByteArray
expect fun inflate(input: ByteArray, expectedSize: Int): ByteArray
