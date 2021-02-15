/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

interface WithGuideBreaks<DomainT> {
    val breaks: List<DomainT>
    val formatter: (DomainT) -> String
}
