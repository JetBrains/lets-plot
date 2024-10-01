/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite.data

object SeattleWeather {
    val json = """
        [
            {
                "date": "2012-01-01",
                "precipitation": 0.0,
                "temp_max": 12.8,
                "temp_min": 5.0,
                "wind": 4.7,
                "weather": "drizzle"
            },
            {
                "date": "2012-01-02",
                "precipitation": 10.9,
                "temp_max": 10.6,
                "temp_min": 2.8,
                "wind": 4.5,
                "weather": "rain"
            },
            {
                "date": "2012-01-03",
                "precipitation": 0.8,
                "temp_max": 11.7,
                "temp_min": 7.2,
                "wind": 2.3,
                "weather": "rain"
            },
            {
                "date": "2012-01-04",
                "precipitation": 20.3,
                "temp_max": 12.2,
                "temp_min": 5.6,
                "wind": 4.7,
                "weather": "rain"
            },
            {
                "date": "2012-01-05",
                "precipitation": 1.3,
                "temp_max": 8.9,
                "temp_min": 2.8,
                "wind": 6.1,
                "weather": "rain"
            },
            {
                "date": "2012-01-06",
                "precipitation": 2.5,
                "temp_max": 4.4,
                "temp_min": 2.2,
                "wind": 2.2,
                "weather": "rain"
            },
            {
                "date": "2012-01-07",
                "precipitation": 0.0,
                "temp_max": 7.2,
                "temp_min": 2.8,
                "wind": 2.3,
                "weather": "rain"
            },
            {
                "date": "2012-01-08",
                "precipitation": 0.0,
                "temp_max": 10.0,
                "temp_min": 2.8,
                "wind": 2.0,
                "weather": "sun"
            },
            {
                "date": "2012-01-09",
                "precipitation": 4.3,
                "temp_max": 9.4,
                "temp_min": 5.0,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2012-01-10",
                "precipitation": 1.0,
                "temp_max": 6.1,
                "temp_min": 0.6,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2012-01-11",
                "precipitation": 0.0,
                "temp_max": 6.1,
                "temp_min": -1.1,
                "wind": 5.1,
                "weather": "sun"
            },
            {
                "date": "2012-01-12",
                "precipitation": 0.0,
                "temp_max": 6.1,
                "temp_min": -1.7,
                "wind": 1.9,
                "weather": "sun"
            },
            {
                "date": "2012-01-13",
                "precipitation": 0.0,
                "temp_max": 5.0,
                "temp_min": -2.8,
                "wind": 1.3,
                "weather": "sun"
            },
            {
                "date": "2012-01-14",
                "precipitation": 4.1,
                "temp_max": 4.4,
                "temp_min": 0.6,
                "wind": 5.3,
                "weather": "snow"
            },
            {
                "date": "2012-01-15",
                "precipitation": 5.3,
                "temp_max": 1.1,
                "temp_min": -3.3,
                "wind": 3.2,
                "weather": "snow"
            },
            {
                "date": "2012-01-16",
                "precipitation": 2.5,
                "temp_max": 1.7,
                "temp_min": -2.8,
                "wind": 5.0,
                "weather": "snow"
            },
            {
                "date": "2012-01-17",
                "precipitation": 8.1,
                "temp_max": 3.3,
                "temp_min": 0.0,
                "wind": 5.6,
                "weather": "snow"
            },
            {
                "date": "2012-01-18",
                "precipitation": 19.8,
                "temp_max": 0.0,
                "temp_min": -2.8,
                "wind": 5.0,
                "weather": "snow"
            },
            {
                "date": "2012-01-19",
                "precipitation": 15.2,
                "temp_max": -1.1,
                "temp_min": -2.8,
                "wind": 1.6,
                "weather": "snow"
            },
            {
                "date": "2012-01-20",
                "precipitation": 13.5,
                "temp_max": 7.2,
                "temp_min": -1.1,
                "wind": 2.3,
                "weather": "snow"
            },
            {
                "date": "2012-01-21",
                "precipitation": 3.0,
                "temp_max": 8.3,
                "temp_min": 3.3,
                "wind": 8.2,
                "weather": "rain"
            },
            {
                "date": "2012-01-22",
                "precipitation": 6.1,
                "temp_max": 6.7,
                "temp_min": 2.2,
                "wind": 4.8,
                "weather": "rain"
            },
            {
                "date": "2012-01-23",
                "precipitation": 0.0,
                "temp_max": 8.3,
                "temp_min": 1.1,
                "wind": 3.6,
                "weather": "rain"
            },
            {
                "date": "2012-01-24",
                "precipitation": 8.6,
                "temp_max": 10.0,
                "temp_min": 2.2,
                "wind": 5.1,
                "weather": "rain"
            },
            {
                "date": "2012-01-25",
                "precipitation": 8.1,
                "temp_max": 8.9,
                "temp_min": 4.4,
                "wind": 5.4,
                "weather": "rain"
            },
            {
                "date": "2012-01-26",
                "precipitation": 4.8,
                "temp_max": 8.9,
                "temp_min": 1.1,
                "wind": 4.8,
                "weather": "rain"
            },
            {
                "date": "2012-01-27",
                "precipitation": 0.0,
                "temp_max": 6.7,
                "temp_min": -2.2,
                "wind": 1.4,
                "weather": "drizzle"
            },
            {
                "date": "2012-01-28",
                "precipitation": 0.0,
                "temp_max": 6.7,
                "temp_min": 0.6,
                "wind": 2.2,
                "weather": "rain"
            },
            {
                "date": "2012-01-29",
                "precipitation": 27.7,
                "temp_max": 9.4,
                "temp_min": 3.9,
                "wind": 4.5,
                "weather": "rain"
            },
            {
                "date": "2012-01-30",
                "precipitation": 3.6,
                "temp_max": 8.3,
                "temp_min": 6.1,
                "wind": 5.1,
                "weather": "rain"
            },
            {
                "date": "2012-01-31",
                "precipitation": 1.8,
                "temp_max": 9.4,
                "temp_min": 6.1,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2012-02-01",
                "precipitation": 13.5,
                "temp_max": 8.9,
                "temp_min": 3.3,
                "wind": 2.7,
                "weather": "rain"
            },
            {
                "date": "2012-02-02",
                "precipitation": 0.0,
                "temp_max": 8.3,
                "temp_min": 1.7,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2012-02-03",
                "precipitation": 0.0,
                "temp_max": 14.4,
                "temp_min": 2.2,
                "wind": 5.3,
                "weather": "sun"
            },
            {
                "date": "2012-02-04",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 5.0,
                "wind": 4.3,
                "weather": "sun"
            },
            {
                "date": "2012-02-05",
                "precipitation": 0.0,
                "temp_max": 13.9,
                "temp_min": 1.7,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2012-02-06",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 1.7,
                "wind": 5.0,
                "weather": "sun"
            },
            {
                "date": "2012-02-07",
                "precipitation": 0.3,
                "temp_max": 15.6,
                "temp_min": 7.8,
                "wind": 5.3,
                "weather": "rain"
            },
            {
                "date": "2012-02-08",
                "precipitation": 2.8,
                "temp_max": 10.0,
                "temp_min": 5.0,
                "wind": 2.7,
                "weather": "rain"
            },
            {
                "date": "2012-02-09",
                "precipitation": 2.5,
                "temp_max": 11.1,
                "temp_min": 7.8,
                "wind": 2.4,
                "weather": "rain"
            },
            {
                "date": "2012-02-10",
                "precipitation": 2.5,
                "temp_max": 12.8,
                "temp_min": 6.7,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2012-02-11",
                "precipitation": 0.8,
                "temp_max": 8.9,
                "temp_min": 5.6,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2012-02-12",
                "precipitation": 1.0,
                "temp_max": 8.3,
                "temp_min": 5.0,
                "wind": 1.3,
                "weather": "rain"
            },
            {
                "date": "2012-02-13",
                "precipitation": 11.4,
                "temp_max": 7.2,
                "temp_min": 4.4,
                "wind": 1.4,
                "weather": "rain"
            },
            {
                "date": "2012-02-14",
                "precipitation": 2.5,
                "temp_max": 6.7,
                "temp_min": 1.1,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2012-02-15",
                "precipitation": 0.0,
                "temp_max": 7.2,
                "temp_min": 0.6,
                "wind": 1.8,
                "weather": "drizzle"
            },
            {
                "date": "2012-02-16",
                "precipitation": 1.8,
                "temp_max": 7.2,
                "temp_min": 3.3,
                "wind": 2.1,
                "weather": "rain"
            },
            {
                "date": "2012-02-17",
                "precipitation": 17.3,
                "temp_max": 10.0,
                "temp_min": 4.4,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2012-02-18",
                "precipitation": 6.4,
                "temp_max": 6.7,
                "temp_min": 3.9,
                "wind": 8.1,
                "weather": "rain"
            },
            {
                "date": "2012-02-19",
                "precipitation": 0.0,
                "temp_max": 6.7,
                "temp_min": 2.2,
                "wind": 4.7,
                "weather": "sun"
            },
            {
                "date": "2012-02-20",
                "precipitation": 3.0,
                "temp_max": 7.8,
                "temp_min": 1.7,
                "wind": 2.9,
                "weather": "rain"
            },
            {
                "date": "2012-02-21",
                "precipitation": 0.8,
                "temp_max": 10.0,
                "temp_min": 7.8,
                "wind": 7.5,
                "weather": "rain"
            },
            {
                "date": "2012-02-22",
                "precipitation": 8.6,
                "temp_max": 10.0,
                "temp_min": 2.8,
                "wind": 5.9,
                "weather": "rain"
            },
            {
                "date": "2012-02-23",
                "precipitation": 0.0,
                "temp_max": 8.3,
                "temp_min": 2.8,
                "wind": 3.9,
                "weather": "sun"
            },
            {
                "date": "2012-02-24",
                "precipitation": 11.4,
                "temp_max": 6.7,
                "temp_min": 4.4,
                "wind": 3.5,
                "weather": "rain"
            },
            {
                "date": "2012-02-25",
                "precipitation": 0.0,
                "temp_max": 7.2,
                "temp_min": 2.8,
                "wind": 6.4,
                "weather": "rain"
            },
            {
                "date": "2012-02-26",
                "precipitation": 1.3,
                "temp_max": 5.0,
                "temp_min": -1.1,
                "wind": 3.4,
                "weather": "snow"
            },
            {
                "date": "2012-02-27",
                "precipitation": 0.0,
                "temp_max": 6.7,
                "temp_min": -2.2,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2012-02-28",
                "precipitation": 3.6,
                "temp_max": 6.7,
                "temp_min": -0.6,
                "wind": 4.2,
                "weather": "snow"
            },
            {
                "date": "2012-02-29",
                "precipitation": 0.8,
                "temp_max": 5.0,
                "temp_min": 1.1,
                "wind": 7.0,
                "weather": "snow"
            },
            {
                "date": "2012-03-01",
                "precipitation": 0.0,
                "temp_max": 6.1,
                "temp_min": 1.1,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2012-03-02",
                "precipitation": 2.0,
                "temp_max": 6.7,
                "temp_min": 3.9,
                "wind": 5.1,
                "weather": "rain"
            },
            {
                "date": "2012-03-03",
                "precipitation": 0.0,
                "temp_max": 12.2,
                "temp_min": 6.7,
                "wind": 7.0,
                "weather": "sun"
            },
            {
                "date": "2012-03-04",
                "precipitation": 0.0,
                "temp_max": 10.6,
                "temp_min": 6.7,
                "wind": 5.6,
                "weather": "rain"
            },
            {
                "date": "2012-03-05",
                "precipitation": 6.9,
                "temp_max": 7.8,
                "temp_min": 1.1,
                "wind": 6.2,
                "weather": "rain"
            },
            {
                "date": "2012-03-06",
                "precipitation": 0.5,
                "temp_max": 6.7,
                "temp_min": 0.0,
                "wind": 2.7,
                "weather": "snow"
            },
            {
                "date": "2012-03-07",
                "precipitation": 0.0,
                "temp_max": 8.9,
                "temp_min": -1.7,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2012-03-08",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 0.6,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2012-03-09",
                "precipitation": 3.6,
                "temp_max": 9.4,
                "temp_min": 5.0,
                "wind": 2.8,
                "weather": "rain"
            },
            {
                "date": "2012-03-10",
                "precipitation": 10.4,
                "temp_max": 7.2,
                "temp_min": 6.1,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2012-03-11",
                "precipitation": 13.7,
                "temp_max": 6.7,
                "temp_min": 2.8,
                "wind": 5.8,
                "weather": "rain"
            },
            {
                "date": "2012-03-12",
                "precipitation": 19.3,
                "temp_max": 8.3,
                "temp_min": 0.6,
                "wind": 6.2,
                "weather": "snow"
            },
            {
                "date": "2012-03-13",
                "precipitation": 9.4,
                "temp_max": 5.6,
                "temp_min": 0.6,
                "wind": 5.3,
                "weather": "snow"
            },
            {
                "date": "2012-03-14",
                "precipitation": 8.6,
                "temp_max": 7.8,
                "temp_min": 1.1,
                "wind": 4.7,
                "weather": "rain"
            },
            {
                "date": "2012-03-15",
                "precipitation": 23.9,
                "temp_max": 11.1,
                "temp_min": 5.6,
                "wind": 5.8,
                "weather": "snow"
            },
            {
                "date": "2012-03-16",
                "precipitation": 8.4,
                "temp_max": 8.9,
                "temp_min": 3.9,
                "wind": 5.1,
                "weather": "rain"
            },
            {
                "date": "2012-03-17",
                "precipitation": 9.4,
                "temp_max": 10.0,
                "temp_min": 0.6,
                "wind": 3.8,
                "weather": "snow"
            },
            {
                "date": "2012-03-18",
                "precipitation": 3.6,
                "temp_max": 5.0,
                "temp_min": -0.6,
                "wind": 2.7,
                "weather": "rain"
            },
            {
                "date": "2012-03-19",
                "precipitation": 2.0,
                "temp_max": 7.2,
                "temp_min": -1.1,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2012-03-20",
                "precipitation": 3.6,
                "temp_max": 7.8,
                "temp_min": 2.2,
                "wind": 6.4,
                "weather": "rain"
            },
            {
                "date": "2012-03-21",
                "precipitation": 1.3,
                "temp_max": 8.9,
                "temp_min": 1.1,
                "wind": 2.5,
                "weather": "rain"
            },
            {
                "date": "2012-03-22",
                "precipitation": 4.1,
                "temp_max": 10.0,
                "temp_min": 1.7,
                "wind": 2.1,
                "weather": "rain"
            },
            {
                "date": "2012-03-23",
                "precipitation": 0.0,
                "temp_max": 12.2,
                "temp_min": 0.6,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2012-03-24",
                "precipitation": 0.0,
                "temp_max": 15.0,
                "temp_min": 3.3,
                "wind": 5.2,
                "weather": "sun"
            },
            {
                "date": "2012-03-25",
                "precipitation": 0.0,
                "temp_max": 13.3,
                "temp_min": 2.2,
                "wind": 2.7,
                "weather": "rain"
            },
            {
                "date": "2012-03-26",
                "precipitation": 0.0,
                "temp_max": 12.8,
                "temp_min": 6.1,
                "wind": 4.3,
                "weather": "drizzle"
            },
            {
                "date": "2012-03-27",
                "precipitation": 4.8,
                "temp_max": 14.4,
                "temp_min": 6.7,
                "wind": 3.8,
                "weather": "rain"
            },
            {
                "date": "2012-03-28",
                "precipitation": 1.3,
                "temp_max": 10.6,
                "temp_min": 7.2,
                "wind": 5.9,
                "weather": "rain"
            },
            {
                "date": "2012-03-29",
                "precipitation": 27.4,
                "temp_max": 10.0,
                "temp_min": 6.1,
                "wind": 4.4,
                "weather": "rain"
            },
            {
                "date": "2012-03-30",
                "precipitation": 5.6,
                "temp_max": 9.4,
                "temp_min": 5.0,
                "wind": 4.7,
                "weather": "rain"
            },
            {
                "date": "2012-03-31",
                "precipitation": 13.2,
                "temp_max": 10.0,
                "temp_min": 2.8,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2012-04-01",
                "precipitation": 1.5,
                "temp_max": 8.9,
                "temp_min": 4.4,
                "wind": 6.8,
                "weather": "rain"
            },
            {
                "date": "2012-04-02",
                "precipitation": 0.0,
                "temp_max": 16.7,
                "temp_min": 4.4,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2012-04-03",
                "precipitation": 1.5,
                "temp_max": 11.7,
                "temp_min": 3.3,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2012-04-04",
                "precipitation": 0.0,
                "temp_max": 10.6,
                "temp_min": 2.8,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2012-04-05",
                "precipitation": 4.6,
                "temp_max": 9.4,
                "temp_min": 2.8,
                "wind": 1.8,
                "weather": "snow"
            },
            {
                "date": "2012-04-06",
                "precipitation": 0.3,
                "temp_max": 11.1,
                "temp_min": 3.3,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2012-04-07",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 1.7,
                "wind": 4.3,
                "weather": "sun"
            },
            {
                "date": "2012-04-08",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 7.2,
                "wind": 4.1,
                "weather": "sun"
            },
            {
                "date": "2012-04-09",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 6.1,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2012-04-10",
                "precipitation": 0.0,
                "temp_max": 17.8,
                "temp_min": 8.9,
                "wind": 3.2,
                "weather": "rain"
            },
            {
                "date": "2012-04-11",
                "precipitation": 2.3,
                "temp_max": 11.1,
                "temp_min": 7.2,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2012-04-12",
                "precipitation": 0.5,
                "temp_max": 13.9,
                "temp_min": 5.6,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2012-04-13",
                "precipitation": 0.0,
                "temp_max": 15.0,
                "temp_min": 3.9,
                "wind": 4.0,
                "weather": "drizzle"
            },
            {
                "date": "2012-04-14",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 3.3,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2012-04-15",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 7.2,
                "wind": 2.9,
                "weather": "rain"
            },
            {
                "date": "2012-04-16",
                "precipitation": 8.1,
                "temp_max": 13.3,
                "temp_min": 6.7,
                "wind": 5.8,
                "weather": "rain"
            },
            {
                "date": "2012-04-17",
                "precipitation": 1.8,
                "temp_max": 10.0,
                "temp_min": 4.4,
                "wind": 2.0,
                "weather": "rain"
            },
            {
                "date": "2012-04-18",
                "precipitation": 1.8,
                "temp_max": 13.3,
                "temp_min": 7.2,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2012-04-19",
                "precipitation": 10.9,
                "temp_max": 13.9,
                "temp_min": 5.0,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2012-04-20",
                "precipitation": 6.6,
                "temp_max": 13.3,
                "temp_min": 6.7,
                "wind": 2.7,
                "weather": "rain"
            },
            {
                "date": "2012-04-21",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 4.4,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2012-04-22",
                "precipitation": 0.0,
                "temp_max": 23.3,
                "temp_min": 8.3,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2012-04-23",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 8.9,
                "wind": 3.5,
                "weather": "sun"
            },
            {
                "date": "2012-04-24",
                "precipitation": 4.3,
                "temp_max": 13.9,
                "temp_min": 10.0,
                "wind": 2.8,
                "weather": "rain"
            },
            {
                "date": "2012-04-25",
                "precipitation": 10.7,
                "temp_max": 16.7,
                "temp_min": 8.9,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2012-04-26",
                "precipitation": 3.8,
                "temp_max": 13.9,
                "temp_min": 6.7,
                "wind": 5.2,
                "weather": "rain"
            },
            {
                "date": "2012-04-27",
                "precipitation": 0.8,
                "temp_max": 13.3,
                "temp_min": 6.1,
                "wind": 4.8,
                "weather": "rain"
            },
            {
                "date": "2012-04-28",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 8.3,
                "wind": 2.5,
                "weather": "drizzle"
            },
            {
                "date": "2012-04-29",
                "precipitation": 4.3,
                "temp_max": 15.6,
                "temp_min": 8.9,
                "wind": 1.6,
                "weather": "rain"
            },
            {
                "date": "2012-04-30",
                "precipitation": 4.3,
                "temp_max": 12.8,
                "temp_min": 7.2,
                "wind": 8.0,
                "weather": "rain"
            },
            {
                "date": "2012-05-01",
                "precipitation": 0.5,
                "temp_max": 11.7,
                "temp_min": 6.1,
                "wind": 6.4,
                "weather": "rain"
            },
            {
                "date": "2012-05-02",
                "precipitation": 0.5,
                "temp_max": 13.3,
                "temp_min": 5.6,
                "wind": 2.5,
                "weather": "rain"
            },
            {
                "date": "2012-05-03",
                "precipitation": 18.5,
                "temp_max": 11.1,
                "temp_min": 7.2,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2012-05-04",
                "precipitation": 1.8,
                "temp_max": 12.2,
                "temp_min": 6.1,
                "wind": 4.6,
                "weather": "rain"
            },
            {
                "date": "2012-05-05",
                "precipitation": 0.0,
                "temp_max": 13.3,
                "temp_min": 5.0,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2012-05-06",
                "precipitation": 0.0,
                "temp_max": 17.8,
                "temp_min": 5.0,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2012-05-07",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 6.1,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2012-05-08",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 9.4,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2012-05-09",
                "precipitation": 0.0,
                "temp_max": 13.3,
                "temp_min": 6.7,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2012-05-10",
                "precipitation": 0.0,
                "temp_max": 14.4,
                "temp_min": 3.9,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2012-05-11",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 4.4,
                "wind": 4.3,
                "weather": "sun"
            },
            {
                "date": "2012-05-12",
                "precipitation": 0.0,
                "temp_max": 24.4,
                "temp_min": 6.7,
                "wind": 3.4,
                "weather": "sun"
            },
            {
                "date": "2012-05-13",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 9.4,
                "wind": 4.2,
                "weather": "sun"
            },
            {
                "date": "2012-05-14",
                "precipitation": 0.0,
                "temp_max": 26.7,
                "temp_min": 12.8,
                "wind": 3.8,
                "weather": "sun"
            },
            {
                "date": "2012-05-15",
                "precipitation": 0.0,
                "temp_max": 24.4,
                "temp_min": 9.4,
                "wind": 4.1,
                "weather": "drizzle"
            },
            {
                "date": "2012-05-16",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 9.4,
                "wind": 3.5,
                "weather": "sun"
            },
            {
                "date": "2012-05-17",
                "precipitation": 0.0,
                "temp_max": 17.8,
                "temp_min": 6.7,
                "wind": 2.9,
                "weather": "rain"
            },
            {
                "date": "2012-05-18",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 7.8,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2012-05-19",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 7.2,
                "wind": 1.5,
                "weather": "sun"
            },
            {
                "date": "2012-05-20",
                "precipitation": 6.4,
                "temp_max": 14.4,
                "temp_min": 11.7,
                "wind": 1.3,
                "weather": "rain"
            },
            {
                "date": "2012-05-21",
                "precipitation": 14.0,
                "temp_max": 16.7,
                "temp_min": 10.0,
                "wind": 4.0,
                "weather": "rain"
            },
            {
                "date": "2012-05-22",
                "precipitation": 6.1,
                "temp_max": 12.8,
                "temp_min": 8.9,
                "wind": 4.8,
                "weather": "rain"
            },
            {
                "date": "2012-05-23",
                "precipitation": 0.3,
                "temp_max": 14.4,
                "temp_min": 8.9,
                "wind": 6.3,
                "weather": "rain"
            },
            {
                "date": "2012-05-24",
                "precipitation": 0.0,
                "temp_max": 17.2,
                "temp_min": 8.9,
                "wind": 3.3,
                "weather": "rain"
            },
            {
                "date": "2012-05-25",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 8.9,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2012-05-26",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 8.9,
                "wind": 3.6,
                "weather": "sun"
            },
            {
                "date": "2012-05-27",
                "precipitation": 0.0,
                "temp_max": 17.2,
                "temp_min": 11.7,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2012-05-28",
                "precipitation": 0.0,
                "temp_max": 16.7,
                "temp_min": 10.0,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2012-05-29",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 7.8,
                "wind": 1.8,
                "weather": "sun"
            },
            {
                "date": "2012-05-30",
                "precipitation": 0.3,
                "temp_max": 18.9,
                "temp_min": 11.1,
                "wind": 1.5,
                "weather": "rain"
            },
            {
                "date": "2012-05-31",
                "precipitation": 3.8,
                "temp_max": 17.8,
                "temp_min": 12.2,
                "wind": 2.7,
                "weather": "rain"
            },
            {
                "date": "2012-06-01",
                "precipitation": 6.6,
                "temp_max": 20.0,
                "temp_min": 12.8,
                "wind": 3.7,
                "weather": "rain"
            },
            {
                "date": "2012-06-02",
                "precipitation": 0.3,
                "temp_max": 18.9,
                "temp_min": 10.6,
                "wind": 3.7,
                "weather": "rain"
            },
            {
                "date": "2012-06-03",
                "precipitation": 0.0,
                "temp_max": 17.2,
                "temp_min": 9.4,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2012-06-04",
                "precipitation": 1.3,
                "temp_max": 12.8,
                "temp_min": 8.9,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2012-06-05",
                "precipitation": 16.0,
                "temp_max": 13.3,
                "temp_min": 8.3,
                "wind": 3.3,
                "weather": "rain"
            },
            {
                "date": "2012-06-06",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 6.1,
                "wind": 3.4,
                "weather": "sun"
            },
            {
                "date": "2012-06-07",
                "precipitation": 16.5,
                "temp_max": 16.1,
                "temp_min": 8.9,
                "wind": 3.5,
                "weather": "rain"
            },
            {
                "date": "2012-06-08",
                "precipitation": 1.5,
                "temp_max": 15.0,
                "temp_min": 8.3,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2012-06-09",
                "precipitation": 0.0,
                "temp_max": 17.2,
                "temp_min": 8.3,
                "wind": 4.7,
                "weather": "rain"
            },
            {
                "date": "2012-06-10",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 10.0,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2012-06-11",
                "precipitation": 0.0,
                "temp_max": 23.3,
                "temp_min": 10.0,
                "wind": 1.8,
                "weather": "rain"
            },
            {
                "date": "2012-06-12",
                "precipitation": 0.8,
                "temp_max": 18.3,
                "temp_min": 12.8,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2012-06-13",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 11.1,
                "wind": 4.3,
                "weather": "sun"
            },
            {
                "date": "2012-06-14",
                "precipitation": 0.0,
                "temp_max": 17.2,
                "temp_min": 10.0,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2012-06-15",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 9.4,
                "wind": 1.7,
                "weather": "sun"
            },
            {
                "date": "2012-06-16",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 15.0,
                "wind": 4.1,
                "weather": "rain"
            },
            {
                "date": "2012-06-17",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 11.7,
                "wind": 6.4,
                "weather": "sun"
            },
            {
                "date": "2012-06-18",
                "precipitation": 3.0,
                "temp_max": 17.2,
                "temp_min": 10.0,
                "wind": 3.8,
                "weather": "rain"
            },
            {
                "date": "2012-06-19",
                "precipitation": 1.0,
                "temp_max": 19.4,
                "temp_min": 10.0,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2012-06-20",
                "precipitation": 0.0,
                "temp_max": 24.4,
                "temp_min": 10.0,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2012-06-21",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 11.7,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2012-06-22",
                "precipitation": 15.7,
                "temp_max": 13.9,
                "temp_min": 11.7,
                "wind": 1.9,
                "weather": "rain"
            },
            {
                "date": "2012-06-23",
                "precipitation": 8.6,
                "temp_max": 15.6,
                "temp_min": 9.4,
                "wind": 2.5,
                "weather": "rain"
            },
            {
                "date": "2012-06-24",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 9.4,
                "wind": 2.0,
                "weather": "drizzle"
            },
            {
                "date": "2012-06-25",
                "precipitation": 0.5,
                "temp_max": 19.4,
                "temp_min": 11.1,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2012-06-26",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 10.6,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2012-06-27",
                "precipitation": 0.0,
                "temp_max": 22.8,
                "temp_min": 8.9,
                "wind": 1.8,
                "weather": "sun"
            },
            {
                "date": "2012-06-28",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 11.7,
                "wind": 2.5,
                "weather": "rain"
            },
            {
                "date": "2012-06-29",
                "precipitation": 0.3,
                "temp_max": 21.7,
                "temp_min": 15.0,
                "wind": 1.9,
                "weather": "rain"
            },
            {
                "date": "2012-06-30",
                "precipitation": 3.0,
                "temp_max": 20.0,
                "temp_min": 13.3,
                "wind": 2.4,
                "weather": "rain"
            },
            {
                "date": "2012-07-01",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 12.2,
                "wind": 2.3,
                "weather": "rain"
            },
            {
                "date": "2012-07-02",
                "precipitation": 2.0,
                "temp_max": 18.9,
                "temp_min": 11.7,
                "wind": 2.1,
                "weather": "rain"
            },
            {
                "date": "2012-07-03",
                "precipitation": 5.8,
                "temp_max": 18.3,
                "temp_min": 10.6,
                "wind": 6.0,
                "weather": "rain"
            },
            {
                "date": "2012-07-04",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 9.4,
                "wind": 3.8,
                "weather": "sun"
            },
            {
                "date": "2012-07-05",
                "precipitation": 0.0,
                "temp_max": 24.4,
                "temp_min": 10.6,
                "wind": 3.1,
                "weather": "drizzle"
            },
            {
                "date": "2012-07-06",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 11.1,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2012-07-07",
                "precipitation": 0.0,
                "temp_max": 26.7,
                "temp_min": 12.8,
                "wind": 3.8,
                "weather": "sun"
            },
            {
                "date": "2012-07-08",
                "precipitation": 0.0,
                "temp_max": 28.3,
                "temp_min": 14.4,
                "wind": 2.8,
                "weather": "rain"
            },
            {
                "date": "2012-07-09",
                "precipitation": 1.5,
                "temp_max": 25.0,
                "temp_min": 12.8,
                "wind": 2.0,
                "weather": "rain"
            },
            {
                "date": "2012-07-10",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 11.1,
                "wind": 2.3,
                "weather": "drizzle"
            },
            {
                "date": "2012-07-11",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 13.3,
                "wind": 2.9,
                "weather": "fog"
            },
            {
                "date": "2012-07-12",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 13.3,
                "wind": 2.7,
                "weather": "drizzle"
            },
            {
                "date": "2012-07-13",
                "precipitation": 0.5,
                "temp_max": 23.3,
                "temp_min": 13.9,
                "wind": 2.2,
                "weather": "rain"
            },
            {
                "date": "2012-07-14",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 15.0,
                "wind": 2.2,
                "weather": "rain"
            },
            {
                "date": "2012-07-15",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 13.3,
                "wind": 3.8,
                "weather": "rain"
            },
            {
                "date": "2012-07-16",
                "precipitation": 0.3,
                "temp_max": 26.1,
                "temp_min": 13.3,
                "wind": 2.5,
                "weather": "rain"
            },
            {
                "date": "2012-07-17",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 15.0,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2012-07-18",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 14.4,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2012-07-19",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 14.4,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2012-07-20",
                "precipitation": 15.2,
                "temp_max": 19.4,
                "temp_min": 13.9,
                "wind": 4.0,
                "weather": "rain"
            },
            {
                "date": "2012-07-21",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 13.9,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2012-07-22",
                "precipitation": 1.0,
                "temp_max": 20.6,
                "temp_min": 12.2,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2012-07-23",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 11.1,
                "wind": 3.3,
                "weather": "rain"
            },
            {
                "date": "2012-07-24",
                "precipitation": 0.0,
                "temp_max": 23.3,
                "temp_min": 12.2,
                "wind": 4.3,
                "weather": "sun"
            },
            {
                "date": "2012-07-25",
                "precipitation": 0.0,
                "temp_max": 26.7,
                "temp_min": 12.8,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2012-07-26",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 12.8,
                "wind": 2.2,
                "weather": "drizzle"
            },
            {
                "date": "2012-07-27",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 13.9,
                "wind": 2.8,
                "weather": "drizzle"
            },
            {
                "date": "2012-07-28",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 13.3,
                "wind": 1.7,
                "weather": "drizzle"
            },
            {
                "date": "2012-07-29",
                "precipitation": 0.0,
                "temp_max": 22.8,
                "temp_min": 15.0,
                "wind": 2.0,
                "weather": "sun"
            },
            {
                "date": "2012-07-30",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 13.3,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2012-07-31",
                "precipitation": 0.0,
                "temp_max": 22.8,
                "temp_min": 13.9,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2012-08-01",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 13.3,
                "wind": 2.2,
                "weather": "drizzle"
            },
            {
                "date": "2012-08-02",
                "precipitation": 0.0,
                "temp_max": 23.3,
                "temp_min": 12.2,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2012-08-03",
                "precipitation": 0.0,
                "temp_max": 27.2,
                "temp_min": 12.8,
                "wind": 3.9,
                "weather": "sun"
            },
            {
                "date": "2012-08-04",
                "precipitation": 0.0,
                "temp_max": 33.9,
                "temp_min": 16.7,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2012-08-05",
                "precipitation": 0.0,
                "temp_max": 33.9,
                "temp_min": 17.8,
                "wind": 1.9,
                "weather": "sun"
            },
            {
                "date": "2012-08-06",
                "precipitation": 0.0,
                "temp_max": 28.3,
                "temp_min": 15.6,
                "wind": 2.5,
                "weather": "rain"
            },
            {
                "date": "2012-08-07",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 15.0,
                "wind": 2.6,
                "weather": "drizzle"
            },
            {
                "date": "2012-08-08",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 15.0,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2012-08-09",
                "precipitation": 0.0,
                "temp_max": 24.4,
                "temp_min": 14.4,
                "wind": 3.8,
                "weather": "drizzle"
            },
            {
                "date": "2012-08-10",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 12.2,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2012-08-11",
                "precipitation": 0.0,
                "temp_max": 28.3,
                "temp_min": 13.3,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2012-08-12",
                "precipitation": 0.0,
                "temp_max": 30.6,
                "temp_min": 15.0,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2012-08-13",
                "precipitation": 0.0,
                "temp_max": 30.6,
                "temp_min": 15.0,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2012-08-14",
                "precipitation": 0.0,
                "temp_max": 28.9,
                "temp_min": 13.9,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2012-08-15",
                "precipitation": 0.0,
                "temp_max": 31.1,
                "temp_min": 16.7,
                "wind": 4.7,
                "weather": "sun"
            },
            {
                "date": "2012-08-16",
                "precipitation": 0.0,
                "temp_max": 34.4,
                "temp_min": 18.3,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2012-08-17",
                "precipitation": 0.0,
                "temp_max": 32.8,
                "temp_min": 16.1,
                "wind": 1.8,
                "weather": "sun"
            },
            {
                "date": "2012-08-18",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 14.4,
                "wind": 3.0,
                "weather": "drizzle"
            },
            {
                "date": "2012-08-19",
                "precipitation": 0.0,
                "temp_max": 23.3,
                "temp_min": 15.0,
                "wind": 2.7,
                "weather": "drizzle"
            },
            {
                "date": "2012-08-20",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 15.0,
                "wind": 1.9,
                "weather": "sun"
            },
            {
                "date": "2012-08-21",
                "precipitation": 0.0,
                "temp_max": 23.3,
                "temp_min": 13.3,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2012-08-22",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 13.3,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2012-08-23",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 13.9,
                "wind": 3.8,
                "weather": "sun"
            },
            {
                "date": "2012-08-24",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 10.0,
                "wind": 3.3,
                "weather": "sun"
            },
            {
                "date": "2012-08-25",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 11.7,
                "wind": 3.2,
                "weather": "sun"
            },
            {
                "date": "2012-08-26",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 12.2,
                "wind": 3.4,
                "weather": "sun"
            },
            {
                "date": "2012-08-27",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 13.3,
                "wind": 1.8,
                "weather": "sun"
            },
            {
                "date": "2012-08-28",
                "precipitation": 0.0,
                "temp_max": 22.8,
                "temp_min": 12.2,
                "wind": 3.2,
                "weather": "sun"
            },
            {
                "date": "2012-08-29",
                "precipitation": 0.0,
                "temp_max": 22.8,
                "temp_min": 13.3,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2012-08-30",
                "precipitation": 0.0,
                "temp_max": 22.8,
                "temp_min": 12.8,
                "wind": 1.9,
                "weather": "sun"
            },
            {
                "date": "2012-08-31",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 10.6,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2012-09-01",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 10.6,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2012-09-02",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 10.0,
                "wind": 2.0,
                "weather": "sun"
            },
            {
                "date": "2012-09-03",
                "precipitation": 0.0,
                "temp_max": 22.8,
                "temp_min": 12.8,
                "wind": 3.3,
                "weather": "sun"
            },
            {
                "date": "2012-09-04",
                "precipitation": 0.0,
                "temp_max": 24.4,
                "temp_min": 11.1,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2012-09-05",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 11.7,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2012-09-06",
                "precipitation": 0.0,
                "temp_max": 28.3,
                "temp_min": 14.4,
                "wind": 4.2,
                "weather": "sun"
            },
            {
                "date": "2012-09-07",
                "precipitation": 0.0,
                "temp_max": 32.2,
                "temp_min": 13.3,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2012-09-08",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 13.3,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2012-09-09",
                "precipitation": 0.3,
                "temp_max": 18.9,
                "temp_min": 13.9,
                "wind": 5.0,
                "weather": "rain"
            },
            {
                "date": "2012-09-10",
                "precipitation": 0.3,
                "temp_max": 20.0,
                "temp_min": 11.7,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2012-09-11",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 8.9,
                "wind": 4.2,
                "weather": "sun"
            },
            {
                "date": "2012-09-12",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 10.0,
                "wind": 5.6,
                "weather": "sun"
            },
            {
                "date": "2012-09-13",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 11.7,
                "wind": 3.6,
                "weather": "sun"
            },
            {
                "date": "2012-09-14",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 11.1,
                "wind": 1.5,
                "weather": "sun"
            },
            {
                "date": "2012-09-15",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 11.1,
                "wind": 1.9,
                "weather": "sun"
            },
            {
                "date": "2012-09-16",
                "precipitation": 0.0,
                "temp_max": 24.4,
                "temp_min": 9.4,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2012-09-17",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 11.7,
                "wind": 2.2,
                "weather": "fog"
            },
            {
                "date": "2012-09-18",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 11.7,
                "wind": 1.4,
                "weather": "sun"
            },
            {
                "date": "2012-09-19",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 11.7,
                "wind": 1.9,
                "weather": "drizzle"
            },
            {
                "date": "2012-09-20",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 10.0,
                "wind": 2.5,
                "weather": "drizzle"
            },
            {
                "date": "2012-09-21",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 12.8,
                "wind": 2.1,
                "weather": "drizzle"
            },
            {
                "date": "2012-09-22",
                "precipitation": 0.3,
                "temp_max": 19.4,
                "temp_min": 11.7,
                "wind": 1.1,
                "weather": "rain"
            },
            {
                "date": "2012-09-23",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 10.0,
                "wind": 1.4,
                "weather": "fog"
            },
            {
                "date": "2012-09-24",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 10.0,
                "wind": 1.8,
                "weather": "fog"
            },
            {
                "date": "2012-09-25",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 11.1,
                "wind": 1.7,
                "weather": "sun"
            },
            {
                "date": "2012-09-26",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 9.4,
                "wind": 1.7,
                "weather": "drizzle"
            },
            {
                "date": "2012-09-27",
                "precipitation": 0.0,
                "temp_max": 22.8,
                "temp_min": 10.0,
                "wind": 1.7,
                "weather": "drizzle"
            },
            {
                "date": "2012-09-28",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 12.2,
                "wind": 1.1,
                "weather": "rain"
            },
            {
                "date": "2012-09-29",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 12.2,
                "wind": 4.3,
                "weather": "sun"
            },
            {
                "date": "2012-09-30",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 7.8,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2012-10-01",
                "precipitation": 0.0,
                "temp_max": 23.3,
                "temp_min": 8.9,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2012-10-02",
                "precipitation": 0.0,
                "temp_max": 17.8,
                "temp_min": 10.0,
                "wind": 4.1,
                "weather": "sun"
            },
            {
                "date": "2012-10-03",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 7.8,
                "wind": 7.3,
                "weather": "sun"
            },
            {
                "date": "2012-10-04",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 8.3,
                "wind": 6.5,
                "weather": "sun"
            },
            {
                "date": "2012-10-05",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 8.9,
                "wind": 5.7,
                "weather": "sun"
            },
            {
                "date": "2012-10-06",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 7.8,
                "wind": 5.1,
                "weather": "sun"
            },
            {
                "date": "2012-10-07",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 7.8,
                "wind": 1.3,
                "weather": "sun"
            },
            {
                "date": "2012-10-08",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 7.8,
                "wind": 1.9,
                "weather": "sun"
            },
            {
                "date": "2012-10-09",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 8.9,
                "wind": 1.6,
                "weather": "drizzle"
            },
            {
                "date": "2012-10-10",
                "precipitation": 0.0,
                "temp_max": 12.2,
                "temp_min": 8.3,
                "wind": 1.4,
                "weather": "drizzle"
            },
            {
                "date": "2012-10-11",
                "precipitation": 0.0,
                "temp_max": 13.9,
                "temp_min": 7.2,
                "wind": 1.3,
                "weather": "drizzle"
            },
            {
                "date": "2012-10-12",
                "precipitation": 2.0,
                "temp_max": 13.9,
                "temp_min": 8.9,
                "wind": 4.6,
                "weather": "rain"
            },
            {
                "date": "2012-10-13",
                "precipitation": 4.8,
                "temp_max": 15.6,
                "temp_min": 12.2,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2012-10-14",
                "precipitation": 16.5,
                "temp_max": 17.8,
                "temp_min": 13.3,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2012-10-15",
                "precipitation": 7.9,
                "temp_max": 17.2,
                "temp_min": 11.1,
                "wind": 4.6,
                "weather": "rain"
            },
            {
                "date": "2012-10-16",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 8.3,
                "wind": 5.5,
                "weather": "sun"
            },
            {
                "date": "2012-10-17",
                "precipitation": 0.0,
                "temp_max": 14.4,
                "temp_min": 6.1,
                "wind": 1.6,
                "weather": "sun"
            },
            {
                "date": "2012-10-18",
                "precipitation": 20.8,
                "temp_max": 17.8,
                "temp_min": 6.7,
                "wind": 2.0,
                "weather": "rain"
            },
            {
                "date": "2012-10-19",
                "precipitation": 4.8,
                "temp_max": 15.0,
                "temp_min": 9.4,
                "wind": 5.3,
                "weather": "rain"
            },
            {
                "date": "2012-10-20",
                "precipitation": 0.5,
                "temp_max": 11.1,
                "temp_min": 6.1,
                "wind": 5.7,
                "weather": "rain"
            },
            {
                "date": "2012-10-21",
                "precipitation": 6.4,
                "temp_max": 11.7,
                "temp_min": 4.4,
                "wind": 2.7,
                "weather": "rain"
            },
            {
                "date": "2012-10-22",
                "precipitation": 8.9,
                "temp_max": 7.8,
                "temp_min": 3.3,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2012-10-23",
                "precipitation": 0.0,
                "temp_max": 11.1,
                "temp_min": 5.6,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2012-10-24",
                "precipitation": 7.1,
                "temp_max": 11.7,
                "temp_min": 6.1,
                "wind": 2.1,
                "weather": "rain"
            },
            {
                "date": "2012-10-25",
                "precipitation": 0.0,
                "temp_max": 11.7,
                "temp_min": 6.7,
                "wind": 1.5,
                "weather": "sun"
            },
            {
                "date": "2012-10-26",
                "precipitation": 1.5,
                "temp_max": 11.1,
                "temp_min": 7.2,
                "wind": 2.5,
                "weather": "rain"
            },
            {
                "date": "2012-10-27",
                "precipitation": 23.1,
                "temp_max": 14.4,
                "temp_min": 9.4,
                "wind": 5.1,
                "weather": "rain"
            },
            {
                "date": "2012-10-28",
                "precipitation": 6.1,
                "temp_max": 14.4,
                "temp_min": 10.0,
                "wind": 3.8,
                "weather": "rain"
            },
            {
                "date": "2012-10-29",
                "precipitation": 10.9,
                "temp_max": 15.6,
                "temp_min": 10.0,
                "wind": 4.9,
                "weather": "rain"
            },
            {
                "date": "2012-10-30",
                "precipitation": 34.5,
                "temp_max": 15.0,
                "temp_min": 12.2,
                "wind": 2.8,
                "weather": "rain"
            },
            {
                "date": "2012-10-31",
                "precipitation": 14.5,
                "temp_max": 15.6,
                "temp_min": 11.1,
                "wind": 2.7,
                "weather": "rain"
            },
            {
                "date": "2012-11-01",
                "precipitation": 9.7,
                "temp_max": 15.0,
                "temp_min": 10.6,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2012-11-02",
                "precipitation": 5.6,
                "temp_max": 15.0,
                "temp_min": 10.6,
                "wind": 1.0,
                "weather": "rain"
            },
            {
                "date": "2012-11-03",
                "precipitation": 0.5,
                "temp_max": 15.6,
                "temp_min": 11.1,
                "wind": 3.6,
                "weather": "rain"
            },
            {
                "date": "2012-11-04",
                "precipitation": 8.1,
                "temp_max": 17.8,
                "temp_min": 12.8,
                "wind": 3.8,
                "weather": "rain"
            },
            {
                "date": "2012-11-05",
                "precipitation": 0.8,
                "temp_max": 15.0,
                "temp_min": 7.8,
                "wind": 4.0,
                "weather": "rain"
            },
            {
                "date": "2012-11-06",
                "precipitation": 0.3,
                "temp_max": 12.8,
                "temp_min": 6.7,
                "wind": 3.5,
                "weather": "rain"
            },
            {
                "date": "2012-11-07",
                "precipitation": 0.0,
                "temp_max": 12.2,
                "temp_min": 3.9,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2012-11-08",
                "precipitation": 0.0,
                "temp_max": 10.0,
                "temp_min": 1.1,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2012-11-09",
                "precipitation": 0.0,
                "temp_max": 8.9,
                "temp_min": 1.1,
                "wind": 2.0,
                "weather": "rain"
            },
            {
                "date": "2012-11-10",
                "precipitation": 0.0,
                "temp_max": 7.8,
                "temp_min": -0.6,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2012-11-11",
                "precipitation": 15.2,
                "temp_max": 8.9,
                "temp_min": 1.1,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2012-11-12",
                "precipitation": 3.6,
                "temp_max": 12.8,
                "temp_min": 6.1,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2012-11-13",
                "precipitation": 5.3,
                "temp_max": 11.1,
                "temp_min": 7.8,
                "wind": 2.5,
                "weather": "rain"
            },
            {
                "date": "2012-11-14",
                "precipitation": 0.8,
                "temp_max": 11.1,
                "temp_min": 5.0,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2012-11-15",
                "precipitation": 0.0,
                "temp_max": 9.4,
                "temp_min": 2.8,
                "wind": 2.4,
                "weather": "drizzle"
            },
            {
                "date": "2012-11-16",
                "precipitation": 5.6,
                "temp_max": 9.4,
                "temp_min": 2.2,
                "wind": 1.6,
                "weather": "rain"
            },
            {
                "date": "2012-11-17",
                "precipitation": 6.1,
                "temp_max": 12.2,
                "temp_min": 6.1,
                "wind": 5.3,
                "weather": "rain"
            },
            {
                "date": "2012-11-18",
                "precipitation": 7.9,
                "temp_max": 10.0,
                "temp_min": 6.1,
                "wind": 4.9,
                "weather": "rain"
            },
            {
                "date": "2012-11-19",
                "precipitation": 54.1,
                "temp_max": 13.3,
                "temp_min": 8.3,
                "wind": 6.0,
                "weather": "rain"
            },
            {
                "date": "2012-11-20",
                "precipitation": 3.8,
                "temp_max": 11.1,
                "temp_min": 7.2,
                "wind": 4.2,
                "weather": "rain"
            },
            {
                "date": "2012-11-21",
                "precipitation": 11.2,
                "temp_max": 8.3,
                "temp_min": 3.9,
                "wind": 5.5,
                "weather": "rain"
            },
            {
                "date": "2012-11-22",
                "precipitation": 0.0,
                "temp_max": 8.9,
                "temp_min": 2.8,
                "wind": 1.5,
                "weather": "rain"
            },
            {
                "date": "2012-11-23",
                "precipitation": 32.0,
                "temp_max": 9.4,
                "temp_min": 6.1,
                "wind": 2.4,
                "weather": "rain"
            },
            {
                "date": "2012-11-24",
                "precipitation": 0.0,
                "temp_max": 8.9,
                "temp_min": 3.9,
                "wind": 1.2,
                "weather": "rain"
            },
            {
                "date": "2012-11-25",
                "precipitation": 0.0,
                "temp_max": 8.3,
                "temp_min": 1.1,
                "wind": 3.6,
                "weather": "drizzle"
            },
            {
                "date": "2012-11-26",
                "precipitation": 0.0,
                "temp_max": 9.4,
                "temp_min": 1.7,
                "wind": 3.8,
                "weather": "fog"
            },
            {
                "date": "2012-11-27",
                "precipitation": 0.0,
                "temp_max": 10.0,
                "temp_min": 1.7,
                "wind": 1.5,
                "weather": "sun"
            },
            {
                "date": "2012-11-28",
                "precipitation": 2.8,
                "temp_max": 9.4,
                "temp_min": 2.2,
                "wind": 2.9,
                "weather": "rain"
            },
            {
                "date": "2012-11-29",
                "precipitation": 1.5,
                "temp_max": 12.8,
                "temp_min": 7.8,
                "wind": 4.2,
                "weather": "rain"
            },
            {
                "date": "2012-11-30",
                "precipitation": 35.6,
                "temp_max": 15.0,
                "temp_min": 7.8,
                "wind": 4.6,
                "weather": "rain"
            },
            {
                "date": "2012-12-01",
                "precipitation": 4.1,
                "temp_max": 13.3,
                "temp_min": 8.3,
                "wind": 5.5,
                "weather": "rain"
            },
            {
                "date": "2012-12-02",
                "precipitation": 19.6,
                "temp_max": 8.3,
                "temp_min": 7.2,
                "wind": 6.2,
                "weather": "rain"
            },
            {
                "date": "2012-12-03",
                "precipitation": 13.0,
                "temp_max": 9.4,
                "temp_min": 7.2,
                "wind": 4.4,
                "weather": "rain"
            },
            {
                "date": "2012-12-04",
                "precipitation": 14.2,
                "temp_max": 11.7,
                "temp_min": 7.2,
                "wind": 6.2,
                "weather": "rain"
            },
            {
                "date": "2012-12-05",
                "precipitation": 1.5,
                "temp_max": 8.9,
                "temp_min": 4.4,
                "wind": 5.0,
                "weather": "rain"
            },
            {
                "date": "2012-12-06",
                "precipitation": 1.5,
                "temp_max": 7.2,
                "temp_min": 6.1,
                "wind": 5.1,
                "weather": "rain"
            },
            {
                "date": "2012-12-07",
                "precipitation": 1.0,
                "temp_max": 7.8,
                "temp_min": 3.3,
                "wind": 4.6,
                "weather": "rain"
            },
            {
                "date": "2012-12-08",
                "precipitation": 0.0,
                "temp_max": 6.7,
                "temp_min": 3.3,
                "wind": 2.0,
                "weather": "sun"
            },
            {
                "date": "2012-12-09",
                "precipitation": 1.5,
                "temp_max": 6.7,
                "temp_min": 2.8,
                "wind": 2.1,
                "weather": "rain"
            },
            {
                "date": "2012-12-10",
                "precipitation": 0.5,
                "temp_max": 7.2,
                "temp_min": 5.6,
                "wind": 1.8,
                "weather": "rain"
            },
            {
                "date": "2012-12-11",
                "precipitation": 3.0,
                "temp_max": 7.8,
                "temp_min": 5.6,
                "wind": 4.5,
                "weather": "rain"
            },
            {
                "date": "2012-12-12",
                "precipitation": 8.1,
                "temp_max": 6.7,
                "temp_min": 4.4,
                "wind": 2.0,
                "weather": "rain"
            },
            {
                "date": "2012-12-13",
                "precipitation": 2.3,
                "temp_max": 7.2,
                "temp_min": 3.3,
                "wind": 2.8,
                "weather": "rain"
            },
            {
                "date": "2012-12-14",
                "precipitation": 7.9,
                "temp_max": 6.1,
                "temp_min": 1.1,
                "wind": 1.7,
                "weather": "rain"
            },
            {
                "date": "2012-12-15",
                "precipitation": 5.3,
                "temp_max": 4.4,
                "temp_min": 0.6,
                "wind": 5.1,
                "weather": "snow"
            },
            {
                "date": "2012-12-16",
                "precipitation": 22.6,
                "temp_max": 6.7,
                "temp_min": 3.3,
                "wind": 5.5,
                "weather": "snow"
            },
            {
                "date": "2012-12-17",
                "precipitation": 2.0,
                "temp_max": 8.3,
                "temp_min": 1.7,
                "wind": 9.5,
                "weather": "rain"
            },
            {
                "date": "2012-12-18",
                "precipitation": 3.3,
                "temp_max": 3.9,
                "temp_min": 0.6,
                "wind": 5.3,
                "weather": "snow"
            },
            {
                "date": "2012-12-19",
                "precipitation": 13.7,
                "temp_max": 8.3,
                "temp_min": 1.7,
                "wind": 5.8,
                "weather": "snow"
            },
            {
                "date": "2012-12-20",
                "precipitation": 13.2,
                "temp_max": 7.2,
                "temp_min": 0.6,
                "wind": 3.7,
                "weather": "rain"
            },
            {
                "date": "2012-12-21",
                "precipitation": 1.8,
                "temp_max": 8.3,
                "temp_min": -1.7,
                "wind": 1.7,
                "weather": "rain"
            },
            {
                "date": "2012-12-22",
                "precipitation": 3.3,
                "temp_max": 8.3,
                "temp_min": 3.9,
                "wind": 3.5,
                "weather": "rain"
            },
            {
                "date": "2012-12-23",
                "precipitation": 6.6,
                "temp_max": 7.2,
                "temp_min": 3.3,
                "wind": 2.5,
                "weather": "rain"
            },
            {
                "date": "2012-12-24",
                "precipitation": 0.3,
                "temp_max": 5.6,
                "temp_min": 2.8,
                "wind": 2.8,
                "weather": "rain"
            },
            {
                "date": "2012-12-25",
                "precipitation": 13.5,
                "temp_max": 5.6,
                "temp_min": 2.8,
                "wind": 4.2,
                "weather": "snow"
            },
            {
                "date": "2012-12-26",
                "precipitation": 4.6,
                "temp_max": 6.7,
                "temp_min": 3.3,
                "wind": 4.9,
                "weather": "rain"
            },
            {
                "date": "2012-12-27",
                "precipitation": 4.1,
                "temp_max": 7.8,
                "temp_min": 3.3,
                "wind": 3.2,
                "weather": "rain"
            },
            {
                "date": "2012-12-28",
                "precipitation": 0.0,
                "temp_max": 8.3,
                "temp_min": 3.9,
                "wind": 1.7,
                "weather": "rain"
            },
            {
                "date": "2012-12-29",
                "precipitation": 1.5,
                "temp_max": 5.0,
                "temp_min": 3.3,
                "wind": 1.7,
                "weather": "rain"
            },
            {
                "date": "2012-12-30",
                "precipitation": 0.0,
                "temp_max": 4.4,
                "temp_min": 0.0,
                "wind": 1.8,
                "weather": "drizzle"
            },
            {
                "date": "2012-12-31",
                "precipitation": 0.0,
                "temp_max": 3.3,
                "temp_min": -1.1,
                "wind": 2.0,
                "weather": "drizzle"
            },
            {
                "date": "2013-01-01",
                "precipitation": 0.0,
                "temp_max": 5.0,
                "temp_min": -2.8,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2013-01-02",
                "precipitation": 0.0,
                "temp_max": 6.1,
                "temp_min": -1.1,
                "wind": 3.2,
                "weather": "sun"
            },
            {
                "date": "2013-01-03",
                "precipitation": 4.1,
                "temp_max": 6.7,
                "temp_min": -1.7,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2013-01-04",
                "precipitation": 2.5,
                "temp_max": 10.0,
                "temp_min": 2.2,
                "wind": 2.8,
                "weather": "rain"
            },
            {
                "date": "2013-01-05",
                "precipitation": 3.0,
                "temp_max": 6.7,
                "temp_min": 4.4,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2013-01-06",
                "precipitation": 2.0,
                "temp_max": 7.2,
                "temp_min": 2.8,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2013-01-07",
                "precipitation": 2.3,
                "temp_max": 10.0,
                "temp_min": 4.4,
                "wind": 7.3,
                "weather": "rain"
            },
            {
                "date": "2013-01-08",
                "precipitation": 16.3,
                "temp_max": 11.7,
                "temp_min": 5.6,
                "wind": 6.3,
                "weather": "rain"
            },
            {
                "date": "2013-01-09",
                "precipitation": 38.4,
                "temp_max": 10.0,
                "temp_min": 1.7,
                "wind": 5.1,
                "weather": "rain"
            },
            {
                "date": "2013-01-10",
                "precipitation": 0.3,
                "temp_max": 3.3,
                "temp_min": -0.6,
                "wind": 2.1,
                "weather": "snow"
            },
            {
                "date": "2013-01-11",
                "precipitation": 0.0,
                "temp_max": 2.8,
                "temp_min": -2.8,
                "wind": 1.9,
                "weather": "drizzle"
            },
            {
                "date": "2013-01-12",
                "precipitation": 0.0,
                "temp_max": 2.8,
                "temp_min": -3.9,
                "wind": 2.0,
                "weather": "sun"
            },
            {
                "date": "2013-01-13",
                "precipitation": 0.0,
                "temp_max": 2.2,
                "temp_min": -4.4,
                "wind": 1.5,
                "weather": "sun"
            },
            {
                "date": "2013-01-14",
                "precipitation": 0.0,
                "temp_max": 3.3,
                "temp_min": -2.2,
                "wind": 1.3,
                "weather": "sun"
            },
            {
                "date": "2013-01-15",
                "precipitation": 0.0,
                "temp_max": 6.7,
                "temp_min": -0.6,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2013-01-16",
                "precipitation": 0.0,
                "temp_max": 6.1,
                "temp_min": -3.9,
                "wind": 1.8,
                "weather": "drizzle"
            },
            {
                "date": "2013-01-17",
                "precipitation": 0.0,
                "temp_max": 3.9,
                "temp_min": -2.8,
                "wind": 1.0,
                "weather": "drizzle"
            },
            {
                "date": "2013-01-18",
                "precipitation": 0.0,
                "temp_max": 3.3,
                "temp_min": -1.1,
                "wind": 1.3,
                "weather": "drizzle"
            },
            {
                "date": "2013-01-19",
                "precipitation": 0.0,
                "temp_max": 1.1,
                "temp_min": -0.6,
                "wind": 1.9,
                "weather": "drizzle"
            },
            {
                "date": "2013-01-20",
                "precipitation": 0.0,
                "temp_max": 3.3,
                "temp_min": -0.6,
                "wind": 2.1,
                "weather": "drizzle"
            },
            {
                "date": "2013-01-21",
                "precipitation": 0.0,
                "temp_max": 2.2,
                "temp_min": -1.7,
                "wind": 1.1,
                "weather": "drizzle"
            },
            {
                "date": "2013-01-22",
                "precipitation": 0.0,
                "temp_max": 3.3,
                "temp_min": -1.7,
                "wind": 0.6,
                "weather": "drizzle"
            },
            {
                "date": "2013-01-23",
                "precipitation": 5.1,
                "temp_max": 7.2,
                "temp_min": 2.2,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2013-01-24",
                "precipitation": 5.8,
                "temp_max": 7.2,
                "temp_min": 1.1,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2013-01-25",
                "precipitation": 3.0,
                "temp_max": 10.6,
                "temp_min": 2.8,
                "wind": 2.1,
                "weather": "rain"
            },
            {
                "date": "2013-01-26",
                "precipitation": 2.3,
                "temp_max": 8.3,
                "temp_min": 3.9,
                "wind": 4.5,
                "weather": "rain"
            },
            {
                "date": "2013-01-27",
                "precipitation": 1.8,
                "temp_max": 5.6,
                "temp_min": 3.9,
                "wind": 4.5,
                "weather": "rain"
            },
            {
                "date": "2013-01-28",
                "precipitation": 7.9,
                "temp_max": 6.1,
                "temp_min": 3.3,
                "wind": 3.2,
                "weather": "rain"
            },
            {
                "date": "2013-01-29",
                "precipitation": 4.3,
                "temp_max": 8.3,
                "temp_min": 5.0,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2013-01-30",
                "precipitation": 3.6,
                "temp_max": 8.9,
                "temp_min": 6.7,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2013-01-31",
                "precipitation": 3.0,
                "temp_max": 9.4,
                "temp_min": 7.2,
                "wind": 4.0,
                "weather": "rain"
            },
            {
                "date": "2013-02-01",
                "precipitation": 0.3,
                "temp_max": 11.7,
                "temp_min": 5.0,
                "wind": 2.9,
                "weather": "rain"
            },
            {
                "date": "2013-02-02",
                "precipitation": 0.0,
                "temp_max": 6.1,
                "temp_min": 2.8,
                "wind": 2.0,
                "weather": "drizzle"
            },
            {
                "date": "2013-02-03",
                "precipitation": 2.3,
                "temp_max": 8.9,
                "temp_min": 2.8,
                "wind": 2.9,
                "weather": "rain"
            },
            {
                "date": "2013-02-04",
                "precipitation": 0.0,
                "temp_max": 10.6,
                "temp_min": 6.7,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2013-02-05",
                "precipitation": 3.3,
                "temp_max": 10.0,
                "temp_min": 6.7,
                "wind": 5.1,
                "weather": "rain"
            },
            {
                "date": "2013-02-06",
                "precipitation": 1.0,
                "temp_max": 10.6,
                "temp_min": 6.1,
                "wind": 4.5,
                "weather": "rain"
            },
            {
                "date": "2013-02-07",
                "precipitation": 1.3,
                "temp_max": 9.4,
                "temp_min": 3.3,
                "wind": 4.1,
                "weather": "rain"
            },
            {
                "date": "2013-02-08",
                "precipitation": 0.0,
                "temp_max": 7.8,
                "temp_min": 2.2,
                "wind": 1.3,
                "weather": "sun"
            },
            {
                "date": "2013-02-09",
                "precipitation": 0.3,
                "temp_max": 8.3,
                "temp_min": 4.4,
                "wind": 1.3,
                "weather": "rain"
            },
            {
                "date": "2013-02-10",
                "precipitation": 0.0,
                "temp_max": 8.9,
                "temp_min": 1.7,
                "wind": 2.0,
                "weather": "drizzle"
            },
            {
                "date": "2013-02-11",
                "precipitation": 0.3,
                "temp_max": 8.3,
                "temp_min": 4.4,
                "wind": 1.4,
                "weather": "rain"
            },
            {
                "date": "2013-02-12",
                "precipitation": 1.0,
                "temp_max": 11.1,
                "temp_min": 7.2,
                "wind": 5.6,
                "weather": "rain"
            },
            {
                "date": "2013-02-13",
                "precipitation": 2.3,
                "temp_max": 9.4,
                "temp_min": 7.2,
                "wind": 4.1,
                "weather": "rain"
            },
            {
                "date": "2013-02-14",
                "precipitation": 1.0,
                "temp_max": 9.4,
                "temp_min": 5.6,
                "wind": 2.2,
                "weather": "rain"
            },
            {
                "date": "2013-02-15",
                "precipitation": 0.0,
                "temp_max": 13.3,
                "temp_min": 5.0,
                "wind": 2.4,
                "weather": "drizzle"
            },
            {
                "date": "2013-02-16",
                "precipitation": 0.0,
                "temp_max": 11.1,
                "temp_min": 3.9,
                "wind": 5.6,
                "weather": "rain"
            },
            {
                "date": "2013-02-17",
                "precipitation": 0.0,
                "temp_max": 9.4,
                "temp_min": 4.4,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2013-02-18",
                "precipitation": 0.0,
                "temp_max": 7.8,
                "temp_min": 3.9,
                "wind": 1.9,
                "weather": "rain"
            },
            {
                "date": "2013-02-19",
                "precipitation": 0.0,
                "temp_max": 10.6,
                "temp_min": 1.7,
                "wind": 3.4,
                "weather": "sun"
            },
            {
                "date": "2013-02-20",
                "precipitation": 1.5,
                "temp_max": 7.8,
                "temp_min": 1.1,
                "wind": 2.1,
                "weather": "rain"
            },
            {
                "date": "2013-02-21",
                "precipitation": 0.5,
                "temp_max": 6.7,
                "temp_min": 3.9,
                "wind": 6.2,
                "weather": "rain"
            },
            {
                "date": "2013-02-22",
                "precipitation": 9.4,
                "temp_max": 7.8,
                "temp_min": 3.9,
                "wind": 8.1,
                "weather": "rain"
            },
            {
                "date": "2013-02-23",
                "precipitation": 0.3,
                "temp_max": 10.0,
                "temp_min": 3.9,
                "wind": 4.6,
                "weather": "rain"
            },
            {
                "date": "2013-02-24",
                "precipitation": 0.0,
                "temp_max": 8.9,
                "temp_min": 5.0,
                "wind": 5.5,
                "weather": "rain"
            },
            {
                "date": "2013-02-25",
                "precipitation": 2.3,
                "temp_max": 10.6,
                "temp_min": 3.3,
                "wind": 7.1,
                "weather": "rain"
            },
            {
                "date": "2013-02-26",
                "precipitation": 0.5,
                "temp_max": 8.9,
                "temp_min": 3.9,
                "wind": 3.8,
                "weather": "rain"
            },
            {
                "date": "2013-02-27",
                "precipitation": 4.6,
                "temp_max": 10.0,
                "temp_min": 4.4,
                "wind": 1.8,
                "weather": "rain"
            },
            {
                "date": "2013-02-28",
                "precipitation": 8.1,
                "temp_max": 11.7,
                "temp_min": 6.7,
                "wind": 3.8,
                "weather": "rain"
            },
            {
                "date": "2013-03-01",
                "precipitation": 4.1,
                "temp_max": 15.0,
                "temp_min": 11.1,
                "wind": 5.4,
                "weather": "rain"
            },
            {
                "date": "2013-03-02",
                "precipitation": 0.8,
                "temp_max": 13.9,
                "temp_min": 5.0,
                "wind": 4.5,
                "weather": "rain"
            },
            {
                "date": "2013-03-03",
                "precipitation": 0.0,
                "temp_max": 11.1,
                "temp_min": 2.2,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2013-03-04",
                "precipitation": 0.0,
                "temp_max": 13.3,
                "temp_min": 0.0,
                "wind": 3.9,
                "weather": "sun"
            },
            {
                "date": "2013-03-05",
                "precipitation": 0.0,
                "temp_max": 9.4,
                "temp_min": 6.1,
                "wind": 2.4,
                "weather": "rain"
            },
            {
                "date": "2013-03-06",
                "precipitation": 11.9,
                "temp_max": 7.2,
                "temp_min": 5.0,
                "wind": 4.1,
                "weather": "rain"
            },
            {
                "date": "2013-03-07",
                "precipitation": 7.4,
                "temp_max": 12.2,
                "temp_min": 5.0,
                "wind": 2.5,
                "weather": "rain"
            },
            {
                "date": "2013-03-08",
                "precipitation": 0.0,
                "temp_max": 11.7,
                "temp_min": 2.2,
                "wind": 2.6,
                "weather": "drizzle"
            },
            {
                "date": "2013-03-09",
                "precipitation": 0.0,
                "temp_max": 12.8,
                "temp_min": 1.1,
                "wind": 1.3,
                "weather": "fog"
            },
            {
                "date": "2013-03-10",
                "precipitation": 0.8,
                "temp_max": 7.8,
                "temp_min": 3.9,
                "wind": 1.6,
                "weather": "rain"
            },
            {
                "date": "2013-03-11",
                "precipitation": 1.3,
                "temp_max": 10.6,
                "temp_min": 6.1,
                "wind": 1.1,
                "weather": "rain"
            },
            {
                "date": "2013-03-12",
                "precipitation": 2.0,
                "temp_max": 12.8,
                "temp_min": 10.0,
                "wind": 5.7,
                "weather": "rain"
            },
            {
                "date": "2013-03-13",
                "precipitation": 2.3,
                "temp_max": 11.7,
                "temp_min": 9.4,
                "wind": 3.7,
                "weather": "rain"
            },
            {
                "date": "2013-03-14",
                "precipitation": 2.8,
                "temp_max": 11.7,
                "temp_min": 9.4,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2013-03-15",
                "precipitation": 0.0,
                "temp_max": 14.4,
                "temp_min": 8.9,
                "wind": 4.3,
                "weather": "rain"
            },
            {
                "date": "2013-03-16",
                "precipitation": 4.3,
                "temp_max": 10.6,
                "temp_min": 4.4,
                "wind": 6.4,
                "weather": "rain"
            },
            {
                "date": "2013-03-17",
                "precipitation": 0.0,
                "temp_max": 8.9,
                "temp_min": 3.9,
                "wind": 6.1,
                "weather": "sun"
            },
            {
                "date": "2013-03-18",
                "precipitation": 0.0,
                "temp_max": 11.7,
                "temp_min": 3.9,
                "wind": 5.9,
                "weather": "rain"
            },
            {
                "date": "2013-03-19",
                "precipitation": 11.7,
                "temp_max": 12.8,
                "temp_min": 1.7,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2013-03-20",
                "precipitation": 9.9,
                "temp_max": 11.1,
                "temp_min": 4.4,
                "wind": 7.6,
                "weather": "rain"
            },
            {
                "date": "2013-03-21",
                "precipitation": 8.1,
                "temp_max": 10.0,
                "temp_min": 2.2,
                "wind": 4.9,
                "weather": "snow"
            },
            {
                "date": "2013-03-22",
                "precipitation": 0.0,
                "temp_max": 9.4,
                "temp_min": 0.6,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2013-03-23",
                "precipitation": 0.0,
                "temp_max": 10.0,
                "temp_min": 1.1,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2013-03-24",
                "precipitation": 0.0,
                "temp_max": 12.2,
                "temp_min": 0.6,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2013-03-25",
                "precipitation": 0.0,
                "temp_max": 16.7,
                "temp_min": 4.4,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2013-03-26",
                "precipitation": 0.0,
                "temp_max": 16.7,
                "temp_min": 6.1,
                "wind": 1.7,
                "weather": "sun"
            },
            {
                "date": "2013-03-27",
                "precipitation": 0.3,
                "temp_max": 13.3,
                "temp_min": 7.2,
                "wind": 1.6,
                "weather": "rain"
            },
            {
                "date": "2013-03-28",
                "precipitation": 2.0,
                "temp_max": 16.1,
                "temp_min": 8.3,
                "wind": 1.3,
                "weather": "rain"
            },
            {
                "date": "2013-03-29",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 7.8,
                "wind": 2.5,
                "weather": "rain"
            },
            {
                "date": "2013-03-30",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 5.6,
                "wind": 4.4,
                "weather": "drizzle"
            },
            {
                "date": "2013-03-31",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 6.7,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2013-04-01",
                "precipitation": 0.0,
                "temp_max": 17.2,
                "temp_min": 8.3,
                "wind": 3.6,
                "weather": "sun"
            },
            {
                "date": "2013-04-02",
                "precipitation": 0.0,
                "temp_max": 13.9,
                "temp_min": 8.9,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2013-04-03",
                "precipitation": 0.0,
                "temp_max": 16.7,
                "temp_min": 7.8,
                "wind": 1.6,
                "weather": "sun"
            },
            {
                "date": "2013-04-04",
                "precipitation": 8.4,
                "temp_max": 14.4,
                "temp_min": 10.0,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2013-04-05",
                "precipitation": 18.5,
                "temp_max": 13.9,
                "temp_min": 10.0,
                "wind": 5.6,
                "weather": "rain"
            },
            {
                "date": "2013-04-06",
                "precipitation": 12.7,
                "temp_max": 12.2,
                "temp_min": 7.2,
                "wind": 5.0,
                "weather": "rain"
            },
            {
                "date": "2013-04-07",
                "precipitation": 39.1,
                "temp_max": 8.3,
                "temp_min": 5.0,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2013-04-08",
                "precipitation": 0.8,
                "temp_max": 13.3,
                "temp_min": 6.1,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2013-04-09",
                "precipitation": 0.0,
                "temp_max": 12.2,
                "temp_min": 6.1,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2013-04-10",
                "precipitation": 9.4,
                "temp_max": 15.0,
                "temp_min": 8.9,
                "wind": 6.4,
                "weather": "rain"
            },
            {
                "date": "2013-04-11",
                "precipitation": 1.5,
                "temp_max": 12.2,
                "temp_min": 6.7,
                "wind": 3.8,
                "weather": "rain"
            },
            {
                "date": "2013-04-12",
                "precipitation": 9.7,
                "temp_max": 7.8,
                "temp_min": 4.4,
                "wind": 4.6,
                "weather": "rain"
            },
            {
                "date": "2013-04-13",
                "precipitation": 9.4,
                "temp_max": 10.6,
                "temp_min": 3.3,
                "wind": 5.7,
                "weather": "rain"
            },
            {
                "date": "2013-04-14",
                "precipitation": 5.8,
                "temp_max": 12.8,
                "temp_min": 4.4,
                "wind": 2.3,
                "weather": "rain"
            },
            {
                "date": "2013-04-15",
                "precipitation": 0.0,
                "temp_max": 13.9,
                "temp_min": 4.4,
                "wind": 2.4,
                "weather": "fog"
            },
            {
                "date": "2013-04-16",
                "precipitation": 0.3,
                "temp_max": 13.9,
                "temp_min": 3.3,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2013-04-17",
                "precipitation": 0.0,
                "temp_max": 15.0,
                "temp_min": 3.9,
                "wind": 3.3,
                "weather": "drizzle"
            },
            {
                "date": "2013-04-18",
                "precipitation": 5.3,
                "temp_max": 11.7,
                "temp_min": 6.7,
                "wind": 4.0,
                "weather": "rain"
            },
            {
                "date": "2013-04-19",
                "precipitation": 20.6,
                "temp_max": 13.3,
                "temp_min": 9.4,
                "wind": 4.9,
                "weather": "rain"
            },
            {
                "date": "2013-04-20",
                "precipitation": 0.0,
                "temp_max": 13.9,
                "temp_min": 8.3,
                "wind": 5.8,
                "weather": "sun"
            },
            {
                "date": "2013-04-21",
                "precipitation": 3.3,
                "temp_max": 12.2,
                "temp_min": 6.7,
                "wind": 4.1,
                "weather": "rain"
            },
            {
                "date": "2013-04-22",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 5.0,
                "wind": 4.3,
                "weather": "sun"
            },
            {
                "date": "2013-04-23",
                "precipitation": 0.0,
                "temp_max": 17.8,
                "temp_min": 3.9,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2013-04-24",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 6.1,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2013-04-25",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 6.7,
                "wind": 1.1,
                "weather": "sun"
            },
            {
                "date": "2013-04-26",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 8.3,
                "wind": 2.2,
                "weather": "fog"
            },
            {
                "date": "2013-04-27",
                "precipitation": 0.0,
                "temp_max": 13.9,
                "temp_min": 10.6,
                "wind": 5.9,
                "weather": "sun"
            },
            {
                "date": "2013-04-28",
                "precipitation": 1.0,
                "temp_max": 15.0,
                "temp_min": 9.4,
                "wind": 5.2,
                "weather": "rain"
            },
            {
                "date": "2013-04-29",
                "precipitation": 3.8,
                "temp_max": 13.9,
                "temp_min": 6.7,
                "wind": 4.2,
                "weather": "rain"
            },
            {
                "date": "2013-04-30",
                "precipitation": 0.0,
                "temp_max": 12.8,
                "temp_min": 4.4,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2013-05-01",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 3.3,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2013-05-02",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 6.7,
                "wind": 4.0,
                "weather": "sun"
            },
            {
                "date": "2013-05-03",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 9.4,
                "wind": 4.9,
                "weather": "sun"
            },
            {
                "date": "2013-05-04",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 11.1,
                "wind": 6.5,
                "weather": "sun"
            },
            {
                "date": "2013-05-05",
                "precipitation": 0.0,
                "temp_max": 28.9,
                "temp_min": 11.7,
                "wind": 5.3,
                "weather": "sun"
            },
            {
                "date": "2013-05-06",
                "precipitation": 0.0,
                "temp_max": 30.6,
                "temp_min": 12.2,
                "wind": 2.0,
                "weather": "sun"
            },
            {
                "date": "2013-05-07",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 11.1,
                "wind": 3.3,
                "weather": "sun"
            },
            {
                "date": "2013-05-08",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 11.1,
                "wind": 1.9,
                "weather": "sun"
            },
            {
                "date": "2013-05-09",
                "precipitation": 0.0,
                "temp_max": 22.8,
                "temp_min": 10.0,
                "wind": 1.3,
                "weather": "sun"
            },
            {
                "date": "2013-05-10",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 9.4,
                "wind": 1.0,
                "weather": "sun"
            },
            {
                "date": "2013-05-11",
                "precipitation": 0.0,
                "temp_max": 27.2,
                "temp_min": 12.2,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2013-05-12",
                "precipitation": 6.6,
                "temp_max": 21.7,
                "temp_min": 13.9,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2013-05-13",
                "precipitation": 3.3,
                "temp_max": 18.9,
                "temp_min": 9.4,
                "wind": 5.0,
                "weather": "rain"
            },
            {
                "date": "2013-05-14",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 7.8,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2013-05-15",
                "precipitation": 1.0,
                "temp_max": 17.2,
                "temp_min": 8.9,
                "wind": 2.3,
                "weather": "rain"
            },
            {
                "date": "2013-05-16",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 12.2,
                "wind": 2.7,
                "weather": "fog"
            },
            {
                "date": "2013-05-17",
                "precipitation": 0.5,
                "temp_max": 17.2,
                "temp_min": 11.7,
                "wind": 3.7,
                "weather": "rain"
            },
            {
                "date": "2013-05-18",
                "precipitation": 0.0,
                "temp_max": 16.7,
                "temp_min": 11.1,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2013-05-19",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 10.6,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2013-05-20",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 9.4,
                "wind": 1.8,
                "weather": "sun"
            },
            {
                "date": "2013-05-21",
                "precipitation": 13.7,
                "temp_max": 15.6,
                "temp_min": 8.3,
                "wind": 4.8,
                "weather": "rain"
            },
            {
                "date": "2013-05-22",
                "precipitation": 13.7,
                "temp_max": 11.1,
                "temp_min": 7.2,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2013-05-23",
                "precipitation": 4.1,
                "temp_max": 12.2,
                "temp_min": 6.7,
                "wind": 1.9,
                "weather": "rain"
            },
            {
                "date": "2013-05-24",
                "precipitation": 0.3,
                "temp_max": 16.7,
                "temp_min": 8.9,
                "wind": 2.7,
                "weather": "rain"
            },
            {
                "date": "2013-05-25",
                "precipitation": 0.0,
                "temp_max": 17.8,
                "temp_min": 10.0,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2013-05-26",
                "precipitation": 1.5,
                "temp_max": 18.3,
                "temp_min": 10.6,
                "wind": 2.2,
                "weather": "rain"
            },
            {
                "date": "2013-05-27",
                "precipitation": 9.7,
                "temp_max": 16.7,
                "temp_min": 11.1,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2013-05-28",
                "precipitation": 0.5,
                "temp_max": 17.2,
                "temp_min": 11.7,
                "wind": 2.8,
                "weather": "rain"
            },
            {
                "date": "2013-05-29",
                "precipitation": 5.6,
                "temp_max": 16.1,
                "temp_min": 9.4,
                "wind": 4.0,
                "weather": "rain"
            },
            {
                "date": "2013-05-30",
                "precipitation": 0.0,
                "temp_max": 16.7,
                "temp_min": 9.4,
                "wind": 5.3,
                "weather": "sun"
            },
            {
                "date": "2013-05-31",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 11.1,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2013-06-01",
                "precipitation": 0.0,
                "temp_max": 22.8,
                "temp_min": 12.2,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2013-06-02",
                "precipitation": 1.0,
                "temp_max": 20.6,
                "temp_min": 12.2,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2013-06-03",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 11.1,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2013-06-04",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 12.2,
                "wind": 3.4,
                "weather": "sun"
            },
            {
                "date": "2013-06-05",
                "precipitation": 0.0,
                "temp_max": 26.7,
                "temp_min": 14.4,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2013-06-06",
                "precipitation": 0.0,
                "temp_max": 26.7,
                "temp_min": 12.2,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2013-06-07",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 13.3,
                "wind": 3.2,
                "weather": "sun"
            },
            {
                "date": "2013-06-08",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 12.8,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2013-06-09",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 11.1,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2013-06-10",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 11.7,
                "wind": 3.2,
                "weather": "sun"
            },
            {
                "date": "2013-06-11",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 10.0,
                "wind": 5.7,
                "weather": "sun"
            },
            {
                "date": "2013-06-12",
                "precipitation": 0.3,
                "temp_max": 20.6,
                "temp_min": 11.7,
                "wind": 4.2,
                "weather": "rain"
            },
            {
                "date": "2013-06-13",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 11.7,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2013-06-14",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 12.2,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2013-06-15",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 10.0,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2013-06-16",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 12.8,
                "wind": 3.4,
                "weather": "sun"
            },
            {
                "date": "2013-06-17",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 13.9,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2013-06-18",
                "precipitation": 0.3,
                "temp_max": 23.3,
                "temp_min": 13.3,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2013-06-19",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 12.8,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2013-06-20",
                "precipitation": 3.0,
                "temp_max": 17.2,
                "temp_min": 12.8,
                "wind": 5.0,
                "weather": "rain"
            },
            {
                "date": "2013-06-21",
                "precipitation": 0.3,
                "temp_max": 20.6,
                "temp_min": 12.2,
                "wind": 1.5,
                "weather": "rain"
            },
            {
                "date": "2013-06-22",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 11.7,
                "wind": 1.7,
                "weather": "sun"
            },
            {
                "date": "2013-06-23",
                "precipitation": 7.9,
                "temp_max": 22.2,
                "temp_min": 15.0,
                "wind": 2.1,
                "weather": "rain"
            },
            {
                "date": "2013-06-24",
                "precipitation": 4.8,
                "temp_max": 21.1,
                "temp_min": 13.9,
                "wind": 3.7,
                "weather": "rain"
            },
            {
                "date": "2013-06-25",
                "precipitation": 9.9,
                "temp_max": 23.3,
                "temp_min": 14.4,
                "wind": 2.8,
                "weather": "rain"
            },
            {
                "date": "2013-06-26",
                "precipitation": 2.0,
                "temp_max": 22.2,
                "temp_min": 15.0,
                "wind": 2.3,
                "weather": "rain"
            },
            {
                "date": "2013-06-27",
                "precipitation": 3.6,
                "temp_max": 21.1,
                "temp_min": 16.7,
                "wind": 1.3,
                "weather": "rain"
            },
            {
                "date": "2013-06-28",
                "precipitation": 0.0,
                "temp_max": 30.6,
                "temp_min": 16.1,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2013-06-29",
                "precipitation": 0.0,
                "temp_max": 30.0,
                "temp_min": 18.3,
                "wind": 1.7,
                "weather": "sun"
            },
            {
                "date": "2013-06-30",
                "precipitation": 0.0,
                "temp_max": 33.9,
                "temp_min": 17.2,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2013-07-01",
                "precipitation": 0.0,
                "temp_max": 31.7,
                "temp_min": 18.3,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2013-07-02",
                "precipitation": 0.0,
                "temp_max": 28.3,
                "temp_min": 15.6,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2013-07-03",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 16.7,
                "wind": 3.2,
                "weather": "sun"
            },
            {
                "date": "2013-07-04",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 13.9,
                "wind": 2.2,
                "weather": "fog"
            },
            {
                "date": "2013-07-05",
                "precipitation": 0.0,
                "temp_max": 23.3,
                "temp_min": 13.9,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2013-07-06",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 13.3,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2013-07-07",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 13.9,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2013-07-08",
                "precipitation": 0.0,
                "temp_max": 26.7,
                "temp_min": 13.3,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2013-07-09",
                "precipitation": 0.0,
                "temp_max": 30.0,
                "temp_min": 15.0,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2013-07-10",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 13.9,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2013-07-11",
                "precipitation": 0.0,
                "temp_max": 22.8,
                "temp_min": 12.2,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2013-07-12",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 13.3,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2013-07-13",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 11.1,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2013-07-14",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 12.8,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2013-07-15",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 14.4,
                "wind": 4.6,
                "weather": "sun"
            },
            {
                "date": "2013-07-16",
                "precipitation": 0.0,
                "temp_max": 31.1,
                "temp_min": 18.3,
                "wind": 4.1,
                "weather": "sun"
            },
            {
                "date": "2013-07-17",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 15.0,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2013-07-18",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 13.9,
                "wind": 2.0,
                "weather": "sun"
            },
            {
                "date": "2013-07-19",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 13.3,
                "wind": 1.9,
                "weather": "sun"
            },
            {
                "date": "2013-07-20",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 13.3,
                "wind": 2.0,
                "weather": "sun"
            },
            {
                "date": "2013-07-21",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 12.8,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2013-07-22",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 13.3,
                "wind": 2.4,
                "weather": "fog"
            },
            {
                "date": "2013-07-23",
                "precipitation": 0.0,
                "temp_max": 31.1,
                "temp_min": 13.9,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2013-07-24",
                "precipitation": 0.0,
                "temp_max": 31.1,
                "temp_min": 14.4,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2013-07-25",
                "precipitation": 0.0,
                "temp_max": 31.1,
                "temp_min": 12.8,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2013-07-26",
                "precipitation": 0.0,
                "temp_max": 31.1,
                "temp_min": 14.4,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2013-07-27",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 12.8,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2013-07-28",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 12.2,
                "wind": 3.4,
                "weather": "fog"
            },
            {
                "date": "2013-07-29",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 13.3,
                "wind": 1.4,
                "weather": "sun"
            },
            {
                "date": "2013-07-30",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 13.3,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2013-07-31",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 13.3,
                "wind": 1.8,
                "weather": "sun"
            },
            {
                "date": "2013-08-01",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 13.3,
                "wind": 3.9,
                "weather": "sun"
            },
            {
                "date": "2013-08-02",
                "precipitation": 2.0,
                "temp_max": 17.2,
                "temp_min": 15.0,
                "wind": 2.0,
                "weather": "rain"
            },
            {
                "date": "2013-08-03",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 15.6,
                "wind": 2.4,
                "weather": "fog"
            },
            {
                "date": "2013-08-04",
                "precipitation": 0.0,
                "temp_max": 28.9,
                "temp_min": 15.0,
                "wind": 3.4,
                "weather": "sun"
            },
            {
                "date": "2013-08-05",
                "precipitation": 0.0,
                "temp_max": 30.0,
                "temp_min": 15.0,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2013-08-06",
                "precipitation": 0.0,
                "temp_max": 30.6,
                "temp_min": 13.9,
                "wind": 1.4,
                "weather": "sun"
            },
            {
                "date": "2013-08-07",
                "precipitation": 0.0,
                "temp_max": 31.1,
                "temp_min": 13.9,
                "wind": 1.9,
                "weather": "sun"
            },
            {
                "date": "2013-08-08",
                "precipitation": 0.0,
                "temp_max": 28.3,
                "temp_min": 14.4,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2013-08-09",
                "precipitation": 0.0,
                "temp_max": 28.3,
                "temp_min": 14.4,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2013-08-10",
                "precipitation": 2.3,
                "temp_max": 25.6,
                "temp_min": 15.0,
                "wind": 2.9,
                "weather": "rain"
            },
            {
                "date": "2013-08-11",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 14.4,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2013-08-12",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 16.1,
                "wind": 1.9,
                "weather": "sun"
            },
            {
                "date": "2013-08-13",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 15.0,
                "wind": 1.8,
                "weather": "sun"
            },
            {
                "date": "2013-08-14",
                "precipitation": 0.8,
                "temp_max": 27.2,
                "temp_min": 15.0,
                "wind": 2.0,
                "weather": "rain"
            },
            {
                "date": "2013-08-15",
                "precipitation": 1.8,
                "temp_max": 21.1,
                "temp_min": 17.2,
                "wind": 1.0,
                "weather": "rain"
            },
            {
                "date": "2013-08-16",
                "precipitation": 0.0,
                "temp_max": 28.9,
                "temp_min": 16.1,
                "wind": 2.2,
                "weather": "fog"
            },
            {
                "date": "2013-08-17",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 17.2,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2013-08-18",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 15.6,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2013-08-19",
                "precipitation": 0.0,
                "temp_max": 26.7,
                "temp_min": 15.6,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2013-08-20",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 16.1,
                "wind": 4.6,
                "weather": "sun"
            },
            {
                "date": "2013-08-21",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 15.0,
                "wind": 4.3,
                "weather": "sun"
            },
            {
                "date": "2013-08-22",
                "precipitation": 0.0,
                "temp_max": 28.9,
                "temp_min": 15.0,
                "wind": 1.9,
                "weather": "sun"
            },
            {
                "date": "2013-08-23",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 16.1,
                "wind": 4.1,
                "weather": "sun"
            },
            {
                "date": "2013-08-24",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 16.7,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2013-08-25",
                "precipitation": 0.3,
                "temp_max": 22.2,
                "temp_min": 16.1,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2013-08-26",
                "precipitation": 1.0,
                "temp_max": 24.4,
                "temp_min": 16.1,
                "wind": 1.9,
                "weather": "rain"
            },
            {
                "date": "2013-08-27",
                "precipitation": 1.3,
                "temp_max": 26.7,
                "temp_min": 17.2,
                "wind": 1.4,
                "weather": "rain"
            },
            {
                "date": "2013-08-28",
                "precipitation": 5.6,
                "temp_max": 26.7,
                "temp_min": 15.6,
                "wind": 1.3,
                "weather": "rain"
            },
            {
                "date": "2013-08-29",
                "precipitation": 19.3,
                "temp_max": 23.9,
                "temp_min": 18.3,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2013-08-30",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 16.1,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2013-08-31",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 13.9,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2013-09-01",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 15.6,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2013-09-02",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 17.2,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2013-09-03",
                "precipitation": 2.3,
                "temp_max": 25.0,
                "temp_min": 16.7,
                "wind": 1.7,
                "weather": "rain"
            },
            {
                "date": "2013-09-04",
                "precipitation": 0.3,
                "temp_max": 22.8,
                "temp_min": 16.1,
                "wind": 2.4,
                "weather": "rain"
            },
            {
                "date": "2013-09-05",
                "precipitation": 27.7,
                "temp_max": 20.0,
                "temp_min": 15.6,
                "wind": 2.5,
                "weather": "rain"
            },
            {
                "date": "2013-09-06",
                "precipitation": 21.3,
                "temp_max": 21.7,
                "temp_min": 16.1,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2013-09-07",
                "precipitation": 0.0,
                "temp_max": 23.3,
                "temp_min": 17.2,
                "wind": 2.0,
                "weather": "sun"
            },
            {
                "date": "2013-09-08",
                "precipitation": 0.0,
                "temp_max": 26.7,
                "temp_min": 14.4,
                "wind": 1.5,
                "weather": "fog"
            },
            {
                "date": "2013-09-09",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 13.9,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2013-09-10",
                "precipitation": 0.0,
                "temp_max": 26.7,
                "temp_min": 15.0,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2013-09-11",
                "precipitation": 0.0,
                "temp_max": 33.9,
                "temp_min": 16.1,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2013-09-12",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 15.0,
                "wind": 1.7,
                "weather": "sun"
            },
            {
                "date": "2013-09-13",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 15.6,
                "wind": 2.0,
                "weather": "sun"
            },
            {
                "date": "2013-09-14",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 15.6,
                "wind": 1.4,
                "weather": "fog"
            },
            {
                "date": "2013-09-15",
                "precipitation": 3.3,
                "temp_max": 18.9,
                "temp_min": 14.4,
                "wind": 2.2,
                "weather": "rain"
            },
            {
                "date": "2013-09-16",
                "precipitation": 0.3,
                "temp_max": 21.7,
                "temp_min": 15.0,
                "wind": 4.3,
                "weather": "rain"
            },
            {
                "date": "2013-09-17",
                "precipitation": 0.0,
                "temp_max": 17.8,
                "temp_min": 13.9,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2013-09-18",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 13.3,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2013-09-19",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 10.0,
                "wind": 1.5,
                "weather": "sun"
            },
            {
                "date": "2013-09-20",
                "precipitation": 3.6,
                "temp_max": 23.3,
                "temp_min": 13.3,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2013-09-21",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 13.3,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2013-09-22",
                "precipitation": 13.5,
                "temp_max": 17.2,
                "temp_min": 13.3,
                "wind": 5.5,
                "weather": "rain"
            },
            {
                "date": "2013-09-23",
                "precipitation": 2.8,
                "temp_max": 16.1,
                "temp_min": 11.1,
                "wind": 4.5,
                "weather": "rain"
            },
            {
                "date": "2013-09-24",
                "precipitation": 0.0,
                "temp_max": 17.8,
                "temp_min": 10.0,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2013-09-25",
                "precipitation": 2.0,
                "temp_max": 16.1,
                "temp_min": 9.4,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2013-09-26",
                "precipitation": 0.0,
                "temp_max": 17.2,
                "temp_min": 7.2,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2013-09-27",
                "precipitation": 1.0,
                "temp_max": 13.9,
                "temp_min": 10.6,
                "wind": 4.3,
                "weather": "rain"
            },
            {
                "date": "2013-09-28",
                "precipitation": 43.4,
                "temp_max": 16.7,
                "temp_min": 11.7,
                "wind": 6.0,
                "weather": "rain"
            },
            {
                "date": "2013-09-29",
                "precipitation": 16.8,
                "temp_max": 14.4,
                "temp_min": 11.1,
                "wind": 7.1,
                "weather": "rain"
            },
            {
                "date": "2013-09-30",
                "precipitation": 18.5,
                "temp_max": 13.9,
                "temp_min": 10.0,
                "wind": 6.3,
                "weather": "rain"
            },
            {
                "date": "2013-10-01",
                "precipitation": 7.9,
                "temp_max": 14.4,
                "temp_min": 8.9,
                "wind": 4.7,
                "weather": "rain"
            },
            {
                "date": "2013-10-02",
                "precipitation": 5.3,
                "temp_max": 12.8,
                "temp_min": 9.4,
                "wind": 2.4,
                "weather": "rain"
            },
            {
                "date": "2013-10-03",
                "precipitation": 0.8,
                "temp_max": 14.4,
                "temp_min": 8.9,
                "wind": 0.9,
                "weather": "rain"
            },
            {
                "date": "2013-10-04",
                "precipitation": 0.0,
                "temp_max": 17.8,
                "temp_min": 5.6,
                "wind": 1.1,
                "weather": "sun"
            },
            {
                "date": "2013-10-05",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 8.3,
                "wind": 1.6,
                "weather": "sun"
            },
            {
                "date": "2013-10-06",
                "precipitation": 4.1,
                "temp_max": 22.8,
                "temp_min": 7.8,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2013-10-07",
                "precipitation": 0.5,
                "temp_max": 16.1,
                "temp_min": 11.7,
                "wind": 6.3,
                "weather": "rain"
            },
            {
                "date": "2013-10-08",
                "precipitation": 6.9,
                "temp_max": 13.9,
                "temp_min": 7.8,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2013-10-09",
                "precipitation": 0.0,
                "temp_max": 15.0,
                "temp_min": 5.6,
                "wind": 1.6,
                "weather": "sun"
            },
            {
                "date": "2013-10-10",
                "precipitation": 1.0,
                "temp_max": 14.4,
                "temp_min": 8.3,
                "wind": 1.7,
                "weather": "rain"
            },
            {
                "date": "2013-10-11",
                "precipitation": 9.1,
                "temp_max": 13.9,
                "temp_min": 10.6,
                "wind": 1.0,
                "weather": "rain"
            },
            {
                "date": "2013-10-12",
                "precipitation": 1.0,
                "temp_max": 14.4,
                "temp_min": 8.9,
                "wind": 2.2,
                "weather": "rain"
            },
            {
                "date": "2013-10-13",
                "precipitation": 0.0,
                "temp_max": 15.0,
                "temp_min": 6.7,
                "wind": 1.8,
                "weather": "fog"
            },
            {
                "date": "2013-10-14",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 3.9,
                "wind": 1.6,
                "weather": "sun"
            },
            {
                "date": "2013-10-15",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 5.0,
                "wind": 0.9,
                "weather": "sun"
            },
            {
                "date": "2013-10-16",
                "precipitation": 0.0,
                "temp_max": 12.8,
                "temp_min": 8.9,
                "wind": 2.7,
                "weather": "fog"
            },
            {
                "date": "2013-10-17",
                "precipitation": 0.0,
                "temp_max": 14.4,
                "temp_min": 8.9,
                "wind": 1.7,
                "weather": "fog"
            },
            {
                "date": "2013-10-18",
                "precipitation": 0.0,
                "temp_max": 12.8,
                "temp_min": 7.2,
                "wind": 1.2,
                "weather": "sun"
            },
            {
                "date": "2013-10-19",
                "precipitation": 0.0,
                "temp_max": 10.6,
                "temp_min": 7.8,
                "wind": 1.4,
                "weather": "sun"
            },
            {
                "date": "2013-10-20",
                "precipitation": 0.0,
                "temp_max": 10.6,
                "temp_min": 7.8,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2013-10-21",
                "precipitation": 0.0,
                "temp_max": 11.7,
                "temp_min": 8.3,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2013-10-22",
                "precipitation": 0.0,
                "temp_max": 14.4,
                "temp_min": 7.2,
                "wind": 1.9,
                "weather": "sun"
            },
            {
                "date": "2013-10-23",
                "precipitation": 0.0,
                "temp_max": 12.8,
                "temp_min": 6.1,
                "wind": 0.4,
                "weather": "sun"
            },
            {
                "date": "2013-10-24",
                "precipitation": 0.0,
                "temp_max": 10.0,
                "temp_min": 6.1,
                "wind": 0.6,
                "weather": "sun"
            },
            {
                "date": "2013-10-25",
                "precipitation": 0.0,
                "temp_max": 12.2,
                "temp_min": 7.8,
                "wind": 1.8,
                "weather": "sun"
            },
            {
                "date": "2013-10-26",
                "precipitation": 0.0,
                "temp_max": 11.7,
                "temp_min": 8.3,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2013-10-27",
                "precipitation": 1.8,
                "temp_max": 13.9,
                "temp_min": 8.3,
                "wind": 4.4,
                "weather": "rain"
            },
            {
                "date": "2013-10-28",
                "precipitation": 0.0,
                "temp_max": 14.4,
                "temp_min": 7.2,
                "wind": 5.1,
                "weather": "sun"
            },
            {
                "date": "2013-10-29",
                "precipitation": 0.0,
                "temp_max": 13.3,
                "temp_min": 3.3,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2013-10-30",
                "precipitation": 0.5,
                "temp_max": 15.0,
                "temp_min": 5.6,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2013-10-31",
                "precipitation": 0.3,
                "temp_max": 14.4,
                "temp_min": 10.6,
                "wind": 2.2,
                "weather": "rain"
            },
            {
                "date": "2013-11-01",
                "precipitation": 1.3,
                "temp_max": 17.8,
                "temp_min": 11.7,
                "wind": 1.4,
                "weather": "rain"
            },
            {
                "date": "2013-11-02",
                "precipitation": 12.7,
                "temp_max": 14.4,
                "temp_min": 8.3,
                "wind": 7.9,
                "weather": "rain"
            },
            {
                "date": "2013-11-03",
                "precipitation": 0.5,
                "temp_max": 12.2,
                "temp_min": 4.4,
                "wind": 2.4,
                "weather": "rain"
            },
            {
                "date": "2013-11-04",
                "precipitation": 0.0,
                "temp_max": 10.6,
                "temp_min": 3.9,
                "wind": 1.6,
                "weather": "drizzle"
            },
            {
                "date": "2013-11-05",
                "precipitation": 2.5,
                "temp_max": 13.3,
                "temp_min": 7.2,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2013-11-06",
                "precipitation": 3.8,
                "temp_max": 12.8,
                "temp_min": 7.8,
                "wind": 1.7,
                "weather": "rain"
            },
            {
                "date": "2013-11-07",
                "precipitation": 30.0,
                "temp_max": 11.1,
                "temp_min": 10.0,
                "wind": 7.2,
                "weather": "rain"
            },
            {
                "date": "2013-11-08",
                "precipitation": 0.0,
                "temp_max": 13.3,
                "temp_min": 7.2,
                "wind": 4.1,
                "weather": "sun"
            },
            {
                "date": "2013-11-09",
                "precipitation": 1.8,
                "temp_max": 11.1,
                "temp_min": 5.0,
                "wind": 1.4,
                "weather": "rain"
            },
            {
                "date": "2013-11-10",
                "precipitation": 0.0,
                "temp_max": 11.1,
                "temp_min": 8.3,
                "wind": 4.4,
                "weather": "sun"
            },
            {
                "date": "2013-11-11",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 6.1,
                "wind": 2.6,
                "weather": "fog"
            },
            {
                "date": "2013-11-12",
                "precipitation": 4.1,
                "temp_max": 15.6,
                "temp_min": 8.9,
                "wind": 2.2,
                "weather": "rain"
            },
            {
                "date": "2013-11-13",
                "precipitation": 0.0,
                "temp_max": 13.9,
                "temp_min": 10.6,
                "wind": 3.8,
                "weather": "sun"
            },
            {
                "date": "2013-11-14",
                "precipitation": 1.3,
                "temp_max": 11.1,
                "temp_min": 6.1,
                "wind": 1.1,
                "weather": "rain"
            },
            {
                "date": "2013-11-15",
                "precipitation": 3.0,
                "temp_max": 10.6,
                "temp_min": 7.2,
                "wind": 6.0,
                "weather": "rain"
            },
            {
                "date": "2013-11-16",
                "precipitation": 0.0,
                "temp_max": 10.0,
                "temp_min": 5.0,
                "wind": 4.6,
                "weather": "sun"
            },
            {
                "date": "2013-11-17",
                "precipitation": 5.3,
                "temp_max": 11.7,
                "temp_min": 7.2,
                "wind": 5.4,
                "weather": "rain"
            },
            {
                "date": "2013-11-18",
                "precipitation": 26.2,
                "temp_max": 12.8,
                "temp_min": 9.4,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2013-11-19",
                "precipitation": 1.0,
                "temp_max": 13.3,
                "temp_min": 4.4,
                "wind": 5.1,
                "weather": "rain"
            },
            {
                "date": "2013-11-20",
                "precipitation": 0.0,
                "temp_max": 7.8,
                "temp_min": 1.7,
                "wind": 4.3,
                "weather": "sun"
            },
            {
                "date": "2013-11-21",
                "precipitation": 0.0,
                "temp_max": 7.8,
                "temp_min": -0.5,
                "wind": 3.6,
                "weather": "sun"
            },
            {
                "date": "2013-11-22",
                "precipitation": 0.0,
                "temp_max": 9.4,
                "temp_min": 0.0,
                "wind": 4.6,
                "weather": "sun"
            },
            {
                "date": "2013-11-23",
                "precipitation": 0.0,
                "temp_max": 11.1,
                "temp_min": 1.1,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2013-11-24",
                "precipitation": 0.0,
                "temp_max": 11.7,
                "temp_min": 0.6,
                "wind": 0.9,
                "weather": "fog"
            },
            {
                "date": "2013-11-25",
                "precipitation": 0.0,
                "temp_max": 12.2,
                "temp_min": 2.2,
                "wind": 0.5,
                "weather": "sun"
            },
            {
                "date": "2013-11-26",
                "precipitation": 0.0,
                "temp_max": 12.2,
                "temp_min": 2.8,
                "wind": 1.0,
                "weather": "sun"
            },
            {
                "date": "2013-11-27",
                "precipitation": 0.0,
                "temp_max": 14.4,
                "temp_min": 5.6,
                "wind": 1.3,
                "weather": "sun"
            },
            {
                "date": "2013-11-28",
                "precipitation": 0.0,
                "temp_max": 11.7,
                "temp_min": 3.3,
                "wind": 0.7,
                "weather": "sun"
            },
            {
                "date": "2013-11-29",
                "precipitation": 0.5,
                "temp_max": 9.4,
                "temp_min": 5.0,
                "wind": 2.1,
                "weather": "rain"
            },
            {
                "date": "2013-11-30",
                "precipitation": 2.3,
                "temp_max": 11.1,
                "temp_min": 7.2,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2013-12-01",
                "precipitation": 3.0,
                "temp_max": 13.3,
                "temp_min": 7.8,
                "wind": 8.8,
                "weather": "rain"
            },
            {
                "date": "2013-12-02",
                "precipitation": 4.6,
                "temp_max": 7.8,
                "temp_min": 1.7,
                "wind": 3.5,
                "weather": "rain"
            },
            {
                "date": "2013-12-03",
                "precipitation": 0.0,
                "temp_max": 5.0,
                "temp_min": -0.5,
                "wind": 5.6,
                "weather": "sun"
            },
            {
                "date": "2013-12-04",
                "precipitation": 0.0,
                "temp_max": 4.4,
                "temp_min": -2.1,
                "wind": 1.6,
                "weather": "sun"
            },
            {
                "date": "2013-12-05",
                "precipitation": 0.0,
                "temp_max": 1.1,
                "temp_min": -4.9,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2013-12-06",
                "precipitation": 0.0,
                "temp_max": 1.1,
                "temp_min": -4.3,
                "wind": 4.7,
                "weather": "sun"
            },
            {
                "date": "2013-12-07",
                "precipitation": 0.0,
                "temp_max": 0.0,
                "temp_min": -7.1,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2013-12-08",
                "precipitation": 0.0,
                "temp_max": 2.2,
                "temp_min": -6.6,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2013-12-09",
                "precipitation": 0.0,
                "temp_max": 1.1,
                "temp_min": -4.9,
                "wind": 1.3,
                "weather": "sun"
            },
            {
                "date": "2013-12-10",
                "precipitation": 0.0,
                "temp_max": 5.6,
                "temp_min": 0.6,
                "wind": 1.5,
                "weather": "sun"
            },
            {
                "date": "2013-12-11",
                "precipitation": 0.0,
                "temp_max": 5.0,
                "temp_min": -1.6,
                "wind": 0.8,
                "weather": "sun"
            },
            {
                "date": "2013-12-12",
                "precipitation": 6.9,
                "temp_max": 5.6,
                "temp_min": -0.5,
                "wind": 2.3,
                "weather": "rain"
            },
            {
                "date": "2013-12-13",
                "precipitation": 0.5,
                "temp_max": 9.4,
                "temp_min": 5.6,
                "wind": 2.9,
                "weather": "rain"
            },
            {
                "date": "2013-12-14",
                "precipitation": 0.0,
                "temp_max": 9.4,
                "temp_min": 6.1,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2013-12-15",
                "precipitation": 1.3,
                "temp_max": 11.7,
                "temp_min": 8.3,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2013-12-16",
                "precipitation": 0.3,
                "temp_max": 10.0,
                "temp_min": 4.4,
                "wind": 1.0,
                "weather": "rain"
            },
            {
                "date": "2013-12-17",
                "precipitation": 0.0,
                "temp_max": 8.3,
                "temp_min": 4.4,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2013-12-18",
                "precipitation": 1.3,
                "temp_max": 7.8,
                "temp_min": 2.2,
                "wind": 2.8,
                "weather": "rain"
            },
            {
                "date": "2013-12-19",
                "precipitation": 0.0,
                "temp_max": 5.0,
                "temp_min": 0.0,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2013-12-20",
                "precipitation": 5.6,
                "temp_max": 8.3,
                "temp_min": 0.6,
                "wind": 3.7,
                "weather": "snow"
            },
            {
                "date": "2013-12-21",
                "precipitation": 5.6,
                "temp_max": 8.9,
                "temp_min": 5.6,
                "wind": 2.3,
                "weather": "rain"
            },
            {
                "date": "2013-12-22",
                "precipitation": 10.7,
                "temp_max": 10.6,
                "temp_min": 8.3,
                "wind": 4.0,
                "weather": "rain"
            },
            {
                "date": "2013-12-23",
                "precipitation": 1.5,
                "temp_max": 11.7,
                "temp_min": 6.1,
                "wind": 5.9,
                "weather": "rain"
            },
            {
                "date": "2013-12-24",
                "precipitation": 0.0,
                "temp_max": 8.3,
                "temp_min": 2.8,
                "wind": 1.7,
                "weather": "sun"
            },
            {
                "date": "2013-12-25",
                "precipitation": 0.0,
                "temp_max": 6.7,
                "temp_min": 1.7,
                "wind": 0.8,
                "weather": "sun"
            },
            {
                "date": "2013-12-26",
                "precipitation": 0.0,
                "temp_max": 6.7,
                "temp_min": 0.6,
                "wind": 0.5,
                "weather": "sun"
            },
            {
                "date": "2013-12-27",
                "precipitation": 0.3,
                "temp_max": 8.9,
                "temp_min": 0.0,
                "wind": 2.1,
                "weather": "rain"
            },
            {
                "date": "2013-12-28",
                "precipitation": 0.0,
                "temp_max": 9.4,
                "temp_min": 3.3,
                "wind": 1.3,
                "weather": "sun"
            },
            {
                "date": "2013-12-29",
                "precipitation": 0.0,
                "temp_max": 7.2,
                "temp_min": 1.7,
                "wind": 1.1,
                "weather": "sun"
            },
            {
                "date": "2013-12-30",
                "precipitation": 0.3,
                "temp_max": 8.9,
                "temp_min": 4.4,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2013-12-31",
                "precipitation": 0.5,
                "temp_max": 8.3,
                "temp_min": 5.0,
                "wind": 1.7,
                "weather": "rain"
            },
            {
                "date": "2014-01-01",
                "precipitation": 0.0,
                "temp_max": 7.2,
                "temp_min": 3.3,
                "wind": 1.2,
                "weather": "sun"
            },
            {
                "date": "2014-01-02",
                "precipitation": 4.1,
                "temp_max": 10.6,
                "temp_min": 6.1,
                "wind": 3.2,
                "weather": "rain"
            },
            {
                "date": "2014-01-03",
                "precipitation": 1.5,
                "temp_max": 8.9,
                "temp_min": 2.8,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2014-01-04",
                "precipitation": 0.0,
                "temp_max": 7.8,
                "temp_min": 0.6,
                "wind": 2.7,
                "weather": "fog"
            },
            {
                "date": "2014-01-05",
                "precipitation": 0.0,
                "temp_max": 8.3,
                "temp_min": -0.5,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2014-01-06",
                "precipitation": 0.3,
                "temp_max": 7.8,
                "temp_min": -0.5,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2014-01-07",
                "precipitation": 12.2,
                "temp_max": 8.3,
                "temp_min": 5.0,
                "wind": 1.6,
                "weather": "rain"
            },
            {
                "date": "2014-01-08",
                "precipitation": 9.7,
                "temp_max": 10.0,
                "temp_min": 7.2,
                "wind": 4.6,
                "weather": "rain"
            },
            {
                "date": "2014-01-09",
                "precipitation": 5.8,
                "temp_max": 9.4,
                "temp_min": 5.6,
                "wind": 6.3,
                "weather": "rain"
            },
            {
                "date": "2014-01-10",
                "precipitation": 4.3,
                "temp_max": 12.8,
                "temp_min": 8.3,
                "wind": 7.0,
                "weather": "rain"
            },
            {
                "date": "2014-01-11",
                "precipitation": 21.3,
                "temp_max": 14.4,
                "temp_min": 7.2,
                "wind": 8.8,
                "weather": "rain"
            },
            {
                "date": "2014-01-12",
                "precipitation": 1.5,
                "temp_max": 11.1,
                "temp_min": 5.6,
                "wind": 8.1,
                "weather": "rain"
            },
            {
                "date": "2014-01-13",
                "precipitation": 0.0,
                "temp_max": 10.6,
                "temp_min": 10.0,
                "wind": 7.1,
                "weather": "sun"
            },
            {
                "date": "2014-01-14",
                "precipitation": 0.0,
                "temp_max": 11.1,
                "temp_min": 7.2,
                "wind": 1.3,
                "weather": "sun"
            },
            {
                "date": "2014-01-15",
                "precipitation": 0.0,
                "temp_max": 11.1,
                "temp_min": 5.6,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2014-01-16",
                "precipitation": 0.0,
                "temp_max": 6.7,
                "temp_min": 4.4,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2014-01-17",
                "precipitation": 0.0,
                "temp_max": 5.6,
                "temp_min": 2.8,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2014-01-18",
                "precipitation": 0.0,
                "temp_max": 9.4,
                "temp_min": 0.6,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2014-01-19",
                "precipitation": 0.0,
                "temp_max": 6.1,
                "temp_min": 3.3,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2014-01-20",
                "precipitation": 0.0,
                "temp_max": 10.0,
                "temp_min": 2.8,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2014-01-21",
                "precipitation": 0.0,
                "temp_max": 10.0,
                "temp_min": 1.7,
                "wind": 1.5,
                "weather": "sun"
            },
            {
                "date": "2014-01-22",
                "precipitation": 0.5,
                "temp_max": 9.4,
                "temp_min": 5.6,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2014-01-23",
                "precipitation": 0.0,
                "temp_max": 10.0,
                "temp_min": 2.8,
                "wind": 5.2,
                "weather": "fog"
            },
            {
                "date": "2014-01-24",
                "precipitation": 0.0,
                "temp_max": 12.8,
                "temp_min": 1.1,
                "wind": 1.9,
                "weather": "sun"
            },
            {
                "date": "2014-01-25",
                "precipitation": 0.0,
                "temp_max": 12.2,
                "temp_min": 1.1,
                "wind": 0.8,
                "weather": "sun"
            },
            {
                "date": "2014-01-26",
                "precipitation": 0.0,
                "temp_max": 8.3,
                "temp_min": 0.6,
                "wind": 1.3,
                "weather": "sun"
            },
            {
                "date": "2014-01-27",
                "precipitation": 0.0,
                "temp_max": 9.4,
                "temp_min": 1.7,
                "wind": 1.3,
                "weather": "sun"
            },
            {
                "date": "2014-01-28",
                "precipitation": 8.9,
                "temp_max": 11.1,
                "temp_min": 6.1,
                "wind": 1.6,
                "weather": "rain"
            },
            {
                "date": "2014-01-29",
                "precipitation": 21.6,
                "temp_max": 11.1,
                "temp_min": 7.2,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2014-01-30",
                "precipitation": 0.0,
                "temp_max": 8.3,
                "temp_min": 6.1,
                "wind": 6.4,
                "weather": "sun"
            },
            {
                "date": "2014-01-31",
                "precipitation": 2.3,
                "temp_max": 7.8,
                "temp_min": 5.6,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2014-02-01",
                "precipitation": 2.0,
                "temp_max": 7.8,
                "temp_min": 2.8,
                "wind": 0.8,
                "weather": "rain"
            },
            {
                "date": "2014-02-02",
                "precipitation": 0.0,
                "temp_max": 8.9,
                "temp_min": 1.1,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2014-02-03",
                "precipitation": 0.0,
                "temp_max": 5.0,
                "temp_min": 0.0,
                "wind": 4.3,
                "weather": "sun"
            },
            {
                "date": "2014-02-04",
                "precipitation": 0.0,
                "temp_max": 2.8,
                "temp_min": -2.1,
                "wind": 4.7,
                "weather": "sun"
            },
            {
                "date": "2014-02-05",
                "precipitation": 0.0,
                "temp_max": -0.5,
                "temp_min": -5.5,
                "wind": 6.6,
                "weather": "sun"
            },
            {
                "date": "2014-02-06",
                "precipitation": 0.0,
                "temp_max": -1.6,
                "temp_min": -6.0,
                "wind": 4.5,
                "weather": "sun"
            },
            {
                "date": "2014-02-07",
                "precipitation": 0.0,
                "temp_max": 3.3,
                "temp_min": -4.9,
                "wind": 4.2,
                "weather": "sun"
            },
            {
                "date": "2014-02-08",
                "precipitation": 5.1,
                "temp_max": 5.6,
                "temp_min": -0.5,
                "wind": 4.6,
                "weather": "snow"
            },
            {
                "date": "2014-02-09",
                "precipitation": 0.5,
                "temp_max": 3.9,
                "temp_min": 0.0,
                "wind": 2.4,
                "weather": "rain"
            },
            {
                "date": "2014-02-10",
                "precipitation": 18.3,
                "temp_max": 10.0,
                "temp_min": 2.2,
                "wind": 4.7,
                "weather": "rain"
            },
            {
                "date": "2014-02-11",
                "precipitation": 17.0,
                "temp_max": 12.2,
                "temp_min": 5.6,
                "wind": 3.8,
                "weather": "rain"
            },
            {
                "date": "2014-02-12",
                "precipitation": 4.6,
                "temp_max": 12.2,
                "temp_min": 7.2,
                "wind": 6.4,
                "weather": "rain"
            },
            {
                "date": "2014-02-13",
                "precipitation": 1.8,
                "temp_max": 12.8,
                "temp_min": 7.8,
                "wind": 6.3,
                "weather": "rain"
            },
            {
                "date": "2014-02-14",
                "precipitation": 9.4,
                "temp_max": 11.7,
                "temp_min": 6.1,
                "wind": 6.4,
                "weather": "rain"
            },
            {
                "date": "2014-02-15",
                "precipitation": 11.7,
                "temp_max": 11.1,
                "temp_min": 5.0,
                "wind": 5.1,
                "weather": "rain"
            },
            {
                "date": "2014-02-16",
                "precipitation": 26.4,
                "temp_max": 9.4,
                "temp_min": 3.9,
                "wind": 7.9,
                "weather": "rain"
            },
            {
                "date": "2014-02-17",
                "precipitation": 14.5,
                "temp_max": 8.3,
                "temp_min": 4.4,
                "wind": 5.5,
                "weather": "rain"
            },
            {
                "date": "2014-02-18",
                "precipitation": 15.2,
                "temp_max": 8.9,
                "temp_min": 5.0,
                "wind": 6.2,
                "weather": "rain"
            },
            {
                "date": "2014-02-19",
                "precipitation": 1.0,
                "temp_max": 8.3,
                "temp_min": 3.9,
                "wind": 6.0,
                "weather": "rain"
            },
            {
                "date": "2014-02-20",
                "precipitation": 3.0,
                "temp_max": 10.0,
                "temp_min": 5.6,
                "wind": 6.9,
                "weather": "rain"
            },
            {
                "date": "2014-02-21",
                "precipitation": 2.8,
                "temp_max": 6.7,
                "temp_min": 3.9,
                "wind": 2.9,
                "weather": "rain"
            },
            {
                "date": "2014-02-22",
                "precipitation": 2.5,
                "temp_max": 5.6,
                "temp_min": 2.8,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2014-02-23",
                "precipitation": 6.1,
                "temp_max": 7.2,
                "temp_min": 3.9,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2014-02-24",
                "precipitation": 13.0,
                "temp_max": 6.7,
                "temp_min": 3.3,
                "wind": 3.2,
                "weather": "rain"
            },
            {
                "date": "2014-02-25",
                "precipitation": 0.3,
                "temp_max": 12.2,
                "temp_min": 3.9,
                "wind": 4.5,
                "weather": "rain"
            },
            {
                "date": "2014-02-26",
                "precipitation": 0.0,
                "temp_max": 13.9,
                "temp_min": 5.6,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2014-02-27",
                "precipitation": 0.0,
                "temp_max": 12.8,
                "temp_min": 4.4,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2014-02-28",
                "precipitation": 0.0,
                "temp_max": 14.4,
                "temp_min": 4.4,
                "wind": 5.9,
                "weather": "sun"
            },
            {
                "date": "2014-03-01",
                "precipitation": 0.5,
                "temp_max": 7.2,
                "temp_min": 4.4,
                "wind": 4.7,
                "weather": "rain"
            },
            {
                "date": "2014-03-02",
                "precipitation": 19.1,
                "temp_max": 11.1,
                "temp_min": 2.8,
                "wind": 5.7,
                "weather": "rain"
            },
            {
                "date": "2014-03-03",
                "precipitation": 10.7,
                "temp_max": 14.4,
                "temp_min": 8.9,
                "wind": 5.1,
                "weather": "rain"
            },
            {
                "date": "2014-03-04",
                "precipitation": 16.5,
                "temp_max": 13.9,
                "temp_min": 7.8,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2014-03-05",
                "precipitation": 46.7,
                "temp_max": 15.6,
                "temp_min": 10.6,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2014-03-06",
                "precipitation": 3.0,
                "temp_max": 13.3,
                "temp_min": 10.0,
                "wind": 6.2,
                "weather": "rain"
            },
            {
                "date": "2014-03-07",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 8.9,
                "wind": 4.2,
                "weather": "sun"
            },
            {
                "date": "2014-03-08",
                "precipitation": 32.3,
                "temp_max": 12.8,
                "temp_min": 6.7,
                "wind": 2.7,
                "weather": "rain"
            },
            {
                "date": "2014-03-09",
                "precipitation": 4.3,
                "temp_max": 15.0,
                "temp_min": 9.4,
                "wind": 4.3,
                "weather": "rain"
            },
            {
                "date": "2014-03-10",
                "precipitation": 18.8,
                "temp_max": 12.2,
                "temp_min": 6.1,
                "wind": 2.2,
                "weather": "rain"
            },
            {
                "date": "2014-03-11",
                "precipitation": 0.0,
                "temp_max": 14.4,
                "temp_min": 4.4,
                "wind": 2.3,
                "weather": "fog"
            },
            {
                "date": "2014-03-12",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 3.3,
                "wind": 1.9,
                "weather": "fog"
            },
            {
                "date": "2014-03-13",
                "precipitation": 0.5,
                "temp_max": 13.9,
                "temp_min": 5.0,
                "wind": 2.5,
                "weather": "rain"
            },
            {
                "date": "2014-03-14",
                "precipitation": 6.9,
                "temp_max": 14.4,
                "temp_min": 8.3,
                "wind": 6.1,
                "weather": "rain"
            },
            {
                "date": "2014-03-15",
                "precipitation": 8.1,
                "temp_max": 16.7,
                "temp_min": 4.4,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2014-03-16",
                "precipitation": 27.7,
                "temp_max": 10.6,
                "temp_min": 4.4,
                "wind": 3.8,
                "weather": "rain"
            },
            {
                "date": "2014-03-17",
                "precipitation": 0.3,
                "temp_max": 10.0,
                "temp_min": 2.8,
                "wind": 3.2,
                "weather": "rain"
            },
            {
                "date": "2014-03-18",
                "precipitation": 0.0,
                "temp_max": 10.0,
                "temp_min": 3.3,
                "wind": 1.6,
                "weather": "sun"
            },
            {
                "date": "2014-03-19",
                "precipitation": 0.5,
                "temp_max": 11.1,
                "temp_min": 3.3,
                "wind": 5.1,
                "weather": "rain"
            },
            {
                "date": "2014-03-20",
                "precipitation": 0.0,
                "temp_max": 11.1,
                "temp_min": 1.7,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2014-03-21",
                "precipitation": 0.0,
                "temp_max": 10.6,
                "temp_min": 2.8,
                "wind": 3.8,
                "weather": "sun"
            },
            {
                "date": "2014-03-22",
                "precipitation": 0.0,
                "temp_max": 11.1,
                "temp_min": 1.1,
                "wind": 1.8,
                "weather": "sun"
            },
            {
                "date": "2014-03-23",
                "precipitation": 0.0,
                "temp_max": 12.8,
                "temp_min": 4.4,
                "wind": 3.3,
                "weather": "sun"
            },
            {
                "date": "2014-03-24",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 2.8,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2014-03-25",
                "precipitation": 4.1,
                "temp_max": 13.9,
                "temp_min": 6.7,
                "wind": 4.4,
                "weather": "rain"
            },
            {
                "date": "2014-03-26",
                "precipitation": 3.6,
                "temp_max": 11.1,
                "temp_min": 5.6,
                "wind": 2.4,
                "weather": "rain"
            },
            {
                "date": "2014-03-27",
                "precipitation": 0.3,
                "temp_max": 12.2,
                "temp_min": 6.7,
                "wind": 2.8,
                "weather": "rain"
            },
            {
                "date": "2014-03-28",
                "precipitation": 22.1,
                "temp_max": 11.7,
                "temp_min": 7.2,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2014-03-29",
                "precipitation": 14.0,
                "temp_max": 11.7,
                "temp_min": 7.2,
                "wind": 5.1,
                "weather": "rain"
            },
            {
                "date": "2014-03-30",
                "precipitation": 0.0,
                "temp_max": 11.1,
                "temp_min": 5.0,
                "wind": 5.1,
                "weather": "sun"
            },
            {
                "date": "2014-03-31",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 2.2,
                "wind": 3.8,
                "weather": "sun"
            },
            {
                "date": "2014-04-01",
                "precipitation": 0.0,
                "temp_max": 14.4,
                "temp_min": 6.7,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2014-04-02",
                "precipitation": 0.0,
                "temp_max": 14.4,
                "temp_min": 5.6,
                "wind": 4.2,
                "weather": "sun"
            },
            {
                "date": "2014-04-03",
                "precipitation": 2.5,
                "temp_max": 13.3,
                "temp_min": 6.1,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2014-04-04",
                "precipitation": 0.0,
                "temp_max": 12.8,
                "temp_min": 6.1,
                "wind": 4.7,
                "weather": "sun"
            },
            {
                "date": "2014-04-05",
                "precipitation": 4.6,
                "temp_max": 11.7,
                "temp_min": 7.8,
                "wind": 4.3,
                "weather": "rain"
            },
            {
                "date": "2014-04-06",
                "precipitation": 0.0,
                "temp_max": 13.9,
                "temp_min": 8.3,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2014-04-07",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 9.4,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2014-04-08",
                "precipitation": 4.6,
                "temp_max": 15.6,
                "temp_min": 8.3,
                "wind": 4.2,
                "weather": "rain"
            },
            {
                "date": "2014-04-09",
                "precipitation": 0.0,
                "temp_max": 14.4,
                "temp_min": 6.7,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2014-04-10",
                "precipitation": 0.0,
                "temp_max": 15.0,
                "temp_min": 6.7,
                "wind": 3.6,
                "weather": "sun"
            },
            {
                "date": "2014-04-11",
                "precipitation": 0.0,
                "temp_max": 17.2,
                "temp_min": 5.0,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2014-04-12",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 7.8,
                "wind": 4.4,
                "weather": "sun"
            },
            {
                "date": "2014-04-13",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 5.6,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2014-04-14",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 5.6,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2014-04-15",
                "precipitation": 0.5,
                "temp_max": 14.4,
                "temp_min": 7.8,
                "wind": 4.0,
                "weather": "rain"
            },
            {
                "date": "2014-04-16",
                "precipitation": 10.9,
                "temp_max": 11.1,
                "temp_min": 8.9,
                "wind": 4.6,
                "weather": "rain"
            },
            {
                "date": "2014-04-17",
                "precipitation": 18.5,
                "temp_max": 11.7,
                "temp_min": 7.2,
                "wind": 4.7,
                "weather": "rain"
            },
            {
                "date": "2014-04-18",
                "precipitation": 0.0,
                "temp_max": 14.4,
                "temp_min": 5.6,
                "wind": 3.8,
                "weather": "sun"
            },
            {
                "date": "2014-04-19",
                "precipitation": 13.7,
                "temp_max": 11.7,
                "temp_min": 5.6,
                "wind": 4.7,
                "weather": "rain"
            },
            {
                "date": "2014-04-20",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 5.6,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2014-04-21",
                "precipitation": 5.1,
                "temp_max": 17.2,
                "temp_min": 7.8,
                "wind": 2.5,
                "weather": "rain"
            },
            {
                "date": "2014-04-22",
                "precipitation": 14.2,
                "temp_max": 12.2,
                "temp_min": 5.0,
                "wind": 4.2,
                "weather": "rain"
            },
            {
                "date": "2014-04-23",
                "precipitation": 8.9,
                "temp_max": 11.7,
                "temp_min": 6.1,
                "wind": 5.0,
                "weather": "rain"
            },
            {
                "date": "2014-04-24",
                "precipitation": 12.4,
                "temp_max": 13.9,
                "temp_min": 6.1,
                "wind": 5.3,
                "weather": "rain"
            },
            {
                "date": "2014-04-25",
                "precipitation": 0.0,
                "temp_max": 14.4,
                "temp_min": 5.6,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2014-04-26",
                "precipitation": 3.3,
                "temp_max": 15.0,
                "temp_min": 5.6,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2014-04-27",
                "precipitation": 6.9,
                "temp_max": 11.1,
                "temp_min": 6.1,
                "wind": 5.8,
                "weather": "rain"
            },
            {
                "date": "2014-04-28",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 4.4,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2014-04-29",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 9.4,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2014-04-30",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 9.4,
                "wind": 3.9,
                "weather": "sun"
            },
            {
                "date": "2014-05-01",
                "precipitation": 0.0,
                "temp_max": 29.4,
                "temp_min": 11.1,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2014-05-02",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 10.6,
                "wind": 4.7,
                "weather": "sun"
            },
            {
                "date": "2014-05-03",
                "precipitation": 33.3,
                "temp_max": 15.0,
                "temp_min": 8.9,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2014-05-04",
                "precipitation": 16.0,
                "temp_max": 14.4,
                "temp_min": 8.9,
                "wind": 4.2,
                "weather": "rain"
            },
            {
                "date": "2014-05-05",
                "precipitation": 5.1,
                "temp_max": 15.6,
                "temp_min": 9.4,
                "wind": 3.8,
                "weather": "rain"
            },
            {
                "date": "2014-05-06",
                "precipitation": 0.0,
                "temp_max": 16.7,
                "temp_min": 8.3,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2014-05-07",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 7.2,
                "wind": 1.7,
                "weather": "sun"
            },
            {
                "date": "2014-05-08",
                "precipitation": 13.7,
                "temp_max": 13.9,
                "temp_min": 9.4,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2014-05-09",
                "precipitation": 2.0,
                "temp_max": 13.3,
                "temp_min": 7.2,
                "wind": 5.6,
                "weather": "rain"
            },
            {
                "date": "2014-05-10",
                "precipitation": 0.5,
                "temp_max": 15.6,
                "temp_min": 7.2,
                "wind": 2.1,
                "weather": "rain"
            },
            {
                "date": "2014-05-11",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 8.3,
                "wind": 1.7,
                "weather": "sun"
            },
            {
                "date": "2014-05-12",
                "precipitation": 0.0,
                "temp_max": 24.4,
                "temp_min": 9.4,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2014-05-13",
                "precipitation": 0.0,
                "temp_max": 26.7,
                "temp_min": 12.8,
                "wind": 3.8,
                "weather": "sun"
            },
            {
                "date": "2014-05-14",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 13.3,
                "wind": 3.3,
                "weather": "sun"
            },
            {
                "date": "2014-05-15",
                "precipitation": 0.0,
                "temp_max": 26.7,
                "temp_min": 12.8,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2014-05-16",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 11.7,
                "wind": 4.1,
                "weather": "sun"
            },
            {
                "date": "2014-05-17",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 11.7,
                "wind": 3.2,
                "weather": "sun"
            },
            {
                "date": "2014-05-18",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 10.6,
                "wind": 3.2,
                "weather": "sun"
            },
            {
                "date": "2014-05-19",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 10.0,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2014-05-20",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 10.0,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2014-05-21",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 10.6,
                "wind": 1.7,
                "weather": "sun"
            },
            {
                "date": "2014-05-22",
                "precipitation": 0.0,
                "temp_max": 24.4,
                "temp_min": 11.7,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2014-05-23",
                "precipitation": 3.8,
                "temp_max": 20.0,
                "temp_min": 12.8,
                "wind": 4.0,
                "weather": "rain"
            },
            {
                "date": "2014-05-24",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 11.1,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2014-05-25",
                "precipitation": 5.6,
                "temp_max": 15.0,
                "temp_min": 10.6,
                "wind": 1.4,
                "weather": "rain"
            },
            {
                "date": "2014-05-26",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 11.1,
                "wind": 4.5,
                "weather": "sun"
            },
            {
                "date": "2014-05-27",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 10.0,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2014-05-28",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 10.0,
                "wind": 3.4,
                "weather": "sun"
            },
            {
                "date": "2014-05-29",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 11.1,
                "wind": 4.3,
                "weather": "sun"
            },
            {
                "date": "2014-05-30",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 8.9,
                "wind": 4.5,
                "weather": "sun"
            },
            {
                "date": "2014-05-31",
                "precipitation": 0.0,
                "temp_max": 23.3,
                "temp_min": 10.0,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2014-06-01",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 10.6,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2014-06-02",
                "precipitation": 0.0,
                "temp_max": 23.3,
                "temp_min": 11.1,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2014-06-03",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 11.1,
                "wind": 3.2,
                "weather": "sun"
            },
            {
                "date": "2014-06-04",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 10.0,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2014-06-05",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 10.0,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2014-06-06",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 10.6,
                "wind": 3.2,
                "weather": "sun"
            },
            {
                "date": "2014-06-07",
                "precipitation": 0.0,
                "temp_max": 24.4,
                "temp_min": 13.3,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2014-06-08",
                "precipitation": 0.0,
                "temp_max": 23.3,
                "temp_min": 12.2,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2014-06-09",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 13.3,
                "wind": 3.6,
                "weather": "sun"
            },
            {
                "date": "2014-06-10",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 12.2,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2014-06-11",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 11.1,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2014-06-12",
                "precipitation": 1.8,
                "temp_max": 21.7,
                "temp_min": 12.2,
                "wind": 4.0,
                "weather": "rain"
            },
            {
                "date": "2014-06-13",
                "precipitation": 6.4,
                "temp_max": 15.6,
                "temp_min": 11.1,
                "wind": 5.0,
                "weather": "rain"
            },
            {
                "date": "2014-06-14",
                "precipitation": 0.0,
                "temp_max": 17.8,
                "temp_min": 11.7,
                "wind": 3.2,
                "weather": "sun"
            },
            {
                "date": "2014-06-15",
                "precipitation": 0.5,
                "temp_max": 18.3,
                "temp_min": 10.0,
                "wind": 3.6,
                "weather": "rain"
            },
            {
                "date": "2014-06-16",
                "precipitation": 3.6,
                "temp_max": 17.8,
                "temp_min": 8.9,
                "wind": 2.4,
                "weather": "rain"
            },
            {
                "date": "2014-06-17",
                "precipitation": 1.3,
                "temp_max": 17.8,
                "temp_min": 10.0,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2014-06-18",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 11.1,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2014-06-19",
                "precipitation": 0.8,
                "temp_max": 25.6,
                "temp_min": 11.7,
                "wind": 3.7,
                "weather": "rain"
            },
            {
                "date": "2014-06-20",
                "precipitation": 0.3,
                "temp_max": 20.0,
                "temp_min": 10.0,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2014-06-21",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 10.6,
                "wind": 3.6,
                "weather": "sun"
            },
            {
                "date": "2014-06-22",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 11.1,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2014-06-23",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 13.3,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2014-06-24",
                "precipitation": 0.0,
                "temp_max": 24.4,
                "temp_min": 14.4,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2014-06-25",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 13.9,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2014-06-26",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 14.4,
                "wind": 4.1,
                "weather": "sun"
            },
            {
                "date": "2014-06-27",
                "precipitation": 1.8,
                "temp_max": 21.1,
                "temp_min": 13.9,
                "wind": 4.5,
                "weather": "rain"
            },
            {
                "date": "2014-06-28",
                "precipitation": 2.3,
                "temp_max": 20.0,
                "temp_min": 13.3,
                "wind": 4.3,
                "weather": "rain"
            },
            {
                "date": "2014-06-29",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 12.8,
                "wind": 3.2,
                "weather": "sun"
            },
            {
                "date": "2014-06-30",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 12.8,
                "wind": 4.4,
                "weather": "sun"
            },
            {
                "date": "2014-07-01",
                "precipitation": 0.0,
                "temp_max": 34.4,
                "temp_min": 15.6,
                "wind": 3.5,
                "weather": "sun"
            },
            {
                "date": "2014-07-02",
                "precipitation": 0.0,
                "temp_max": 27.2,
                "temp_min": 14.4,
                "wind": 3.6,
                "weather": "sun"
            },
            {
                "date": "2014-07-03",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 13.9,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2014-07-04",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 13.9,
                "wind": 3.6,
                "weather": "sun"
            },
            {
                "date": "2014-07-05",
                "precipitation": 0.0,
                "temp_max": 24.4,
                "temp_min": 13.3,
                "wind": 2.2,
                "weather": "fog"
            },
            {
                "date": "2014-07-06",
                "precipitation": 0.0,
                "temp_max": 28.9,
                "temp_min": 15.0,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2014-07-07",
                "precipitation": 0.0,
                "temp_max": 27.2,
                "temp_min": 17.8,
                "wind": 4.1,
                "weather": "fog"
            },
            {
                "date": "2014-07-08",
                "precipitation": 0.0,
                "temp_max": 30.0,
                "temp_min": 15.6,
                "wind": 3.5,
                "weather": "sun"
            },
            {
                "date": "2014-07-09",
                "precipitation": 0.0,
                "temp_max": 26.7,
                "temp_min": 13.9,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2014-07-10",
                "precipitation": 0.0,
                "temp_max": 28.9,
                "temp_min": 12.8,
                "wind": 2.2,
                "weather": "fog"
            },
            {
                "date": "2014-07-11",
                "precipitation": 0.0,
                "temp_max": 31.1,
                "temp_min": 15.0,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2014-07-12",
                "precipitation": 0.0,
                "temp_max": 32.2,
                "temp_min": 16.7,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2014-07-13",
                "precipitation": 0.0,
                "temp_max": 29.4,
                "temp_min": 15.0,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2014-07-14",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 15.0,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2014-07-15",
                "precipitation": 0.0,
                "temp_max": 31.1,
                "temp_min": 13.9,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2014-07-16",
                "precipitation": 0.0,
                "temp_max": 31.1,
                "temp_min": 14.4,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2014-07-17",
                "precipitation": 0.0,
                "temp_max": 26.7,
                "temp_min": 13.9,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2014-07-18",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 11.7,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2014-07-19",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 15.0,
                "wind": 5.4,
                "weather": "fog"
            },
            {
                "date": "2014-07-20",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 14.4,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2014-07-21",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 13.3,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2014-07-22",
                "precipitation": 0.3,
                "temp_max": 21.1,
                "temp_min": 13.3,
                "wind": 1.1,
                "weather": "rain"
            },
            {
                "date": "2014-07-23",
                "precipitation": 19.3,
                "temp_max": 18.9,
                "temp_min": 13.3,
                "wind": 3.3,
                "weather": "rain"
            },
            {
                "date": "2014-07-24",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 12.8,
                "wind": 4.7,
                "weather": "sun"
            },
            {
                "date": "2014-07-25",
                "precipitation": 0.0,
                "temp_max": 22.8,
                "temp_min": 12.2,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2014-07-26",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 13.3,
                "wind": 3.6,
                "weather": "sun"
            },
            {
                "date": "2014-07-27",
                "precipitation": 0.0,
                "temp_max": 28.3,
                "temp_min": 15.0,
                "wind": 4.1,
                "weather": "sun"
            },
            {
                "date": "2014-07-28",
                "precipitation": 0.0,
                "temp_max": 30.6,
                "temp_min": 15.0,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2014-07-29",
                "precipitation": 0.0,
                "temp_max": 30.0,
                "temp_min": 15.6,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2014-07-30",
                "precipitation": 0.0,
                "temp_max": 29.4,
                "temp_min": 14.4,
                "wind": 3.4,
                "weather": "sun"
            },
            {
                "date": "2014-07-31",
                "precipitation": 0.0,
                "temp_max": 30.6,
                "temp_min": 17.8,
                "wind": 4.1,
                "weather": "sun"
            },
            {
                "date": "2014-08-01",
                "precipitation": 0.0,
                "temp_max": 28.9,
                "temp_min": 15.0,
                "wind": 3.3,
                "weather": "sun"
            },
            {
                "date": "2014-08-02",
                "precipitation": 0.5,
                "temp_max": 29.4,
                "temp_min": 15.6,
                "wind": 1.7,
                "weather": "rain"
            },
            {
                "date": "2014-08-03",
                "precipitation": 0.0,
                "temp_max": 31.7,
                "temp_min": 14.4,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2014-08-04",
                "precipitation": 0.0,
                "temp_max": 32.8,
                "temp_min": 16.1,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2014-08-05",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 13.9,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2014-08-06",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 15.0,
                "wind": 2.2,
                "weather": "fog"
            },
            {
                "date": "2014-08-07",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 13.3,
                "wind": 2.4,
                "weather": "fog"
            },
            {
                "date": "2014-08-08",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 13.3,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2014-08-09",
                "precipitation": 0.0,
                "temp_max": 27.2,
                "temp_min": 15.6,
                "wind": 4.1,
                "weather": "sun"
            },
            {
                "date": "2014-08-10",
                "precipitation": 0.0,
                "temp_max": 30.6,
                "temp_min": 13.9,
                "wind": 3.4,
                "weather": "sun"
            },
            {
                "date": "2014-08-11",
                "precipitation": 0.5,
                "temp_max": 35.6,
                "temp_min": 17.8,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2014-08-12",
                "precipitation": 12.7,
                "temp_max": 27.2,
                "temp_min": 17.2,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2014-08-13",
                "precipitation": 21.6,
                "temp_max": 23.3,
                "temp_min": 15.0,
                "wind": 2.7,
                "weather": "rain"
            },
            {
                "date": "2014-08-14",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 17.2,
                "wind": 0.6,
                "weather": "sun"
            },
            {
                "date": "2014-08-15",
                "precipitation": 1.0,
                "temp_max": 24.4,
                "temp_min": 16.7,
                "wind": 1.5,
                "weather": "rain"
            },
            {
                "date": "2014-08-16",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 15.6,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2014-08-17",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 15.0,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2014-08-18",
                "precipitation": 0.0,
                "temp_max": 29.4,
                "temp_min": 15.6,
                "wind": 3.3,
                "weather": "sun"
            },
            {
                "date": "2014-08-19",
                "precipitation": 0.0,
                "temp_max": 27.2,
                "temp_min": 15.6,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2014-08-20",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 13.9,
                "wind": 3.6,
                "weather": "sun"
            },
            {
                "date": "2014-08-21",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 11.1,
                "wind": 1.7,
                "weather": "sun"
            },
            {
                "date": "2014-08-22",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 13.3,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2014-08-23",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 13.9,
                "wind": 2.0,
                "weather": "sun"
            },
            {
                "date": "2014-08-24",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 13.3,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2014-08-25",
                "precipitation": 0.0,
                "temp_max": 28.9,
                "temp_min": 14.4,
                "wind": 2.0,
                "weather": "sun"
            },
            {
                "date": "2014-08-26",
                "precipitation": 0.0,
                "temp_max": 31.1,
                "temp_min": 15.6,
                "wind": 1.8,
                "weather": "sun"
            },
            {
                "date": "2014-08-27",
                "precipitation": 0.0,
                "temp_max": 28.9,
                "temp_min": 16.1,
                "wind": 1.6,
                "weather": "sun"
            },
            {
                "date": "2014-08-28",
                "precipitation": 0.0,
                "temp_max": 23.3,
                "temp_min": 14.4,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2014-08-29",
                "precipitation": 0.0,
                "temp_max": 22.8,
                "temp_min": 15.0,
                "wind": 3.4,
                "weather": "sun"
            },
            {
                "date": "2014-08-30",
                "precipitation": 8.4,
                "temp_max": 17.8,
                "temp_min": 15.0,
                "wind": 2.2,
                "weather": "rain"
            },
            {
                "date": "2014-08-31",
                "precipitation": 1.3,
                "temp_max": 21.1,
                "temp_min": 13.9,
                "wind": 1.9,
                "weather": "rain"
            },
            {
                "date": "2014-09-01",
                "precipitation": 0.0,
                "temp_max": 23.3,
                "temp_min": 12.8,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2014-09-02",
                "precipitation": 3.0,
                "temp_max": 20.0,
                "temp_min": 13.9,
                "wind": 4.3,
                "weather": "rain"
            },
            {
                "date": "2014-09-03",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 12.8,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2014-09-04",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 11.1,
                "wind": 3.1,
                "weather": "fog"
            },
            {
                "date": "2014-09-05",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 13.9,
                "wind": 6.5,
                "weather": "fog"
            },
            {
                "date": "2014-09-06",
                "precipitation": 0.0,
                "temp_max": 32.2,
                "temp_min": 15.0,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2014-09-07",
                "precipitation": 0.0,
                "temp_max": 28.3,
                "temp_min": 13.3,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2014-09-08",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 13.3,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2014-09-09",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 13.3,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2014-09-10",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 12.2,
                "wind": 3.9,
                "weather": "sun"
            },
            {
                "date": "2014-09-11",
                "precipitation": 0.0,
                "temp_max": 24.4,
                "temp_min": 12.8,
                "wind": 5.3,
                "weather": "sun"
            },
            {
                "date": "2014-09-12",
                "precipitation": 0.0,
                "temp_max": 24.4,
                "temp_min": 12.8,
                "wind": 5.9,
                "weather": "sun"
            },
            {
                "date": "2014-09-13",
                "precipitation": 0.0,
                "temp_max": 28.3,
                "temp_min": 10.0,
                "wind": 4.2,
                "weather": "sun"
            },
            {
                "date": "2014-09-14",
                "precipitation": 0.0,
                "temp_max": 30.0,
                "temp_min": 11.7,
                "wind": 1.8,
                "weather": "sun"
            },
            {
                "date": "2014-09-15",
                "precipitation": 0.0,
                "temp_max": 30.6,
                "temp_min": 12.2,
                "wind": 1.2,
                "weather": "sun"
            },
            {
                "date": "2014-09-16",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 13.9,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2014-09-17",
                "precipitation": 0.5,
                "temp_max": 22.8,
                "temp_min": 14.4,
                "wind": 2.3,
                "weather": "rain"
            },
            {
                "date": "2014-09-18",
                "precipitation": 0.3,
                "temp_max": 19.4,
                "temp_min": 15.0,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2014-09-19",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 16.1,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2014-09-20",
                "precipitation": 0.0,
                "temp_max": 24.4,
                "temp_min": 14.4,
                "wind": 4.4,
                "weather": "fog"
            },
            {
                "date": "2014-09-21",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 12.8,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2014-09-22",
                "precipitation": 0.3,
                "temp_max": 22.2,
                "temp_min": 15.0,
                "wind": 2.1,
                "weather": "rain"
            },
            {
                "date": "2014-09-23",
                "precipitation": 18.3,
                "temp_max": 18.9,
                "temp_min": 14.4,
                "wind": 2.5,
                "weather": "rain"
            },
            {
                "date": "2014-09-24",
                "precipitation": 20.3,
                "temp_max": 18.9,
                "temp_min": 14.4,
                "wind": 2.7,
                "weather": "rain"
            },
            {
                "date": "2014-09-25",
                "precipitation": 4.3,
                "temp_max": 21.7,
                "temp_min": 14.4,
                "wind": 2.5,
                "weather": "rain"
            },
            {
                "date": "2014-09-26",
                "precipitation": 8.9,
                "temp_max": 20.0,
                "temp_min": 13.9,
                "wind": 3.3,
                "weather": "rain"
            },
            {
                "date": "2014-09-27",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 11.7,
                "wind": 3.2,
                "weather": "fog"
            },
            {
                "date": "2014-09-28",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 12.2,
                "wind": 2.0,
                "weather": "fog"
            },
            {
                "date": "2014-09-29",
                "precipitation": 0.8,
                "temp_max": 16.7,
                "temp_min": 11.1,
                "wind": 3.5,
                "weather": "rain"
            },
            {
                "date": "2014-09-30",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 12.2,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2014-10-01",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 11.1,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2014-10-02",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 10.0,
                "wind": 2.0,
                "weather": "sun"
            },
            {
                "date": "2014-10-03",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 8.9,
                "wind": 1.0,
                "weather": "sun"
            },
            {
                "date": "2014-10-04",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 12.2,
                "wind": 1.2,
                "weather": "sun"
            },
            {
                "date": "2014-10-05",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 11.7,
                "wind": 1.4,
                "weather": "fog"
            },
            {
                "date": "2014-10-06",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 13.3,
                "wind": 2.5,
                "weather": "fog"
            },
            {
                "date": "2014-10-07",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 13.9,
                "wind": 1.0,
                "weather": "fog"
            },
            {
                "date": "2014-10-08",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 12.8,
                "wind": 1.8,
                "weather": "fog"
            },
            {
                "date": "2014-10-09",
                "precipitation": 0.0,
                "temp_max": 17.2,
                "temp_min": 11.1,
                "wind": 1.0,
                "weather": "fog"
            },
            {
                "date": "2014-10-10",
                "precipitation": 0.3,
                "temp_max": 18.3,
                "temp_min": 10.0,
                "wind": 3.8,
                "weather": "rain"
            },
            {
                "date": "2014-10-11",
                "precipitation": 7.4,
                "temp_max": 18.3,
                "temp_min": 11.7,
                "wind": 3.5,
                "weather": "rain"
            },
            {
                "date": "2014-10-12",
                "precipitation": 0.0,
                "temp_max": 17.8,
                "temp_min": 11.7,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2014-10-13",
                "precipitation": 7.6,
                "temp_max": 21.1,
                "temp_min": 10.0,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2014-10-14",
                "precipitation": 7.1,
                "temp_max": 16.7,
                "temp_min": 11.7,
                "wind": 2.2,
                "weather": "rain"
            },
            {
                "date": "2014-10-15",
                "precipitation": 8.6,
                "temp_max": 16.1,
                "temp_min": 11.7,
                "wind": 4.7,
                "weather": "rain"
            },
            {
                "date": "2014-10-16",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 11.1,
                "wind": 3.3,
                "weather": "sun"
            },
            {
                "date": "2014-10-17",
                "precipitation": 3.3,
                "temp_max": 16.7,
                "temp_min": 11.7,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2014-10-18",
                "precipitation": 15.0,
                "temp_max": 19.4,
                "temp_min": 13.9,
                "wind": 1.9,
                "weather": "rain"
            },
            {
                "date": "2014-10-19",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 12.8,
                "wind": 3.2,
                "weather": "sun"
            },
            {
                "date": "2014-10-20",
                "precipitation": 11.7,
                "temp_max": 16.1,
                "temp_min": 12.2,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2014-10-21",
                "precipitation": 1.0,
                "temp_max": 16.1,
                "temp_min": 11.7,
                "wind": 4.7,
                "weather": "rain"
            },
            {
                "date": "2014-10-22",
                "precipitation": 32.0,
                "temp_max": 15.6,
                "temp_min": 11.7,
                "wind": 5.0,
                "weather": "rain"
            },
            {
                "date": "2014-10-23",
                "precipitation": 9.4,
                "temp_max": 14.4,
                "temp_min": 8.3,
                "wind": 4.6,
                "weather": "rain"
            },
            {
                "date": "2014-10-24",
                "precipitation": 4.1,
                "temp_max": 14.4,
                "temp_min": 8.9,
                "wind": 3.2,
                "weather": "rain"
            },
            {
                "date": "2014-10-25",
                "precipitation": 6.1,
                "temp_max": 16.7,
                "temp_min": 8.3,
                "wind": 5.4,
                "weather": "rain"
            },
            {
                "date": "2014-10-26",
                "precipitation": 1.5,
                "temp_max": 12.8,
                "temp_min": 7.8,
                "wind": 5.0,
                "weather": "rain"
            },
            {
                "date": "2014-10-27",
                "precipitation": 0.8,
                "temp_max": 15.6,
                "temp_min": 6.7,
                "wind": 2.4,
                "weather": "rain"
            },
            {
                "date": "2014-10-28",
                "precipitation": 12.7,
                "temp_max": 15.0,
                "temp_min": 9.4,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2014-10-29",
                "precipitation": 0.5,
                "temp_max": 16.7,
                "temp_min": 11.7,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2014-10-30",
                "precipitation": 25.4,
                "temp_max": 15.6,
                "temp_min": 11.1,
                "wind": 3.2,
                "weather": "rain"
            },
            {
                "date": "2014-10-31",
                "precipitation": 17.0,
                "temp_max": 12.8,
                "temp_min": 8.3,
                "wind": 2.0,
                "weather": "rain"
            },
            {
                "date": "2014-11-01",
                "precipitation": 0.0,
                "temp_max": 11.1,
                "temp_min": 7.2,
                "wind": 1.2,
                "weather": "fog"
            },
            {
                "date": "2014-11-02",
                "precipitation": 1.8,
                "temp_max": 13.3,
                "temp_min": 7.2,
                "wind": 2.9,
                "weather": "rain"
            },
            {
                "date": "2014-11-03",
                "precipitation": 10.9,
                "temp_max": 13.9,
                "temp_min": 11.1,
                "wind": 4.8,
                "weather": "rain"
            },
            {
                "date": "2014-11-04",
                "precipitation": 4.1,
                "temp_max": 14.4,
                "temp_min": 10.6,
                "wind": 3.3,
                "weather": "rain"
            },
            {
                "date": "2014-11-05",
                "precipitation": 4.8,
                "temp_max": 15.0,
                "temp_min": 10.6,
                "wind": 2.1,
                "weather": "rain"
            },
            {
                "date": "2014-11-06",
                "precipitation": 4.1,
                "temp_max": 16.7,
                "temp_min": 10.6,
                "wind": 6.7,
                "weather": "rain"
            },
            {
                "date": "2014-11-07",
                "precipitation": 0.0,
                "temp_max": 14.4,
                "temp_min": 7.2,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2014-11-08",
                "precipitation": 0.0,
                "temp_max": 12.8,
                "temp_min": 3.9,
                "wind": 0.8,
                "weather": "fog"
            },
            {
                "date": "2014-11-09",
                "precipitation": 5.1,
                "temp_max": 13.3,
                "temp_min": 7.8,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2014-11-10",
                "precipitation": 0.0,
                "temp_max": 11.1,
                "temp_min": 5.6,
                "wind": 3.9,
                "weather": "sun"
            },
            {
                "date": "2014-11-11",
                "precipitation": 0.0,
                "temp_max": 7.8,
                "temp_min": 1.1,
                "wind": 7.7,
                "weather": "sun"
            },
            {
                "date": "2014-11-12",
                "precipitation": 0.0,
                "temp_max": 6.7,
                "temp_min": 0.0,
                "wind": 7.6,
                "weather": "sun"
            },
            {
                "date": "2014-11-13",
                "precipitation": 0.0,
                "temp_max": 7.2,
                "temp_min": 0.6,
                "wind": 4.7,
                "weather": "sun"
            },
            {
                "date": "2014-11-14",
                "precipitation": 0.0,
                "temp_max": 7.2,
                "temp_min": -2.1,
                "wind": 4.5,
                "weather": "sun"
            },
            {
                "date": "2014-11-15",
                "precipitation": 0.0,
                "temp_max": 8.3,
                "temp_min": -1.6,
                "wind": 4.2,
                "weather": "sun"
            },
            {
                "date": "2014-11-16",
                "precipitation": 0.0,
                "temp_max": 9.4,
                "temp_min": -2.1,
                "wind": 4.2,
                "weather": "sun"
            },
            {
                "date": "2014-11-17",
                "precipitation": 0.0,
                "temp_max": 10.6,
                "temp_min": -2.1,
                "wind": 1.9,
                "weather": "sun"
            },
            {
                "date": "2014-11-18",
                "precipitation": 0.0,
                "temp_max": 7.2,
                "temp_min": -0.5,
                "wind": 0.9,
                "weather": "sun"
            },
            {
                "date": "2014-11-19",
                "precipitation": 0.0,
                "temp_max": 11.1,
                "temp_min": 2.2,
                "wind": 1.9,
                "weather": "sun"
            },
            {
                "date": "2014-11-20",
                "precipitation": 3.6,
                "temp_max": 11.1,
                "temp_min": 5.6,
                "wind": 2.1,
                "weather": "rain"
            },
            {
                "date": "2014-11-21",
                "precipitation": 15.2,
                "temp_max": 11.1,
                "temp_min": 8.3,
                "wind": 4.7,
                "weather": "rain"
            },
            {
                "date": "2014-11-22",
                "precipitation": 0.5,
                "temp_max": 9.4,
                "temp_min": 6.7,
                "wind": 4.7,
                "weather": "rain"
            },
            {
                "date": "2014-11-23",
                "precipitation": 11.9,
                "temp_max": 12.8,
                "temp_min": 5.6,
                "wind": 5.1,
                "weather": "rain"
            },
            {
                "date": "2014-11-24",
                "precipitation": 1.3,
                "temp_max": 11.7,
                "temp_min": 4.4,
                "wind": 3.8,
                "weather": "rain"
            },
            {
                "date": "2014-11-25",
                "precipitation": 18.3,
                "temp_max": 13.9,
                "temp_min": 9.4,
                "wind": 4.5,
                "weather": "rain"
            },
            {
                "date": "2014-11-26",
                "precipitation": 0.3,
                "temp_max": 15.0,
                "temp_min": 12.2,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2014-11-27",
                "precipitation": 3.3,
                "temp_max": 14.4,
                "temp_min": 11.7,
                "wind": 6.6,
                "weather": "rain"
            },
            {
                "date": "2014-11-28",
                "precipitation": 34.3,
                "temp_max": 12.8,
                "temp_min": 3.3,
                "wind": 5.8,
                "weather": "rain"
            },
            {
                "date": "2014-11-29",
                "precipitation": 3.6,
                "temp_max": 4.4,
                "temp_min": -4.3,
                "wind": 5.3,
                "weather": "snow"
            },
            {
                "date": "2014-11-30",
                "precipitation": 0.0,
                "temp_max": 2.8,
                "temp_min": -4.9,
                "wind": 4.4,
                "weather": "sun"
            },
            {
                "date": "2014-12-01",
                "precipitation": 0.0,
                "temp_max": 4.4,
                "temp_min": -3.2,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2014-12-02",
                "precipitation": 0.0,
                "temp_max": 5.6,
                "temp_min": -3.2,
                "wind": 5.7,
                "weather": "fog"
            },
            {
                "date": "2014-12-03",
                "precipitation": 0.0,
                "temp_max": 10.0,
                "temp_min": 0.0,
                "wind": 3.6,
                "weather": "sun"
            },
            {
                "date": "2014-12-04",
                "precipitation": 0.8,
                "temp_max": 8.3,
                "temp_min": 3.9,
                "wind": 1.1,
                "weather": "rain"
            },
            {
                "date": "2014-12-05",
                "precipitation": 3.0,
                "temp_max": 12.8,
                "temp_min": 6.7,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2014-12-06",
                "precipitation": 7.4,
                "temp_max": 11.7,
                "temp_min": 7.8,
                "wind": 3.6,
                "weather": "rain"
            },
            {
                "date": "2014-12-07",
                "precipitation": 0.0,
                "temp_max": 14.4,
                "temp_min": 6.1,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2014-12-08",
                "precipitation": 9.1,
                "temp_max": 14.4,
                "temp_min": 8.9,
                "wind": 4.2,
                "weather": "rain"
            },
            {
                "date": "2014-12-09",
                "precipitation": 9.9,
                "temp_max": 16.1,
                "temp_min": 10.6,
                "wind": 5.1,
                "weather": "rain"
            },
            {
                "date": "2014-12-10",
                "precipitation": 13.0,
                "temp_max": 18.9,
                "temp_min": 10.0,
                "wind": 6.7,
                "weather": "rain"
            },
            {
                "date": "2014-12-11",
                "precipitation": 6.9,
                "temp_max": 14.4,
                "temp_min": 8.3,
                "wind": 6.4,
                "weather": "rain"
            },
            {
                "date": "2014-12-12",
                "precipitation": 0.0,
                "temp_max": 11.1,
                "temp_min": 7.2,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2014-12-13",
                "precipitation": 0.0,
                "temp_max": 10.0,
                "temp_min": 3.9,
                "wind": 1.1,
                "weather": "fog"
            },
            {
                "date": "2014-12-14",
                "precipitation": 0.0,
                "temp_max": 12.8,
                "temp_min": 1.7,
                "wind": 3.5,
                "weather": "fog"
            },
            {
                "date": "2014-12-15",
                "precipitation": 0.0,
                "temp_max": 12.2,
                "temp_min": 6.7,
                "wind": 5.9,
                "weather": "sun"
            },
            {
                "date": "2014-12-16",
                "precipitation": 0.0,
                "temp_max": 10.0,
                "temp_min": 8.3,
                "wind": 4.0,
                "weather": "sun"
            },
            {
                "date": "2014-12-17",
                "precipitation": 2.8,
                "temp_max": 8.9,
                "temp_min": 6.1,
                "wind": 1.6,
                "weather": "rain"
            },
            {
                "date": "2014-12-18",
                "precipitation": 13.0,
                "temp_max": 9.4,
                "temp_min": 6.7,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2014-12-19",
                "precipitation": 3.0,
                "temp_max": 11.1,
                "temp_min": 7.2,
                "wind": 4.3,
                "weather": "rain"
            },
            {
                "date": "2014-12-20",
                "precipitation": 19.6,
                "temp_max": 12.8,
                "temp_min": 6.7,
                "wind": 5.5,
                "weather": "rain"
            },
            {
                "date": "2014-12-21",
                "precipitation": 0.0,
                "temp_max": 12.8,
                "temp_min": 10.0,
                "wind": 5.2,
                "weather": "sun"
            },
            {
                "date": "2014-12-22",
                "precipitation": 0.0,
                "temp_max": 10.6,
                "temp_min": 6.1,
                "wind": 1.5,
                "weather": "sun"
            },
            {
                "date": "2014-12-23",
                "precipitation": 20.6,
                "temp_max": 12.2,
                "temp_min": 5.0,
                "wind": 3.8,
                "weather": "rain"
            },
            {
                "date": "2014-12-24",
                "precipitation": 5.3,
                "temp_max": 7.2,
                "temp_min": 3.9,
                "wind": 1.8,
                "weather": "rain"
            },
            {
                "date": "2014-12-25",
                "precipitation": 0.0,
                "temp_max": 7.8,
                "temp_min": 2.8,
                "wind": 2.2,
                "weather": "fog"
            },
            {
                "date": "2014-12-26",
                "precipitation": 0.0,
                "temp_max": 5.6,
                "temp_min": 1.7,
                "wind": 1.2,
                "weather": "fog"
            },
            {
                "date": "2014-12-27",
                "precipitation": 3.3,
                "temp_max": 9.4,
                "temp_min": 4.4,
                "wind": 4.9,
                "weather": "rain"
            },
            {
                "date": "2014-12-28",
                "precipitation": 4.1,
                "temp_max": 6.7,
                "temp_min": 2.8,
                "wind": 1.8,
                "weather": "rain"
            },
            {
                "date": "2014-12-29",
                "precipitation": 0.0,
                "temp_max": 6.1,
                "temp_min": 0.6,
                "wind": 4.3,
                "weather": "fog"
            },
            {
                "date": "2014-12-30",
                "precipitation": 0.0,
                "temp_max": 3.3,
                "temp_min": -2.1,
                "wind": 3.6,
                "weather": "sun"
            },
            {
                "date": "2014-12-31",
                "precipitation": 0.0,
                "temp_max": 3.3,
                "temp_min": -2.7,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2015-01-01",
                "precipitation": 0.0,
                "temp_max": 5.6,
                "temp_min": -3.2,
                "wind": 1.2,
                "weather": "sun"
            },
            {
                "date": "2015-01-02",
                "precipitation": 1.5,
                "temp_max": 5.6,
                "temp_min": 0.0,
                "wind": 2.3,
                "weather": "rain"
            },
            {
                "date": "2015-01-03",
                "precipitation": 0.0,
                "temp_max": 5.0,
                "temp_min": 1.7,
                "wind": 1.7,
                "weather": "fog"
            },
            {
                "date": "2015-01-04",
                "precipitation": 10.2,
                "temp_max": 10.6,
                "temp_min": 3.3,
                "wind": 4.5,
                "weather": "rain"
            },
            {
                "date": "2015-01-05",
                "precipitation": 8.1,
                "temp_max": 12.2,
                "temp_min": 9.4,
                "wind": 6.4,
                "weather": "rain"
            },
            {
                "date": "2015-01-06",
                "precipitation": 0.0,
                "temp_max": 12.2,
                "temp_min": 6.1,
                "wind": 1.3,
                "weather": "fog"
            },
            {
                "date": "2015-01-07",
                "precipitation": 0.0,
                "temp_max": 7.8,
                "temp_min": 5.6,
                "wind": 1.6,
                "weather": "fog"
            },
            {
                "date": "2015-01-08",
                "precipitation": 0.0,
                "temp_max": 7.8,
                "temp_min": 1.7,
                "wind": 2.6,
                "weather": "fog"
            },
            {
                "date": "2015-01-09",
                "precipitation": 0.3,
                "temp_max": 10.0,
                "temp_min": 3.3,
                "wind": 0.6,
                "weather": "rain"
            },
            {
                "date": "2015-01-10",
                "precipitation": 5.8,
                "temp_max": 7.8,
                "temp_min": 6.1,
                "wind": 0.5,
                "weather": "rain"
            },
            {
                "date": "2015-01-11",
                "precipitation": 1.5,
                "temp_max": 9.4,
                "temp_min": 7.2,
                "wind": 1.1,
                "weather": "rain"
            },
            {
                "date": "2015-01-12",
                "precipitation": 0.0,
                "temp_max": 11.1,
                "temp_min": 4.4,
                "wind": 1.6,
                "weather": "fog"
            },
            {
                "date": "2015-01-13",
                "precipitation": 0.0,
                "temp_max": 9.4,
                "temp_min": 2.8,
                "wind": 2.7,
                "weather": "fog"
            },
            {
                "date": "2015-01-14",
                "precipitation": 0.0,
                "temp_max": 6.1,
                "temp_min": 0.6,
                "wind": 2.8,
                "weather": "fog"
            },
            {
                "date": "2015-01-15",
                "precipitation": 9.7,
                "temp_max": 7.8,
                "temp_min": 1.1,
                "wind": 3.2,
                "weather": "rain"
            },
            {
                "date": "2015-01-16",
                "precipitation": 0.0,
                "temp_max": 11.7,
                "temp_min": 5.6,
                "wind": 4.5,
                "weather": "fog"
            },
            {
                "date": "2015-01-17",
                "precipitation": 26.2,
                "temp_max": 13.3,
                "temp_min": 3.3,
                "wind": 2.8,
                "weather": "rain"
            },
            {
                "date": "2015-01-18",
                "precipitation": 21.3,
                "temp_max": 13.9,
                "temp_min": 7.2,
                "wind": 6.6,
                "weather": "rain"
            },
            {
                "date": "2015-01-19",
                "precipitation": 0.5,
                "temp_max": 10.0,
                "temp_min": 6.1,
                "wind": 2.8,
                "weather": "rain"
            },
            {
                "date": "2015-01-20",
                "precipitation": 0.0,
                "temp_max": 10.0,
                "temp_min": 3.3,
                "wind": 3.0,
                "weather": "fog"
            },
            {
                "date": "2015-01-21",
                "precipitation": 0.0,
                "temp_max": 7.2,
                "temp_min": -0.5,
                "wind": 1.3,
                "weather": "fog"
            },
            {
                "date": "2015-01-22",
                "precipitation": 0.8,
                "temp_max": 9.4,
                "temp_min": 6.1,
                "wind": 1.3,
                "weather": "rain"
            },
            {
                "date": "2015-01-23",
                "precipitation": 5.8,
                "temp_max": 12.2,
                "temp_min": 8.3,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2015-01-24",
                "precipitation": 0.5,
                "temp_max": 14.4,
                "temp_min": 11.1,
                "wind": 3.3,
                "weather": "rain"
            },
            {
                "date": "2015-01-25",
                "precipitation": 0.0,
                "temp_max": 17.2,
                "temp_min": 7.2,
                "wind": 1.4,
                "weather": "fog"
            },
            {
                "date": "2015-01-26",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 6.1,
                "wind": 2.2,
                "weather": "fog"
            },
            {
                "date": "2015-01-27",
                "precipitation": 0.8,
                "temp_max": 11.1,
                "temp_min": 8.3,
                "wind": 2.0,
                "weather": "rain"
            },
            {
                "date": "2015-01-28",
                "precipitation": 0.0,
                "temp_max": 12.2,
                "temp_min": 5.0,
                "wind": 1.8,
                "weather": "fog"
            },
            {
                "date": "2015-01-29",
                "precipitation": 0.0,
                "temp_max": 12.2,
                "temp_min": 3.3,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2015-01-30",
                "precipitation": 0.0,
                "temp_max": 8.3,
                "temp_min": 1.1,
                "wind": 0.8,
                "weather": "fog"
            },
            {
                "date": "2015-01-31",
                "precipitation": 0.0,
                "temp_max": 7.2,
                "temp_min": 3.3,
                "wind": 1.9,
                "weather": "fog"
            },
            {
                "date": "2015-02-01",
                "precipitation": 1.5,
                "temp_max": 9.4,
                "temp_min": 4.4,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2015-02-02",
                "precipitation": 7.4,
                "temp_max": 11.1,
                "temp_min": 5.0,
                "wind": 4.0,
                "weather": "rain"
            },
            {
                "date": "2015-02-03",
                "precipitation": 1.3,
                "temp_max": 10.0,
                "temp_min": 5.6,
                "wind": 1.9,
                "weather": "rain"
            },
            {
                "date": "2015-02-04",
                "precipitation": 8.4,
                "temp_max": 10.6,
                "temp_min": 4.4,
                "wind": 1.7,
                "weather": "rain"
            },
            {
                "date": "2015-02-05",
                "precipitation": 26.2,
                "temp_max": 13.3,
                "temp_min": 8.3,
                "wind": 4.6,
                "weather": "rain"
            },
            {
                "date": "2015-02-06",
                "precipitation": 17.3,
                "temp_max": 14.4,
                "temp_min": 10.0,
                "wind": 4.5,
                "weather": "rain"
            },
            {
                "date": "2015-02-07",
                "precipitation": 23.6,
                "temp_max": 12.2,
                "temp_min": 9.4,
                "wind": 4.6,
                "weather": "rain"
            },
            {
                "date": "2015-02-08",
                "precipitation": 3.6,
                "temp_max": 15.0,
                "temp_min": 8.3,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2015-02-09",
                "precipitation": 6.1,
                "temp_max": 13.3,
                "temp_min": 8.3,
                "wind": 2.5,
                "weather": "rain"
            },
            {
                "date": "2015-02-10",
                "precipitation": 0.3,
                "temp_max": 12.8,
                "temp_min": 8.3,
                "wind": 4.0,
                "weather": "rain"
            },
            {
                "date": "2015-02-11",
                "precipitation": 0.0,
                "temp_max": 12.8,
                "temp_min": 5.6,
                "wind": 1.0,
                "weather": "fog"
            },
            {
                "date": "2015-02-12",
                "precipitation": 1.0,
                "temp_max": 16.7,
                "temp_min": 9.4,
                "wind": 2.1,
                "weather": "rain"
            },
            {
                "date": "2015-02-13",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 6.7,
                "wind": 1.7,
                "weather": "fog"
            },
            {
                "date": "2015-02-14",
                "precipitation": 0.3,
                "temp_max": 14.4,
                "temp_min": 6.7,
                "wind": 2.9,
                "weather": "rain"
            },
            {
                "date": "2015-02-15",
                "precipitation": 0.0,
                "temp_max": 12.2,
                "temp_min": 3.9,
                "wind": 4.8,
                "weather": "sun"
            },
            {
                "date": "2015-02-16",
                "precipitation": 0.0,
                "temp_max": 15.0,
                "temp_min": 5.6,
                "wind": 6.6,
                "weather": "fog"
            },
            {
                "date": "2015-02-17",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 4.4,
                "wind": 4.0,
                "weather": "sun"
            },
            {
                "date": "2015-02-18",
                "precipitation": 0.0,
                "temp_max": 12.2,
                "temp_min": 4.4,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2015-02-19",
                "precipitation": 4.6,
                "temp_max": 10.6,
                "temp_min": 8.3,
                "wind": 2.2,
                "weather": "rain"
            },
            {
                "date": "2015-02-20",
                "precipitation": 0.8,
                "temp_max": 11.1,
                "temp_min": 7.2,
                "wind": 0.9,
                "weather": "rain"
            },
            {
                "date": "2015-02-21",
                "precipitation": 0.0,
                "temp_max": 12.2,
                "temp_min": 5.6,
                "wind": 4.5,
                "weather": "sun"
            },
            {
                "date": "2015-02-22",
                "precipitation": 0.0,
                "temp_max": 11.7,
                "temp_min": 3.3,
                "wind": 4.2,
                "weather": "sun"
            },
            {
                "date": "2015-02-23",
                "precipitation": 0.0,
                "temp_max": 12.8,
                "temp_min": 0.6,
                "wind": 1.4,
                "weather": "sun"
            },
            {
                "date": "2015-02-24",
                "precipitation": 0.0,
                "temp_max": 11.1,
                "temp_min": 2.2,
                "wind": 1.5,
                "weather": "sun"
            },
            {
                "date": "2015-02-25",
                "precipitation": 4.1,
                "temp_max": 10.0,
                "temp_min": 6.7,
                "wind": 1.0,
                "weather": "rain"
            },
            {
                "date": "2015-02-26",
                "precipitation": 9.4,
                "temp_max": 11.7,
                "temp_min": 7.8,
                "wind": 1.4,
                "weather": "rain"
            },
            {
                "date": "2015-02-27",
                "precipitation": 18.3,
                "temp_max": 10.0,
                "temp_min": 6.7,
                "wind": 4.0,
                "weather": "rain"
            },
            {
                "date": "2015-02-28",
                "precipitation": 0.0,
                "temp_max": 12.2,
                "temp_min": 3.3,
                "wind": 5.1,
                "weather": "sun"
            },
            {
                "date": "2015-03-01",
                "precipitation": 0.0,
                "temp_max": 11.1,
                "temp_min": 1.1,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2015-03-02",
                "precipitation": 0.0,
                "temp_max": 11.1,
                "temp_min": 4.4,
                "wind": 4.8,
                "weather": "sun"
            },
            {
                "date": "2015-03-03",
                "precipitation": 0.0,
                "temp_max": 10.6,
                "temp_min": 0.0,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2015-03-04",
                "precipitation": 0.0,
                "temp_max": 12.8,
                "temp_min": -0.5,
                "wind": 1.8,
                "weather": "sun"
            },
            {
                "date": "2015-03-05",
                "precipitation": 0.0,
                "temp_max": 13.3,
                "temp_min": 2.8,
                "wind": 1.3,
                "weather": "sun"
            },
            {
                "date": "2015-03-06",
                "precipitation": 0.0,
                "temp_max": 15.0,
                "temp_min": 3.3,
                "wind": 1.4,
                "weather": "sun"
            },
            {
                "date": "2015-03-07",
                "precipitation": 0.0,
                "temp_max": 16.7,
                "temp_min": 3.9,
                "wind": 2.7,
                "weather": "fog"
            },
            {
                "date": "2015-03-08",
                "precipitation": 0.0,
                "temp_max": 17.2,
                "temp_min": 3.9,
                "wind": 1.7,
                "weather": "fog"
            },
            {
                "date": "2015-03-09",
                "precipitation": 0.0,
                "temp_max": 14.4,
                "temp_min": 4.4,
                "wind": 1.8,
                "weather": "fog"
            },
            {
                "date": "2015-03-10",
                "precipitation": 0.8,
                "temp_max": 13.3,
                "temp_min": 5.0,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2015-03-11",
                "precipitation": 2.5,
                "temp_max": 14.4,
                "temp_min": 8.9,
                "wind": 3.1,
                "weather": "rain"
            },
            {
                "date": "2015-03-12",
                "precipitation": 0.0,
                "temp_max": 17.8,
                "temp_min": 9.4,
                "wind": 3.2,
                "weather": "sun"
            },
            {
                "date": "2015-03-13",
                "precipitation": 2.0,
                "temp_max": 17.2,
                "temp_min": 7.8,
                "wind": 2.2,
                "weather": "rain"
            },
            {
                "date": "2015-03-14",
                "precipitation": 17.0,
                "temp_max": 13.9,
                "temp_min": 9.4,
                "wind": 3.8,
                "weather": "rain"
            },
            {
                "date": "2015-03-15",
                "precipitation": 55.9,
                "temp_max": 10.6,
                "temp_min": 6.1,
                "wind": 4.2,
                "weather": "rain"
            },
            {
                "date": "2015-03-16",
                "precipitation": 1.0,
                "temp_max": 13.9,
                "temp_min": 6.1,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2015-03-17",
                "precipitation": 0.8,
                "temp_max": 13.3,
                "temp_min": 4.4,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2015-03-18",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 7.2,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2015-03-19",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 8.3,
                "wind": 1.9,
                "weather": "sun"
            },
            {
                "date": "2015-03-20",
                "precipitation": 4.1,
                "temp_max": 13.9,
                "temp_min": 8.9,
                "wind": 1.9,
                "weather": "rain"
            },
            {
                "date": "2015-03-21",
                "precipitation": 3.8,
                "temp_max": 13.3,
                "temp_min": 8.3,
                "wind": 4.7,
                "weather": "rain"
            },
            {
                "date": "2015-03-22",
                "precipitation": 1.0,
                "temp_max": 11.7,
                "temp_min": 6.1,
                "wind": 2.3,
                "weather": "rain"
            },
            {
                "date": "2015-03-23",
                "precipitation": 8.1,
                "temp_max": 11.1,
                "temp_min": 5.6,
                "wind": 2.8,
                "weather": "rain"
            },
            {
                "date": "2015-03-24",
                "precipitation": 7.6,
                "temp_max": 12.8,
                "temp_min": 6.1,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2015-03-25",
                "precipitation": 5.1,
                "temp_max": 14.4,
                "temp_min": 7.2,
                "wind": 4.4,
                "weather": "rain"
            },
            {
                "date": "2015-03-26",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 10.0,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2015-03-27",
                "precipitation": 1.0,
                "temp_max": 18.3,
                "temp_min": 8.9,
                "wind": 4.0,
                "weather": "rain"
            },
            {
                "date": "2015-03-28",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 9.4,
                "wind": 5.7,
                "weather": "sun"
            },
            {
                "date": "2015-03-29",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 8.9,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2015-03-30",
                "precipitation": 1.8,
                "temp_max": 17.8,
                "temp_min": 10.6,
                "wind": 2.9,
                "weather": "rain"
            },
            {
                "date": "2015-03-31",
                "precipitation": 1.0,
                "temp_max": 12.8,
                "temp_min": 6.1,
                "wind": 4.2,
                "weather": "rain"
            },
            {
                "date": "2015-04-01",
                "precipitation": 5.1,
                "temp_max": 12.8,
                "temp_min": 5.6,
                "wind": 3.2,
                "weather": "rain"
            },
            {
                "date": "2015-04-02",
                "precipitation": 0.0,
                "temp_max": 13.3,
                "temp_min": 5.6,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2015-04-03",
                "precipitation": 1.5,
                "temp_max": 11.1,
                "temp_min": 5.0,
                "wind": 3.6,
                "weather": "rain"
            },
            {
                "date": "2015-04-04",
                "precipitation": 0.0,
                "temp_max": 12.8,
                "temp_min": 3.9,
                "wind": 1.7,
                "weather": "sun"
            },
            {
                "date": "2015-04-05",
                "precipitation": 0.0,
                "temp_max": 16.7,
                "temp_min": 2.8,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2015-04-06",
                "precipitation": 1.0,
                "temp_max": 13.9,
                "temp_min": 6.7,
                "wind": 3.5,
                "weather": "rain"
            },
            {
                "date": "2015-04-07",
                "precipitation": 0.5,
                "temp_max": 14.4,
                "temp_min": 6.7,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2015-04-08",
                "precipitation": 0.0,
                "temp_max": 17.2,
                "temp_min": 6.1,
                "wind": 1.7,
                "weather": "sun"
            },
            {
                "date": "2015-04-09",
                "precipitation": 0.0,
                "temp_max": 17.2,
                "temp_min": 6.1,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2015-04-10",
                "precipitation": 10.9,
                "temp_max": 13.9,
                "temp_min": 7.8,
                "wind": 4.6,
                "weather": "rain"
            },
            {
                "date": "2015-04-11",
                "precipitation": 0.0,
                "temp_max": 11.7,
                "temp_min": 5.6,
                "wind": 6.5,
                "weather": "sun"
            },
            {
                "date": "2015-04-12",
                "precipitation": 0.0,
                "temp_max": 13.3,
                "temp_min": 5.6,
                "wind": 3.6,
                "weather": "sun"
            },
            {
                "date": "2015-04-13",
                "precipitation": 14.0,
                "temp_max": 11.7,
                "temp_min": 3.9,
                "wind": 3.6,
                "weather": "rain"
            },
            {
                "date": "2015-04-14",
                "precipitation": 3.3,
                "temp_max": 11.7,
                "temp_min": 2.8,
                "wind": 3.3,
                "weather": "rain"
            },
            {
                "date": "2015-04-15",
                "precipitation": 0.0,
                "temp_max": 13.9,
                "temp_min": 3.3,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2015-04-16",
                "precipitation": 0.0,
                "temp_max": 17.8,
                "temp_min": 3.9,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2015-04-17",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 6.1,
                "wind": 3.6,
                "weather": "sun"
            },
            {
                "date": "2015-04-18",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 8.3,
                "wind": 3.9,
                "weather": "sun"
            },
            {
                "date": "2015-04-19",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 8.3,
                "wind": 3.6,
                "weather": "sun"
            },
            {
                "date": "2015-04-20",
                "precipitation": 0.0,
                "temp_max": 22.8,
                "temp_min": 7.8,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2015-04-21",
                "precipitation": 5.6,
                "temp_max": 17.2,
                "temp_min": 6.7,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2015-04-22",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 5.0,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2015-04-23",
                "precipitation": 3.0,
                "temp_max": 12.2,
                "temp_min": 6.7,
                "wind": 4.1,
                "weather": "rain"
            },
            {
                "date": "2015-04-24",
                "precipitation": 3.3,
                "temp_max": 12.2,
                "temp_min": 6.1,
                "wind": 5.0,
                "weather": "rain"
            },
            {
                "date": "2015-04-25",
                "precipitation": 1.3,
                "temp_max": 13.3,
                "temp_min": 5.6,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2015-04-26",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 4.4,
                "wind": 2.7,
                "weather": "fog"
            },
            {
                "date": "2015-04-27",
                "precipitation": 0.3,
                "temp_max": 25.0,
                "temp_min": 10.6,
                "wind": 2.3,
                "weather": "rain"
            },
            {
                "date": "2015-04-28",
                "precipitation": 1.8,
                "temp_max": 15.6,
                "temp_min": 8.9,
                "wind": 4.3,
                "weather": "rain"
            },
            {
                "date": "2015-04-29",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 7.2,
                "wind": 4.7,
                "weather": "sun"
            },
            {
                "date": "2015-04-30",
                "precipitation": 0.0,
                "temp_max": 17.2,
                "temp_min": 7.8,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2015-05-01",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 8.9,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2015-05-02",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 7.8,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2015-05-03",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 7.8,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2015-05-04",
                "precipitation": 0.0,
                "temp_max": 17.2,
                "temp_min": 7.2,
                "wind": 5.2,
                "weather": "sun"
            },
            {
                "date": "2015-05-05",
                "precipitation": 6.1,
                "temp_max": 14.4,
                "temp_min": 7.2,
                "wind": 5.1,
                "weather": "rain"
            },
            {
                "date": "2015-05-06",
                "precipitation": 0.0,
                "temp_max": 16.7,
                "temp_min": 7.2,
                "wind": 2.6,
                "weather": "fog"
            },
            {
                "date": "2015-05-07",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 6.1,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2015-05-08",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 8.3,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2015-05-09",
                "precipitation": 0.0,
                "temp_max": 26.7,
                "temp_min": 9.4,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2015-05-10",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 11.1,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2015-05-11",
                "precipitation": 0.0,
                "temp_max": 13.9,
                "temp_min": 10.0,
                "wind": 2.5,
                "weather": "fog"
            },
            {
                "date": "2015-05-12",
                "precipitation": 4.3,
                "temp_max": 15.6,
                "temp_min": 10.6,
                "wind": 3.3,
                "weather": "rain"
            },
            {
                "date": "2015-05-13",
                "precipitation": 4.1,
                "temp_max": 12.2,
                "temp_min": 10.0,
                "wind": 2.8,
                "weather": "rain"
            },
            {
                "date": "2015-05-14",
                "precipitation": 0.3,
                "temp_max": 17.8,
                "temp_min": 9.4,
                "wind": 2.0,
                "weather": "rain"
            },
            {
                "date": "2015-05-15",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 9.4,
                "wind": 2.8,
                "weather": "fog"
            },
            {
                "date": "2015-05-16",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 11.1,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2015-05-17",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 10.6,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2015-05-18",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 12.2,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2015-05-19",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 11.7,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2015-05-20",
                "precipitation": 0.0,
                "temp_max": 23.3,
                "temp_min": 10.6,
                "wind": 1.8,
                "weather": "fog"
            },
            {
                "date": "2015-05-21",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 11.7,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2015-05-22",
                "precipitation": 0.0,
                "temp_max": 16.7,
                "temp_min": 11.7,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2015-05-23",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 11.7,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2015-05-24",
                "precipitation": 0.0,
                "temp_max": 17.8,
                "temp_min": 11.1,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2015-05-25",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 11.1,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2015-05-26",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 11.7,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2015-05-27",
                "precipitation": 0.0,
                "temp_max": 24.4,
                "temp_min": 11.7,
                "wind": 1.8,
                "weather": "sun"
            },
            {
                "date": "2015-05-28",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 12.2,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2015-05-29",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 12.8,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2015-05-30",
                "precipitation": 0.0,
                "temp_max": 22.8,
                "temp_min": 10.0,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2015-05-31",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 11.7,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2015-06-01",
                "precipitation": 4.6,
                "temp_max": 16.1,
                "temp_min": 11.7,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2015-06-02",
                "precipitation": 0.5,
                "temp_max": 17.8,
                "temp_min": 12.8,
                "wind": 5.0,
                "weather": "rain"
            },
            {
                "date": "2015-06-03",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 11.7,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2015-06-04",
                "precipitation": 0.0,
                "temp_max": 22.8,
                "temp_min": 11.7,
                "wind": 3.9,
                "weather": "sun"
            },
            {
                "date": "2015-06-05",
                "precipitation": 0.0,
                "temp_max": 26.7,
                "temp_min": 12.8,
                "wind": 4.3,
                "weather": "sun"
            },
            {
                "date": "2015-06-06",
                "precipitation": 0.0,
                "temp_max": 29.4,
                "temp_min": 13.3,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2015-06-07",
                "precipitation": 0.0,
                "temp_max": 31.1,
                "temp_min": 15.6,
                "wind": 3.2,
                "weather": "sun"
            },
            {
                "date": "2015-06-08",
                "precipitation": 0.0,
                "temp_max": 30.6,
                "temp_min": 14.4,
                "wind": 3.5,
                "weather": "sun"
            },
            {
                "date": "2015-06-09",
                "precipitation": 0.0,
                "temp_max": 28.9,
                "temp_min": 14.4,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2015-06-10",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 11.1,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2015-06-11",
                "precipitation": 0.0,
                "temp_max": 24.4,
                "temp_min": 11.1,
                "wind": 3.5,
                "weather": "sun"
            },
            {
                "date": "2015-06-12",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 11.7,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2015-06-13",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 9.4,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2015-06-14",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 11.7,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2015-06-15",
                "precipitation": 0.0,
                "temp_max": 30.0,
                "temp_min": 16.1,
                "wind": 3.5,
                "weather": "drizzle"
            },
            {
                "date": "2015-06-16",
                "precipitation": 0.0,
                "temp_max": 22.8,
                "temp_min": 11.1,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2015-06-17",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 11.1,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2015-06-18",
                "precipitation": 0.0,
                "temp_max": 24.4,
                "temp_min": 13.9,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2015-06-19",
                "precipitation": 0.5,
                "temp_max": 23.9,
                "temp_min": 13.3,
                "wind": 3.2,
                "weather": "rain"
            },
            {
                "date": "2015-06-20",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 12.8,
                "wind": 4.3,
                "weather": "sun"
            },
            {
                "date": "2015-06-21",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 13.9,
                "wind": 3.4,
                "weather": "sun"
            },
            {
                "date": "2015-06-22",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 12.8,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2015-06-23",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 11.7,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2015-06-24",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 16.1,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2015-06-25",
                "precipitation": 0.0,
                "temp_max": 30.6,
                "temp_min": 15.6,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2015-06-26",
                "precipitation": 0.0,
                "temp_max": 31.7,
                "temp_min": 17.8,
                "wind": 4.7,
                "weather": "sun"
            },
            {
                "date": "2015-06-27",
                "precipitation": 0.0,
                "temp_max": 33.3,
                "temp_min": 17.2,
                "wind": 3.9,
                "weather": "sun"
            },
            {
                "date": "2015-06-28",
                "precipitation": 0.3,
                "temp_max": 28.3,
                "temp_min": 18.3,
                "wind": 2.1,
                "weather": "rain"
            },
            {
                "date": "2015-06-29",
                "precipitation": 0.0,
                "temp_max": 28.9,
                "temp_min": 17.2,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2015-06-30",
                "precipitation": 0.0,
                "temp_max": 30.6,
                "temp_min": 15.0,
                "wind": 3.4,
                "weather": "fog"
            },
            {
                "date": "2015-07-01",
                "precipitation": 0.0,
                "temp_max": 32.2,
                "temp_min": 17.2,
                "wind": 4.3,
                "weather": "sun"
            },
            {
                "date": "2015-07-02",
                "precipitation": 0.0,
                "temp_max": 33.9,
                "temp_min": 17.8,
                "wind": 3.4,
                "weather": "sun"
            },
            {
                "date": "2015-07-03",
                "precipitation": 0.0,
                "temp_max": 33.3,
                "temp_min": 17.8,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2015-07-04",
                "precipitation": 0.0,
                "temp_max": 33.3,
                "temp_min": 15.0,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2015-07-05",
                "precipitation": 0.0,
                "temp_max": 32.8,
                "temp_min": 16.7,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2015-07-06",
                "precipitation": 0.0,
                "temp_max": 29.4,
                "temp_min": 15.6,
                "wind": 3.2,
                "weather": "drizzle"
            },
            {
                "date": "2015-07-07",
                "precipitation": 0.0,
                "temp_max": 27.2,
                "temp_min": 13.9,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2015-07-08",
                "precipitation": 0.0,
                "temp_max": 30.0,
                "temp_min": 14.4,
                "wind": 1.9,
                "weather": "drizzle"
            },
            {
                "date": "2015-07-09",
                "precipitation": 0.0,
                "temp_max": 28.9,
                "temp_min": 14.4,
                "wind": 3.4,
                "weather": "sun"
            },
            {
                "date": "2015-07-10",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 16.7,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2015-07-11",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 16.7,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2015-07-12",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 16.7,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2015-07-13",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 16.1,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2015-07-14",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 16.1,
                "wind": 3.3,
                "weather": "sun"
            },
            {
                "date": "2015-07-15",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 14.4,
                "wind": 3.2,
                "weather": "sun"
            },
            {
                "date": "2015-07-16",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 15.0,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2015-07-17",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 13.9,
                "wind": 3.3,
                "weather": "sun"
            },
            {
                "date": "2015-07-18",
                "precipitation": 0.0,
                "temp_max": 33.3,
                "temp_min": 17.8,
                "wind": 3.4,
                "weather": "sun"
            },
            {
                "date": "2015-07-19",
                "precipitation": 0.0,
                "temp_max": 35.0,
                "temp_min": 17.2,
                "wind": 3.3,
                "weather": "sun"
            },
            {
                "date": "2015-07-20",
                "precipitation": 0.0,
                "temp_max": 26.7,
                "temp_min": 16.7,
                "wind": 3.9,
                "weather": "sun"
            },
            {
                "date": "2015-07-21",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 15.0,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2015-07-22",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 13.9,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2015-07-23",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 14.4,
                "wind": 1.9,
                "weather": "sun"
            },
            {
                "date": "2015-07-24",
                "precipitation": 0.3,
                "temp_max": 22.8,
                "temp_min": 13.3,
                "wind": 3.8,
                "weather": "rain"
            },
            {
                "date": "2015-07-25",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 14.4,
                "wind": 2.4,
                "weather": "fog"
            },
            {
                "date": "2015-07-26",
                "precipitation": 2.0,
                "temp_max": 22.2,
                "temp_min": 13.9,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2015-07-27",
                "precipitation": 0.0,
                "temp_max": 23.3,
                "temp_min": 12.2,
                "wind": 1.9,
                "weather": "fog"
            },
            {
                "date": "2015-07-28",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 13.9,
                "wind": 3.4,
                "weather": "sun"
            },
            {
                "date": "2015-07-29",
                "precipitation": 0.0,
                "temp_max": 32.2,
                "temp_min": 14.4,
                "wind": 3.8,
                "weather": "sun"
            },
            {
                "date": "2015-07-30",
                "precipitation": 0.0,
                "temp_max": 34.4,
                "temp_min": 17.2,
                "wind": 3.5,
                "weather": "sun"
            },
            {
                "date": "2015-07-31",
                "precipitation": 0.0,
                "temp_max": 34.4,
                "temp_min": 17.8,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2015-08-01",
                "precipitation": 0.0,
                "temp_max": 33.3,
                "temp_min": 15.6,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2015-08-02",
                "precipitation": 0.0,
                "temp_max": 30.6,
                "temp_min": 16.1,
                "wind": 2.0,
                "weather": "sun"
            },
            {
                "date": "2015-08-03",
                "precipitation": 0.0,
                "temp_max": 28.3,
                "temp_min": 17.2,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2015-08-04",
                "precipitation": 0.0,
                "temp_max": 26.1,
                "temp_min": 14.4,
                "wind": 2.6,
                "weather": "fog"
            },
            {
                "date": "2015-08-05",
                "precipitation": 0.0,
                "temp_max": 23.3,
                "temp_min": 12.2,
                "wind": 3.5,
                "weather": "sun"
            },
            {
                "date": "2015-08-06",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 15.0,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2015-08-07",
                "precipitation": 0.0,
                "temp_max": 28.3,
                "temp_min": 15.6,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2015-08-08",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 15.6,
                "wind": 3.6,
                "weather": "fog"
            },
            {
                "date": "2015-08-09",
                "precipitation": 0.0,
                "temp_max": 28.3,
                "temp_min": 15.0,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2015-08-10",
                "precipitation": 0.0,
                "temp_max": 28.9,
                "temp_min": 16.1,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2015-08-11",
                "precipitation": 0.0,
                "temp_max": 30.0,
                "temp_min": 16.7,
                "wind": 4.4,
                "weather": "sun"
            },
            {
                "date": "2015-08-12",
                "precipitation": 7.6,
                "temp_max": 28.3,
                "temp_min": 16.7,
                "wind": 2.7,
                "weather": "rain"
            },
            {
                "date": "2015-08-13",
                "precipitation": 0.0,
                "temp_max": 28.3,
                "temp_min": 15.6,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2015-08-14",
                "precipitation": 30.5,
                "temp_max": 18.3,
                "temp_min": 15.0,
                "wind": 5.2,
                "weather": "rain"
            },
            {
                "date": "2015-08-15",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 13.9,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2015-08-16",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 14.4,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2015-08-17",
                "precipitation": 0.0,
                "temp_max": 27.2,
                "temp_min": 13.9,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2015-08-18",
                "precipitation": 0.0,
                "temp_max": 30.0,
                "temp_min": 15.0,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2015-08-19",
                "precipitation": 0.0,
                "temp_max": 31.7,
                "temp_min": 16.1,
                "wind": 2.1,
                "weather": "drizzle"
            },
            {
                "date": "2015-08-20",
                "precipitation": 2.0,
                "temp_max": 22.8,
                "temp_min": 14.4,
                "wind": 4.2,
                "weather": "rain"
            },
            {
                "date": "2015-08-21",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 14.4,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2015-08-22",
                "precipitation": 0.0,
                "temp_max": 26.7,
                "temp_min": 12.2,
                "wind": 2.5,
                "weather": "drizzle"
            },
            {
                "date": "2015-08-23",
                "precipitation": 0.0,
                "temp_max": 27.8,
                "temp_min": 13.9,
                "wind": 1.8,
                "weather": "drizzle"
            },
            {
                "date": "2015-08-24",
                "precipitation": 0.0,
                "temp_max": 23.9,
                "temp_min": 12.2,
                "wind": 2.3,
                "weather": "sun"
            },
            {
                "date": "2015-08-25",
                "precipitation": 0.0,
                "temp_max": 25.6,
                "temp_min": 12.2,
                "wind": 3.4,
                "weather": "sun"
            },
            {
                "date": "2015-08-26",
                "precipitation": 0.0,
                "temp_max": 28.3,
                "temp_min": 13.9,
                "wind": 1.7,
                "weather": "sun"
            },
            {
                "date": "2015-08-27",
                "precipitation": 0.0,
                "temp_max": 29.4,
                "temp_min": 14.4,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2015-08-28",
                "precipitation": 0.5,
                "temp_max": 23.3,
                "temp_min": 15.6,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2015-08-29",
                "precipitation": 32.5,
                "temp_max": 22.2,
                "temp_min": 13.3,
                "wind": 5.8,
                "weather": "rain"
            },
            {
                "date": "2015-08-30",
                "precipitation": 10.2,
                "temp_max": 20.0,
                "temp_min": 12.8,
                "wind": 4.7,
                "weather": "rain"
            },
            {
                "date": "2015-08-31",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 16.1,
                "wind": 5.8,
                "weather": "sun"
            },
            {
                "date": "2015-09-01",
                "precipitation": 5.8,
                "temp_max": 19.4,
                "temp_min": 13.9,
                "wind": 5.0,
                "weather": "rain"
            },
            {
                "date": "2015-09-02",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 11.1,
                "wind": 3.8,
                "weather": "sun"
            },
            {
                "date": "2015-09-03",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 10.6,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2015-09-04",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 10.0,
                "wind": 2.9,
                "weather": "sun"
            },
            {
                "date": "2015-09-05",
                "precipitation": 0.3,
                "temp_max": 20.6,
                "temp_min": 8.9,
                "wind": 3.5,
                "weather": "rain"
            },
            {
                "date": "2015-09-06",
                "precipitation": 5.3,
                "temp_max": 16.1,
                "temp_min": 11.7,
                "wind": 2.4,
                "weather": "rain"
            },
            {
                "date": "2015-09-07",
                "precipitation": 0.3,
                "temp_max": 21.1,
                "temp_min": 13.3,
                "wind": 1.5,
                "weather": "rain"
            },
            {
                "date": "2015-09-08",
                "precipitation": 0.0,
                "temp_max": 22.8,
                "temp_min": 13.3,
                "wind": 2.4,
                "weather": "sun"
            },
            {
                "date": "2015-09-09",
                "precipitation": 0.0,
                "temp_max": 24.4,
                "temp_min": 13.9,
                "wind": 3.3,
                "weather": "sun"
            },
            {
                "date": "2015-09-10",
                "precipitation": 0.0,
                "temp_max": 25.0,
                "temp_min": 14.4,
                "wind": 3.6,
                "weather": "fog"
            },
            {
                "date": "2015-09-11",
                "precipitation": 0.0,
                "temp_max": 27.2,
                "temp_min": 15.0,
                "wind": 3.1,
                "weather": "sun"
            },
            {
                "date": "2015-09-12",
                "precipitation": 0.0,
                "temp_max": 26.7,
                "temp_min": 14.4,
                "wind": 2.1,
                "weather": "sun"
            },
            {
                "date": "2015-09-13",
                "precipitation": 0.5,
                "temp_max": 20.6,
                "temp_min": 12.8,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2015-09-14",
                "precipitation": 0.0,
                "temp_max": 16.7,
                "temp_min": 10.6,
                "wind": 3.4,
                "weather": "sun"
            },
            {
                "date": "2015-09-15",
                "precipitation": 0.0,
                "temp_max": 17.8,
                "temp_min": 10.0,
                "wind": 2.8,
                "weather": "sun"
            },
            {
                "date": "2015-09-16",
                "precipitation": 1.0,
                "temp_max": 20.0,
                "temp_min": 10.0,
                "wind": 1.9,
                "weather": "rain"
            },
            {
                "date": "2015-09-17",
                "precipitation": 1.8,
                "temp_max": 18.3,
                "temp_min": 12.8,
                "wind": 3.8,
                "weather": "rain"
            },
            {
                "date": "2015-09-18",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 12.8,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2015-09-19",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 14.4,
                "wind": 4.3,
                "weather": "sun"
            },
            {
                "date": "2015-09-20",
                "precipitation": 4.1,
                "temp_max": 22.8,
                "temp_min": 12.2,
                "wind": 6.8,
                "weather": "rain"
            },
            {
                "date": "2015-09-21",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 9.4,
                "wind": 2.7,
                "weather": "fog"
            },
            {
                "date": "2015-09-22",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 7.8,
                "wind": 2.0,
                "weather": "sun"
            },
            {
                "date": "2015-09-23",
                "precipitation": 0.0,
                "temp_max": 20.6,
                "temp_min": 8.3,
                "wind": 1.8,
                "weather": "sun"
            },
            {
                "date": "2015-09-24",
                "precipitation": 0.0,
                "temp_max": 22.2,
                "temp_min": 11.1,
                "wind": 2.5,
                "weather": "fog"
            },
            {
                "date": "2015-09-25",
                "precipitation": 2.0,
                "temp_max": 15.6,
                "temp_min": 12.8,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2015-09-26",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 10.0,
                "wind": 2.7,
                "weather": "sun"
            },
            {
                "date": "2015-09-27",
                "precipitation": 0.0,
                "temp_max": 17.8,
                "temp_min": 7.2,
                "wind": 3.8,
                "weather": "sun"
            },
            {
                "date": "2015-09-28",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 9.4,
                "wind": 5.1,
                "weather": "sun"
            },
            {
                "date": "2015-09-29",
                "precipitation": 0.0,
                "temp_max": 21.7,
                "temp_min": 8.9,
                "wind": 1.9,
                "weather": "sun"
            },
            {
                "date": "2015-09-30",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 10.0,
                "wind": 1.3,
                "weather": "fog"
            },
            {
                "date": "2015-10-01",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 9.4,
                "wind": 1.3,
                "weather": "fog"
            },
            {
                "date": "2015-10-02",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 10.0,
                "wind": 2.9,
                "weather": "fog"
            },
            {
                "date": "2015-10-03",
                "precipitation": 0.0,
                "temp_max": 19.4,
                "temp_min": 11.1,
                "wind": 4.8,
                "weather": "sun"
            },
            {
                "date": "2015-10-04",
                "precipitation": 0.0,
                "temp_max": 22.8,
                "temp_min": 10.0,
                "wind": 3.7,
                "weather": "sun"
            },
            {
                "date": "2015-10-05",
                "precipitation": 0.0,
                "temp_max": 23.3,
                "temp_min": 9.4,
                "wind": 1.6,
                "weather": "sun"
            },
            {
                "date": "2015-10-06",
                "precipitation": 0.0,
                "temp_max": 18.3,
                "temp_min": 10.0,
                "wind": 2.6,
                "weather": "drizzle"
            },
            {
                "date": "2015-10-07",
                "precipitation": 9.9,
                "temp_max": 16.1,
                "temp_min": 13.9,
                "wind": 2.2,
                "weather": "rain"
            },
            {
                "date": "2015-10-08",
                "precipitation": 0.0,
                "temp_max": 18.9,
                "temp_min": 13.3,
                "wind": 1.1,
                "weather": "fog"
            },
            {
                "date": "2015-10-09",
                "precipitation": 0.3,
                "temp_max": 19.4,
                "temp_min": 12.2,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2015-10-10",
                "precipitation": 28.7,
                "temp_max": 21.1,
                "temp_min": 13.3,
                "wind": 4.7,
                "weather": "rain"
            },
            {
                "date": "2015-10-11",
                "precipitation": 0.0,
                "temp_max": 17.8,
                "temp_min": 10.6,
                "wind": 2.6,
                "weather": "sun"
            },
            {
                "date": "2015-10-12",
                "precipitation": 4.6,
                "temp_max": 18.3,
                "temp_min": 10.6,
                "wind": 2.8,
                "weather": "rain"
            },
            {
                "date": "2015-10-13",
                "precipitation": 1.3,
                "temp_max": 16.7,
                "temp_min": 9.4,
                "wind": 3.2,
                "weather": "rain"
            },
            {
                "date": "2015-10-14",
                "precipitation": 0.0,
                "temp_max": 15.0,
                "temp_min": 10.0,
                "wind": 5.0,
                "weather": "fog"
            },
            {
                "date": "2015-10-15",
                "precipitation": 0.0,
                "temp_max": 21.1,
                "temp_min": 9.4,
                "wind": 3.4,
                "weather": "fog"
            },
            {
                "date": "2015-10-16",
                "precipitation": 0.0,
                "temp_max": 20.0,
                "temp_min": 8.9,
                "wind": 1.3,
                "weather": "sun"
            },
            {
                "date": "2015-10-17",
                "precipitation": 0.3,
                "temp_max": 19.4,
                "temp_min": 11.7,
                "wind": 1.3,
                "weather": "rain"
            },
            {
                "date": "2015-10-18",
                "precipitation": 3.8,
                "temp_max": 15.0,
                "temp_min": 12.8,
                "wind": 2.0,
                "weather": "rain"
            },
            {
                "date": "2015-10-19",
                "precipitation": 0.3,
                "temp_max": 17.2,
                "temp_min": 12.2,
                "wind": 2.6,
                "weather": "rain"
            },
            {
                "date": "2015-10-20",
                "precipitation": 0.0,
                "temp_max": 17.8,
                "temp_min": 10.6,
                "wind": 1.8,
                "weather": "fog"
            },
            {
                "date": "2015-10-21",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 8.3,
                "wind": 1.3,
                "weather": "fog"
            },
            {
                "date": "2015-10-22",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 8.9,
                "wind": 2.7,
                "weather": "fog"
            },
            {
                "date": "2015-10-23",
                "precipitation": 0.0,
                "temp_max": 12.8,
                "temp_min": 7.2,
                "wind": 2.6,
                "weather": "fog"
            },
            {
                "date": "2015-10-24",
                "precipitation": 0.0,
                "temp_max": 15.0,
                "temp_min": 8.9,
                "wind": 2.9,
                "weather": "fog"
            },
            {
                "date": "2015-10-25",
                "precipitation": 8.9,
                "temp_max": 19.4,
                "temp_min": 8.9,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2015-10-26",
                "precipitation": 6.9,
                "temp_max": 12.2,
                "temp_min": 10.0,
                "wind": 4.6,
                "weather": "rain"
            },
            {
                "date": "2015-10-27",
                "precipitation": 0.0,
                "temp_max": 16.1,
                "temp_min": 7.8,
                "wind": 1.7,
                "weather": "fog"
            },
            {
                "date": "2015-10-28",
                "precipitation": 3.3,
                "temp_max": 13.9,
                "temp_min": 11.1,
                "wind": 2.8,
                "weather": "rain"
            },
            {
                "date": "2015-10-29",
                "precipitation": 1.8,
                "temp_max": 15.0,
                "temp_min": 12.2,
                "wind": 4.7,
                "weather": "rain"
            },
            {
                "date": "2015-10-30",
                "precipitation": 19.3,
                "temp_max": 17.2,
                "temp_min": 11.7,
                "wind": 6.7,
                "weather": "rain"
            },
            {
                "date": "2015-10-31",
                "precipitation": 33.0,
                "temp_max": 15.6,
                "temp_min": 11.7,
                "wind": 7.2,
                "weather": "rain"
            },
            {
                "date": "2015-11-01",
                "precipitation": 26.2,
                "temp_max": 12.2,
                "temp_min": 8.9,
                "wind": 6.0,
                "weather": "rain"
            },
            {
                "date": "2015-11-02",
                "precipitation": 0.3,
                "temp_max": 11.1,
                "temp_min": 7.2,
                "wind": 2.8,
                "weather": "rain"
            },
            {
                "date": "2015-11-03",
                "precipitation": 0.8,
                "temp_max": 10.6,
                "temp_min": 5.0,
                "wind": 1.4,
                "weather": "rain"
            },
            {
                "date": "2015-11-04",
                "precipitation": 0.0,
                "temp_max": 10.0,
                "temp_min": 3.3,
                "wind": 2.2,
                "weather": "sun"
            },
            {
                "date": "2015-11-05",
                "precipitation": 1.3,
                "temp_max": 11.7,
                "temp_min": 7.8,
                "wind": 2.3,
                "weather": "rain"
            },
            {
                "date": "2015-11-06",
                "precipitation": 0.0,
                "temp_max": 15.6,
                "temp_min": 8.3,
                "wind": 2.7,
                "weather": "fog"
            },
            {
                "date": "2015-11-07",
                "precipitation": 12.7,
                "temp_max": 12.2,
                "temp_min": 9.4,
                "wind": 3.0,
                "weather": "rain"
            },
            {
                "date": "2015-11-08",
                "precipitation": 6.6,
                "temp_max": 11.1,
                "temp_min": 7.8,
                "wind": 1.8,
                "weather": "rain"
            },
            {
                "date": "2015-11-09",
                "precipitation": 3.3,
                "temp_max": 10.0,
                "temp_min": 5.0,
                "wind": 1.3,
                "weather": "rain"
            },
            {
                "date": "2015-11-10",
                "precipitation": 1.3,
                "temp_max": 11.1,
                "temp_min": 3.9,
                "wind": 3.9,
                "weather": "rain"
            },
            {
                "date": "2015-11-11",
                "precipitation": 1.5,
                "temp_max": 11.1,
                "temp_min": 6.1,
                "wind": 4.6,
                "weather": "rain"
            },
            {
                "date": "2015-11-12",
                "precipitation": 9.9,
                "temp_max": 11.1,
                "temp_min": 5.0,
                "wind": 5.1,
                "weather": "rain"
            },
            {
                "date": "2015-11-13",
                "precipitation": 33.5,
                "temp_max": 13.3,
                "temp_min": 9.4,
                "wind": 6.5,
                "weather": "rain"
            },
            {
                "date": "2015-11-14",
                "precipitation": 47.2,
                "temp_max": 9.4,
                "temp_min": 6.1,
                "wind": 4.5,
                "weather": "rain"
            },
            {
                "date": "2015-11-15",
                "precipitation": 22.4,
                "temp_max": 8.9,
                "temp_min": 2.2,
                "wind": 4.1,
                "weather": "rain"
            },
            {
                "date": "2015-11-16",
                "precipitation": 2.0,
                "temp_max": 8.9,
                "temp_min": 1.7,
                "wind": 4.0,
                "weather": "rain"
            },
            {
                "date": "2015-11-17",
                "precipitation": 29.5,
                "temp_max": 13.3,
                "temp_min": 6.7,
                "wind": 8.0,
                "weather": "rain"
            },
            {
                "date": "2015-11-18",
                "precipitation": 1.5,
                "temp_max": 8.9,
                "temp_min": 3.3,
                "wind": 3.8,
                "weather": "rain"
            },
            {
                "date": "2015-11-19",
                "precipitation": 2.0,
                "temp_max": 8.9,
                "temp_min": 2.8,
                "wind": 4.2,
                "weather": "rain"
            },
            {
                "date": "2015-11-20",
                "precipitation": 0.0,
                "temp_max": 8.3,
                "temp_min": 0.6,
                "wind": 4.0,
                "weather": "fog"
            },
            {
                "date": "2015-11-21",
                "precipitation": 0.0,
                "temp_max": 8.9,
                "temp_min": 0.6,
                "wind": 4.7,
                "weather": "sun"
            },
            {
                "date": "2015-11-22",
                "precipitation": 0.0,
                "temp_max": 10.0,
                "temp_min": 1.7,
                "wind": 3.1,
                "weather": "fog"
            },
            {
                "date": "2015-11-23",
                "precipitation": 3.0,
                "temp_max": 6.7,
                "temp_min": 0.0,
                "wind": 1.3,
                "weather": "rain"
            },
            {
                "date": "2015-11-24",
                "precipitation": 7.1,
                "temp_max": 6.7,
                "temp_min": 2.8,
                "wind": 4.5,
                "weather": "rain"
            },
            {
                "date": "2015-11-25",
                "precipitation": 0.0,
                "temp_max": 7.2,
                "temp_min": 0.0,
                "wind": 5.7,
                "weather": "sun"
            },
            {
                "date": "2015-11-26",
                "precipitation": 0.0,
                "temp_max": 9.4,
                "temp_min": -1.0,
                "wind": 4.3,
                "weather": "sun"
            },
            {
                "date": "2015-11-27",
                "precipitation": 0.0,
                "temp_max": 9.4,
                "temp_min": -1.6,
                "wind": 3.0,
                "weather": "sun"
            },
            {
                "date": "2015-11-28",
                "precipitation": 0.0,
                "temp_max": 7.2,
                "temp_min": -2.7,
                "wind": 1.0,
                "weather": "sun"
            },
            {
                "date": "2015-11-29",
                "precipitation": 0.0,
                "temp_max": 1.7,
                "temp_min": -2.1,
                "wind": 0.9,
                "weather": "fog"
            },
            {
                "date": "2015-11-30",
                "precipitation": 0.5,
                "temp_max": 5.6,
                "temp_min": -3.8,
                "wind": 1.7,
                "weather": "rain"
            },
            {
                "date": "2015-12-01",
                "precipitation": 12.2,
                "temp_max": 10.0,
                "temp_min": 3.9,
                "wind": 3.5,
                "weather": "rain"
            },
            {
                "date": "2015-12-02",
                "precipitation": 2.5,
                "temp_max": 10.6,
                "temp_min": 4.4,
                "wind": 5.0,
                "weather": "rain"
            },
            {
                "date": "2015-12-03",
                "precipitation": 12.7,
                "temp_max": 15.6,
                "temp_min": 7.8,
                "wind": 5.9,
                "weather": "rain"
            },
            {
                "date": "2015-12-04",
                "precipitation": 2.0,
                "temp_max": 10.6,
                "temp_min": 6.1,
                "wind": 4.7,
                "weather": "rain"
            },
            {
                "date": "2015-12-05",
                "precipitation": 15.7,
                "temp_max": 10.0,
                "temp_min": 6.1,
                "wind": 4.0,
                "weather": "rain"
            },
            {
                "date": "2015-12-06",
                "precipitation": 11.2,
                "temp_max": 12.8,
                "temp_min": 7.2,
                "wind": 5.9,
                "weather": "rain"
            },
            {
                "date": "2015-12-07",
                "precipitation": 27.4,
                "temp_max": 11.1,
                "temp_min": 8.3,
                "wind": 3.4,
                "weather": "rain"
            },
            {
                "date": "2015-12-08",
                "precipitation": 54.1,
                "temp_max": 15.6,
                "temp_min": 10.0,
                "wind": 6.2,
                "weather": "rain"
            },
            {
                "date": "2015-12-09",
                "precipitation": 13.5,
                "temp_max": 12.2,
                "temp_min": 7.8,
                "wind": 6.3,
                "weather": "rain"
            },
            {
                "date": "2015-12-10",
                "precipitation": 9.4,
                "temp_max": 11.7,
                "temp_min": 6.1,
                "wind": 7.5,
                "weather": "rain"
            },
            {
                "date": "2015-12-11",
                "precipitation": 0.3,
                "temp_max": 9.4,
                "temp_min": 4.4,
                "wind": 2.8,
                "weather": "rain"
            },
            {
                "date": "2015-12-12",
                "precipitation": 16.0,
                "temp_max": 8.9,
                "temp_min": 5.6,
                "wind": 5.6,
                "weather": "rain"
            },
            {
                "date": "2015-12-13",
                "precipitation": 1.3,
                "temp_max": 7.8,
                "temp_min": 6.1,
                "wind": 6.1,
                "weather": "rain"
            },
            {
                "date": "2015-12-14",
                "precipitation": 0.0,
                "temp_max": 7.8,
                "temp_min": 1.7,
                "wind": 1.7,
                "weather": "sun"
            },
            {
                "date": "2015-12-15",
                "precipitation": 1.5,
                "temp_max": 6.7,
                "temp_min": 1.1,
                "wind": 2.9,
                "weather": "rain"
            },
            {
                "date": "2015-12-16",
                "precipitation": 3.6,
                "temp_max": 6.1,
                "temp_min": 2.8,
                "wind": 2.3,
                "weather": "rain"
            },
            {
                "date": "2015-12-17",
                "precipitation": 21.8,
                "temp_max": 6.7,
                "temp_min": 3.9,
                "wind": 6.0,
                "weather": "rain"
            },
            {
                "date": "2015-12-18",
                "precipitation": 18.5,
                "temp_max": 8.9,
                "temp_min": 4.4,
                "wind": 5.1,
                "weather": "rain"
            },
            {
                "date": "2015-12-19",
                "precipitation": 0.0,
                "temp_max": 8.3,
                "temp_min": 2.8,
                "wind": 4.1,
                "weather": "fog"
            },
            {
                "date": "2015-12-20",
                "precipitation": 4.3,
                "temp_max": 7.8,
                "temp_min": 4.4,
                "wind": 6.7,
                "weather": "rain"
            },
            {
                "date": "2015-12-21",
                "precipitation": 27.4,
                "temp_max": 5.6,
                "temp_min": 2.8,
                "wind": 4.3,
                "weather": "rain"
            },
            {
                "date": "2015-12-22",
                "precipitation": 4.6,
                "temp_max": 7.8,
                "temp_min": 2.8,
                "wind": 5.0,
                "weather": "rain"
            },
            {
                "date": "2015-12-23",
                "precipitation": 6.1,
                "temp_max": 5.0,
                "temp_min": 2.8,
                "wind": 7.6,
                "weather": "rain"
            },
            {
                "date": "2015-12-24",
                "precipitation": 2.5,
                "temp_max": 5.6,
                "temp_min": 2.2,
                "wind": 4.3,
                "weather": "rain"
            },
            {
                "date": "2015-12-25",
                "precipitation": 5.8,
                "temp_max": 5.0,
                "temp_min": 2.2,
                "wind": 1.5,
                "weather": "rain"
            },
            {
                "date": "2015-12-26",
                "precipitation": 0.0,
                "temp_max": 4.4,
                "temp_min": 0.0,
                "wind": 2.5,
                "weather": "sun"
            },
            {
                "date": "2015-12-27",
                "precipitation": 8.6,
                "temp_max": 4.4,
                "temp_min": 1.7,
                "wind": 2.9,
                "weather": "rain"
            },
            {
                "date": "2015-12-28",
                "precipitation": 1.5,
                "temp_max": 5.0,
                "temp_min": 1.7,
                "wind": 1.3,
                "weather": "rain"
            },
            {
                "date": "2015-12-29",
                "precipitation": 0.0,
                "temp_max": 7.2,
                "temp_min": 0.6,
                "wind": 2.6,
                "weather": "fog"
            },
            {
                "date": "2015-12-30",
                "precipitation": 0.0,
                "temp_max": 5.6,
                "temp_min": -1.0,
                "wind": 3.4,
                "weather": "sun"
            },
            {
                "date": "2015-12-31",
                "precipitation": 0.0,
                "temp_max": 5.6,
                "temp_min": -2.1,
                "wind": 3.5,
                "weather": "sun"
            }
        ]
    """.trimIndent()
}