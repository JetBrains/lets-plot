/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.common.canvas

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.core.canvas.Canvas

const val dataUrl =
    """data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAYAAABw4pVUAAAABmJLR0QA/wD/AP+gvaeTAAAE5klEQVR4nO2cW4hNURjHxyWXJ5SSPE1KKRM1PMrcijyMzCYvePAiJZQXkcKTpCnMqxoeNINklEsawhMPLjUSUYzMiIhchhkM/8/+jtlznHPm7H32Wmuvtb5//Zp5mJp91n//1p69Zu9VVWVpgr61k8Bc0ABqwQww3vRxeRkaeFADjoEn4DpYD6aZPjYvg4GfDnaCN2AYDIGrYAmYYPr4vAoNOFgMLoLv4BeX0ge2U1mmj9GrsB3bQC+XMQAGwVfQxWWJJToSsaOLC6AyesBrLqeXyxJLdKSAHfT1MLjEBYklulLAjtzg14MdeSWJJapTxI5t9S0d7UWKEktUpYQdi+vqzvyuazr9vFBZYomiFLODivhbCBBLNGVMO3KIJXpSjh1iiaaUbYdYoidx7BBLFCe2HWKJ2iSxQyxRlMR2FLfkRRDezc8E40x/PutSiR15lpwHX8BncAXUgcmmP59VqdiO0ZZsCcL/KNLyfD/YB+aIJTGShh0RSxaBTvCJTekGTWJJmUnNjtGlbAaPxZIESdMOsaTCqLBDLKkgKuwQSxJGpR1iSYKotEMsiRkddoglMaLDjhKWXAMrwBTT45CJ6LQjUsom8DAIn3p8C1pBtVhSpdeOSCHzQBt4x1PXXbAaTDU9HkZjwo5IKau5iEEu5oj3lpiwI1JINZcgllBM2iGWFIhJO8SSvGTBDrEkkizYIZZwsmRHCUva+E9j918gzZIdeZa08k3id75ppJtHt18gzaIdkVJo+YSWUWg5hZZVaHmFllncfWwoi3ZECqEFRlpo7OepixYgNztrSZbtiJRCS/HdXliSZTu8s8QGO7yyxAY7vLHEJju8sMQmO0pYQo+i0iOpdr/OYKMdkVLooWx6OPszm3I+sP11BhvtyNG49STdqdPrCy8CF176sdmOiCXuvPRjsx3/cOXVOBfscMoSJ+xwxRKX7HDCEqfssN0SF+2w2hIn7bDVEpftsNISp+2wzRIf7LDKEi/ssMUSn+ywwhKv7Mi6JT7akWlLvLQjq5b4bEcmLfHajuKWmNkkTez4zxKzm6SJHf9ZYm6TNLGjoCXmtuwQO4qWon/LDrEjY5aIHRmyROwoqxB9logdZZei3hKxI1Yh6rd/Ejtil6Ju+yexI1Eh6rZ/EjsSl5L+lh1iR0WFpL9lh9hRcSnpWSJ2pFJIepaIHamVUrklYkeqhVRuidiReinJt38SO5QUknz7J7FDWSnxt38SO5QWEn/LDrFDeSnlb9khdmgppHxLxA5tpYxtidihtZCxLRE7tJdS2hK244LYoa2Q0pbgm93gVdSO+pWdN0wfuEvUt3S0N2w4tZ/A+M4GLeAmC0CPoJ5jMSZSIXfAEN9F3gLrwEKwQBE1oBYsBY2ssA7o5qw5CJcyWjSwBmwMwgewd/GJT+wBB4PwRpGmrJ/gNTgAZlEh78FvLqSHp6yzCqGz4TK4De6B+xp4wJ/tGXiuCXoa/iUP9psgXDqJ8iEIp6zhIJyd6DiXUSFPuaUf4Fswci1RyTf+fbr4yQwbIPe7Cx3PL/4ZEqIPrKJC6Cr/EQxoKiM3b5KZ+WeNKugMpTOVzlg6c3VZQkaSmWRoIXMfBSMWHQfzqRCaW49yMSqnqhynwQlwCOwNRuZWldAcTnM5zek0t+u4htC1qpnHt9h1bTn/bEBlgMl/ALtgE+8p1GZLAAAAAElFTkSuQmCC"""

class DrawImageAndClearRectDemoModel(
    canvas: Canvas,
    createSnapshot: (String) -> Async<Canvas.Snapshot>
) {
    init {
        createSnapshot(dataUrl).map { snapshot ->
            with(canvas.context2d) {
                drawImage(snapshot, 10.0, 10.0)
                drawImage(snapshot, 120.0, 35.0, 75.0, 75.0)
                drawImage(snapshot, 0.0, 0.0, 75.0, 75.0, 60.0, 120.0, 100.0, 100.0)

                clearRect(DoubleRectangle(85.0, 60.0, 75.0, 100.0))
            }
        }
    }
}