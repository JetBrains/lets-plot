/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("unused", "UNUSED_PARAMETER", "KDocUnresolvedReference")
package org.jetbrains.letsPlot.nat.encoding.png

/**
 * Some utility static methods for internal use.
 *
 *
 * Client code should not normally use this class
 *
 *
 */
internal object PngHelperInternal {
    private const val isDebug = false
    //private val LOGGER: Logger = PrintlnLogger(PngHelperInternal::class.simpleName ?: "PngHelperInternal")

    /**
     * Default charset, used internally by PNG for several things
     */
    //var charsetLatin1: java.nio.charset.Charset = java.nio.charset.StandardCharsets.ISO_8859_1

    /**
     * UTF-8 is only used for some chunks
     */
    //var charsetUTF8 = UTF_8

    /**
     * PNG magic bytes
     */
    val pngIdSignature: ByteArray
        get() = byteArrayOf(-119, 80, 78, 71, 13, 10, 26, 10)

    fun doubleToInt100000(d: Double): Int {
        return (d * 100000.0 + 0.5).toInt()
    }

    fun intToDouble100000(i: Int): Double {
        return i / 100000.0
    }

    fun readByte(inputStream: InputPngStream): Int {
        return try {
            inputStream.read()
        } catch (e: Throwable) {
            throw PngjInputException("error reading byte", e)
        }
    }

    /**
     * -1 if eof
     *
     * PNG uses "network byte order"
     */
    fun readInt2(inputStream: InputPngStream): Int {
        return try {
            val b1: Int = inputStream.read()
            val b2: Int = inputStream.read()
            if (b1 == -1 || b2 == -1) -1 else b1 shl 8 or b2
        } catch (e: Throwable) {
            throw PngjInputException("error reading Int2", e)
        }
    }

    /**
     * -1 if eof
     *
     * PNG uses "network byte order"
     */
    fun readInt4(inputStream: InputPngStream): Int {
        return try {
            val b1: Int = inputStream.read()
            val b2: Int = inputStream.read()
            val b3: Int = inputStream.read()
            val b4: Int = inputStream.read()
            if (b1 == -1 || b2 == -1 || b3 == -1 || b4 == -1) -1 else b1 shl 24 or (b2 shl 16) or (b3 shl 8) + b4
        } catch (e: Throwable) {
            throw PngjInputException("error reading Int4", e)
        }
    }

    fun readInt1fromByte(b: ByteArray?, offset: Int): Int {
        require(b != null)
        return b[offset].toInt() and 0xff
    }

    fun readInt2fromBytes(b: ByteArray?, offset: Int): Int {
        require(b != null)
        return b[offset].toInt() and 0xff shl 8 or (b[offset + 1].toInt() and 0xff)
    }

    fun readInt4fromBytes(b: ByteArray?, offset: Int): Int {
        require(b != null)
        return (b[offset].toInt() and 0xff shl 24 or (b[offset + 1].toInt() and 0xff shl 16) or (b[offset + 2].toInt() and 0xff shl 8)
                or (b[offset + 3].toInt() and 0xff))
    }

    fun writeByte(os: OutputPngStream, b: Byte) {
        try {
            os.write(b.toInt())
        } catch (e: Throwable) {
            throw PngjOutputException(e)
        }
    }

    fun writeByte(os: OutputPngStream, bs: ByteArray) {
        try {
            os.write(bs)
        } catch (e: Throwable) {
            throw PngjOutputException(e)
        }
    }

    fun writeInt2(os: OutputPngStream, n: Int) {
        val temp = byteArrayOf((n shr 8 and 0xff).toByte(), (n and 0xff).toByte())
        writeBytes(os, temp)
    }

    fun writeInt4(os: OutputPngStream, n: Int) {
        val temp = ByteArray(4)
        writeInt4tobytes(n, temp, 0)
        writeBytes(os, temp)
    }

    fun writeInt2tobytes(n: Int, b: ByteArray?, offset: Int) {
        require(b != null)
        b[offset] = (n shr 8 and 0xff).toByte()
        b[offset + 1] = (n and 0xff).toByte()
    }

