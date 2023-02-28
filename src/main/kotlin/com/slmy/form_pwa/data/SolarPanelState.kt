package com.slmy.form_pwa.data

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

private const val solarPanelKWc = 0.375 // KWc
private const val solarPanelSurface = 1.16 * 1.69 // m2

data class SolarPanelState(
    val electricityCost: Double = 2500.0,
    val usageCost: UsageCost = UsageCost(),
    val surface: Double = 0.0,
    val orientation: Orientation = Orientation.S,
    val sunHours: Int = 1,
    val currentConsumption: Int = 3700
) {
    val maxPanelCapacity: Int = floor(surface / solarPanelSurface).toInt()
    val maxPossiblePower: Double = (maxPanelCapacity.toDouble() * solarPanelKWc)
    val optimalRequiredPower: Double = (currentConsumption.toDouble() / sunHours.toDouble())
    val optimalPanelCount: Int = ceil(optimalRequiredPower / solarPanelKWc).toInt()

    val savingsFactor: Double = (min(maxPanelCapacity, optimalPanelCount).toDouble() * solarPanelKWc * sunHours.toDouble() * .7) / currentConsumption.toDouble()
    val costSavings: Double = electricityCost * savingsFactor
}