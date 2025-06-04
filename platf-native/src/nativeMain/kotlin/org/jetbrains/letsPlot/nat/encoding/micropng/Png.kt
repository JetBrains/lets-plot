/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.nat.encoding.micropng

import kotlinx.cinterop.*
import org.jetbrains.letsPlot.nat.encoding.png.CRC32
import org.jetbrains.letsPlot.nat.encoding.png.InputPngStream
import org.jetbrains.letsPlot.nat.encoding.png.OutputPngStream
import org.jetbrains.letsPlot.nat.encoding.png.arraycopy
import platform.zlib.*

fun encodePng(width: Int, height: Int, rgba: ByteArray): ByteArray {
    val output = OutputPngStream()

    // Write PNG signature
    output.write(byteArrayOf(137.toByte(), 80, 78, 71, 13, 10, 26, 10))

    // IHDR chunk
    output.writePngChunk("IHDR", buildIHDR(width, height))

    // IDAT chunk
    val rawScanlines = ByteArray((width * 4 + 1) * height)
    for (y in 0 until height) {
        rawScanlines[y * (width * 4 + 1)] = 0  // filter type 0
        arraycopy(rgba, y * width * 4, rawScanlines, y * (width * 4 + 1) + 1, width * 4)
    }
    val deflated = compressPngData(rawScanlines)
    output.writePngChunk("IDAT", deflated)

    // IEND chunk
    output.writePngChunk("IEND", ByteArray(0))

    return output.byteArray
}

fun buildIHDR(width: Int, height: Int): ByteArray = ByteBuffer(ByteArray(13)).apply {
    putInt(width)
    putInt(height)
    put(8) // Bit depth
    put(6) // Color type RGBA
    put(0) // Compression method
    put(0) // Filter method
    put(0) // Interlace method
}.array()

fun OutputPngStream.writePngChunk(type: String, data: ByteArray) {
    val typeBytes = type.encodeToByteArray()
    val crcInput = typeBytes + data
    val crc = CRC32().apply { update(crcInput) }.value.toInt()

    writeInt(data.size)
    write(typeBytes)
    write(data)
    writeInt(crc)
}

fun OutputPngStream.writeInt(value: Int) {
    write((value shr 24) and 0xFF)
    write((value shr 16) and 0xFF)
    write((value shr 8) and 0xFF)
    write(value and 0xFF)
}



data class DecodedPng(val width: Int, val height: Int, val rgba: ByteArray)

fun decodePng(input: ByteArray): DecodedPng {
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
                require(bitDepth == 8 && colorType == 6) { "Only 8-bit RGBA PNG supported" }
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
    val decompressed = ByteArray(scanlineLength * height)
    decompressPngData(compressed, scanlineLength * height).also { decompressed ->
        require(decompressed.size == scanlineLength * height) { "Decompressed data size mismatch" }
    }

    // 4. Remove scanline filters (only filter type 0)
    val rgba = ByteArray(width * height * 4)
    for (y in 0 until height) {
        val inOffset = y * scanlineLength
        val outOffset = y * width * 4
        require(decompressed[inOffset].toInt() == 0) { "Only filter type 0 supported" }
        arraycopy(decompressed, inOffset + 1, rgba, outOffset, width * 4)
    }

    return DecodedPng(width, height, rgba)
}

private fun readInt(stream: InputPngStream): Int {
    return (stream.read() shl 24) or (stream.read() shl 16) or (stream.read() shl 8) or stream.read()
}

class ByteBuffer(private val buffer: ByteArray) {
    private var position = 0

    val remaining: Int get() = buffer.size - position
    val capacity: Int get() = buffer.size

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

    fun rewind() {
        position = 0
    }

    fun slice(): ByteArray = buffer.copyOfRange(position, buffer.size)

    fun array(): ByteArray = buffer.copyOf()
}


fun compressPngData(input: ByteArray): ByteArray {
    memScoped {
        val inputSize = input.size
        val inputPtr = input.refTo(0).getPointer(this)
        val maxOutputSize = compressBound(inputSize.convert()).toInt()
        val output = ByteArray(maxOutputSize)
        val outputPtr = output.refTo(0).getPointer(this)
        val outputSize = alloc<platform.posix.size_tVar>()

        val result = compress2(
            dest = outputPtr.reinterpret(),
            destLen = outputSize.ptr.reinterpret(),
            source = inputPtr.reinterpret(),
            sourceLen = inputSize.convert(),
            level = Z_BEST_COMPRESSION
        )

        check(result == Z_OK) { "Zlib compression failed: $result" }
        return output.copyOf(outputSize.value.toInt())
    }
}

fun decompressPngData(input: ByteArray, expectedSize: Int): ByteArray {
    memScoped {
        val inputPtr = input.refTo(0).getPointer(this)
        val output = ByteArray(expectedSize)
        val outputPtr = output.refTo(0).getPointer(this)
        val outputSize = alloc<platform.posix.size_tVar>()
        outputSize.value = expectedSize.toULong()

        val result = uncompress(
            dest = outputPtr.reinterpret(),
            destLen = outputSize.ptr.reinterpret(),
            source = inputPtr.reinterpret(),
            sourceLen = input.size.convert()
        )

        check(result == Z_OK) { "Zlib decompression failed: $result" }
        return output.copyOf(outputSize.value.toInt())
    }
}