/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.spec.Option.Meta

class MappingAnnotationOptions : Options() {
    var aes: Aes<*>? by map(Meta.MappingAnnotation.AES)
    var asDiscrete: Boolean? by map(Meta.MappingAnnotation.AS_DISCRETE)
}