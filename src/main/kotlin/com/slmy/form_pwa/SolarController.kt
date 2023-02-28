package com.slmy.form_pwa

import com.slmy.form_pwa.data.Orientation
import com.slmy.form_pwa.data.SolarPanelState
import io.kvision.state.ObservableValue

class SolarController {

    val stateObservable = ObservableValue(SolarPanelState())

    fun updateEnergyCost(electricity: Double) {
        stateObservable.update { it.copy(electricityCost = electricity) }
    }

    fun updateSurface(surface: Double) {
        stateObservable.update {
            it.copy(surface = surface)
        }
    }

    fun updateSunExposition(sunHours: Int, orientation: Orientation) {
        stateObservable.update { it.copy(sunHours = sunHours, orientation = orientation) }
    }

    fun updateCurrentConsumption(consumption: Int) {
        stateObservable.update { it.copy(currentConsumption = consumption) }
    }

    fun reset() {
        stateObservable.update { SolarPanelState() }
    }
}