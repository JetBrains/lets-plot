/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.data

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.builder.VarBinding

class StatInput(
    data: DataFrame,
    bindings: List<VarBinding>,
    transformByAes: Map<Aes<*>, Transform>,
    statCtx: StatContext,
    flipXY: Boolean
) {
    val data: DataFrame = if (flipXY) YOrientationUtil.flipDataFrame(data) else data
    val bindings: List<VarBinding> = if (flipXY) YOrientationUtil.flipVarBinding(bindings) else bindings
    val transformByAes: Map<Aes<*>, Transform> =
        if (flipXY) YOrientationUtil.flipAesKeys(transformByAes) else transformByAes
    val statCtx: StatContext = if (flipXY) statCtx.getFlipped() else statCtx
}