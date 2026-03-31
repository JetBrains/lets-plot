/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalJsExport::class, ExperimentalWasmJsInterop::class)

package visualtesting

import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import kotlin.js.Promise

@JsFun("() => undefined")
private external fun jsUndefined(): JsAny?

@JsFun(
    """
    (relativePath, base64) => fetch('/__visual_testing__/artifacts', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ relativePath, base64 })
    }).then(response => {
        if (!response.ok) {
            throw new Error(`Failed to save visual test artifact '${'$'}{relativePath}': HTTP ${'$'}{response.status}`);
        }
        return response.text();
    })
    """
)
private external fun uploadArtifact(relativePath: String, base64: String): Promise<JsAny?>

@JsFun(
    """
    async (relativePath, logicalPath) => {
        const karmaPath = Object.keys(globalThis.__karma__?.files ?? {}).find(path => path.endsWith('/' + relativePath));
        const candidates = [
            karmaPath,
            relativePath,
            'kotlin/' + relativePath,
            '/base/' + relativePath,
            '/base/kotlin/' + relativePath,
            'build/processedResources/wasmJs/test/' + relativePath,
            '/base/build/processedResources/wasmJs/test/' + relativePath,
            'wasmjs-package/build/processedResources/wasmJs/test/' + relativePath,
            '/base/wasmjs-package/build/processedResources/wasmJs/test/' + relativePath
        ];

        for (const candidate of candidates) {
            if (!candidate) continue;
            const response = await fetch(candidate);
            if (response.ok) {
                return { url: candidate, buffer: await response.arrayBuffer() };
            }
        }

        throw new Error(`Failed to fetch expected image '${'$'}{logicalPath}' from any known resource location.`);
    }
    """
)
private external fun fetchArrayBuffer(relativePath: String, logicalPath: String): Promise<JsAny?>

@JsFun(
    """
    async (relativePath, logicalPath) => {
        const karmaPath = Object.keys(globalThis.__karma__?.files ?? {}).find(path => path.endsWith('/' + relativePath));
        const candidates = [
            karmaPath,
            relativePath,
            'kotlin/' + relativePath,
            '/base/' + relativePath,
            '/base/kotlin/' + relativePath,
            'build/processedResources/wasmJs/test/' + relativePath,
            '/base/build/processedResources/wasmJs/test/' + relativePath,
            'wasmjs-package/build/processedResources/wasmJs/test/' + relativePath,
            '/base/wasmjs-package/build/processedResources/wasmJs/test/' + relativePath
        ];

        for (const candidate of candidates) {
            if (!candidate) continue;
            const response = await fetch(candidate);
            if (response.ok) {
                return response.text();
            }
        }

        throw new Error(`Failed to fetch expected image manifest '${'$'}{logicalPath}' from any known resource location.`);
    }
    """
)
private external fun fetchText(url: String, logicalPath: String): Promise<JsAny?>

@JsFun("(result) => result.url")
private external fun getFetchResultUrl(result: JsAny?): String

@JsFun("(result) => result.buffer")
private external fun getFetchResultBuffer(result: JsAny?): ArrayBuffer

@JsFun(
    """
    (path) => path.startsWith('/absolute/') ? path.substring('/absolute'.length) : path
    """
)
private external fun normalizeKarmaPath(path: String): String

@JsFun("() => []")
private external fun createJsArray(): JsAny

@JsFun("(entries, relativePath, bytes) => { entries.push({ relativePath, bytes }); }")
private external fun pushFileEntry(entries: JsAny, relativePath: String, bytes: Uint8Array)

@JsFun("(arr, index, value) => { arr[index] = value; }")
private external fun setUint8ArrayValue(arr: Uint8Array, index: Int, value: Int)