    fun writeInt4tobytes(n: Int, b: ByteArray?, offset: Int) {
        require(b != null)
        b[offset] = (n shr 24 and 0xff).toByte()
        b[offset + 1] = (n shr 16 and 0xff).toByte()
        b[offset + 2] = (n shr 8 and 0xff).toByte()
        b[offset + 3] = (n and 0xff).toByte()
    }

    /**
     * guaranteed to read exactly len bytes. throws error if it can't
     */
    fun readBytes(inputStream: InputPngStream, b: ByteArray, offset: Int, len: Int) {
        if (len == 0) return
        try {
            var read = 0
            while (read < len) {
                val n: Int = inputStream.read(b, offset + read, len - read)
                if (n < 1) throw PngjInputException("error reading bytes, $n !=$len")
                read += n
            }
        } catch (e: Throwable) {
            throw PngjInputException("error reading", e)
        }
    }

    fun skipBytes(inputStream: InputPngStream, len: Long) {
        @Suppress("NAME_SHADOWING")
        var len = len
        try {
            while (len > 0) {
                val n1: Long = inputStream.skip(len)
                if (n1 > 0) {
                    len -= n1
                } else if (n1 == 0L) { // should we retry? lets read one byte
                    if (inputStream.read() == -1) // EOF
                        break else len--
                } else throw Throwable("skip() returned a negative value ???")
            }
        } catch (e: Throwable) {
            throw PngjInputException(e)
        }
    }

    fun writeBytes(os: OutputPngStream, b: ByteArray) {
        try {
            os.write(b)
        } catch (e: Throwable) {
            throw PngjOutputException(e)
        }
    }

    fun writeBytes(os: OutputPngStream, b: ByteArray, offset: Int, n: Int) {
        try {
            os.write(b, offset, n)
        } catch (e: Throwable) {
            throw PngjOutputException(e)
        }
    }

    fun logdebug(msg: String) {
        if (isDebug) println("logdebug: $msg")
    }

    // / filters
    fun filterRowNone(r: Int): Int {
        return (r and 0xFF)
    }

    fun filterRowSub(r: Int, left: Int): Int {
        return (r - left) and 0xFF
    }

    fun filterRowUp(r: Int, up: Int): Int {
        return (r - up) and 0xFF
    }

    fun filterRowAverage(r: Int, left: Int, up: Int): Int {
        return r - (left + up) / 2 and 0xFF
    }

    fun filterRowPaeth(r: Int, left: Int, up: Int, upleft: Int): Int { // a = left, b = above, c
        // = upper left
        return r - filterPaethPredictor(left, up, upleft) and 0xFF
    }

    fun filterPaethPredictor(a: Int, b: Int, c: Int): Int { // a = left, b =
        // above, c = upper
        // left
        // from
        // http://www.libpng.org/pub/png/spec/1.2/PNG-Filters.html
        val p = a + b - c // ; initial estimate
        val pa = if (p >= a) p - a else a - p
        val pb = if (p >= b) p - b else b - p
        val pc = if (p >= c) p - c else c - p
        // ; return nearest of a,b,c,
        // ; breaking ties in order a,b,c.
        return if (pa <= pb && pa <= pc) a else if (pb <= pc) b else c
    }

    /**
     * Prits a debug message (prints class name, method and line number)
     *
     * @param obj
     * : Object to print
     */
    fun debug(obj: Any?) {
        debug(obj, 1, true)
    }

    /**
     * Prints a debug message (prints class name, method and line number) to
     * stderr and logFile
     *
     * @param obj
     * : Object to print
     * @param offset
     * : Offset N lines from stacktrace
     * @param newLine
     * : Print a newline char at the end ('\n')
     */
    /**
     * Prits a debug message (prints class name, method and line number)
     *
     * @param obj
     * : Object to print
     * @param offset
     * : Offset N lines from stacktrace
     */
    fun debug(obj: Any?, offset: Int, newLine: Boolean = true) {
        //val ste: java.lang.StackTraceElement = java.lang.Exception().getStackTrace().get(1 + offset)
        //var steStr: String = ste.getClassName()
        //val ind = steStr.lastIndexOf('.')
        //steStr = steStr.substring(ind + 1)
        //steStr += "." + ste.getMethodName() + "(" + ste.getLineNumber() + "): " + obj?.toString()
        //println(steStr)
    }

}