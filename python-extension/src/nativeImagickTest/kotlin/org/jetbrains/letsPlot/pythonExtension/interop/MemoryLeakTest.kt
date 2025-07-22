package kotlin.org.jetbrains.letsPlot.pythonExtension.interop
//  WORKS ONLY ON LINUX
// To enable allocation tracking, enable the following log:
// org/jetbrains/letsPlot/imagick/canvas/MagickUtil.kt:15
/*
import demoAndTestShared.parsePlotSpec
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import org.jetbrains.letsPlot.pythonExtension.interop.PlotReprGenerator
import org.jetbrains.letsPlot.pythonExtension.interop.createImageComparer
import org.jetbrains.letsPlot.pythonExtension.interop.newEmbeddedFontsManager
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen
import kotlin.math.absoluteValue
import kotlin.test.Test
import platform.posix.*
import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.values.Bitmap
import platform.linux.RUSAGE_SELF
import platform.linux.getrusage
import platform.linux.rusage
import kotlin.math.absoluteValue

class MemoryLeakTest {
    companion object {
        private val embeddedFontsManager by lazy { newEmbeddedFontsManager() }
        private val imageComparer by lazy { createImageComparer(embeddedFontsManager) }

        data class MemorySnapshot(
            val vmSize: Long,
            val vmRss: Long,
            val deltaSize: Long,
            val deltaRss: Long
        ) {
            override fun toString(): String {
                return "MemorySnapshot: vmSize = ${byteSizeToString(vmSize)}, vmRss = ${byteSizeToString(vmRss)}, deltaSize = ${
                    byteSizeToString(
                        deltaSize
                    )
                }, deltaRss = ${byteSizeToString(deltaRss)}"
            }
        }

        val ZERO_SNAPSHOT = MemorySnapshot(
            vmSize = 0L,
            vmRss = 0L,
            deltaSize = 0L,
            deltaRss = 0L
        )

        fun logProcStatus(prev: MemorySnapshot = ZERO_SNAPSHOT): MemorySnapshot {
            val file = fopen("/proc/self/status", "r") ?: run {
                println("Cannot open /proc/self/status")
                throw Exception("Cannot open /proc/self/status")
            }

            var vmSize: Long? = null
            var vmRss: Long? = null

            memScoped {
                val bufferSize = 1024
                val buffer = allocArray<ByteVar>(bufferSize)

                while (fgets(buffer, bufferSize, file) != null) {
                    val line = buffer.toKString().trim()
                    when {
                        line.startsWith("VmSize:") -> {
                            vmSize = extractKbValue(line)
                        }

                        line.startsWith("VmRSS:") -> {
                            vmRss = extractKbValue(line)
                        }
                    }
                }
            }

            fclose(file)

            val vmSizeBytes = vmSize?.times(1024) ?: error("VmSize not found in /proc/self/status")
            val vmRssBytes = vmRss?.times(1024) ?: error("VmRSS not found in /proc/self/status")

            return MemorySnapshot(
                vmSize = vmSizeBytes,
                vmRss = vmRssBytes,
                deltaSize = vmSizeBytes - prev.vmSize,
                deltaRss = vmRssBytes - prev.vmRss
            )
        }


        fun byteSizeToString(size: Long): String {
            val v = size.absoluteValue
            return when {
                v < 1024 -> "$size B"
                v < 1024 * 1024 -> "${size / 1024} KB"
                v < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
                else -> {
                    val gb = size / (1024 * 1024 * 1024)
                    val mb = ((size % (1024 * 1024 * 1024)) / (1024 * 1024)).toString().padStart(3, '0')

                    "$gb,$mb GB"
                }
            }
        }

        private fun extractKbValue(line: String): Long? {
            // Format: "VmSize:    173456 kB"
            val regex = Regex("""\d+""")
            return regex.find(line)?.value?.toLongOrNull()
        }

    }


    @Test
    fun smallPictures1xLeakTest() {
        runTest(n = 1, w = 5, h = 5)
    }

    @Test
    fun smallPictures50xLeakTest() {
        runTest(n = 50, w = 5, h = 5)
    }

    @Test
    fun smallPictures100xLeakTest() {
        runTest(n = 100, w = 5, h = 5)
    }

    @Test
    fun smallPictures30xLeakTest() {
        runTest(n = 30, w = 5, h = 5)
    }

    @Test
    fun smallPictures10xLeakTest() {
        runTest(n = 10, w = 5, h = 5)
    }

    @Test
    fun largePictures10xLeakTest() {
        runTest(n = 10, w = 600, h = 400)
    }

    @Test
    fun largePictures30xLeakTest() {
        runTest(n = 30, w = 600, h = 400)
    }

    private fun runTest(n: Int, w: Int, h: Int) {
        // This is a helper function to run the test multiple times with different parameters.
        val spec = """
            |{
            |  "kind": "plot",
            |  "layers": [ { "geom": "blank" } ]
            |}
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)

        val startupMemoryStatus = logProcStatus()

        fun exportBitmap(plotSpec: MutableMap<String, Any>, w: Int, h: Int): Bitmap {
            return PlotReprGenerator.exportBitmap(plotSpec, w.toFloat(), h.toFloat(), "px", 0, 1.0, embeddedFontsManager)
                ?: error("Failed to export bitmap from plot spec")
        }

        exportBitmap(plotSpec, w, h)

        val firstRunMemoryStatus = logProcStatus(startupMemoryStatus)

        repeat(n - 1) { runIndex -> exportBitmap(plotSpec, w, h) }

        val finalMemoryStatus = logProcStatus(firstRunMemoryStatus)

        println("\n=== Memory Report ===")
        println("Startup Memory: $startupMemoryStatus")
        println("First Run Memory: $firstRunMemoryStatus")
        println("Final Memory: $finalMemoryStatus")
    }
}
*/