@JsFun(
    """
    async (entries, dialogTitle) => {
        if (typeof globalThis.showDirectoryPicker !== 'function') {
            throw new Error('File System Access API is not available in this browser.');
        }

        const rootDir = await globalThis.showDirectoryPicker({ mode: 'readwrite' });
        for (const entry of entries) {
            const segments = entry.relativePath.split('/').filter(Boolean);
            let dir = rootDir;

            for (let i = 0; i < segments.length - 1; i++) {
                dir = await dir.getDirectoryHandle(segments[i], { create: true });
            }

            const fileHandle = await dir.getFileHandle(segments[segments.length - 1], { create: true });
            const writable = await fileHandle.createWritable();
            await writable.write(entry.bytes);
            await writable.close();
        }
    }
    """
)
private external fun saveEntriesWithDirectoryPicker(entries: JsAny, dialogTitle: String): Promise<JsAny?>

@JsFun(
    """
    (entries) => {
        for (const entry of entries) {
            const blob = new Blob([entry.bytes], { type: 'image/png' });
            const url = URL.createObjectURL(blob);
            const anchor = document.createElement('a');
            anchor.href = url;
            anchor.download = entry.relativePath.split('/').pop();
            document.body.appendChild(anchor);
            anchor.click();
            anchor.remove();
            setTimeout(() => URL.revokeObjectURL(url), 0);
        }
    }
    """
)
private external fun downloadEntries(entries: JsAny)

@JsFun(
    """
    (
        hasWrittenImages,
        listWrittenImages,
        artifactOutputDir,
        saveWrittenImagesWithDirectoryPicker,
        saveActualImagesAsExpectedWithDirectoryPicker,
        downloadWrittenImages,
        downloadActualImagesAsExpected
    ) => {
        globalThis.letsPlotVisualTesting = {
            hasWrittenImages,
            listWrittenImages,
            artifactOutputDir,
            saveWrittenImagesWithDirectoryPicker,
            saveActualImagesAsExpectedWithDirectoryPicker,
            downloadWrittenImages,
            downloadActualImagesAsExpected
        };
    }
    """
)
private external fun installVisualTestingArtifactsApiJs(
    hasWrittenImages: () -> Boolean,
    listWrittenImages: () -> String,
    artifactOutputDir: () -> String,
    saveWrittenImagesWithDirectoryPicker: () -> Promise<JsAny?>,
    saveActualImagesAsExpectedWithDirectoryPicker: () -> Promise<JsAny?>,
    downloadWrittenImages: () -> Unit,
    downloadActualImagesAsExpected: () -> Unit
)

