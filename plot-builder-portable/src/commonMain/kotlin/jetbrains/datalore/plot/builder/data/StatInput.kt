/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.data

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.Transform
import org.jetbrains.letsPlot.core.plot.base.util.YOrientationBaseUtil
import jetbrains.datalore.plot.builder.VarBinding

class StatInput(
    data: DataFrame,
    bindings: List<VarBinding>,
    transformByAes: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Transform>,
    statCtx: StatContext,
    flipXY: Boolean
) {
    val data: DataFrame = if (flipXY) YOrientationUtil.flipDataFrame(data) else data
    val bindings: List<VarBinding> = if (flipXY) YOrientationUtil.flipVarBinding(bindings) else bindings
    val transformByAes: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Transform> =
        if (flipXY) YOrientationBaseUtil.flipAesKeys(transformByAes) else transformByAes
    val statCtx: StatContext = if (flipXY) statCtx.getFlipped() else statCtx
}