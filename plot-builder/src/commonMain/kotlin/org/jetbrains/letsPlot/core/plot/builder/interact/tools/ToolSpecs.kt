/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

import org.jetbrains.letsPlot.core.interact.event.ToolInteractionSpec
import org.jetbrains.letsPlot.core.interact.event.ToolInteractionSpec.ZoomBoxMode

object ToolSpecs {
    val PAN_TOOL_NAME = "org.jetbrains.letsPlot.interact.DragPanTool"
    val BBOX_ZOOM_TOOL_NAME = "org.jetbrains.letsPlot.interact.StartCornerBoxZoomTool"
    val CBOX_ZOOM_TOOL_NAME = "org.jetbrains.letsPlot.interact.StartCenterBoxZoomTool"

    val PAN_TOOL_SPEC = mapOf(
        "name" to PAN_TOOL_NAME,
        "label" to "Pan",
        "interactions" to listOf(
            mapOf(
                ToolInteractionSpec.NAME to ToolInteractionSpec.DRAG_PAN
            ),
            mapOf(
                ToolInteractionSpec.NAME to ToolInteractionSpec.WHEEL_ZOOM
            )
        )
    )

    val BBOX_ZOOM_TOOL_SPEC = mapOf(
        "name" to BBOX_ZOOM_TOOL_NAME,
        "label" to "BBox Zoom",
        "interactions" to listOf(
            mapOf(
                ToolInteractionSpec.NAME to ToolInteractionSpec.BOX_ZOOM,
                ToolInteractionSpec.ZOOM_BOX_MODE to ZoomBoxMode.CORNER_START
            ),
            mapOf(
                ToolInteractionSpec.NAME to ToolInteractionSpec.WHEEL_ZOOM
            )
        )
    )

    val CBOX_ZOOM_TOOL_SPEC = mapOf(
        "name" to CBOX_ZOOM_TOOL_NAME,
        "label" to "CBox Zoom",
        "interactions" to listOf(
            mapOf(
                ToolInteractionSpec.NAME to ToolInteractionSpec.BOX_ZOOM,
                ToolInteractionSpec.ZOOM_BOX_MODE to ZoomBoxMode.CORNER_START
            ),
            mapOf(
                ToolInteractionSpec.NAME to ToolInteractionSpec.WHEEL_ZOOM
            )
        )
    )
}