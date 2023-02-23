package com.slmy.form_pwa.data

data class SolarPanelState(
    val energyCost: EnergyCost = EnergyCost(),
    val usageCost: UsageCost = UsageCost(),
)