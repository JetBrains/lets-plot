/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import jetbrains.datalore.plot.builder.scale.MapperProviderAdapter

abstract class MapperProviderBase<T>(protected val naValue: T) : MapperProviderAdapter<T>()
