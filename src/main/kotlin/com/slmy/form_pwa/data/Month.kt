package com.slmy.form_pwa.data

enum class Month(val number: Int, val label: String) {
    Jan(1, "Janvier"),
    Feb(2, "Février"),
    Mar(3, "Mars"),
    Apr(4, "Avril"),
    May(5, "Mai"),
    Jun(6, "Juin"),
    Jul(7, "Juillet"),
    Aug(8, "Août"),
    Sep(9, "Septembre"),
    Oct(10, "Octobre"),
    Nov(11, "Novembre"),
    Dec(12, "Décembre");

    companion object {
        fun labelOf(number: Int): String {
            return values().firstOrNull {
                it.number == number
            }?.label ?: "?"
        }

        fun withNumber(number: Int): Month {
            return values().first { it.number == number }
        }
    }
}