class WasmBitmapIO(
    subdir: String = ""
) : ImageComparer.BitmapIO {
    private val expectedImagesSubdir = "expected-images".appendSubdir(subdir)
    private val outputSubdir = "actual-images".appendSubdir(subdir)

    override fun write(bitmap: Bitmap, fileName: String) {
        val pngBytes = Png.encode(bitmap)
        val base64 = org.jetbrains.letsPlot.commons.encoding.Base64.encode(pngBytes)
        val outputRelativePath = "$outputSubdir/$fileName"
        writtenImagesByFileName[fileName] = getAbsoluteWritePath(outputRelativePath)
        writtenImageOutputsByFileName[fileName] = WrittenImageOutput(
            outputRelativePath = outputRelativePath,
            expectedRelativePath = "$expectedImagesSubdir/$fileName",
            pngBytes = pngBytes
        )
        queueArtifactUpload(outputRelativePath, base64)
    }

    override fun read(fileName: String): Bitmap {
        val pngBytes = expectedImagesByFileName[fileName]
            ?: error("Expected image was not preloaded: $expectedImagesSubdir/$fileName")
        return Png.decode(pngBytes)
    }

    override fun getReadFilePath(fileName: String): String {
        return getExpectedImageUrl(fileName) ?: buildExpectedImagePath(fileName)
    }

    override fun getWriteFilePath(fileName: String): String {
        return writtenImagesByFileName[fileName]
            ?: "missing://$outputSubdir/$fileName"
    }

    private fun buildExpectedImagePath(fileName: String): String {
        return "$expectedImagesSubdir/$fileName"
    }

    private fun String.appendSubdir(subdir: String): String {
        return if (subdir.isEmpty()) this else "$this/$subdir"
    }

    companion object {
        private data class WrittenImageOutput(
            val outputRelativePath: String,
            val expectedRelativePath: String,
            val pngBytes: ByteArray
        )

        private const val ARTIFACTS_ROOT = "wasmjs-package/build/reports"
        private const val EXPECTED_IMAGES_ROOT_SUFFIX = "/src/wasmJsTest/resources/expected-images"
        private val expectedImagesByFileName = mutableMapOf<String, ByteArray>()
        private val expectedImageReadPathsByFileName = mutableMapOf<String, String>()
        private val writtenImagesByFileName = mutableMapOf<String, String>()
        private val writtenImageOutputsByFileName = linkedMapOf<String, WrittenImageOutput>()
        private var pendingArtifactUploads: Promise<JsAny?> = Promise.resolve(jsUndefined())
        private var absoluteArtifactsRootPath: String? = null

        fun preloadExpectedImages(resourceSubdir: String): Promise<JsAny?> {
            installVisualTestingArtifactsApi()
            expectedImagesByFileName.clear()
            expectedImageReadPathsByFileName.clear()
            writtenImagesByFileName.clear()
            writtenImageOutputsByFileName.clear()
            pendingArtifactUploads = Promise.resolve(jsUndefined())
            absoluteArtifactsRootPath = null
            val expectedImagesDir = "expected-images/$resourceSubdir"
            val manifestPath = "$expectedImagesDir/index.txt"

            return fetchText(manifestPath, manifestPath).then { manifestText ->
                val fileNames = manifestText.toString()
                    .lineSequence()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .toList()

                if (fileNames.isEmpty()) {
                    error("No expected images listed in $manifestPath")
                }

                var chain = Promise.resolve(jsUndefined())
                for (fileName in fileNames) {
                    val resourcePath = "$expectedImagesDir/$fileName"
                    chain = chain.then {
                        fetchArrayBuffer(resourcePath, fileName).then { fetchResult ->
                            val arrayBuffer = getFetchResultBuffer(fetchResult)
                            val bytes = Uint8Array(arrayBuffer)
                            val pngBytes = ByteArray(bytes.length)
                            for (i in 0 until bytes.length) {
                                pngBytes[i] = bytes[i]
                            }
                            expectedImagesByFileName[fileName] = pngBytes
                            val fetchedUrl = getFetchResultUrl(fetchResult)
                            val readPath = reportPathForFetchedUrl(fetchedUrl)
                            expectedImageReadPathsByFileName[fileName] = readPath
                            initializeAbsoluteArtifactsRootPath(readPath)
                            jsUndefined()
                        }
                    }
                }

                chain
            }
        }

        fun hasWrittenImages(): Boolean {
            return writtenImageOutputsByFileName.isNotEmpty()
        }

        fun listWrittenImages(): Array<String> {
            return writtenImageOutputsByFileName.values
                .map { it.outputRelativePath }
                .toTypedArray()
        }

        fun artifactOutputDir(): String = absoluteArtifactsRootPath ?: ARTIFACTS_ROOT

        fun saveWrittenImagesWithDirectoryPicker(): Promise<JsAny?> {
            val entries = buildEntries { it.outputRelativePath }
            return if (writtenImageOutputsByFileName.isEmpty()) {
                Promise.resolve(jsUndefined())
            } else {
                saveEntriesWithDirectoryPicker(entries, "Save visual test artifacts")
            }
        }

        fun saveActualImagesAsExpectedWithDirectoryPicker(): Promise<JsAny?> {
            val entries = buildEntries { output ->
                if (output.outputRelativePath.endsWith("_diff.png")) {
                    output.outputRelativePath
                } else {
                    output.expectedRelativePath
                }
            }
            return if (writtenImageOutputsByFileName.isEmpty()) {
                Promise.resolve(jsUndefined())
            } else {
                saveEntriesWithDirectoryPicker(entries, "Save visual test baselines")
            }
        }

        fun downloadWrittenImages() {
            if (writtenImageOutputsByFileName.isNotEmpty()) {
                downloadEntries(buildEntries { it.outputRelativePath })
            }
        }

        fun downloadActualImagesAsExpected() {
            if (writtenImageOutputsByFileName.isNotEmpty()) {
                downloadEntries(
                    buildEntries { output ->
                        if (output.outputRelativePath.endsWith("_diff.png")) {
                            output.outputRelativePath
                        } else {
                            output.expectedRelativePath
                        }
                    }
                )
            }
        }

        fun awaitPendingArtifactUploads(): Promise<JsAny?> = pendingArtifactUploads

        private fun buildEntries(pathSelector: (WrittenImageOutput) -> String): JsAny {
            val entries = createJsArray()
            for (output in writtenImageOutputsByFileName.values) {
                val bytes = Uint8Array(output.pngBytes.size)
                for (index in output.pngBytes.indices) {
                    setUint8ArrayValue(bytes, index, output.pngBytes[index].toInt() and 0xFF)
                }
                pushFileEntry(entries, pathSelector(output), bytes)
            }
            return entries
        }

        private fun queueArtifactUpload(relativePath: String, base64: String) {
            pendingArtifactUploads = pendingArtifactUploads.then {
                uploadArtifact(relativePath, base64)
            }
        }

        private fun reportPathForFetchedUrl(url: String): String {
            return if (url.startsWith("/absolute/")) normalizeKarmaPath(url) else url
        }

        private fun initializeAbsoluteArtifactsRootPath(readPath: String) {
            if (absoluteArtifactsRootPath != null) {
                return
            }
            val expectedRootIndex = readPath.indexOf(EXPECTED_IMAGES_ROOT_SUFFIX)
            if (expectedRootIndex >= 0) {
                val moduleDir = readPath.substring(0, expectedRootIndex)
                absoluteArtifactsRootPath = "$moduleDir/build/reports"
            }
        }

        private fun getExpectedImageUrl(fileName: String): String? = expectedImageReadPathsByFileName[fileName]

        private fun getAbsoluteWritePath(relativePath: String): String {
            return absoluteArtifactsRootPath?.let { "$it/$relativePath" } ?: "$ARTIFACTS_ROOT/$relativePath"
        }
    }
}

