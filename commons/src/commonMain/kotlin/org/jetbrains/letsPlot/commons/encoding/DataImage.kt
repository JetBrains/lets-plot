/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.encoding

import org.jetbrains.letsPlot.commons.values.Bitmap
import kotlin.math.min

expect fun deflate(input: ByteArray): ByteArray
expect fun inflate(input: ByteArray, expectedSize: Int): ByteArray

object DataImage {
    fun encode(width: Int, height: Int, rgba: IntArray): String {
        val byteArray = ByteArray(rgba.size * 4)
        for (i in rgba.indices) {
            val color = rgba[i]
            byteArray[i * 4] = (color shr 16 and 0xFF).toByte() // R
            byteArray[i * 4 + 1] = (color shr 8 and 0xFF).toByte() // G
            byteArray[i * 4 + 2] = (color and 0xFF).toByte() // B
            byteArray[i * 4 + 3] = (color shr 24 and 0xFF).toByte() // A
        }
        return encode(width, height, byteArray)
    }

    fun encode(width: Int, height: Int, rgba: ByteArray): String {
        val pngData = encodePng(width, height, rgba)
        val base64 = Base64.encode(pngData)
        return "data:image/png;base64,$base64"
    }

    // Returns Triple(width, height, ByteArray of RGBA values)
    fun decode(dataUrl: String): Bitmap {
        val base64 = dataUrl.substringAfter("base64,")
        val pngData = Base64.decode(base64)
        return decodePng(pngData)
    }


    private fun encodePng(width: Int, height: Int, rgba: ByteArray): ByteArray {
        val output = OutputStream()

        // Write PNG signature
        output.write(byteArrayOf(137.toByte(), 80, 78, 71, 13, 10, 26, 10))

        // IHDR chunk
        output.writePngChunk("IHDR", buildIHDR(width, height))

        // IDAT chunk
        val rawScanlines = if (false) {
            val rawScanlines = ByteArray((width * 4 + 1) * height)
            for (y in 0 until height) {
                rawScanlines[y * (width * 4 + 1)] = 0  // filter type 0
                arraycopy(rgba, y * width * 4, rawScanlines, y * (width * 4 + 1) + 1, width * 4)
            }
            rawScanlines
        } else {
            val rawScanlines = mutableListOf<Byte>()
            rgba.asSequence().windowed(width * 4, width * 4).forEach {
                rawScanlines += 0.toByte() // filter type 0
                rawScanlines += it
            }

            rawScanlines.toByteArray()
        }
        val deflated = deflate(rawScanlines)
        output.writePngChunk("IDAT", deflated)

        // IEND chunk
        output.writePngChunk("IEND", ByteArray(0))

        return output.byteArray
    }

    private fun buildIHDR(width: Int, height: Int): ByteArray = ByteBuffer(ByteArray(13)).apply {
        putInt(width)
        putInt(height)
        put(8) // Bit depth
        put(6) // Color type RGBA
        put(0) // Compression method
        put(0) // Filter method
        put(0) // Interlace method
    }.array()

    private fun OutputStream.writePngChunk(type: String, data: ByteArray) {
        val typeBytes = type.encodeToByteArray()
        val crcInput = typeBytes + data
        val crc = Crc32.compute(crcInput)

        writeInt(data.size)
        write(typeBytes)
        write(data)
        writeInt(crc)
    }

    private fun OutputStream.writeInt(value: Int) {
        write((value shr 24) and 0xFF)
        write((value shr 16) and 0xFF)
        write((value shr 8) and 0xFF)
        write(value and 0xFF)
    }


    class DecodedPng(
        val width: Int, val height: Int, val rgbaByteArray: ByteArray,
    ) {
        val argbIntArray: IntArray
            get() {
                return IntArray(width * height).apply {
                    for (i in rgbaByteArray.indices step 4) {
                        val r = rgbaByteArray[i].toInt() and 0xFF
                        val g = rgbaByteArray[i + 1].toInt() and 0xFF
                        val b = rgbaByteArray[i + 2].toInt() and 0xFF
                        val a = rgbaByteArray[i + 3].toInt() and 0xFF

                        this[i / 4] = (a shl 24) or (r shl 16) or (g shl 8) or b
                    }
                }
            }

        val rgbaIntArray: IntArray
            get() {
                return IntArray(width * height).apply {
                    for (i in rgbaByteArray.indices step 4) {
                        this[i / 4] = (rgbaByteArray[i].toInt() shl 16) or
                                (rgbaByteArray[i + 1].toInt() shl 8) or
                                rgbaByteArray[i + 2].toInt() or
                                (rgbaByteArray[i + 3].toInt() shl 24)
                    }
                }
            }


    }

