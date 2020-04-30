/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.testing


val SINGLE_PLOT_STYLE_ELEMENT = """
        |  <style type="text/css">
        |  .plt-container {
        |	font-family: "Lucida Grande", sans-serif;
        |	cursor: crosshair;
        |	user-select: none;
        |	-webkit-user-select: none;
        |	-moz-user-select: none;
        |	-ms-user-select: none;
        |}
        |.plt-backdrop {
        |   fill: white;
        |}
        |.plt-transparent .plt-backdrop {
        |   visibility: hidden;
        |}
        |text {
        |	font-size: 12px;
        |	fill: #3d3d3d;
        |}
        |.plt-data-tooltip text {
        |	font-size: 12px;
        |}
        |.plt-axis-tooltip text {
        |	font-size: 10px;
        |}
        |.plt-axis line {
        |	shape-rendering: crispedges;
        |}
        |.plt-plot-title {
        |
        |  font-size: 16.0px;
        |  font-weight: bold;
        |}
        |.plt-axis .tick text {
        |
        |  font-size: 10.0px;
        |}
        |.plt-axis.small-tick-font .tick text {
        |
        |  font-size: 8.0px;
        |}
        |.plt-axis-title text {
        |
        |  font-size: 12.0px;
        |}
        |.plt_legend .legend-title text {
        |
        |  font-size: 12.0px;
        |  font-weight: bold;
        |}
        |.plt_legend text {
        |
        |  font-size: 10.0px;
        |}
        |
        |  </style>
        """.trimMargin()

val EXPECTED_SINGLE_PLOT_SVG = """
        |<svg xmlns="http://www.w3.org/2000/svg" class="plt-container" width="400.0" height="300.0">
        |$SINGLE_PLOT_STYLE_ELEMENT
        |  <rect class="plt-backdrop" width="100%" height="100%">
        |  </rect>
        |  <g class="plt-plot">
        |    <g transform="translate(20.0 10.0 ) ">
        |      <g transform="translate(29.1 251.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(115.46612903225807 0.0 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="0.0" y2="6.0">
        |          </line>
        |          <g transform="translate(0.0 9.0 ) ">
        |            <text style="fill:#000000;" text-anchor="middle" dy="0.7em">
        |            Lunch
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(225.43387096774194 0.0 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="0.0" y2="6.0">
        |          </line>
        |          <g transform="translate(0.0 9.0 ) ">
        |            <text style="fill:#000000;" text-anchor="middle" dy="0.7em">
        |            Dinner
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="340.9" y2="0.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(29.1 0.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(0.0 251.0 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            0.0
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 209.85245901639342 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            0.5
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 168.70491803278688 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            1.0
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 127.55737704918032 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            1.5
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 86.40983606557376 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            2.0
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 45.26229508196718 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            2.5
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 4.114754098360635 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            3.0
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="0.0" y2="251.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(29.1 0.0 ) " clip-path="url(#clip-0)" clip-bounds-jfx="[rect (0.0, 0.0), (340.9, 251.0)]">
        |        <defs>
        |          <clipPath id="clip-0">
        |            <rect x="0.0" y="0.0" width="340.9" height="251.0">
        |            </rect>
        |          </clipPath>
        |        </defs>
        |        <rect x="175.94838709677418" y="4.114754098360635" height="246.88524590163937" width="98.97096774193545" stroke="rgb(0,0,0)" stroke-opacity="0.0" fill="rgb(0,0,128)" fill-opacity="1.0" stroke-width="1.0">
        |        </rect>
        |        <rect x="65.98064516129033" y="86.40983606557376" height="164.59016393442624" width="98.97096774193547" stroke="rgb(0,0,0)" stroke-opacity="0.0" fill="rgb(0,0,128)" fill-opacity="1.0" stroke-width="1.0">
        |        </rect>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(4.0 135.5 ) rotate(-90.0 ) " class="plt-axis-title">
        |        <text text-anchor="middle" dy="0.7em">
        |        count
        |        </text>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(219.54999999999998 296.0 ) " class="plt-axis-title">
        |        <text text-anchor="middle">
        |        time
        |        </text>
        |      </g>
        |    </g>
        |  </g>
        |</svg>
        """.trimMargin()

