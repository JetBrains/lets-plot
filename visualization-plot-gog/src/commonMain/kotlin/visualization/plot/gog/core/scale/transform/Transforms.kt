package jetbrains.datalore.visualization.plot.gog.core.scale.transform

import jetbrains.datalore.visualization.plot.gog.core.scale.BreaksGenerator
import jetbrains.datalore.visualization.plot.gog.core.scale.Transform

object Transforms {
    val IDENTITY: Transform = IdentityTransform()
    val LOG10: Transform = Log10Transform()
    val REVERSE: Transform = ReverseTransform()
    val SQRT: Transform = SqrtTransform()

    fun identityWithBreaksGen(breaksGenerator: BreaksGenerator): Transform {
        return IdentityTransform(breaksGenerator)
    }
}
