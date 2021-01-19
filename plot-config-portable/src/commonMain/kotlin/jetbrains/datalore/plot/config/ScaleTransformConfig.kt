/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.stringFormat.StringFormat
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.scale.BreaksGenerator
import jetbrains.datalore.plot.base.scale.transform.*

internal class ScaleTransformConfig private constructor(
    val transform: Transform,
    opts: Map<String, Any>
) : OptionsAccessor(opts) {

    companion object {
        fun create(trans: Any, format: String?): ScaleTransformConfig {
            // trans - name (identity,log10 ...)
            //        or
            //        map with transform options
            if (trans is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                val opts = trans as Map<String, Any>
                return createForName(
                    ConfigUtil.featureName(opts),
                    opts,
                    format
                )
            }
            return createForName(
                trans.toString(),
                HashMap(),
                format
            )
        }

        private fun createForName(name: String, opts: Map<String, Any>, format: String?) : ScaleTransformConfig {
            val transform = Transforms.createTransform(transKind = TransformKind.safeValueOf(name))
            if (transform is BreaksGenerator) {
                val formatter = format?.let { { value: Any -> StringFormat(it).format(value) } }
                transform.setLabelFormatter(formatter)
            }
            return ScaleTransformConfig(transform, opts)
        }
    }
}
