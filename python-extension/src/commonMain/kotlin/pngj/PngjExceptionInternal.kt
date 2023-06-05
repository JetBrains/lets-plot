/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 * 
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 * */

package org.jetbrains.letsPlot.util.pngj

/**
 * Exception for anomalous internal problems (sort of asserts) that point to
 * some issue with the library
 *
 * @author Hernan J Gonzalez
 */
internal class PngjExceptionInternal(message: String?) : Exception(message)