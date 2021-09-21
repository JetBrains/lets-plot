/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.typedGeometry.Rect
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.Client
import jetbrains.livemap.ClientPoint

fun Context2d.scale(scale: DoubleVector) = scale(scale.x, scale.y)
fun Context2d.scale(scale: ClientPoint) = scale(scale.x, scale.y)

fun Context2d.moveTo(p: ClientPoint) = moveTo(p.x, p.y)
fun Context2d.lineTo(p: ClientPoint) = lineTo(p.x, p.y)
fun Context2d.translate(p: ClientPoint) = translate(p.x, p.y)
fun Context2d.fillRect(r: Rect<Client>) = fillRect(r.origin.x, r.origin.y, r.dimension.x, r.dimension.y)
fun Context2d.strokeRect(r: Rect<Client>) = strokeRect(r.origin.x, r.origin.y, r.dimension.x, r.dimension.y)