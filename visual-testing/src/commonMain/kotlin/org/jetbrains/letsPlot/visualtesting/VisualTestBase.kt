package org.jetbrains.letsPlot.visualtesting

import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.visualtesting.ImageComparer.ComparisonProfile

abstract class VisualTestBase {
    abstract val imageComparer: ImageComparer
    abstract val canvasPeer: CanvasPeer
    open val defaultComparisonProfile: ComparisonProfile = ComparisonProfile.Strict

    protected fun assertImage(actual: Bitmap, testName: String, profile: ComparisonProfile? = null) {
        val expectedFileName = testName.replace(" ", "_").replace(".", "_")
        imageComparer.assertBitmapEquals(expectedFileName, actual, profile)
    }
}