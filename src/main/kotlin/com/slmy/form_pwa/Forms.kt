package com.slmy.form_pwa

import kotlinx.serialization.Serializable

@Serializable
data class ConsumptionCostsForm(
    val fuelCost: Double = 3000.0,
    val electricityCost: Double = 1000.0,
    val withHeatPump: Boolean = false,
    val withBalloonTD: Boolean = false,
    val withPVSystem: Boolean = false,
)

@Serializable
data class IsolationDataForm(
    val houseBuiltAfter2012: Boolean = false,
    val doubleWindow: Boolean = false,
    val fillsIsolation: Boolean = false,
    val sanitaryVoid: Boolean = false,
)

fun IsolationDataForm.computeIsolationIndex(): Int {
    if (houseBuiltAfter2012) {
        return 60
    }

    var isolationIndex = 120

    listOf(doubleWindow, fillsIsolation, sanitaryVoid).forEach { checked ->
        if (checked) {
            isolationIndex -= 20
        }
    }

    return isolationIndex
}

@Serializable
data class NeededPowerForm(
    val surface: Double = 150.0,
    val heightUnderCeiling: Double = 2.6,
)

fun NeededPowerForm.computeNeededPower(isolationIndex: Int): Double {
    val rawResult = (((surface * isolationIndex.toDouble() * heightUnderCeiling) / 2.5) / 1000.0)
    return ((rawResult * 100.0).toInt()).toDouble() / 100.0
}

@Serializable
data class HeatPumpCostForm(
    val heatingDays: Int = 180,
    val compressorHours: Int = 8,
    val heatPumpCOP: Double = 4.6,
)

fun HeatPumpCostForm.computeCost(neededPower: Double): Double {
    return (neededPower * heatingDays * compressorHours * 0.22) / heatPumpCOP
}