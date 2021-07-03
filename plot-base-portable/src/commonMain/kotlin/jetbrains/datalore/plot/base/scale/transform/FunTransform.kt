/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.plot.base.Transform

open class FunTransform(
    private val myFun: (Double?) -> Double?,
    private val myInverse: (Double?) -> Double?
) : Transform {

    override fun apply(rawData: List<*>): List<Double?> {
        return rawData.map { myFun(it as? Double) }
    }

    override fun applyInverse(v: Double?): Any? {
        return myInverse(v)
    }
}