    private fun decodePng(input: ByteArray): Bitmap {
        val stream = InputPngStream(input)

        // 1. Verify PNG signature
        val signature = ByteArray(8)
        stream.read(signature)
        val expected = byteArrayOf(137.toByte(), 80, 78, 71, 13, 10, 26, 10)
        require(signature.contentEquals(expected)) { "Invalid PNG signature" }

        var width = 0
        var height = 0
        var colorType = -1
        var bitDepth = -1
        val idatChunks = mutableListOf<Byte>()

        // 2. Read chunks
        while (true) {
            val length = readInt(stream)
            val chunkType = ByteArray(4).also { stream.read(it) }
            val type = chunkType.decodeToString() //String(chunkType, Charsets.US_ASCII)
            val data = ByteArray(length).also { stream.read(it) }
            stream.skip(4) // skip CRC

            println("Chunk: $type, Length: $length")
            when (type) {
                "IHDR" -> {
                    val buffer = ByteBuffer(data)
                    width = buffer.getInt()
                    height = buffer.getInt()
                    bitDepth = buffer.get().toInt()
                    colorType = buffer.get().toInt()
                    require(bitDepth == 8 && colorType == 6) { "Only 8-bit RGBA PNG supported. Bit depth: $bitDepth, Color type: $colorType" }
                }

                "IDAT" -> {
                    idatChunks.addAll(data.toList())
                }

                "IEND" -> break
            }
        }

        println("PNG dimensions: $width x $height, Color type: $colorType, Bit depth: $bitDepth")

        // 3. Decompress IDAT data
        val compressed = idatChunks.toByteArray()
        val scanlineLength = width * 4 + 1
        val decompressed = inflate(compressed, scanlineLength * height)
        val unfiltered = unfilterScanlines(width, height, 4, decompressed)

        println("Decoded PNG: $width x $height, RGBA size: ${unfiltered.size}")

        return Bitmap.fromRGBABytes(width, height, unfiltered)
    }

    private fun readInt(stream: InputPngStream): Int {
        return (stream.read() shl 24) or (stream.read() shl 16) or (stream.read() shl 8) or stream.read()
    }

    private class ByteBuffer(private val buffer: ByteArray) {
        private var position = 0

        fun get(): Byte {
            if (position >= buffer.size) throw IndexOutOfBoundsException("Buffer underflow")
            return buffer[position++]
        }

        fun get(index: Int): Byte = buffer[index]

        fun put(value: Byte) {
            if (position >= buffer.size) throw IndexOutOfBoundsException("Buffer overflow")
            buffer[position++] = value
        }

        fun put(index: Int, value: Byte) {
            buffer[index] = value
        }

        fun getInt(): Int {
            return (get().toInt() and 0xFF shl 24) or
                    (get().toInt() and 0xFF shl 16) or
                    (get().toInt() and 0xFF shl 8) or
                    (get().toInt() and 0xFF)
        }

        fun putInt(value: Int) {
            put((value shr 24).toByte())
            put((value shr 16).toByte())
            put((value shr 8).toByte())
            put((value).toByte())
        }

        fun array(): ByteArray = buffer.copyOf()
    }


    private fun arraycopy(
        src: ByteArray,
        srcPos: Int,
        dest: ByteArray,
        destPos: Int,
        length: Int
    ) {
        src.copyInto(dest, destPos, srcPos, srcPos + length)
    }

    private fun arraycopy(
        src: DoubleArray,
        srcPos: Int,
        dest: DoubleArray,
        destPos: Int,
        length: Int
    ) {
        src.copyInto(dest, destPos, srcPos, srcPos + length)
    }

    private fun fill(
        src: ByteArray,
        value: Byte
    ) {
        src.fill(value)
    }

    private fun fill(
        src: IntArray,
        value: Int
    ) {
        src.fill(value)
    }

    private fun fill(
        src: DoubleArray,
        value: Double
    ) {
        src.fill(value)
    }

    private class Crc32 {
        private var crc = -1 // 0xFFFFFFFF

        fun update(byte: Int) {
            val index = (crc xor byte) and 0xFF
            crc = (crc ushr 8) xor crcTable[index]
        }

        fun update(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size) {
            for (i in offset until (offset + length)) {
                update(bytes[i].toInt() and 0xFF)
            }
        }

        fun getValue(): Int {
            return crc.inv()
        }

        fun reset() {
            crc = -1
        }

