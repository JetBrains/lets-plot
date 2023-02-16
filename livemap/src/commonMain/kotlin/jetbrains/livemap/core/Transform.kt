/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core

interface UnsafeTransform<InT, OutT> {
    fun apply(v: InT): OutT?
    fun invert(v: OutT): InT?
}

interface Transform<InT, OutT> {
    fun apply(v: InT): OutT
    fun invert(v: OutT): InT
}
