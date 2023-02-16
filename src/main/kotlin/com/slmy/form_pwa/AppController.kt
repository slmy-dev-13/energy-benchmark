package com.slmy.form_pwa

import com.slmy.form_pwa.data.SystemType
import io.kvision.state.ObservableValue

data class EnergyCost(
    val electricity: Double = 0.0,
    val other: Double = 0.0,
)

data class UsageCost(
    val heat: Double = 0.0,
    val water: Double = 0.0,
    val diverse: Double = 0.0,
)

class AppController {
    private var systemType: SystemType = SystemType.Mixed
        set(value) {
            field = value
            systemTypeObservable.update { value }
        }

    private var energyCost = EnergyCost()
        set(value) {
            field = value
            energyCostObservable.update { value }
        }

    private var usageCost = UsageCost()
        set(value) {
            field = value
            usageCostObservable.update { value }
        }

    val systemTypeObservable = ObservableValue(systemType)
    val energyCostObservable = ObservableValue(energyCost)
    val usageCostObservable = ObservableValue(usageCost)

    init {
        processUsageCost()
    }

    private fun processUsageCost() {
        usageCost = if (systemType == SystemType.Simple) {
            UsageCost(
                heat = systemType.heatRatio * energyCost.other,
                water = systemType.waterRatio * energyCost.electricity,
                diverse = systemType.diverseRatio * energyCost.electricity
            )
        } else {
            UsageCost(
                heat = systemType.heatRatio * energyCost.other,
                water = systemType.waterRatio * energyCost.other,
                diverse = systemType.diverseRatio * energyCost.electricity
            )
        }
    }

    fun updateSystemType(systemType: SystemType) {
        this.systemType = systemType
        processUsageCost()
    }

    fun updateEnergyCost(electricity: Double, other: Double) {
        energyCost = EnergyCost(electricity, other)
        processUsageCost()
    }

}