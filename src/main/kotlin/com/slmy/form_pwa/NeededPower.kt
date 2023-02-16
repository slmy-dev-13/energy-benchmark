package com.slmy.form_pwa

import com.slmy.form_pwa.data.NeededPowerForm
import com.slmy.form_pwa.ui.card
import io.kvision.core.Container
import io.kvision.form.formPanel
import io.kvision.form.spinner.simpleSpinner
import io.kvision.html.br
import io.kvision.html.div
import io.kvision.html.h3
import io.kvision.html.span
import io.kvision.state.ObservableState
import io.kvision.state.ObservableValue
import io.kvision.state.bind

fun Container.neededPower(formObservable: ObservableValue<NeededPowerForm>, needPowerObservable: ObservableState<Double>) {
    card(
        headerContent = { h3("Puissance préconisée du matériel") },
        bodyContent = {
            formPanel(className = "columns column") {
                val subscriber: (Number?) -> Unit = {
                    formObservable.value = getData()
                }

                simpleSpinner(null, label = "Surface en m²") {
                    addCssClass("col-6")
                    addCssClass("col-xs-12")

                    bind(NeededPowerForm::surface)
                    subscribe(subscriber)
                }

                simpleSpinner(null, label = "Hauteur sous plafond en m") {
                    addCssClass("col-6")
                    addCssClass("col-xs-12")

                    bind(NeededPowerForm::heightUnderCeiling)
                    subscribe(subscriber)
                }

                br()

                setData(formObservable.value)
            }
        },
        extraContent = {
            div(className = "card-extra bg-gray p-2 text-right mt-2").bind(needPowerObservable) { neededPower ->
                span("Puissance préconisée")
                br()
                span("$neededPower KWh", className = "h1")
            }
        }
    )
}
