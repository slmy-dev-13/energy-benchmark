package com.slmy.form_pwa.data

enum class SystemType(
    val label: String,
    val heatRatio: Double,
    val waterRatio: Double,
    val diverseRatio: Double,
) {
    Simple("Simple", 1.0, 0.4, 0.6),
    Mixed("Combiné", 0.7, 0.3, 1.0);
}