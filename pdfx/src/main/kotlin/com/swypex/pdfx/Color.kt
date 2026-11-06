package com.swypex.pdfx

data class Color(val red: Int, val green: Int, val blue: Int) {
    init {
        require(listOf(red, green, blue).all { it in 0..255 })
    }

    // Tailwind style color shades.
    companion object {
        val Black = Color(22, 34, 54)
        val White = Color(255, 255, 255)
        val Blue = Color(35, 82, 187)
        val Red = Color(220, 38, 38)
        val Gray = Color(100, 116, 139)

        val Slate50 = Color(248, 250, 252)
        val Slate100 = Color(241, 245, 249)
        val Slate200 = Color(226, 232, 240)
        val Slate300 = Color(203, 213, 225)
        val Slate400 = Color(148, 163, 184)
        val Slate500 = Color(100, 116, 139)
        val Slate600 = Color(71, 85, 105)
        val Slate700 = Color(51, 65, 85)
        val Slate800 = Color(30, 41, 59)
        val Slate900 = Color(15, 23, 42)
        val Slate950 = Color(2, 6, 23)

        val Gray50 = Color(249, 250, 251)
        val Gray100 = Color(243, 244, 246)
        val Gray200 = Color(229, 231, 235)
        val Gray300 = Color(209, 213, 219)
        val Gray400 = Color(156, 163, 175)
        val Gray500 = Color(107, 114, 128)
        val Gray600 = Color(75, 85, 99)
        val Gray700 = Color(55, 65, 81)
        val Gray800 = Color(31, 41, 55)
        val Gray900 = Color(17, 24, 39)
        val Gray950 = Color(3, 7, 18)

        val Red50 = Color(254, 242, 242)
        val Red100 = Color(254, 226, 226)
        val Red200 = Color(254, 202, 202)
        val Red300 = Color(252, 165, 165)
        val Red400 = Color(248, 113, 113)
        val Red500 = Color(239, 68, 68)
        val Red600 = Color(220, 38, 38)
        val Red700 = Color(185, 28, 28)
        val Red800 = Color(153, 27, 27)
        val Red900 = Color(127, 29, 29)
        val Red950 = Color(69, 10, 10)

        val Orange50 = Color(255, 247, 237)
        val Orange100 = Color(255, 237, 213)
        val Orange200 = Color(254, 215, 170)
        val Orange300 = Color(253, 186, 116)
        val Orange400 = Color(251, 146, 60)
        val Orange500 = Color(249, 115, 22)
        val Orange600 = Color(234, 88, 12)
        val Orange700 = Color(194, 65, 12)
        val Orange800 = Color(154, 52, 18)
        val Orange900 = Color(124, 45, 18)
        val Orange950 = Color(67, 20, 7)

        val Amber50 = Color(255, 251, 235)
        val Amber100 = Color(254, 243, 199)
        val Amber200 = Color(253, 230, 138)
        val Amber300 = Color(252, 211, 77)
        val Amber400 = Color(251, 191, 36)
        val Amber500 = Color(245, 158, 11)
        val Amber600 = Color(217, 119, 6)
        val Amber700 = Color(180, 83, 9)
        val Amber800 = Color(146, 64, 14)
        val Amber900 = Color(120, 53, 15)
        val Amber950 = Color(69, 26, 3)

        val Yellow50 = Color(254, 252, 232)
        val Yellow100 = Color(254, 249, 195)
        val Yellow200 = Color(254, 240, 138)
        val Yellow300 = Color(253, 224, 71)
        val Yellow400 = Color(250, 204, 21)
        val Yellow500 = Color(234, 179, 8)
        val Yellow600 = Color(202, 138, 4)
        val Yellow700 = Color(161, 98, 7)
        val Yellow800 = Color(133, 77, 14)
        val Yellow900 = Color(113, 63, 18)
        val Yellow950 = Color(66, 32, 6)

        val Green50 = Color(240, 253, 244)
        val Green100 = Color(220, 252, 231)
        val Green200 = Color(187, 247, 208)
        val Green300 = Color(134, 239, 172)
        val Green400 = Color(74, 222, 128)
        val Green500 = Color(34, 197, 94)
        val Green600 = Color(22, 163, 74)
        val Green700 = Color(21, 128, 61)
        val Green800 = Color(22, 101, 52)
        val Green900 = Color(20, 83, 45)
        val Green950 = Color(5, 46, 22)

        val Teal50 = Color(240, 253, 250)
        val Teal100 = Color(204, 251, 241)
        val Teal200 = Color(153, 246, 228)
        val Teal300 = Color(94, 234, 212)
        val Teal400 = Color(45, 212, 191)
        val Teal500 = Color(20, 184, 166)
        val Teal600 = Color(13, 148, 136)
        val Teal700 = Color(15, 118, 110)
        val Teal800 = Color(17, 94, 89)
        val Teal900 = Color(19, 78, 74)
        val Teal950 = Color(4, 47, 46)

        val Cyan50 = Color(236, 254, 255)
        val Cyan100 = Color(207, 250, 254)
        val Cyan200 = Color(165, 243, 252)
        val Cyan300 = Color(103, 232, 249)
        val Cyan400 = Color(34, 211, 238)
        val Cyan500 = Color(6, 182, 212)
        val Cyan600 = Color(8, 145, 178)
        val Cyan700 = Color(14, 116, 144)
        val Cyan800 = Color(21, 94, 117)
        val Cyan900 = Color(22, 78, 99)
        val Cyan950 = Color(8, 51, 68)

        val Blue50 = Color(239, 246, 255)
        val Blue100 = Color(219, 234, 254)
        val Blue200 = Color(191, 219, 254)
        val Blue300 = Color(147, 197, 253)
        val Blue400 = Color(96, 165, 250)
        val Blue500 = Color(59, 130, 246)
        val Blue600 = Color(37, 99, 235)
        val Blue700 = Color(29, 78, 216)
        val Blue800 = Color(30, 64, 175)
        val Blue900 = Color(30, 58, 138)
        val Blue950 = Color(23, 37, 84)

        val Indigo50 = Color(238, 242, 255)
        val Indigo100 = Color(224, 231, 255)
        val Indigo200 = Color(199, 210, 254)
        val Indigo300 = Color(165, 180, 252)
        val Indigo400 = Color(129, 140, 248)
        val Indigo500 = Color(99, 102, 241)
        val Indigo600 = Color(79, 70, 229)
        val Indigo700 = Color(67, 56, 202)
        val Indigo800 = Color(55, 48, 163)
        val Indigo900 = Color(49, 46, 129)
        val Indigo950 = Color(30, 27, 75)

        val Violet50 = Color(245, 243, 255)
        val Violet100 = Color(237, 233, 254)
        val Violet200 = Color(221, 214, 254)
        val Violet300 = Color(196, 181, 253)
        val Violet400 = Color(167, 139, 250)
        val Violet500 = Color(139, 92, 246)
        val Violet600 = Color(124, 58, 237)
        val Violet700 = Color(109, 40, 217)
        val Violet800 = Color(91, 33, 182)
        val Violet900 = Color(76, 29, 149)
        val Violet950 = Color(46, 16, 101)

        val Purple50 = Color(250, 245, 255)
        val Purple100 = Color(243, 232, 255)
        val Purple200 = Color(233, 213, 255)
        val Purple300 = Color(216, 180, 254)
        val Purple400 = Color(192, 132, 252)
        val Purple500 = Color(168, 85, 247)
        val Purple600 = Color(147, 51, 234)
        val Purple700 = Color(126, 34, 206)
        val Purple800 = Color(107, 33, 168)
        val Purple900 = Color(88, 28, 135)
        val Purple950 = Color(59, 7, 100)

        val Pink50 = Color(253, 242, 248)
        val Pink100 = Color(252, 231, 243)
        val Pink200 = Color(251, 207, 232)
        val Pink300 = Color(249, 168, 212)
        val Pink400 = Color(244, 114, 182)
        val Pink500 = Color(236, 72, 153)
        val Pink600 = Color(219, 39, 119)
        val Pink700 = Color(190, 24, 93)
        val Pink800 = Color(157, 23, 77)
        val Pink900 = Color(131, 24, 67)
        val Pink950 = Color(80, 7, 36)

        val Rose50 = Color(255, 241, 242)
        val Rose100 = Color(255, 228, 230)
        val Rose200 = Color(254, 205, 211)
        val Rose300 = Color(253, 164, 175)
        val Rose400 = Color(251, 113, 133)
        val Rose500 = Color(244, 63, 94)
        val Rose600 = Color(225, 29, 72)
        val Rose700 = Color(190, 18, 60)
        val Rose800 = Color(159, 18, 57)
        val Rose900 = Color(136, 19, 55)
        val Rose950 = Color(76, 5, 25)
    }
}

