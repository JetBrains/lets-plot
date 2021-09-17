/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.plot.builder.layout.LegendBoxInfo
import jetbrains.datalore.plot.builder.layout.PlotLayout
import jetbrains.datalore.plot.builder.theme.Theme

class PlotBuilder(
    private val theme: Theme
) {
    private var title: String? = null
    private val layersByTile = ArrayList<List<GeomLayer>>()
    private lateinit var frameOfReferenceProvider: TileFrameOfReferenceProvider
    private lateinit var plotLayout: PlotLayout

    private val legendBoxInfos = ArrayList<LegendBoxInfo>()
    private var interactionsEnabled = true

    fun title(v: String?): PlotBuilder {
        title = v
        return this
    }

    fun tileFrameOfReferenceProvider(v: TileFrameOfReferenceProvider): PlotBuilder {
        frameOfReferenceProvider = v
        return this
    }

    fun addTileLayers(tileLayers: List<GeomLayer>): PlotBuilder {
        layersByTile.add(ArrayList(tileLayers))
        return this
    }

    fun plotLayout(v: PlotLayout): PlotBuilder {
        plotLayout = v
        return this
    }

    fun addLegendBoxInfo(v: LegendBoxInfo): PlotBuilder {
        legendBoxInfos.add(v)
        return this
    }

    fun interactionsEnabled(b: Boolean): PlotBuilder {
        interactionsEnabled = b
        return this
    }


    fun build(): Plot {
        return PlotImpl(this)
    }

    private class PlotImpl(b: PlotBuilder) : Plot(b.theme) {

        private val myTitle: String? = b.title
        private val myLayersByTile: List<List<GeomLayer>> = ArrayList(b.layersByTile)
        private val plotLayout: PlotLayout = b.plotLayout

        override val frameOfReferenceProvider: TileFrameOfReferenceProvider = b.frameOfReferenceProvider

        override val interactionsEnabled: Boolean = b.interactionsEnabled

        override val legendBoxInfos: List<LegendBoxInfo> = ArrayList(b.legendBoxInfos)

        override val containsLiveMap: Boolean = myLayersByTile.flatten().any(GeomLayer::isLiveMap)

        override val title: String
            get() {
                require(hasTitle()) { "No title" }
                return myTitle!!
            }

        override val axisTitleLeft: String
            get() {
                require(hasAxisTitleLeft()) { "No left axis title" }
                return frameOfReferenceProvider.vAxisLabel!!
            }

        override val axisTitleBottom: String
            get() {
                require(hasAxisTitleBottom()) { "No bottom axis title" }
                return frameOfReferenceProvider.hAxisLabel!!
            }

        override fun hasTitle(): Boolean {
            return !myTitle.isNullOrEmpty()
        }

        override fun hasAxisTitleLeft(): Boolean {
            return !frameOfReferenceProvider.vAxisLabel.isNullOrEmpty()
        }

        override fun hasAxisTitleBottom(): Boolean {
            return !frameOfReferenceProvider.hAxisLabel.isNullOrEmpty()
        }

        override fun tileLayers(tileIndex: Int): List<GeomLayer> {
            return myLayersByTile[tileIndex]
        }

        override fun plotLayout(): PlotLayout {
            return plotLayout
        }
    }
}
