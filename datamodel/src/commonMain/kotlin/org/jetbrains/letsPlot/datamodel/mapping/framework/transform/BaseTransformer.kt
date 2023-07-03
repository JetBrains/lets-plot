/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework.transform

abstract class BaseTransformer<SourceT, TargetT> : Transformer<SourceT, TargetT> {
    override fun <ParameterTargetT> andThen(
            transformer: Transformer<TargetT, ParameterTargetT>
    ): Transformer<SourceT, ParameterTargetT> {

        val firstTransformer = this
        return object : BaseTransformer<SourceT, ParameterTargetT>() {
            override fun transform(from: SourceT): Transformation<SourceT, ParameterTargetT> {
                val tn1 = firstTransformer.transform(from)
                val tn2 = transformer.transform(tn1.target)
                return createTransformation(tn1, tn2)
            }

            override fun transform(from: SourceT, to: ParameterTargetT): Transformation<SourceT, ParameterTargetT> {
                val tn1 = firstTransformer.transform(from)
                val tn2 = transformer.transform(tn1.target, to)
                return createTransformation(tn1, tn2)
            }

            private fun createTransformation(
                tn1: Transformation<SourceT, TargetT>,
                tn2: Transformation<TargetT, ParameterTargetT>
            ): Transformation<SourceT, ParameterTargetT> {

                return object : Transformation<SourceT, ParameterTargetT>() {
                    override val source: SourceT
                        get() = tn1.source

                    override val target: ParameterTargetT
                        get() = tn2.target

                    override fun doDispose() {
                        tn1.dispose()
                        tn2.dispose()
                    }
                }
            }
        }
    }
}