// Java names mirror the Kotlin palette without duplicating color values.
object PdfColors {
    @JvmField val BLACK = Color.Black
    @JvmField val WHITE = Color.White

    @JvmField val BLUE = Color.Blue
    @JvmField val RED = Color.Red
    @JvmField val GRAY = Color.Gray

    @JvmField val SLATE_50 = Color.Slate50
    @JvmField val SLATE_100 = Color.Slate100
    @JvmField val SLATE_200 = Color.Slate200
    @JvmField val SLATE_300 = Color.Slate300
    @JvmField val SLATE_400 = Color.Slate400
    @JvmField val SLATE_500 = Color.Slate500
    @JvmField val SLATE_600 = Color.Slate600
    @JvmField val SLATE_700 = Color.Slate700
    @JvmField val SLATE_800 = Color.Slate800
    @JvmField val SLATE_900 = Color.Slate900
    @JvmField val SLATE_950 = Color.Slate950

    @JvmField val GRAY_50 = Color.Gray50
    @JvmField val GRAY_100 = Color.Gray100
    @JvmField val GRAY_200 = Color.Gray200
    @JvmField val GRAY_300 = Color.Gray300
    @JvmField val GRAY_400 = Color.Gray400
    @JvmField val GRAY_500 = Color.Gray500
    @JvmField val GRAY_600 = Color.Gray600
    @JvmField val GRAY_700 = Color.Gray700
    @JvmField val GRAY_800 = Color.Gray800
    @JvmField val GRAY_900 = Color.Gray900
    @JvmField val GRAY_950 = Color.Gray950