fun expectedSingleBunchItemSvg(index: Int) = """
        |<svg xmlns="http://www.w3.org/2000/svg" class="plt-container" width="150.0" height="150.0">
        |$SINGLE_PLOT_STYLE_ELEMENT
        |  <rect class="plt-backdrop" width="100%" height="100%">
        |  </rect>
        |  <g class="plt-plot">
        |    <g transform="translate(20.0 10.0 ) ">
        |      <g transform="translate(15.7 101.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(4.3495867768595105 0.0 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="0.0" y2="6.0">
        |          </line>
        |          <g transform="translate(0.0 9.0 ) ">
        |            <text style="fill:#000000;" text-anchor="middle" dy="0.7em">
        |            1
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(91.34132231404958 0.0 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="0.0" y2="6.0">
        |          </line>
        |          <g transform="translate(0.0 9.0 ) ">
        |            <text style="fill:#000000;" text-anchor="middle" dy="0.7em">
        |            2
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="95.69090909090906" y2="0.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(15.7 0.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(0.0 96.40909090909092 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            0
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 65.80303030303031 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            1
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 35.1969696969697 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            2
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 4.5909090909090935 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            3
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="0.0" y2="101.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(15.7 0.0 ) " clip-path="url(#clip-$index)" clip-bounds-jfx="[rect (0.0, 0.0), (95.69090909090906, 101.0)]">
        |        <defs>
        |          <clipPath id="clip-$index">
        |            <rect x="0.0" y="0.0" width="95.69090909090906" height="101.0">
        |            </rect>
        |          </clipPath>
        |        </defs>
        |        <g>
        |          
        |          <g >
        |            <circle fill="#000080" stroke="#000080" stroke-opacity="0.0" stroke-width="0.0" cx="4.3495867768595105" cy="96.40909090909092" r="2.2" />
        |            <circle fill="#000080" stroke="#000080" stroke-opacity="0.0" stroke-width="0.0" cx="91.34132231404958" cy="4.5909090909090935" r="2.2" />
        |          </g>
        |        </g>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(4.0 60.5 ) rotate(-90.0 ) " class="plt-axis-title">
        |        <text text-anchor="middle" dy="0.7em">
        |        y
        |        </text>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(83.54545454545453 146.0 ) " class="plt-axis-title">
        |        <text text-anchor="middle">
        |        x
        |        </text>
        |      </g>
        |    </g>
        |  </g>
        |</svg>
        """.trimMargin()

