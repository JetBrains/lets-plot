/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import demoAndTestShared.parsePlotSpec
import kotlin.test.Test

class LatexTest : VisualPlotTestBase() {
    @Test
    fun `latex symbols`() {
        val spec = """
            {
              "data": {
                "x": [
                  0.0,
                  1.0,
                  2.0,
                  3.0,
                  4.0,
                  5.0,
                  6.0,
                  7.0,
                  8.0,
                  9.0,
                  10.0,
                  11.0,
                  0.0,
                  1.0,
                  2.0,
                  3.0,
                  4.0,
                  5.0,
                  6.0,
                  7.0,
                  8.0,
                  9.0,
                  10.0,
                  11.0,
                  0.0,
                  1.0,
                  2.0,
                  3.0,
                  4.0,
                  5.0,
                  6.0,
                  7.0,
                  8.0,
                  9.0,
                  10.0,
                  11.0,
                  0.0,
                  1.0,
                  2.0,
                  3.0,
                  4.0,
                  5.0,
                  6.0,
                  7.0,
                  8.0,
                  9.0,
                  10.0,
                  11.0,
                  0.0,
                  1.0,
                  2.0,
                  3.0,
                  4.0,
                  5.0,
                  6.0,
                  7.0,
                  8.0
                ],
                "y": [
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  1.0,
                  1.0,
                  1.0,
                  1.0,
                  1.0,
                  1.0,
                  1.0,
                  1.0,
                  1.0,
                  1.0,
                  1.0,
                  1.0,
                  2.0,
                  2.0,
                  2.0,
                  2.0,
                  2.0,
                  2.0,
                  2.0,
                  2.0,
                  2.0,
                  2.0,
                  2.0,
                  2.0,
                  3.0,
                  3.0,
                  3.0,
                  3.0,
                  3.0,
                  3.0,
                  3.0,
                  3.0,
                  3.0,
                  3.0,
                  3.0,
                  3.0,
                  4.0,
                  4.0,
                  4.0,
                  4.0,
                  4.0,
                  4.0,
                  4.0,
                  4.0,
                  4.0
                ],
                "label": [
                  "\\( \\Alpha \\)",
                  "\\( \\Beta \\)",
                  "\\( \\Gamma \\)",
                  "\\( \\Delta \\)",
                  "\\( \\Epsilon \\)",
                  "\\( \\Zeta \\)",
                  "\\( \\Eta \\)",
                  "\\( \\Theta \\)",
                  "\\( \\Iota \\)",
                  "\\( \\Kappa \\)",
                  "\\( \\Lambda \\)",
                  "\\( \\Mu \\)",
                  "\\( \\Nu \\)",
                  "\\( \\Xi \\)",
                  "\\( \\Omicron \\)",
                  "\\( \\Pi \\)",
                  "\\( \\Rho \\)",
                  "\\( \\Sigma \\)",
                  "\\( \\Tau \\)",
                  "\\( \\Upsilon \\)",
                  "\\( \\Phi \\)",
                  "\\( \\Chi \\)",
                  "\\( \\Psi \\)",
                  "\\( \\Omega \\)",
                  "\\( \\alpha \\)",
                  "\\( \\beta \\)",
                  "\\( \\gamma \\)",
                  "\\( \\delta \\)",
                  "\\( \\epsilon \\)",
                  "\\( \\zeta \\)",
                  "\\( \\eta \\)",
                  "\\( \\theta \\)",
                  "\\( \\iota \\)",
                  "\\( \\kappa \\)",
                  "\\( \\lambda \\)",
                  "\\( \\mu \\)",
                  "\\( \\nu \\)",
                  "\\( \\xi \\)",
                  "\\( \\omicron \\)",
                  "\\( \\pi \\)",
                  "\\( \\rho \\)",
                  "\\( \\sigma \\)",
                  "\\( \\tau \\)",
                  "\\( \\upsilon \\)",
                  "\\( \\phi \\)",
                  "\\( \\chi \\)",
                  "\\( \\psi \\)",
                  "\\( \\omega \\)",
                  "\\( \\pm \\)",
                  "\\( \\mp \\)",
                  "\\( \\times \\)",
                  "\\( \\div \\)",
                  "\\( \\cdot \\)",
                  "\\( \\leq \\)",
                  "\\( \\geq \\)",
                  "\\( \\neq \\)",
                  "\\( \\infty \\)"
                ]
              },
              "mapping": {
                "x": "x",
                "y": "y"
              },
              "data_meta": {
                "series_annotations": [
                  {
                    "type": "int",
                    "column": "x"
                  },
                  {
                    "type": "int",
                    "column": "y"
                  },
                  {
                    "type": "str",
                    "column": "label"
                  }
                ]
              },
              "theme": {
                "name": "classic",
                "line": "blank",
                "axis": "blank"
              },
              "kind": "plot",
              "scales": [
                {
                  "aesthetic": "y",
                  "trans": "reverse"
                }
              ],
              "layers": [
                {
                  "geom": "text",
                  "mapping": {
                    "label": "label"
                  },
                  "data_meta": {},
                  "size": 12.0
                }
              ],
              "metainfo_list": []
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("latex_symbols.png", plotSpec)
    }

    @Test
    fun `geom_label renders latex formulas for different hjust values`() {
        val spec = """
            {
              "kind": "subplots",
              "layout": {
                "ncol": 3.0,
                "nrow": 16.0,
                "name": "grid"
              },
              "figures": [
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\Alpha + \\alpha^{\\Beta + \\beta^{\\Gamma + \\gamma_{\\Delta + \\delta + \\Delta} + \\Gamma} + \\Beta} + \\Alpha\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\Alpha + \\alpha^{\\Beta + \\beta^{\\Gamma + \\gamma_{\\Delta + \\delta + \\Delta} + \\Gamma} + \\Beta} + \\Alpha\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\Alpha + \\alpha^{\\Beta + \\beta^{\\Gamma + \\gamma_{\\Delta + \\delta + \\Delta} + \\Gamma} + \\Beta} + \\Alpha\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\((\\eta + a^{d})(\\eta - a^d)\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\((\\eta + a^{d})(\\eta - a^d)\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\((\\eta + a^{d})(\\eta - a^d)\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^\\infty\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^\\infty\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^\\infty\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\((1 + (a^2 + 2))(b^{2\\theta - 1} - (c^{-1} / \\chi_i))\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\((1 + (a^2 + 2))(b^{2\\theta - 1} - (c^{-1} / \\chi_i))\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\((1 + (a^2 + 2))(b^{2\\theta - 1} - (c^{-1} / \\chi_i))\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(a + \\frac{b}{c}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(a + \\frac{b}{c}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(a + \\frac{b}{c}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + c\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + c\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + c\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + \\frac{c}{d}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + \\frac{c}{d}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + \\frac{c}{d}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\frac{a + b}{d} + \\frac{d}{e + f}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\frac{a + b}{d} + \\frac{d}{e + f}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\frac{a + b}{d} + \\frac{d}{e + f}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b + c} + \\frac{d + e}{f}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b + c} + \\frac{d + e}{f}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b + c} + \\frac{d + e}{f}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(10^{1000} - \\frac{\\Alpha^\\beta - \\gamma_\\Delta}{\\Omega} - \\frac{\\omega}{\\alpha_\\Beta - \\Gamma^\\delta} - 1000^{10}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(10^{1000} - \\frac{\\Alpha^\\beta - \\gamma_\\Delta}{\\Omega} - \\frac{\\omega}{\\alpha_\\Beta - \\Gamma^\\delta} - 1000^{10}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(10^{1000} - \\frac{\\Alpha^\\beta - \\gamma_\\Delta}{\\Omega} - \\frac{\\omega}{\\alpha_\\Beta - \\Gamma^\\delta} - 1000^{10}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\alpha^\\frac{a + b}{c} + \\beta_{\\frac{d}{e + f}}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\alpha^\\frac{a + b}{c} + \\beta_{\\frac{d}{e + f}}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(\\alpha^\\frac{a + b}{c} + \\beta_{\\frac{d}{e + f}}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(A^{\\frac{a^2}{b_1 + c_1} + \\frac{d_1 + e_1}{f^2}} + \\frac{B + C^2}{D^2 + E}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(A^{\\frac{a^2}{b_1 + c_1} + \\frac{d_1 + e_1}{f^2}} + \\frac{B + C^2}{D^2 + E}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(A^{\\frac{a^2}{b_1 + c_1} + \\frac{d_1 + e_1}{f^2}} + \\frac{B + C^2}{D^2 + E}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2} + 1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2} + 1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\(1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2} + 1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2}\\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "ABC & \\(a + \\frac{b + c + d}{e} + f\\) & DEF",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "ABC & \\(a + \\frac{b + c + d}{e} + f\\) & DEF",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "ABC & \\(a + \\frac{b + c + d}{e} + f\\) & DEF",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "<a href=\"https://github.com\">GitHub</a> & \\(a + \\frac{b + c + d}{e} + f\\) & <a href=\"https://github.com\">GitHub</a>",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "<a href=\"https://github.com\">GitHub</a> & \\(a + \\frac{b + c + d}{e} + f\\) & <a href=\"https://github.com\">GitHub</a>",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "<a href=\"https://github.com\">GitHub</a> & \\(a + \\frac{b + c + d}{e} + f\\) & <a href=\"https://github.com\">GitHub</a>",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                }
              ]
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("geom_label_renders_latex_formulas_for_different_hjust_values_test.png", plotSpec)
    }

    @Test
    fun `geom_label renders regression latex formulas for different hjust values`() {
        val spec = """
            {
              "ggsize": {
                "width": 1600.0,
                "height": 600.0
              },
              "kind": "subplots",
              "layout": {
                "ncol": 3.0,
                "nrow": 3.0,
                "name": "grid"
              },
              "figures": [
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\( A^{a + \\frac{b}{c} } \\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\( A^{a + \\frac{b}{c} } \\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\( A^{a + \\frac{b}{c} } \\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\( A^{a + \\frac{b}{c} } + \\frac{B}{C} \\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\( A^{a + \\frac{b}{c} } + \\frac{B}{C} \\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\( A^{a + \\frac{b}{c} } + \\frac{B}{C} \\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\( a^{b^{\\frac{c}{d} + C} + B} + A \\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\( a^{b^{\\frac{c}{d} + C} + B} + A \\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\( a^{b^{\\frac{c}{d} + C} + B} + A \\)",
                      "size": 10.0,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                }
              ]
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("geom_label_renders_regression_latex_formulas_for_different_hjust_values.png", plotSpec)
    }

    @Test
    fun `geom_label renders latex formulas for different nudge_x values`() {
        val spec = """
            {
              "kind": "subplots",
              "layout": {
                "ncol": 3.0,
                "nrow": 16.0,
                "name": "grid"
              },
              "figures": [
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "x": 0.0,
                      "label": "\\(\\Alpha + \\alpha^{\\Beta + \\beta^{\\Gamma + \\gamma_{\\Delta + \\delta + \\Delta} + \\Gamma} + \\Beta} + \\Alpha\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "x": 0.0,
                      "label": "\\(\\Alpha + \\alpha^{\\Beta + \\beta^{\\Gamma + \\gamma_{\\Delta + \\delta + \\Delta} + \\Gamma} + \\Beta} + \\Alpha\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "x": 0.0,
                      "label": "\\(\\Alpha + \\alpha^{\\Beta + \\beta^{\\Gamma + \\gamma_{\\Delta + \\delta + \\Delta} + \\Gamma} + \\Beta} + \\Alpha\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "x": 0.0,
                      "label": "\\((\\eta + a^{d})(\\eta - a^d)\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "x": 0.0,
                      "label": "\\((\\eta + a^{d})(\\eta - a^d)\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "x": 0.0,
                      "label": "\\((\\eta + a^{d})(\\eta - a^d)\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "x": 0.0,
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^\\infty\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "x": 0.0,
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^\\infty\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "x": 0.0,
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^\\infty\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "x": 0.0,
                      "label": "\\((1 + (a^2 + 2))(b^{2\\theta - 1} - (c^{-1} / \\chi_i))\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "x": 0.0,
                      "label": "\\((1 + (a^2 + 2))(b^{2\\theta - 1} - (c^{-1} / \\chi_i))\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "x": 0.0,
                      "label": "\\((1 + (a^2 + 2))(b^{2\\theta - 1} - (c^{-1} / \\chi_i))\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "x": 0.0,
                      "label": "\\(a + \\frac{b}{c}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "x": 0.0,
                      "label": "\\(a + \\frac{b}{c}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "x": 0.0,
                      "label": "\\(a + \\frac{b}{c}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + c\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + c\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + c\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + \\frac{c}{d}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + \\frac{c}{d}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + \\frac{c}{d}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "x": 0.0,
                      "label": "\\(\\frac{a + b}{d} + \\frac{d}{e + f}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "x": 0.0,
                      "label": "\\(\\frac{a + b}{d} + \\frac{d}{e + f}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "x": 0.0,
                      "label": "\\(\\frac{a + b}{d} + \\frac{d}{e + f}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b + c} + \\frac{d + e}{f}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b + c} + \\frac{d + e}{f}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b + c} + \\frac{d + e}{f}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "x": 0.0,
                      "label": "\\(10^{1000} - \\frac{\\Alpha^\\beta - \\gamma_\\Delta}{\\Omega} - \\frac{\\omega}{\\alpha_\\Beta - \\Gamma^\\delta} - 1000^{10}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "x": 0.0,
                      "label": "\\(10^{1000} - \\frac{\\Alpha^\\beta - \\gamma_\\Delta}{\\Omega} - \\frac{\\omega}{\\alpha_\\Beta - \\Gamma^\\delta} - 1000^{10}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "x": 0.0,
                      "label": "\\(10^{1000} - \\frac{\\Alpha^\\beta - \\gamma_\\Delta}{\\Omega} - \\frac{\\omega}{\\alpha_\\Beta - \\Gamma^\\delta} - 1000^{10}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "x": 0.0,
                      "label": "\\(\\alpha^\\frac{a + b}{c} + \\beta_{\\frac{d}{e + f}}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "x": 0.0,
                      "label": "\\(\\alpha^\\frac{a + b}{c} + \\beta_{\\frac{d}{e + f}}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "x": 0.0,
                      "label": "\\(\\alpha^\\frac{a + b}{c} + \\beta_{\\frac{d}{e + f}}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "x": 0.0,
                      "label": "\\(A^{\\frac{a^2}{b_1 + c_1} + \\frac{d_1 + e_1}{f^2}} + \\frac{B + C^2}{D^2 + E}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "x": 0.0,
                      "label": "\\(A^{\\frac{a^2}{b_1 + c_1} + \\frac{d_1 + e_1}{f^2}} + \\frac{B + C^2}{D^2 + E}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "x": 0.0,
                      "label": "\\(A^{\\frac{a^2}{b_1 + c_1} + \\frac{d_1 + e_1}{f^2}} + \\frac{B + C^2}{D^2 + E}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "x": 0.0,
                      "label": "\\(1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2} + 1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "x": 0.0,
                      "label": "\\(1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2} + 1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "x": 0.0,
                      "label": "\\(1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2} + 1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2}\\)",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "x": 0.0,
                      "label": "ABC & \\(a + \\frac{b + c + d}{e} + f\\) & DEF",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "x": 0.0,
                      "label": "ABC & \\(a + \\frac{b + c + d}{e} + f\\) & DEF",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "x": 0.0,
                      "label": "ABC & \\(a + \\frac{b + c + d}{e} + f\\) & DEF",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "x": 0.0,
                      "label": "<a href=\"https://github.com\">GitHub</a> & \\(a + \\frac{b + c + d}{e} + f\\) & <a href=\"https://github.com\">GitHub</a>",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "x": 0.0,
                      "label": "<a href=\"https://github.com\">GitHub</a> & \\(a + \\frac{b + c + d}{e} + f\\) & <a href=\"https://github.com\">GitHub</a>",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "x": 0.0,
                      "label": "<a href=\"https://github.com\">GitHub</a> & \\(a + \\frac{b + c + d}{e} + f\\) & <a href=\"https://github.com\">GitHub</a>",
                      "size": 10.0,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                }
              ]
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("geom_label_renders_latex_formulas_for_different_nudge_x_values_test.png", plotSpec)
    }

    @Test
    fun `geom_text renders latex formulas for different hjust values`() {
        val spec = """
            {
              "kind": "subplots",
              "layout": {
                "ncol": 3.0,
                "nrow": 16.0,
                "name": "grid"
              },
              "figures": [
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\Alpha + \\alpha^{\\Beta + \\beta^{\\Gamma + \\gamma_{\\Delta + \\delta + \\Delta} + \\Gamma} + \\Beta} + \\Alpha\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\Alpha + \\alpha^{\\Beta + \\beta^{\\Gamma + \\gamma_{\\Delta + \\delta + \\Delta} + \\Gamma} + \\Beta} + \\Alpha\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\Alpha + \\alpha^{\\Beta + \\beta^{\\Gamma + \\gamma_{\\Delta + \\delta + \\Delta} + \\Gamma} + \\Beta} + \\Alpha\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\((\\eta + a^{d})(\\eta - a^d)\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\((\\eta + a^{d})(\\eta - a^d)\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\((\\eta + a^{d})(\\eta - a^d)\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^\\infty\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^\\infty\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^\\infty\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\((1 + (a^2 + 2))(b^{2\\theta - 1} - (c^{-1} / \\chi_i))\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\((1 + (a^2 + 2))(b^{2\\theta - 1} - (c^{-1} / \\chi_i))\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\((1 + (a^2 + 2))(b^{2\\theta - 1} - (c^{-1} / \\chi_i))\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(a + \\frac{b}{c}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(a + \\frac{b}{c}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(a + \\frac{b}{c}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + c\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + c\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + c\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + \\frac{c}{d}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + \\frac{c}{d}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + \\frac{c}{d}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a + b}{d} + \\frac{d}{e + f}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a + b}{d} + \\frac{d}{e + f}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a + b}{d} + \\frac{d}{e + f}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b + c} + \\frac{d + e}{f}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b + c} + \\frac{d + e}{f}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b + c} + \\frac{d + e}{f}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(10^{1000} - \\frac{\\Alpha^\\beta - \\gamma_\\Delta}{\\Omega} - \\frac{\\omega}{\\alpha_\\Beta - \\Gamma^\\delta} - 1000^{10}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(10^{1000} - \\frac{\\Alpha^\\beta - \\gamma_\\Delta}{\\Omega} - \\frac{\\omega}{\\alpha_\\Beta - \\Gamma^\\delta} - 1000^{10}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(10^{1000} - \\frac{\\Alpha^\\beta - \\gamma_\\Delta}{\\Omega} - \\frac{\\omega}{\\alpha_\\Beta - \\Gamma^\\delta} - 1000^{10}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\alpha^\\frac{a + b}{c} + \\beta_{\\frac{d}{e + f}}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\alpha^\\frac{a + b}{c} + \\beta_{\\frac{d}{e + f}}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\alpha^\\frac{a + b}{c} + \\beta_{\\frac{d}{e + f}}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(A^{\\frac{a^2}{b_1 + c_1} + \\frac{d_1 + e_1}{f^2}} + \\frac{B + C^2}{D^2 + E}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(A^{\\frac{a^2}{b_1 + c_1} + \\frac{d_1 + e_1}{f^2}} + \\frac{B + C^2}{D^2 + E}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(A^{\\frac{a^2}{b_1 + c_1} + \\frac{d_1 + e_1}{f^2}} + \\frac{B + C^2}{D^2 + E}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2} + 1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2} + 1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2} + 1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2}\\)",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "ABC & \\(a + \\frac{b + c + d}{e} + f\\) & DEF",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "ABC & \\(a + \\frac{b + c + d}{e} + f\\) & DEF",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "ABC & \\(a + \\frac{b + c + d}{e} + f\\) & DEF",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=left"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "<a href=\"https://github.com\">GitHub</a> & \\(a + \\frac{b + c + d}{e} + f\\) & <a href=\"https://github.com\">GitHub</a>",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "left"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=center"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "<a href=\"https://github.com\">GitHub</a> & \\(a + \\frac{b + c + d}{e} + f\\) & <a href=\"https://github.com\">GitHub</a>",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "<a href=\"https://github.com\">GitHub</a> & \\(a + \\frac{b + c + d}{e} + f\\) & <a href=\"https://github.com\">GitHub</a>",
                      "size": 0.5,
                      "color": "blue",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                }
              ]
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("geom_text_renders_latex_formulas_for_different_hjust_values_test.png", plotSpec)
    }

    @Test
    fun `geom_text renders latex formulas for different nudge_x values`() {
        val spec = """
            {
              "kind": "subplots",
              "layout": {
                "ncol": 3.0,
                "nrow": 16.0,
                "name": "grid"
              },
              "figures": [
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\Alpha + \\alpha^{\\Beta + \\beta^{\\Gamma + \\gamma_{\\Delta + \\delta + \\Delta} + \\Gamma} + \\Beta} + \\Alpha\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\Alpha + \\alpha^{\\Beta + \\beta^{\\Gamma + \\gamma_{\\Delta + \\delta + \\Delta} + \\Gamma} + \\Beta} + \\Alpha\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\Alpha + \\alpha^{\\Beta + \\beta^{\\Gamma + \\gamma_{\\Delta + \\delta + \\Delta} + \\Gamma} + \\Beta} + \\Alpha\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\((\\eta + a^{d})(\\eta - a^d)\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\((\\eta + a^{d})(\\eta - a^d)\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\((\\eta + a^{d})(\\eta - a^d)\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^\\infty\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^\\infty\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^\\infty\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\((1 + (a^2 + 2))(b^{2\\theta - 1} - (c^{-1} / \\chi_i))\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\((1 + (a^2 + 2))(b^{2\\theta - 1} - (c^{-1} / \\chi_i))\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\((1 + (a^2 + 2))(b^{2\\theta - 1} - (c^{-1} / \\chi_i))\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(a + \\frac{b}{c}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(a + \\frac{b}{c}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(a + \\frac{b}{c}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + c\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + c\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + c\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + \\frac{c}{d}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + \\frac{c}{d}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b} + \\frac{c}{d}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a + b}{d} + \\frac{d}{e + f}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a + b}{d} + \\frac{d}{e + f}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a + b}{d} + \\frac{d}{e + f}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b + c} + \\frac{d + e}{f}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b + c} + \\frac{d + e}{f}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\frac{a}{b + c} + \\frac{d + e}{f}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(10^{1000} - \\frac{\\Alpha^\\beta - \\gamma_\\Delta}{\\Omega} - \\frac{\\omega}{\\alpha_\\Beta - \\Gamma^\\delta} - 1000^{10}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(10^{1000} - \\frac{\\Alpha^\\beta - \\gamma_\\Delta}{\\Omega} - \\frac{\\omega}{\\alpha_\\Beta - \\Gamma^\\delta} - 1000^{10}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(10^{1000} - \\frac{\\Alpha^\\beta - \\gamma_\\Delta}{\\Omega} - \\frac{\\omega}{\\alpha_\\Beta - \\Gamma^\\delta} - 1000^{10}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\alpha^\\frac{a + b}{c} + \\beta_{\\frac{d}{e + f}}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\alpha^\\frac{a + b}{c} + \\beta_{\\frac{d}{e + f}}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(\\alpha^\\frac{a + b}{c} + \\beta_{\\frac{d}{e + f}}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(A^{\\frac{a^2}{b_1 + c_1} + \\frac{d_1 + e_1}{f^2}} + \\frac{B + C^2}{D^2 + E}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(A^{\\frac{a^2}{b_1 + c_1} + \\frac{d_1 + e_1}{f^2}} + \\frac{B + C^2}{D^2 + E}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(A^{\\frac{a^2}{b_1 + c_1} + \\frac{d_1 + e_1}{f^2}} + \\frac{B + C^2}{D^2 + E}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2} + 1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2} + 1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "\\(1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2} + 1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2}\\)",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "ABC & \\(a + \\frac{b + c + d}{e} + f\\) & DEF",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "ABC & \\(a + \\frac{b + c + d}{e} + f\\) & DEF",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "ABC & \\(a + \\frac{b + c + d}{e} + f\\) & DEF",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=-0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": -0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "<a href=\"https://github.com\">GitHub</a> & \\(a + \\frac{b + c + d}{e} + f\\) & <a href=\"https://github.com\">GitHub</a>",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.0,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "<a href=\"https://github.com\">GitHub</a> & \\(a + \\frac{b + c + d}{e} + f\\) & <a href=\"https://github.com\">GitHub</a>",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "ggtitle": {
                    "text": "nudge_x=0.2"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "limits": [
                        -0.6,
                        0.6
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "nudge_x": 0.2,
                      "size_unit": "y",
                      "x": 0.0,
                      "label": "<a href=\"https://github.com\">GitHub</a> & \\(a + \\frac{b + c + d}{e} + f\\) & <a href=\"https://github.com\">GitHub</a>",
                      "size": 0.5,
                      "color": "blue"
                    }
                  ],
                  "metainfo_list": []
                }
              ]
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("geom_text_renders_latex_formulas_for_different_nudge_x_values_test.png", plotSpec)
    }

    @Test
    fun `plot title renders latex formulas for different hjust values`() {
        val spec = """
            {
              "ggsize": {
                "width": 1600.0,
                "height": 3200.0
              },
              "kind": "subplots",
              "layout": {
                "ncol": 3.0,
                "nrow": 20.0,
                "name": "grid"
              },
              "figures": [
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\Alpha + \\alpha^{\\Beta + \\beta^{\\Gamma + \\gamma_{\\Delta + \\delta + \\Delta} + \\Gamma} + \\Beta} + \\Alpha\\)",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\Alpha + \\alpha^{\\Beta + \\beta^{\\Gamma + \\gamma_{\\Delta + \\delta + \\Delta} + \\Gamma} + \\Beta} + \\Alpha\\)",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\Alpha + \\alpha^{\\Beta + \\beta^{\\Gamma + \\gamma_{\\Delta + \\delta + \\Delta} + \\Gamma} + \\Beta} + \\Alpha\\)",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\((\\eta + a^{d})(\\eta - a^d)\\)",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\((\\eta + a^{d})(\\eta - a^d)\\)",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\((\\eta + a^{d})(\\eta - a^d)\\)",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^\\infty\\)",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^\\infty\\)",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^\\infty\\)",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\((1 + (a^2 + 2))(b^{2\\theta - 1} - (c^{-1} / \\chi_i))\\)",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\((1 + (a^2 + 2))(b^{2\\theta - 1} - (c^{-1} / \\chi_i))\\)",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\((1 + (a^2 + 2))(b^{2\\theta - 1} - (c^{-1} / \\chi_i))\\)",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\frac{a}{b}\\)",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\frac{a}{b}\\)",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\frac{a}{b}\\)",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(a + \\frac{b}{c}\\)",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(a + \\frac{b}{c}\\)",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(a + \\frac{b}{c}\\)",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\frac{a}{b} + c\\)",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\frac{a}{b} + c\\)",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\frac{a}{b} + c\\)",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\frac{a}{b} + \\frac{c}{d}\\)",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\frac{a}{b} + \\frac{c}{d}\\)",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\frac{a}{b} + \\frac{c}{d}\\)",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\frac{a + b}{d} + \\frac{d}{e + f}\\)",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\frac{a + b}{d} + \\frac{d}{e + f}\\)",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\frac{a + b}{d} + \\frac{d}{e + f}\\)",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\frac{a}{b + c} + \\frac{d + e}{f}\\)",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\frac{a}{b + c} + \\frac{d + e}{f}\\)",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\frac{a}{b + c} + \\frac{d + e}{f}\\)",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(10^{1000} - \\frac{\\Alpha^\\beta - \\gamma_\\Delta}{\\Omega} - \\frac{\\omega}{\\alpha_\\Beta - \\Gamma^\\delta} - 1000^{10}\\)",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(10^{1000} - \\frac{\\Alpha^\\beta - \\gamma_\\Delta}{\\Omega} - \\frac{\\omega}{\\alpha_\\Beta - \\Gamma^\\delta} - 1000^{10}\\)",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(10^{1000} - \\frac{\\Alpha^\\beta - \\gamma_\\Delta}{\\Omega} - \\frac{\\omega}{\\alpha_\\Beta - \\Gamma^\\delta} - 1000^{10}\\)",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\alpha^\\frac{a + b}{c} + \\beta_{\\frac{d}{e + f}}\\)",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\alpha^\\frac{a + b}{c} + \\beta_{\\frac{d}{e + f}}\\)",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(\\alpha^\\frac{a + b}{c} + \\beta_{\\frac{d}{e + f}}\\)",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(A^{\\frac{a^2}{b_1 + c_1} + \\frac{d_1 + e_1}{f^2}} + \\frac{B + C^2}{D^2 + E}\\)",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(A^{\\frac{a^2}{b_1 + c_1} + \\frac{d_1 + e_1}{f^2}} + \\frac{B + C^2}{D^2 + E}\\)",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(A^{\\frac{a^2}{b_1 + c_1} + \\frac{d_1 + e_1}{f^2}} + \\frac{B + C^2}{D^2 + E}\\)",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2} + 1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2}\\)",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2} + 1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2}\\)",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2} + 1^{2^{\\frac{3^4 + 3}{3^4 + 3} + 3} + 2}\\)",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "ABC & \\(a + \\frac{b + c + d}{e} + f\\) & DEF",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "ABC & \\(a + \\frac{b + c + d}{e} + f\\) & DEF",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "ABC & \\(a + \\frac{b + c + d}{e} + f\\) & DEF",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "<a href=\"https://github.com\">GitHub</a> & \\(a + \\frac{b + c + d}{e} + f\\) & <a href=\"https://github.com\">GitHub</a>",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "<a href=\"https://github.com\">GitHub</a> & \\(a + \\frac{b + c + d}{e} + f\\) & <a href=\"https://github.com\">GitHub</a>",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "<a href=\"https://github.com\">GitHub</a> & \\(a + \\frac{b + c + d}{e} + f\\) & <a href=\"https://github.com\">GitHub</a>",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "*Markdown* & \\(a + \\frac{b + c + d}{e} + f\\) & *Markdown*",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "*Markdown* & \\(a + \\frac{b + c + d}{e} + f\\) & *Markdown*",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "*Markdown* & \\(a + \\frac{b + c + d}{e} + f\\) & *Markdown*",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "**Markdown** & \\(a + \\frac{b + c + d}{e} + f\\) & **Markdown**",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "**Markdown** & \\(a + \\frac{b + c + d}{e} + f\\) & **Markdown**",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "**Markdown** & \\(a + \\frac{b + c + d}{e} + f\\) & **Markdown**",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "<span style='color:red'>Markdown</span> & \\(a + \\frac{b + c + d}{e} + f\\) & <span style='color:red'>Markdown</span>",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "<span style='color:red'>Markdown</span> & \\(a + \\frac{b + c + d}{e} + f\\) & <span style='color:red'>Markdown</span>",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "<span style='color:red'>Markdown</span> & \\(a + \\frac{b + c + d}{e} + f\\) & <span style='color:red'>Markdown</span>",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.2,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(a + \\frac{b + c + d}{e} + f\\)  \n\\(a + \\frac{b + c + d}{e} + f\\)",
                    "subtitle": "hjust=0.2"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.5,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(a + \\frac{b + c + d}{e} + f\\)  \n\\(a + \\frac{b + c + d}{e} + f\\)",
                    "subtitle": "hjust=0.5"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "classic",
                    "line": "blank",
                    "axis": "blank",
                    "plot_background": {
                      "fill": "#eeeeee",
                      "blank": false
                    },
                    "plot_title": {
                      "markdown": true,
                      "color": "blue",
                      "size": 20.0,
                      "hjust": 0.8,
                      "margin": [
                        10.0,
                        0.0,
                        0.0,
                        0.0
                      ],
                      "blank": false
                    },
                    "plot_subtitle": {
                      "markdown": true,
                      "hjust": 0.5,
                      "blank": false
                    },
                    "plot_caption": {
                      "markdown": true,
                      "hjust": 0.0,
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\(a + \\frac{b + c + d}{e} + f\\)  \n\\(a + \\frac{b + c + d}{e} + f\\)",
                    "subtitle": "hjust=0.8"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "x": 0.0,
                      "color": "rgba(0,0,0,0)"
                    }
                  ],
                  "metainfo_list": []
                }
              ]
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("plot_title_renders_latex_formulas_for_different_hjust_values_test.png", plotSpec)
    }

    @Test
    fun `geom_label renders multiline latex formulas for different line counts and formula combinations`() {
        val spec = """
            {
              "data": {
                "x": [
                  0.0,
                  1.0,
                  2.0,
                  3.0,
                  4.0,
                  5.0,
                  6.0,
                  7.0,
                  8.0,
                  9.0,
                  10.0,
                  11.0,
                  12.0,
                  13.0,
                  0.0,
                  1.0,
                  2.0,
                  3.0,
                  4.0,
                  5.0,
                  6.0,
                  7.0,
                  8.0,
                  9.0,
                  10.0,
                  11.0,
                  12.0,
                  13.0
                ],
                "y": [
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  -1.0,
                  -1.0,
                  -1.0,
                  -1.0,
                  -1.0,
                  -1.0,
                  -1.0,
                  -1.0,
                  -1.0,
                  -1.0,
                  -1.0,
                  -1.0,
                  -1.0,
                  -1.0
                ],
                "l": [
                  "\\( A_{ 1 } \\)\n\\( A_{ 2 } \\)",
                  "\\( A_{ 1 } \\)\n\\( \\frac{ B_{ 2 } }{ C_{ 2 } } \\)",
                  "\\( \\frac{ B_{ 1 } }{ C_{ 1 } } \\)\n\\( A_{ 2 } \\)",
                  "\\( \\frac{ B_{ 1 } }{ C_{ 1 } } \\)\n\\( \\frac{ B_{ 2 } }{ C_{ 2 } } \\)",
                  "\\( A_{ 1 } \\)\n\\( A_{ 2 } \\)\n\\( A_{ 3 } \\)",
                  "\\( A_{ 1 } \\)\n\\( A_{ 2 } \\)\n\\( \\frac{ B_{ 3 } }{ C_{ 3 } } \\)",
                  "\\( A_{ 1 } \\)\n\\( \\frac{ B_{ 2 } }{ C_{ 2 } } \\)\n\\( A_{ 3 } \\)",
                  "\\( A_{ 1 } \\)\n\\( \\frac{ B_{ 2 } }{ C_{ 2 } } \\)\n\\( \\frac{ B_{ 3 } }{ C_{ 3 } } \\)",
                  "\\( \\frac{ B_{ 1 } }{ C_{ 1 } } \\)\n\\( A_{ 2 } \\)\n\\( A_{ 3 } \\)",
                  "\\( \\frac{ B_{ 1 } }{ C_{ 1 } } \\)\n\\( A_{ 2 } \\)\n\\( \\frac{ B_{ 3 } }{ C_{ 3 } } \\)",
                  "\\( \\frac{ B_{ 1 } }{ C_{ 1 } } \\)\n\\( \\frac{ B_{ 2 } }{ C_{ 2 } } \\)\n\\( A_{ 3 } \\)",
                  "\\( \\frac{ B_{ 1 } }{ C_{ 1 } } \\)\n\\( \\frac{ B_{ 2 } }{ C_{ 2 } } \\)\n\\( \\frac{ B_{ 3 } }{ C_{ 3 } } \\)",
                  "\\( A_{ 1 } \\)\n\\( A_{ 2 } \\)\n\\( A_{ 3 } \\)\n\\( A_{ 4 } \\)",
                  "\\( A_{ 1 } \\)\n\\( A_{ 2 } \\)\n\\( A_{ 3 } \\)\n\\( \\frac{ B_{ 4 } }{ C_{ 4 } } \\)",
                  "\\( A_{ 1 } \\)\n\\( A_{ 2 } \\)\n\\( \\frac{ B_{ 3 } }{ C_{ 3 } } \\)\n\\( A_{ 4 } \\)",
                  "\\( A_{ 1 } \\)\n\\( A_{ 2 } \\)\n\\( \\frac{ B_{ 3 } }{ C_{ 3 } } \\)\n\\( \\frac{ B_{ 4 } }{ C_{ 4 } } \\)",
                  "\\( A_{ 1 } \\)\n\\( \\frac{ B_{ 2 } }{ C_{ 2 } } \\)\n\\( A_{ 3 } \\)\n\\( A_{ 4 } \\)",
                  "\\( A_{ 1 } \\)\n\\( \\frac{ B_{ 2 } }{ C_{ 2 } } \\)\n\\( A_{ 3 } \\)\n\\( \\frac{ B_{ 4 } }{ C_{ 4 } } \\)",
                  "\\( A_{ 1 } \\)\n\\( \\frac{ B_{ 2 } }{ C_{ 2 } } \\)\n\\( \\frac{ B_{ 3 } }{ C_{ 3 } } \\)\n\\( A_{ 4 } \\)",
                  "\\( A_{ 1 } \\)\n\\( \\frac{ B_{ 2 } }{ C_{ 2 } } \\)\n\\( \\frac{ B_{ 3 } }{ C_{ 3 } } \\)\n\\( \\frac{ B_{ 4 } }{ C_{ 4 } } \\)",
                  "\\( \\frac{ B_{ 1 } }{ C_{ 1 } } \\)\n\\( A_{ 2 } \\)\n\\( A_{ 3 } \\)\n\\( A_{ 4 } \\)",
                  "\\( \\frac{ B_{ 1 } }{ C_{ 1 } } \\)\n\\( A_{ 2 } \\)\n\\( A_{ 3 } \\)\n\\( \\frac{ B_{ 4 } }{ C_{ 4 } } \\)",
                  "\\( \\frac{ B_{ 1 } }{ C_{ 1 } } \\)\n\\( A_{ 2 } \\)\n\\( \\frac{ B_{ 3 } }{ C_{ 3 } } \\)\n\\( A_{ 4 } \\)",
                  "\\( \\frac{ B_{ 1 } }{ C_{ 1 } } \\)\n\\( A_{ 2 } \\)\n\\( \\frac{ B_{ 3 } }{ C_{ 3 } } \\)\n\\( \\frac{ B_{ 4 } }{ C_{ 4 } } \\)",
                  "\\( \\frac{ B_{ 1 } }{ C_{ 1 } } \\)\n\\( \\frac{ B_{ 2 } }{ C_{ 2 } } \\)\n\\( A_{ 3 } \\)\n\\( A_{ 4 } \\)",
                  "\\( \\frac{ B_{ 1 } }{ C_{ 1 } } \\)\n\\( \\frac{ B_{ 2 } }{ C_{ 2 } } \\)\n\\( A_{ 3 } \\)\n\\( \\frac{ B_{ 4 } }{ C_{ 4 } } \\)",
                  "\\( \\frac{ B_{ 1 } }{ C_{ 1 } } \\)\n\\( \\frac{ B_{ 2 } }{ C_{ 2 } } \\)\n\\( \\frac{ B_{ 3 } }{ C_{ 3 } } \\)\n\\( A_{ 4 } \\)",
                  "\\( \\frac{ B_{ 1 } }{ C_{ 1 } } \\)\n\\( \\frac{ B_{ 2 } }{ C_{ 2 } } \\)\n\\( \\frac{ B_{ 3 } }{ C_{ 3 } } \\)\n\\( \\frac{ B_{ 4 } }{ C_{ 4 } } \\)"
                ]
              },
              "mapping": {
                "x": "x",
                "y": "y"
              },
              "data_meta": {
                "series_annotations": [
                  {
                    "type": "int",
                    "column": "x"
                  },
                  {
                    "type": "int",
                    "column": "y"
                  },
                  {
                    "type": "str",
                    "column": "l"
                  }
                ]
              },
              "theme": {
                "name": "gray",
                "axis": "blank"
              },
              "ggsize": {
                "width": 600.0,
                "height": 400.0
              },
              "kind": "plot",
              "scales": [
                {
                  "aesthetic": "x",
                  "limits": [
                    -0.25,
                    13.25
                  ]
                },
                {
                  "aesthetic": "y",
                  "limits": [
                    0.25,
                    -1.25
                  ]
                }
              ],
              "layers": [
                {
                  "geom": "label",
                  "mapping": {
                    "label": "l"
                  },
                  "data_meta": {}
                }
              ],
              "metainfo_list": []
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("geom_label_renders_multiline_latex_formulas_for_different_line_counts_and_formula_combinations.png", plotSpec)
    }

    @Test
    fun `geom_label renders multiline labels with latex formulas for different vjust and lineheight values`() {
        val spec = """
            {
              "kind": "subplots",
              "layout": {
                "ncol": 4.0,
                "nrow": 4.0,
                "name": "grid"
              },
              "figures": [
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "vjust=None\nlineheight=None"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "y": 0.0,
                      "label": "A\nB",
                      "alpha": 0.75
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "vjust=None\nlineheight=None"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "y": 0.0,
                      "label": "A\n\\( \\frac{B}{C} \\)",
                      "alpha": 0.75
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "vjust=top\nlineheight=None"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "y": 0.0,
                      "label": "A\nB",
                      "alpha": 0.75,
                      "vjust": "top"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "vjust=top\nlineheight=None"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "y": 0.0,
                      "label": "A\n\\( \\frac{B}{C} \\)",
                      "alpha": 0.75,
                      "vjust": "top"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "vjust=center\nlineheight=None"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "y": 0.0,
                      "label": "A\nB",
                      "alpha": 0.75,
                      "vjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "vjust=center\nlineheight=None"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "y": 0.0,
                      "label": "A\n\\( \\frac{B}{C} \\)",
                      "alpha": 0.75,
                      "vjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "vjust=bottom\nlineheight=None"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "y": 0.0,
                      "label": "A\nB",
                      "alpha": 0.75,
                      "vjust": "bottom"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "vjust=bottom\nlineheight=None"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "y": 0.0,
                      "label": "A\n\\( \\frac{B}{C} \\)",
                      "alpha": 0.75,
                      "vjust": "bottom"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "vjust=None\nlineheight=3"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "y": 0.0,
                      "label": "A\nB",
                      "alpha": 0.75,
                      "lineheight": 3.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "vjust=None\nlineheight=3"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "y": 0.0,
                      "label": "A\n\\( \\frac{B}{C} \\)",
                      "alpha": 0.75,
                      "lineheight": 3.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "vjust=top\nlineheight=3"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "y": 0.0,
                      "label": "A\nB",
                      "alpha": 0.75,
                      "lineheight": 3.0,
                      "vjust": "top"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "vjust=top\nlineheight=3"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "y": 0.0,
                      "label": "A\n\\( \\frac{B}{C} \\)",
                      "alpha": 0.75,
                      "lineheight": 3.0,
                      "vjust": "top"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "vjust=center\nlineheight=3"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "y": 0.0,
                      "label": "A\nB",
                      "alpha": 0.75,
                      "lineheight": 3.0,
                      "vjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "vjust=center\nlineheight=3"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "y": 0.0,
                      "label": "A\n\\( \\frac{B}{C} \\)",
                      "alpha": 0.75,
                      "lineheight": 3.0,
                      "vjust": "center"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "vjust=bottom\nlineheight=3"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "y": 0.0,
                      "label": "A\nB",
                      "alpha": 0.75,
                      "lineheight": 3.0,
                      "vjust": "bottom"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "vjust=bottom\nlineheight=3"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "y": 0.0,
                      "label": "A\n\\( \\frac{B}{C} \\)",
                      "alpha": 0.75,
                      "lineheight": 3.0,
                      "vjust": "bottom"
                    }
                  ],
                  "metainfo_list": []
                }
              ]
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("geom_label_renders_multiline_labels_with_latex_formulas_for_different_vjust_and_lineheight_values.png", plotSpec)
    }

    @Test
    fun `latex formulas render consistently across plot title axis labels and geom_text`() {
        val spec = """
            {
              "kind": "subplots",
              "layout": {
                "ncol": 2.0,
                "nrow": 2.0,
                "name": "grid"
              },
              "figures": [
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "panel_background": {
                      "fill": "gray90",
                      "blank": false
                    },
                    "plot_background": {
                      "fill": "gray80",
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\( \\frac{A}{B} \\)"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "name": "\\( \\frac{A}{B} \\)",
                      "aesthetic": "x"
                    },
                    {
                      "name": "\\( \\frac{A}{B} \\)",
                      "aesthetic": "y",
                      "breaks": [
                        0.0
                      ],
                      "labels": [
                        "\\( \\frac{A}{B} \\)"
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\( \\frac{A}{B} \\)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "panel_background": {
                      "fill": "gray90",
                      "blank": false
                    },
                    "plot_background": {
                      "fill": "gray80",
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\( A + B\\frac{C}{D} \\)"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "name": "\\( A + B\\frac{C}{D} \\)",
                      "aesthetic": "x"
                    },
                    {
                      "name": "\\( A + B\\frac{C}{D} \\)",
                      "aesthetic": "y",
                      "breaks": [
                        0.0
                      ],
                      "labels": [
                        "\\( A + B\\frac{C}{D} \\)"
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\( A + B\\frac{C}{D} \\)"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "panel_background": {
                      "fill": "gray90",
                      "blank": false
                    },
                    "plot_background": {
                      "fill": "gray80",
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\( \\frac{A}{B} \\)\nC"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "name": "\\( \\frac{A}{B} \\)\nC",
                      "aesthetic": "x"
                    },
                    {
                      "name": "\\( \\frac{A}{B} \\)\nC",
                      "aesthetic": "y",
                      "breaks": [
                        0.0
                      ],
                      "labels": [
                        "\\( \\frac{A}{B} \\)\nC"
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\( \\frac{A}{B} \\)\nC"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "panel_background": {
                      "fill": "gray90",
                      "blank": false
                    },
                    "plot_background": {
                      "fill": "gray80",
                      "blank": false
                    }
                  },
                  "ggtitle": {
                    "text": "\\( A + B\\frac{C}{D} \\)\n\\( \\frac{E}{F} \\)"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "name": "\\( A + B\\frac{C}{D} \\)\n\\( \\frac{E}{F} \\)",
                      "aesthetic": "x"
                    },
                    {
                      "name": "\\( A + B\\frac{C}{D} \\)\n\\( \\frac{E}{F} \\)",
                      "aesthetic": "y",
                      "breaks": [
                        0.0
                      ],
                      "labels": [
                        "\\( A + B\\frac{C}{D} \\)\n\\( \\frac{E}{F} \\)"
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "\\( A + B\\frac{C}{D} \\)\n\\( \\frac{E}{F} \\)"
                    }
                  ],
                  "metainfo_list": []
                }
              ]
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("latex_formulas_render_consistently_across_plot_title_axis_labels_and_geom_text.png", plotSpec)
    }

    @Test
    fun `text geoms render long multiline labels with mixed text and latex formulas`() {
        val spec = """
            {
              "mapping": {},
              "data_meta": {},
              "theme": {
                "name": "gray",
                "axis": "blank"
              },
              "ggsize": {
                "width": 600.0,
                "height": 600.0
              },
              "kind": "plot",
              "scales": [
                {
                  "aesthetic": "y",
                  "limits": [
                    -2.0,
                    2.0
                  ]
                }
              ],
              "layers": [
                {
                  "geom": "hline",
                  "mapping": {
                    "yintercept": [
                      -1.0,
                      1.0
                    ]
                  },
                  "data_meta": {},
                  "color": "salmon"
                },
                {
                  "geom": "vline",
                  "mapping": {},
                  "data_meta": {},
                  "xintercept": 0.0,
                  "color": "salmon"
                },
                {
                  "geom": "text",
                  "mapping": {},
                  "data_meta": {},
                  "y": 1.0,
                  "size": 10.0,
                  "label": "First: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\)\nSecond: \\( \\Theta_{ a_1 a_2 a_3 a_4 ... a_{n + 1} } \\)\nThird\nFourth: \\( \\frac{A_1}{A_2} + \\frac{B_1}{B_2} + \\frac{C_1}{C_2} + \\frac{D_1}{D_2} + \\frac{E_1}{E_2} + \\frac{F_1}{F_2} \\)\nFifth and final one for the moment: \\( \\frac{\\Gamma}{\\Delta} \\)"
                },
                {
                  "geom": "label",
                  "mapping": {},
                  "data_meta": {},
                  "y": -1.0,
                  "size": 10.0,
                  "alpha": 0.75,
                  "label": "First: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\)\nSecond: \\( \\Theta_{ a_1 a_2 a_3 a_4 ... a_{n + 1} } \\)\nThird\nFourth: \\( \\frac{A_1}{A_2} + \\frac{B_1}{B_2} + \\frac{C_1}{C_2} + \\frac{D_1}{D_2} + \\frac{E_1}{E_2} + \\frac{F_1}{F_2} \\)\nFifth and final one for the moment: \\( \\frac{\\Gamma}{\\Delta} \\)"
                }
              ],
              "metainfo_list": []
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("text_geoms_render_long_multiline_labels_with_mixed_text_and_latex_formulas.png", plotSpec)
    }

    @Test
    fun `facet strip labels render latex formulas`() {
        val spec = """
            {
              "data": {
                "x": [
                  0.0,
                  0.0
                ],
                "f": [
                  "\\( \\frac{A}{B} \\)",
                  "C"
                ]
              },
              "mapping": {
                "x": "x"
              },
              "data_meta": {
                "series_annotations": [
                  {
                    "type": "int",
                    "column": "x"
                  },
                  {
                    "type": "str",
                    "column": "f"
                  }
                ]
              },
              "facet": {
                "name": "grid",
                "x": "f",
                "x_order": 1.0,
                "y_order": 1.0
              },
              "theme": {
                "name": "grey"
              },
              "kind": "plot",
              "scales": [],
              "layers": [
                {
                  "geom": "point",
                  "mapping": {},
                  "data_meta": {}
                }
              ],
              "metainfo_list": []
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("facet_strip_labels_render_latex_formulas.png", plotSpec)
    }

    @Test
    fun `discrete legend labels render latex formulas`() {
        val spec = """
            {
              "kind": "subplots",
              "layout": {
                "ncol": 4.0,
                "nrow": 2.0,
                "name": "grid"
              },
              "figures": [
                {
                  "data": {
                    "x": [
                      0.0,
                      1.0
                    ],
                    "formula": [
                      "\\( \\frac{A}{B} \\)",
                      "C"
                    ]
                  },
                  "mapping": {
                    "x": "x",
                    "color": "formula"
                  },
                  "data_meta": {
                    "series_annotations": [
                      {
                        "type": "int",
                        "column": "x"
                      },
                      {
                        "type": "str",
                        "column": "formula"
                      }
                    ]
                  },
                  "theme": {
                    "legend_position": "right"
                  },
                  "ggtitle": {
                    "text": "#1: right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "data_meta": {},
                      "shape": 22.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "data": {
                    "x": [
                      0.0,
                      1.0
                    ],
                    "formula": [
                      "\\( \\frac{A}{B} \\)",
                      "C"
                    ]
                  },
                  "mapping": {
                    "x": "x",
                    "color": "formula"
                  },
                  "data_meta": {
                    "series_annotations": [
                      {
                        "type": "int",
                        "column": "x"
                      },
                      {
                        "type": "str",
                        "column": "formula"
                      }
                    ]
                  },
                  "theme": {
                    "legend_position": "bottom"
                  },
                  "ggtitle": {
                    "text": "#1: bottom"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "data_meta": {},
                      "shape": 22.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "data": {
                    "x": [
                      0.0,
                      1.0
                    ],
                    "formula": [
                      "\\( \\frac{A}{B} \\)\nC",
                      "D"
                    ]
                  },
                  "mapping": {
                    "x": "x",
                    "color": "formula"
                  },
                  "data_meta": {
                    "series_annotations": [
                      {
                        "type": "int",
                        "column": "x"
                      },
                      {
                        "type": "str",
                        "column": "formula"
                      }
                    ]
                  },
                  "theme": {
                    "legend_position": "right"
                  },
                  "ggtitle": {
                    "text": "#2: right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "data_meta": {},
                      "shape": 22.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "data": {
                    "x": [
                      0.0,
                      1.0
                    ],
                    "formula": [
                      "\\( \\frac{A}{B} \\)\nC",
                      "D"
                    ]
                  },
                  "mapping": {
                    "x": "x",
                    "color": "formula"
                  },
                  "data_meta": {
                    "series_annotations": [
                      {
                        "type": "int",
                        "column": "x"
                      },
                      {
                        "type": "str",
                        "column": "formula"
                      }
                    ]
                  },
                  "theme": {
                    "legend_position": "bottom"
                  },
                  "ggtitle": {
                    "text": "#2: bottom"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "data_meta": {},
                      "shape": 22.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "data": {
                    "x": [
                      0.0,
                      1.0
                    ],
                    "formula": [
                      "\\( \\frac{A}{B} \\)\n\\( \\frac{C}{D} \\)",
                      "E"
                    ]
                  },
                  "mapping": {
                    "x": "x",
                    "color": "formula"
                  },
                  "data_meta": {
                    "series_annotations": [
                      {
                        "type": "int",
                        "column": "x"
                      },
                      {
                        "type": "str",
                        "column": "formula"
                      }
                    ]
                  },
                  "theme": {
                    "legend_position": "right"
                  },
                  "ggtitle": {
                    "text": "#3: right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "data_meta": {},
                      "shape": 22.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "data": {
                    "x": [
                      0.0,
                      1.0
                    ],
                    "formula": [
                      "\\( \\frac{A}{B} \\)\n\\( \\frac{C}{D} \\)",
                      "E"
                    ]
                  },
                  "mapping": {
                    "x": "x",
                    "color": "formula"
                  },
                  "data_meta": {
                    "series_annotations": [
                      {
                        "type": "int",
                        "column": "x"
                      },
                      {
                        "type": "str",
                        "column": "formula"
                      }
                    ]
                  },
                  "theme": {
                    "legend_position": "bottom"
                  },
                  "ggtitle": {
                    "text": "#3: bottom"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "data_meta": {},
                      "shape": 22.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "data": {
                    "x": [
                      0.0,
                      1.0
                    ],
                    "formula": [
                      "\\( \\frac{A}{B} \\)\nC",
                      "\\( \\frac{D}{E} \\)"
                    ]
                  },
                  "mapping": {
                    "x": "x",
                    "color": "formula"
                  },
                  "data_meta": {
                    "series_annotations": [
                      {
                        "type": "int",
                        "column": "x"
                      },
                      {
                        "type": "str",
                        "column": "formula"
                      }
                    ]
                  },
                  "theme": {
                    "legend_position": "right"
                  },
                  "ggtitle": {
                    "text": "#4: right"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "data_meta": {},
                      "shape": 22.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "data": {
                    "x": [
                      0.0,
                      1.0
                    ],
                    "formula": [
                      "\\( \\frac{A}{B} \\)\nC",
                      "\\( \\frac{D}{E} \\)"
                    ]
                  },
                  "mapping": {
                    "x": "x",
                    "color": "formula"
                  },
                  "data_meta": {
                    "series_annotations": [
                      {
                        "type": "int",
                        "column": "x"
                      },
                      {
                        "type": "str",
                        "column": "formula"
                      }
                    ]
                  },
                  "theme": {
                    "legend_position": "bottom"
                  },
                  "ggtitle": {
                    "text": "#4: bottom"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "mapping": {},
                      "data_meta": {},
                      "shape": 22.0
                    }
                  ],
                  "metainfo_list": []
                }
              ]
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("discrete_legend_labels_render_latex_formulas.png", plotSpec)
    }

    @Test
    fun `continuous legend labels render latex formulas`() {
        val spec = """
            {
              "data": {
                "x": [
                  0.0,
                  1.0,
                  2.0,
                  3.0,
                  4.0
                ]
              },
              "mapping": {
                "x": "x",
                "color": "x"
              },
              "data_meta": {
                "series_annotations": [
                  {
                    "type": "int",
                    "column": "x"
                  }
                ]
              },
              "kind": "plot",
              "scales": [
                {
                  "aesthetic": "color",
                  "breaks": [
                    0.0,
                    1.0,
                    2.0,
                    3.0,
                    4.0
                  ],
                  "labels": [
                    "\\( \\frac{0}{1} \\)",
                    "1",
                    "\\( \\frac{2}{1} \\)",
                    "3",
                    "\\( \\frac{4}{1} \\)"
                  ],
                  "scale_mapper_kind": "color_gradient"
                }
              ],
              "layers": [
                {
                  "geom": "line",
                  "mapping": {},
                  "data_meta": {},
                  "y": 0.0,
                  "size": 5.0
                }
              ],
              "metainfo_list": []
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("continuous_legend_labels_render_latex_formulas.png", plotSpec)
    }

    @Test
    fun `geom_text renders latex formulas for different appearance settings`() {
        val spec = """
            {
              "kind": "subplots",
              "layout": {
                "ncol": 4.0,
                "nrow": 2.0,
                "name": "grid"
              },
              "figures": [
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "color=red"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "First: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\)  \n Second: \\( \\Theta_{ a_1 a_2 ... a_{n + 1} } \\)",
                      "color": "red"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "size=12"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "First: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\)  \n Second: \\( \\Theta_{ a_1 a_2 ... a_{n + 1} } \\)",
                      "size": 12.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "family=mono"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "First: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\)  \n Second: \\( \\Theta_{ a_1 a_2 ... a_{n + 1} } \\)",
                      "family": "mono"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "fontface=italic"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "First: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\)  \n Second: \\( \\Theta_{ a_1 a_2 ... a_{n + 1} } \\)",
                      "fontface": "italic"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "hjust=right"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "First: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\)  \n Second: \\( \\Theta_{ a_1 a_2 ... a_{n + 1} } \\)",
                      "hjust": "right"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "vjust=top"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "First: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\)  \n Second: \\( \\Theta_{ a_1 a_2 ... a_{n + 1} } \\)",
                      "vjust": "top"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "angle=30"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "First: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\)  \n Second: \\( \\Theta_{ a_1 a_2 ... a_{n + 1} } \\)",
                      "angle": 30.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "ggtitle": {
                    "text": "lineheight=3"
                  },
                  "theme": {
                    "name": "gray",
                    "axis": "blank"
                  },
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "tooltips": "none",
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "x": 0.0,
                      "label": "First: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\)  \n Second: \\( \\Theta_{ a_1 a_2 ... a_{n + 1} } \\)",
                      "lineheight": 3.0
                    }
                  ],
                  "metainfo_list": []
                }
              ]
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("geom_text_renders_latex_formulas_for_different_appearance_settings.png", plotSpec)
    }

    @Test
    fun `latex formulas embedded in plain text render consistently across plot elements`() {
        val spec = """
            {
              "ggsize": {
                "width": 800.0,
                "height": 1300.0
              },
              "kind": "subplots",
              "layout": {
                "ncol": 1.0,
                "nrow": 2.0,
                "name": "grid"
              },
              "figures": [
                {
                  "data": {
                    "x": [
                      "F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\neq 0 \\) (L)",
                      "F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\neq 0 \\) (L)"
                    ],
                    "g": [
                      "F1: \\( \\Sigma \\cdot \\frac{ 20 - a^{b_1} }{\\rho} \\geq 1 \\)",
                      "F2: \\( \\Delta \\cdot \\frac{\\xi}{ 30 - c_{d^2} } \\leq -1 \\)"
                    ]
                  },
                  "mapping": {
                    "x": "x"
                  },
                  "data_meta": {
                    "series_annotations": [
                      {
                        "type": "str",
                        "column": "x"
                      },
                      {
                        "type": "str",
                        "column": "g"
                      }
                    ]
                  },
                  "ggtitle": {
                    "text": "Title: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\neq 0 \\) (L)\"",
                    "subtitle": "Subtitle: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\neq 0 \\) (L)\""
                  },
                  "caption": {
                    "text": "Caption: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\neq 0 \\) (L)\""
                  },
                  "facet": {
                    "name": "grid",
                    "x": "g",
                    "x_order": 1.0,
                    "y_order": 1.0
                  },
                  "theme": {
                    "legend_position": "bottom",
                    "flavor": "solarized_light"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "name": "X: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\neq 0 \\) (L)\"",
                      "aesthetic": "x"
                    },
                    {
                      "name": "Y: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\neq 0 \\) (L)\"",
                      "aesthetic": "y",
                      "limits": [
                        0.0,
                        3.0
                      ]
                    },
                    {
                      "name": "F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\neq 0 \\) (L)",
                      "aesthetic": "color",
                      "discrete": true
                    }
                  ],
                  "layers": [
                    {
                      "geom": "bar",
                      "stat": "identity",
                      "mapping": {
                        "color": "g"
                      },
                      "data_meta": {},
                      "labels": {
                        "formats": [],
                        "variables": [
                          "x"
                        ]
                      },
                      "y": 1.0,
                      "size": 2.0,
                      "fill": "black"
                    },
                    {
                      "geom": "label",
                      "mapping": {
                        "label": "x",
                        "color": "g"
                      },
                      "data_meta": {},
                      "y": 2.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "data": {
                    "x": [
                      "F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\neq 0 \\) (L)",
                      "F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\neq 0 \\) (L)"
                    ],
                    "g": [
                      "F1: \\( \\Sigma \\cdot \\frac{ 20 - a^{b_1} }{\\rho} \\geq 1 \\)",
                      "F2: \\( \\Delta \\cdot \\frac{\\xi}{ 30 - c_{d^2} } \\leq -1 \\)"
                    ]
                  },
                  "mapping": {
                    "x": "x"
                  },
                  "data_meta": {
                    "series_annotations": [
                      {
                        "type": "str",
                        "column": "x"
                      },
                      {
                        "type": "str",
                        "column": "g"
                      }
                    ]
                  },
                  "ggtitle": {
                    "text": "Title: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\neq 0 \\) (L)\"",
                    "subtitle": "Subtitle: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\neq 0 \\) (L)\""
                  },
                  "caption": {
                    "text": "Caption: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\neq 0 \\) (L)\""
                  },
                  "facet": {
                    "name": "grid",
                    "x": "g",
                    "x_order": 1.0,
                    "y_order": 1.0
                  },
                  "theme": {
                    "legend_position": "bottom",
                    "flavor": "solarized_light"
                  },
                  "coord": {
                    "name": "polar"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "name": "X: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\neq 0 \\) (L)\"",
                      "aesthetic": "x"
                    },
                    {
                      "name": "Y: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\neq 0 \\) (L)\"",
                      "aesthetic": "y",
                      "limits": [
                        0.0,
                        3.0
                      ]
                    },
                    {
                      "name": "F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} \\neq 0 \\) (L)",
                      "aesthetic": "color",
                      "discrete": true
                    }
                  ],
                  "layers": [
                    {
                      "geom": "bar",
                      "stat": "identity",
                      "mapping": {
                        "color": "g"
                      },
                      "data_meta": {},
                      "labels": {
                        "formats": [],
                        "variables": [
                          "x"
                        ]
                      },
                      "y": 1.0,
                      "size": 2.0,
                      "fill": "black"
                    },
                    {
                      "geom": "label",
                      "mapping": {
                        "label": "x",
                        "color": "g"
                      },
                      "data_meta": {},
                      "y": 2.0
                    }
                  ],
                  "metainfo_list": []
                }
              ]
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("latex_formulas_embedded_in_plain_text_render_consistently_across_plot_elements.png", plotSpec)
    }

    @Test
    fun `latex formulas in plot elements respect global and element-specific theme text settings`() {
        val spec = """
            {
              "ggsize": {
                "width": 1200.0,
                "height": 600.0
              },
              "kind": "subplots",
              "layout": {
                "ncol": 4.0,
                "nrow": 2.0,
                "name": "grid"
              },
              "figures": [
                {
                  "data": {
                    "x": [
                      "\\( a^2 \\)",
                      "\\( a^2 \\)"
                    ],
                    "g": [
                      "g1",
                      "g2"
                    ]
                  },
                  "mapping": {
                    "x": "x"
                  },
                  "data_meta": {
                    "series_annotations": [
                      {
                        "type": "str",
                        "column": "x"
                      },
                      {
                        "type": "str",
                        "column": "g"
                      }
                    ]
                  },
                  "ggtitle": {
                    "text": "Title: \"\\( a^2 \\)\"",
                    "subtitle": "Subtitle: \"\\( a^2 \\)\""
                  },
                  "caption": {
                    "text": "Caption: \"\\( a^2 \\)\""
                  },
                  "facet": {
                    "name": "grid",
                    "x": "g",
                    "x_order": 1.0,
                    "y_order": 1.0
                  },
                  "theme": {
                    "legend_position": "bottom",
                    "text": {
                      "color": "green",
                      "family": "Times",
                      "face": "italic",
                      "size": 30.0,
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "name": "X: \"\\( a^2 \\)\"",
                      "aesthetic": "x"
                    },
                    {
                      "name": "Y: \"\\( a^2 \\)\"",
                      "aesthetic": "y"
                    },
                    {
                      "name": "\\( a^2 \\)",
                      "aesthetic": "color",
                      "discrete": true
                    }
                  ],
                  "layers": [
                    {
                      "geom": "bar",
                      "stat": "identity",
                      "mapping": {
                        "color": "g"
                      },
                      "data_meta": {},
                      "labels": {
                        "formats": [],
                        "variables": [
                          "x"
                        ]
                      },
                      "y": 1.0,
                      "size": 2.0,
                      "fill": "black"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "data": {
                    "x": [
                      "\\( a^2 \\)",
                      "\\( a^2 \\)"
                    ],
                    "g": [
                      "g1",
                      "g2"
                    ]
                  },
                  "mapping": {
                    "x": "x"
                  },
                  "data_meta": {
                    "series_annotations": [
                      {
                        "type": "str",
                        "column": "x"
                      },
                      {
                        "type": "str",
                        "column": "g"
                      }
                    ]
                  },
                  "ggtitle": {
                    "text": "Title: \"\\( a^2 \\)\"",
                    "subtitle": "Subtitle: \"\\( a^2 \\)\""
                  },
                  "caption": {
                    "text": "Caption: \"\\( a^2 \\)\""
                  },
                  "facet": {
                    "name": "grid",
                    "x": "g",
                    "x_order": 1.0,
                    "y_order": 1.0
                  },
                  "theme": {
                    "legend_position": "bottom",
                    "plot_title": {
                      "color": "green",
                      "family": "Times",
                      "face": "italic",
                      "size": 30.0,
                      "blank": false
                    },
                    "plot_subtitle": {
                      "color": "green",
                      "family": "Times",
                      "face": "italic",
                      "size": 30.0,
                      "blank": false
                    },
                    "plot_caption": {
                      "color": "green",
                      "family": "Times",
                      "face": "italic",
                      "size": 30.0,
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "name": "X: \"\\( a^2 \\)\"",
                      "aesthetic": "x"
                    },
                    {
                      "name": "Y: \"\\( a^2 \\)\"",
                      "aesthetic": "y"
                    },
                    {
                      "name": "\\( a^2 \\)",
                      "aesthetic": "color",
                      "discrete": true
                    }
                  ],
                  "layers": [
                    {
                      "geom": "bar",
                      "stat": "identity",
                      "mapping": {
                        "color": "g"
                      },
                      "data_meta": {},
                      "labels": {
                        "formats": [],
                        "variables": [
                          "x"
                        ]
                      },
                      "y": 1.0,
                      "size": 2.0,
                      "fill": "black"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "data": {
                    "x": [
                      "\\( a^2 \\)",
                      "\\( a^2 \\)"
                    ],
                    "g": [
                      "g1",
                      "g2"
                    ]
                  },
                  "mapping": {
                    "x": "x"
                  },
                  "data_meta": {
                    "series_annotations": [
                      {
                        "type": "str",
                        "column": "x"
                      },
                      {
                        "type": "str",
                        "column": "g"
                      }
                    ]
                  },
                  "ggtitle": {
                    "text": "Title: \"\\( a^2 \\)\"",
                    "subtitle": "Subtitle: \"\\( a^2 \\)\""
                  },
                  "caption": {
                    "text": "Caption: \"\\( a^2 \\)\""
                  },
                  "facet": {
                    "name": "grid",
                    "x": "g",
                    "x_order": 1.0,
                    "y_order": 1.0
                  },
                  "theme": {
                    "legend_position": "bottom",
                    "axis_title": {
                      "color": "green",
                      "family": "Times",
                      "face": "italic",
                      "size": 30.0,
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "name": "X: \"\\( a^2 \\)\"",
                      "aesthetic": "x"
                    },
                    {
                      "name": "Y: \"\\( a^2 \\)\"",
                      "aesthetic": "y"
                    },
                    {
                      "name": "\\( a^2 \\)",
                      "aesthetic": "color",
                      "discrete": true
                    }
                  ],
                  "layers": [
                    {
                      "geom": "bar",
                      "stat": "identity",
                      "mapping": {
                        "color": "g"
                      },
                      "data_meta": {},
                      "labels": {
                        "formats": [],
                        "variables": [
                          "x"
                        ]
                      },
                      "y": 1.0,
                      "size": 2.0,
                      "fill": "black"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "data": {
                    "x": [
                      "\\( a^2 \\)",
                      "\\( a^2 \\)"
                    ],
                    "g": [
                      "g1",
                      "g2"
                    ]
                  },
                  "mapping": {
                    "x": "x"
                  },
                  "data_meta": {
                    "series_annotations": [
                      {
                        "type": "str",
                        "column": "x"
                      },
                      {
                        "type": "str",
                        "column": "g"
                      }
                    ]
                  },
                  "ggtitle": {
                    "text": "Title: \"\\( a^2 \\)\"",
                    "subtitle": "Subtitle: \"\\( a^2 \\)\""
                  },
                  "caption": {
                    "text": "Caption: \"\\( a^2 \\)\""
                  },
                  "facet": {
                    "name": "grid",
                    "x": "g",
                    "x_order": 1.0,
                    "y_order": 1.0
                  },
                  "theme": {
                    "legend_position": "bottom",
                    "legend_title": {
                      "color": "green",
                      "family": "Times",
                      "face": "italic",
                      "size": 30.0,
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "name": "X: \"\\( a^2 \\)\"",
                      "aesthetic": "x"
                    },
                    {
                      "name": "Y: \"\\( a^2 \\)\"",
                      "aesthetic": "y"
                    },
                    {
                      "name": "\\( a^2 \\)",
                      "aesthetic": "color",
                      "discrete": true
                    }
                  ],
                  "layers": [
                    {
                      "geom": "bar",
                      "stat": "identity",
                      "mapping": {
                        "color": "g"
                      },
                      "data_meta": {},
                      "labels": {
                        "formats": [],
                        "variables": [
                          "x"
                        ]
                      },
                      "y": 1.0,
                      "size": 2.0,
                      "fill": "black"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "data": {
                    "x": [
                      "\\( a^2 \\)",
                      "\\( a^2 \\)"
                    ],
                    "g": [
                      "g1",
                      "g2"
                    ]
                  },
                  "mapping": {
                    "x": "x"
                  },
                  "data_meta": {
                    "series_annotations": [
                      {
                        "type": "str",
                        "column": "x"
                      },
                      {
                        "type": "str",
                        "column": "g"
                      }
                    ]
                  },
                  "ggtitle": {
                    "text": "Title: \"\\( a^2 \\)\"",
                    "subtitle": "Subtitle: \"\\( a^2 \\)\""
                  },
                  "caption": {
                    "text": "Caption: \"\\( a^2 \\)\""
                  },
                  "facet": {
                    "name": "grid",
                    "x": "g",
                    "x_order": 1.0,
                    "y_order": 1.0
                  },
                  "theme": {
                    "legend_position": "bottom",
                    "text": {
                      "color": "green",
                      "family": "Times",
                      "face": "italic",
                      "size": 30.0,
                      "blank": false
                    }
                  },
                  "coord": {
                    "name": "polar"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "name": "X: \"\\( a^2 \\)\"",
                      "aesthetic": "x"
                    },
                    {
                      "name": "Y: \"\\( a^2 \\)\"",
                      "aesthetic": "y"
                    },
                    {
                      "name": "\\( a^2 \\)",
                      "aesthetic": "color",
                      "discrete": true
                    }
                  ],
                  "layers": [
                    {
                      "geom": "bar",
                      "stat": "identity",
                      "mapping": {
                        "color": "g"
                      },
                      "data_meta": {},
                      "labels": {
                        "formats": [],
                        "variables": [
                          "x"
                        ]
                      },
                      "y": 1.0,
                      "size": 2.0,
                      "fill": "black"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "data": {
                    "x": [
                      "\\( a^2 \\)",
                      "\\( a^2 \\)"
                    ],
                    "g": [
                      "g1",
                      "g2"
                    ]
                  },
                  "mapping": {
                    "x": "x"
                  },
                  "data_meta": {
                    "series_annotations": [
                      {
                        "type": "str",
                        "column": "x"
                      },
                      {
                        "type": "str",
                        "column": "g"
                      }
                    ]
                  },
                  "ggtitle": {
                    "text": "Title: \"\\( a^2 \\)\"",
                    "subtitle": "Subtitle: \"\\( a^2 \\)\""
                  },
                  "caption": {
                    "text": "Caption: \"\\( a^2 \\)\""
                  },
                  "facet": {
                    "name": "grid",
                    "x": "g",
                    "x_order": 1.0,
                    "y_order": 1.0
                  },
                  "theme": {
                    "legend_position": "bottom",
                    "plot_title": {
                      "color": "green",
                      "family": "Times",
                      "face": "italic",
                      "size": 30.0,
                      "blank": false
                    },
                    "plot_subtitle": {
                      "color": "green",
                      "family": "Times",
                      "face": "italic",
                      "size": 30.0,
                      "blank": false
                    },
                    "plot_caption": {
                      "color": "green",
                      "family": "Times",
                      "face": "italic",
                      "size": 30.0,
                      "blank": false
                    }
                  },
                  "coord": {
                    "name": "polar"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "name": "X: \"\\( a^2 \\)\"",
                      "aesthetic": "x"
                    },
                    {
                      "name": "Y: \"\\( a^2 \\)\"",
                      "aesthetic": "y"
                    },
                    {
                      "name": "\\( a^2 \\)",
                      "aesthetic": "color",
                      "discrete": true
                    }
                  ],
                  "layers": [
                    {
                      "geom": "bar",
                      "stat": "identity",
                      "mapping": {
                        "color": "g"
                      },
                      "data_meta": {},
                      "labels": {
                        "formats": [],
                        "variables": [
                          "x"
                        ]
                      },
                      "y": 1.0,
                      "size": 2.0,
                      "fill": "black"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "data": {
                    "x": [
                      "\\( a^2 \\)",
                      "\\( a^2 \\)"
                    ],
                    "g": [
                      "g1",
                      "g2"
                    ]
                  },
                  "mapping": {
                    "x": "x"
                  },
                  "data_meta": {
                    "series_annotations": [
                      {
                        "type": "str",
                        "column": "x"
                      },
                      {
                        "type": "str",
                        "column": "g"
                      }
                    ]
                  },
                  "ggtitle": {
                    "text": "Title: \"\\( a^2 \\)\"",
                    "subtitle": "Subtitle: \"\\( a^2 \\)\""
                  },
                  "caption": {
                    "text": "Caption: \"\\( a^2 \\)\""
                  },
                  "facet": {
                    "name": "grid",
                    "x": "g",
                    "x_order": 1.0,
                    "y_order": 1.0
                  },
                  "theme": {
                    "legend_position": "bottom",
                    "axis_title": {
                      "color": "green",
                      "family": "Times",
                      "face": "italic",
                      "size": 30.0,
                      "blank": false
                    }
                  },
                  "coord": {
                    "name": "polar"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "name": "X: \"\\( a^2 \\)\"",
                      "aesthetic": "x"
                    },
                    {
                      "name": "Y: \"\\( a^2 \\)\"",
                      "aesthetic": "y"
                    },
                    {
                      "name": "\\( a^2 \\)",
                      "aesthetic": "color",
                      "discrete": true
                    }
                  ],
                  "layers": [
                    {
                      "geom": "bar",
                      "stat": "identity",
                      "mapping": {
                        "color": "g"
                      },
                      "data_meta": {},
                      "labels": {
                        "formats": [],
                        "variables": [
                          "x"
                        ]
                      },
                      "y": 1.0,
                      "size": 2.0,
                      "fill": "black"
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "data": {
                    "x": [
                      "\\( a^2 \\)",
                      "\\( a^2 \\)"
                    ],
                    "g": [
                      "g1",
                      "g2"
                    ]
                  },
                  "mapping": {
                    "x": "x"
                  },
                  "data_meta": {
                    "series_annotations": [
                      {
                        "type": "str",
                        "column": "x"
                      },
                      {
                        "type": "str",
                        "column": "g"
                      }
                    ]
                  },
                  "ggtitle": {
                    "text": "Title: \"\\( a^2 \\)\"",
                    "subtitle": "Subtitle: \"\\( a^2 \\)\""
                  },
                  "caption": {
                    "text": "Caption: \"\\( a^2 \\)\""
                  },
                  "facet": {
                    "name": "grid",
                    "x": "g",
                    "x_order": 1.0,
                    "y_order": 1.0
                  },
                  "theme": {
                    "legend_position": "bottom",
                    "legend_title": {
                      "color": "green",
                      "family": "Times",
                      "face": "italic",
                      "size": 30.0,
                      "blank": false
                    }
                  },
                  "coord": {
                    "name": "polar"
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "name": "X: \"\\( a^2 \\)\"",
                      "aesthetic": "x"
                    },
                    {
                      "name": "Y: \"\\( a^2 \\)\"",
                      "aesthetic": "y"
                    },
                    {
                      "name": "\\( a^2 \\)",
                      "aesthetic": "color",
                      "discrete": true
                    }
                  ],
                  "layers": [
                    {
                      "geom": "bar",
                      "stat": "identity",
                      "mapping": {
                        "color": "g"
                      },
                      "data_meta": {},
                      "labels": {
                        "formats": [],
                        "variables": [
                          "x"
                        ]
                      },
                      "y": 1.0,
                      "size": 2.0,
                      "fill": "black"
                    }
                  ],
                  "metainfo_list": []
                }
              ]
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("latex_formulas_in_plot_elements_respect_global_and_element_specific_theme_text_settings.png", plotSpec)
    }

    @Test
    fun `geom_label aligns mixed text and inline latex formulas correctly`() {
        val spec = """
            {
              "ggsize": {
                "width": 1200.0,
                "height": 400.0
              },
              "kind": "subplots",
              "layout": {
                "ncol": 1.0,
                "nrow": 2.0,
                "name": "grid"
              },
              "figures": [
                {
                  "kind": "subplots",
                  "layout": {
                    "ncol": 3.0,
                    "nrow": 1.0,
                    "name": "grid"
                  },
                  "figures": [
                    {
                      "mapping": {},
                      "data_meta": {},
                      "theme": {
                        "name": "gray",
                        "axis": "blank"
                      },
                      "kind": "plot",
                      "scales": [],
                      "layers": [
                        {
                          "geom": "hline",
                          "mapping": {},
                          "tooltips": "none",
                          "data_meta": {},
                          "yintercept": 0.0,
                          "color": "salmon"
                        },
                        {
                          "geom": "vline",
                          "mapping": {},
                          "tooltips": "none",
                          "data_meta": {},
                          "xintercept": 0.0,
                          "color": "salmon"
                        },
                        {
                          "geom": "label",
                          "mapping": {},
                          "data_meta": {},
                          "x": 0.0,
                          "label": "AB-\\(a + \\frac{b + c}{d} + e\\)-CD",
                          "size": 10.0,
                          "color": "blue",
                          "hjust": "left"
                        }
                      ],
                      "metainfo_list": []
                    },
                    {
                      "mapping": {},
                      "data_meta": {},
                      "theme": {
                        "name": "gray",
                        "axis": "blank"
                      },
                      "kind": "plot",
                      "scales": [],
                      "layers": [
                        {
                          "geom": "hline",
                          "mapping": {},
                          "tooltips": "none",
                          "data_meta": {},
                          "yintercept": 0.0,
                          "color": "salmon"
                        },
                        {
                          "geom": "vline",
                          "mapping": {},
                          "tooltips": "none",
                          "data_meta": {},
                          "xintercept": 0.0,
                          "color": "salmon"
                        },
                        {
                          "geom": "label",
                          "mapping": {},
                          "data_meta": {},
                          "x": 0.0,
                          "label": "AB-\\(a + \\frac{b + c}{d} + e\\)-CD",
                          "size": 10.0,
                          "color": "blue",
                          "hjust": "middle"
                        }
                      ],
                      "metainfo_list": []
                    },
                    {
                      "mapping": {},
                      "data_meta": {},
                      "theme": {
                        "name": "gray",
                        "axis": "blank"
                      },
                      "kind": "plot",
                      "scales": [],
                      "layers": [
                        {
                          "geom": "hline",
                          "mapping": {},
                          "tooltips": "none",
                          "data_meta": {},
                          "yintercept": 0.0,
                          "color": "salmon"
                        },
                        {
                          "geom": "vline",
                          "mapping": {},
                          "tooltips": "none",
                          "data_meta": {},
                          "xintercept": 0.0,
                          "color": "salmon"
                        },
                        {
                          "geom": "label",
                          "mapping": {},
                          "data_meta": {},
                          "x": 0.0,
                          "label": "AB-\\(a + \\frac{b + c}{d} + e\\)-CD",
                          "size": 10.0,
                          "color": "blue",
                          "hjust": "right"
                        }
                      ],
                      "metainfo_list": []
                    }
                  ]
                },
                {
                  "kind": "subplots",
                  "layout": {
                    "ncol": 2.0,
                    "nrow": 2.0,
                    "name": "grid"
                  },
                  "figures": [
                    {
                      "mapping": {},
                      "data_meta": {},
                      "theme": {
                        "name": "gray",
                        "axis": "blank"
                      },
                      "kind": "plot",
                      "scales": [],
                      "layers": [
                        {
                          "geom": "hline",
                          "mapping": {},
                          "tooltips": "none",
                          "data_meta": {},
                          "yintercept": 0.0,
                          "color": "salmon"
                        },
                        {
                          "geom": "vline",
                          "mapping": {},
                          "tooltips": "none",
                          "data_meta": {},
                          "xintercept": 0.0,
                          "color": "salmon"
                        },
                        {
                          "geom": "label",
                          "mapping": {},
                          "data_meta": {},
                          "x": 0.0,
                          "label": "AAAAAA\\( \\frac{a + b}{c} + e \\)",
                          "size": 10.0,
                          "color": "blue",
                          "hjust": "left"
                        }
                      ],
                      "metainfo_list": []
                    },
                    {
                      "mapping": {},
                      "data_meta": {},
                      "theme": {
                        "name": "gray",
                        "axis": "blank"
                      },
                      "kind": "plot",
                      "scales": [],
                      "layers": [
                        {
                          "geom": "hline",
                          "mapping": {},
                          "tooltips": "none",
                          "data_meta": {},
                          "yintercept": 0.0,
                          "color": "salmon"
                        },
                        {
                          "geom": "vline",
                          "mapping": {},
                          "tooltips": "none",
                          "data_meta": {},
                          "xintercept": 0.0,
                          "color": "salmon"
                        },
                        {
                          "geom": "label",
                          "mapping": {},
                          "data_meta": {},
                          "x": 0.0,
                          "label": "\\( AAAAAA\\frac{a + b}{c} + e \\)",
                          "size": 10.0,
                          "color": "blue",
                          "hjust": "left"
                        }
                      ],
                      "metainfo_list": []
                    },
                    {
                      "mapping": {},
                      "data_meta": {},
                      "theme": {
                        "name": "gray",
                        "axis": "blank"
                      },
                      "kind": "plot",
                      "scales": [],
                      "layers": [
                        {
                          "geom": "hline",
                          "mapping": {},
                          "tooltips": "none",
                          "data_meta": {},
                          "yintercept": 0.0,
                          "color": "salmon"
                        },
                        {
                          "geom": "vline",
                          "mapping": {},
                          "tooltips": "none",
                          "data_meta": {},
                          "xintercept": 0.0,
                          "color": "salmon"
                        },
                        {
                          "geom": "label",
                          "mapping": {},
                          "data_meta": {},
                          "x": 0.0,
                          "label": "------\\( \\frac{a + b}{c} + e \\)",
                          "size": 10.0,
                          "color": "blue",
                          "hjust": "left"
                        }
                      ],
                      "metainfo_list": []
                    },
                    {
                      "mapping": {},
                      "data_meta": {},
                      "theme": {
                        "name": "gray",
                        "axis": "blank"
                      },
                      "kind": "plot",
                      "scales": [],
                      "layers": [
                        {
                          "geom": "hline",
                          "mapping": {},
                          "tooltips": "none",
                          "data_meta": {},
                          "yintercept": 0.0,
                          "color": "salmon"
                        },
                        {
                          "geom": "vline",
                          "mapping": {},
                          "tooltips": "none",
                          "data_meta": {},
                          "xintercept": 0.0,
                          "color": "salmon"
                        },
                        {
                          "geom": "label",
                          "mapping": {},
                          "data_meta": {},
                          "x": 0.0,
                          "label": "AAAAAAAAAAAA\\( \\frac{a + b}{c} + e \\)",
                          "size": 10.0,
                          "color": "blue",
                          "hjust": "left"
                        }
                      ],
                      "metainfo_list": []
                    }
                  ]
                }
              ]
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("geom_label_aligns_mixed_text_and_inline_latex_formulas_correctly.png", plotSpec)
    }

    @Test
    fun `geom_text renders multiline labels with latex formulas for different vjust values`() {
        val spec = """
            {
              "ggsize": {
                "width": 1500.0,
                "height": 500.0
              },
              "kind": "subplots",
              "layout": {
                "ncol": 9.0,
                "nrow": 3.0,
                "name": "grid"
              },
              "figures": [
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X",
                      "x": 0.0,
                      "vjust": 0.0,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X\nX",
                      "x": 0.0,
                      "vjust": 0.0,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.0,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\nX",
                      "x": 0.0,
                      "vjust": 0.0,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.0,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.0,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X\n\\(X + \\frac{X}{X}\\)\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.0,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\nX\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.0,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\n\\(X + \\frac{X}{X}\\)\nX",
                      "x": 0.0,
                      "vjust": 0.0,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X",
                      "x": 0.0,
                      "vjust": 0.5,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X\nX",
                      "x": 0.0,
                      "vjust": 0.5,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.5,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\nX",
                      "x": 0.0,
                      "vjust": 0.5,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.5,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.5,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X\n\\(X + \\frac{X}{X}\\)\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.5,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\nX\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.5,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\n\\(X + \\frac{X}{X}\\)\nX",
                      "x": 0.0,
                      "vjust": 0.5,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X",
                      "x": 0.0,
                      "vjust": 1.0,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X\nX",
                      "x": 0.0,
                      "vjust": 1.0,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 1.0,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\nX",
                      "x": 0.0,
                      "vjust": 1.0,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 1.0,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 1.0,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X\n\\(X + \\frac{X}{X}\\)\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 1.0,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\nX\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 1.0,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "text",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\n\\(X + \\frac{X}{X}\\)\nX",
                      "x": 0.0,
                      "vjust": 1.0,
                      "size": 8.0,
                      "alpha": 1.0
                    }
                  ],
                  "metainfo_list": []
                }
              ]
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("geom_text_renders_multiline_labels_with_latex_formulas_for_different_vjust_values.png", plotSpec)
    }

    @Test
    fun `geom_label renders multiline labels with latex formulas for different vjust values`() {
        val spec = """
            {
              "ggsize": {
                "width": 1500.0,
                "height": 500.0
              },
              "kind": "subplots",
              "layout": {
                "ncol": 9.0,
                "nrow": 3.0,
                "name": "grid"
              },
              "figures": [
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X",
                      "x": 0.0,
                      "vjust": 0.0,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X\nX",
                      "x": 0.0,
                      "vjust": 0.0,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.0,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\nX",
                      "x": 0.0,
                      "vjust": 0.0,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.0,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.0,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X\n\\(X + \\frac{X}{X}\\)\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.0,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\nX\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.0,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\n\\(X + \\frac{X}{X}\\)\nX",
                      "x": 0.0,
                      "vjust": 0.0,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X",
                      "x": 0.0,
                      "vjust": 0.5,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X\nX",
                      "x": 0.0,
                      "vjust": 0.5,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.5,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\nX",
                      "x": 0.0,
                      "vjust": 0.5,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.5,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.5,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X\n\\(X + \\frac{X}{X}\\)\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.5,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\nX\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 0.5,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\n\\(X + \\frac{X}{X}\\)\nX",
                      "x": 0.0,
                      "vjust": 0.5,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X",
                      "x": 0.0,
                      "vjust": 1.0,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X\nX",
                      "x": 0.0,
                      "vjust": 1.0,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 1.0,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\nX",
                      "x": 0.0,
                      "vjust": 1.0,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 1.0,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 1.0,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "X\n\\(X + \\frac{X}{X}\\)\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 1.0,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\nX\n\\(X + \\frac{X}{X}\\)",
                      "x": 0.0,
                      "vjust": 1.0,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                },
                {
                  "mapping": {},
                  "data_meta": {},
                  "theme": {
                    "name": "minimal",
                    "axis": "blank",
                    "panel_grid_major": {
                      "color": "sky_blue",
                      "blank": false
                    },
                    "panel_grid_minor": {
                      "color": "pale_green",
                      "blank": false
                    }
                  },
                  "kind": "plot",
                  "scales": [
                    {
                      "aesthetic": "y",
                      "breaks": [
                        -5.0,
                        -4.0,
                        -3.0,
                        -2.0,
                        -1.0,
                        0.0,
                        1.0,
                        2.0,
                        3.0,
                        4.0,
                        5.0
                      ],
                      "limits": [
                        -5.0,
                        5.0
                      ]
                    }
                  ],
                  "layers": [
                    {
                      "geom": "hline",
                      "mapping": {},
                      "data_meta": {},
                      "yintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "vline",
                      "mapping": {},
                      "data_meta": {},
                      "xintercept": 0.0,
                      "color": "salmon"
                    },
                    {
                      "geom": "label",
                      "mapping": {},
                      "data_meta": {},
                      "label": "\\(X + \\frac{X}{X}\\)\n\\(X + \\frac{X}{X}\\)\nX",
                      "x": 0.0,
                      "vjust": 1.0,
                      "size": 8.0,
                      "alpha": 0.25
                    }
                  ],
                  "metainfo_list": []
                }
              ]
            }
        """.trimMargin()

        val plotSpec = parsePlotSpec(spec)
        assertPlot("geom_label_renders_multiline_labels_with_latex_formulas_for_different_vjust_values.png", plotSpec)
    }
}