    @JvmField val RED_50 = Color.Red50
    @JvmField val RED_100 = Color.Red100
    @JvmField val RED_200 = Color.Red200
    @JvmField val RED_300 = Color.Red300
    @JvmField val RED_400 = Color.Red400
    @JvmField val RED_500 = Color.Red500
    @JvmField val RED_600 = Color.Red600
    @JvmField val RED_700 = Color.Red700
    @JvmField val RED_800 = Color.Red800
    @JvmField val RED_900 = Color.Red900
    @JvmField val RED_950 = Color.Red950

    @JvmField val ORANGE_50 = Color.Orange50
    @JvmField val ORANGE_100 = Color.Orange100
    @JvmField val ORANGE_200 = Color.Orange200
    @JvmField val ORANGE_300 = Color.Orange300
    @JvmField val ORANGE_400 = Color.Orange400
    @JvmField val ORANGE_500 = Color.Orange500
    @JvmField val ORANGE_600 = Color.Orange600
    @JvmField val ORANGE_700 = Color.Orange700
    @JvmField val ORANGE_800 = Color.Orange800
    @JvmField val ORANGE_900 = Color.Orange900
    @JvmField val ORANGE_950 = Color.Orange950

    @JvmField val AMBER_50 = Color.Amber50
    @JvmField val AMBER_100 = Color.Amber100
    @JvmField val AMBER_200 = Color.Amber200
    @JvmField val AMBER_300 = Color.Amber300
    @JvmField val AMBER_400 = Color.Amber400
    @JvmField val AMBER_500 = Color.Amber500
    @JvmField val AMBER_600 = Color.Amber600
    @JvmField val AMBER_700 = Color.Amber700
    @JvmField val AMBER_800 = Color.Amber800
    @JvmField val AMBER_900 = Color.Amber900
    @JvmField val AMBER_950 = Color.Amber950

    @JvmField val YELLOW_50 = Color.Yellow50
    @JvmField val YELLOW_100 = Color.Yellow100
    @JvmField val YELLOW_200 = Color.Yellow200
    @JvmField val YELLOW_300 = Color.Yellow300
    @JvmField val YELLOW_400 = Color.Yellow400
    @JvmField val YELLOW_500 = Color.Yellow500
    @JvmField val YELLOW_600 = Color.Yellow600
    @JvmField val YELLOW_700 = Color.Yellow700
    @JvmField val YELLOW_800 = Color.Yellow800
    @JvmField val YELLOW_900 = Color.Yellow900
    @JvmField val YELLOW_950 = Color.Yellow950

    @JvmField val GREEN_50 = Color.Green50
    @JvmField val GREEN_100 = Color.Green100
    @JvmField val GREEN_200 = Color.Green200
    @JvmField val GREEN_300 = Color.Green300
    @JvmField val GREEN_400 = Color.Green400
    @JvmField val GREEN_500 = Color.Green500
    @JvmField val GREEN_600 = Color.Green600
    @JvmField val GREEN_700 = Color.Green700
    @JvmField val GREEN_800 = Color.Green800
    @JvmField val GREEN_900 = Color.Green900
    @JvmField val GREEN_950 = Color.Green950

    @JvmField val TEAL_50 = Color.Teal50
    @JvmField val TEAL_100 = Color.Teal100
    @JvmField val TEAL_200 = Color.Teal200
    @JvmField val TEAL_300 = Color.Teal300
    @JvmField val TEAL_400 = Color.Teal400
    @JvmField val TEAL_500 = Color.Teal500
    @JvmField val TEAL_600 = Color.Teal600
    @JvmField val TEAL_700 = Color.Teal700
    @JvmField val TEAL_800 = Color.Teal800
    @JvmField val TEAL_900 = Color.Teal900
    @JvmField val TEAL_950 = Color.Teal950