val EXPECTED_BUNCH_SVG = """
        |<svg xmlns="http://www.w3.org/2000/svg" class="plt-container" width="300.0" height="150.0">
        |<style type="text/css">
        |
        |  .plt-container {
        |	font-family: "Lucida Grande", sans-serif;
        |	cursor: crosshair;
        |	user-select: none;
        |	-webkit-user-select: none;
        |	-moz-user-select: none;
        |	-ms-user-select: none;
        |}
        |.plt-backdrop {
        |   fill: white;
        |}
        |.plt-transparent .plt-backdrop {
        |   visibility: hidden;
        |}
        |text {
        |	font-size: 12px;
        |	fill: #3d3d3d;
        |}
        |.plt-data-tooltip text {
        |	font-size: 12px;
        |}
        |.plt-axis-tooltip text {
        |	font-size: 10px;
        |}
        |.plt-axis line {
        |	shape-rendering: crispedges;
        |}
        |.plt-plot-title {
        |
        |  font-size: 16.0px;
        |  font-weight: bold;
        |}
        |.plt-axis .tick text {
        |
        |  font-size: 10.0px;
        |}
        |.plt-axis.small-tick-font .tick text {
        |
        |  font-size: 8.0px;
        |}
        |.plt-axis-title text {
        |
        |  font-size: 12.0px;
        |}
        |.plt_legend .legend-title text {
        |
        |  font-size: 12.0px;
        |  font-weight: bold;
        |}
        |.plt_legend text {
        |
        |  font-size: 10.0px;
        |}
        |
        |  
        |</style>
        |<g transform="translate(0.0 0.0)" class="plt-plot">
        |    <g transform="translate(20.0 10.0 ) ">
        |      <g transform="translate(15.7 101.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(4.3495867768595105 0.0 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="0.0" y2="6.0">
        |          </line>
        |          <g transform="translate(0.0 9.0 ) ">
        |            <text style="fill:#000000;" text-anchor="middle" dy="0.7em">
        |            1
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(91.34132231404958 0.0 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="0.0" y2="6.0">
        |          </line>
        |          <g transform="translate(0.0 9.0 ) ">
        |            <text style="fill:#000000;" text-anchor="middle" dy="0.7em">
        |            2
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="95.69090909090906" y2="0.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(15.7 0.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(0.0 96.40909090909092 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            0
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 65.80303030303031 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            1
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 35.1969696969697 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            2
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 4.5909090909090935 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            3
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="0.0" y2="101.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(15.7 0.0 ) " clip-path="url(#clip-0)" clip-bounds-jfx="[rect (0.0, 0.0), (95.69090909090906, 101.0)]">
        |        <defs>
        |          <clipPath id="clip-0">
        |            <rect x="0.0" y="0.0" width="95.69090909090906" height="101.0">
        |            </rect>
        |          </clipPath>
        |        </defs>
        |        <g>
        |          
        |          <g >
        |            <circle fill="#000080" stroke="#000080" stroke-opacity="0.0" stroke-width="0.0" cx="4.3495867768595105" cy="96.40909090909092" r="2.2" />
        |            <circle fill="#000080" stroke="#000080" stroke-opacity="0.0" stroke-width="0.0" cx="91.34132231404958" cy="4.5909090909090935" r="2.2" />
        |          </g>
        |        </g>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(4.0 60.5 ) rotate(-90.0 ) " class="plt-axis-title">
        |        <text text-anchor="middle" dy="0.7em">
        |        y
        |        </text>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(83.54545454545453 146.0 ) " class="plt-axis-title">
        |        <text text-anchor="middle">
        |        x
        |        </text>
        |      </g>
        |    </g>
        |  </g>
        |
        |<g transform="translate(150.0 0.0)" class="plt-plot">
        |    <g transform="translate(20.0 10.0 ) ">
        |      <g transform="translate(15.7 101.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(4.3495867768595105 0.0 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="0.0" y2="6.0">
        |          </line>
        |          <g transform="translate(0.0 9.0 ) ">
        |            <text style="fill:#000000;" text-anchor="middle" dy="0.7em">
        |            1
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(91.34132231404958 0.0 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="0.0" y2="6.0">
        |          </line>
        |          <g transform="translate(0.0 9.0 ) ">
        |            <text style="fill:#000000;" text-anchor="middle" dy="0.7em">
        |            2
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="95.69090909090906" y2="0.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(15.7 0.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(0.0 96.40909090909092 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            0
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 65.80303030303031 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            1
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 35.1969696969697 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            2
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 4.5909090909090935 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            3
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="0.0" y2="101.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(15.7 0.0 ) " clip-path="url(#clip-1)" clip-bounds-jfx="[rect (0.0, 0.0), (95.69090909090906, 101.0)]">
        |        <defs>
        |          <clipPath id="clip-1">
        |            <rect x="0.0" y="0.0" width="95.69090909090906" height="101.0">
        |            </rect>
        |          </clipPath>
        |        </defs>
        |        <g>
        |          
        |          <g >
        |            <circle fill="#000080" stroke="#000080" stroke-opacity="0.0" stroke-width="0.0" cx="4.3495867768595105" cy="96.40909090909092" r="2.2" />
        |            <circle fill="#000080" stroke="#000080" stroke-opacity="0.0" stroke-width="0.0" cx="91.34132231404958" cy="4.5909090909090935" r="2.2" />
        |          </g>
        |        </g>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(4.0 60.5 ) rotate(-90.0 ) " class="plt-axis-title">
        |        <text text-anchor="middle" dy="0.7em">
        |        y
        |        </text>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(83.54545454545453 146.0 ) " class="plt-axis-title">
        |        <text text-anchor="middle">
        |        x
        |        </text>
        |      </g>
        |    </g>
        |  </g>
        |
        |</svg>
        """.trimMargin()