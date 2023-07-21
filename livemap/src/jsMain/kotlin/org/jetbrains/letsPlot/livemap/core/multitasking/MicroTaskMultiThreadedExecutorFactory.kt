/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.multitasking

import org.jetbrains.letsPlot.livemap.core.multitasking.MicroTaskExecutor

actual class MicroTaskMultiThreadedExecutorFactory {

    actual companion object {
        actual fun create(): MicroTaskExecutor? = null
    }
}