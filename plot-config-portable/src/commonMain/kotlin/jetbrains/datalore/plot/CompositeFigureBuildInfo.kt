/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

// ToDo...
interface CompositeFigureBuildInfo : FigureBuildInfo {
    val buildInfos: List<FigureBuildInfo>

    override val computationMessages: List<String>
        get() {
            return buildInfos.flatMap { it.computationMessages }
        }
}