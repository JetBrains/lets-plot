/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.pythonExtension.pngj

/**
 * Image Line factory.
 */
interface IImageLineFactory<T : IImageLine> {
    fun createImageLine(iminfo: ImageInfo): T
}