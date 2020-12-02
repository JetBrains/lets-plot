/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.scale.transform.Transforms

internal class ScaleTransformConfig private constructor(
    val transform: Transform,
    opts: Map<String, Any>
) : OptionsAccessor(opts) {

    companion object {
        fun create(trans: Any): ScaleTransformConfig {
            // trans - name (identity,log10 ...)
            //        or
            //        map with transform options
            if (trans is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                val opts = trans as Map<String, Any>
                return createForName(
                    ConfigUtil.featureName(opts),
                    opts
                )
            }
            return createForName(
                trans.toString(),
                HashMap()
            )
        }

        private fun createForName(name: String, opts: Map<String, Any>): ScaleTransformConfig {
            val transform = when (name) {
                "identity" -> Transforms.IDENTITY
                "log10" -> Transforms.LOG10
                "reverse" -> Transforms.REVERSE
                "sqrt" -> Transforms.SQRT
                else -> throw IllegalArgumentException("Can't create transform '$name'")
            }
            return ScaleTransformConfig(transform, opts)
        }
    }
}
