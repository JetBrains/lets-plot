/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.projectionGeometry

class MultiPolygon<TypeT>(polygons: List<Polygon<TypeT>>) : AbstractGeometryList<Polygon<TypeT>>(polygons)