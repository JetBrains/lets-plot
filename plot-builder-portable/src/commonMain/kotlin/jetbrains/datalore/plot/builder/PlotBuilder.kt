/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.plot.builder.layout.LegendBoxInfo
import jetbrains.datalore.plot.builder.layout.PlotLayout
import jetbrains.datalore.plot.builder.theme.Theme

class PlotBuilder(
    private val myTheme: Theme
) {
    private val myLayersByTile = ArrayList<List<GeomLayer>>()
    private var myTitle: String? = null
    private lateinit var frameOfReferenceProvider: TileFrameOfReferenceProvider
    private lateinit var plotLayout: PlotLayout

    private val myLegendBoxInfos = ArrayList<LegendBoxInfo>()

    private var myAxisEnabled = true
    private var myInteractionsEnabled = true
    private var hasLiveMap = false

    fun setTitle(title: String?) {
        myTitle = title
    }

    fun tileFrameOfReferenceProvider(v: TileFrameOfReferenceProvider): PlotBuilder {
        frameOfReferenceProvider = v
        return this
    }

    fun addTileLayers(tileLayers: List<GeomLayer>): PlotBuilder {
        myLayersByTile.add(ArrayList(tileLayers))
        return this
    }

    fun plotLayout(v: PlotLayout): PlotBuilder {
        plotLayout = v
        return this
    }

    fun addLegendBoxInfo(v: LegendBoxInfo): PlotBuilder {
        myLegendBoxInfos.add(v)
        return this
    }

    fun axisEnabled(b: Boolean): PlotBuilder {
        myAxisEnabled = b
        return this
    }

    fun interactionsEnabled(b: Boolean): PlotBuilder {
        myInteractionsEnabled = b
        return this
    }

    fun setLiveMap(b: Boolean): PlotBuilder {
        hasLiveMap = b
        return this
    }

    fun build(): Plot {
        return MyPlot(this)
    }

    private class MyPlot(b: PlotBuilder) : Plot(b.myTheme) {

        private val myTitle: String? = b.myTitle

        private val myAxisXTitleEnabled: Boolean = b.myTheme.axisX().showTitle()
        private val myAxisYTitleEnabled: Boolean = b.myTheme.axisY().showTitle()

        override val frameOfReferenceProvider: TileFrameOfReferenceProvider = b.frameOfReferenceProvider

        private val myLayersByTile: List<List<GeomLayer>>
        private val myLayout: PlotLayout?
        private val myLegendBoxInfos: List<LegendBoxInfo>
        private val hasLiveMap: Boolean

        override val isAxisEnabled: Boolean
        override val isInteractionsEnabled: Boolean

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

        override val legendBoxInfos: List<LegendBoxInfo>
            get() = myLegendBoxInfos

        init {
            myLayersByTile = ArrayList(b.myLayersByTile)
            myLayout = b.plotLayout
            myLegendBoxInfos = ArrayList(b.myLegendBoxInfos)

            hasLiveMap = b.hasLiveMap

            isAxisEnabled = b.myAxisEnabled
            isInteractionsEnabled = b.myInteractionsEnabled
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

        override fun hasLiveMap(): Boolean {
            return hasLiveMap
        }

        override fun tileLayers(tileIndex: Int): List<GeomLayer> {
            return myLayersByTile[tileIndex]
        }

        override fun plotLayout(): PlotLayout {
            return myLayout!!
        }
    }
}
