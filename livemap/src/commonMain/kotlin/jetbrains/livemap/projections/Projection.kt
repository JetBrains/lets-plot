/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.projections

interface Projection<InT, OutT> {
    fun project(v: InT): OutT
    fun invert(v: OutT): InT
}
