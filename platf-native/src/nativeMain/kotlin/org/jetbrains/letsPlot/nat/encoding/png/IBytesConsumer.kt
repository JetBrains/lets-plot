/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 *
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 *
 * THE FOLLOWING IS THE COPYRIGHT OF THE ORIGINAL DOCUMENT:
 *
 * Copyright (c) 2009-2012, Hernán J. González.
 * Licensed under the Apache License, Version 2.0.
 *
 * The original PNGJ library is written in Java and can be found here: [PNGJ](https://github.com/leonbloy/pngj).
 */

package org.jetbrains.letsPlot.nat.encoding.png

/**
 * Bytes consumer.
 *
 * An object implementing can be fed with bytes.
 *
 * It can consume in steps, so each time it's fed with n bytes it can eat
 * between 1 and n bytes.
 */
internal interface IBytesConsumer {
    /**
     * Eats some bytes, at most len (perhaps less).
     *
     *
     * Returns bytes actually consumed.
     *
     * It returns -1 if the object didn't consume bytes because it was done or
     * closed
     *
     * It should only returns 0 if len is 0
     */
    fun consume(buf: ByteArray, offset: Int, len: Int): Int

    /**
     * The consumer is DONE when it does not need more bytes,
     * either because it ended normally, or abnormally
     * Typically this implies it will return -1 if consume() is called afterwards,
     * but it might happen that it will consume more (unneeded) bytes anwyway
     */
    val isDone: Boolean
}