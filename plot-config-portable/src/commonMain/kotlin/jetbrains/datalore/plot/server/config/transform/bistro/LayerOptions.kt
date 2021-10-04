/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind


class LayerOptions(
    val geom: GeomKind? = null,
    val data: Map<String, List<Any?>>? = null,
    val mappings: Map<Aes<*>, String>? = null,
    val contants: Map<Aes<*>, Any?>? = null,
    val tooltipsOptions: TooltipsOptions? = null,
    val samplingOptions: SamplingOptions? = null,
    val stat: String? = null,
    val position: String? = null,
    val sizeUnit: String? = null,
    val showLegend: Boolean? = null,
    val naText: String? = null,
    val labelFormat: String? = null,
)
