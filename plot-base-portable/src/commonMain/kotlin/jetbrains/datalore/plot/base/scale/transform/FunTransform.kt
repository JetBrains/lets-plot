/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.scale.BreaksGenerator
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.base.scale.ScaleBreaks

open class FunTransform(
        private val myFun: (Double?) -> Double?,
        private val myInverse: (Double?) -> Double?) :
        Transform, BreaksGenerator {


    private val myLinearBreaksGen = LinearBreaksGen()

    override fun labelFormatter(domainAfterTransform: ClosedRange<Double>, targetCount: Int): (Any) -> String {
        val domainBeforeTransform = MapperUtil.map(domainAfterTransform) { myInverse(it) }
        return myLinearBreaksGen.labelFormatter(domainBeforeTransform, targetCount)
    }

    override fun apply(rawData: List<*>): List<Double?> {
        return rawData.map { myFun(it as Double) }
    }

    override fun applyInverse(v: Double?): Any? {
        return myInverse(v)
    }


    override fun generateBreaks(domainAfterTransform: ClosedRange<Double>, targetCount: Int): ScaleBreaks {
        val domainBeforeTransform = MapperUtil.map(domainAfterTransform) { myInverse(it) }
        val originalBreaks = myLinearBreaksGen.generateBreaks(domainBeforeTransform, targetCount)
        val domainValues = originalBreaks.domainValues
        val transformValues = ArrayList<Double>()
        for (domainValue in domainValues) {
            val transformed = myFun(domainValue)
            transformValues.add(transformed!!)
        }

        return ScaleBreaks(domainValues, transformValues, originalBreaks.labels)
    }
}
