package com.slmy.form_pwa.data

import kotlin.math.ceil
import kotlin.math.floor

private const val solarPanelPower = 0.375
private const val solarPanelSurface = 1.16 * 1.69

data class SolarPanelState(
    val energyCost: EnergyCost = EnergyCost(),
    val usageCost: UsageCost = UsageCost(),
    val surface: Double = 0.0,
    val orientation: Orientation = Orientation.S,
    val sunHours: Int = 0,
    val currentConsumption: Int = 0
) {
    val maxPanelCapacity: Int = floor(surface / solarPanelSurface).toInt()
    val optimalRequiredPower: Int = ceil(currentConsumption / sunHours.toDouble()).toInt()
    val maxPossiblePower: Int = ceil(maxPanelCapacity.toDouble() * solarPanelPower * sunHours.toDouble()).toInt()

}