/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */
package org.jetbrains.letsPlot.core.plot.base.stat.math3

import kotlin.math.min

class BlockRealMatrix {
    /** Block size.  */
    private val BLOCK_SIZE = 52
    /** Number of rows of the matrix.  */
    private var rows: Int = 0
    /** Number of columns of the matrix.  */
    private var columns: Int = 0
    /** Number of block rows of the matrix.  */
    private var blockRows: Int = 0
    /** Number of block columns of the matrix.  */
    private var blockColumns: Int = 0
    /** Blocks of matrix entries.  */
    private lateinit var blocks: Array<DoubleArray>
    /**
     * Create a new matrix with the supplied row and column dimensions.
     *
     * @param rows  the number of rows in the new matrix
     * @param columns  the number of columns in the new matrix
     * @throws org.apache.commons.math3.exception.NotStrictlyPositiveException
     * if row or column dimension is not positive.
     */
    constructor(rows: Int, columns: Int) {
        this.rows = rows
        this.columns = columns

        // number of blocks
        blockRows = (rows + BLOCK_SIZE - 1) / BLOCK_SIZE
        blockColumns = (columns + BLOCK_SIZE - 1) / BLOCK_SIZE

        // allocate storage blocks, taking care of smaller ones at right and bottom
        blocks = createBlocksLayout(rows, columns)
    }

    /**
     * Create a new dense matrix copying entries from raw layout data.
     *
     * The input array *must* already be in raw layout.
     *
     * Calling this constructor is equivalent to call:
     * <pre>matrix = new BlockRealMatrix(rawData.length, rawData[0].length,
     * toBlocksLayout(rawData), false);</pre>
     *
     *
     * @param rawData data for new matrix, in raw layout
     * @throws DimensionMismatchException if the shape of `blockData` is
     * inconsistent with block layout.
     * @see .BlockRealMatrix
     */
    constructor(rawData: Array<DoubleArray>) {

        create(rawData.size, rawData[0].size, toBlocksLayout(rawData), false)
    }

    /**
     * Create a new dense matrix copying entries from block layout data.
     *
     * The input array *must* already be in blocks layout.
     *
     * @param rows Number of rows in the new matrix.
     * @param columns Number of columns in the new matrix.
     * @param blockData data for new matrix
     * @param copyArray Whether the input array will be copied or referenced.
     * @throws DimensionMismatchException if the shape of `blockData` is
     * inconsistent with block layout.
     * @see .createBlocksLayout
     * @see .toBlocksLayout
     * @see .BlockRealMatrix
     */
    fun create(
        rows: Int, columns: Int,
        blockData: Array<DoubleArray>, copyArray: Boolean
    ) {
        this.rows = rows
        this.columns = columns

        // number of blocks
        blockRows = (rows + BLOCK_SIZE - 1) / BLOCK_SIZE
        blockColumns = (columns + BLOCK_SIZE - 1) / BLOCK_SIZE

        val blocksCopyList = ArrayList<DoubleArray>()
        if (copyArray) {
            // allocate storage blocks, taking care of smaller ones at right and bottom
        } else {
            // reference existing array
            blocks = blockData
        }

        var index = 0
        for (iBlock in 0 until blockRows) {
            val iHeight = blockHeight(iBlock)
            var jBlock = 0
            while (jBlock < blockColumns) {
                if (blockData[index].size != iHeight * blockWidth(jBlock)) {
                    error("")
                    //throw DimensionMismatchException(
                    //    blockData[index].size,
                    //    iHeight * blockWidth(jBlock)
                    //)
                }
                if (copyArray) {
                    blocksCopyList.add(blockData[index].copyOf())
                    //blocks[index] = blockData[index].copyOf()
                }
                ++jBlock
                ++index
            }
        }

        if (copyArray) {
            blocks = blocksCopyList.toTypedArray()
        }
    }

