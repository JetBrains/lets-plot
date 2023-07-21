/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.api

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.typedGeometry.LineString
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.livemap.LiveMapTestBase
import org.jetbrains.letsPlot.livemap.ClientPoint
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.api.path
import org.jetbrains.letsPlot.livemap.api.paths
import org.jetbrains.letsPlot.livemap.geocoding.LocationCalculateSystem
import org.jetbrains.letsPlot.livemap.geocoding.LocationCounterSystem
import org.jetbrains.letsPlot.livemap.geocoding.MapLocationInitializationSystem
import org.jetbrains.letsPlot.livemap.geometry.WorldGeometryComponent
import org.jetbrains.letsPlot.livemap.mapengine.viewport.ViewportHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class PathWrappingTest : org.jetbrains.letsPlot.livemap.LiveMapTestBase() {
    private val path = listOf<Vec<LonLat>>(Vec(-73.7997, 70.6408), Vec(124.0012, 41.3256))

    override val systemsOrder = listOf(
        LocationCounterSystem::class,
        LocationCalculateSystem::class,
        MapLocationInitializationSystem::class,
    )

    override val size: org.jetbrains.letsPlot.livemap.ClientPoint =
        org.jetbrains.letsPlot.livemap.ClientPoint(960.0, 520.0)

    @Before
    override fun setUp() {
        super.setUp()

        addSystem(LocationCounterSystem(componentManager, myNeedLocation = true))
        addSystem(
            LocationCalculateSystem(
                ViewportHelper(org.jetbrains.letsPlot.livemap.World.DOMAIN, myLoopX = true, myLoopY = false),
                liveMapContext.mapProjection,
                componentManager
            )
        )
        addSystem(MapLocationInitializationSystem(componentManager, myZoom = null, myLocationRect = null))
    }

    @Test
    fun `path with flat=true - strait line wrapped across the antimeridian with proper initial position`() {
        layers {
            paths {
                path {
                    points = path
                    flat = true
                }
            }
        }

        update()

        with(liveMapContext) {
            assertThat(initialPosition)
                .isEqualTo(Vec<org.jetbrains.letsPlot.livemap.World>(17.849422222222216, 75.80654394955472))

            assertThat(initialZoom)
                .isEqualTo(3)
        }

        assertThat(getSingletonComponent<WorldGeometryComponent>().geometry!!.multiLineString)
            .containsExactly(
                LineString.of(Vec(75.52021333333334, 55.939578182385866), Vec(0.0, 81.955477463739)),
                LineString.of(Vec(256.0, 81.955477463739), Vec(216.1786311111111, 95.67350971672359)),
            )
    }

    @Test
    fun `path with flat=false - curve wrapped across the antimeridian with proper initial position`() {
        layers {
            paths {
                path {
                    points = path
                    flat = false
                }
            }
        }

        update()

        with(liveMapContext) {
            assertThat(initialPosition)
                .isEqualTo(Vec<org.jetbrains.letsPlot.livemap.World>(17.849422222222216, 75.80654394955472))

            assertThat(initialZoom)
                .isEqualTo(3)
        }

        with(getSingletonComponent<WorldGeometryComponent>().geometry) {
            // Wrapped across the antimeridian
            assertThat(multiLineString.size)
                .isEqualTo(2)

            // Check was the path resampled with mercator projection by total points count.
            // Pretty inaccurate method, but at least it works.
            // bbox can't be used for testing as mercators distortion is too low on straight lines.
            assertThat(multiLineString.sumOf(org.jetbrains.letsPlot.commons.intern.typedGeometry.LineString<org.jetbrains.letsPlot.livemap.World>::size))
                .isEqualTo(89)

            assertThat(multiLineString[0].bbox)
                .isEqualTo(
                    Rect.XYWH<org.jetbrains.letsPlot.livemap.World>(
                        Vec(0.0, 55.939578182385866),
                        Vec(75.52021333333334, 29.25638119066631)
                    )
                )

            assertThat(multiLineString[1].bbox)
                .isEqualTo(
                    Rect.XYWH<org.jetbrains.letsPlot.livemap.World>(
                        Vec(216.1786311111111, 85.19595937305218),
                        Vec(39.821368888888884, 10.47755034367141)
                    )
                )
        }
    }

    @Test
    fun `path with geodesic=true - geodesic with proper initial position`() {
        layers {
            paths {
                path {
                    points = path
                    geodesic = true
                }
            }
        }

        update()

        with(liveMapContext) {
            assertThat(initialPosition)
                .isEqualTo(Vec<org.jetbrains.letsPlot.livemap.World>(17.849422222222216, 75.80654394955472))

            assertThat(initialZoom)
                .isEqualTo(3)
        }

        with(getSingletonComponent<WorldGeometryComponent>().geometry) {
            assertThat(multiLineString.map(org.jetbrains.letsPlot.commons.intern.typedGeometry.LineString<org.jetbrains.letsPlot.livemap.World>::bbox))
                .containsExactly(
                    Rect.XYWH(0.0, 5.4495652533470364E-11, 75.52021333333334, 55.93957818233137),
                    Rect.XYWH(216.1786311111111, 3.9411088703014, 39.821368888888884, 91.73240084642218),
                )
        }
    }
}