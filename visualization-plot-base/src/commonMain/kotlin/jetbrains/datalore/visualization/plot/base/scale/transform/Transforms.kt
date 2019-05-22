package jetbrains.datalore.visualization.plot.base.scale.transform

import jetbrains.datalore.visualization.plot.base.scale.BreaksGenerator
import jetbrains.datalore.visualization.plot.base.scale.Transform

object Transforms {
    val IDENTITY: Transform = IdentityTransform()
    val LOG10: Transform = Log10Transform()
    val REVERSE: Transform = ReverseTransform()
    val SQRT: Transform = SqrtTransform()

    fun identityWithBreaksGen(breaksGenerator: BreaksGenerator): Transform {
        return IdentityTransform(breaksGenerator)
    }
}
