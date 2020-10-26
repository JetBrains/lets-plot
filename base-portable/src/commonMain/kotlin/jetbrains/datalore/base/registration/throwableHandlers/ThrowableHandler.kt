/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.registration.throwableHandlers

class ThrowableHandler internal constructor() {

    fun handle(t: Throwable) {
        throw t
    }
}
