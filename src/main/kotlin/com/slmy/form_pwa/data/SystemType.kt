package com.slmy.form_pwa.data

enum class SystemType(
    val label: String,
    val waterRatio: Double,
    val heatRatio: Double
) {
    Simple("Simple", .4, .6),
    Mixed("Combiné", .3, .7);

    companion object {
        fun fromName(name: String?): SystemType {
            if (name == Simple.name) {
                return Simple
            }

            return Mixed
        }
    }
}