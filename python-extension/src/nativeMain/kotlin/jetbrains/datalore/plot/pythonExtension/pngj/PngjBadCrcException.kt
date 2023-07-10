/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.pythonExtension.pngj

/**
 * Exception thrown by bad CRC check
 */
internal class PngjBadCrcException(message: String) : PngjInputException(message)