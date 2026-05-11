/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("FunctionName")

package org.jetbrains.letsPlot.visualtesting.plot

import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.visualtesting.ImageComparer

class LatexTest(
    override val canvasPeer: CanvasPeer,
    override val imageComparer: ImageComparer,
) : PlotTestSuitBase() {
    init {
        registerTest(::`latex symbols`)
        registerTest(::`geom_label renders latex formulas for different hjust values`)
        registerTest(::`geom_label renders regression latex formulas for different hjust values`)
        registerTest(::`geom_label renders latex formulas for different nudge_x values`)
        registerTest(::`geom_text renders latex formulas for different hjust values`)
        registerTest(::`geom_text renders latex formulas for different nudge_x values`)
        registerTest(::`plot title renders latex formulas for different hjust values`)
        registerTest(::`geom_label renders multiline labels with latex formulas for different vjust and lineheight values`)
        registerTest(::`latex formulas render consistently across plot title axis labels and geom_text`)
        registerTest(::`facet strip labels render latex formulas`)
        registerTest(::`discrete legend labels render latex formulas`)
        registerTest(::`continuous legend labels render latex formulas`)
        registerTest(::`geom_text renders latex formulas for different appearance settings`)
        registerTest(::`latex formulas embedded in plain text render consistently across plot elements`)
        registerTest(::`geom_label aligns mixed text and inline latex formulas correctly`)
        registerTest(::`geom_text renders multiline labels with latex formulas for different vjust values`)
        registerTest(::`geom_label renders multiline labels with latex formulas for different vjust values`)
    }

    fun `latex symbols`(): Bitmap {
        val greekLetters = listOf(
            "Alpha",
            "Beta",
            "Gamma",
            "Delta",
            "Epsilon",
            "Zeta",
            "Eta",
            "Theta",
            "Iota",
            "Kappa",
            "Lambda",
            "Mu",
            "Nu",
            "Xi",
            "Omicron",
            "Pi",
            "Rho",
            "Sigma",
            "Tau",
            "Upsilon",
            "Phi",
            "Chi",
            "Psi",
            "Omega",
            "alpha",
            "beta",
            "gamma",
            "delta",
            "epsilon",
            "zeta",
            "eta",
            "theta",
            "iota",
            "kappa",
            "lambda",
            "mu",
            "nu",
            "xi",
            "omicron",
            "pi",
            "rho",
            "sigma",
            "tau",
            "upsilon",
            "phi",
            "chi",
            "psi",
            "omega",
        )
        val operations = listOf(
            "pm",
            //"mp",     // Maps to U+2213 (`∓`), but the bundled Noto fonts used by the AWT and Magick backends don't have this glyph
            "times",
            "div",
            "cdot",
        )
        val relations = emptyList<String>(
            //"leq",    // The bundled Noto fonts used by the AWT and Magick backends don't have this glyph
            //"geq",    // The bundled Noto fonts used by the AWT and Magick backends don't have this glyph
            //"neq",    // The bundled Noto fonts used by the AWT and Magick backends don't have this glyph
        )
        val miscellaneous = emptyList<String>(
            //"infty",  // The bundled Noto fonts used by the AWT and Magick backends don't have this glyph
        )
        val symbols = greekLetters + operations + relations + miscellaneous
        val cols = 12
        val xs = (0 until symbols.size).map { it % cols }
        val ys = (0 until symbols.size).map { it / cols }
        val labels = symbols.map { symbol -> """\\( \\$symbol \\)""" }
        val spec = """
            {
              "data": {
                "x": [${xs.joinToString(",")}],
                "y": [${ys.joinToString(",")}],
                "label": [${labels.joinToString(",") { "\"$it\"" }}]
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

        return paint(parseJson(spec))
    }

    fun `geom_label renders latex formulas for different hjust values`(): Bitmap {
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
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^8\\)",
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
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^8\\)",
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
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^8\\)",
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
                      "label": "\\(\\frac{g}{h}\\)",
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
                      "label": "\\(\\frac{g}{h}\\)",
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
                      "label": "\\(\\frac{g}{h}\\)",
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

        return paint(parseJson(spec))
    }

    fun `geom_label renders regression latex formulas for different hjust values`(): Bitmap {
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

        return paint(parseJson(spec))
    }

    fun `geom_label renders latex formulas for different nudge_x values`(): Bitmap {
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
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^8\\)",
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
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^8\\)",
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
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^8\\)",
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
                      "label": "\\(\\frac{g}{h}\\)",
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
                      "label": "\\(\\frac{g}{h}\\)",
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
                      "label": "\\(\\frac{g}{h}\\)",
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

        return paint(parseJson(spec))
    }

    fun `geom_text renders latex formulas for different hjust values`(): Bitmap {
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
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^8\\)",
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
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^8\\)",
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
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^8\\)",
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
                      "label": "\\(\\frac{g}{h}\\)",
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
                      "label": "\\(\\frac{g}{h}\\)",
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
                      "label": "\\(\\frac{g}{h}\\)",
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

        return paint(parseJson(spec))
    }

    fun `geom_text renders latex formulas for different nudge_x values`(): Bitmap {
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
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^8\\)",
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
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^8\\)",
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
                      "label": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^8\\)",
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
                      "label": "\\(\\frac{g}{h}\\)",
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
                      "label": "\\(\\frac{g}{h}\\)",
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
                      "label": "\\(\\frac{g}{h}\\)",
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

        return paint(parseJson(spec))
    }

    fun `plot title renders latex formulas for different hjust values`(): Bitmap {
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
                    "text": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^8\\)",
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
                    "text": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^8\\)",
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
                    "text": "\\(\\Alpha\\Beta_{\\gamma\\delta}\\Sigma^8\\)",
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
                    "text": "\\(\\frac{g}{h}\\)",
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
                    "text": "\\(\\frac{g}{h}\\)",
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
                    "text": "\\(\\frac{g}{h}\\)",
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

        return paint(parseJson(spec))
    }

    fun `geom_label renders multiline labels with latex formulas for different vjust and lineheight values`(): Bitmap {
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

        return paint(parseJson(spec))
    }

    fun `latex formulas render consistently across plot title axis labels and geom_text`(): Bitmap {
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

        return paint(parseJson(spec))
    }

    fun `facet strip labels render latex formulas`(): Bitmap {
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

        return paint(parseJson(spec))
    }

    fun `discrete legend labels render latex formulas`(): Bitmap {
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

        return paint(parseJson(spec))
    }

    fun `continuous legend labels render latex formulas`(): Bitmap {
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

        return paint(parseJson(spec))
    }

    fun `geom_text renders latex formulas for different appearance settings`(): Bitmap {
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

        return paint(parseJson(spec))
    }

    fun `latex formulas embedded in plain text render consistently across plot elements`(): Bitmap {
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
                      "F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} = 0 \\) (L)",
                      "F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} = 0 \\) (L)"
                    ],
                    "g": [
                      "F1: \\( \\Sigma \\cdot \\frac{ 20 - a^{b_1} }{\\rho} > 1 \\)",
                      "F2: \\( \\Delta \\cdot \\frac{\\xi}{ 30 - c_{d^2} } < -1 \\)"
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
                    "text": "Title: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} = 0 \\) (L)\"",
                    "subtitle": "Subtitle: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} = 0 \\) (L)\""
                  },
                  "caption": {
                    "text": "Caption: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} = 0 \\) (L)\""
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
                      "name": "X: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} = 0 \\) (L)\"",
                      "aesthetic": "x"
                    },
                    {
                      "name": "Y: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} = 0 \\) (L)\"",
                      "aesthetic": "y",
                      "limits": [
                        0.0,
                        3.0
                      ]
                    },
                    {
                      "name": "F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} = 0 \\) (L)",
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
                      "F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} = 0 \\) (L)",
                      "F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} = 0 \\) (L)"
                    ],
                    "g": [
                      "F1: \\( \\Sigma \\cdot \\frac{ 20 - a^{b_1} }{\\rho} > 1 \\)",
                      "F2: \\( \\Delta \\cdot \\frac{\\xi}{ 30 - c_{d^2} } < -1 \\)"
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
                    "text": "Title: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} = 0 \\) (L)\"",
                    "subtitle": "Subtitle: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} = 0 \\) (L)\""
                  },
                  "caption": {
                    "text": "Caption: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} = 0 \\) (L)\""
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
                      "name": "X: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} = 0 \\) (L)\"",
                      "aesthetic": "x"
                    },
                    {
                      "name": "Y: \"F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} = 0 \\) (L)\"",
                      "aesthetic": "y",
                      "limits": [
                        0.0,
                        3.0
                      ]
                    },
                    {
                      "name": "F: \\( \\Omega \\cdot \\frac{ 10 - a^{b_1} }{\\sigma} = 0 \\) (L)",
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

        return paint(parseJson(spec))
    }

    fun `geom_label aligns mixed text and inline latex formulas correctly`(): Bitmap {
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

        return paint(parseJson(spec))
    }

    fun `geom_text renders multiline labels with latex formulas for different vjust values`(): Bitmap {
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

        return paint(parseJson(spec))
    }

    fun `geom_label renders multiline labels with latex formulas for different vjust values`(): Bitmap {
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

        return paint(parseJson(spec))
    }
}