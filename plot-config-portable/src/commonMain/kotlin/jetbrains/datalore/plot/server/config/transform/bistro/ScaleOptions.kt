/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro

import jetbrains.datalore.plot.base.Aes


class ScaleOptions(
    val name: String? = null,
    val aes: Aes<*>? = null,
    val mapperKind: String? = null,
    val palette: String? = null,
    val naValue: Any? = null,
    val limits: List<Any>? = null,
    val breaks: List<Any>? = null,
    val labels: List<String>? = null,
    val expand: List<Any>? = null,
    val low: String? = null,
    val mid: String? = null,
    val high: String? = null,
    val midpoint: Double? = null,
    val isDiscrete: Boolean? = null,
    val isReverse: Boolean? = null,
    val guide: String? = null
) {
}