    @JvmField val CYAN_50 = Color.Cyan50
    @JvmField val CYAN_100 = Color.Cyan100
    @JvmField val CYAN_200 = Color.Cyan200
    @JvmField val CYAN_300 = Color.Cyan300
    @JvmField val CYAN_400 = Color.Cyan400
    @JvmField val CYAN_500 = Color.Cyan500
    @JvmField val CYAN_600 = Color.Cyan600
    @JvmField val CYAN_700 = Color.Cyan700
    @JvmField val CYAN_800 = Color.Cyan800
    @JvmField val CYAN_900 = Color.Cyan900
    @JvmField val CYAN_950 = Color.Cyan950

    @JvmField val BLUE_50 = Color.Blue50
    @JvmField val BLUE_100 = Color.Blue100
    @JvmField val BLUE_200 = Color.Blue200
    @JvmField val BLUE_300 = Color.Blue300
    @JvmField val BLUE_400 = Color.Blue400
    @JvmField val BLUE_500 = Color.Blue500
    @JvmField val BLUE_600 = Color.Blue600
    @JvmField val BLUE_700 = Color.Blue700
    @JvmField val BLUE_800 = Color.Blue800
    @JvmField val BLUE_900 = Color.Blue900
    @JvmField val BLUE_950 = Color.Blue950

    @JvmField val INDIGO_50 = Color.Indigo50
    @JvmField val INDIGO_100 = Color.Indigo100
    @JvmField val INDIGO_200 = Color.Indigo200
    @JvmField val INDIGO_300 = Color.Indigo300
    @JvmField val INDIGO_400 = Color.Indigo400
    @JvmField val INDIGO_500 = Color.Indigo500
    @JvmField val INDIGO_600 = Color.Indigo600
    @JvmField val INDIGO_700 = Color.Indigo700
    @JvmField val INDIGO_800 = Color.Indigo800
    @JvmField val INDIGO_900 = Color.Indigo900
    @JvmField val INDIGO_950 = Color.Indigo950

    @JvmField val VIOLET_50 = Color.Violet50
    @JvmField val VIOLET_100 = Color.Violet100
    @JvmField val VIOLET_200 = Color.Violet200
    @JvmField val VIOLET_300 = Color.Violet300
    @JvmField val VIOLET_400 = Color.Violet400
    @JvmField val VIOLET_500 = Color.Violet500
    @JvmField val VIOLET_600 = Color.Violet600
    @JvmField val VIOLET_700 = Color.Violet700
    @JvmField val VIOLET_800 = Color.Violet800
    @JvmField val VIOLET_900 = Color.Violet900
    @JvmField val VIOLET_950 = Color.Violet950

    @JvmField val PURPLE_50 = Color.Purple50
    @JvmField val PURPLE_100 = Color.Purple100
    @JvmField val PURPLE_200 = Color.Purple200
    @JvmField val PURPLE_300 = Color.Purple300
    @JvmField val PURPLE_400 = Color.Purple400
    @JvmField val PURPLE_500 = Color.Purple500
    @JvmField val PURPLE_600 = Color.Purple600
    @JvmField val PURPLE_700 = Color.Purple700
    @JvmField val PURPLE_800 = Color.Purple800
    @JvmField val PURPLE_900 = Color.Purple900
    @JvmField val PURPLE_950 = Color.Purple950

    @JvmField val PINK_50 = Color.Pink50
    @JvmField val PINK_100 = Color.Pink100
    @JvmField val PINK_200 = Color.Pink200
    @JvmField val PINK_300 = Color.Pink300
    @JvmField val PINK_400 = Color.Pink400
    @JvmField val PINK_500 = Color.Pink500
    @JvmField val PINK_600 = Color.Pink600
    @JvmField val PINK_700 = Color.Pink700
    @JvmField val PINK_800 = Color.Pink800
    @JvmField val PINK_900 = Color.Pink900
    @JvmField val PINK_950 = Color.Pink950

    @JvmField val ROSE_50 = Color.Rose50
    @JvmField val ROSE_100 = Color.Rose100
    @JvmField val ROSE_200 = Color.Rose200
    @JvmField val ROSE_300 = Color.Rose300
    @JvmField val ROSE_400 = Color.Rose400
    @JvmField val ROSE_500 = Color.Rose500
    @JvmField val ROSE_600 = Color.Rose600
    @JvmField val ROSE_700 = Color.Rose700
    @JvmField val ROSE_800 = Color.Rose800
    @JvmField val ROSE_900 = Color.Rose900
    @JvmField val ROSE_950 = Color.Rose950
}
