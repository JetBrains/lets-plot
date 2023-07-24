/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.data

object AutoMpg {
    data class Column<T>(val name: String, val data: List<T>)

    private val sequence: List<List<String>>
        get() = demo.plot.common.data.AutoMpg.data.map { it.split(',') }

    private fun int(column: Int): List<Int> {
        return demo.plot.common.data.AutoMpg.sequence.map { it.get(column).toInt() }
    }

    private fun double(column: Int): List<Double> {
        return demo.plot.common.data.AutoMpg.sequence.map { it.get(column).toDouble() }
    }

    private fun string(column: Int): List<String> {
        return demo.plot.common.data.AutoMpg.sequence.map { it.get(column).toString() }
    }

    val mpg get() = demo.plot.common.data.AutoMpg.Column(
        "miles per gallon",
        demo.plot.common.data.AutoMpg.double(0)
    )
    val cylinders get() = demo.plot.common.data.AutoMpg.Column(
        "number of cylinders",
        demo.plot.common.data.AutoMpg.int(1)
    )
    val displacement get() = demo.plot.common.data.AutoMpg.Column(
        "engine displacement (cu. inches)",
        demo.plot.common.data.AutoMpg.double(2)
    )
    val horsepower get() = demo.plot.common.data.AutoMpg.Column(
        "engine horsepower",
        demo.plot.common.data.AutoMpg.int(3)
    )
    val weight get() = demo.plot.common.data.AutoMpg.Column(
        "vehicle weight (lbs.)",
        demo.plot.common.data.AutoMpg.double(4)
    )
    val acceleration get() = demo.plot.common.data.AutoMpg.Column(
        "time to accelerate (sec.)",
        demo.plot.common.data.AutoMpg.double(5)
    )
    val modelYear get() = demo.plot.common.data.AutoMpg.Column(
        "model year",
        demo.plot.common.data.AutoMpg.int(6)
    )
    val origin get() = demo.plot.common.data.AutoMpg.Column(
        "origin of car",
        demo.plot.common.data.AutoMpg.string(7)
    )
    val vehicleName get() = demo.plot.common.data.AutoMpg.Column(
        "vehicle name",
        demo.plot.common.data.AutoMpg.string(8)
    )
    val df get() = run {
        mapOf(
            demo.plot.common.data.AutoMpg.mpg.name to demo.plot.common.data.AutoMpg.mpg.data,
            demo.plot.common.data.AutoMpg.cylinders.name to demo.plot.common.data.AutoMpg.cylinders.data,
            demo.plot.common.data.AutoMpg.displacement.name to demo.plot.common.data.AutoMpg.displacement.data,
            demo.plot.common.data.AutoMpg.horsepower.name to demo.plot.common.data.AutoMpg.horsepower.data,
            demo.plot.common.data.AutoMpg.weight.name to demo.plot.common.data.AutoMpg.weight.data,
            demo.plot.common.data.AutoMpg.acceleration.name to demo.plot.common.data.AutoMpg.acceleration.data,
            demo.plot.common.data.AutoMpg.modelYear.name to demo.plot.common.data.AutoMpg.modelYear.data,
            demo.plot.common.data.AutoMpg.origin.name to demo.plot.common.data.AutoMpg.origin.data,
            demo.plot.common.data.AutoMpg.vehicleName.name to demo.plot.common.data.AutoMpg.vehicleName.data
        )
    }

