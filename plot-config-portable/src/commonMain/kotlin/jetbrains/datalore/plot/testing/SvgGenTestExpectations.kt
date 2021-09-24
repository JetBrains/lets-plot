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
        |	font-size: 15px;
        |	fill: #3d3d3d;
        |	
        |	text-rendering: optimizeLegibility;
        |}
        |.plt-data-tooltip text {
        |	font-size: 15px;
        |}
        |.plt-axis-tooltip text {
        |	font-size: 13px;
        |}
        |.plt-axis line {
        |	shape-rendering: crispedges;
        |}
        |.plt-plot-title {
        |
        |  font-size: 19.0px;
        |  font-weight: bold;
        |}
        |.plt-axis .tick text {
        |
        |  font-size: 13.0px;
        |}
        |.plt-axis.small-tick-font .tick text {
        |
        |  font-size: 11.0px;
        |}
        |.plt-axis-title text {
        |
        |  font-size: 15.0px;
        |}
        |.plt_legend .legend-title text {
        |
        |  font-size: 15.0px;
        |  font-weight: bold;
        |}
        |.plt_legend text {
        |
        |  font-size: 13.0px;
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
        |    <g transform="translate(23.0 10.0 ) ">
        |      <g transform="translate(35.13 245.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(112.4075806451613 0.0 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="0.0" y2="6.0">
        |          </line>
        |          <g transform="translate(0.0 9.0 ) ">
        |            <text style="fill:#000000;" text-anchor="middle" dy="0.7em">
        |            Lunch
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(219.46241935483874 0.0 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="0.0" y2="6.0">
        |          </line>
        |          <g transform="translate(0.0 9.0 ) ">
        |            <text style="fill:#000000;" text-anchor="middle" dy="0.7em">
        |            Dinner
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="331.87" y2="0.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(35.13 0.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(0.0 245.00000000000003 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            0.0
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 206.11111111111114 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            0.5
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 167.22222222222223 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            1.0
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 128.33333333333334 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            1.5
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 89.44444444444446 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            2.0
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 50.55555555555557 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            2.5
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 11.666666666666657 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            3.0
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="0.0" y2="245.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(35.13 0.0 ) " clip-path="url(#clip-0)" clip-bounds-jfx="[rect (0.0, 0.0), (331.87, 245.0)]">
        |        <defs>
        |          <clipPath id="clip-0">
        |            <rect x="0.0" y="0.0" width="331.87" height="245.0">
        |            </rect>
        |          </clipPath>
        |        </defs>
        |        <rect x="171.2877419354839" y="11.666666666666657" height="233.33333333333337" width="96.3493548387097" stroke="rgb(0,0,0)" stroke-opacity="0.0" fill="rgb(17,142,216)" fill-opacity="1.0" stroke-width="1.0">
        |        </rect>
        |        <rect x="64.23290322580647" y="89.44444444444446" height="155.55555555555557" width="96.34935483870967" stroke="rgb(0,0,0)" stroke-opacity="0.0" fill="rgb(17,142,216)" fill-opacity="1.0" stroke-width="1.0">
        |        </rect>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(4.0 132.5 ) rotate(-90.0 ) " class="plt-axis-title">
        |        <text text-anchor="middle" dy="0.7em">
        |        count
        |        </text>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(224.065 296.0 ) " class="plt-axis-title">
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
        |        b
        |        </text>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(83.54545454545453 146.0 ) " class="plt-axis-title">
        |        <text text-anchor="middle">
        |        a
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
        |	font-size: 15px;
        |	fill: #3d3d3d;
        |	
        |	text-rendering: optimizeLegibility;
        |}
        |.plt-data-tooltip text {
        |	font-size: 15px;
        |}
        |.plt-axis-tooltip text {
        |	font-size: 13px;
        |}
        |.plt-axis line {
        |	shape-rendering: crispedges;
        |}
        |.plt-plot-title {
        |
        |  font-size: 19.0px;
        |  font-weight: bold;
        |}
        |.plt-axis .tick text {
        |
        |  font-size: 13.0px;
        |}
        |.plt-axis.small-tick-font .tick text {
        |
        |  font-size: 11.0px;
        |}
        |.plt-axis-title text {
        |
        |  font-size: 15.0px;
        |}
        |.plt_legend .legend-title text {
        |
        |  font-size: 15.0px;
        |  font-weight: bold;
        |}
        |.plt_legend text {
        |
        |  font-size: 13.0px;
        |}
        |
        |  
        |</style>
        |<g transform="translate(0.0 0.0)" class="plt-plot">
        |    <g transform="translate(23.0 10.0 ) ">
        |      <g transform="translate(17.71 95.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(4.51318181818182 0.0 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="0.0" y2="6.0">
        |          </line>
        |          <g transform="translate(0.0 9.0 ) ">
        |            <text style="fill:#000000;" text-anchor="middle" dy="0.7em">
        |            1
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="99.28999999999999" y2="0.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(17.71 0.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(0.0 90.68181818181819 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            0
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 33.10606060606061 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            2
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="0.0" y2="95.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(17.71 0.0 ) " clip-path="url(#clip-0)" clip-bounds-jfx="[rect (0.0, 0.0), (99.28999999999999, 95.0)]">
        |        <defs>
        |          <clipPath id="clip-0">
        |            <rect x="0.0" y="0.0" width="99.28999999999999" height="95.0">
        |            </rect>
        |          </clipPath>
        |        </defs>
        |        <g>
        |          
        |          <g >
        |            <circle fill="#118ed8" stroke="#118ed8" stroke-opacity="0.0" stroke-width="0.0" cx="4.51318181818182" cy="90.68181818181819" r="2.2" />
        |            <circle fill="#118ed8" stroke="#118ed8" stroke-opacity="0.0" stroke-width="0.0" cx="94.77681818181819" cy="4.318181818181813" r="2.2" />
        |          </g>
        |        </g>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(4.0 57.5 ) rotate(-90.0 ) " class="plt-axis-title">
        |        <text text-anchor="middle" dy="0.7em">
        |        b
        |        </text>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(90.35499999999999 146.0 ) " class="plt-axis-title">
        |        <text text-anchor="middle">
        |        a
        |        </text>
        |      </g>
        |    </g>
        |  </g>
        |
        |<g transform="translate(150.0 0.0)" class="plt-plot">
        |    <g transform="translate(23.0 10.0 ) ">
        |      <g transform="translate(17.71 95.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(4.51318181818182 0.0 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="0.0" y2="6.0">
        |          </line>
        |          <g transform="translate(0.0 9.0 ) ">
        |            <text style="fill:#000000;" text-anchor="middle" dy="0.7em">
        |            1
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="99.28999999999999" y2="0.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(17.71 0.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(0.0 90.68181818181819 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            0
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 33.10606060606061 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-6.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-9.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            2
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="0.0" y2="95.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(17.71 0.0 ) " clip-path="url(#clip-1)" clip-bounds-jfx="[rect (0.0, 0.0), (99.28999999999999, 95.0)]">
        |        <defs>
        |          <clipPath id="clip-1">
        |            <rect x="0.0" y="0.0" width="99.28999999999999" height="95.0">
        |            </rect>
        |          </clipPath>
        |        </defs>
        |        <g>
        |          
        |          <g >
        |            <circle fill="#118ed8" stroke="#118ed8" stroke-opacity="0.0" stroke-width="0.0" cx="4.51318181818182" cy="90.68181818181819" r="2.2" />
        |            <circle fill="#118ed8" stroke="#118ed8" stroke-opacity="0.0" stroke-width="0.0" cx="94.77681818181819" cy="4.318181818181813" r="2.2" />
        |          </g>
        |        </g>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(4.0 57.5 ) rotate(-90.0 ) " class="plt-axis-title">
        |        <text text-anchor="middle" dy="0.7em">
        |        b
        |        </text>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(90.35499999999999 146.0 ) " class="plt-axis-title">
        |        <text text-anchor="middle">
        |        a
        |        </text>
        |      </g>
        |    </g>
        |  </g>
        |
        |</svg>
        """.trimMargin()