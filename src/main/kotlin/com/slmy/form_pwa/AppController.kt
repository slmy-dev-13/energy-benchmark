package com.slmy.form_pwa

import com.slmy.form_pwa.data.EnergyCost
import com.slmy.form_pwa.data.HeatPumpState
import com.slmy.form_pwa.data.SystemType
import com.slmy.form_pwa.data.UsageCost
import com.slmy.form_pwa.expenses.Energy
import io.kvision.state.ObservableValue
import io.kvision.state.sub

class AppController {

    val stateObservable = ObservableValue(HeatPumpState())

    val isolationIndexStore = stateObservable.sub { it.isolationIndex }
    val neededPowerStore = stateObservable.sub { it.neededPower }

    private fun processUsageCost() {
        val systemType = stateObservable.value.systemType
        val energyCost = stateObservable.value.energyCost

        val usageCost = if (systemType == SystemType.Simple) {
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

        stateObservable.update { it.copy(usageCost = usageCost) }
    }

    fun updateSystemType(systemType: SystemType) {
        stateObservable.update { it.copy(systemType = systemType) }
        processUsageCost()
    }

    fun updateEnergyCost(electricity: Double, other: Double) {
        stateObservable.update { it.copy(energyCost = EnergyCost(electricity, other)) }
        processUsageCost()
    }

    fun updateEnergy(energy: Energy) {
        stateObservable.update { it.copy(energy = energy) }
    }

    fun updateIsolationIndex(isolationIndex: Int) {
        stateObservable.update { it.copy(isolationIndex = isolationIndex) }
    }

    fun updateNeededPower(neededPower: Double) {
        stateObservable.update { it.copy(neededPower = neededPower) }
    }

    fun updateHeatPumpCost(heatPumpCost: Double) {
        stateObservable.update { it.copy(heatPumpCost = heatPumpCost) }
    }
}