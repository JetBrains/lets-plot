/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.transform.encode

import jetbrains.datalore.plot.config.transform.SpecChange
import jetbrains.datalore.plot.config.transform.SpecChangeContext

internal class ClientSideDecodeChange : SpecChange {

    override fun isApplicable(spec: Map<String, Any>): Boolean {
        return DataFrameEncoding.isEncodedDataSpec(spec)
    }

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val decoded: Map<String, *>
        decoded = DataFrameEncoding.decode1(spec)
        spec.clear()
        spec.putAll(decoded)
    }
}
