/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.registration

interface Disposable {
    /**
     * Disposes this item. You shouldn't call this method more than once. It's recommended to throw
     * an exception in case it's called for the second time.
     */
    fun dispose()
}