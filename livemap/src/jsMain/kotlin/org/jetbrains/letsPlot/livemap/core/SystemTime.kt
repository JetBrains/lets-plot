/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core

import kotlin.js.Date

actual open class SystemTime actual constructor() {

    actual open fun getTimeMs(): Long {
        return Date.now().toLong()
    }
}