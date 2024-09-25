/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.core.spec.Option

class DataMetaOptions : Options() {
    var seriesAnnotation: List<SeriesAnnotationOptions>? by map(Option.Meta.SeriesAnnotation.TAG)
    var mappingAnnotation: List<MappingAnnotationOptions>? by map(Option.Meta.MappingAnnotation.TAG)
}