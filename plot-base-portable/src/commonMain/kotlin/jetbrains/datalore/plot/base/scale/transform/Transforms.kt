/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.scale.BreaksGenerator

object Transforms {
    val IDENTITY: Transform = createTransform(TransformKind.IDENTITY)
    val LOG10: Transform = createTransform(TransformKind.LOG10)
    val REVERSE: Transform = createTransform(TransformKind.REVERSE)
    val SQRT: Transform = createTransform(TransformKind.SQRT)

    fun identityWithBreaksGen(breaksGenerator: BreaksGenerator): Transform {
        return IdentityTransform(breaksGenerator)
    }

    fun createTransform(transKind: TransformKind, labelFormatter: ((Any) -> String)? = null): Transform {
        return when (transKind) {
            TransformKind.IDENTITY -> IdentityTransform(labelFormatter)
            TransformKind.LOG10 -> Log10Transform(labelFormatter)
            TransformKind.REVERSE -> ReverseTransform(labelFormatter)
            TransformKind.SQRT -> SqrtTransform(labelFormatter)
        }
    }
}