        companion object {
            fun compute(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size): Int {
                val crc32 = Crc32()
                crc32.update(bytes, offset, length)
                return crc32.getValue()
            }

            private val crcTable: IntArray by lazy {
                IntArray(256) { i ->
                    var c = i
                    repeat(8) {
                        c = if ((c and 1) != 0) {
                            (c ushr 1) xor 0xEDB88320.toInt()
                        } else {
                            c ushr 1
                        }
                    }
                    c
                }
            }
        }
    }

    private fun unfilterScanlines(width: Int, height: Int, bytesPerPixel: Int, filtered: ByteArray): ByteArray {
        val stride = width * bytesPerPixel
        val result = ByteArray(height * stride)

        for (y in 0 until height) {
            val filterType = filtered[y * (stride + 1)].toInt() and 0xFF
            val prevLineOffset = (y - 1) * stride
            val lineOffset = y * stride
            val filteredOffset = y * (stride + 1) + 1

            for (x in 0 until stride) {
                val raw: Int = when (filterType) {
                    0 -> filtered[filteredOffset + x].toInt() and 0xFF
                    1 -> {
                        val left = if (x >= bytesPerPixel) result[lineOffset + x - bytesPerPixel].toInt() and 0xFF else 0
                        (filtered[filteredOffset + x].toInt() and 0xFF + left) and 0xFF
                    }
                    2 -> {
                        val above = if (y > 0) result[prevLineOffset + x].toInt() and 0xFF else 0
                        (filtered[filteredOffset + x].toInt() and 0xFF + above) and 0xFF
                    }
                    3 -> {
                        val left = if (x >= bytesPerPixel) result[lineOffset + x - bytesPerPixel].toInt() and 0xFF else 0
                        val above = if (y > 0) result[prevLineOffset + x].toInt() and 0xFF else 0
                        (filtered[filteredOffset + x].toInt() and 0xFF + ((left + above) / 2)) and 0xFF
                    }
                    4 -> {
                        val f = filtered[filteredOffset + x].toInt() and 0xFF

                        val left = if (x >= bytesPerPixel) result[lineOffset + x - bytesPerPixel].toInt() and 0xFF else 0
                        val above = if (y > 0) result[prevLineOffset + x].toInt() and 0xFF else 0
                        val upperLeft = if (y > 0 && x >= bytesPerPixel) result[prevLineOffset + x - bytesPerPixel].toInt() and 0xFF else 0
                        val paeth = paethPredictor(left, above, upperLeft)
                        val raw = (f + paeth) and 0xFF

                        println("[$y:$x] filter=$f left=$left above=$above upperLeft=$upperLeft paeth=$paeth => raw=$raw")


                        (filtered[filteredOffset + x].toInt() and 0xFF + paeth) and 0xFF
                        raw
                    }
                    else -> error("Unsupported filter type: $filterType")
                }

                result[lineOffset + x] = raw.toByte()
            }
        }

        return result
    }

    private fun paethPredictor(a: Int, b: Int, c: Int): Int {
        val p = a + b - c
        val pa = kotlin.math.abs(p - a)
        val pb = kotlin.math.abs(p - b)
        val pc = kotlin.math.abs(p - c)
        return when {
            pa <= pb && pa <= pc -> a
            pb <= pc -> b
            else -> c
        }
    }

    private class InputPngStream(
        private val data: ByteArray
    ) {
        private var i = 0

        private val available get() = data.size - i

        fun read(): Int {
            return data[i++].toInt() and 0xFF
        }

        fun read(b: ByteArray, off: Int, len: Int): Int {
            var read = 0
            val end = min(len, available)
            while (read < end) {
                b[off + read++] = read().toByte()
            }

            return read
        }

        fun skip(len: Long): Long {
            return min(available, len.toInt()).also { i += it }.toLong()
        }

        fun read(outBuffer: ByteArray): Int {
            return read(outBuffer, 0, outBuffer.size)
        }

        override fun toString(): String {
            return data.joinToString { it.toUByte().toString(16) }
        }
    }


    private class OutputStream {
        private val buffer = mutableListOf<Byte>()
        val byteArray get() = buffer.toByteArray()

        fun write(data: ByteArray, off: Int, len: Int) {
            (off until off + len).forEach { i ->
                buffer.add(data[i])
            }
        }

        fun write(b: Int) {
            buffer.add(b.toByte())
        }

        fun write(data: ByteArray) {
            write(data, 0, data.size)
        }

        override fun toString(): String {
            return buffer.joinToString { it.toUByte().toString(16) }
        }
    }

}
