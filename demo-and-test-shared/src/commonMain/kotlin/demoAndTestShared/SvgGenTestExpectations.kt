/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demoAndTestShared


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
        |	font-size: 13px;
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
        |      <g transform="translate(33.13 247.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(113.08500000000002 0.0 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="0.0" y2="4.0">
        |          </line>
        |          <g transform="translate(0.0 7.0 ) ">
        |            <text style="fill:#000000;" text-anchor="middle" dy="0.7em">
        |            Lunch
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(220.78500000000003 0.0 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="0.0" y2="4.0">
        |          </line>
        |          <g transform="translate(0.0 7.0 ) ">
        |            <text style="fill:#000000;" text-anchor="middle" dy="0.7em">
        |            Dinner
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="333.87" y2="0.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(33.13 0.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(0.0 247.0 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-4.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-7.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            0.0
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 207.79365079365078 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-4.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-7.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            0.5
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 168.58730158730157 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-4.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-7.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            1.0
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 129.38095238095238 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-4.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-7.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            1.5
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 90.17460317460316 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-4.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-7.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            2.0
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 50.96825396825395 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-4.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-7.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            2.5
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 11.76190476190476 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-4.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-7.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            3.0
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="0.0" y2="247.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(33.13 0.0 ) " clip-path="url(#clip-0)" clip-bounds-jfx="[rect (0.0, 0.0), (333.87, 247.0)]">
        |        <defs>
        |          <clipPath id="clip-0">
        |            <rect x="0.0" y="0.0" width="333.87" height="247.0">
        |            </rect>
        |          </clipPath>
        |        </defs>
        |        <rect x="172.32000000000002" y="11.76190476190476" height="235.23809523809524" width="96.93000000000004" stroke="rgb(0,0,0)" stroke-opacity="0.0" fill="rgb(17,142,216)" fill-opacity="1.0" stroke-width="1.0">
        |        </rect>
        |        <rect x="64.62" y="90.17460317460316" height="156.82539682539684" width="96.93000000000004" stroke="rgb(0,0,0)" stroke-opacity="0.0" fill="rgb(17,142,216)" fill-opacity="1.0" stroke-width="1.0">
        |        </rect>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(4.0 133.5 ) rotate(-90.0 ) " class="plt-axis-title">
        |        <text text-anchor="middle" dy="0.7em">
        |        count
        |        </text>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(223.065 296.0 ) " class="plt-axis-title">
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
        |	font-size: 13px;
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
        |      <g transform="translate(15.71 97.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(4.604090909090914 0.0 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="0.0" y2="4.0">
        |          </line>
        |          <g transform="translate(0.0 7.0 ) ">
        |            <text style="fill:#000000;" text-anchor="middle" dy="0.7em">
        |            1
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="101.28999999999999" y2="0.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(15.71 0.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(0.0 92.5909090909091 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-4.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-7.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            0
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 33.803030303030305 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-4.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-7.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            2
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="0.0" y2="97.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(15.71 0.0 ) " clip-path="url(#clip-0)" clip-bounds-jfx="[rect (0.0, 0.0), (101.28999999999999, 97.0)]">
        |        <defs>
        |          <clipPath id="clip-0">
        |            <rect x="0.0" y="0.0" width="101.28999999999999" height="97.0">
        |            </rect>
        |          </clipPath>
        |        </defs>
        |        <g>
        |          
        |          <g >
        |            <circle fill="#118ed8" stroke="#118ed8" stroke-opacity="0.0" stroke-width="0.0" cx="4.604090909090914" cy="92.5909090909091" r="2.2" />
        |            <circle fill="#118ed8" stroke="#118ed8" stroke-opacity="0.0" stroke-width="0.0" cx="96.6859090909091" cy="4.4090909090909065" r="2.2" />
        |          </g>
        |        </g>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(4.0 58.5 ) rotate(-90.0 ) " class="plt-axis-title">
        |        <text text-anchor="middle" dy="0.7em">
        |        b
        |        </text>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(89.35499999999999 146.0 ) " class="plt-axis-title">
        |        <text text-anchor="middle">
        |        a
        |        </text>
        |      </g>
        |    </g>
        |  </g>
        |
        |<g transform="translate(150.0 0.0)" class="plt-plot">
        |    <g transform="translate(23.0 10.0 ) ">
        |      <g transform="translate(15.71 97.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(4.604090909090914 0.0 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="0.0" y2="4.0">
        |          </line>
        |          <g transform="translate(0.0 7.0 ) ">
        |            <text style="fill:#000000;" text-anchor="middle" dy="0.7em">
        |            1
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="101.28999999999999" y2="0.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(15.71 0.0 ) " class="plt-axis">
        |        <g class="tick" transform="translate(0.0 92.5909090909091 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-4.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-7.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            0
        |            </text>
        |          </g>
        |        </g>
        |        <g class="tick" transform="translate(0.0 33.803030303030305 ) ">
        |          <line stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0" x2="-4.0" y2="0.0">
        |          </line>
        |          <g transform="translate(-7.0 0.0 ) ">
        |            <text style="fill:#000000;" text-anchor="end" dy="0.35em">
        |            2
        |            </text>
        |          </g>
        |        </g>
        |        <line x1="0.0" y1="0.0" x2="0.0" y2="97.0" stroke-width="1.0" stroke="rgb(0,0,0)" stroke-opacity="1.0">
        |        </line>
        |      </g>
        |      <g transform="translate(15.71 0.0 ) " clip-path="url(#clip-1)" clip-bounds-jfx="[rect (0.0, 0.0), (101.28999999999999, 97.0)]">
        |        <defs>
        |          <clipPath id="clip-1">
        |            <rect x="0.0" y="0.0" width="101.28999999999999" height="97.0">
        |            </rect>
        |          </clipPath>
        |        </defs>
        |        <g>
        |          
        |          <g >
        |            <circle fill="#118ed8" stroke="#118ed8" stroke-opacity="0.0" stroke-width="0.0" cx="4.604090909090914" cy="92.5909090909091" r="2.2" />
        |            <circle fill="#118ed8" stroke="#118ed8" stroke-opacity="0.0" stroke-width="0.0" cx="96.6859090909091" cy="4.4090909090909065" r="2.2" />
        |          </g>
        |        </g>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(4.0 58.5 ) rotate(-90.0 ) " class="plt-axis-title">
        |        <text text-anchor="middle" dy="0.7em">
        |        b
        |        </text>
        |      </g>
        |    </g>
        |    <g class="plt-axis">
        |      <g transform="translate(89.35499999999999 146.0 ) " class="plt-axis-title">
        |        <text text-anchor="middle">
        |        a
        |        </text>
        |      </g>
        |    </g>
        |  </g>
        |
        |</svg>
        """.trimMargin()