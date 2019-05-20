package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.visualization.plot.base.scale.Transform
import jetbrains.datalore.visualization.plot.base.scale.transform.Transforms

internal class ScaleTransformConfig private constructor(val transform: Transform, opts: Map<String, Any>) : OptionsAccessor(opts, emptyMap<Any, Any>()) {
    companion object {
        fun create(trans: Any): ScaleTransformConfig {
            // trans - name (identity,log10 ...)
            //        or
            //        map with transform options
            if (trans is Map<*, *>) {
                val opts = trans as Map<String, Any>
                return createForName(ConfigUtil.featureName(opts), opts)
            }
            return createForName(trans.toString(), HashMap())
        }

        private fun createForName(name: String, opts: Map<String, Any>): ScaleTransformConfig {
            val transform: Transform
            when (name) {
                "identity" -> transform = Transforms.IDENTITY
                "log10" -> transform = Transforms.LOG10
                "reverse" -> transform = Transforms.REVERSE
                "sqrt" -> transform = Transforms.SQRT
                else -> throw IllegalArgumentException("Can't create transform '$name'")
            }

            return ScaleTransformConfig(transform, opts)
        }
    }
}