    /**
     * Create a data array in blocks layout.
     *
     *
     * This method can be used to create the array argument of the [ ][.BlockRealMatrix] constructor.
     *
     * @param rows Number of rows in the new matrix.
     * @param columns Number of columns in the new matrix.
     * @return a new data array in blocks layout.
     * @see .toBlocksLayout
     * @see .BlockRealMatrix
     */
    private fun createBlocksLayout(rows: Int, columns: Int): Array<DoubleArray> {
        val blockRows = (rows + BLOCK_SIZE - 1) / BLOCK_SIZE
        val blockColumns = (columns + BLOCK_SIZE - 1) / BLOCK_SIZE

        val blocksList = ArrayList<DoubleArray>()
        var blockIndex = 0
        for (iBlock in 0 until blockRows) {
            val pStart = iBlock * BLOCK_SIZE
            val pEnd = min(pStart + BLOCK_SIZE, rows)
            val iHeight = pEnd - pStart
            for (jBlock in 0 until blockColumns) {
                val qStart = jBlock * BLOCK_SIZE
                val qEnd = min(qStart + BLOCK_SIZE, columns)
                val jWidth = qEnd - qStart
                blocksList.add(DoubleArray(iHeight * jWidth))
                ++blockIndex
            }
        }

        return blocksList.toTypedArray()
    }

    fun transpose(): BlockRealMatrix {
        val nRows = getRowDimension()
        val nCols = getColumnDimension()
        val out = BlockRealMatrix(nCols, nRows)

        // perform transpose block-wise, to ensure good cache behavior
        var blockIndex = 0
        for (iBlock in 0 until blockColumns) {
            for (jBlock in 0 until blockRows) {
                // transpose current block
                val outBlock = out.blocks[blockIndex]
                val tBlock = blocks[jBlock * blockColumns + iBlock]
                val pStart = iBlock * BLOCK_SIZE
                val pEnd = min(pStart + BLOCK_SIZE, columns)
                val qStart = jBlock * BLOCK_SIZE
                val qEnd = min(qStart + BLOCK_SIZE, rows)
                var k = 0
                for (p in pStart until pEnd) {
                    val lInc = pEnd - pStart
                    var l = p - pStart
                    for (q in qStart until qEnd) {
                        outBlock[k] = tBlock[l]
                        ++k
                        l += lInc
                    }
                }
                // go to next block
                ++blockIndex
            }
        }

        return out
    }

    /**
     * Returns the result of postmultiplying this by `m`.
     *
     * @param m Matrix to postmultiply by.
     * @return `this` * m.
     * @throws MatrixDimensionMismatchException if the matrices are not
     * compatible.
     */
    fun multiply(m: BlockRealMatrix): BlockRealMatrix {
        // safety check
        if (this.getColumnDimension() != m.getRowDimension()) {
            error("Matrix multiply dimension mismatch: ${this.getColumnDimension()} x ${m.getRowDimension()}")
        }

        val out = BlockRealMatrix(rows, m.columns)

        // perform multiplication block-wise, to ensure good cache behavior
        var blockIndex = 0
        for (iBlock in 0 until out.blockRows) {

            val pStart = iBlock * BLOCK_SIZE
            val pEnd = min(pStart + BLOCK_SIZE, rows)

            for (jBlock in 0 until out.blockColumns) {
                val jWidth = out.blockWidth(jBlock)
                val jWidth2 = jWidth + jWidth
                val jWidth3 = jWidth2 + jWidth
                val jWidth4 = jWidth3 + jWidth

                // select current block
                val outBlock = out.blocks[blockIndex]

                // perform multiplication on current block
                for (kBlock in 0 until blockColumns) {
                    val kWidth = blockWidth(kBlock)
                    val tBlock = blocks[iBlock * blockColumns + kBlock]
                    val mBlock = m.blocks[kBlock * m.blockColumns + jBlock]
                    var k = 0
                    for (p in pStart until pEnd) {
                        val lStart = (p - pStart) * kWidth
                        val lEnd = lStart + kWidth
                        for (nStart in 0 until jWidth) {
                            var sum = 0.0
                            var l = lStart
                            var n = nStart
                            while (l < lEnd - 3) {
                                sum += tBlock[l] * mBlock[n] +
                                        tBlock[l + 1] * mBlock[n + jWidth] +
                                        tBlock[l + 2] * mBlock[n + jWidth2] +
                                        tBlock[l + 3] * mBlock[n + jWidth3]
                                l += 4
                                n += jWidth4
                            }
                            while (l < lEnd) {
                                sum += tBlock[l++] * mBlock[n]
                                n += jWidth
                            }
                            outBlock[k] += sum
                            ++k
                        }
                    }
                }
                // go to next block
                ++blockIndex
            }
        }

        return out
    }


