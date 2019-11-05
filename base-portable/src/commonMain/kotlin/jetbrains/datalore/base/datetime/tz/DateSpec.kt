/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.datetime.tz

import jetbrains.datalore.base.datetime.Date

internal interface DateSpec {
    val rRule: String
    fun getDate(year: Int): Date
}
