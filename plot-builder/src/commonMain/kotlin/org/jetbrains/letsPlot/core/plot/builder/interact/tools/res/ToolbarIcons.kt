/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools.res

object ToolbarIcons {
    val PAN_TOOL = """
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16">
            <g stroke="none">
                <path d="M7.5,1V6.793L4.354,3.646 3.646,4.354 6.793,7.5H1v1h5.793L3.646,11.646 4.354,12.354 7.5,9.207V15h1V9.207L11.646,12.354 12.354,11.646 9.207,8.5H15v-1H9.207L12.354,4.354 11.646,3.646 8.5,6.793V1Z"/>
            </g>
        </svg>
    """.trimIndent()

    val ZOOM_CORNER = """
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16">
            <g stroke="none">
                <circle cx="1.6" cy="2.6" r="1.5"/>
                <path d="M3,2C1.901,2 1,2.901 1,4v8c0,1.099 0.901,2 2,2h10c1.099,0 2,-0.901 2,-2V4c0,-1.099 -0.901,-2 -2,-2ZM3,3h10c0.563,0 1,0.438 1,1v8c0,0.563 -0.438,1 -1,1H3c-0.563,0 -1,-0.438 -1,-1V4c0,-0.563 0.438,-1 1,-1Z"/>
            </g>
        </svg>
    """.trimIndent()

    val ZOOM_CENTER = """
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16">
            <g stroke="none">
                <circle cx="8" cy="8" r="1.5" />
                <path d="M3,2C1.901,2 1,2.901 1,4v8c0,1.099 0.901,2 2,2h10c1.099,0 2,-0.901 2,-2V4c0,-1.099 -0.901,-2 -2,-2ZM3,3h10c0.563,0 1,0.438 1,1v8c0,0.563 -0.438,1 -1,1H3c-0.563,0 -1,-0.438 -1,-1V4c0,-0.563 0.438,-1 1,-1Z"/>
            </g>
        </svg>
    """.trimIndent()

    val RESET = """
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16">
            <g stroke="none">
                <path d="M8.322,1.482C6.095,1.34 3.77,2.313 2.367,4.75l0.865,0.5C4.867,2.41 7.757,1.939 10.098,2.908 12.439,3.877 14.152,6.255 13.311,9.41 12.469,12.566 9.8,13.773 7.287,13.447 4.775,13.122 2.504,11.276 2.5,8h-1c0.004,3.75 2.727,6.06 5.658,6.439C10.09,14.819 13.314,13.282 14.277,9.668 15.241,6.054 13.212,3.115 10.48,1.984 9.798,1.702 9.065,1.53 8.322,1.482Z"/>
                <path d="M2,1v4.5h4.5v-1H3V1Z"/>
            </g>
        </svg>
    """.trimIndent()
}