    val data =
"""18.0,8,307.0,130,3504,12.0,70,US,chevrolet chevelle malibu
15.0,8,350.0,165,3693,11.5,70,US,buick skylark 320
18.0,8,318.0,150,3436,11.0,70,US,plymouth satellite
16.0,8,304.0,150,3433,12.0,70,US,amc rebel sst
17.0,8,302.0,140,3449,10.5,70,US,ford torino
15.0,8,429.0,198,4341,10.0,70,US,ford galaxie 500
14.0,8,454.0,220,4354,9.0,70,US,chevrolet impala
14.0,8,440.0,215,4312,8.5,70,US,plymouth fury iii
14.0,8,455.0,225,4425,10.0,70,US,pontiac catalina
15.0,8,390.0,190,3850,8.5,70,US,amc ambassador dpl
15.0,8,383.0,170,3563,10.0,70,US,dodge challenger se
14.0,8,340.0,160,3609,8.0,70,US,plymouth 'cuda 340
15.0,8,400.0,150,3761,9.5,70,US,chevrolet monte carlo
14.0,8,455.0,225,3086,10.0,70,US,buick estate wagon (sw)
24.0,4,113.0,95,2372,15.0,70,Asia,toyota corona mark ii
22.0,6,198.0,95,2833,15.5,70,US,plymouth duster
18.0,6,199.0,97,2774,15.5,70,US,amc hornet
21.0,6,200.0,85,2587,16.0,70,US,ford maverick
27.0,4,97.0,88,2130,14.5,70,Asia,datsun pl510
26.0,4,97.0,46,1835,20.5,70,Europe,volkswagen 1131 deluxe sedan
25.0,4,110.0,87,2672,17.5,70,Europe,peugeot 504
24.0,4,107.0,90,2430,14.5,70,Europe,audi 100 ls
25.0,4,104.0,95,2375,17.5,70,Europe,saab 99e
26.0,4,121.0,113,2234,12.5,70,Europe,bmw 2002
21.0,6,199.0,90,2648,15.0,70,US,amc gremlin
10.0,8,360.0,215,4615,14.0,70,US,ford f250
10.0,8,307.0,200,4376,15.0,70,US,chevy c20
11.0,8,318.0,210,4382,13.5,70,US,dodge d200
9.0,8,304.0,193,4732,18.5,70,US,hi 1200d
27.0,4,97.0,88,2130,14.5,71,Asia,datsun pl510
28.0,4,140.0,90,2264,15.5,71,US,chevrolet vega 2300
25.0,4,113.0,95,2228,14.0,71,Asia,toyota corona
19.0,6,232.0,100,2634,13.0,71,US,amc gremlin
16.0,6,225.0,105,3439,15.5,71,US,plymouth satellite custom
17.0,6,250.0,100,3329,15.5,71,US,chevrolet chevelle malibu
19.0,6,250.0,88,3302,15.5,71,US,ford torino 500
18.0,6,232.0,100,3288,15.5,71,US,amc matador
14.0,8,350.0,165,4209,12.0,71,US,chevrolet impala
14.0,8,400.0,175,4464,11.5,71,US,pontiac catalina brougham
14.0,8,351.0,153,4154,13.5,71,US,ford galaxie 500
14.0,8,318.0,150,4096,13.0,71,US,plymouth fury iii
12.0,8,383.0,180,4955,11.5,71,US,dodge monaco (sw)
13.0,8,400.0,170,4746,12.0,71,US,ford country squire (sw)
13.0,8,400.0,175,5140,12.0,71,US,pontiac safari (sw)
18.0,6,258.0,110,2962,13.5,71,US,amc hornet sportabout (sw)
22.0,4,140.0,72,2408,19.0,71,US,chevrolet vega (sw)
19.0,6,250.0,100,3282,15.0,71,US,pontiac firebird
18.0,6,250.0,88,3139,14.5,71,US,ford mustang
23.0,4,122.0,86,2220,14.0,71,US,mercury capri 2000
28.0,4,116.0,90,2123,14.0,71,Europe,opel 1900
30.0,4,79.0,70,2074,19.5,71,Europe,peugeot 304
30.0,4,88.0,76,2065,14.5,71,Europe,fiat 124b
31.0,4,71.0,65,1773,19.0,71,Asia,toyota corolla 1200
35.0,4,72.0,69,1613,18.0,71,Asia,datsun 1200
27.0,4,97.0,60,1834,19.0,71,Europe,volkswagen model 111
26.0,4,91.0,70,1955,20.5,71,US,plymouth cricket
24.0,4,113.0,95,2278,15.5,72,Asia,toyota corona hardtop
25.0,4,97.5,80,2126,17.0,72,US,dodge colt hardtop
23.0,4,97.0,54,2254,23.5,72,Europe,volkswagen type 3
20.0,4,140.0,90,2408,19.5,72,US,chevrolet vega
21.0,4,122.0,86,2226,16.5,72,US,ford pinto runabout
13.0,8,350.0,165,4274,12.0,72,US,chevrolet impala
14.0,8,400.0,175,4385,12.0,72,US,pontiac catalina
15.0,8,318.0,150,4135,13.5,72,US,plymouth fury iii
14.0,8,351.0,153,4129,13.0,72,US,ford galaxie 500
17.0,8,304.0,150,3672,11.5,72,US,amc ambassador sst
11.0,8,429.0,208,4633,11.0,72,US,mercury marquis
13.0,8,350.0,155,4502,13.5,72,US,buick lesabre custom
12.0,8,350.0,160,4456,13.5,72,US,oldsmobile delta 88 royale
13.0,8,400.0,190,4422,12.5,72,US,chrysler newport royal
19.0,3,70.0,97,2330,13.5,72,Asia,mazda rx2 coupe
15.0,8,304.0,150,3892,12.5,72,US,amc matador (sw)
13.0,8,307.0,130,4098,14.0,72,US,chevrolet chevelle concours (sw)
13.0,8,302.0,140,4294,16.0,72,US,ford gran torino (sw)
14.0,8,318.0,150,4077,14.0,72,US,plymouth satellite custom (sw)
18.0,4,121.0,112,2933,14.5,72,Europe,volvo 145e (sw)
22.0,4,121.0,76,2511,18.0,72,Europe,volkswagen 411 (sw)
21.0,4,120.0,87,2979,19.5,72,Europe,peugeot 504 (sw)
26.0,4,96.0,69,2189,18.0,72,Europe,renault 12 (sw)
22.0,4,122.0,86,2395,16.0,72,US,ford pinto (sw)
28.0,4,97.0,92,2288,17.0,72,Asia,datsun 510 (sw)
23.0,4,120.0,97,2506,14.5,72,Asia,toyouta corona mark ii (sw)
28.0,4,98.0,80,2164,15.0,72,US,dodge colt (sw)
27.0,4,97.0,88,2100,16.5,72,Asia,toyota corolla 1600 (sw)
13.0,8,350.0,175,4100,13.0,73,US,buick century 350
14.0,8,304.0,150,3672,11.5,73,US,amc matador
13.0,8,350.0,145,3988,13.0,73,US,chevrolet malibu
14.0,8,302.0,137,4042,14.5,73,US,ford gran torino
15.0,8,318.0,150,3777,12.5,73,US,dodge coronet custom
12.0,8,429.0,198,4952,11.5,73,US,mercury marquis brougham
13.0,8,400.0,150,4464,12.0,73,US,chevrolet caprice classic
13.0,8,351.0,158,4363,13.0,73,US,ford ltd
14.0,8,318.0,150,4237,14.5,73,US,plymouth fury gran sedan
13.0,8,440.0,215,4735,11.0,73,US,chrysler new yorker brougham
12.0,8,455.0,225,4951,11.0,73,US,buick electra 225 custom
13.0,8,360.0,175,3821,11.0,73,US,amc ambassador brougham
18.0,6,225.0,105,3121,16.5,73,US,plymouth valiant
16.0,6,250.0,100,3278,18.0,73,US,chevrolet nova custom
18.0,6,232.0,100,2945,16.0,73,US,amc hornet
18.0,6,250.0,88,3021,16.5,73,US,ford maverick
23.0,6,198.0,95,2904,16.0,73,US,plymouth duster
26.0,4,97.0,46,1950,21.0,73,Europe,volkswagen super beetle
11.0,8,400.0,150,4997,14.0,73,US,chevrolet impala
12.0,8,400.0,167,4906,12.5,73,US,ford country
13.0,8,360.0,170,4654,13.0,73,US,plymouth custom suburb
12.0,8,350.0,180,4499,12.5,73,US,oldsmobile vista cruiser
18.0,6,232.0,100,2789,15.0,73,US,amc gremlin
20.0,4,97.0,88,2279,19.0,73,Asia,toyota carina
21.0,4,140.0,72,2401,19.5,73,US,chevrolet vega
22.0,4,108.0,94,2379,16.5,73,Asia,datsun 610
18.0,3,70.0,90,2124,13.5,73,Asia,maxda rx3
19.0,4,122.0,85,2310,18.5,73,US,ford pinto
21.0,6,155.0,107,2472,14.0,73,US,mercury capri v6
26.0,4,98.0,90,2265,15.5,73,Europe,fiat 124 sport coupe
15.0,8,350.0,145,4082,13.0,73,US,chevrolet monte carlo s
16.0,8,400.0,230,4278,9.5,73,US,pontiac grand prix
29.0,4,68.0,49,1867,19.5,73,Europe,fiat 128
24.0,4,116.0,75,2158,15.5,73,Europe,opel manta
20.0,4,114.0,91,2582,14.0,73,Europe,audi 100ls
19.0,4,121.0,112,2868,15.5,73,Europe,volvo 144ea
15.0,8,318.0,150,3399,11.0,73,US,dodge dart custom
24.0,4,121.0,110,2660,14.0,73,Europe,saab 99le
20.0,6,156.0,122,2807,13.5,73,Asia,toyota mark ii
11.0,8,350.0,180,3664,11.0,73,US,oldsmobile omega
20.0,6,198.0,95,3102,16.5,74,US,plymouth duster
19.0,6,232.0,100,2901,16.0,74,US,amc hornet
15.0,6,250.0,100,3336,17.0,74,US,chevrolet nova
31.0,4,79.0,67,1950,19.0,74,Asia,datsun b210
26.0,4,122.0,80,2451,16.5,74,US,ford pinto
32.0,4,71.0,65,1836,21.0,74,Asia,toyota corolla 1200
25.0,4,140.0,75,2542,17.0,74,US,chevrolet vega
16.0,6,250.0,100,3781,17.0,74,US,chevrolet chevelle malibu classic
16.0,6,258.0,110,3632,18.0,74,US,amc matador
18.0,6,225.0,105,3613,16.5,74,US,plymouth satellite sebring
16.0,8,302.0,140,4141,14.0,74,US,ford gran torino
13.0,8,350.0,150,4699,14.5,74,US,buick century luxus (sw)
14.0,8,318.0,150,4457,13.5,74,US,dodge coronet custom (sw)
14.0,8,302.0,140,4638,16.0,74,US,ford gran torino (sw)
14.0,8,304.0,150,4257,15.5,74,US,amc matador (sw)
29.0,4,98.0,83,2219,16.5,74,Europe,audi fox
26.0,4,79.0,67,1963,15.5,74,Europe,volkswagen dasher
26.0,4,97.0,78,2300,14.5,74,Europe,opel manta
31.0,4,76.0,52,1649,16.5,74,Asia,toyota corona
32.0,4,83.0,61,2003,19.0,74,Asia,datsun 710
28.0,4,90.0,75,2125,14.5,74,US,dodge colt
24.0,4,90.0,75,2108,15.5,74,Europe,fiat 128
26.0,4,116.0,75,2246,14.0,74,Europe,fiat 124 tc
24.0,4,120.0,97,2489,15.0,74,Asia,honda civic
26.0,4,108.0,93,2391,15.5,74,Asia,subaru
31.0,4,79.0,67,2000,16.0,74,Europe,fiat x1.9
19.0,6,225.0,95,3264,16.0,75,US,plymouth valiant custom
18.0,6,250.0,105,3459,16.0,75,US,chevrolet nova
15.0,6,250.0,72,3432,21.0,75,US,mercury monarch
15.0,6,250.0,72,3158,19.5,75,US,ford maverick
16.0,8,400.0,170,4668,11.5,75,US,pontiac catalina
15.0,8,350.0,145,4440,14.0,75,US,chevrolet bel air
16.0,8,318.0,150,4498,14.5,75,US,plymouth grand fury
14.0,8,351.0,148,4657,13.5,75,US,ford ltd
17.0,6,231.0,110,3907,21.0,75,US,buick century
16.0,6,250.0,105,3897,18.5,75,US,chevroelt chevelle malibu
15.0,6,258.0,110,3730,19.0,75,US,amc matador
18.0,6,225.0,95,3785,19.0,75,US,plymouth fury
21.0,6,231.0,110,3039,15.0,75,US,buick skyhawk
20.0,8,262.0,110,3221,13.5,75,US,chevrolet monza 2+2
13.0,8,302.0,129,3169,12.0,75,US,ford mustang ii
29.0,4,97.0,75,2171,16.0,75,Asia,toyota corolla
23.0,4,140.0,83,2639,17.0,75,US,ford pinto
20.0,6,232.0,100,2914,16.0,75,US,amc gremlin
23.0,4,140.0,78,2592,18.5,75,US,pontiac astro
24.0,4,134.0,96,2702,13.5,75,Asia,toyota corona
25.0,4,90.0,71,2223,16.5,75,Europe,volkswagen dasher
24.0,4,119.0,97,2545,17.0,75,Asia,datsun 710
18.0,6,171.0,97,2984,14.5,75,US,ford pinto
29.0,4,90.0,70,1937,14.0,75,Europe,volkswagen rabbit
19.0,6,232.0,90,3211,17.0,75,US,amc pacer
23.0,4,115.0,95,2694,15.0,75,Europe,audi 100ls
23.0,4,120.0,88,2957,17.0,75,Europe,peugeot 504
22.0,4,121.0,98,2945,14.5,75,Europe,volvo 244dl
25.0,4,121.0,115,2671,13.5,75,Europe,saab 99le
33.0,4,91.0,53,1795,17.5,75,Asia,honda civic cvcc
28.0,4,107.0,86,2464,15.5,76,Europe,fiat 131
25.0,4,116.0,81,2220,16.9,76,Europe,opel 1900
25.0,4,140.0,92,2572,14.9,76,US,capri ii
26.0,4,98.0,79,2255,17.7,76,US,dodge colt
27.0,4,101.0,83,2202,15.3,76,Europe,renault 12tl
17.5,8,305.0,140,4215,13.0,76,US,chevrolet chevelle malibu classic
16.0,8,318.0,150,4190,13.0,76,US,dodge coronet brougham
15.5,8,304.0,120,3962,13.9,76,US,amc matador
14.5,8,351.0,152,4215,12.8,76,US,ford gran torino
22.0,6,225.0,100,3233,15.4,76,US,plymouth valiant
22.0,6,250.0,105,3353,14.5,76,US,chevrolet nova
24.0,6,200.0,81,3012,17.6,76,US,ford maverick
22.5,6,232.0,90,3085,17.6,76,US,amc hornet
29.0,4,85.0,52,2035,22.2,76,US,chevrolet chevette
24.5,4,98.0,60,2164,22.1,76,US,chevrolet woody
29.0,4,90.0,70,1937,14.2,76,Europe,vw rabbit
33.0,4,91.0,53,1795,17.4,76,Asia,honda civic
20.0,6,225.0,100,3651,17.7,76,US,dodge aspen se
18.0,6,250.0,78,3574,21.0,76,US,ford granada ghia
18.5,6,250.0,110,3645,16.2,76,US,pontiac ventura sj
17.5,6,258.0,95,3193,17.8,76,US,amc pacer d/l
29.5,4,97.0,71,1825,12.2,76,Europe,volkswagen rabbit
32.0,4,85.0,70,1990,17.0,76,Asia,datsun b-210
28.0,4,97.0,75,2155,16.4,76,Asia,toyota corolla
26.5,4,140.0,72,2565,13.6,76,US,ford pinto
20.0,4,130.0,102,3150,15.7,76,Europe,volvo 245
13.0,8,318.0,150,3940,13.2,76,US,plymouth volare premier v8
19.0,4,120.0,88,3270,21.9,76,Europe,peugeot 504
19.0,6,156.0,108,2930,15.5,76,Asia,toyota mark ii
16.5,6,168.0,120,3820,16.7,76,Europe,mercedes-benz 280s
16.5,8,350.0,180,4380,12.1,76,US,cadillac seville
13.0,8,350.0,145,4055,12.0,76,US,chevy c10
13.0,8,302.0,130,3870,15.0,76,US,ford f108
13.0,8,318.0,150,3755,14.0,76,US,dodge d100
31.5,4,98.0,68,2045,18.5,77,Asia,honda accord cvcc
30.0,4,111.0,80,2155,14.8,77,US,buick opel isuzu deluxe
36.0,4,79.0,58,1825,18.6,77,Europe,renault 5 gtl
25.5,4,122.0,96,2300,15.5,77,US,plymouth arrow gs
33.5,4,85.0,70,1945,16.8,77,Asia,datsun f-10 hatchback
17.5,8,305.0,145,3880,12.5,77,US,chevrolet caprice classic
17.0,8,260.0,110,4060,19.0,77,US,oldsmobile cutlass supreme
15.5,8,318.0,145,4140,13.7,77,US,dodge monaco brougham
15.0,8,302.0,130,4295,14.9,77,US,mercury cougar brougham
17.5,6,250.0,110,3520,16.4,77,US,chevrolet concours
20.5,6,231.0,105,3425,16.9,77,US,buick skylark
19.0,6,225.0,100,3630,17.7,77,US,plymouth volare custom
18.5,6,250.0,98,3525,19.0,77,US,ford granada
16.0,8,400.0,180,4220,11.1,77,US,pontiac grand prix lj
15.5,8,350.0,170,4165,11.4,77,US,chevrolet monte carlo landau
15.5,8,400.0,190,4325,12.2,77,US,chrysler cordoba
16.0,8,351.0,149,4335,14.5,77,US,ford thunderbird
29.0,4,97.0,78,1940,14.5,77,Europe,volkswagen rabbit custom
24.5,4,151.0,88,2740,16.0,77,US,pontiac sunbird coupe
26.0,4,97.0,75,2265,18.2,77,Asia,toyota corolla liftback
25.5,4,140.0,89,2755,15.8,77,US,ford mustang ii 2+2
30.5,4,98.0,63,2051,17.0,77,US,chevrolet chevette
33.5,4,98.0,83,2075,15.9,77,US,dodge colt m/m
30.0,4,97.0,67,1985,16.4,77,Asia,subaru dl
30.5,4,97.0,78,2190,14.1,77,Europe,volkswagen dasher
22.0,6,146.0,97,2815,14.5,77,Asia,datsun 810
21.5,4,121.0,110,2600,12.8,77,Europe,bmw 320i
21.5,3,80.0,110,2720,13.5,77,Asia,mazda rx-4
43.1,4,90.0,48,1985,21.5,78,Europe,volkswagen rabbit custom diesel
36.1,4,98.0,66,1800,14.4,78,US,ford fiesta
32.8,4,78.0,52,1985,19.4,78,Asia,mazda glc deluxe
39.4,4,85.0,70,2070,18.6,78,Asia,datsun b210 gx
36.1,4,91.0,60,1800,16.4,78,Asia,honda civic cvcc
19.9,8,260.0,110,3365,15.5,78,US,oldsmobile cutlass salon brougham
19.4,8,318.0,140,3735,13.2,78,US,dodge diplomat
20.2,8,302.0,139,3570,12.8,78,US,mercury monarch ghia
19.2,6,231.0,105,3535,19.2,78,US,pontiac phoenix lj
20.5,6,200.0,95,3155,18.2,78,US,chevrolet malibu
20.2,6,200.0,85,2965,15.8,78,US,ford fairmont (auto)
25.1,4,140.0,88,2720,15.4,78,US,ford fairmont (man)
20.5,6,225.0,100,3430,17.2,78,US,plymouth volare
19.4,6,232.0,90,3210,17.2,78,US,amc concord
20.6,6,231.0,105,3380,15.8,78,US,buick century special
20.8,6,200.0,85,3070,16.7,78,US,mercury zephyr
18.6,6,225.0,110,3620,18.7,78,US,dodge aspen
18.1,6,258.0,120,3410,15.1,78,US,amc concord d/l
19.2,8,305.0,145,3425,13.2,78,US,chevrolet monte carlo landau
17.7,6,231.0,165,3445,13.4,78,US,buick regal sport coupe (turbo)
18.1,8,302.0,139,3205,11.2,78,US,ford futura
17.5,8,318.0,140,4080,13.7,78,US,dodge magnum xe
30.0,4,98.0,68,2155,16.5,78,US,chevrolet chevette
27.5,4,134.0,95,2560,14.2,78,Asia,toyota corona
27.2,4,119.0,97,2300,14.7,78,Asia,datsun 510
30.9,4,105.0,75,2230,14.5,78,US,dodge omni
21.1,4,134.0,95,2515,14.8,78,Asia,toyota celica gt liftback
23.2,4,156.0,105,2745,16.7,78,US,plymouth sapporo
23.8,4,151.0,85,2855,17.6,78,US,oldsmobile starfire sx
23.9,4,119.0,97,2405,14.9,78,Asia,datsun 200-sx
20.3,5,131.0,103,2830,15.9,78,Europe,audi 5000
17.0,6,163.0,125,3140,13.6,78,Europe,volvo 264gl
21.6,4,121.0,115,2795,15.7,78,Europe,saab 99gle
16.2,6,163.0,133,3410,15.8,78,Europe,peugeot 604sl
31.5,4,89.0,71,1990,14.9,78,Europe,volkswagen scirocco
29.5,4,98.0,68,2135,16.6,78,Asia,honda accord lx
21.5,6,231.0,115,3245,15.4,79,US,pontiac lemans v6
19.8,6,200.0,85,2990,18.2,79,US,mercury zephyr 6
22.3,4,140.0,88,2890,17.3,79,US,ford fairmont 4
20.2,6,232.0,90,3265,18.2,79,US,amc concord dl 6
20.6,6,225.0,110,3360,16.6,79,US,dodge aspen 6
17.0,8,305.0,130,3840,15.4,79,US,chevrolet caprice classic
17.6,8,302.0,129,3725,13.4,79,US,ford ltd landau
16.5,8,351.0,138,3955,13.2,79,US,mercury grand marquis
18.2,8,318.0,135,3830,15.2,79,US,dodge st. regis
16.9,8,350.0,155,4360,14.9,79,US,buick estate wagon (sw)
15.5,8,351.0,142,4054,14.3,79,US,ford country squire (sw)
19.2,8,267.0,125,3605,15.0,79,US,chevrolet malibu classic (sw)
18.5,8,360.0,150,3940,13.0,79,US,chrysler lebaron town @ country (sw)
31.9,4,89.0,71,1925,14.0,79,Europe,vw rabbit custom
34.1,4,86.0,65,1975,15.2,79,Asia,maxda glc deluxe
35.7,4,98.0,80,1915,14.4,79,US,dodge colt hatchback custom
27.4,4,121.0,80,2670,15.0,79,US,amc spirit dl
25.4,5,183.0,77,3530,20.1,79,Europe,mercedes benz 300d
23.0,8,350.0,125,3900,17.4,79,US,cadillac eldorado
27.2,4,141.0,71,3190,24.8,79,Europe,peugeot 504
23.9,8,260.0,90,3420,22.2,79,US,oldsmobile cutlass salon brougham
34.2,4,105.0,70,2200,13.2,79,US,plymouth horizon
34.5,4,105.0,70,2150,14.9,79,US,plymouth horizon tc3
31.8,4,85.0,65,2020,19.2,79,Asia,datsun 210
37.3,4,91.0,69,2130,14.7,79,Europe,fiat strada custom
28.4,4,151.0,90,2670,16.0,79,US,buick skylark limited
28.8,6,173.0,115,2595,11.3,79,US,chevrolet citation
26.8,6,173.0,115,2700,12.9,79,US,oldsmobile omega brougham
33.5,4,151.0,90,2556,13.2,79,US,pontiac phoenix
41.5,4,98.0,76,2144,14.7,80,Europe,vw rabbit
38.1,4,89.0,60,1968,18.8,80,Asia,toyota corolla tercel
32.1,4,98.0,70,2120,15.5,80,US,chevrolet chevette
37.2,4,86.0,65,2019,16.4,80,Asia,datsun 310
28.0,4,151.0,90,2678,16.5,80,US,chevrolet citation
26.4,4,140.0,88,2870,18.1,80,US,ford fairmont
24.3,4,151.0,90,3003,20.1,80,US,amc concord
19.1,6,225.0,90,3381,18.7,80,US,dodge aspen
34.3,4,97.0,78,2188,15.8,80,Europe,audi 4000
29.8,4,134.0,90,2711,15.5,80,Asia,toyota corona liftback
31.3,4,120.0,75,2542,17.5,80,Asia,mazda 626
37.0,4,119.0,92,2434,15.0,80,Asia,datsun 510 hatchback
32.2,4,108.0,75,2265,15.2,80,Asia,toyota corolla
46.6,4,86.0,65,2110,17.9,80,Asia,mazda glc
27.9,4,156.0,105,2800,14.4,80,US,dodge colt
40.8,4,85.0,65,2110,19.2,80,Asia,datsun 210
44.3,4,90.0,48,2085,21.7,80,Europe,vw rabbit c (diesel)
43.4,4,90.0,48,2335,23.7,80,Europe,vw dasher (diesel)
36.4,5,121.0,67,2950,19.9,80,Europe,audi 5000s (diesel)
30.0,4,146.0,67,3250,21.8,80,Europe,mercedes-benz 240d
44.6,4,91.0,67,1850,13.8,80,Asia,honda civic 1500 gl
33.8,4,97.0,67,2145,18.0,80,Asia,subaru dl
29.8,4,89.0,62,1845,15.3,80,Europe,vokswagen rabbit
32.7,6,168.0,132,2910,11.4,80,Asia,datsun 280-zx
23.7,3,70.0,100,2420,12.5,80,Asia,mazda rx-7 gs
35.0,4,122.0,88,2500,15.1,80,Europe,triumph tr7 coupe
32.4,4,107.0,72,2290,17.0,80,Asia,honda accord
27.2,4,135.0,84,2490,15.7,81,US,plymouth reliant
26.6,4,151.0,84,2635,16.4,81,US,buick skylark
25.8,4,156.0,92,2620,14.4,81,US,dodge aries wagon (sw)
23.5,6,173.0,110,2725,12.6,81,US,chevrolet citation
30.0,4,135.0,84,2385,12.9,81,US,plymouth reliant
39.1,4,79.0,58,1755,16.9,81,Asia,toyota starlet
39.0,4,86.0,64,1875,16.4,81,US,plymouth champ
35.1,4,81.0,60,1760,16.1,81,Asia,honda civic 1300
32.3,4,97.0,67,2065,17.8,81,Asia,subaru
37.0,4,85.0,65,1975,19.4,81,Asia,datsun 210 mpg
37.7,4,89.0,62,2050,17.3,81,Asia,toyota tercel
34.1,4,91.0,68,1985,16.0,81,Asia,mazda glc 4
34.7,4,105.0,63,2215,14.9,81,US,plymouth horizon 4
34.4,4,98.0,65,2045,16.2,81,US,ford escort 4w
29.9,4,98.0,65,2380,20.7,81,US,ford escort 2h
33.0,4,105.0,74,2190,14.2,81,Europe,volkswagen jetta
33.7,4,107.0,75,2210,14.4,81,Asia,honda prelude
32.4,4,108.0,75,2350,16.8,81,Asia,toyota corolla
32.9,4,119.0,100,2615,14.8,81,Asia,datsun 200sx
31.6,4,120.0,74,2635,18.3,81,Asia,mazda 626
28.1,4,141.0,80,3230,20.4,81,Europe,peugeot 505s turbo diesel
30.7,6,145.0,76,3160,19.6,81,Europe,volvo diesel
25.4,6,168.0,116,2900,12.6,81,Asia,toyota cressida
24.2,6,146.0,120,2930,13.8,81,Asia,datsun 810 maxima
22.4,6,231.0,110,3415,15.8,81,US,buick century
26.6,8,350.0,105,3725,19.0,81,US,oldsmobile cutlass ls
20.2,6,200.0,88,3060,17.1,81,US,ford granada gl
17.6,6,225.0,85,3465,16.6,81,US,chrysler lebaron salon
28.0,4,112.0,88,2605,19.6,82,US,chevrolet cavalier
27.0,4,112.0,88,2640,18.6,82,US,chevrolet cavalier wagon
34.0,4,112.0,88,2395,18.0,82,US,chevrolet cavalier 2-door
31.0,4,112.0,85,2575,16.2,82,US,pontiac j2000 se hatchback
29.0,4,135.0,84,2525,16.0,82,US,dodge aries se
27.0,4,151.0,90,2735,18.0,82,US,pontiac phoenix
24.0,4,140.0,92,2865,16.4,82,US,ford fairmont futura
36.0,4,105.0,74,1980,15.3,82,Europe,volkswagen rabbit l
37.0,4,91.0,68,2025,18.2,82,Asia,mazda glc custom l
31.0,4,91.0,68,1970,17.6,82,Asia,mazda glc custom
38.0,4,105.0,63,2125,14.7,82,US,plymouth horizon miser
36.0,4,98.0,70,2125,17.3,82,US,mercury lynx l
36.0,4,120.0,88,2160,14.5,82,Asia,nissan stanza xe
36.0,4,107.0,75,2205,14.5,82,Asia,honda accord
34.0,4,108.0,70,2245,16.9,82,Asia,toyota corolla
38.0,4,91.0,67,1965,15.0,82,Asia,honda civic
32.0,4,91.0,67,1965,15.7,82,Asia,honda civic (auto)
38.0,4,91.0,67,1995,16.2,82,Asia,datsun 310 gx
25.0,6,181.0,110,2945,16.4,82,US,buick century limited
38.0,6,262.0,85,3015,17.0,82,US,oldsmobile cutlass ciera (diesel)
26.0,4,156.0,92,2585,14.5,82,US,chrysler lebaron medallion
22.0,6,232.0,112,2835,14.7,82,US,ford granada l
32.0,4,144.0,96,2665,13.9,82,Asia,toyota celica gt
36.0,4,135.0,84,2370,13.0,82,US,dodge charger 2.2
27.0,4,151.0,90,2950,17.3,82,US,chevrolet camaro
27.0,4,140.0,86,2790,15.6,82,US,ford mustang gl
44.0,4,97.0,52,2130,24.6,82,Europe,vw pickup
32.0,4,135.0,84,2295,11.6,82,US,dodge rampage
28.0,4,120.0,79,2625,18.6,82,US,ford ranger
31.0,4,119.0,82,2720,19.4,82,US,chevy s-10"""
    .split('\n')
}