/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.rendering

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.projection.ClientPoint

fun Context2d.scale(scale: DoubleVector) = scale(scale.x, scale.y)
fun Context2d.scale(scale: ClientPoint) = scale(scale.x, scale.y)

fun Context2d.moveTo(p: ClientPoint) = moveTo(p.x, p.y)
fun Context2d.lineTo(p: ClientPoint) = lineTo(p.x, p.y)
fun Context2d.translate(p: ClientPoint) = translate(p.x, p.y)
