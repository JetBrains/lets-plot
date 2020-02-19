/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.point.symbol

import jetbrains.datalore.plot.base.render.point.UpdatableShape
import jetbrains.datalore.vis.svg.slim.SvgSlimObject

interface Glyph : UpdatableShape, SvgSlimObject
