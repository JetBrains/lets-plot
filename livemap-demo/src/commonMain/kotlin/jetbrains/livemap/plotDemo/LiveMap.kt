/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.plotDemo

import jetbrains.datalore.plot.config.asMaps
import jetbrains.datalore.plot.config.asMutable
import jetbrains.datalore.plot.config.getList
import jetbrains.datalore.plot.parsePlotSpec
import kotlin.random.Random

class LiveMap {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            titanic()
//            airports(),
//            volcanos(),
//            georeference(),
//            blankPoint(),
//            blankMap(),
//            barWithNanValuesInData(),
//            pieWithNanValuesInData(),
//            pieWithNullValuesInData(),
//            barWithNullValuesInData()
//            multiLayerTooltips()
//            mapJoinBar(),
//            antiMeridian(),
//            tooltips(),
//            symbol_point(),
//            geom_point()
//            fourPointsTwoLayers(),
//            basic(),
//            bunch(),
//           facet()
        )
    }

    object Tileset {
        val nasa = mapOf(
            "tiles" to mapOf(
                "kind" to "raster_zxy",
                "url" to "https://gibs.earthdata.nasa.gov/wmts/epsg3857/best/ASTER_GDEM_Greyscale_Shaded_Relief/default//GoogleMapsCompatible_Level12/{z}/{y}/{x}.jpg",
                "attribution" to "<a href=\"https://earthdata.nasa.gov/eosdis/science-system-description/eosdis-components/gibs\">\u00a9 NASA Global Imagery Browse Services (GIBS)</a>",
                "min_zoom" to 1,
                "max_zoom" to 12
            )
        )

        val osm = mapOf(
            "tiles" to mapOf(
                "kind" to "raster_zxy",
                "url" to "https://[abc].tile.openstreetmap.org/{z}/{x}/{y}.png",
                "attribution" to "<a href=\"https://www.openstreetmap.org/copyright\">© OpenStreetMap contributors</a>"
            )
        )

        val devVector = mapOf(
            "tiles" to mapOf(
                "kind" to "vector_lets_plot",
                "url" to "ws://10.0.0.127:3943",
                "min_zoom" to 1,
                "max_zoom" to 15,
                "theme" to "color"
            )
        )

        val chessboard = mapOf(
            "tiles" to mapOf(
                "kind" to "chessboard"
            )
        )
    }

    private fun MutableMap<String, Any>.updateTiles(tilesSpec: Map<String, Any>) = apply {
        getList("layers")!!.asMaps().first().asMutable().putAll(tilesSpec)
    }

    private fun volcanos(): MutableMap<String, Any> {
        val spec = """
{
  "mapping": {},
  "data_meta": {},
  "ggsize": {
    "width": 800,
    "height": 800
  },
  "kind": "plot",
  "scales": [],
  "layers": [
    {
      "geom": "livemap",
      "mapping": {},
      "data_meta": {},
      "location": {
        "type": "coordinates",
        "data": [
          129.450953,
          30.151413,
          144.875758,
          42.515155
        ]
      },
      "zoom": 6,
      "const_size_zoomin": 2, 
      "tiles": {
        "kind": "raster_zxy",
        "url": "https://gibs.earthdata.nasa.gov/wmts/epsg3857/best/ASTER_GDEM_Greyscale_Shaded_Relief/default//GoogleMapsCompatible_Level12/{z}/{y}/{x}.jpg",
        "attribution": "<a href=\"https://earthdata.nasa.gov/eosdis/science-system-description/eosdis-components/gibs\">\u00a9 NASA Global Imagery Browse Services (GIBS)</a>",
        "min_zoom": 1,
        "max_zoom": 12
      },
      "geocoding": {
        "url": "http://10.0.0.127:3020/map_data/geocoding"
      }
    },
    {
      "geom": "point",
      "data": {
        "Unnamed: 0": [
          0,
          1,
          2,
          3,
          4,
          5,
          6,
          7,
          8,
          9,
          10,
          11,
          12,
          13,
          14,
          15,
          16,
          17,
          18,
          19,
          20,
          21,
          22,
          23,
          24,
          25,
          26,
          27,
          28,
          29,
          30,
          31,
          32,
          33,
          34,
          35,
          36,
          37,
          38,
          39,
          40,
          41,
          42,
          43,
          44,
          45,
          46,
          47,
          48,
          49,
          50,
          51,
          52,
          53,
          54,
          55,
          56,
          57,
          58,
          59,
          60,
          61,
          62,
          63,
          64,
          65,
          66,
          67,
          68,
          69,
          70,
          71,
          72,
          73,
          74,
          75,
          76,
          77,
          78,
          79,
          80,
          81,
          82,
          83,
          84,
          85,
          86,
          87,
          88,
          89,
          90,
          91,
          92,
          93,
          94,
          95,
          96,
          97,
          98,
          99,
          100,
          101,
          102,
          103,
          104,
          105,
          106,
          107,
          108,
          109,
          110,
          111,
          112,
          113,
          114,
          115,
          116,
          117,
          118,
          119,
          120,
          121,
          122,
          123,
          124,
          125,
          126,
          127,
          128,
          129,
          130,
          131,
          132,
          133,
          134,
          135,
          136,
          137,
          138,
          139,
          140,
          141,
          142,
          143,
          144,
          145,
          146,
          147,
          148,
          149,
          150,
          151,
          152,
          153,
          154,
          155,
          156,
          157,
          158,
          159,
          160,
          161,
          162,
          163,
          164,
          165,
          166,
          167,
          168,
          169,
          170,
          171,
          172,
          173,
          174,
          175,
          176,
          177,
          178,
          179,
          180,
          181,
          182,
          183
        ],
        "Name": [
          "Akaigawa Caldera",
          "Mount Atosanupuri",
          "Daisetsuzan Volcanic Group",
          "Mount Eniwa",
          "Mount Esan",
          "Akan Caldera\u00a0[ja]",
          "Mount Meakan",
          "Mount Oakan",
          "Mount I\u014d",
          "Kussharo Caldera",
          "Kuttara Caldera",
          "Mash\u016b Caldera",
          "Nigorigawa Caldera",
          "Nipesotsu-Maruyama Volcanic Group",
          "Niseko Volcanic Group",
          "Mount Onnebetsu",
          "Oshima-\u014cshima",
          "Mount Rausu",
          "Mount Rishiri",
          "Shikaribetsu Volcano Group",
          "Shikotsu Caldera",
          "Mount Shiribetsu",
          "T\u014dya Caldera",
          "Mount Hokkaid\u014d-Komagatake",
          "Mount Shari",
          "Mount Shiretoko",
          "Mount Tarumae",
          "Mount Tench\u014d\u00a0[ja]",
          "Tokachi-Mitsumata Caldera",
          "Tomuraushi-Chubetsu Volcano Group",
          "Mount Unabetsu",
          "Mount Usu",
          "Mount Y\u014dtei",
          "Abu Volcano Group",
          "Mount Adatara",
          "Mount Akagi",
          "Mount Akandana",
          "Mount Akita-Komagatake",
          "Mount Akita-Yakeyama",
          "Mount Amagi",
          "Aonoyama Volcano Group\u00a0[ja]",
          "Mount Asakusa\u00a0[ja]",
          "Mount Asama",
          "Mount Ashitaka",
          "Mount Azuma",
          "Mount Azumaya",
          "Mount Bandai",
          "Mount Ch\u014dkai",
          "Mount Daisen",
          "Mount Daruma\u00a0[ja]",
          "Eboshi Volcano Group",
          "Mount Fuji",
          "Mount Futamata (ja)",
          "Mount Gassan",
          "Mount Hachimantai",
          "Hakk\u014dda Caldera",
          "Mount Hakk\u014dda",
          "Mount Hakone",
          "Mount Haku",
          "Mount Haruna",
          "Hijiori Caldera",
          "Mount Hiuchigatake",
          "Mount Hotaka (ja)",
          "Ikarigaseki Caldera",
          "Mount Iiji\u00a0[ja]",
          "Mount Iizuna",
          "Mount Iwaki",
          "Izu-T\u014dbu volcano Group",
          "Mount Iwate",
          "Kannabe Volcano Group",
          "Mount Kanp\u016b\u00a0[ja]",
          "Mount Kirigamine",
          "Mount Komochi",
          "Mount Kurohime",
          "Kurofuji Volcano Group",
          "Mount Kurikoma\u00a0[ja]",
          "Mount Kusatsu-Shirane",
          "Mount Onoko\u00a0[ja]",
          "Mount Madarao\u00a0[ja]",
          "Mount Minakami\u00a0[ja]",
          "Megata",
          "Mount Moriyoshi\u00a0[ja]",
          "Mukaimachi Caldera\n",
          "Mount Mutsu-Hiuchi",
          "Mount My\u014dk\u014d",
          "Mount Naeba",
          "Mount Nanashigure\u00a0[ja]",
          "Mount Nantai",
          "Narugo Caldera",
          "Mount Nasu",
          "Mount Nekoma",
          "Mount Niigata-Yakeyama",
          "Mount Nikk\u014d-Shirane",
          "Nodai Caldera",
          "Mount Ny\u014dh\u014d-Akanagi",
          "Mount Norikura",
          "Numazawa\u00a0[ja]",
          "Oki-D\u014dgo",
          "Okiura Caldera",
          "Mount Omanago\u00a0[ja]",
          "Mount Ontake",
          "Onikobe Caldera\u00a0[ja]\n",
          "Mount Osore",
          "Mount Sanbe\u00a0[ja]",
          "Mount Shiga\u00a0[ja]",
          "Mount Sukai",
          "Mount Takahara\u00a0[ja]",
          "Mount Takara",
          "Mount Takayashiro\u00a0[ja]",
          "Midagahara(A.K.A. Tateyama)",
          "Tazawako Caldera",
          "Tokachidake Volcano Group",
          "Towada Caldera",
          "Washiba-Kumontaira",
          "Mount Yake",
          "Mount Yakeishi",
          "Kita-Yatsugatake(Northern Yatsugatake Volcanic Group)",
          "Minami-Yatsugatake(Southern Yatsugatake Volcanic Group)",
          "Mount Tateshina (Part of\u3000Northern Yatsugatake Volcanic Group)",
          "Mount Za\u014d",
          "Aogashima",
          "Bayonnaise Rocks",
          "Hachij\u014djima",
          "Izu-\u014cshima",
          "K\u014dzushima",
          "Kurose",
          "Mikurajima",
          "Miyakejima",
          "My\u014djinsh\u014d (A.K.A. Myojin Reef)",
          "Niijima",
          "Sofugan (A.K.A. Lot_s Wife)",
          "Sumisujima (A.K.A. Smith Rocks)",
          "Toshima",
          "Torishima (A.K.A. Izu-Torishima)",
          "Nishinoshima",
          "Fukutoku-Okanoba",
          "Fukujin Seamount\u00a0[ja]",
          "Funka Asane",
          "Kita-I\u014djima (North Iwo Jima)",
          "I\u014djima (Iwo Jima)",
          "Kaitoku Seamount",
          "Kaikata Seamount",
          "Kasuga Seamount",
          "Minami-Kasuga Seamount",
          "Kita-Fukutokutai",
          "Minami-Hiyoshi Seamount\u00a0[ja]",
          "Nikk\u014d Seamount\u00a0[ja]",
          "Mokuy\u014d Seamount",
          "Suiyo Seamount",
          "Aira Caldera",
          "Ata Caldera\u00a0[ja]",
          "Wakamiko Caldera\u00a0[ja]",
          "Mount Aso(A.K.A. Aso Caldera)",
          "Fukue Volcano Group",
          "Mount Futago (ja)",
          "Ibusuki Volcanic Field\u00a0[ja]",
          "Ikeda (Part of Ibusuki Volcanic Field)",
          "Imuta",
          "Mount Kaimon",
          "Kakuto Caldera",
          "Mount Kinb\u014d (Kinp\u014d)",
          "Mount Kirishima",
          "Kobayashi Caldera",
          "Mount Kuj\u016b",
          "Sakurajima",
          "Ojikajima Volcano Group",
          "Lake Sumiyoshi\u00a0[ja]",
          "Yonemaru\u00a0[ja]",
          "Mount Takasaki\u00a0[ja]",
          "Mount Tara\u00a0[ja]",
          "Mount Tsurumi and Mount Garan",
          "Mount Unzen",
          "Mount Yufu",
          "Akusekijima",
          "Gajajima",
          "Kogajajima",
          "Submarine Volcano NNE of Iriomotejima\u00a0[ja]",
          "I\u014dtorishima",
          "Kikai Caldera",
          "Kuchinoshima",
          "Kuchinoerabujima",
          "Nakanoshima",
          "Suwanosejima",
          "Yokoatejima"
        ],
        "Elevation_meters": [
          "725",
          "512",
          "2290",
          "1320",
          "613",
          "-",
          "1499",
          "1370",
          "1563",
          "-",
          "581",
          "855",
          "356",
          "2013",
          "1309",
          "1330",
          "737",
          "1661",
          "1721",
          "1430",
          "1320",
          "1107",
          "-",
          "1131",
          "1547",
          "1254",
          "1041",
          "1046",
          "-",
          "2141",
          "1419",
          "737",
          "1898",
          "-",
          "1718",
          "1828",
          "2109",
          "1637",
          "1366",
          "1406",
          "-",
          "1585",
          "2544",
          "1504",
          "1705",
          "2354",
          "1819",
          "2237",
          "1729",
          "982",
          "2227",
          "3776",
          "1548",
          "1984",
          "1614",
          "-",
          "1585",
          "1438",
          "2702",
          "1449",
          "516",
          "2356",
          "2158",
          "-",
          "1112",
          "1917",
          "1625",
          "1406",
          "2041",
          "-",
          "355",
          "1925",
          "1296",
          "2053",
          "1642",
          "1628",
          "2160",
          "1208",
          "1382",
          "659",
          "291",
          "1454",
          "-",
          "781",
          "2446",
          "2145",
          "1063",
          "2484",
          "462",
          "1917",
          "1404",
          "2400",
          "2578",
          "-",
          "2483",
          "3026",
          "1100",
          "151",
          "-",
          "2376",
          "3063",
          "-",
          "879",
          "1126",
          "2041",
          "2144",
          "1795",
          "350",
          "1351",
          "-",
          "-",
          "2077",
          "1159",
          "2924",
          "2455",
          "1548",
          "-",
          "-",
          "2530",
          "1841",
          "423",
          "11",
          "854",
          "758",
          "574",
          "-110",
          "851",
          "813",
          "-50",
          "432",
          "99",
          "136",
          "508",
          "394",
          "142",
          "-25",
          "-43",
          "-20",
          "792",
          "161",
          "-103",
          "-165",
          "-598",
          "-274",
          "-55",
          "-30",
          "-612",
          "-920",
          "-1418",
          "-",
          "-",
          "-",
          "1592",
          "315",
          "720",
          "922",
          "256",
          "509",
          "922",
          "-",
          "665",
          "1700",
          "-",
          "1791",
          "1117",
          "111",
          "-",
          "-",
          "628",
          "1076",
          "1584",
          "1500",
          "1583",
          "586",
          "497",
          "301",
          "-",
          "212",
          "704",
          "628",
          "649",
          "979",
          "799",
          "495"
        ],
        "Elevation_ft": [
          "2379",
          "1680",
          "7513",
          "4331",
          "2028",
          "-",
          "4916",
          "4495",
          "5128",
          "-",
          "1906",
          "2805",
          "1168",
          "6604",
          "4295",
          "4364",
          "2418",
          "5449",
          "5646",
          "4692",
          "4331",
          "3632",
          "-",
          "3711",
          "5075",
          "4114",
          "3416",
          "3432",
          "-",
          "7024",
          "4656",
          "2418",
          "6227",
          "-",
          "5635",
          "5997",
          "6919",
          "5371",
          "4482",
          "4613",
          "-",
          "5200",
          "8340",
          "4934",
          "5594",
          "7723",
          "5968",
          "7326",
          "5673",
          "3222",
          "7306",
          "12388",
          "5079",
          "6509",
          "5295",
          "-",
          "5200",
          "4720",
          "8865",
          "4754",
          "1693",
          "7730",
          "7080",
          "-",
          "3648",
          "6289",
          "5331",
          "4613",
          "6696",
          "-",
          "1165",
          "6316",
          "4252",
          "6736",
          "5387",
          "5341",
          "7123",
          "3963",
          "4534",
          "2162",
          "955",
          "4770",
          "-",
          "2562",
          "8025",
          "7037",
          "3488",
          "8148",
          "1542",
          "6283",
          "4606",
          "7874",
          "8458",
          "-",
          "8146",
          "9928",
          "3609",
          "495",
          "-",
          "7795",
          "10049",
          "-",
          "2884",
          "3694",
          "6696",
          "7034",
          "5889",
          "1148",
          "4432",
          "-",
          "-",
          "6814",
          "3802",
          "9593",
          "8054",
          "5079",
          "-",
          "-",
          "8300",
          "6040",
          "1388",
          "36",
          "2802",
          "2507",
          "1877",
          "-361",
          "2792",
          "2674",
          "-164",
          "1417",
          "325",
          "446",
          "1667",
          "1293",
          "466",
          "-82",
          "-141",
          "-67",
          "2598",
          "528",
          "-338",
          "-541",
          "-1962",
          "-899",
          "-180",
          "-98",
          "-2008",
          "-3018",
          "-4652",
          "-",
          "-",
          "-",
          "5223",
          "1033",
          "2362",
          "3025",
          "840",
          "1670",
          "3025",
          "-",
          "2182",
          "5577",
          "-",
          "5876",
          "3665",
          "364",
          "-",
          "-",
          "2060",
          "3530",
          "5197",
          "4921",
          "5194",
          "1923",
          "1631",
          "988",
          "-",
          "696",
          "2310",
          "2060",
          "2129",
          "3212",
          "2621",
          "1624"
        ],
        "Coordinates": [
          " \ufeff43.083\u00b0N 140.817\u00b0E\ufeff ",
          " \ufeff43.610\u00b0N 144.438\u00b0E\ufeff ",
          " \ufeff43.663\u00b0N 142.854\u00b0E\ufeff ",
          " \ufeff42.793\u00b0N 141.285\u00b0E\ufeff ",
          " \ufeff41.804\u00b0N 141.166\u00b0E\ufeff ",
          " \ufeff43.451\u00b0N 144.110\u00b0E\ufeff ",
          " \ufeff43.386\u00b0N 144.008\u00b0E\ufeff ",
          " \ufeff43.453\u00b0N 144.163\u00b0E\ufeff ",
          " \ufeff44.131\u00b0N 145.165\u00b0E\ufeff ",
          " \ufeff43.621\u00b0N 144.336\u00b0E\ufeff ",
          " \ufeff42.499\u00b0N 141.175\u00b0E\ufeff ",
          " \ufeff43.570\u00b0N 144.565\u00b0E\ufeff ",
          " \ufeff42.12\u00b0N 140.45\u00b0E\ufeff ",
          " \ufeff43.453\u00b0N 143.036\u00b0E\ufeff ",
          " \ufeff42.88\u00b0N 140.63\u00b0E\ufeff ",
          " \ufeff43.993\u00b0N 145.013\u00b0E\ufeff ",
          " \ufeff41.50\u00b0N 139.37\u00b0E\ufeff ",
          " \ufeff44.075\u00b0N 145.122\u00b0E\ufeff ",
          " \ufeff45.18\u00b0N 141.25\u00b0E\ufeff ",
          " \ufeff43.312\u00b0N 143.096\u00b0E\ufeff ",
          " \ufeff42.753\u00b0N 141.327\u00b0E\ufeff ",
          " \ufeff42.767\u00b0N 140.916\u00b0E\ufeff ",
          " \ufeff42.602\u00b0N 140.851\u00b0E\ufeff ",
          " \ufeff42.063\u00b0N 140.677\u00b0E\ufeff ",
          " \ufeff43.765\u00b0N 144.717\u00b0E\ufeff ",
          " \ufeff44.235\u00b0N 145.273\u00b0E\ufeff ",
          " \ufeff42.690\u00b0N 141.376\u00b0E\ufeff ",
          " \ufeff44.044\u00b0N 145.086\u00b0E\ufeff ",
          " \ufeff43.516\u00b0N 143.150\u00b0E\ufeff ",
          " \ufeff43.527\u00b0N 142.849\u00b0E\ufeff ",
          " \ufeff43.876\u00b0N 144.876\u00b0E\ufeff ",
          " \ufeff42.543\u00b0N 140.839\u00b0E\ufeff ",
          " \ufeff42.826\u00b0N 140.811\u00b0E\ufeff ",
          " \ufeff34.50\u00b0N 131.60\u00b0E\ufeff ",
          " \ufeff37.62\u00b0N 140.28\u00b0E\ufeff ",
          " \ufeff36.53\u00b0N 139.18\u00b0E\ufeff ",
          " \ufeff36.200\u00b0N 137.572\u00b0E\ufeff ",
          " \ufeff39.75\u00b0N 140.80\u00b0E\ufeff ",
          " \ufeff39.964\u00b0N 140.757\u00b0E\ufeff ",
          " \ufeff34.862\u00b0N 139.002\u00b0E\ufeff ",
          " \ufeff34.461\u00b0N 131.791\u00b0E\ufeff ",
          " \ufeff37.343\u00b0N 139.234\u00b0E\ufeff ",
          " \ufeff36.405\u00b0N 138.522\u00b0E\ufeff ",
          " \ufeff35.219\u00b0N 138.800\u00b0E\ufeff ",
          " \ufeff37.73\u00b0N 140.25\u00b0E\ufeff ",
          " \ufeff36.542\u00b0N 138.413\u00b0E\ufeff ",
          " \ufeff37.60\u00b0N 140.08\u00b0E\ufeff ",
          " \ufeff39.08\u00b0N 140.03\u00b0E\ufeff ",
          " \ufeff35.371\u00b0N 133.54\u00b0E\ufeff ",
          " \ufeff34.955\u00b0N 138.839\u00b0E\ufeff ",
          " \ufeff36.433\u00b0N 138.387\u00b0E\ufeff ",
          " \ufeff35.358\u00b0N 138.731\u00b0E\ufeff ",
          " \ufeff37.246\u00b0N 139.967\u00b0E\ufeff ",
          " \ufeff38.549\u00b0N 140.027\u00b0E\ufeff ",
          " \ufeff39.95\u00b0N 140.85\u00b0E\ufeff ",
          " \ufeff40.693\u00b0N 140.930\u00b0E\ufeff ",
          " \ufeff40.656\u00b0N 140.881\u00b0E\ufeff ",
          " \ufeff35.22\u00b0N 139.02\u00b0E\ufeff ",
          " \ufeff36.15\u00b0N 136.78\u00b0E\ufeff ",
          " \ufeff36.47\u00b0N 138.88\u00b0E\ufeff ",
          " \ufeff38.608\u00b0N 140.161\u00b0E\ufeff ",
          " \ufeff36.95\u00b0N 139.28\u00b0E\ufeff ",
          " \ufeff36.805\u00b0N 139.132\u00b0E\ufeff ",
          " \ufeff40.499\u00b0N 140.620\u00b0E\ufeff ",
          " \ufeff36.956\u00b0N 138.833\u00b0E\ufeff ",
          " \ufeff36.739\u00b0N 138.136\u00b0E\ufeff ",
          " \ufeff40.65\u00b0N 140.30\u00b0E\ufeff ",
          " \ufeff34.92\u00b0N 139.12\u00b0E\ufeff ",
          " \ufeff39.850\u00b0N 141.004\u00b0E\ufeff ",
          " \ufeff35.507\u00b0N 134.675\u00b0E\ufeff ",
          " \ufeff39.93\u00b0N 139.88\u00b0E\ufeff ",
          " \ufeff36.103\u00b0N 138.196\u00b0E\ufeff ",
          " \ufeff36.591\u00b0N 138.998\u00b0E\ufeff ",
          " \ufeff36.817\u00b0N 138.120\u00b0E\ufeff ",
          " \ufeff35.803\u00b0N 138.536\u00b0E\ufeff ",
          " \ufeff38.95\u00b0N 140.78\u00b0E\ufeff ",
          " \ufeff36.619\u00b0N 138.535\u00b0E\ufeff ",
          " \ufeff36.581\u00b0N 138.937\u00b0E\ufeff ",
          " \ufeff36.837\u00b0N 138.274\u00b0E\ufeff ",
          " \ufeff36.554\u00b0N 138.222\u00b0E\ufeff ",
          " \ufeff39.95\u00b0N 139.73\u00b0E\ufeff ",
          " \ufeff39.977\u00b0N 140.544\u00b0E\ufeff ",
          " \ufeff38.75\u00b0N 140.51\u00b0E\ufeff ",
          " \ufeff41.439\u00b0N 141.052\u00b0E\ufeff ",
          " \ufeff36.891\u00b0N 138.113\u00b0E\ufeff ",
          " \ufeff36.836\u00b0N 138.684\u00b0E\ufeff ",
          " \ufeff40.069\u00b0N 141.106\u00b0E\ufeff ",
          " \ufeff36.768\u00b0N 139.486\u00b0E\ufeff ",
          " \ufeff38.73\u00b0N 140.73\u00b0E\ufeff ",
          " \ufeff37.12\u00b0N 139.97\u00b0E\ufeff ",
          " \ufeff37.615\u00b0N 140.016\u00b0E\ufeff ",
          " \ufeff36.920\u00b0N 138.036\u00b0E\ufeff ",
          " \ufeff36.798\u00b0N 139.376\u00b0E\ufeff ",
          " \ufeff41.267\u00b0N 140.867\u00b0E\ufeff ",
          " \ufeff36.811\u00b0N 139.536\u00b0E\ufeff ",
          " \ufeff36.106\u00b0N 137.55\u00b0E\ufeff ",
          " \ufeff37.454\u00b0N 139.572\u00b0E\ufeff ",
          " \ufeff36.181\u00b0N 133.324\u00b0E\ufeff ",
          " \ufeff40.567\u00b0N 140.733\u00b0E\ufeff ",
          " \ufeff36.796\u00b0N 139.507\u00b0E\ufeff ",
          " \ufeff35.890\u00b0N 137.48\u00b0E\ufeff ",
          " \ufeff38.83\u00b0N 140.69\u00b0E\ufeff ",
          " \ufeff41.32\u00b0N 141.08\u00b0E\ufeff ",
          " \ufeff35.13\u00b0N 132.62\u00b0E\ufeff ",
          " \ufeff36.70\u00b0N 138.52\u00b0E\ufeff ",
          " \ufeff36.689\u00b0N 139.337\u00b0E\ufeff ",
          " \ufeff36.90\u00b0N 139.78\u00b0E\ufeff ",
          " \ufeff35.346\u00b0N 134.919\u00b0E\ufeff ",
          " \ufeff36.799\u00b0N 138.403\u00b0E\ufeff ",
          " \ufeff36.57\u00b0N 137.60\u00b0E\ufeff ",
          " \ufeff39.722\u00b0N 140.661\u00b0E\ufeff ",
          " \ufeff43.418\u00b0N 142.686\u00b0E\ufeff ",
          " \ufeff40.47\u00b0N 140.92\u00b0E\ufeff ",
          " \ufeff36.408\u00b0N 137.594\u00b0E\ufeff ",
          " \ufeff36.226\u00b0N 137.587\u00b0E\ufeff ",
          " \ufeff39.164\u00b0N 140.829\u00b0E\ufeff ",
          " \ufeff36.088\u00b0N 138.32\u00b0E\ufeff ",
          " \ufeff35.971\u00b0N 138.37\u00b0E\ufeff ",
          " \ufeff36.10\u00b0N 138.30\u00b0E\ufeff ",
          " \ufeff38.15\u00b0N 140.45\u00b0E\ufeff ",
          " \ufeff32.45\u00b0N 139.77\u00b0E\ufeff ",
          " \ufeff31.888\u00b0N 139.918\u00b0E\ufeff ",
          " \ufeff33.13\u00b0N 139.77\u00b0E\ufeff ",
          " \ufeff34.726\u00b0N 139.394\u00b0E\ufeff ",
          " \ufeff34.22\u00b0N 139.15\u00b0E\ufeff ",
          " \ufeff33.40\u00b0N 139.68\u00b0E\ufeff ",
          " \ufeff33.871\u00b0N 139.605\u00b0E\ufeff ",
          " \ufeff34.086\u00b0N 139.526\u00b0E\ufeff ",
          " \ufeff31.918\u00b0N 140.021\u00b0E\ufeff ",
          " \ufeff34.37\u00b0N 139.27\u00b0E\ufeff ",
          " \ufeff29.793\u00b0N 140.342\u00b0E\ufeff ",
          " \ufeff31.439\u00b0N 140.05\u00b0E\ufeff ",
          " \ufeff34.52\u00b0N 139.28\u00b0E\ufeff ",
          " \ufeff30.484\u00b0N 140.302\u00b0E\ufeff ",
          " \ufeff27.274\u00b0N 140.877\u00b0E\ufeff ",
          " \ufeff24.28\u00b0N 141.485\u00b0E\ufeff ",
          " \ufeff21.767\u00b0N 143.717\u00b0E\ufeff ",
          " \ufeff25.455\u00b0N 141.238\u00b0E\ufeff ",
          " \ufeff25.43\u00b0N 141.28\u00b0E\ufeff ",
          " \ufeff24.781\u00b0N 141.315\u00b0E\ufeff ",
          " \ufeff26.127\u00b0N 141.098\u00b0E\ufeff ",
          " \ufeff26.667\u00b0N 140.929\u00b0E\ufeff ",
          " \ufeff22.767\u00b0N 142.717\u00b0E\ufeff ",
          " \ufeff21.6\u00b0N 143.633\u00b0E\ufeff ",
          " \ufeff24.414\u00b0N 141.419\u00b0E\ufeff ",
          " \ufeff23.5\u00b0N 141.935\u00b0E\ufeff ",
          " \ufeff23.083\u00b0N 142.3\u00b0E\ufeff ",
          " \ufeff28.317\u00b0N 140.566\u00b0E\ufeff ",
          " \ufeff28.6\u00b0N 140.633\u00b0E\ufeff ",
          " \ufeff31.656\u00b0N 130.705\u00b0E\ufeff ",
          " \ufeff31.359\u00b0N 130.683\u00b0E\ufeff ",
          " \ufeff31.666\u00b0N 130.783\u00b0E\ufeff ",
          " \ufeff32.881\u00b0N 131.106\u00b0E\ufeff ",
          " \ufeff32.653\u00b0N 128.861\u00b0E\ufeff ",
          " \ufeff33.583\u00b0N 131.601\u00b0E\ufeff ",
          " \ufeff31.22\u00b0N 130.57\u00b0E\ufeff ",
          " \ufeff31.235\u00b0N 130.564\u00b0E\ufeff ",
          " \ufeff31.822\u00b0N 130.461\u00b0E\ufeff ",
          " \ufeff31.180\u00b0N 130.528\u00b0E\ufeff ",
          " \ufeff32.0455\u00b0N 130.764\u00b0E\ufeff ",
          " \ufeff32.814\u00b0N 130.639\u00b0E\ufeff ",
          " \ufeff31.931\u00b0N 130.864\u00b0E\ufeff ",
          " \ufeff31.99\u00b0N 130.976\u00b0E\ufeff ",
          " \ufeff33.083\u00b0N 131.251\u00b0E\ufeff ",
          " \ufeff31.585\u00b0N 130.657\u00b0E\ufeff ",
          " \ufeff33.201\u00b0N 129.063\u00b0E\ufeff ",
          " \ufeff31.771\u00b0N 130.591\u00b0E\ufeff ",
          " \ufeff31.775\u00b0N 130.564\u00b0E\ufeff ",
          " \ufeff33.252\u00b0N 131.524\u00b0E\ufeff ",
          " \ufeff32.974\u00b0N 130.071\u00b0E\ufeff ",
          " \ufeff33.28\u00b0N 131.432\u00b0E\ufeff ",
          " \ufeff32.757\u00b0N 130.294\u00b0E\ufeff ",
          " \ufeff33.282\u00b0N 131.390\u00b0E\ufeff ",
          " \ufeff29.45\u00b0N 129.60\u00b0E\ufeff ",
          " \ufeff29.903\u00b0N 129.541\u00b0E\ufeff ",
          " \ufeff29.879\u00b0N 129.625\u00b0E\ufeff ",
          " \ufeff24.558\u00b0N 124.00\u00b0E\ufeff ",
          " \ufeff27.877\u00b0N 128.224\u00b0E\ufeff ",
          " \ufeff30.789\u00b0N 130.308\u00b0E\ufeff ",
          " \ufeff29.967\u00b0N 129.924\u00b0E\ufeff ",
          " \ufeff30.43\u00b0N 130.22\u00b0E\ufeff ",
          " \ufeff29.859\u00b0N 129.856\u00b0E\ufeff ",
          " \ufeff29.638\u00b0N 129.714\u00b0E\ufeff ",
          " \ufeff28.799\u00b0N 128.996\u00b0E\ufeff "
        ],
        "Last_eruption": [
          "1.3 Ma BP",
          "1000-200 BP",
          "AD 1739",
          "17th century",
          "AD 1874",
          "0.25 Ma BP",
          "AD 2008",
          "5-2.5 ka BP",
          "AD 1936",
          "2.3 ka BP",
          "40 ka BP",
          "1 ka BP",
          "15 ka BP",
          "AD 1898[\u2020 2]",
          "6 ka BP",
          "0.2 Ma BP",
          "AD 1759",
          "700-500 BP",
          "8-2 ka BP",
          "20 ka BP or later",
          "50-40 ka BP",
          "Not known",
          "40-30 ka BP",
          "AD 2000",
          "0.25 Ma BP",
          "0.2 Ma BP",
          "AD 1981",
          "1.9 ka BP",
          "1 Ma BP",
          "0.1 Ma BP",
          "0.5 Ma BP",
          "AD 2000",
          "2.5 ka BP",
          "Mt. Kasayama (ja): 8.8 ka BP",
          "AD 1900",
          "AD 1251?[\u2020 3]",
          "10-6.5 ka[\u2020 4]",
          "AD 1971",
          "AD 1997",
          "0.2 Ma BP",
          "70 ka BP",
          "1.6 Ma BP",
          "AD 2019[\u2020 5]",
          "80 ka BP",
          "AD 1977",
          "0.3 Ma BP",
          "AD 1888",
          "AD 1974",
          "17 ka BP",
          "0.5 Ma BP",
          "0.3 Ma BP",
          "AD 1707",
          "90 ka BP",
          "0.3 Ma BP",
          "7.3 ka BP",
          "0.4 Ma BP BP",
          "600-400 BP",
          "AD 2015",
          "AD 1659",
          "AD 525-550[\u2020 6]",
          "12 ka BP",
          "AD 1544",
          "1 Ma BP",
          "1.3 Ma BP",
          "0.2 Ma BP",
          "0.15 Ma BP",
          "AD 1863",
          "Teishi Knoll (ja): 1989",
          "AD 1919",
          "Mt.Kannabe: 20-10 ka BP",
          "10 ka BP",
          "0.75 Ma BP",
          "0.2 Ma BP",
          "50 ka BP",
          "0.5 Ma BP",
          "AD 1944[\u2020 7]",
          "AD 2018[\u2020 8]",
          "1.2 Ma BP",
          "0.5 Ma BP",
          "0.3 Ma BP",
          "20-29 ka BP",
          "0.5 Ma BP",
          "0.6 Ma BP",
          "0.5 Ma BP",
          "3 ka BP[\u2020 9]",
          "0.2 Ma BP",
          "0.9 Ma BP",
          "7 ka BP",
          "AD 837[\u2020 10]",
          "Mt. Chausu (ja): AD 1963",
          "0.8 Ma BP",
          "2016",
          "AD 1890",
          "1.9 Ma BP",
          "80 ka BP[\u2020 11]",
          "2 ka BP",
          "5.4 ka BP",
          "0.42 Ma BP",
          "1.5 Ma BP",
          "0.47 Ma[\u2020 12]",
          "AD 2020[\u2020 13]",
          "0.2 Ma BP",
          "20 ka BP or 1787",
          "1.4-1.3 ka BP",
          "10 ka BP",
          "0.9 Ma BP",
          "6.5 ka BP",
          "0.3 Ma BP",
          "0.2 Ma BP",
          "AD 1836 (AD 1949?)",
          "1.4 Ma BP",
          "2004",
          "AD 915",
          "4000 BC",
          "AD 1963",
          "0.2 Ma BP",
          "Mt.Yokodake\u00a0[ja]: 0.9-0.7 ka BP",
          "0.1 Ma BP",
          null,
          "AD 1940",
          "AD 1785",
          "AD 1970[\u2020 14]",
          "AD 1605",
          "Mt. Mihara: AD 1990",
          "AD 838",
          "Caldera: older than 20 ka BP[\u2020 15]",
          "6.3 ka BP",
          "AD 2013[\u2020 16]",
          "AD 1970",
          "Mt. Mukaiyama: 887",
          "(Discolored water: AD 1975)",
          "AD 1916",
          "9.1-4.0 ka BP",
          "AD 2002",
          "AD 2017",
          "AD 2005[\u2020 17]",
          "AD 1974[\u2020 18]",
          "AD 1945",
          "0.14 Ma BP",
          "AD 2015",
          "AD 1984[\u2020 19]",
          "(Submarine hydrothermal activity: AD 1988)",
          "AD 1959[\u2020 20]",
          null,
          "AD 1988?",
          "AD 1976[\u2020 21]",
          "(Discolored water: AD 1979)",
          "(Submarine hydrothermal activity: AD 1990)",
          "(Submarine hydrothermal activity: AD 1991)",
          "25 ka BP",
          "0.11 Ma BP",
          "19 ka BP",
          "AD 2019[\u2020 22]",
          "unknown volcano: 2.4 ka BP",
          "1.1 Ma BP",
          "30 ka BP",
          "Mt. Nabeshima 4.9 ka BP",
          "0.35 Ma BP",
          "AD 885",
          "0.35-0.30 Ma BP",
          "0.2 Ma BP",
          "Mt. Shinmoedake: AD 2018",
          "0.4 Ma BP",
          "AD 1996",
          "AD 2016[\u2020 23]",
          "0.3 Ma BP",
          "8.2 ka BP[\u2020 24]",
          "8.1-8 ka BP[\u2020 24]",
          "0.5 Ma BP[\u2020 24]",
          "0.4 Ma BP",
          "AD 867",
          "AD 1996",
          "2.0-1.9 ka BP",
          "Late Pleistocene",
          "0.2 Ma BP",
          "30-20 ka BP",
          "AD 1924",
          "AD 1968",
          "Satsuma-I\u014djima: AD 2013",
          "0.84-0.78 ka BP",
          "AD 2018",
          "Mt. Otake: AD 1914",
          "AD 2016",
          "younger than 10 ka BP"
        ],
        "Region": [
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Hokkaido",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Honshu",
          "Izu_Islands",
          "Izu_Islands",
          "Izu_Islands",
          "Izu_Islands",
          "Izu_Islands",
          "Izu_Islands",
          "Izu_Islands",
          "Izu_Islands",
          "Izu_Islands",
          "Izu_Islands",
          "Izu_Islands",
          "Izu_Islands",
          "Izu_Islands",
          "Izu_Islands",
          "Ogasawara_Archipelago",
          "Ogasawara_Archipelago",
          "Ogasawara_Archipelago",
          "Ogasawara_Archipelago",
          "Ogasawara_Archipelago",
          "Ogasawara_Archipelago",
          "Ogasawara_Archipelago",
          "Ogasawara_Archipelago",
          "Ogasawara_Archipelago",
          "Ogasawara_Archipelago",
          "Ogasawara_Archipelago",
          "Ogasawara_Archipelago",
          "Ogasawara_Archipelago",
          "Ogasawara_Archipelago",
          "Ogasawara_Archipelago",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Kyushu",
          "Ryukyu_Islands",
          "Ryukyu_Islands",
          "Ryukyu_Islands",
          "Ryukyu_Islands",
          "Ryukyu_Islands",
          "Ryukyu_Islands",
          "Ryukyu_Islands",
          "Ryukyu_Islands",
          "Ryukyu_Islands",
          "Ryukyu_Islands",
          "Ryukyu_Islands"
        ],
        "geometry": [
          "{\"type\": \"Point\", \"coordinates\": [140.817, 43.083]}",
          "{\"type\": \"Point\", \"coordinates\": [144.438, 43.61]}",
          "{\"type\": \"Point\", \"coordinates\": [142.854, 43.663]}",
          "{\"type\": \"Point\", \"coordinates\": [141.285, 42.793]}",
          "{\"type\": \"Point\", \"coordinates\": [141.166, 41.804]}",
          "{\"type\": \"Point\", \"coordinates\": [144.11, 43.451]}",
          "{\"type\": \"Point\", \"coordinates\": [144.008, 43.386]}",
          "{\"type\": \"Point\", \"coordinates\": [144.163, 43.453]}",
          "{\"type\": \"Point\", \"coordinates\": [145.165, 44.131]}",
          "{\"type\": \"Point\", \"coordinates\": [144.336, 43.621]}",
          "{\"type\": \"Point\", \"coordinates\": [141.175, 42.499]}",
          "{\"type\": \"Point\", \"coordinates\": [144.565, 43.57]}",
          "{\"type\": \"Point\", \"coordinates\": [140.45, 42.12]}",
          "{\"type\": \"Point\", \"coordinates\": [143.036, 43.453]}",
          "{\"type\": \"Point\", \"coordinates\": [140.63, 42.88]}",
          "{\"type\": \"Point\", \"coordinates\": [145.013, 43.993]}",
          "{\"type\": \"Point\", \"coordinates\": [139.37, 41.5]}",
          "{\"type\": \"Point\", \"coordinates\": [145.122, 44.075]}",
          "{\"type\": \"Point\", \"coordinates\": [141.25, 45.18]}",
          "{\"type\": \"Point\", \"coordinates\": [143.096, 43.312]}",
          "{\"type\": \"Point\", \"coordinates\": [141.327, 42.753]}",
          "{\"type\": \"Point\", \"coordinates\": [140.916, 42.767]}",
          "{\"type\": \"Point\", \"coordinates\": [140.851, 42.602]}",
          "{\"type\": \"Point\", \"coordinates\": [140.677, 42.063]}",
          "{\"type\": \"Point\", \"coordinates\": [144.717, 43.765]}",
          "{\"type\": \"Point\", \"coordinates\": [145.273, 44.235]}",
          "{\"type\": \"Point\", \"coordinates\": [141.376, 42.69]}",
          "{\"type\": \"Point\", \"coordinates\": [145.086, 44.044]}",
          "{\"type\": \"Point\", \"coordinates\": [143.15, 43.516]}",
          "{\"type\": \"Point\", \"coordinates\": [142.849, 43.527]}",
          "{\"type\": \"Point\", \"coordinates\": [144.876, 43.876]}",
          "{\"type\": \"Point\", \"coordinates\": [140.839, 42.543]}",
          "{\"type\": \"Point\", \"coordinates\": [140.811, 42.826]}",
          "{\"type\": \"Point\", \"coordinates\": [131.6, 34.5]}",
          "{\"type\": \"Point\", \"coordinates\": [140.28, 37.62]}",
          "{\"type\": \"Point\", \"coordinates\": [139.18, 36.53]}",
          "{\"type\": \"Point\", \"coordinates\": [137.572, 36.2]}",
          "{\"type\": \"Point\", \"coordinates\": [140.8, 39.75]}",
          "{\"type\": \"Point\", \"coordinates\": [140.757, 39.964]}",
          "{\"type\": \"Point\", \"coordinates\": [139.002, 34.862]}",
          "{\"type\": \"Point\", \"coordinates\": [131.791, 34.461]}",
          "{\"type\": \"Point\", \"coordinates\": [139.234, 37.343]}",
          "{\"type\": \"Point\", \"coordinates\": [138.522, 36.405]}",
          "{\"type\": \"Point\", \"coordinates\": [138.8, 35.219]}",
          "{\"type\": \"Point\", \"coordinates\": [140.25, 37.73]}",
          "{\"type\": \"Point\", \"coordinates\": [138.413, 36.542]}",
          "{\"type\": \"Point\", \"coordinates\": [140.08, 37.6]}",
          "{\"type\": \"Point\", \"coordinates\": [140.03, 39.08]}",
          "{\"type\": \"Point\", \"coordinates\": [133.54, 35.371]}",
          "{\"type\": \"Point\", \"coordinates\": [138.839, 34.955]}",
          "{\"type\": \"Point\", \"coordinates\": [138.387, 36.433]}",
          "{\"type\": \"Point\", \"coordinates\": [138.731, 35.358]}",
          "{\"type\": \"Point\", \"coordinates\": [139.967, 37.246]}",
          "{\"type\": \"Point\", \"coordinates\": [140.027, 38.549]}",
          "{\"type\": \"Point\", \"coordinates\": [140.85, 39.95]}",
          "{\"type\": \"Point\", \"coordinates\": [140.93, 40.693]}",
          "{\"type\": \"Point\", \"coordinates\": [140.881, 40.656]}",
          "{\"type\": \"Point\", \"coordinates\": [139.02, 35.22]}",
          "{\"type\": \"Point\", \"coordinates\": [136.78, 36.15]}",
          "{\"type\": \"Point\", \"coordinates\": [138.88, 36.47]}",
          "{\"type\": \"Point\", \"coordinates\": [140.161, 38.608]}",
          "{\"type\": \"Point\", \"coordinates\": [139.28, 36.95]}",
          "{\"type\": \"Point\", \"coordinates\": [139.132, 36.805]}",
          "{\"type\": \"Point\", \"coordinates\": [140.62, 40.499]}",
          "{\"type\": \"Point\", \"coordinates\": [138.833, 36.956]}",
          "{\"type\": \"Point\", \"coordinates\": [138.136, 36.739]}",
          "{\"type\": \"Point\", \"coordinates\": [140.3, 40.65]}",
          "{\"type\": \"Point\", \"coordinates\": [139.12, 34.92]}",
          "{\"type\": \"Point\", \"coordinates\": [141.004, 39.85]}",
          "{\"type\": \"Point\", \"coordinates\": [134.675, 35.507]}",
          "{\"type\": \"Point\", \"coordinates\": [139.88, 39.93]}",
          "{\"type\": \"Point\", \"coordinates\": [138.196, 36.103]}",
          "{\"type\": \"Point\", \"coordinates\": [138.998, 36.591]}",
          "{\"type\": \"Point\", \"coordinates\": [138.12, 36.817]}",
          "{\"type\": \"Point\", \"coordinates\": [138.536, 35.803]}",
          "{\"type\": \"Point\", \"coordinates\": [140.78, 38.95]}",
          "{\"type\": \"Point\", \"coordinates\": [138.535, 36.619]}",
          "{\"type\": \"Point\", \"coordinates\": [138.937, 36.581]}",
          "{\"type\": \"Point\", \"coordinates\": [138.274, 36.837]}",
          "{\"type\": \"Point\", \"coordinates\": [138.222, 36.554]}",
          "{\"type\": \"Point\", \"coordinates\": [139.73, 39.95]}",
          "{\"type\": \"Point\", \"coordinates\": [140.544, 39.977]}",
          "{\"type\": \"Point\", \"coordinates\": [140.51, 38.75]}",
          "{\"type\": \"Point\", \"coordinates\": [141.052, 41.439]}",
          "{\"type\": \"Point\", \"coordinates\": [138.113, 36.891]}",
          "{\"type\": \"Point\", \"coordinates\": [138.684, 36.836]}",
          "{\"type\": \"Point\", \"coordinates\": [141.106, 40.069]}",
          "{\"type\": \"Point\", \"coordinates\": [139.486, 36.768]}",
          "{\"type\": \"Point\", \"coordinates\": [140.73, 38.73]}",
          "{\"type\": \"Point\", \"coordinates\": [139.97, 37.12]}",
          "{\"type\": \"Point\", \"coordinates\": [140.016, 37.615]}",
          "{\"type\": \"Point\", \"coordinates\": [138.036, 36.92]}",
          "{\"type\": \"Point\", \"coordinates\": [139.376, 36.798]}",
          "{\"type\": \"Point\", \"coordinates\": [140.867, 41.267]}",
          "{\"type\": \"Point\", \"coordinates\": [139.536, 36.811]}",
          "{\"type\": \"Point\", \"coordinates\": [137.55, 36.106]}",
          "{\"type\": \"Point\", \"coordinates\": [139.572, 37.454]}",
          "{\"type\": \"Point\", \"coordinates\": [133.324, 36.181]}",
          "{\"type\": \"Point\", \"coordinates\": [140.733, 40.567]}",
          "{\"type\": \"Point\", \"coordinates\": [139.507, 36.796]}",
          "{\"type\": \"Point\", \"coordinates\": [137.48, 35.89]}",
          "{\"type\": \"Point\", \"coordinates\": [140.69, 38.83]}",
          "{\"type\": \"Point\", \"coordinates\": [141.08, 41.32]}",
          "{\"type\": \"Point\", \"coordinates\": [132.62, 35.13]}",
          "{\"type\": \"Point\", \"coordinates\": [138.52, 36.7]}",
          "{\"type\": \"Point\", \"coordinates\": [139.337, 36.689]}",
          "{\"type\": \"Point\", \"coordinates\": [139.78, 36.9]}",
          "{\"type\": \"Point\", \"coordinates\": [134.919, 35.346]}",
          "{\"type\": \"Point\", \"coordinates\": [138.403, 36.799]}",
          "{\"type\": \"Point\", \"coordinates\": [137.6, 36.57]}",
          "{\"type\": \"Point\", \"coordinates\": [140.661, 39.722]}",
          "{\"type\": \"Point\", \"coordinates\": [142.686, 43.418]}",
          "{\"type\": \"Point\", \"coordinates\": [140.92, 40.47]}",
          "{\"type\": \"Point\", \"coordinates\": [137.594, 36.408]}",
          "{\"type\": \"Point\", \"coordinates\": [137.587, 36.226]}",
          "{\"type\": \"Point\", \"coordinates\": [140.829, 39.164]}",
          "{\"type\": \"Point\", \"coordinates\": [138.32, 36.088]}",
          "{\"type\": \"Point\", \"coordinates\": [138.37, 35.971]}",
          "{\"type\": \"Point\", \"coordinates\": [138.3, 36.1]}",
          "{\"type\": \"Point\", \"coordinates\": [140.45, 38.15]}",
          "{\"type\": \"Point\", \"coordinates\": [139.77, 32.45]}",
          "{\"type\": \"Point\", \"coordinates\": [139.918, 31.888]}",
          "{\"type\": \"Point\", \"coordinates\": [139.77, 33.13]}",
          "{\"type\": \"Point\", \"coordinates\": [139.394, 34.726]}",
          "{\"type\": \"Point\", \"coordinates\": [139.15, 34.22]}",
          "{\"type\": \"Point\", \"coordinates\": [139.68, 33.4]}",
          "{\"type\": \"Point\", \"coordinates\": [139.605, 33.871]}",
          "{\"type\": \"Point\", \"coordinates\": [139.526, 34.086]}",
          "{\"type\": \"Point\", \"coordinates\": [140.021, 31.918]}",
          "{\"type\": \"Point\", \"coordinates\": [139.27, 34.37]}",
          "{\"type\": \"Point\", \"coordinates\": [140.342, 29.793]}",
          "{\"type\": \"Point\", \"coordinates\": [140.05, 31.439]}",
          "{\"type\": \"Point\", \"coordinates\": [139.28, 34.52]}",
          "{\"type\": \"Point\", \"coordinates\": [140.302, 30.484]}",
          "{\"type\": \"Point\", \"coordinates\": [140.877, 27.274]}",
          "{\"type\": \"Point\", \"coordinates\": [141.485, 24.28]}",
          "{\"type\": \"Point\", \"coordinates\": [143.717, 21.767]}",
          "{\"type\": \"Point\", \"coordinates\": [141.238, 25.455]}",
          "{\"type\": \"Point\", \"coordinates\": [141.28, 25.43]}",
          "{\"type\": \"Point\", \"coordinates\": [141.315, 24.781]}",
          "{\"type\": \"Point\", \"coordinates\": [141.098, 26.127]}",
          "{\"type\": \"Point\", \"coordinates\": [140.929, 26.667]}",
          "{\"type\": \"Point\", \"coordinates\": [142.717, 22.767]}",
          "{\"type\": \"Point\", \"coordinates\": [143.633, 21.6]}",
          "{\"type\": \"Point\", \"coordinates\": [141.419, 24.414]}",
          "{\"type\": \"Point\", \"coordinates\": [141.935, 23.5]}",
          "{\"type\": \"Point\", \"coordinates\": [142.3, 23.083]}",
          "{\"type\": \"Point\", \"coordinates\": [140.566, 28.317]}",
          "{\"type\": \"Point\", \"coordinates\": [140.633, 28.6]}",
          "{\"type\": \"Point\", \"coordinates\": [130.705, 31.656]}",
          "{\"type\": \"Point\", \"coordinates\": [130.683, 31.359]}",
          "{\"type\": \"Point\", \"coordinates\": [130.783, 31.666]}",
          "{\"type\": \"Point\", \"coordinates\": [131.106, 32.881]}",
          "{\"type\": \"Point\", \"coordinates\": [128.861, 32.653]}",
          "{\"type\": \"Point\", \"coordinates\": [131.601, 33.583]}",
          "{\"type\": \"Point\", \"coordinates\": [130.57, 31.22]}",
          "{\"type\": \"Point\", \"coordinates\": [130.564, 31.235]}",
          "{\"type\": \"Point\", \"coordinates\": [130.461, 31.822]}",
          "{\"type\": \"Point\", \"coordinates\": [130.528, 31.18]}",
          "{\"type\": \"Point\", \"coordinates\": [130.764, 32.0455]}",
          "{\"type\": \"Point\", \"coordinates\": [130.639, 32.814]}",
          "{\"type\": \"Point\", \"coordinates\": [130.864, 31.931]}",
          "{\"type\": \"Point\", \"coordinates\": [130.976, 31.99]}",
          "{\"type\": \"Point\", \"coordinates\": [131.251, 33.083]}",
          "{\"type\": \"Point\", \"coordinates\": [130.657, 31.585]}",
          "{\"type\": \"Point\", \"coordinates\": [129.063, 33.201]}",
          "{\"type\": \"Point\", \"coordinates\": [130.591, 31.771]}",
          "{\"type\": \"Point\", \"coordinates\": [130.564, 31.775]}",
          "{\"type\": \"Point\", \"coordinates\": [131.524, 33.252]}",
          "{\"type\": \"Point\", \"coordinates\": [130.071, 32.974]}",
          "{\"type\": \"Point\", \"coordinates\": [131.432, 33.28]}",
          "{\"type\": \"Point\", \"coordinates\": [130.294, 32.757]}",
          "{\"type\": \"Point\", \"coordinates\": [131.39, 33.282]}",
          "{\"type\": \"Point\", \"coordinates\": [129.6, 29.45]}",
          "{\"type\": \"Point\", \"coordinates\": [129.541, 29.903]}",
          "{\"type\": \"Point\", \"coordinates\": [129.625, 29.879]}",
          "{\"type\": \"Point\", \"coordinates\": [124.0, 24.558]}",
          "{\"type\": \"Point\", \"coordinates\": [128.224, 27.877]}",
          "{\"type\": \"Point\", \"coordinates\": [130.308, 30.789]}",
          "{\"type\": \"Point\", \"coordinates\": [129.924, 29.967]}",
          "{\"type\": \"Point\", \"coordinates\": [130.22, 30.43]}",
          "{\"type\": \"Point\", \"coordinates\": [129.856, 29.859]}",
          "{\"type\": \"Point\", \"coordinates\": [129.714, 29.638]}",
          "{\"type\": \"Point\", \"coordinates\": [128.996, 28.799]}"
        ]
      },
      "mapping": {},
      "tooltips": {
        "formats": [],
        "lines": [
          "Elevation | @Elevation_meters(m)/@Elevation_ft(ft)"
        ],
        "tooltip_color": "black",
        "variables": [
          "Name",
          "Region",
          "Last_eruption"
        ]
      },
      "data_meta": {
        "geodataframe": {
          "geometry": "geometry"
        }
      },
      "fill": "#75DFBD",
      "color": "white",
      "shape": 24,
      "size": 14
    }
  ]
}            
        """.trimIndent()

        return parsePlotSpec(spec)
    }

    private fun titanic(): MutableMap<String, Any> {
        val spec = """
{
    'ggsize': {'width': 800, 'height': 300},
    'kind': 'plot',
    'layers': [
    {
        'geom': 'livemap',
        'tiles': {'kind': 'raster_zxy',
        'url': 'https://cartocdn_c.global.ssl.fastly.net/base-antique/{z}/{x}/{y}@2x.png',
        'attribution': '<a href=\"https://www.openstreetmap.org/copyright\">© OpenStreetMap contributors</a> <a href=\"https://carto.com/attributions#basemaps\">© CARTO</a>, <a href=\"https://carto.com/attributions\">© CARTO</a>'},
        'geocoding': {'url': 'http://10.0.0.127:3020/map_data/geocoding'}
    },
    {
        'geom': 'path',
        'map': {
            'geometry': [
                '{\"type\": \"LineString\", \"coordinates\": [[-1.40253666522018, 50.9183686226606], [-1.60901494773099, 49.6272752434015], [-8.29427875578403, 51.8531472980976], [-38.056641, 46.920255]]}'
            ]
        },
        'color': 'dark-blue',
        'linetype': 'dotted',
        'size': 1.2,
        'map_data_meta': {'geodataframe': {'geometry': 'geometry'}}
   },
   {
        'geom': 'segment',
        'x': -38.056641,
        'y': 46.920255,
        'xend': -73.8673749469137,
        'yend': 40.6847005337477,
        'color': 'gray',
        'linetype': 'dotted',
        'size': 1.2
  },
  {
        'geom': 'point',
        'x': -73.8673749469137,
        'y': 40.6847005337477,
        'size': 7,
        'shape': 21,
        'color': 'black',
        'fill': 'white'
  },
  {
        'geom': 'point',
        'x': -38.056641,
        'y': 46.920255,
        'size': 10,
        'shape': 9,
        'color': 'red'
  },
  {
        'geom': 'point',
        'map': {
            'city': ['Southampton', 'Cherbourg', 'Cobh'],
            'found name': ['Southampton', 'Cherbourg', 'Cobh'],
            'geometry': [
                '{\"type\": \"Point\", \"coordinates\": [-1.40253666522018, 50.9183686226606]}', 
                '{\"type\": \"Point\", \"coordinates\": [-1.60901494773099, 49.6272752434015]}', 
                '{\"type\": \"Point\", \"coordinates\": [-8.29427875578403, 51.8531472980976]}'
            ]
        },
        'size': 7,
        'shape': 21,
        'color': 'black',
        'fill': 'yellow',
        'map_data_meta': { 'geodataframe': { 'geometry': 'geometry' } }
  }  
]}"""
        return parsePlotSpec(spec)
    }

    private fun airports(): MutableMap<String, Any> {
        val spec = """
{
  "mapping": {},
  "data_meta": {},
  "theme": {
    "legend_position": "none"
  },
  "ggsize": {
    "width": 900,
    "height": 520
  },
  "kind": "plot",
  "scales": [
    {
      "aesthetic": "fill",
      "values": [
        "#30a2da",
        "#fc4f30"
      ]
    },
    {
      "aesthetic": "size",
      "trans": "sqrt",
      "range": [
        10,
        40
      ]
    }
  ],
  "layers": [
    {
      "geom": "livemap",
      "mapping": {},
      "data_meta": {},
      "location": {
        "type": "coordinates",
        "data": [
          26.65,
          38.61
        ]
      },
      "zoom": 6,
      "data_size_zoomin": -1,
      "tiles": {
        "kind": "raster_zxy",
        "url": "https://gibs.earthdata.nasa.gov/wmts/epsg3857/best/VIIRS_CityLights_2012/default//GoogleMapsCompatible_Level8/{z}/{y}/{x}.jpg",
        "attribution": "<a href=\"https://earthdata.nasa.gov/eosdis/science-system-description/eosdis-components/gibs\">\u00a9 NASA Global Imagery Browse Services (GIBS)</a>",
        "max_zoom": 8
      },
      "geocoding": {
        "url": "http://10.0.0.127:3020/map_data/geocoding"
      }
    },
    {
      "geom": "polygon",
      "data": {
        "country": [
          "GR",
          "TR"
        ],
        "found name": [
          "\u0395\u03bb\u03bb\u03ac\u03c2",
          "T\u00fcrkiye"
        ],
        "geometry": [
          "{\"type\": \"MultiPolygon\", \"coordinates\": [[[[23.73046875, 37.9961626797281], [23.5546875, 37.857507156252], [23.5546875, 37.9961626797281], [23.37890625, 37.9961626797281], [23.203125, 37.9961626797281], [23.203125, 37.857507156252], [23.203125, 37.7185903255881], [23.203125, 37.5794125134384], [23.37890625, 37.5794125134384], [23.5546875, 37.5794125134384], [23.5546875, 37.4399740522706], [23.37890625, 37.4399740522706], [23.203125, 37.4399740522706], [23.203125, 37.3002752813443], [23.02734375, 37.3002752813443], [23.02734375, 37.4399740522706], [23.02734375, 37.5794125134384], [22.8515625, 37.5794125134384], [22.67578125, 37.5794125134384], [22.67578125, 37.4399740522706], [22.8515625, 37.4399740522706], [22.8515625, 37.3002752813443], [22.8515625, 37.1603165467368], [23.02734375, 37.1603165467368], [23.02734375, 37.0200982013681], [23.02734375, 36.8796206050268], [23.02734375, 36.7388841243943], [23.02734375, 36.5978891330702], [22.8515625, 36.5978891330702], [22.8515625, 36.7388841243943], [22.67578125, 36.7388841243943], [22.5, 36.7388841243943], [22.5, 36.5978891330702], [22.5, 36.4566360115962], [22.32421875, 36.4566360115962], [22.32421875, 36.5978891330702], [22.32421875, 36.7388841243943], [22.32421875, 36.8796206050268], [22.1484375, 36.8796206050268], [22.1484375, 37.0200982013681], [21.97265625, 37.0200982013681], [21.97265625, 36.8796206050268], [21.97265625, 36.7388841243943], [21.796875, 36.7388841243943], [21.796875, 36.8796206050268], [21.62109375, 36.8796206050268], [21.62109375, 37.0200982013681], [21.62109375, 37.1603165467368], [21.62109375, 37.3002752813443], [21.62109375, 37.4399740522706], [21.62109375, 37.5794125134384], [21.4453125, 37.5794125134384], [21.4453125, 37.7185903255881], [21.26953125, 37.7185903255881], [21.26953125, 37.857507156252], [21.09375, 37.857507156252], [21.26953125, 37.9961626797281], [21.26953125, 38.1345565770541], [21.4453125, 38.1345565770541], [21.4453125, 38.272688535981], [21.4453125, 38.4105582509461], [21.26953125, 38.4105582509461], [21.26953125, 38.272688535981], [21.09375, 38.272688535981], [21.09375, 38.4105582509461], [21.09375, 38.5481654230466], [21.09375, 38.685509760012], [20.91796875, 38.685509760012], [20.91796875, 38.8225909761771], [20.7421875, 38.8225909761771], [20.7421875, 38.9594087924542], [20.7421875, 39.0959629363055], [20.56640625, 39.0959629363055], [20.56640625, 39.2322531417149], [20.390625, 39.2322531417149], [20.21484375, 39.3682791491601], [20.21484375, 39.5040407055842], [20.21484375, 39.6395375643667], [20.21484375, 39.7747694852955], [20.390625, 39.7747694852955], [20.390625, 39.9097362345372], [20.390625, 40.0444375846086], [20.56640625, 40.0444375846086], [20.7421875, 40.0444375846086], [20.7421875, 40.178873314347], [20.7421875, 40.3130432088809], [20.7421875, 40.4469470596005], [20.91796875, 40.4469470596005], [20.91796875, 40.5805846641276], [21.09375, 40.5805846641276], [21.09375, 40.713955826286], [20.91796875, 40.713955826286], [20.91796875, 40.8470603560712], [21.09375, 40.8470603560712], [21.26953125, 40.8470603560712], [21.4453125, 40.8470603560712], [21.62109375, 40.8470603560712], [21.62109375, 40.9798980696201], [21.796875, 40.9798980696201], [21.97265625, 40.9798980696201], [21.97265625, 41.1124687891809], [22.1484375, 41.1124687891809], [22.32421875, 41.1124687891809], [22.5, 41.1124687891809], [22.67578125, 41.1124687891809], [22.67578125, 41.2447723430821], [22.67578125, 41.3768085657023], [22.8515625, 41.3768085657023], [23.02734375, 41.3768085657023], [23.203125, 41.3768085657023], [23.37890625, 41.3768085657023], [23.5546875, 41.3768085657023], [23.73046875, 41.3768085657023], [23.90625, 41.3768085657023], [23.90625, 41.5085772974393], [24.08203125, 41.5085772974393], [24.2578125, 41.5085772974393], [24.43359375, 41.5085772974393], [24.609375, 41.5085772974393], [24.609375, 41.3768085657023], [24.78515625, 41.3768085657023], [24.9609375, 41.3768085657023], [25.13671875, 41.3768085657023], [25.13671875, 41.2447723430821], [25.3125, 41.2447723430821], [25.48828125, 41.2447723430821], [25.48828125, 41.3768085657023], [25.6640625, 41.3768085657023], [25.83984375, 41.3768085657023], [26.015625, 41.3768085657023], [26.19140625, 41.3768085657023], [26.19140625, 41.5085772974393], [26.19140625, 41.6400783846789], [26.015625, 41.6400783846789], [26.015625, 41.7713116797641], [26.19140625, 41.7713116797641], [26.3671875, 41.7713116797641], [26.3671875, 41.6400783846789], [26.54296875, 41.6400783846789], [26.54296875, 41.5085772974393], [26.54296875, 41.3768085657023], [26.54296875, 41.2447723430821], [26.3671875, 41.2447723430821], [26.3671875, 41.1124687891809], [26.3671875, 40.9798980696201], [26.3671875, 40.8470603560712], [26.19140625, 40.8470603560712], [26.19140625, 40.713955826286], [26.015625, 40.713955826286], [26.015625, 40.8470603560712], [25.83984375, 40.8470603560712], [25.6640625, 40.8470603560712], [25.48828125, 40.8470603560712], [25.3125, 40.8470603560712], [25.3125, 40.9798980696201], [25.13671875, 40.9798980696201], [24.9609375, 40.9798980696201], [24.9609375, 40.8470603560712], [24.78515625, 40.8470603560712], [24.78515625, 40.713955826286], [24.78515625, 40.5805846641276], [24.609375, 40.5805846641276], [24.43359375, 40.5805846641276], [24.43359375, 40.713955826286], [24.609375, 40.713955826286], [24.609375, 40.8470603560712], [24.609375, 40.9798980696201], [24.43359375, 40.9798980696201], [24.43359375, 40.8470603560712], [24.2578125, 40.8470603560712], [24.2578125, 40.713955826286], [24.08203125, 40.713955826286], [23.90625, 40.713955826286], [23.90625, 40.8470603560712], [23.73046875, 40.713955826286], [23.73046875, 40.5805846641276], [23.90625, 40.5805846641276], [23.90625, 40.4469470596005], [24.08203125, 40.4469470596005], [24.08203125, 40.3130432088809], [23.90625, 40.3130432088809], [23.73046875, 40.3130432088809], [23.73046875, 40.178873314347], [23.90625, 40.178873314347], [23.90625, 40.0444375846086], [23.73046875, 40.0444375846086], [23.73046875, 39.9097362345372], [23.5546875, 39.9097362345372], [23.37890625, 39.9097362345372], [23.37890625, 40.0444375846086], [23.37890625, 40.178873314347], [23.203125, 40.178873314347], [23.203125, 40.3130432088809], [23.02734375, 40.3130432088809], [22.8515625, 40.4469470596005], [23.02734375, 40.5805846641276], [22.8515625, 40.5805846641276], [22.67578125, 40.5805846641276], [22.67578125, 40.4469470596005], [22.67578125, 40.3130432088809], [22.5, 40.178873314347], [22.5, 40.0444375846086], [22.67578125, 40.0444375846086], [22.67578125, 39.9097362345372], [22.8515625, 39.9097362345372], [22.8515625, 39.7747694852955], [22.8515625, 39.6395375643667], [23.02734375, 39.6395375643667], [23.02734375, 39.5040407055842], [23.203125, 39.5040407055842], [23.203125, 39.3682791491601], [23.203125, 39.2322531417149], [23.37890625, 39.2322531417149], [23.5546875, 39.2322531417149], [23.73046875, 39.2322531417149], [23.73046875, 39.0959629363055], [23.5546875, 39.0959629363055], [23.37890625, 39.0959629363055], [23.37890625, 38.9594087924542], [23.37890625, 38.8225909761771], [23.203125, 38.8225909761771], [23.02734375, 38.8225909761771], [22.8515625, 38.8225909761771], [22.8515625, 38.685509760012], [23.02734375, 38.685509760012], [23.203125, 38.685509760012], [23.37890625, 38.685509760012], [23.37890625, 38.5481654230466], [23.5546875, 38.5481654230466], [23.5546875, 38.685509760012], [23.73046875, 38.8225909761771], [23.73046875, 38.685509760012], [23.90625, 38.685509760012], [24.08203125, 38.685509760012], [24.08203125, 38.5481654230466], [24.2578125, 38.5481654230466], [24.2578125, 38.4105582509461], [24.2578125, 38.272688535981], [24.08203125, 38.272688535981], [24.08203125, 38.1345565770541], [24.08203125, 37.9961626797281], [24.08203125, 37.857507156252], [24.08203125, 37.7185903255881], [23.90625, 37.7185903255881], [23.90625, 37.857507156252], [23.73046875, 37.857507156252], [23.73046875, 37.9961626797281]], [[24.08203125, 38.272688535981], [24.08203125, 38.4105582509461], [23.90625, 38.4105582509461], [23.73046875, 38.4105582509461], [23.73046875, 38.272688535981], [23.90625, 38.272688535981], [24.08203125, 38.272688535981]], [[23.02734375, 39.0959629363055], [23.02734375, 38.9594087924542], [23.203125, 38.9594087924542], [23.203125, 39.0959629363055], [23.02734375, 39.0959629363055]], [[23.02734375, 39.0959629363055], [23.02734375, 39.2322531417149], [22.8515625, 39.2322531417149], [22.8515625, 39.0959629363055], [23.02734375, 39.0959629363055]], [[22.67578125, 38.8225909761771], [22.67578125, 38.9594087924542], [22.5, 38.9594087924542], [22.5, 38.8225909761771], [22.67578125, 38.8225909761771]], [[23.02734375, 37.9961626797281], [23.203125, 38.1345565770541], [23.02734375, 38.272688535981], [22.8515625, 38.272688535981], [22.67578125, 38.272688535981], [22.67578125, 38.4105582509461], [22.5, 38.272688535981], [22.5, 38.4105582509461], [22.32421875, 38.4105582509461], [22.1484375, 38.4105582509461], [21.97265625, 38.4105582509461], [21.796875, 38.4105582509461], [21.796875, 38.272688535981], [21.97265625, 38.272688535981], [22.1484375, 38.272688535981], [22.1484375, 38.1345565770541], [22.32421875, 38.1345565770541], [22.5, 38.1345565770541], [22.67578125, 38.1345565770541], [22.67578125, 37.9961626797281], [22.8515625, 37.9961626797281], [23.02734375, 37.9961626797281]]], [[[25.3125, 35.0299963690257], [25.13671875, 34.8859309407531], [24.9609375, 34.8859309407531], [24.78515625, 34.8859309407531], [24.78515625, 35.0299963690257], [24.609375, 35.0299963690257], [24.609375, 35.1738083179996], [24.43359375, 35.1738083179996], [24.2578125, 35.1738083179996], [24.08203125, 35.1738083179996], [23.90625, 35.1738083179996], [23.90625, 35.3173663292379], [23.73046875, 35.3173663292379], [23.73046875, 35.1738083179996], [23.5546875, 35.1738083179996], [23.5546875, 35.3173663292379], [23.5546875, 35.4606699514953], [23.73046875, 35.4606699514953], [23.73046875, 35.6037187406973], [23.90625, 35.6037187406973], [23.90625, 35.4606699514953], [24.08203125, 35.4606699514953], [24.08203125, 35.6037187406973], [24.2578125, 35.6037187406973], [24.2578125, 35.4606699514953], [24.2578125, 35.3173663292379], [24.43359375, 35.3173663292379], [24.609375, 35.3173663292379], [24.609375, 35.4606699514953], [24.78515625, 35.4606699514953], [24.9609375, 35.4606699514953], [25.13671875, 35.4606699514953], [24.9609375, 35.3173663292379], [25.13671875, 35.3173663292379], [25.3125, 35.3173663292379], [25.48828125, 35.3173663292379], [25.6640625, 35.3173663292379], [25.6640625, 35.1738083179996], [25.83984375, 35.1738083179996], [26.015625, 35.1738083179996], [26.19140625, 35.1738083179996], [26.3671875, 35.1738083179996], [26.19140625, 35.0299963690257], [26.015625, 35.0299963690257], [25.83984375, 35.0299963690257], [25.6640625, 35.0299963690257], [25.48828125, 35.0299963690257], [25.3125, 35.0299963690257]]], [[[27.24609375, 35.6037187406973], [27.24609375, 35.4606699514953], [27.0703125, 35.4606699514953], [27.0703125, 35.6037187406973], [27.0703125, 35.7465122599185], [27.24609375, 35.7465122599185], [27.24609375, 35.6037187406973]]], [[[23.02734375, 36.1733569352216], [22.8515625, 36.1733569352216], [22.8515625, 36.3151251474805], [23.02734375, 36.3151251474805], [23.02734375, 36.1733569352216]]], [[[28.125, 36.1733569352216], [28.125, 36.0313317763319], [27.94921875, 36.0313317763319], [27.94921875, 35.8890500793609], [27.7734375, 35.8890500793609], [27.7734375, 36.0313317763319], [27.7734375, 36.1733569352216], [27.7734375, 36.3151251474805], [27.94921875, 36.3151251474805], [28.125, 36.4566360115962], [28.30078125, 36.4566360115962], [28.30078125, 36.3151251474805], [28.125, 36.3151251474805], [28.125, 36.1733569352216]]], [[[27.7734375, 36.5978891330702], [27.94921875, 36.5978891330702], [27.94921875, 36.4566360115962], [27.7734375, 36.4566360115962], [27.7734375, 36.5978891330702]]], [[[26.71875, 37.7185903255881], [26.71875, 37.857507156252], [26.89453125, 37.857507156252], [27.0703125, 37.857507156252], [27.0703125, 37.7185903255881], [26.89453125, 37.7185903255881], [26.89453125, 37.5794125134384], [26.71875, 37.5794125134384], [26.71875, 37.7185903255881]]], [[[25.13671875, 37.4399740522706], [25.3125, 37.4399740522706], [25.13671875, 37.3002752813443], [25.13671875, 37.4399740522706]]], [[[27.24609375, 36.8796206050268], [27.24609375, 36.7388841243943], [27.0703125, 36.7388841243943], [27.0703125, 36.8796206050268], [26.89453125, 36.8796206050268], [26.89453125, 37.0200982013681], [27.0703125, 37.0200982013681], [27.24609375, 37.0200982013681], [27.24609375, 36.8796206050268]]], [[[24.609375, 36.7388841243943], [24.43359375, 36.8796206050268], [24.609375, 36.8796206050268], [24.609375, 36.7388841243943]]], [[[24.609375, 37.0200982013681], [24.78515625, 37.0200982013681], [24.78515625, 36.8796206050268], [24.609375, 36.8796206050268], [24.609375, 37.0200982013681]]], [[[25.6640625, 36.7388841243943], [25.6640625, 36.8796206050268], [25.83984375, 36.7388841243943], [25.6640625, 36.7388841243943]]], [[[25.83984375, 36.3151251474805], [25.6640625, 36.3151251474805], [25.6640625, 36.4566360115962], [25.83984375, 36.4566360115962], [25.83984375, 36.3151251474805]]], [[[23.02734375, 36.5978891330702], [23.203125, 36.5978891330702], [23.203125, 36.4566360115962], [23.02734375, 36.4566360115962], [23.02734375, 36.5978891330702]]], [[[24.43359375, 36.5978891330702], [24.2578125, 36.5978891330702], [24.2578125, 36.7388841243943], [24.43359375, 36.7388841243943], [24.43359375, 36.5978891330702]]], [[[25.48828125, 37.0200982013681], [25.3125, 37.0200982013681], [25.13671875, 37.0200982013681], [25.13671875, 37.1603165467368], [25.3125, 37.1603165467368], [25.48828125, 37.1603165467368], [25.6640625, 37.1603165467368], [25.6640625, 37.0200982013681], [25.48828125, 37.0200982013681]]], [[[19.6875, 39.6395375643667], [19.6875, 39.7747694852955], [19.86328125, 39.7747694852955], [19.86328125, 39.6395375643667], [19.6875, 39.6395375643667]]], [[[25.3125, 39.9097362345372], [25.3125, 39.7747694852955], [25.13671875, 39.7747694852955], [24.9609375, 39.7747694852955], [25.13671875, 39.9097362345372], [24.9609375, 40.0444375846086], [25.13671875, 40.0444375846086], [25.3125, 40.0444375846086], [25.48828125, 40.0444375846086], [25.48828125, 39.9097362345372], [25.3125, 39.9097362345372]]], [[[24.08203125, 40.3130432088809], [24.2578125, 40.3130432088809], [24.2578125, 40.178873314347], [24.08203125, 40.3130432088809]]], [[[19.86328125, 39.5040407055842], [20.0390625, 39.5040407055842], [20.0390625, 39.3682791491601], [19.86328125, 39.3682791491601], [19.86328125, 39.5040407055842]]], [[[24.609375, 38.8225909761771], [24.43359375, 38.8225909761771], [24.43359375, 38.9594087924542], [24.609375, 38.9594087924542], [24.609375, 38.8225909761771]]], [[[26.54296875, 39.0959629363055], [26.54296875, 38.9594087924542], [26.3671875, 38.9594087924542], [26.19140625, 38.9594087924542], [26.19140625, 39.0959629363055], [26.3671875, 39.0959629363055], [26.3671875, 39.2322531417149], [26.54296875, 39.2322531417149], [26.54296875, 39.0959629363055]]], [[[26.19140625, 39.0959629363055], [26.015625, 39.0959629363055], [25.83984375, 39.0959629363055], [25.83984375, 39.2322531417149], [26.015625, 39.2322531417149], [26.015625, 39.3682791491601], [26.19140625, 39.3682791491601], [26.3671875, 39.3682791491601], [26.3671875, 39.2322531417149], [26.19140625, 39.2322531417149], [26.19140625, 39.0959629363055]]], [[[26.3671875, 37.7185903255881], [26.3671875, 37.5794125134384], [26.19140625, 37.5794125134384], [26.19140625, 37.7185903255881], [26.3671875, 37.7185903255881]]], [[[24.9609375, 37.7185903255881], [25.13671875, 37.7185903255881], [25.13671875, 37.5794125134384], [24.9609375, 37.5794125134384], [24.9609375, 37.7185903255881]]], [[[24.2578125, 37.5794125134384], [24.2578125, 37.7185903255881], [24.43359375, 37.7185903255881], [24.43359375, 37.5794125134384], [24.2578125, 37.5794125134384]]], [[[20.7421875, 37.857507156252], [20.91796875, 37.857507156252], [20.91796875, 37.7185903255881], [20.91796875, 37.5794125134384], [20.7421875, 37.5794125134384], [20.7421875, 37.7185903255881], [20.7421875, 37.857507156252]]], [[[24.78515625, 37.857507156252], [24.9609375, 37.857507156252], [24.9609375, 37.7185903255881], [24.78515625, 37.7185903255881], [24.78515625, 37.857507156252]]], [[[26.015625, 38.272688535981], [26.015625, 38.1345565770541], [25.83984375, 38.1345565770541], [25.83984375, 38.272688535981], [26.015625, 38.272688535981]]], [[[20.7421875, 38.272688535981], [20.7421875, 38.1345565770541], [20.56640625, 38.1345565770541], [20.390625, 38.1345565770541], [20.390625, 38.272688535981], [20.56640625, 38.272688535981], [20.56640625, 38.4105582509461], [20.56640625, 38.5481654230466], [20.56640625, 38.685509760012], [20.56640625, 38.8225909761771], [20.7421875, 38.8225909761771], [20.7421875, 38.685509760012], [20.7421875, 38.5481654230466], [20.7421875, 38.4105582509461], [20.7421875, 38.272688535981]]], [[[24.78515625, 37.9961626797281], [24.78515625, 37.857507156252], [24.609375, 37.857507156252], [24.609375, 37.9961626797281], [24.78515625, 37.9961626797281]]], [[[23.5546875, 37.857507156252], [23.37890625, 37.857507156252], [23.37890625, 37.9961626797281], [23.5546875, 37.857507156252]]], [[[23.37890625, 38.8225909761771], [23.5546875, 38.8225909761771], [23.5546875, 38.685509760012], [23.37890625, 38.685509760012], [23.37890625, 38.8225909761771]]], [[[24.2578125, 38.1345565770541], [24.43359375, 38.1345565770541], [24.609375, 38.1345565770541], [24.609375, 37.9961626797281], [24.43359375, 37.9961626797281], [24.2578125, 37.9961626797281], [24.2578125, 38.1345565770541]]], [[[26.015625, 38.5481654230466], [26.19140625, 38.5481654230466], [26.19140625, 38.4105582509461], [26.19140625, 38.272688535981], [26.015625, 38.272688535981], [26.015625, 38.4105582509461], [25.83984375, 38.4105582509461], [25.83984375, 38.5481654230466], [26.015625, 38.5481654230466]]]]}",
          "{\"type\": \"MultiPolygon\", \"coordinates\": [[[[36.5625, 36.3151251474805], [36.73828125, 36.3151251474805], [36.73828125, 36.1733569352216], [36.5625, 36.1733569352216], [36.38671875, 36.1733569352216], [36.38671875, 36.0313317763319], [36.2109375, 36.0313317763319], [36.2109375, 35.8890500793609], [36.03515625, 35.8890500793609], [35.859375, 35.8890500793609], [35.859375, 36.0313317763319], [35.859375, 36.1733569352216], [35.859375, 36.3151251474805], [35.859375, 36.4566360115962], [36.03515625, 36.4566360115962], [36.03515625, 36.5978891330702], [36.2109375, 36.5978891330702], [36.2109375, 36.7388841243943], [36.2109375, 36.8796206050268], [36.03515625, 36.8796206050268], [35.859375, 36.8796206050268], [35.859375, 36.7388841243943], [35.68359375, 36.7388841243943], [35.68359375, 36.5978891330702], [35.5078125, 36.5978891330702], [35.33203125, 36.5978891330702], [35.15625, 36.5978891330702], [35.15625, 36.7388841243943], [34.98046875, 36.7388841243943], [34.8046875, 36.7388841243943], [34.8046875, 36.8796206050268], [34.62890625, 36.7388841243943], [34.453125, 36.7388841243943], [34.453125, 36.5978891330702], [34.27734375, 36.5978891330702], [34.27734375, 36.4566360115962], [34.1015625, 36.4566360115962], [34.1015625, 36.3151251474805], [33.92578125, 36.3151251474805], [33.75, 36.3151251474805], [33.75, 36.1733569352216], [33.57421875, 36.1733569352216], [33.3984375, 36.1733569352216], [33.22265625, 36.1733569352216], [33.046875, 36.1733569352216], [33.046875, 36.0313317763319], [32.87109375, 36.1733569352216], [32.87109375, 36.0313317763319], [32.6953125, 36.0313317763319], [32.51953125, 36.0313317763319], [32.51953125, 36.1733569352216], [32.34375, 36.1733569352216], [32.34375, 36.3151251474805], [32.16796875, 36.3151251474805], [32.16796875, 36.4566360115962], [31.9921875, 36.4566360115962], [31.9921875, 36.5978891330702], [31.81640625, 36.5978891330702], [31.640625, 36.5978891330702], [31.640625, 36.7388841243943], [31.46484375, 36.7388841243943], [31.2890625, 36.7388841243943], [31.2890625, 36.8796206050268], [31.11328125, 36.8796206050268], [30.9375, 36.8796206050268], [30.76171875, 36.8796206050268], [30.5859375, 36.8796206050268], [30.5859375, 36.7388841243943], [30.5859375, 36.5978891330702], [30.5859375, 36.4566360115962], [30.41015625, 36.4566360115962], [30.41015625, 36.3151251474805], [30.234375, 36.3151251474805], [30.05859375, 36.3151251474805], [30.05859375, 36.1733569352216], [29.8828125, 36.1733569352216], [29.70703125, 36.1733569352216], [29.53125, 36.1733569352216], [29.35546875, 36.1733569352216], [29.35546875, 36.3151251474805], [29.1796875, 36.3151251474805], [29.1796875, 36.4566360115962], [29.1796875, 36.5978891330702], [29.00390625, 36.5978891330702], [28.828125, 36.7388841243943], [28.65234375, 36.7388841243943], [28.65234375, 36.8796206050268], [28.4765625, 36.8796206050268], [28.4765625, 36.7388841243943], [28.30078125, 36.7388841243943], [28.125, 36.7388841243943], [28.125, 36.5978891330702], [27.94921875, 36.5978891330702], [27.94921875, 36.7388841243943], [27.94921875, 36.8796206050268], [28.125, 36.8796206050268], [28.125, 37.0200982013681], [27.94921875, 37.0200982013681], [27.7734375, 37.0200982013681], [27.59765625, 37.0200982013681], [27.421875, 37.0200982013681], [27.24609375, 37.0200982013681], [27.24609375, 37.1603165467368], [27.421875, 37.1603165467368], [27.421875, 37.3002752813443], [27.421875, 37.4399740522706], [27.24609375, 37.4399740522706], [27.24609375, 37.5794125134384], [27.0703125, 37.5794125134384], [27.0703125, 37.7185903255881], [27.24609375, 37.7185903255881], [27.24609375, 37.857507156252], [27.24609375, 37.9961626797281], [27.0703125, 37.9961626797281], [26.89453125, 38.1345565770541], [26.71875, 38.1345565770541], [26.54296875, 38.1345565770541], [26.54296875, 38.272688535981], [26.3671875, 38.1345565770541], [26.3671875, 38.272688535981], [26.3671875, 38.4105582509461], [26.3671875, 38.5481654230466], [26.3671875, 38.685509760012], [26.54296875, 38.685509760012], [26.54296875, 38.5481654230466], [26.71875, 38.5481654230466], [26.71875, 38.685509760012], [26.89453125, 38.685509760012], [26.89453125, 38.8225909761771], [27.0703125, 38.8225909761771], [27.0703125, 38.9594087924542], [26.89453125, 38.9594087924542], [26.71875, 38.9594087924542], [26.71875, 39.0959629363055], [26.71875, 39.2322531417149], [26.54296875, 39.2322531417149], [26.54296875, 39.3682791491601], [26.71875, 39.3682791491601], [26.89453125, 39.3682791491601], [26.89453125, 39.5040407055842], [26.71875, 39.5040407055842], [26.54296875, 39.5040407055842], [26.3671875, 39.5040407055842], [26.19140625, 39.5040407055842], [26.015625, 39.5040407055842], [26.19140625, 39.6395375643667], [26.19140625, 39.7747694852955], [26.19140625, 39.9097362345372], [26.19140625, 40.0444375846086], [26.19140625, 40.178873314347], [26.3671875, 40.178873314347], [26.19140625, 40.3130432088809], [26.3671875, 40.3130432088809], [26.3671875, 40.4469470596005], [26.54296875, 40.4469470596005], [26.71875, 40.4469470596005], [26.71875, 40.5805846641276], [26.54296875, 40.5805846641276], [26.3671875, 40.5805846641276], [26.19140625, 40.5805846641276], [26.015625, 40.5805846641276], [26.015625, 40.713955826286], [26.19140625, 40.713955826286], [26.19140625, 40.8470603560712], [26.3671875, 40.8470603560712], [26.3671875, 40.9798980696201], [26.3671875, 41.1124687891809], [26.3671875, 41.2447723430821], [26.54296875, 41.2447723430821], [26.54296875, 41.3768085657023], [26.54296875, 41.5085772974393], [26.54296875, 41.6400783846789], [26.3671875, 41.6400783846789], [26.3671875, 41.7713116797641], [26.54296875, 41.7713116797641], [26.54296875, 41.9022770409637], [26.54296875, 42.0329743324414], [26.71875, 42.0329743324414], [26.89453125, 42.0329743324414], [27.0703125, 42.0329743324414], [27.24609375, 42.0329743324414], [27.421875, 42.0329743324414], [27.421875, 41.9022770409637], [27.59765625, 41.9022770409637], [27.59765625, 42.0329743324414], [27.7734375, 42.0329743324414], [27.94921875, 42.0329743324414], [28.125, 41.9022770409637], [27.94921875, 41.9022770409637], [27.94921875, 41.7713116797641], [28.125, 41.7713116797641], [28.125, 41.6400783846789], [28.125, 41.5085772974393], [28.30078125, 41.5085772974393], [28.4765625, 41.3768085657023], [28.65234375, 41.3768085657023], [28.828125, 41.3768085657023], [28.828125, 41.2447723430821], [29.00390625, 41.2447723430821], [29.1796875, 41.2447723430821], [29.35546875, 41.2447723430821], [29.53125, 41.1124687891809], [29.70703125, 41.2447723430821], [29.70703125, 41.1124687891809], [29.8828125, 41.1124687891809], [30.05859375, 41.1124687891809], [30.234375, 41.1124687891809], [30.234375, 41.2447723430821], [30.41015625, 41.2447723430821], [30.41015625, 41.1124687891809], [30.5859375, 41.1124687891809], [30.76171875, 41.1124687891809], [30.9375, 41.1124687891809], [31.11328125, 41.1124687891809], [31.2890625, 41.1124687891809], [31.46484375, 41.2447723430821], [31.46484375, 41.3768085657023], [31.640625, 41.3768085657023], [31.81640625, 41.3768085657023], [31.81640625, 41.5085772974393], [31.9921875, 41.5085772974393], [32.16796875, 41.6400783846789], [32.34375, 41.7713116797641], [32.51953125, 41.7713116797641], [32.6953125, 41.7713116797641], [32.6953125, 41.9022770409637], [32.87109375, 41.9022770409637], [33.046875, 41.9022770409637], [33.22265625, 41.9022770409637], [33.22265625, 42.0329743324414], [33.3984375, 42.0329743324414], [33.57421875, 42.0329743324414], [33.75, 42.0329743324414], [33.92578125, 42.0329743324414], [34.1015625, 42.0329743324414], [34.1015625, 41.9022770409637], [34.27734375, 41.9022770409637], [34.453125, 41.9022770409637], [34.62890625, 41.9022770409637], [34.8046875, 41.9022770409637], [34.8046875, 42.0329743324414], [34.98046875, 42.0329743324414], [35.15625, 42.0329743324414], [35.15625, 41.9022770409637], [35.15625, 41.7713116797641], [35.33203125, 41.7713116797641], [35.33203125, 41.6400783846789], [35.5078125, 41.6400783846789], [35.68359375, 41.6400783846789], [35.859375, 41.6400783846789], [36.03515625, 41.7713116797641], [36.03515625, 41.6400783846789], [36.2109375, 41.5085772974393], [36.2109375, 41.3768085657023], [36.38671875, 41.3768085657023], [36.38671875, 41.2447723430821], [36.5625, 41.2447723430821], [36.5625, 41.3768085657023], [36.73828125, 41.3768085657023], [36.9140625, 41.3768085657023], [36.9140625, 41.2447723430821], [37.08984375, 41.2447723430821], [37.08984375, 41.1124687891809], [37.265625, 41.1124687891809], [37.44140625, 41.1124687891809], [37.44140625, 40.9798980696201], [37.6171875, 40.9798980696201], [37.6171875, 41.1124687891809], [37.79296875, 41.1124687891809], [37.79296875, 40.9798980696201], [37.96875, 40.9798980696201], [38.14453125, 40.9798980696201], [38.3203125, 40.9798980696201], [38.49609375, 40.9798980696201], [38.671875, 40.9798980696201], [38.84765625, 40.9798980696201], [39.0234375, 40.9798980696201], [39.19921875, 41.1124687891809], [39.375, 41.1124687891809], [39.55078125, 41.1124687891809], [39.55078125, 40.9798980696201], [39.7265625, 40.9798980696201], [39.90234375, 40.9798980696201], [40.078125, 40.9798980696201], [40.078125, 40.8470603560712], [40.25390625, 40.9798980696201], [40.4296875, 40.9798980696201], [40.60546875, 40.9798980696201], [40.60546875, 41.1124687891809], [40.78125, 41.1124687891809], [40.95703125, 41.2447723430821], [41.1328125, 41.2447723430821], [41.30859375, 41.3768085657023], [41.484375, 41.3768085657023], [41.484375, 41.5085772974393], [41.66015625, 41.5085772974393], [41.8359375, 41.5085772974393], [42.01171875, 41.5085772974393], [42.1875, 41.5085772974393], [42.36328125, 41.5085772974393], [42.36328125, 41.3768085657023], [42.5390625, 41.3768085657023], [42.5390625, 41.5085772974393], [42.5390625, 41.6400783846789], [42.71484375, 41.6400783846789], [42.890625, 41.6400783846789], [42.890625, 41.5085772974393], [42.890625, 41.3768085657023], [43.06640625, 41.3768085657023], [43.2421875, 41.3768085657023], [43.2421875, 41.2447723430821], [43.41796875, 41.2447723430821], [43.41796875, 41.1124687891809], [43.41796875, 40.9798980696201], [43.59375, 40.9798980696201], [43.59375, 40.8470603560712], [43.76953125, 40.8470603560712], [43.76953125, 40.713955826286], [43.76953125, 40.5805846641276], [43.59375, 40.5805846641276], [43.59375, 40.4469470596005], [43.59375, 40.3130432088809], [43.59375, 40.178873314347], [43.59375, 40.0444375846086], [43.76953125, 40.0444375846086], [43.9453125, 40.0444375846086], [44.12109375, 40.0444375846086], [44.296875, 40.0444375846086], [44.47265625, 40.0444375846086], [44.47265625, 39.9097362345372], [44.6484375, 39.9097362345372], [44.6484375, 39.7747694852955], [44.47265625, 39.7747694852955], [44.47265625, 39.6395375643667], [44.47265625, 39.5040407055842], [44.47265625, 39.3682791491601], [44.296875, 39.3682791491601], [44.12109375, 39.3682791491601], [44.12109375, 39.2322531417149], [44.12109375, 39.0959629363055], [44.12109375, 38.9594087924542], [44.296875, 38.9594087924542], [44.296875, 38.8225909761771], [44.296875, 38.685509760012], [44.296875, 38.5481654230466], [44.296875, 38.4105582509461], [44.47265625, 38.4105582509461], [44.47265625, 38.272688535981], [44.47265625, 38.1345565770541], [44.296875, 38.1345565770541], [44.296875, 37.9961626797281], [44.296875, 37.857507156252], [44.47265625, 37.857507156252], [44.47265625, 37.7185903255881], [44.6484375, 37.7185903255881], [44.6484375, 37.5794125134384], [44.6484375, 37.4399740522706], [44.6484375, 37.3002752813443], [44.82421875, 37.3002752813443], [44.82421875, 37.1603165467368], [44.6484375, 37.1603165467368], [44.47265625, 37.1603165467368], [44.47265625, 37.0200982013681], [44.296875, 37.0200982013681], [44.12109375, 37.0200982013681], [44.12109375, 37.1603165467368], [44.296875, 37.1603165467368], [44.296875, 37.3002752813443], [44.12109375, 37.3002752813443], [43.9453125, 37.3002752813443], [43.9453125, 37.1603165467368], [43.76953125, 37.1603165467368], [43.76953125, 37.3002752813443], [43.59375, 37.1603165467368], [43.59375, 37.3002752813443], [43.41796875, 37.3002752813443], [43.2421875, 37.3002752813443], [43.06640625, 37.3002752813443], [42.890625, 37.3002752813443], [42.890625, 37.4399740522706], [42.71484375, 37.4399740522706], [42.71484375, 37.3002752813443], [42.71484375, 37.1603165467368], [42.5390625, 37.1603165467368], [42.36328125, 37.1603165467368], [42.36328125, 37.3002752813443], [42.1875, 37.3002752813443], [42.1875, 37.1603165467368], [42.01171875, 37.1603165467368], [41.8359375, 37.1603165467368], [41.66015625, 37.1603165467368], [41.484375, 37.1603165467368], [41.484375, 37.0200982013681], [41.30859375, 37.0200982013681], [41.1328125, 37.0200982013681], [41.1328125, 37.1603165467368], [40.95703125, 37.1603165467368], [40.78125, 37.1603165467368], [40.60546875, 37.1603165467368], [40.60546875, 37.0200982013681], [40.4296875, 37.0200982013681], [40.25390625, 37.0200982013681], [40.25390625, 36.8796206050268], [40.078125, 36.8796206050268], [39.90234375, 36.8796206050268], [39.90234375, 36.7388841243943], [39.7265625, 36.7388841243943], [39.55078125, 36.7388841243943], [39.375, 36.7388841243943], [39.19921875, 36.7388841243943], [39.0234375, 36.7388841243943], [38.84765625, 36.7388841243943], [38.671875, 36.7388841243943], [38.671875, 36.8796206050268], [38.49609375, 36.8796206050268], [38.3203125, 36.8796206050268], [38.14453125, 36.8796206050268], [37.96875, 36.8796206050268], [37.96875, 36.7388841243943], [37.79296875, 36.7388841243943], [37.6171875, 36.7388841243943], [37.44140625, 36.7388841243943], [37.44140625, 36.5978891330702], [37.265625, 36.5978891330702], [37.08984375, 36.5978891330702], [37.08984375, 36.7388841243943], [36.9140625, 36.7388841243943], [36.73828125, 36.7388841243943], [36.5625, 36.7388841243943], [36.5625, 36.5978891330702], [36.5625, 36.4566360115962], [36.5625, 36.3151251474805]], [[26.71875, 38.5481654230466], [26.71875, 38.4105582509461], [26.89453125, 38.4105582509461], [26.89453125, 38.5481654230466], [26.71875, 38.5481654230466]], [[26.3671875, 40.178873314347], [26.54296875, 40.178873314347], [26.54296875, 40.3130432088809], [26.3671875, 40.178873314347]], [[26.71875, 40.5805846641276], [26.89453125, 40.5805846641276], [26.89453125, 40.713955826286], [26.71875, 40.713955826286], [26.71875, 40.5805846641276]], [[26.89453125, 40.5805846641276], [26.89453125, 40.4469470596005], [27.0703125, 40.4469470596005], [27.24609375, 40.4469470596005], [27.24609375, 40.3130432088809], [27.421875, 40.3130432088809], [27.59765625, 40.3130432088809], [27.7734375, 40.3130432088809], [27.94921875, 40.3130432088809], [28.125, 40.3130432088809], [28.125, 40.4469470596005], [28.30078125, 40.4469470596005], [28.4765625, 40.4469470596005], [28.65234375, 40.3130432088809], [28.65234375, 40.4469470596005], [28.828125, 40.4469470596005], [28.828125, 40.5805846641276], [29.00390625, 40.5805846641276], [29.1796875, 40.5805846641276], [29.1796875, 40.713955826286], [29.35546875, 40.713955826286], [29.35546875, 40.8470603560712], [29.1796875, 40.8470603560712], [29.1796875, 40.9798980696201], [29.00390625, 40.9798980696201], [28.828125, 40.9798980696201], [28.65234375, 40.9798980696201], [28.4765625, 40.9798980696201], [28.30078125, 41.1124687891809], [28.125, 41.1124687891809], [28.125, 40.9798980696201], [27.94921875, 40.9798980696201], [27.7734375, 40.9798980696201], [27.59765625, 40.9798980696201], [27.421875, 40.9798980696201], [27.421875, 40.8470603560712], [27.421875, 40.713955826286], [27.24609375, 40.713955826286], [27.24609375, 40.5805846641276], [27.0703125, 40.5805846641276], [26.89453125, 40.5805846641276]], [[28.828125, 40.4469470596005], [29.00390625, 40.3130432088809], [29.00390625, 40.4469470596005], [28.828125, 40.4469470596005]], [[29.53125, 40.713955826286], [29.70703125, 40.713955826286], [29.53125, 40.8470603560712], [29.53125, 40.713955826286]]], [[[44.6484375, 39.7747694852955], [44.82421875, 39.7747694852955], [44.82421875, 39.6395375643667], [44.6484375, 39.6395375643667], [44.6484375, 39.7747694852955]]], [[[25.6640625, 40.178873314347], [25.83984375, 40.178873314347], [26.015625, 40.178873314347], [25.83984375, 40.0444375846086], [25.6640625, 40.0444375846086], [25.6640625, 40.178873314347]]], [[[27.94921875, 40.4469470596005], [27.7734375, 40.4469470596005], [27.59765625, 40.4469470596005], [27.421875, 40.4469470596005], [27.421875, 40.5805846641276], [27.59765625, 40.5805846641276], [27.59765625, 40.713955826286], [27.7734375, 40.713955826286], [27.7734375, 40.5805846641276], [27.94921875, 40.5805846641276], [27.94921875, 40.4469470596005]]]]}"
        ]
      },
      "mapping": {
        "fill": "country"
      },
      "data_meta": {
        "geodataframe": {
          "geometry": "geometry"
        }
      },
      "alpha": 0.2
    },
    {
      "geom": "point",
      "data": {
        "IATA": [
          "ATH",
          "HER",
          "SKG",
          "RHO",
          "CFU",
          "CHQ",
          "KGS",
          "JTR",
          "ZTH",
          "JMK",
          "ISL",
          "SAW",
          "AYT",
          "ADB",
          "ESB"
        ],
        "city": [
          "Athens",
          "Heraklion",
          "Thessaloniki",
          "Rhodes",
          "Corfu",
          "Chania",
          "Kos",
          "Santorini",
          "Zakinthos",
          "Mikonos",
          "Istanbul",
          "Istanbul",
          "Antalya",
          "Izmir",
          "Ankara"
        ],
        "latitude": [
          37.9354,
          35.34,
          40.523,
          36.4041,
          39.6067,
          35.5335,
          36.8,
          36.3987,
          37.753,
          37.4342,
          40.983,
          40.9053,
          36.9032,
          38.2932,
          40.1243
        ],
        "longitude": [
          23.9437,
          25.1753,
          22.9767,
          28.0898,
          19.9133,
          24.1499,
          27.089,
          25.4793,
          20.885,
          25.3484,
          28.8104,
          29.3172,
          30.8008,
          27.1516,
          32.9918
        ],
        "passengers": [
          24.13,
          8.0,
          6.67,
          5.57,
          3.36,
          3.0,
          2.67,
          2.26,
          1.8,
          1.4,
          67.98,
          34.13,
          31.7,
          13.41,
          16.74
        ],
        "country": [
          "GR",
          "GR",
          "GR",
          "GR",
          "GR",
          "GR",
          "GR",
          "GR",
          "GR",
          "GR",
          "TR",
          "TR",
          "TR",
          "TR",
          "TR"
        ]
      },
      "mapping": {
        "x": "longitude",
        "y": "latitude",
        "fill": "country",
        "size": "passengers"
      },
      "tooltips": {
        "formats": [
          {
            "field": "passengers",
            "format": "{.1f} m"
          },
          {
            "field": "^x",
            "format": ".2f"
          },
          {
            "field": "^y",
            "format": ".2f"
          }
        ],
        "lines": [
          "@|@IATA",
          "Passengers|@passengers",
          "City|@city",
          "Country|@country",
          "Longitude|^x",
          "Latitude|^y"
        ]
      },
      "data_meta": {},
      "shape": 21,
      "alpha": 0.7,
      "color": "white"
    }
  ]
}

        """.trimMargin()

        return parsePlotSpec(spec)
    }

    private fun georeference() : MutableMap<String, Any> {
        val spec = """
            |{
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "livemap",
            |      "tiles": {
            |        "kind": "vector_lets_plot",
            |        "url": "wss://tiles.datalore.jetbrains.com",
            |        "theme": "color",
            |        "attribution": "Map: <a href=\"https://github.com/JetBrains/lets-plot\">\u00a9 Lets-Plot</a>, map data: <a href=\"https://www.openstreetmap.org/copyright\">\u00a9 OpenStreetMap contributors</a>."
            |      },
            |      "geocoding": {
            |        "url": "http://10.0.0.127:3020/map_data/geocoding"
            |      }
            |    },
            |    {
            |      "geom": "polygon",
            |      "map": {
            |        "id": ["148838", "1428125"],
            |        "country": ["usa", "canada"],
            |        "found name": ["United States", "Canada"],
            |        "centroid": [[-99.7426055742426, 37.2502586245537], [-110.450525298983, 56.8387750536203]],
            |        "position": [
            |          [-124.733375608921, 25.1162923872471, -66.9498561322689, 49.3844716250896],
            |          [-141.002660393715, 41.6765552759171, -55.6205673515797, 72.0015004277229]
            |        ],
            |        "limit": [
            |          [144.618412256241, -14.3740922212601, -64.564847946167, 71.3878083229065],
            |          [-141.002660393715, 41.6765552759171, -52.6194141805172, 83.1445701420307]
            |        ]
            |      },
            |      "fill": "orange",
            |      "map_data_meta": {
            |        "georeference": {}
            |      }
            |    }
            |  ]
            |}
            |""".trimMargin()

        return parsePlotSpec(spec)
    }


    private fun blankPoint(): MutableMap<String, Any> {
        val spec = """{
            "kind": "plot",
            "layers": [
            {
            "geom": "point",
            "data": {},
            "mapping": {}
            }
            ]
            }""".trimIndent()

        return parsePlotSpec(spec)
    }

    private fun pieWithNullValuesInData(): MutableMap<String, Any> {
        val spec = """
            {
              "kind": "plot",
              "layers": [
                {
                  "geom": "livemap",
                  "data": {
                    "States": [
                      "Alabama", "Alabama", "Alabama", 
                      "Alaska", "Alaska", "Alaska",
                      "Arizona", "Arizona", "Arizona",
                      "Arkansas", "Arkansas", "Arkansas"
                    ],
                    "Item": [
                      "State Debt", "Local Debt", "Gross State Product",
                      "State Debt", "Local Debt", "Gross State Product",
                      "State Debt", "Local Debt", "Gross State Product",
                      "State Debt", "Local Debt", "Gross State Product"
                    ],
                    "Values": [
                      10.7, 26.1, 228.0,
                      5.9, 3.5, 55.7,
                      34.9, 23.5, 355.7,
                      13.3, 30.5, 361.1
                    ]
                  },
                  "mapping": {
                    "sym_y": "Values",
                    "fill": "Item"
                  },
                  "map_data_meta": {
                    "geodataframe": {
                      "geometry": "geometry"
                    }
                  },
                  "map": {
                    "request": ["Alabama", "California", "Alaska", "Arizona", "Nevada"],
                    "found name": ["Alabama", "California", "Alaska", "Arizona", "Nevada"],
                    "geometry": [
                      "{\"type\": \"Point\", \"coordinates\": [-86.7421099329499, 32.6446247845888]}",
                      "{\"type\": \"Point\", \"coordinates\": [-119.994112927034, 37.277335524559]}",
                      "{\"type\": \"Point\", \"coordinates\": [-152.012666774028, 63.0759818851948]}",
                      "{\"type\": \"Point\", \"coordinates\": [-111.665190827228, 34.1682100296021]}",
                      "{\"type\": \"Point\", \"coordinates\": [-116.666956541192, 38.5030842572451]}"
                    ]
                  },
                  "map_join": [
                    ["States"],
                    ["request"]
                  ],
                  "display_mode": "pie",
                  "tiles": {
                    "kind": "vector_lets_plot",
                    "url": "wss://tiles.datalore.jetbrains.com",
                    "theme": "color",
                    "attribution": "Map: <a href=\"https://github.com/JetBrains/lets-plot\">\u00a9 Lets-Plot</a>, map data: <a href=\"https://www.openstreetmap.org/copyright\">\u00a9 OpenStreetMap contributors</a>."
                  },
                  "geocoding": {
                    "url": "http://172.31.52.145:3025"
                  },
                  "map_join": ["States", "state"]
                }
              ]
            }
        """.trimIndent()

        return parsePlotSpec(spec)
    }

    private fun blankMap(): MutableMap<String, Any> {
        val spec = """{
  "kind": "plot",
  "layers": [
    {
      "geom": "livemap",
      "data": {},
      "mapping": {},
      "tiles": {
        "kind": "vector_lets_plot",
        "url": "wss://tiles.datalore.jetbrains.com",
        "url": "wss://tiles.datalore.jetbrains.com",
        "theme": null,
        "attribution": "Map: <a href=\"https://github.com/JetBrains/lets-plot\">\u00a9 Lets-Plot</a>, map data: <a href=\"https://www.openstreetmap.org/copyright\">\u00a9 OpenStreetMap contributors</a>."
      },
      "geocoding": {
        "url": "http://localhost:3020"
      }
    }
  ]
}""".trimIndent()

        return parsePlotSpec(spec)
    }

    private fun barWithNanValuesInData(): MutableMap<String, Any> {
        val spec = """{
  "kind": "plot",
  "layers": [
    {
      "geom": "livemap",
      "data": {
        "x": [0, 0, 0, 10, 10, 10, 20, 20, 20],
        "y": [0, 0, 0, 10, 10, 10, 20, 20, 20],
        "z": [100, 200, 400, 144, null, 230, 123, 543, -231],
        "c": ['A', 'B', 'C', 'A', 'B', 'C', 'A', 'B', 'C']
      },
      "mapping": {
        "x": "x",
        "y": "y",
        "sym_y": "z",
        "fill": "c"
      },
      "display_mode": "bar",
      "tiles": {
        "kind": "vector_lets_plot",
        "url": "wss://tiles.datalore.jetbrains.com",
        "url": "wss://tiles.datalore.jetbrains.com",
        "theme": null,
        "attribution": "Map: <a href=\"https://github.com/JetBrains/lets-plot\">\u00a9 Lets-Plot</a>, map data: <a href=\"https://www.openstreetmap.org/copyright\">\u00a9 OpenStreetMap contributors</a>."
      },
      "geocoding": {
        "url": "http://localhost:3020"
      }
    }
  ]
}""".trimIndent()

        return parsePlotSpec(spec)
    }
    private fun pieWithNanValuesInData(): MutableMap<String, Any> {
        val spec = """{
  "kind": "plot",
  "layers": [
    {
      "geom": "livemap",
      "data": {
        "x": [0, 0, 0, 10, 10, 10, 20, 20, 20],
        "y": [0, 0, 0, 10, 10, 10, 20, 20, 20],
        "z": [100, 200, 400, 144, null, 230, 123, 543, -231],
        "c": ['A', 'B', 'C', 'A', 'B', 'C', 'A', 'B', 'C']
      },
      "mapping": {
        "x": "x",
        "y": "y",
        "sym_y": "z",
        "fill": "c"
      },
      "display_mode": "pie",
      "tiles": {
        "kind": "vector_lets_plot",
        "url": "wss://tiles.datalore.jetbrains.com",
        "url": "wss://tiles.datalore.jetbrains.com",
        "theme": null,
        "attribution": "Map: <a href=\"https://github.com/JetBrains/lets-plot\">\u00a9 Lets-Plot</a>, map data: <a href=\"https://www.openstreetmap.org/copyright\">\u00a9 OpenStreetMap contributors</a>."
      },
      "geocoding": {
        "url": "http://localhost:3020"
      }
    }
  ]
}""".trimIndent()

        return parsePlotSpec(spec)
    }

    private fun barWithNullValuesInData(): MutableMap<String, Any> {
        val spec = """
            {
              "kind": "plot",
              "layers": [
                {
                  "geom": "livemap",
                  "data": {
                    "States": [
                      "Alabama", "Alabama", "Alabama",
                      "Alaska", "Alaska", "Alaska",
                      "Arizona", "Arizona", "Arizona",
                      "Arkansas", "Arkansas", "Arkansas"
                    ],
                    "Item": [
                      "State Debt", "Local Debt", "Gross State Product",
                      "State Debt", "Local Debt", "Gross State Product",
                      "State Debt", "Local Debt", "Gross State Product",
                      "State Debt", "Local Debt", "Gross State Product"
                    ],
                    "Values": [
                      10.7, 26.1, 228.0,
                      5.9, 3.5, 55.7,
                      34.9, 23.5, 355.7,
                      13.3, 30.5, 361.1
                    ]
                  },
                  "mapping": {
                    "sym_y": "Values",
                    "fill": "Item"
                  },
                  "map_data_meta": {
                    "geodataframe": {
                      "geometry": "geometry"
                    }
                  },
                  "map": {
                    "request": ["Alabama", "California", "Alaska", "Arizona", "Nevada"],
                    "found name": ["Alabama", "California", "Alaska", "Arizona", "Nevada"],
                    "geometry": [
                      "{\"type\": \"Point\", \"coordinates\": [-86.7421099329499, 32.6446247845888]}",
                      "{\"type\": \"Point\", \"coordinates\": [-119.994112927034, 37.277335524559]}",
                      "{\"type\": \"Point\", \"coordinates\": [-152.012666774028, 63.0759818851948]}",
                      "{\"type\": \"Point\", \"coordinates\": [-111.665190827228, 34.1682100296021]}",
                      "{\"type\": \"Point\", \"coordinates\": [-116.666956541192, 38.5030842572451]}"
                    ]
                  },
                  "map_join": [
                    ["States"],
                    ["request"]
                  ],
                  "display_mode": "bar",
                  "tiles": {
                    "kind": "vector_lets_plot",
                    "url": "wss://tiles.datalore.jetbrains.com",
                    "theme": "color",
                    "attribution": "Map: <a href=\"https://github.com/JetBrains/lets-plot\">\u00a9 Lets-Plot</a>, map data: <a href=\"https://www.openstreetmap.org/copyright\">\u00a9 OpenStreetMap contributors</a>."
                  },
                  "geocoding": {
                    "url": "http://172.31.52.145:3025"
                  },
                  "map_join": ["States", "state"]
                }
              ]
            }
        """.trimIndent()

        return parsePlotSpec(spec)
    }

    private fun multiLayerTooltips(): MutableMap<String, Any> {
        val n = 10
        val rnd = Random(0)
        val data = """
            {
                "x": [${(0..n).map { rnd.nextDouble(-2.0, 2.0) }.joinToString()}],
                "y": [${(0..n).map { rnd.nextDouble(-2.0, 2.0) }.joinToString()}],
                "v": [${(0..n).map { rnd.nextDouble(0.0, 200_000.0) }.joinToString()}],
                "age": [${(0..n).map { rnd.nextInt(0, 70) }.joinToString()}]
            }
        """.trimIndent()

        val poly = """
            {
                "x": [-5.0, 5.0, 5.0, -5.0, -5.0],
                "y": [5.0, 5.0, -5.0, -5.0, 5.0]
            }
        """.trimIndent()

        val spec = """{
            "data": $data,
            "kind": "plot",
            "layers": [
                {
                    "geom": "livemap",
                    "tiles": {
                        "kind": "vector_lets_plot",
                        "url": "wss://tiles.datalore.jetbrains.com",
                        "theme": "dark",
                        "attribution": "Map data <a href=\"https://www.openstreetmap.org/copyright\">\u00a9 OpenStreetMap</a> contributors"
                    }
                },
                {
                    "geom": "polygon",
                    "data": $poly,
                    "mapping": { "x": "x", "y": "y" },
                    "fill": "#F8F4F0", 
                    "color": "#B71234",
                    "alpha": 0.5
                },
                {
                    "geom": "point",
                    "data": $data,
                    "mapping": { "x": "x", "y": "y", "size": "v", "color": "age" }
                }
            ]
        },
        """.trimIndent()

        return parsePlotSpec(spec)
    }

    private fun mapJoinBar(): MutableMap<String, Any> {
        val spec = """{
  "data": {
    "State": ["Alabama", "Alabama", "Alabama", "Alaska", "Alaska", "Alaska", "Arizona", "Arizona", "Arizona"],
    "Item": [ "State Debt", "Local Debt", "Gross State Product", "State Debt", "Local Debt", "Gross State Product", "State Debt", "Local Debt", "Gross State Product"],
    "$ B": [ 10.7, 26.1, 228.0, 5.9, 3.5, 55.7, 13.3, 30.5, 361.1]
  },
  "kind": "plot",
  "layers": [
    {
      "geom": "livemap",
      "mapping": {
        "sym_x": "Item",
        "sym_y": "$ B",
        "fill": "Item"
      },
      "map_data_meta": {
        "geodataframe": {
          "geometry": "geometry"
        }
      },
      "display_mode": "pie",
      "tiles": {
        "kind": "raster_zxy",
        "url": "https://[abc].tile.openstreetmap.org/{z}/{x}/{y}.png",
        "attribution": "<a href=\"https://www.openstreetmap.org/copyright\">© OpenStreetMap contributors</a>"
      },
      "geocoding": {
        "url": "https://geo2.datalore.jetbrains.com"
      },
      "map": {
        "State": [ "Alabama", "Alaska", "Arizona"],
        "Latitude": [ 32.806671, 61.370716, 33.729759],
        "Longitude": [ -86.79113000000001, -152.404419, -111.431221],
        "geometry": [
          "{\"type\": \"Point\", \"coordinates\": [-86.79113000000001, 32.806671]}",
          "{\"type\": \"Point\", \"coordinates\": [-152.404419, 61.370716]}",
          "{\"type\": \"Point\", \"coordinates\": [-111.431221, 33.729759]}"
        ]
      },
      "map_join": [["State"], ["State"]]
    }
  ]
}""".trimIndent()

        return parsePlotSpec(spec)
    }


    private fun antiMeridian(): MutableMap<String, Any> {
        val spec = """{
  "data": null,
  "mapping": {
    "x": null,
    "y": null
  },
  "data_meta": {},
  "theme": {
    "axis_title": null,
    "axis_title_x": null,
    "axis_title_y": null,
    "axis_text": null,
    "axis_text_x": null,
    "axis_text_y": null,
    "axis_ticks": null,
    "axis_ticks_x": null,
    "axis_ticks_y": null,
    "axis_line": null,
    "axis_line_x": null,
    "axis_line_y": null,
    "legend_position": "none",
    "legend_justification": null,
    "legend_direction": null,
    "axis_tooltip": null,
    "axis_tooltip_x": null,
    "axis_tooltip_y": null
  },
  "kind": "plot",
  "scales": [],
  "layers": [
    {
      "geom": "livemap",
      "stat": null,
      "data": null,
      "mapping": {
        "x": null,
        "y": null
      },
      "position": null,
      "show_legend": null,
      "tooltips": null,
      "data_meta": {},
      "sampling": null,
      "display_mode": null,
      "location": null,
      "zoom": null,
      "projection": null,
      "geodesic": null,
      "tiles": {
        "kind": "vector_lets_plot",
        "url": "wss://tiles.datalore.jetbrains.com",
        "theme": null
      },
      "geocoding": {}
    },
    {
      "geom": "rect",
      "stat": null,
      "data": {
        "request": [
          "Russia",
          "Russia",
          "USA",
          "USA"
        ],
        "lonmin": [
          19.6389412879944,
          -180.0,
          144.618412256241,
          -180.0
        ],
        "latmin": [
          41.1850968003273,
          41.1850968003273,
          -14.3735490739346,
          -14.3735490739346
        ],
        "lonmax": [
          180.0,
          -168.997978270054,
          180.0,
          -64.564847946167
        ],
        "latmax": [
          81.8587204813957,
          81.8587204813957,
          71.3878083229065,
          71.3878083229065
        ],
        "found name": [
          "\u0420\u043e\u0441\u0441\u0438\u044f",
          "\u0420\u043e\u0441\u0441\u0438\u044f",
          "United States of America",
          "United States of America"
        ]
      },
      "mapping": {
        "x": null,
        "y": null,
        "xmin": "lonmin",
        "xmax": "lonmax",
        "ymin": "latmin",
        "ymax": "latmax",
        "fill": "found name"
      },
      "position": null,
      "show_legend": null,
      "tooltips": null,
      "data_meta": {},
      "sampling": null,
      "map": null,
      "map_join": null,
      "alpha": 0.3
    }
  ]
}""".trimIndent()
        return parsePlotSpec(spec)
    }

    private fun tooltips(): MutableMap<String, Any> {
        val spec = """
            {
              "data": {
                "request": ["Texas", "Nevada", "Iowa"],
                "lon": [-99.6829525269137, -116.666956541192, -93.1514127397129],
                "lat": [31.1685702949762, 38.5030842572451, 41.9395130127668],
                "found name": ["Texas", "Nevada", "Iowa"]
              },
              "kind": "plot",
              "layers": [
                {
                  "geom": "livemap",
                  "tiles": {
                    "kind": "vector_lets_plot",
                    "url": "wss://tiles.datalore.jetbrains.com",
                    "theme": null
                  },
                  "geocoding": {}
                },
                {
                  "geom": "point",
                  "mapping": {
                    "x": "lon",
                    "y": "lat"
                  },
                  "tooltips": {
                    "lines": [
                        "^x"
                     ],
                     "formats": [
                        { "field": "^x", "format": "mean = {.4f}" }
                     ]
                  },
                  "symbol": "point",
                  "size": 50
                }
              ]
            }
        """.trimIndent()

        return parsePlotSpec(spec)
    }

    fun symbol_point(): MutableMap<String, Any> {
        val spec = """
                {
                  "data": {
                    "request": ["Texas", "Nevada", "Iowa"],
                    "lon": [-99.6829525269137, -116.666956541192, -93.1514127397129],
                    "lat": [31.1685702949762, 38.5030842572451, 41.9395130127668],
                    "found name": ["Texas", "Nevada", "Iowa"]
                  },
                  "kind": "plot",
                  "layers": [
                    {
                      "geom": "livemap",
                      "mapping": {
                        "x": "lon",
                        "y": "lat"
                      },
                      "symbol": "point",
                      "tiles": {
                        "kind": "vector_lets_plot",
                        "url": "wss://tiles.datalore.jetbrains.com",
                        "theme": null
                      },
                      "size": 50
                    }
                  ]
                }
            """.trimIndent()
        return parsePlotSpec(spec)
    }

    private fun geom_point(): MutableMap<String, Any> {
        val spec = """{
  "data": {
    "request": ["Texas", "Nevada", "Iowa"],
    "lon": [-99.6829525269137, -116.666956541192, -93.1514127397129],
    "lat": [31.1685702949762, 38.5030842572451, 41.9395130127668],
    "found name": ["Texas", "Nevada", "Iowa"]
  },
  "kind": "plot",
  "layers": [
    {
      "geom": "livemap",
      "tiles": {
        "kind": "vector_lets_plot",
        "url": "wss://tiles.datalore.jetbrains.com",
        "theme": null
      }
    },
    {
      "geom": "point",
      "mapping": {
        "x": "lon",
        "y": "lat"
      },
      "symbol": "point",
      "size": 50
    }
  ]
}"""
        return parsePlotSpec(spec)
    }

    fun basic(): MutableMap<String, Any> {
        val spec = """
                {
                    "kind": "plot", 
                    "layers": [
                        {
                            "geom": "livemap",
                            "tiles": {"kind": "raster_zxy", "url": "https://a.tile.openstreetmap.org/{z}/{x}/{y}.png"}                            
                        }
                    ]
                }
            """.trimIndent()

        return parsePlotSpec(spec)
    }

    fun facetBars(): MutableMap<String, Any> {
        val spec = """{
                "ggtitle":{
                    "text":"Facet bars"
                },
                "data":{
                    "time":["Lunch", "Lunch", "Dinner", "Dinner", "Dinner"]
                },
                "facet":{
                    "name":"grid",
                    "x":"time",
                },
                "kind":"plot",
                "layers":[
                    {
                        "geom":"bar",
                        "mapping":{
                            "x":"time",
                            "fill":"time"
                        }
                    }
                ]
            }"""
        return parsePlotSpec(spec)
    }

    fun points(): MutableMap<String, Any> {
        val spec = """{
                "ggtitle": {"text": "Points on map"}, 
                "data": {"lon": [-100.420313, -91.016016], "lat": [34.835461, 38.843142], "clr": ["one", "two"]}, 
                "kind": "plot", 
                "layers": [
                    {
                        "geom": "livemap", 
                        "tiles": {"kind": "raster_zxy", "url": "https://a.tile.openstreetmap.org/{z}/{x}/{y}.png"}
                    }, 
                    {
                        "geom": "point", 
                        "mapping": {"x": "lon", "y": "lat", "color": "clr"}, 
                        "size": 20
                    }
                ]
            }""".trimMargin()

        return parsePlotSpec(spec)
    }

    fun bunch(): MutableMap<String, Any> {
        val spec = """{
                "kind": "ggbunch", 
                "items": [
                    {
                        "x": 0, 
                        "y": 0, 
                        "feature_spec": {
                            "kind": "plot", 
                            "layers": [
                                {
                                    "geom": "livemap", 
                                    "tiles": {"kind": "raster_zxy", "url": "https://a.tile.openstreetmap.org/{z}/{x}/{y}.png"}
                                }
                            ]
                        }
                    }, 
                    {
                        "x": 0, 
                        "y": 400, 
                        "feature_spec": {
                            "kind": "plot", 
                            "layers": [
                                {
                                    "geom": "livemap", 
                                    "tiles": {"kind": "raster_zxy", "url": "https://a.tile.openstreetmap.org/{z}/{x}/{y}.png"}
                                }
                            ]
                        }
                    }
                ]
            }""".trimIndent()

        return parsePlotSpec(spec)

    }

    fun facet(): MutableMap<String, Any> {
        val spec = """{
                "data":{
                    "lon":[
                        -100.420313,
                        -91.016016
                    ],
                    "lat":[
                        34.835461,
                        38.843142
                    ]
                },
                "facet":{
                    "name":"grid",
                    "x":"lat"
                },
                "ggtitle":{
                    "text":"Two points"
                },
                "kind":"plot",
                "layers":[
                    {
                        "geom":"livemap",
                        "mapping":{
                            "x":"lon",
                            "y":"lat",
                            "color":"lon"
                        },
                        "tiles": {"kind": "raster_zxy", "url": "https://a.tile.openstreetmap.org/{z}/{x}/{y}.png"}
                    }
                ]
            }"""
        return parsePlotSpec(spec)
    }

    fun pointsWithZoomAndLocation(): MutableMap<String, Any> {
        val spec = """{
                "ggtitle": {"text": "Points with zoom and location"}, 
                "data": {
                    "lon": [-100.420313, -91.016016], 
                    "lat": [34.835461, 38.843142], 
                    "clr": ["one", "two"]
                }, 
                "kind": "plot", 
                "layers": [
                    {
                        "geom": "livemap", 
                        "location": {
                            "type": "coordinates",
                            "data": [25.878516, 58.317548, 33.590918, 60.884144]
                        },
                        "zoom": 10 
                    }, 
                    {
                        "geom": "point", 
                        "mapping": {"x": "lon", "y": "lat", "color": "clr"}, 
                        "size": 20
                    }
                ]
            }""".trimMargin()

        return parsePlotSpec(spec)
    }

    fun setLocation(): MutableMap<String, Any> {
        val spec = """{
                "ggtitle": {"text": "Set location"}, 
                "kind": "plot",
                "layers": [
                    {
                        "geom": "livemap",
                        "location": {
                            "type": "coordinates",
                            "data": [25.878516, 58.317548, 33.590918, 60.884144]
                        },
                        "tiles": null
                    }
                ]
            }"""
        return parsePlotSpec(spec)
    }

    fun setZoom(): MutableMap<String, Any> {
        val spec = """{
                "ggtitle": {"text": "Set zoom and default location"}, 
                "kind": "plot",
                "layers": [
                    {
                        "geom": "livemap",
                        "zoom": 4,
                        "tiles": {"kind": "raster_zxy", "url": "https://a.tile.openstreetmap.org/{z}/{x}/{y}.png"}
                    }
                ]
            }"""
        return parsePlotSpec(spec)
    }

    fun wrongRasterTileUrl(): MutableMap<String, Any> {
        val spec = """{
                "ggtitle": {"text": "Wrong tile url"}, 
                "kind": "plot",
                "layers": [
                    {
                        "geom": "livemap",
                        "tiles": {"raster": "http://c.tile.stamen.com/tonerd/{x}/{y}.png"}
                    }
                ]
            }"""
        return parsePlotSpec(spec)
    }

    fun fourPointsTwoLayers(): MutableMap<String, Any> {
        val spec = """{

                "kind":"plot",
                "layers":[
                    {
                        "geom":"livemap",
                        "data":{
                            "x":[29.777834, 29.778033],
                            "y":[59.991666, 59.988106],
                            "lonlat":["29.777834,59.991666", "29.778033,59.988106"],
                            "label":["one", "two"]
                        },                        
                        "mapping":{
                            "x":"x",
                            "y":"y",
                            "color":"label"
                        },
                        "location": {"type": "coordinates", "data": [29.7, 60.02]},
                        "zoom": 10,
                        "display_mode":"point",
                        "tiles":{ "kind":"raster_zxy", "url":"https://a.tile.openstreetmap.org/{z}/{x}/{y}.png"},
                        "shape":19,
                        "size":21
                    },
                    {
                        "geom":"point",
                        "data":{
                            "lon":[29.703667, 29.72339, 29.75339, 29.77339, 29.79339, 29.81339],
                            "lat":[60.01668, 60.008983, 60.012983, 60.022983, 60.032983, 60.042983],
                            "label":["three", "four", "five", "six", "seve", "eight"]
                        },
                        "mapping":{
                            "x":"lon",
                            "y":"lat",
                            "color":"label"
                        },
                        "shape":19,
                        "size":21
                    }
                ]
            }
                """

        return parsePlotSpec(spec)
    }

    fun pointAndText(): MutableMap<String, Any> {
        val spec = """{
                "data":{
                    "x":[29.777834, 29.778033],
                    "y":[59.991666, 59.988106]
                },
                "kind":"plot",
                "layers":[
                    {
                        "geom":"livemap",
                        "tiles": {"kind": "raster_zxy", "url": "https://a.tile.openstreetmap.org/{z}/{x}/{y}.png"}
                    },
                    {
                        "geom":"point",
                        "mapping":{
                            "x":"x",
                            "y":"y",
                            "color":"x"
                        },
                        "shape":19,
                        "size":21
                    },
                    {
                        "geom":"text",
                        "data":{
                            "lon":[29.72339],
                            "lat":[60.008983],
                            "label":["Kotlin"]
                        },
                        "mapping":{
                            "x":"lon",
                            "y":"lat",
                            "label":"label"
                        },
                        "size":18,
                        "color":"#900090",
                        "family":"serif",
                        "fontface":"italic bold",
                        "hjust":"middle",
                        "vjust":"center",
                        "angle":30
                    }
                ]
            }"""
        return parsePlotSpec(spec)
    }

}
