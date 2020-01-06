/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.multitasking

actual class MicroTaskMultiThreadedExecutorFactory {

    actual companion object {
        actual fun create(): MicroTaskExecutor? = null
    }
}