@JsExport
fun hasWrittenImages(): Boolean = WasmBitmapIO.hasWrittenImages()

@JsExport
fun listWrittenImages(): String = WasmBitmapIO.listWrittenImages().joinToString("\n")

@JsExport
fun artifactOutputDir(): String = WasmBitmapIO.artifactOutputDir()

@JsExport
fun saveWrittenImagesWithDirectoryPicker(): Promise<JsAny?> = WasmBitmapIO.saveWrittenImagesWithDirectoryPicker()

@JsExport
fun saveActualImagesAsExpectedWithDirectoryPicker(): Promise<JsAny?> =
    WasmBitmapIO.saveActualImagesAsExpectedWithDirectoryPicker()

@JsExport
fun downloadWrittenImages() {
    WasmBitmapIO.downloadWrittenImages()
}

@JsExport
fun downloadActualImagesAsExpected() {
    WasmBitmapIO.downloadActualImagesAsExpected()
}

private fun installVisualTestingArtifactsApi() {
    installVisualTestingArtifactsApiJs(
        ::hasWrittenImages,
        ::listWrittenImages,
        ::artifactOutputDir,
        ::saveWrittenImagesWithDirectoryPicker,
        ::saveActualImagesAsExpectedWithDirectoryPicker,
        ::downloadWrittenImages,
        ::downloadActualImagesAsExpected
    )
}
