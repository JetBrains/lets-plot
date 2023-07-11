/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("unused")
package jetbrains.datalore.plot.pythonExtension.pngj.pixels

import jetbrains.datalore.plot.pythonExtension.pngj.*

/**
 * Encodes a set of rows (pixels) as a continuous deflated stream (does not know
 * about IDAT chunk segmentation).
 *
 *
 * This includes the filter selection strategy, plus the filtering itself and
 * the deflating. Only supports fixed length rows (no interlaced writing).
 *
 *
 * Typically an instance of this is hold by a PngWriter - but more instances
 * could be used (for APGN)
 */
internal abstract class PixelsWriter(
    protected val imgInfo: ImageInfo
) {

    /**
     * row buffer length, including filter byte (imgInfo.bytesPerRow + 1)
     */
    protected val buflen: Int
    private val bytesPixel: Int
    private val bytesRow: Int
    // to compress the idat stream
    private var compressorStream: CompressorStream? = null

    /**
     * Deflater (ZLIB) compression level, between 0 (no compression) and 9
     */
    var deflaterCompLevel = 6
    private var deflaterStrategy: Int = DEFLATER_DEFAULT_STRATEGY
    protected var initdone = false

    /**
     * This is the globally configured filter type - it can be a concrete type
     * or a pseudo type (hint or strategy)
     */
    var filterType: FilterType

    // counts the filters used - just for stats
    private val filtersUsed = IntArray(5)

    // this is the raw underlying os (shared with the PngWriter)
    private lateinit var os: OutputPngStream
    private var idatMaxSize = IDAT_MAX_SIZE_DEFAULT

    /**
     * row being processed, couting from zero
     */
    protected var currentRow: Int

    init {
        bytesRow = imgInfo.bytesPerRow
        buflen = bytesRow + 1
        bytesPixel = imgInfo.bytesPixel
        currentRow = -1
        filterType = FilterType.FILTER_DEFAULT
    }

    /**
     * main internal point for external call. It does the lazy initializion if
     * necessary, sets current row, and call [.filterAndWrite]
     */
    fun processRow(rowb: ByteArray) {
        if (!initdone) init()
        currentRow++
        filterAndWrite(rowb)
    }

    protected fun sendToCompressedStream(rowf: ByteArray) {
        compressorStream!!.write(rowf, 0, rowf.size)
        filtersUsed[rowf[0].toInt()]++
    }

    /**
     * This does the filtering and send to stream. Typically should decide the
     * filtering, call
     * [.filterRowWithFilterType] and
     * and [.sendToCompressedStream]
     *
     * @param rowb
     */
    protected abstract fun filterAndWrite(rowb: ByteArray)

    /**
     * Does the real filtering. This must be called with the real (standard)
     * filterType. This should rarely be overriden.
     *
     *
     * WARNING: look out the contract
     *
     * @param _filterType
     * @param _rowb
     * current row (the first byte might be modified)
     * @param _rowbprev
     * previous row (should be all zero the first time)
     * @param _rowf
     * tentative buffer to store the filtered bytes. might not be
     * used!
     * @return normally _rowf, but eventually _rowb. This MUST NOT BE MODIFIED
     * nor reused by caller
     */
    protected fun filterRowWithFilterType(
        _filterType: FilterType, _rowb: ByteArray, _rowbprev: ByteArray,
        _rowf: ByteArray
    ): ByteArray {
        // warning: some filters rely on: "previous row" (rowbprev) it must be initialized to 0 the first time
        @Suppress("NAME_SHADOWING")
        var _rowf = _rowf
        if (_filterType === FilterType.FILTER_NONE) _rowf = _rowb
        _rowf[0] = _filterType.value.toByte()
        var i: Int
        var j: Int
        when (_filterType) {
            FilterType.FILTER_NONE -> {}
            FilterType.FILTER_PAETH -> {
                i = 1
                while (i <= bytesPixel) {
                    _rowf[i] =
                        PngHelperInternal.filterRowPaeth(_rowb[i].toInt(), 0, _rowbprev[i].toInt() and 0xFF, 0).toByte()
                    i++
                }
                j = 1
                i = bytesPixel + 1
                while (i <= bytesRow) {
                    _rowf[i] = PngHelperInternal.filterRowPaeth(
                        _rowb[i].toInt(), _rowb[j].toInt() and 0xFF, _rowbprev[i].toInt() and 0xFF,
                        _rowbprev[j].toInt() and 0xFF
                    ).toByte()
                    i++
                    j++
                }
            }

            FilterType.FILTER_SUB -> {
                i = 1
                while (i <= bytesPixel) {
                    _rowf[i] = _rowb[i]
                    i++
                }
                j = 1
                i = bytesPixel + 1
                while (i <= bytesRow) {
                    _rowf[i] = (_rowb[i] - _rowb[j]).toByte()
                    i++
                    j++
                }
            }

            FilterType.FILTER_AVERAGE -> {
                i = 1
                while (i <= bytesPixel) {
                    _rowf[i] = (_rowb[i] - (_rowbprev[i].toInt() and 0xFF) / 2).toByte()
                    i++
                }
                j = 1
                i = bytesPixel + 1
                while (i <= bytesRow) {
                    _rowf[i] = (_rowb[i] - ((_rowbprev[i].toInt() and 0xFF) + (_rowb[j].toInt() and 0xFF)) / 2).toByte()
                    i++
                    j++
                }
            }

            FilterType.FILTER_UP -> {
                i = 1
                while (i <= bytesRow) {
                    _rowf[i] = (_rowb[i] - _rowbprev[i]).toByte()
                    i++
                }
            }

            else -> throw PngjOutputException("Filter type not recognized: $_filterType")
        }
        return _rowf
    }

    /**
     * This will be called by the PngWrite to fill the raw pixels for each row.
     * This can change from call to call. Warning: this can be called before the
     * object is init, implementations should call init() to be sure
     */
    abstract fun getCurentRowb(): ByteArray

    /**
     * This will be called lazily just before writing row 0. Idempotent.
     */
    protected fun init() {
        if (!initdone) {
            initParams()
            initdone = true
        }
    }

    /**
     * called by init(); override (calling this first) to do additional
     * initialization
     */
    protected open fun initParams() {
        val idatWriter = IdatChunkWriter(os, idatMaxSize)
        if (compressorStream == null) { // if not set, use the deflater
            compressorStream = CompressorStreamDeflater(
                idatWriter, buflen, imgInfo.totalRawBytes,
                deflaterCompLevel, deflaterStrategy
            )
        }
    }

    /** cleanup. This should be called explicitly. Idempotent and secure  */
    open fun close() {
        if (compressorStream != null) {
            compressorStream!!.close()
        }
    }

    /**
     * Deflater (ZLIB) strategy. You should rarely change this from the default
     * (Deflater.DEFAULT_STRATEGY) to Deflater.FILTERED (Deflater.HUFFMAN_ONLY
     * is fast but compress poorly)
     */
    fun setDeflaterStrategy(deflaterStrategy: Int) {
        this.deflaterStrategy = deflaterStrategy
    }

    fun setOs(datStream: OutputPngStream) {
        os = datStream
    }

    fun getOs(): OutputPngStream {
        return os
    }

    /* out/in This should be called only after end() to get reliable results */
    val compression: Double
        get() = if (compressorStream!!.isDone) compressorStream!!.compressionRatio else 1.0

    fun setCompressorStream(compressorStream: CompressorStream?) {
        this.compressorStream = compressorStream
    }

    val totalBytesToWrite: Long
        get() = imgInfo.totalRawBytes

    val isDone: Boolean
        get() = currentRow == imgInfo.rows - 1

    /**
     * computed default fixed filter type to use, if specified DEFAULT; wilde
     * guess based on image properties
     *
     * @return One of the five concrete filter types
     */
    protected val defaultFilter: FilterType
        get() = if (imgInfo.indexed || imgInfo.bitDepth < 8) FilterType.FILTER_NONE else if (imgInfo.totalPixels < 1024) FilterType.FILTER_NONE else if (imgInfo.rows == 1) FilterType.FILTER_SUB else if (imgInfo.cols == 1) FilterType.FILTER_UP else FilterType.FILTER_PAETH

    /** informational stats : filter used, in percentages  */
    fun getFiltersUsed(): String {
        return (filtersUsed[0] * 100.0 / imgInfo.rows + 0.5).toString() + "," +
            (filtersUsed[1] * 100.0 / imgInfo.rows + 0.5).toString() + "," +
            (filtersUsed[2] * 100.0 / imgInfo.rows + 0.5).toString() + "," +
            (filtersUsed[3] * 100.0 / imgInfo.rows + 0.5).toString() + "," +
            (filtersUsed[4] * 100.0 / imgInfo.rows + 0.5).toString()
    }

    fun setIdatMaxSize(idatMaxSize: Int) {
        this.idatMaxSize = idatMaxSize
    }

    companion object {
        private const val IDAT_MAX_SIZE_DEFAULT = 32000
    }
}