/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.nat.encoding.png

/**
 * Exception for anomalous internal problems (sort of asserts) that point to
 * some issue with the library
 *
 * @author Hernan J Gonzalez
 */
internal class PngjExceptionInternal(message: String?) : Exception(message)