package com.slmy.form_pwa.data

import com.slmy.form_pwa.expenses.Energy

data class HeatPumpState(
    val systemType: SystemType = SystemType.Simple,
    val energy: Energy = Energy.Fioul,
    val energyCost: EnergyCost = EnergyCost(),
    val usageCost: UsageCost = UsageCost(),
    val isolationIndex: Int = 120,
    val neededPower: Double = 0.0,
    val heatPumpCost: Double = 0.0
)