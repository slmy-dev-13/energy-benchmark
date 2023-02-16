package com.slmy.form_pwa.expenses

import com.slmy.form_pwa.data.ConsumptionCostsForm
import com.slmy.form_pwa.data.SystemType
import io.kvision.core.Container
import io.kvision.html.h2
import io.kvision.panel.vPanel
import io.kvision.state.ObservableValue

fun Container.energyExpenses(
    formObservable: ObservableValue<ConsumptionCostsForm>,
    systemTypeObservable: ObservableValue<SystemType>
) {
    vPanel(spacing = 16) {
        h2("Les dépenses énergétiques")

        ratios(systemTypeObservable)

        costsAndSavings(formObservable)
    }
}
