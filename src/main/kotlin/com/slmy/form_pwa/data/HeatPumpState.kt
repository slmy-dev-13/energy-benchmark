package com.slmy.form_pwa.data

data class HeatPumpState(
    val systemType: SystemType = SystemType.Simple,
    val energyCost: EnergyCost = EnergyCost(),
    val usageCost: UsageCost = UsageCost(),
    val isolationIndex: Int = 120,
    val neededPower: Double = 0.0,
    val heatPumpCost: Double = 0.0
)