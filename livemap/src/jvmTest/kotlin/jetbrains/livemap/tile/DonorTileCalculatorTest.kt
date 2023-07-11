/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.tile

import org.jetbrains.letsPlot.commons.intern.spatial.projectRect
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.Client
import jetbrains.livemap.mapengine.basemap.BasemapCellRenderer
import jetbrains.livemap.mapengine.basemap.DonorTileCalculator
import jetbrains.livemap.mapengine.basemap.Tile
import jetbrains.livemap.mapengine.viewport.CellKey
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.verify

class DonorTileCalculatorTest {
    private val myCellRect = Rect.XYWH<Client>(
        explicitVec(0.0, 0.0),
        explicitVec(256.0, 256.0)
    )
    private val myContext2d = Mockito.mock(Context2d::class.java)
    private val mySnapshotMap = HashMap<String, Tile>()
    private var myDrawImageCount: Int = 0

    @Before
    fun setUp() {
        mySnapshotMap.clear()
        myDrawImageCount = 0
    }

    @After
    fun cleanUp() {
        //verifyDrawImageCount()
    }

    @Test
    fun renderUpTile() {
        val calculator = CalculatorBuilder()
            .addTiles("0", "1", "1221", "12")
            .build()

        render(calculator, "122103")
        verifyDrawImage("1221", "03", "")
    }

    @Test
    fun renderDownTile() {
        val calculator = CalculatorBuilder()
            .addTiles("01", "30", "3112", "311", "3113", "312")
            .build()

        render(calculator, "3")
        verifyDrawImage("30", "", "0")
        verifyDrawImage("311", "", "11")
        verifyDrawImage("312", "", "12")
    }

    @Test
    fun renderUpDownTile() {
        val calculator = CalculatorBuilder()
            .addTiles("1", "12", "13", "1231213", "123121")
            .build()

        render(calculator, "1231")
        verifyDrawImage("12", "31", "")
        verifyDrawImage("123121", "", "21")
    }

    @Test
    fun renderUpSubTile() {
        val calculator = CalculatorBuilder()
            .addTiles("01230")
            .calculateTile("012301")
            .build()

        render(calculator, "0123012")
        verifyDrawImage("01230", "12", "")
    }

    @Test
    fun renderDownSubTile() {
        val calculator = CalculatorBuilder()
            .addTiles("01230")
            .calculateTile("0123")
            .build()

        render(calculator, "012")
        verifyDrawImage("01230", "", "30")
    }

    @Test
    fun renderUpDownSubTile() {
        val calculator = CalculatorBuilder()
            .addTiles("01230")
            .calculateTile("0123012")
            .build()

        render(calculator, "012301")
        verifyDrawImage("01230", "1", "")
        verifyDrawImage("01230", "12", "2")
    }

    private fun render(calculator: DonorTileCalculator, tileKey: String) {
        val tile = calculator.createDonorTile(CellKey(tileKey))
        val renderer = BasemapCellRenderer()
        renderer.render(tile, myCellRect, myContext2d)
    }

    private fun verifyDrawImage(snapshotKey: String, srcSubKey: String, dstSubKey: String) {
        val tile = mySnapshotMap[snapshotKey] as Tile.SnapshotTile
        val srcRect = CellKey(srcSubKey).projectRect(myCellRect)
        val dstRect = CellKey(dstSubKey).projectRect(myCellRect)
        verify(myContext2d).drawImage(
            tile.snapshot,
            srcRect.left,
            srcRect.top,
            srcRect.width,
            srcRect.height,
            dstRect.left,
            dstRect.top,
            dstRect.width + 1.0,
            dstRect.height + 1.0
        )
        ++myDrawImageCount
    }

//    private fun verifyDrawImageCount() {
//        verify(myContext2d, times(myDrawImageCount)).drawImage(
//            any(),
//            anyDouble(),
//            anyDouble(),
//            anyDouble(),
//            anyDouble(),
//            anyDouble(),
//            anyDouble(),
//            anyDouble(),
//            anyDouble()
//        )
//    }

    private inner class CalculatorBuilder {
        internal fun addTiles(vararg keys: String): CalculatorBuilder {
            for (key in keys) {
                val snapshot = Mockito.mock(Canvas.Snapshot::class.java)
                mySnapshotMap[key] = Tile.SnapshotTile(snapshot)
            }
            return this
        }

        internal fun calculateTile(vararg keys: String): CalculatorBuilder {
            val calculator = build()
            for (key in keys) {
                mySnapshotMap[key] = calculator.createDonorTile(CellKey(key))
            }
            return this
        }

        internal fun build(): DonorTileCalculator {
            val tileMap = HashMap<CellKey, Tile>()
            mySnapshotMap.forEach { (key, tile) -> tileMap[CellKey(key)] = tile }
            return DonorTileCalculator(tileMap)
        }
    }
}