/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.DevParams
import jetbrains.livemap.LayerProvider
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.LayerManager
import jetbrains.livemap.mapobjects.MapLayer
import jetbrains.livemap.obj2entity.TextMeasurer
import jetbrains.livemap.projections.MapProjection

class DemoLayerProvider(
    private val myDevParams: DevParams,
    private val myBlock: LayersBuilder.() -> Unit
) : LayerProvider {

    override fun provide(
        componentManager: EcsComponentManager,
        layerManager: LayerManager,
        mapProjection: MapProjection,
        context2d: Context2d
    ) {
        LayersBuilder(
            componentManager,
            layerManager,
            mapProjection,
            myDevParams,
            TextMeasurer(context2d)
        ).apply(myBlock)
    }

    override val layers: List<MapLayer> = emptyList()
}