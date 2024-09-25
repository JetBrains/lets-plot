/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools.res

object ToolbarIcons {
    val PAN_TOOL = """
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16">
            <g stroke="#6C707E" stroke-width="1.4">
                <path d="M8,1 V15 M1,8 H15 M4,4 L12,12 M12,4 L4,12" />
            </g>    
        </svg>
    """.trimIndent()

    val ZOOM_CORNER = """
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16">
            <g stroke="#6C707E" fill="none">
                <path d="M5,3 H15 V13 H1 V7" />
                <rect x="1" y="3" width="2" height="2" />
            </g>
        </svg>
    """.trimIndent()

    val ZOOM_CENTER = """
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16">
            <g stroke="#6C707E" fill="none">
                <path d="M1,6 V3 H4 M6,3 H15 V8 M15,10 V13 H12 M10,13 H1 V8" />
                <circle cx="8" cy="8" r="0.5" />
            </g>
        </svg>
    """.trimIndent()

    val RESET = """
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16">
            <g stroke="#6C707E" stroke-width="1.4" fill="none">
                <path d="M2,8 A6,6 0 1,0 2.8,5 M2.5,1 v4 h4"/>
            </g>
        </svg>
    """.trimIndent()
}