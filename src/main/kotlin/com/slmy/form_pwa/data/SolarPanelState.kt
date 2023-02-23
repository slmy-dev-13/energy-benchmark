package com.slmy.form_pwa.data

import com.slmy.form_pwa.solar.Orientation

data class SolarPanelState(
    val energyCost: EnergyCost = EnergyCost(),
    val usageCost: UsageCost = UsageCost(),
    val surface: Double = 0.0,
    val orientation: Orientation = Orientation.S
)