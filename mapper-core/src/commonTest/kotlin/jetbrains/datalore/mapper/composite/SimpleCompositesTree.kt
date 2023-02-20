/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.mapper.composite

internal class SimpleCompositesTree {

    val a: SimpleComposite
    lateinit var c: SimpleComposite
    lateinit var d: SimpleComposite
    lateinit var e: SimpleComposite
    lateinit var f: SimpleComposite
    lateinit var g: SimpleComposite
    lateinit var h: SimpleComposite
    lateinit var i: SimpleComposite
    lateinit var k: SimpleComposite
    lateinit var l: SimpleComposite
    lateinit var m: SimpleComposite
    lateinit var o: SimpleComposite
    lateinit var p: SimpleComposite
    lateinit var r: SimpleComposite
    lateinit var s: SimpleComposite
    lateinit var t: SimpleComposite
    lateinit var u: SimpleComposite
    lateinit var v: SimpleComposite
    lateinit var w: SimpleComposite
    lateinit var x: SimpleComposite
    lateinit var y: SimpleComposite

    init {
        a = SimpleComposite("a",
            SimpleComposite("b",
                SimpleComposite("e") { e = it },
                SimpleComposite("f") { f = it }
            ),
            SimpleComposite("c",
                SimpleComposite("g") { g = it }
            ) { c = it },
            SimpleComposite("d",
                SimpleComposite("h",
                    SimpleComposite("k") { k = it },
                    SimpleComposite("l") { l = it },
                    SimpleComposite("m",
                        SimpleComposite("r") { r = it },
                        SimpleComposite("s") { s = it },
                        SimpleComposite("t") { t = it }
                    ) { m = it }
                ) { h = it },
                SimpleComposite("i") { i = it },
                SimpleComposite("j",
                    SimpleComposite("o") { o = it },
                    SimpleComposite("p") { p = it },
                    SimpleComposite("q",
                        SimpleComposite("u") { u = it },
                        SimpleComposite("v") { v = it },
                        SimpleComposite("w") { w = it },
                        SimpleComposite("x") { x = it },
                        SimpleComposite("y") { y = it }
                    )
                )
            ) { d = it }
        )
    }
}
