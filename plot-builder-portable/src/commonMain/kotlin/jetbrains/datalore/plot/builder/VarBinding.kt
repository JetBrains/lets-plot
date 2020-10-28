/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame

class VarBinding(
    val variable: DataFrame.Variable,
    val aes: Aes<*>
) {
    override fun toString() = "VarBinding{variable=${variable}, aes=${aes}"
}
