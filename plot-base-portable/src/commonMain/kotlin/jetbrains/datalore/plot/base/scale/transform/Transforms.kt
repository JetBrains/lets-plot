/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.scale.BreaksGenerator
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.base.scale.ScaleBreaks

object Transforms {
    val IDENTITY: ContinuousTransform = IdentityTransform()
    val LOG10: ContinuousTransform = Log10Transform()
    val REVERSE: ContinuousTransform = ReverseTransform()
    val SQRT: ContinuousTransform = SqrtTransform()

    fun createBreaksGeneratorForTransformedDomain(
        transform: Transform,
        labelFormatter: ((Any) -> String)? = null
    ): BreaksGenerator {
        val breaksGenerator: BreaksGenerator = when (transform) {
            is IdentityTransform -> LinearBreaksGen(labelFormatter)
            is ReverseTransform -> LinearBreaksGen(labelFormatter)
            is SqrtTransform -> LinearBreaksGen(labelFormatter)
            is Log10Transform -> Log10BreaksGen(labelFormatter)
            else -> throw IllegalStateException("Unexpected 'transform' type: ${transform::class.simpleName}")
        }

        return BreaksGeneratorForTransformedDomain(transform, breaksGenerator)
    }

    class BreaksGeneratorForTransformedDomain(
        private val transform: Transform,
        val breaksGenerator: BreaksGenerator
    ) : BreaksGenerator {
        override fun labelFormatter(domain: ClosedRange<Double>, targetCount: Int): (Any) -> String {
            val domainBeforeTransform = MapperUtil.map(domain) {
                transform.applyInverse(it) as Double // Should not contain NULLs
            }
            return breaksGenerator.labelFormatter(domainBeforeTransform, targetCount)
        }

        override fun generateBreaks(domain: ClosedRange<Double>, targetCount: Int): ScaleBreaks {
            val domainBeforeTransform = MapperUtil.map(domain) {
                transform.applyInverse(it) as Double // Should not contain NULLs
            }
            val scaleBreaks = breaksGenerator.generateBreaks(domainBeforeTransform, targetCount)
            val originalBreaks = scaleBreaks.domainValues
            val transformedBreaks = transform.apply(originalBreaks).map {
                it as Double // Should not contain NULLs
            }

            return ScaleBreaks(originalBreaks, transformedBreaks, scaleBreaks.labels)
        }
    }
}
