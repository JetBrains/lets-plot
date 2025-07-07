/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.encoding

import org.jetbrains.letsPlot.commons.values.Bitmap
import kotlin.math.min

/** * PNG encoder/decoder.
 * Supports only 8-bit RGBA PNG format without filters.
 *
 * To clean up any PNG to match these requirements, use the following Python function:
 *
 * import png
 * import os
 *
 * def cleanup_png(input_filepath, output_filepath=None):
 *     if output_filepath is None:
 *         base_name, ext = os.path.splitext(input_filepath)
 *         output_filepath = f"{base_name}_minimized.png"
 *
 *     with open(input_filepath, 'rb') as f_input:
 *         reader = png.Reader(file=f_input)
 *
 *         width, height, pixels_iter_original_format, info = reader.read()
 *         pixels_list_original_format = [list(row) for row in pixels_iter_original_format]
 *
 *     writer_params = {
 *         'width': width,
 *         'height': height,
 *         'greyscale': False,
 *         'alpha': True,
 *         'bitdepth': 8,
 *         'compression': 9,
 *     }
 *
 *     writer = png.Writer(**writer_params)
 *
 *     with open(output_filepath, 'wb') as f_output:
 *         writer.write(f_output, pixels_list_original_format)
 */
object Png {

    fun encodeDataImage(bitmap: Bitmap): String {
        val pngData = encode(bitmap)
        val base64 = Base64.encode(pngData)
        return "data:image/png;base64,$base64"
    }

    fun decodeDataImage(dataUrl: String): Bitmap {
        val base64 = dataUrl.substringAfter("base64,")
        val pngData = Base64.decode(base64)
        return decode(pngData)
    }

    fun encode(bitmap: Bitmap): ByteArray {
        val output = OutputStream()

        // Write PNG signature
        output.write(byteArrayOf(137.toByte(), 80, 78, 71, 13, 10, 26, 10))

        // IHDR chunk
        output.writePngChunk("IHDR", buildIHDR(bitmap.width, bitmap.height))

        // IDAT chunk
        val rawScanlines = run {
            val rawScanlines = mutableListOf<Byte>()
            bitmap.rgbaBytes()
                .asSequence()
                .windowed(bitmap.width * 4, bitmap.width * 4)
                .forEach {
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

    fun decode(input: ByteArray): Bitmap {
        val stream = InputPngStream(input)

        // 1. Verify PNG signature
        val signature = ByteArray(8)
        stream.read(signature)
        val expected = byteArrayOf(137.toByte(), 80, 78, 71, 13, 10, 26, 10)
        require(signature.contentEquals(expected)) { "Invalid PNG signature" }

        var width = 0
        var height = 0
        val idatChunks = mutableListOf<Byte>()

        // 2. Read chunks
        while (true) {
            val length = readInt(stream)
            val chunkType = ByteArray(4).also { stream.read(it) }
            val type = chunkType.decodeToString() //String(chunkType, Charsets.US_ASCII)
            val data = ByteArray(length).also { stream.read(it) }
            stream.skip(4) // skip CRC

            //println("Chunk: $type, Length: $length")
            when (type) {
                "IHDR" -> {
                    val buffer = ByteBuffer(data)
                    width = buffer.getInt()
                    height = buffer.getInt()
                    val bitDepth = buffer.get().toInt()
                    val colorType = buffer.get().toInt()
                    require(bitDepth == 8 && colorType == 6) { "Only 8-bit RGBA PNG supported. Bit depth: $bitDepth, Color type: $colorType" }
                }

                "IDAT" -> {
                    idatChunks.addAll(data.toList())
                }

                "IEND" -> break
            }
        }

        // 3. Decompress IDAT data
        val compressed = idatChunks.toByteArray()
        val scanlineLength = width * 4 + 1
        val decompressed = inflate(compressed, scanlineLength * height)

        val strideLength = scanlineLength - 1
        val rgba = ByteArray(height * strideLength)
        for (rowIndex in 0 until height) {
            val filterType = decompressed[rowIndex * scanlineLength].toInt() and 0xFF
            if (filterType != 0) {
                error("Unsupported filter type: $filterType")
            }

            arraycopy(
                src = decompressed,
                srcPos = rowIndex * scanlineLength + 1, // +1 to skip the filter byte
                dest = rgba,
                destPos = rowIndex * strideLength,
                length = strideLength
            )
        }

        return Bitmap.fromRGBABytes(width, height, rgba)
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

    private fun readInt(stream: InputPngStream): Int {
        return (stream.read() shl 24) or (stream.read() shl 16) or (stream.read() shl 8) or stream.read()
    }

    private class ByteBuffer(private val buffer: ByteArray) {
        private var position = 0

        fun get(): Byte {
            if (position >= buffer.size) throw IndexOutOfBoundsException("Buffer underflow")
            return buffer[position++]
        }

        fun put(value: Byte) {
            if (position >= buffer.size) throw IndexOutOfBoundsException("Buffer overflow")
            buffer[position++] = value
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
