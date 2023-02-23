package com.slmy.form_pwa

import com.slmy.form_pwa.data.EnergyCost
import com.slmy.form_pwa.data.SolarPanelState
import io.kvision.state.ObservableValue

class SolarController {

    val stateObservable = ObservableValue(SolarPanelState())

    fun updateEnergyCost(electricity: Double) {
        stateObservable.update { it.copy(energyCost = EnergyCost(electricity)) }
    }
}