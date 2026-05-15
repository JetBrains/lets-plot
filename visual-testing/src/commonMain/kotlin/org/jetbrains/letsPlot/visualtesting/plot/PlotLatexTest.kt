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

class PlotLatexTest(
    override val canvasPeer: CanvasPeer,
    override val imageComparer: ImageComparer,
) : PlotTestSuitBase() {
    init {
        registerTest(::plot_latex_geom_label_renders_latex_formulas_for_different_hjust_values)
    }

    fun plot_latex_geom_label_renders_latex_formulas_for_different_hjust_values(): Bitmap {
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
}
