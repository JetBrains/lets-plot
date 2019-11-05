/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.transform.encode

import jetbrains.datalore.plot.config.transform.SpecChange
import jetbrains.datalore.plot.config.transform.SpecChangeContext

internal class ServerSideEncodeChange : SpecChange {
    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        jetbrains.datalore.plot.FeatureSwitch.printEncodedDataSummary("DataFrameOptionHelper.encodeUpdateOption", spec)

        @Suppress("ConstantConditionIf")
        if (jetbrains.datalore.plot.FeatureSwitch.USE_DATA_FRAME_ENCODING) {
            val encoded = DataFrameEncoding.encode1(spec)
            spec.clear()
            spec.putAll(encoded)
        }
    }
}
