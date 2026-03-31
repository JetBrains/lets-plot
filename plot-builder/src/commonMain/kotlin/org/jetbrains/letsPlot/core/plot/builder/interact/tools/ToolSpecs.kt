/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

import org.jetbrains.letsPlot.core.interact.InteractionSpec
import org.jetbrains.letsPlot.core.interact.InteractionSpec.Name.*
import org.jetbrains.letsPlot.core.interact.InteractionSpec.ZoomBoxMode.CENTER_START
import org.jetbrains.letsPlot.core.interact.InteractionSpec.ZoomBoxMode.CORNER_START
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.res.ToolbarIcons

object ToolSpecs {
    const val PAN_TOOL_NAME = "org.jetbrains.letsPlot.interact.DragPanTool"
    const val BBOX_ZOOM_TOOL_NAME = "org.jetbrains.letsPlot.interact.RubberBandZoomTool"
    const val CBOX_ZOOM_TOOL_NAME = "org.jetbrains.letsPlot.interact.CenterpointZoomTool"

    val PAN_TOOL_SPEC = mapOf(
        "name" to PAN_TOOL_NAME,
        "label" to "Pan",
        "icon" to ToolbarIcons.PAN_TOOL,
        "interactions" to listOf(
            InteractionSpec(DRAG_PAN),
            InteractionSpec(WHEEL_ZOOM)
        )
    )

    val BBOX_ZOOM_TOOL_SPEC = mapOf(
        "name" to BBOX_ZOOM_TOOL_NAME,
        "label" to "Rubber Band Zoom",
        "icon" to ToolbarIcons.ZOOM_CORNER,
        "interactions" to listOf(
            InteractionSpec(BOX_ZOOM, CORNER_START),
            InteractionSpec(WHEEL_ZOOM)
        )
    )

    val CBOX_ZOOM_TOOL_SPEC = mapOf(
        "name" to CBOX_ZOOM_TOOL_NAME,
        "label" to "Centerpoint Zoom",
        "icon" to ToolbarIcons.ZOOM_CENTER,
        "interactions" to listOf(
            InteractionSpec(BOX_ZOOM, CENTER_START),
            InteractionSpec(WHEEL_ZOOM)
        )
    )
}