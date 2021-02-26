/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing


interface ApplicationContext {
    fun runWriteAction(action: Runnable)
    fun invokeLater(action: Runnable, expared: () -> Boolean)
}