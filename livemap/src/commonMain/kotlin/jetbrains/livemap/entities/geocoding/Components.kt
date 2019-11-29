/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geocoding

import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.projections.WorldPoint

class CentroidTag : EcsComponent

class LineOrientationComponent(val horizontal: Boolean) : EcsComponent

class CentroidComponent(val point: WorldPoint): EcsComponent

class WaitingGeocodingComponent : EcsComponent

class RegionBBoxComponent(val bbox: GeoRectangle) : EcsComponent