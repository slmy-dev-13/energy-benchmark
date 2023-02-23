package com.slmy.form_pwa

import com.slmy.form_pwa.data.EnergyCost
import com.slmy.form_pwa.data.SolarPanelState
import com.slmy.form_pwa.solar.Orientation
import io.kvision.state.ObservableValue

class SolarController {

    val stateObservable = ObservableValue(SolarPanelState())

    fun updateEnergyCost(electricity: Double) {
        stateObservable.update { it.copy(energyCost = EnergyCost(electricity)) }
    }

    fun updateSurfaceAndOrientation(surface: Double, orientation: Orientation) {
        stateObservable.update {
            it.copy(surface = surface, orientation = orientation)
        }
    }
}