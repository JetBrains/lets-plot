/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools.res

object ToolbarIcons {
    val PAN_TOOL = """
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16">
          <path fill="none" stroke="black" stroke-width="1" d="M8,1 L8,15 M1,8 L15,8" />
          <path fill="none" stroke="black" stroke-width="1" d="M4,4 L12,12 M12,4 L4,12" />
        </svg>
    """.trimIndent()

    val ZOOM_CORNER = """
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16">
          <rect x="2" y="2" width="12" height="12" fill="none" stroke="black" stroke-width="1" />
          <path fill="none" stroke="black" stroke-width="1" d="M2,2 L14,14" />
          <circle cx="2" cy="2" r="1.5" fill="black" />
        </svg>
    """.trimIndent()

    val ZOOM_CENTER = """
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16">
          <rect x="2" y="2" width="12" height="12" fill="none" stroke="black" stroke-width="1" />
          <path fill="none" stroke="black" stroke-width="1" d="M2,2 L14,14" />
          <circle cx="8" cy="8" r="1.5" fill="black" />
        </svg>
    """.trimIndent()

    val RESET = """
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16">
          <path fill="none" stroke="black" stroke-width="1" d="M14,8 A6,6 0 1,1 8,2" />
          <polygon points="8,2 11,5 5,5" fill="black" />
        </svg>
    """.trimIndent()
}