    /** {@inheritDoc}  */
    fun getEntry(row: Int, column: Int): Double {
        //MatrixUtils.checkMatrixIndex(this, row, column)
        if (row < 0 || row > getRowDimension()) error("row out of range: $row")
        if (column < 0 || column > getColumnDimension()) error("column out of range: $column")

        val iBlock = row / BLOCK_SIZE
        val jBlock = column / BLOCK_SIZE
        val k = (row - iBlock * BLOCK_SIZE) * blockWidth(jBlock) + (column - jBlock * BLOCK_SIZE)
        return blocks[iBlock * blockColumns + jBlock][k]
    }

    private fun getRowDimension(): Int {
        return rows
    }

    /** {@inheritDoc}  */
    private fun getColumnDimension(): Int {
        return columns
    }

    /**
     * Get the width of a block.
     * @param blockColumn column index (in block sense) of the block
     * @return width (number of columns) of the block
     */
    private fun blockWidth(blockColumn: Int): Int {
        return if (blockColumn == blockColumns - 1) columns - blockColumn * BLOCK_SIZE else BLOCK_SIZE
    }

    /**
     * Get the height of a block.
     * @param blockRow row index (in block sense) of the block
     * @return height (number of rows) of the block
     */
    private fun blockHeight(blockRow: Int): Int {
        return if (blockRow == blockRows - 1) rows - blockRow * BLOCK_SIZE else BLOCK_SIZE
    }

    /**
     * Convert a data array from raw layout to blocks layout.
     *
     *
     * Raw layout is the straightforward layout where element at row i and
     * column j is in array element `rawData[i][j]`. Blocks layout
     * is the layout used in [BlockRealMatrix] instances, where the matrix
     * is split in square blocks (except at right and bottom side where blocks may
     * be rectangular to fit matrix size) and each block is stored in a flattened
     * one-dimensional array.
     *
     *
     *
     * This method creates an array in blocks layout from an input array in raw layout.
     * It can be used to provide the array argument of the [ ][.BlockRealMatrix] constructor.
     *
     * @param rawData Data array in raw layout.
     * @return a new data array containing the same entries but in blocks layout.
     * @throws DimensionMismatchException if `rawData` is not rectangular.
     * @see .createBlocksLayout
     * @see .BlockRealMatrix
     */
    fun toBlocksLayout(rawData: Array<DoubleArray>): Array<DoubleArray> {
        val rows = rawData.size
        val columns = rawData[0].size
        val blockRows = (rows + BLOCK_SIZE - 1) / BLOCK_SIZE
        val blockColumns = (columns + BLOCK_SIZE - 1) / BLOCK_SIZE

        // safety checks
        for (i in rawData.indices) {
            val length = rawData[i].size
            if (length != columns) {
                error("Wrong dimension: $columns, $length")
            }
        }

        // convert array
        val blocksList = ArrayList<DoubleArray>()
        var blockIndex = 0
        for (iBlock in 0 until blockRows) {
            val pStart = iBlock * BLOCK_SIZE
            val pEnd = min(pStart + BLOCK_SIZE, rows)
            val iHeight = pEnd - pStart
            for (jBlock in 0 until blockColumns) {
                val qStart = jBlock * BLOCK_SIZE
                val qEnd = min(qStart + BLOCK_SIZE, columns)
                val jWidth = qEnd - qStart

                // allocate new block
                val block = DoubleArray(iHeight * jWidth)
                blocksList.add(block)

                // copy data
                var index = 0
                for (p in pStart until pEnd) {
                    rawData[p].copyInto(block, index, qStart, qEnd)
                    //System.arraycopy(rawData[p], qStart, block, index, jWidth)
                    index += jWidth
                }
                ++blockIndex
            }
        }

        return blocksList.toTypedArray()
    }
}