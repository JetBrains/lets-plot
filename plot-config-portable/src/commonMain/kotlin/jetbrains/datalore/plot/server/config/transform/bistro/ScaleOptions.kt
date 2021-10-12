/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.config.Option


class ScaleOptions : Options<ScaleOptions>() {
    var name: String? by map(Option.Scale.NAME)
    var aes: Aes<*>? by map(Option.Scale.AES)
    var mapperKind: String? by map(Option.Scale.SCALE_MAPPER_KIND)
    var palette: String? by map(Option.Scale.PALETTE)
    var naValue: Any? by map(Option.Scale.NA_VALUE)
    var limits: List<Any>? by map(Option.Scale.LIMITS)
    var breaks: List<Any>? by map(Option.Scale.BREAKS)
    var labels: List<String>? by map(Option.Scale.LABELS)
    var expand: List<Any>? by map(Option.Scale.EXPAND)
    var low: String? by map(Option.Scale.LOW)
    var mid: String? by map(Option.Scale.MID)
    var high: String? by map(Option.Scale.HIGH)
    var midpoint: Double? by map(Option.Scale.MIDPOINT)
    var isDiscrete: Boolean? by map(Option.Scale.DISCRETE_DOMAIN)
    var isReverse: Boolean? by map(Option.Scale.DISCRETE_DOMAIN_REVERSE)
    var guide: String? by map(Option.Scale.GUIDE)
}
