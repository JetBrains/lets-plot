/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale.transform

import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.scale.BreaksGenerator

object Transforms {
    val IDENTITY: Transform =
        IdentityTransform()
    val LOG10: Transform = Log10Transform()
    val REVERSE: Transform =
        ReverseTransform()
    val SQRT: Transform = SqrtTransform()

    fun identityWithBreaksGen(breaksGenerator: BreaksGenerator): Transform {
        return IdentityTransform(breaksGenerator)
    }
}
