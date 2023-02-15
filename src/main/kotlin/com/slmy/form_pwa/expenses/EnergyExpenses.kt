package com.slmy.form_pwa.expenses

import com.slmy.form_pwa.ConsumptionCostsForm
import io.kvision.core.Container
import io.kvision.html.h2
import io.kvision.panel.vPanel
import io.kvision.state.ObservableValue

fun Container.energyExpenses(formObservable: ObservableValue<ConsumptionCostsForm>) {
    vPanel(spacing = 16) {
        h2("Les dépenses énergétiques")

        ratioCard()

        costsAndSavings(formObservable)
    }
}
