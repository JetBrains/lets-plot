package jetbrains.datalore.visualization.plot.base.scale.transform

import jetbrains.datalore.visualization.plot.base.Transform
import jetbrains.datalore.visualization.plot.base.scale.BreaksGenerator

object Transforms {
    val IDENTITY: Transform = IdentityTransform()
    val LOG10: Transform = Log10Transform()
    val REVERSE: Transform = ReverseTransform()
    val SQRT: Transform = SqrtTransform()

    fun identityWithBreaksGen(breaksGenerator: BreaksGenerator): Transform {
        return IdentityTransform(breaksGenerator)
    }
}
